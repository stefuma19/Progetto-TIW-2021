package it.polimi.tiw.progetto.beans;

import java.util.List;

public class Fornitore {

	private int ID;
	private String nome;
	private float valutazione; 
	private int soglia;
	private List<Range> politica;
	
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public float getValutazione() {
		return valutazione;
	}
	public void setValutazione(float valutazione) {
		this.valutazione = valutazione;
	}
	public int getSoglia() {
		return soglia;
	}
	public void setSoglia(int soglia) {
		this.soglia = soglia;
	}
	public List<Range> getPolitica() {
		return politica;
	}
	public void setPolitica(List<Range> politica) {
		this.politica = politica;
	}
	
	
}
