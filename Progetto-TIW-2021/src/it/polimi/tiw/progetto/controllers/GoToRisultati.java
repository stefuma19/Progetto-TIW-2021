package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
import it.polimi.tiw.progetto.dao.ProdottoDAO;
import it.polimi.tiw.progetto.utils.GestoreConnessione;

@WebServlet("/GoToRisultati")
public class GoToRisultati extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	private List<Prodotto> prodotti = new ArrayList<Prodotto>();
	
	public GoToRisultati() {
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

		List<Prodotto> offerte = new ArrayList<Prodotto>();
		
		if(request.getParameter("keyword") != null) {
			try {
				prodotti = prodottoDAO.prendiProdottiCercati(request.getParameter("keyword"));
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare prodotti da keyword");
				return;
			}
		}
		String path = "/WEB-INF/risultati.html";
		response.setContentType("text");
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if(request.getParameter("idProdotto") != null) {
			int idProdotto = Integer.parseInt(request.getParameter("idProdotto"));
			Queue<Integer> listaVisualizzati = new LinkedList<>();
			HttpSession session = request.getSession();
			if (session.getAttribute("listaVisualizzati") == null) {
				listaVisualizzati.add(idProdotto);
				request.getSession().setAttribute("listaVisualizzati", listaVisualizzati);
			}
			else {
				listaVisualizzati = (Queue<Integer>) session.getAttribute("listaVisualizzati");
				if(!listaVisualizzati.contains(idProdotto)) {
					if(listaVisualizzati.size()==5)
						listaVisualizzati.remove();
					listaVisualizzati.add(idProdotto);
				}
			}
			
			ctx.setVariable("idDaMostrare", request.getParameter("idProdotto")); 
			
			try {
				offerte = prodottoDAO.prendiOfferteByIdProdotto(Integer.parseInt(request.getParameter("idProdotto")));
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare prodotti da id");
				return;
			}
		}
		
		ctx.setVariable("offerte", offerte);
		ctx.setVariable("prodotti", prodotti);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
