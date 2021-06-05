package it.polimi.tiw.progetto.dao;

import java.sql.SQLException;

import it.polimi.tiw.progetto.beans.Carrello;
import it.polimi.tiw.progetto.beans.Prodotto;
import it.polimi.tiw.progetto.beans.Range;

public class OrdineDAO {
	
	public OrdineDAO() {
		
	}
	
	public Carrello calcolaCosti(Carrello carrello){
		
		float totale = 0;
		int numeroProdotti = 0;
		for(Prodotto p : carrello.getProdotti()) {
			totale += (p.getPrezzo() * p.getQuantita());
			numeroProdotti += p.getQuantita();
		}
		carrello.setTotaleCosto(totale);
		if(carrello.getFornitore().getSoglia() != -1 && totale > carrello.getFornitore().getSoglia()) {
			carrello.setCostoSpedizione(0);
		}else {
			for(Range range : carrello.getFornitore().getPolitica()) {
				if((range.getMin() <= numeroProdotti && range.getMax() >= numeroProdotti) || (range.getMin() <= numeroProdotti && range.getMax() == -1) ) {
					carrello.setCostoSpedizione(range.getPrezzo());
					break;
				}
			}
		}
		
		
		return carrello;
	}
	
}
