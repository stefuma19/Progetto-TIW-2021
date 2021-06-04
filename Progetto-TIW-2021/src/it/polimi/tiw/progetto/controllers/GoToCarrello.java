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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.standard.serializer.StandardJavaScriptSerializer;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.mysql.cj.Session;

import it.polimi.tiw.progetto.beans.Prodotto;
import it.polimi.tiw.progetto.dao.ProdottoDAO;
import it.polimi.tiw.progetto.utils.CookieParser;
import it.polimi.tiw.progetto.utils.GestoreConnessione;

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
		
		List<List<Prodotto>> daMostrare = new ArrayList<List<Prodotto>>();
		ProdottoDAO prodottoDAO = new ProdottoDAO(connection);
		
		if(request.getParameter("IdFor") != null) {
			response = addCookie(request, response);
		}
		
		Cookie[] cookies = request.getCookies();
		List<Prodotto> prodotti = new ArrayList<>();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) { //TODO: x evitare il cookie JSESSIONID?
				List<Prodotto> listaForn = new ArrayList<Prodotto>();
				if(!cookies[i].getName().equals("JSESSIONID")) {
					prodotti = CookieParser.parseCookie(cookies[i]);
					for(Prodotto p : prodotti) {
						try {
							listaForn.add(prodottoDAO.prendiOffertaByCookieInfo(p));
						} catch (SQLException e) {
							response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Impossibile recuperare prodotti da cookie info");
							return;
						}
					}
					daMostrare.add(listaForn);
				}
			}
		}
		

		String path = "/WEB-INF/carrello.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("fornitori", daMostrare);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	private HttpServletResponse addCookie(HttpServletRequest request, HttpServletResponse response) {
		boolean primo=true;  //se dobbiamo creare un cookie per il fornitore
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie c = cookies[i];
				String nome = c.getName();
				if(nome.equals(request.getParameter("IdFor"))) {
					primo = false;
					String valore = c.getValue(); //TODO: controllo se ho già comprato quel prodotto e ne aumento solo la quantita, serve il parser
					valore += "_" + request.getParameter("IdProd") + "-" + request.getParameter("quantita");
					Cookie coo = new Cookie(nome, valore);
					coo.setMaxAge(3600);
					response.addCookie(coo);
					break;
				}
			}
		}

		if(primo) {
			String idFor = request.getParameter("IdFor");
			String nome = idFor;
			String valore = request.getParameter("IdProd") + "-" + request.getParameter("quantita");
			Cookie coo = new Cookie(nome, valore);
			coo.setMaxAge(3600);
			response.addCookie(coo);
		}
		return response;
	}
}
