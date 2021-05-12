package it.polimi.tiw.progetto.beans;

import java.util.Date;
import java.util.List;

public class Ordine {
	
	private Integer id;
	private Integer totale;
	private String descrizione;
	private List<Prodotto> prodotti;
	private Date data;
	private String indirizzo;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTotale() {
		return totale;
	}
	public void setTotale(Integer totale) {
		this.totale = totale;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public List<Prodotto> getProdotti() {
		return prodotti;
	}
	public void setProdotti(List<Prodotto> prodotti) {
		this.prodotti = prodotti;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	
	
}
