package it.polimi.tiw.progetto.utils;

import java.util.List;

import it.polimi.tiw.progetto.beans.Fornitore;
import it.polimi.tiw.progetto.beans.Prodotto;
import it.polimi.tiw.progetto.beans.Range;

public class CalcoloCosti {

	public static float calcolaTotale(List<Prodotto> prodotti, Fornitore fornitore) {
		return calcolaPrezzo(prodotti)+ calcolaCostiSpedizione(prodotti, fornitore);
	}
	
	public static int calcolaCostiSpedizione(List<Prodotto> prodotti, Fornitore fornitore) {
		int costoSpedizione = 0;
		int numeroProdotti = calcolaNumeroProdotti(prodotti);
		float  totale = calcolaPrezzo(prodotti);
		if(!(fornitore.getSoglia() != -1 && totale > fornitore.getSoglia())) {
			for(Range range : fornitore.getPolitica()) {
				if((range.getMin() <= numeroProdotti && range.getMax() >= numeroProdotti) || (range.getMin() <= numeroProdotti && range.getMax() == -1) ) {
					costoSpedizione = range.getPrezzo();
					break;
				}
			}
		}
		return costoSpedizione;
	}
	
	public static float calcolaPrezzo(List<Prodotto> prodotti) {
		float totale = 0;
		for(Prodotto p : prodotti) {
			totale += (p.getPrezzo() * p.getQuantita());
		}
		return totale;
	}
	
	public static int calcolaNumeroProdotti(List<Prodotto> prodotti) {
		int numeroProdotti = 0;
		for(Prodotto p : prodotti) {
			numeroProdotti += p.getQuantita();
		}
		return numeroProdotti;
	}
}
