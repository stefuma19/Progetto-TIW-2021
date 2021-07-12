package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

import it.polimi.tiw.progetto.beans.Prodotto;
import it.polimi.tiw.progetto.beans.Utente;
import it.polimi.tiw.progetto.dao.FornitoreDAO;
import it.polimi.tiw.progetto.dao.ProdottoDAO;
import it.polimi.tiw.progetto.utils.CalcoloCosti;
import it.polimi.tiw.progetto.utils.CookieParser;
import it.polimi.tiw.progetto.utils.GestoreConnessione;

@WebServlet("/AggiungiCarrello")
public class AggiungiCarrello extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public AggiungiCarrello() {
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
		
		ProdottoDAO prodottoDAO = new ProdottoDAO(connection);
		FornitoreDAO fornitoreDAO = new FornitoreDAO(connection);
		
		if(request.getParameter("IdFor") != null && request.getParameter("IdProd") != null) {
			
			try {
				Integer.parseInt(request.getParameter("IdProd"));
				Integer.parseInt(request.getParameter("IdFor"));
				Integer.parseInt(request.getParameter("quantita"));
			} catch (NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Richiesta mal formata");
				return;
		    }

			
			try {
				if(!prodottoDAO.esisteProdotto(Integer.parseInt(request.getParameter("IdProd"))) || 
						!fornitoreDAO.esisteFornitore(Integer.parseInt(request.getParameter("IdFor")))) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "L'ID a cui si sta tentando di accedere non esiste");
					return;
				}
			}catch(SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile aggiungere a carrello per Id fornitore");
				return;
			}
			
			if(request.getParameter("quantita")==null || request.getParameter("quantita")=="") {
						response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Campo quantità assente");
						return;
			}
			
			boolean primo=true;  //se dobbiamo creare un cookie per il fornitore
			Cookie[] cookies = request.getCookies();
			HttpSession s = request.getSession(); 
			if(Integer.parseInt(request.getParameter("quantita")) < 1){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quantità selezionata minore o uguale a 0");
				return;
			}else if (Integer.parseInt(request.getParameter("quantita")) > 999) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "La quantità di prodotti nel carrello non può superare le 999 unità");
				return;
			}else if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					Cookie c = cookies[i];
					String nome = c.getName();
					if(nome.equals(((Utente)s.getAttribute("utente")).getId()+"-"+request.getParameter("IdFor"))) {
						primo = false;
						String valore = c.getValue(); 
						List<Prodotto> prodottiPresenti = CookieParser.parseCookie(c); 
						if(CalcoloCosti.calcolaNumeroProdotti(prodottiPresenti) + Integer.parseInt(request.getParameter("quantita")) > 999) {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST, "La quantità di prodotti nel carrello non può superare le 999 unità");
							return;
						}else { //controllo se ho già aggiunto quel prodotto e ne aumento solo la quantità
							boolean presente = false;
							for(Prodotto p: prodottiPresenti) {
								if(p.getID() == Integer.parseInt(request.getParameter("IdProd"))) {
									p.setQuantita(p.getQuantita() + Integer.parseInt(request.getParameter("quantita")));
									presente = true;
									break;
								}
							} 
							if(presente) {
								Cookie coo = CookieParser.creaCookieByProdotti(prodottiPresenti, request);
								coo.setMaxAge(3600);
								response.addCookie(coo);
							}else {
								valore += "_" + request.getParameter("IdProd") + "-" + request.getParameter("quantita");
								Cookie coo = new Cookie(nome, valore);
								coo.setMaxAge(3600);
								response.addCookie(coo);
							}
							break;
						}
					}
				}
			}
			
			if(primo) {
				String idFor = request.getParameter("IdFor");
				String nome = ((Utente)s.getAttribute("utente")).getId() + "-" + idFor;
				String valore = request.getParameter("IdProd") + "-" + request.getParameter("quantita");
				Cookie coo = new Cookie(nome, valore);
				coo.setMaxAge(3600);
				response.addCookie(coo);
			}
	
			response.sendRedirect(getServletContext().getContextPath() + "/VisualizzaCarrello");
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
			return;
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
