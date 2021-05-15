package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.progetto.beans.Utente;
import it.polimi.tiw.progetto.dao.UtenteDAO;

@WebServlet("/ControllaLogin")
public class ControllaLogin extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public ControllaLogin() {
		super();
	}
	
	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)  //TODO: serve o lo togliamo?
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		String psw = request.getParameter("psw");
		UtenteDAO usr = new UtenteDAO(connection);
		Utente u = null;
		try {
			u = usr.controllaCredenziali(email, psw);
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
		}
		String path = getServletContext().getContextPath();
		if (u == null) {
			path = getServletContext().getContextPath() + "/login.html";
		} else {
			request.getSession().setAttribute("user", u);
			path = getServletContext().getContextPath() + "/GoToHome";
		}
		response.sendRedirect(path);
	}
}
