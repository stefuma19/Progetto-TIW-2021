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
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.progetto.beans.Ordine;
import it.polimi.tiw.progetto.beans.Prodotto;
import it.polimi.tiw.progetto.beans.Utente;
import it.polimi.tiw.progetto.dao.FornitoreDAO;
import it.polimi.tiw.progetto.dao.IndirizzoDAO;
import it.polimi.tiw.progetto.dao.OrdineDAO;
import it.polimi.tiw.progetto.dao.ProdottoDAO;
import it.polimi.tiw.progetto.utils.CalcoloCosti;
import it.polimi.tiw.progetto.utils.CookieParser;
import it.polimi.tiw.progetto.utils.GestoreConnessione;
import it.polimi.tiw.progetto.utils.IdException;

@WebServlet("/AggiungiOrdine")
public class AggiungiOrdine extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public AggiungiOrdine() {
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
		
		OrdineDAO ordineDAO = new OrdineDAO(connection);
		ProdottoDAO prodottoDAO= new ProdottoDAO(connection);
		FornitoreDAO fornitoreDAO= new FornitoreDAO(connection);
		IndirizzoDAO indirizzoDAO= new IndirizzoDAO(connection);
		HttpSession s = request.getSession(); 
		
		if(request.getParameter("idForn") != null) {  //se devo inserire un nuovo ordine
			try {
				if(!fornitoreDAO.esisteFornitore(Integer.parseInt(request.getParameter("idForn")))) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "L'ID a cui si sta tentando di accedere non esiste");
					return;
				}
				if(request.getParameter("citta")==null || request.getParameter("citta")=="" || 
						request.getParameter("via")==null || request.getParameter("via")=="" ||
						request.getParameter("cap")==null || request.getParameter("cap")=="" ||
						request.getParameter("numero")==null || request.getParameter("numero")=="") {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Campi indirizzo assenti");
							return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			int idFor = Integer.parseInt(request.getParameter("idForn"));
			int idUtente = (((Utente)s.getAttribute("utente")).getId());
			List<Prodotto> prodottiUtente = new ArrayList<Prodotto>();
			float totale = -1;
			int idInd = -1;
			
			List<Prodotto> prodotti = CookieParser.prendiProdottiByIdFornitoreUtente(idUtente,idFor,request.getCookies()); //prendo info da cookie
			Cookie c = new Cookie(String.valueOf(idUtente) + "-" + String.valueOf(idFor),"");
			c.setMaxAge(0);
			response.addCookie(c);
			for(Prodotto p : prodotti) {  //prendo informazioni prodotto da cookie
				try {
					Prodotto daAggiungere = prodottoDAO.prendiProdottoByIdProdottoFornitore(p.getID(),p.getFornitore().getID());
					daAggiungere.setQuantita(p.getQuantita());
					prodottiUtente.add(daAggiungere);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare prodotti da id prodotto e id fornitore");
					return;
				}catch (IdException e) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
					return;
				}
			}
			
			try {  //calcolo costi dell'ordine
				totale = CalcoloCosti.calcolaTotale(prodottiUtente, fornitoreDAO.prendiFornitoreById(idFor));
			}catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare fornitore da ID");
				return;
			}catch (IdException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
				return;
			}
			
			try {  //gestione indirizzo dell'ordine
				idInd = indirizzoDAO.prendiIdIndirizzoByParam(request.getParameter("citta"), request.getParameter("via"), request.getParameter("cap"), Integer.parseInt(request.getParameter("numero")));
			}catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare valore indirizzo");
				return;
			}
			
			try {  //aggiunta ordine nel db
				ordineDAO.aggiungiOrdine(totale, idInd, idUtente, idFor, prodottiUtente);
			}catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile aggiungere ordine");
				return;
			}
			
			response.sendRedirect(getServletContext().getContextPath() + "/VisualizzaOrdini");
		}
		
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
