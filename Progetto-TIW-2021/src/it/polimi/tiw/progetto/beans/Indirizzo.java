package it.polimi.tiw.progetto.beans;

public class Indirizzo {
	private int id;
	private String citta;
	private String via;
	private String cap;
	private int numero;
	
	public Indirizzo(int id, String citta, String via, String cap, int numero) {
		super();
		this.id = id;
		this.citta = citta;
		this.via = via;
		this.cap = cap;
		this.numero = numero;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCitta() {
		return citta;
	}
	public void setCitta(String citta) {
		this.citta = citta;
	}
	public String getVia() {
		return via;
	}
	public void setVia(String via) {
		this.via = via;
	}
	public String getCap() {
		return cap;
	}
	public void setCap(String cap) {
		this.cap = cap;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	
}
