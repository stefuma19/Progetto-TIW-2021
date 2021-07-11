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

import it.polimi.tiw.progetto.beans.Carrello;
import it.polimi.tiw.progetto.beans.Prodotto;
import it.polimi.tiw.progetto.beans.Utente;
import it.polimi.tiw.progetto.dao.FornitoreDAO;
import it.polimi.tiw.progetto.dao.ProdottoDAO;
import it.polimi.tiw.progetto.utils.CalcoloCosti;
import it.polimi.tiw.progetto.utils.CookieParser;
import it.polimi.tiw.progetto.utils.GestoreConnessione;
import it.polimi.tiw.progetto.utils.IdException;

@WebServlet("/VisualizzaCarrello")
public class VisualizzaCarrello extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public VisualizzaCarrello() {
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
		
		List<Carrello> carrelliDaMostrare = new ArrayList<Carrello>();
		ProdottoDAO prodottoDAO = new ProdottoDAO(connection);
		FornitoreDAO fornitoreDAO = new FornitoreDAO(connection);
		
		//se devo visualizzare il carrello
		
		HttpSession s = request.getSession(); 
		Cookie[] cookies = request.getCookies();
		List<Prodotto> prodottiCarrello = new ArrayList<>();
		
		if (cookies != null) {  //se ho dei cookie 
			for (int i = 0; i < cookies.length; i++) { 
				List<Prodotto> listaForn = new ArrayList<Prodotto>();
				if(!cookies[i].getName().equals("JSESSIONID")) {
					if(cookies[i].getName().split("-")[0].equals(String.valueOf((((Utente)s.getAttribute("utente")).getId()))))
					{
						Carrello carrello = new Carrello();
						try {
							carrello.setFornitore(fornitoreDAO.prendiFornitoreById(Integer.parseInt(cookies[i].getName().split("-")[1])));
						} catch (SQLException e) {
							response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare fornitore da cookie");
							return;
						}catch (IdException e) {
							response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
							return;
						}
						prodottiCarrello = CookieParser.parseCookie(cookies[i]);
						for(Prodotto p : prodottiCarrello) {  //prendo info prodotto e le aggiungo al carrello creato in precedenza
							try {
								Prodotto daAggiungere = prodottoDAO.prendiProdottoByIdProdottoFornitore(p.getID(),p.getFornitore().getID());
								daAggiungere.setQuantita(p.getQuantita());
								listaForn.add(daAggiungere);
							} catch (SQLException e) {
								response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare prodotti da cookie info");
								return;
							}catch (IdException e) {
								response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
								return;
							}
						}
						carrello.setProdotti(listaForn);
						carrelliDaMostrare.add(carrello);
					}
				}
			}
		}
		
		for(Carrello c : carrelliDaMostrare) {
			c.setCostoSpedizione(CalcoloCosti.calcolaCostiSpedizione(c.getProdotti(), c.getFornitore()));
			c.setTotaleCosto(CalcoloCosti.calcolaPrezzo(c.getProdotti()));
		}
		
		
		String path = "/WEB-INF/carrello.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("fornitori", carrelliDaMostrare);
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
