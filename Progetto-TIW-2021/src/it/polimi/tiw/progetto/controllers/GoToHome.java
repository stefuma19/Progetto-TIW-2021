package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.progetto.beans.Prodotto;
import it.polimi.tiw.progetto.beans.Utente;
import it.polimi.tiw.progetto.dao.ProdottoDAO;
import it.polimi.tiw.progetto.utils.GestoreConnessione;

@WebServlet("/GoToHome")
public class GoToHome extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToHome() {
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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//HttpSession session = request.getSession();
		//Utente usr = (Utente) session.getAttribute("utente");
		ProdottoDAO prodottoDAO = new ProdottoDAO(connection);
		List<Prodotto> prodotti = new ArrayList<Prodotto>();
		
		try {
			prodotti = prodottoDAO.prendi5Prodotti();
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare prodotti");
			return;
		}
		
		String path = "/WEB-INF/home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("prodotti", prodotti);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			GestoreConnessione.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
