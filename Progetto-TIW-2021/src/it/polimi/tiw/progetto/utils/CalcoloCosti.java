package it.polimi.tiw.progetto.utils;

import java.util.List;

import it.polimi.tiw.progetto.beans.Fornitore;
import it.polimi.tiw.progetto.beans.Prodotto;
import it.polimi.tiw.progetto.beans.Range;

public class CalcoloCosti {

	/**
	 * Calcola il totale relativo ad un carrello formato da un elenco di prodotti 
	 * offerti da un fornitore
	 * @param prodotti Lista dei prodotti presenti nel carrello
	 * @param fornitore Fornitore dei prodotti
	 * @return Ritorna il totale dato dalla somma dei prezzi dei prodotti
	 */
	public static float calcolaTotale(List<Prodotto> prodotti, Fornitore fornitore) {
		return calcolaPrezzo(prodotti) + calcolaCostiSpedizione(prodotti, fornitore);
	}
	
	/**
	 * Calcola i costi di spedizione relativi ad un carrello formato da un elenco di prodotti 
	 * offerti da un fornitore
	 * @param prodotti Lista dei prodotti presenti nel carrello
	 * @param fornitore Fornitore dei prodotti
	 * @return Ritorna il valore dei costi di spedizione totali
	 */
	public static int calcolaCostiSpedizione(List<Prodotto> prodotti, Fornitore fornitore) {
		
		int costoSpedizione = 0;
		int numeroProdotti = calcolaNumeroProdotti(prodotti);
		float totale = calcolaPrezzo(prodotti);
		
		if(!(fornitore.getSoglia() != -1 && totale > fornitore.getSoglia())) {
			for(Range range : fornitore.getPolitica()) {
				if((range.getMin() <= numeroProdotti && range.getMax() >= numeroProdotti) || 
						(range.getMin() <= numeroProdotti && range.getMax() == 0)) {
					costoSpedizione = range.getPrezzo();
					break;
				}
			}
		}
		
		return costoSpedizione;
	}
	
	/**
	 * Calcola il prezzo totale dei prodotti all'interno di una lista di prodotti
	 * @param prodotti Lista di prodotti
	 * @return Ritorna il prezzo totale dei prodotti
	 */
	public static float calcolaPrezzo(List<Prodotto> prodotti) {
		
		float totale = 0;
		
		for(Prodotto p : prodotti)
			totale += (p.getPrezzo() * p.getQuantita());
		
		return totale;
	}
	
	/**
	 * Calcola il numero totale dei prodotti all'interno di una lista di prodotti
	 * @param prodotti Lista di prodotti
	 * @return Ritorna il numero totale dei prodotti
	 */
	public static int calcolaNumeroProdotti(List<Prodotto> prodotti) {
		
		int numeroProdotti = 0;
		
		for(Prodotto p : prodotti)
			numeroProdotti += p.getQuantita();
		
		return numeroProdotti;
	}
}
