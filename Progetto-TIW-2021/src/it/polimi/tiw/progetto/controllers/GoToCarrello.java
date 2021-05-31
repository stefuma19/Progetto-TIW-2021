package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
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

@WebServlet("/GoToCarrello")
public class GoToCarrello extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	public GoToCarrello() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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
		
		
		String path = "/WEB-INF/carrello.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
