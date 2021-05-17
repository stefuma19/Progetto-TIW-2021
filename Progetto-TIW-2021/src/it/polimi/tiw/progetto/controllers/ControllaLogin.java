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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.progetto.beans.Utente;
import it.polimi.tiw.progetto.dao.UtenteDAO;
import it.polimi.tiw.progetto.utils.GestoreConnessione;

@WebServlet("/ControllaLogin")
public class ControllaLogin extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	
	public ControllaLogin() {
		super();
	}
	
	public void init() throws ServletException {
		connection = GestoreConnessione.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)  //TODO: serve o lo togliamo?
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = null;
		String psw = null;
		try {
			email = request.getParameter("email");
			psw = request.getParameter("psw");
			if (email == null || psw == null || email.isEmpty() || psw.isEmpty()) {
				throw new Exception("Credenziali mancanti o inesistenti");
			}
		} catch (Exception e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Credenziali non presenti");
			return;
		}
		
		UtenteDAO usrDao = new UtenteDAO(connection);
		Utente usr = null;
		try {
			usr = usrDao.controllaCredenziali(email, psw);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile controllare le credenziali");
			return;
		}
		
		String path;
		if (usr == null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Email o password errata");
			path = "/login.html";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			request.getSession().setAttribute("utente", usr);
			path = getServletContext().getContextPath() + "/GoToHome";
			response.sendRedirect(path);
		}
	}
	
	public void destroy() {
		try {
			GestoreConnessione.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
