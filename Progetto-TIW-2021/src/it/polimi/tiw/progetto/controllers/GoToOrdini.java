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

import it.polimi.tiw.progetto.utils.GestoreConnessione;
import it.polimi.tiw.progetto.beans.*;
import it.polimi.tiw.progetto.dao.OrdineDAO;

@WebServlet("/GoToOrdini")
public class GoToOrdini extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public GoToOrdini() {
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
		
		List<Ordine> ordiniDaMostrare = new ArrayList<Ordine>();
		OrdineDAO ordineDAO = new OrdineDAO(connection);
		
		if(request.getParameter("IdFor") != null) {  //se devo inserire un nuovo prodotto
			
			//aggiungo ordine e invio redirect
			
			response.sendRedirect(getServletContext().getContextPath() + "/GoToOrdini");
		}
		
			//mostro tutti gli ordini presi dal db

		HttpSession s = request.getSession(); 
		try {
			ordiniDaMostrare = ordineDAO.prendiOrdiniByIdUtente(((Utente)s.getAttribute("utente")).getId());
		}catch(SQLException e) {
			e.printStackTrace();
		}
		

		String path = "/WEB-INF/ordini.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("ordini", ordiniDaMostrare);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
