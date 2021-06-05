package it.polimi.tiw.progetto.beans;

import java.util.List;

public class Carrello {

	private int IdForn;
	private List<Prodotto> prodotti;
	private int totaleCosto;
	private int costoSpedizione;
	
	public int getIdForn() {
		return IdForn;
	}
	public void setIdForn(int idForn) {
		IdForn = idForn;
	}
	public List<Prodotto> getProdotti() {
		return prodotti;
	}
	public void setProdotti(List<Prodotto> prodotti) {
		this.prodotti = prodotti;
	}
	public int getTotaleCosto() {
		return totaleCosto;
	}
	public void setTotaleCosto(int totaleCosto) {
		this.totaleCosto = totaleCosto;
	}
	public int getCostoSpedizione() {
		return costoSpedizione;
	}
	public void setCostoSpedizione(int costoSpedizione) {
		this.costoSpedizione = costoSpedizione;
	}
}
