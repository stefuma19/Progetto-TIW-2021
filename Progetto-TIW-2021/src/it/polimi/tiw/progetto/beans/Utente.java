package it.polimi.tiw.progetto.beans;

public class Utente {

	private int id;
	private String email;
	private String nome;
	private String cognome;
	private String indirizzo; //TODO: come string?
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id= id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getCognome() {
		return cognome;
	}
	
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	
	public String getIndirizzo() {
		return indirizzo;
	}
	
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
}
