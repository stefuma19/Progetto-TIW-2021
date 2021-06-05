package it.polimi.tiw.progetto.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import it.polimi.tiw.progetto.beans.Prodotto;

public class CookieParser {

	public static List<Prodotto> parseCookie(Cookie cookie){
		List<Prodotto> carrello = new ArrayList<Prodotto>();
	
		if (cookie != null) {
			String idForn = cookie.getName();
			String valore = cookie.getValue();
			String prodotti[] = valore.split("_");
			for(int i = 0; i < prodotti.length; i++) {
				String  info[] = prodotti[i].split("-");
				Prodotto p = new Prodotto();
				p.setID(Integer.parseInt(info[0]));
				p.setQuantita(Integer.parseInt(info[1]));
				p.getFornitore().setID(Integer.parseInt(idForn));
				carrello.add(p);
			}
		}
		return carrello;
	}
	
	public static Cookie creaCookieByProdotti(List<Prodotto> prodotti) {
		if(prodotti.size() != 0){
			String nome = String.valueOf(prodotti.get(0).getFornitore().getID());
			boolean primo = true;
			String valore = "";
			for(Prodotto p : prodotti) {
				valore += ((primo)? "" : "_") + p.getID() + "-" + p.getQuantita();
				primo = false;
			}
			Cookie c = new Cookie(nome,valore);
			return c;
		}else {
			return null;
		}
	}
}
