package it.polimi.tiw.progetto.beans;

import java.util.List;

public class Carrello {

	private Fornitore fornitore;
	private List<Prodotto> prodotti;
	private float totaleCosto;
	private int costoSpedizione;
	
	public Fornitore getFornitore() {
		return fornitore;
	}
	public void setFornitore(Fornitore fornitore) {
		this.fornitore = fornitore;
	}
	public List<Prodotto> getProdotti() {
		return prodotti;
	}
	public void setProdotti(List<Prodotto> prodotti) {
		this.prodotti = prodotti;
	}
	public float getTotaleCosto() {
		return totaleCosto;
	}
	public void setTotaleCosto(float totaleCosto) {
		this.totaleCosto = totaleCosto;
	}
	public int getCostoSpedizione() {
		return costoSpedizione;
	}
	public void setCostoSpedizione(int costoSpedizione) {
		this.costoSpedizione = costoSpedizione;
	}
}
