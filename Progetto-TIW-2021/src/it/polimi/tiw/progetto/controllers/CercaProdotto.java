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
import javax.servlet.http.Cookie;
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
import it.polimi.tiw.progetto.utils.CalcoloCosti;
import it.polimi.tiw.progetto.utils.CookieParser;
import it.polimi.tiw.progetto.utils.GestoreConnessione;
import it.polimi.tiw.progetto.utils.IdException;


@WebServlet("/CercaProdotto")
public class CercaProdotto extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
	public CercaProdotto() {
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
	
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		ProdottoDAO prodottoDAO = new ProdottoDAO(connection);
		List<Prodotto> offerte = new ArrayList<Prodotto>();
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		if(request.getParameter("idProdotto") != null) {

			try {
				Integer.parseInt(request.getParameter("idProdotto"));
			} catch (NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Richiesta mal formata");
				return;
		    }

			
			int idProdotto = Integer.parseInt(request.getParameter("idProdotto"));
			List<Prodotto> listaProdotti = new ArrayList<>();
			try {
				listaProdotti.add(prodottoDAO.prendiProdottoById(idProdotto));
				request.getSession().setAttribute("listaProdotti", listaProdotti);
			}catch(SQLException e) {
				e.printStackTrace();
			}catch (IdException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
				return;
			}
			
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
				offerte = prodottoDAO.prendiOfferteByIdProdotto(idProdotto);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare prodotti da id");
				return;
			}catch (IdException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
				return;
			}
			
			HttpSession s = request.getSession(); 
			Cookie[] cookies = request.getCookies();
			List<Prodotto> prodotti = new ArrayList<Prodotto>();
			List<Prodotto> prodottiOfferta = new ArrayList<Prodotto>();
			for(Prodotto o : offerte) {
				int idForn = o.getFornitore().getID();
				o.setValore(0); //spesa totale del fornitore nel carrello
				o.setQuantita(0); //# prodotti totale del fornitore nel carrello
				if (cookies != null) {
					for (int i = 0; i < cookies.length; i++) {
						
						prodottiOfferta = new ArrayList<Prodotto>();
						
						if(!cookies[i].getName().equals("JSESSIONID")) {
							if(cookies[i].getName().split("-")[0].equals(String.valueOf((((Utente)s.getAttribute("utente")).getId())))
									&& cookies[i].getName().split("-")[1].equals(String.valueOf(idForn)))
							{
								prodotti = CookieParser.parseCookie(cookies[i]);
								for(Prodotto p : prodotti) {
									try {
										Prodotto daAggiungere = prodottoDAO.prendiProdottoByIdProdottoFornitore(p.getID(),p.getFornitore().getID());
										daAggiungere.setQuantita(p.getQuantita());
										prodottiOfferta.add(daAggiungere);
									} catch (SQLException e) {
										response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare prodotti da cookie info");
										return;
									}catch (IdException e) {
										response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
										return;
									}
								}
								o.setValore(CalcoloCosti.calcolaPrezzo(prodottiOfferta));
								o.setQuantita(CalcoloCosti.calcolaNumeroProdotti(prodottiOfferta));
						
							}
						}
					}
				}
			}
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro mancante");
			return;
		}
		
		List<Prodotto> prodotti = (List<Prodotto>)request.getSession().getAttribute("listaProdotti");
		String path = "/WEB-INF/risultati.html";
		response.setContentType("text");
		ctx.setVariable("offerte", offerte);
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
