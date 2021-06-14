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
import org.thymeleaf.standard.serializer.StandardJavaScriptSerializer;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.mysql.cj.Session;

import it.polimi.tiw.progetto.beans.Carrello;
import it.polimi.tiw.progetto.beans.Prodotto;
import it.polimi.tiw.progetto.beans.Utente;
import it.polimi.tiw.progetto.dao.FornitoreDAO;
import it.polimi.tiw.progetto.dao.OrdineDAO;
import it.polimi.tiw.progetto.dao.ProdottoDAO;
import it.polimi.tiw.progetto.utils.CalcoloCosti;
import it.polimi.tiw.progetto.utils.CookieParser;
import it.polimi.tiw.progetto.utils.GestoreConnessione;
import it.polimi.tiw.progetto.utils.IdException;

@WebServlet("/GoToCarrello")
public class GoToCarrello extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public GoToCarrello() {
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
		
		List<Carrello> daMostrare = new ArrayList<Carrello>();
		ProdottoDAO prodottoDAO = new ProdottoDAO(connection);
		FornitoreDAO fornitoreDAO = new FornitoreDAO(connection);
		
		if(request.getParameter("IdFor") != null) {  //se devo inserire un nuovo prodotto
			try {
				response = addCookie(request, response);
			}catch (IdException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
				return;
			}catch (Exception e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quantità selezionata minore o uguale a 0");
				return;
			}
			response.sendRedirect(getServletContext().getContextPath() + "/GoToCarrello");
		}
		else { 										 //se devo solo visualizzare il carrello
			HttpSession s = request.getSession(); 
			Cookie[] cookies = request.getCookies();
			List<Prodotto> prodotti = new ArrayList<>();
			
			if (cookies != null) {  //se ho dei cookie 
				for (int i = 0; i < cookies.length; i++) { //TODO: x evitare il cookie JSESSIONID?
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
							prodotti = CookieParser.parseCookie(cookies[i]);
							for(Prodotto p : prodotti) {  //prendo info prodotto e le aggiungo al carrello creato in precedenza
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
							daMostrare.add(carrello);
						}
					}
				}
			}
			
			for(Carrello c : daMostrare) {
				c.setCostoSpedizione(CalcoloCosti.calcolaCostiSpedizione(c.getProdotti(), c.getFornitore()));
				c.setTotaleCosto(CalcoloCosti.calcolaPrezzo(c.getProdotti()));
			}
			
			
			String path = "/WEB-INF/carrello.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("fornitori", daMostrare);
			templateEngine.process(path, ctx, response.getWriter());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	private HttpServletResponse addCookie(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ProdottoDAO prodottoDAO = new ProdottoDAO(connection);
		FornitoreDAO fornitoreDAO = new FornitoreDAO(connection);
		if(!prodottoDAO.esisteProdotto(Integer.parseInt(request.getParameter("IdProd"))) || 
				!fornitoreDAO.esisteFornitore(Integer.parseInt(request.getParameter("IdFor")))) {
			throw new IdException();
		}
		boolean primo=true;  //se dobbiamo creare un cookie per il fornitore
		Cookie[] cookies = request.getCookies();
		HttpSession s = request.getSession(); 
		if(Integer.parseInt(request.getParameter("quantita")) < 1){
			throw new Exception();
		}else if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie c = cookies[i];
				String nome = c.getName();
				if(nome.equals(((Utente)s.getAttribute("utente")).getId()+"-"+request.getParameter("IdFor"))) {
					primo = false;
					String valore = c.getValue(); //TODO: controllo se ho già comprato quel prodotto e ne aumento solo la quantita, serve il parser
					List<Prodotto> prodottiPresenti = CookieParser.parseCookie(c);
					boolean presente = false;
					for(Prodotto p: prodottiPresenti) {
						if(p.getID() == Integer.parseInt(request.getParameter("IdProd"))) {
							p.setQuantita(p.getQuantita() + Integer.parseInt(request.getParameter("quantita")));  //TODO: lavora per copia? LB19
							presente = true;
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

		if(primo) {
			String idFor = request.getParameter("IdFor");
			String nome = ((Utente)s.getAttribute("utente")).getId() + "-" + idFor;
			String valore = request.getParameter("IdProd") + "-" + request.getParameter("quantita");
			Cookie coo = new Cookie(nome, valore);
			coo.setMaxAge(3600);
			response.addCookie(coo);
		}
		return response;
	}
	
	public void destroy() {
		try {
			GestoreConnessione.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
