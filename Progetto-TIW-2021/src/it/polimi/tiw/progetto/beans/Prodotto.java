package it.polimi.tiw.progetto.beans;

public class Prodotto {

	private Integer ID;
	private String nome;
	private String descrizione;
	private String categoria;
	private String immagine;
	private Fornitore fornitore;
	private Float prezzo;
	private Integer quantita;     //in risultati indica n.ro totale di prodotti del fornitore nel carrello
	private float valore;         //per visualizzare valore totale prodotti in risultati
	
	public Prodotto() {
		fornitore = new Fornitore();
	}
	
	public Integer getID() {
		return ID;
	}
	public void setID(Integer iD) {
		ID = iD;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public String getImmagine() {
		return immagine;
	}
	public void setImmagine(String immagine) {
		this.immagine = immagine;
	}
	public Fornitore getFornitore() {
		return this.fornitore;
	}
	public void setFornitore(Fornitore fornitore) {
		this.fornitore = fornitore;
	}
	public Float getPrezzo() {
		return prezzo;
	}
	public void setPrezzo(Float prezzo) {
		this.prezzo = prezzo;
	}
	public Integer getQuantita() {
		return quantita;
	}
	public void setQuantita(Integer quantita) {
		this.quantita = quantita;
	}

	public float getValore() {
		return valore;
	}

	public void setValore(float valore) {
		this.valore = valore;
	}
	
	
	
}
