package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.progetto.utils.CalcoloCosti;
import it.polimi.tiw.progetto.utils.CookieParser;
import it.polimi.tiw.progetto.utils.GestoreConnessione;
import it.polimi.tiw.progetto.beans.*;
import it.polimi.tiw.progetto.dao.FornitoreDAO;
import it.polimi.tiw.progetto.dao.IndirizzoDAO;
import it.polimi.tiw.progetto.dao.OrdineDAO;
import it.polimi.tiw.progetto.dao.ProdottoDAO;

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
		ProdottoDAO prodottoDAO= new ProdottoDAO(connection);
		FornitoreDAO fornitoreDAO= new FornitoreDAO(connection);
		IndirizzoDAO indirizzoDAO= new IndirizzoDAO(connection);
		HttpSession s = request.getSession(); 
		
		if(request.getParameter("idForn") != null) {  //se devo inserire un nuovo ordine
			
			
			int idFor = Integer.parseInt(request.getParameter("idForn"));
			int idUtente = (((Utente)s.getAttribute("utente")).getId());
			List<Prodotto> mieiProdotti = new ArrayList<Prodotto>();
			float totale = -1;
			int idInd = -1;
			
			List<Prodotto> prodotti = CookieParser.prendiProdottiByIdFornitoreUtente(idUtente,idFor,request.getCookies()); //prendo info da cookie
			Cookie c = new Cookie(String.valueOf(idUtente) + "-" + String.valueOf(idFor),"");
			c.setMaxAge(0);
			response.addCookie(c);
			for(Prodotto p : prodotti) {
				try {
					Prodotto daAggiungere = prodottoDAO.prendiProdottoByIdProdottoFornitore(p.getID(),p.getFornitore().getID());
					daAggiungere.setQuantita(p.getQuantita());
					mieiProdotti.add(daAggiungere);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare prodotti da id prodotto e id fornitore");
					return;
				}
			}
			try {
				totale = CalcoloCosti.calcolaTotale(mieiProdotti, fornitoreDAO.prendiFornitoreById(idFor));
			}catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare fornitore da ID");
				return;
			}
			
			try {
				idInd = indirizzoDAO.prendiIdIndirizzoByParam(request.getParameter("citta"), request.getParameter("via"), request.getParameter("cap"), Integer.parseInt(request.getParameter("numero")));
			}catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare valore indirizzo");
				return;
			}
			try {
				ordineDAO.aggiungiOrdine(totale, idInd, idUtente, idFor, mieiProdotti);
			}catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile aggiungere ordine");
				return;
			}
			//aggiungo ordine e invio redirect
			
			response.sendRedirect(getServletContext().getContextPath() + "/GoToOrdini");
		}
		
			//mostro tutti gli ordini presi dal db

		try {
			ordiniDaMostrare = ordineDAO.prendiOrdiniByIdUtente(((Utente)s.getAttribute("utente")).getId());
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile prendere ordine by id utente");
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
