package it.polimi.tiw.progetto.beans;

import java.util.ArrayList;
import java.util.List;

public class Fornitore {

	private int ID;
	private String nome;
	private String valutazione; 
	private int soglia;
	private List<Range> politica;
	
	public Fornitore() {
		politica = new ArrayList<Range>();
	}
	
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
	public String getValutazione() {
		return valutazione;
	}
	public void setValutazione(String valutazione) {
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
