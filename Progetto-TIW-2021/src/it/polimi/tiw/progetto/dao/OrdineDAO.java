package it.polimi.tiw.progetto.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.progetto.beans.Carrello;
import it.polimi.tiw.progetto.beans.Ordine;
import it.polimi.tiw.progetto.beans.Prodotto;
import it.polimi.tiw.progetto.beans.Range;

public class OrdineDAO {
	private Connection connection;
	
	public OrdineDAO(Connection connection) {
		this.connection = connection;
	}
	
	public OrdineDAO() {
		
	}
	
	public Carrello calcolaCosti(Carrello carrello){
		
		float totale = 0;
		int numeroProdotti = 0;
		for(Prodotto p : carrello.getProdotti()) {
			totale += (p.getPrezzo() * p.getQuantita());
			numeroProdotti += p.getQuantita();
		}
		carrello.setTotaleCosto(totale);
		if(carrello.getFornitore().getSoglia() != -1 && totale > carrello.getFornitore().getSoglia()) {
			carrello.setCostoSpedizione(0);
		}else {
			for(Range range : carrello.getFornitore().getPolitica()) {
				if((range.getMin() <= numeroProdotti && range.getMax() >= numeroProdotti) || (range.getMin() <= numeroProdotti && range.getMax() == -1) ) {
					carrello.setCostoSpedizione(range.getPrezzo());
					break;
				}
			}
		}
		
		return carrello;
	}
	
	public List<Ordine> prendiOrdiniByIdUtenteFornitore(int IdUtente, int IdForn) throws SQLException{  
		//TODO: testare
		List<Ordine> ordini = new ArrayList<Ordine>();
		List<Integer> idOrdini = new ArrayList<Integer>();
		//prendo lista di id dei miei ordini e poi per ogni id prendo info prodotti ecc
		String query = "select Id from ordine or where or.IdUtente = ? and or.IdFornitore = ?"; 
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, IdUtente);
			pstatement.setInt(2, IdForn);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					idOrdini.add(result.getInt("Id"));
				}
			}
		}

		FornitoreDAO fornitoreDAO = new FornitoreDAO(connection);
		ProdottoDAO prodottoDAO = new ProdottoDAO(connection);
		
		for(Integer idOrdine : idOrdini) {
			
			query = "select * from ordine or join contenuto co on or.Id=co.IdOrdine where or.IdUtente = ? and or.IdFornitore = ? and or.Id= ? "; 
			
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setInt(1, IdUtente);
				pstatement.setInt(2, IdForn);
				pstatement.setInt(3, idOrdine);
				try (ResultSet result = pstatement.executeQuery();) {
					Ordine ordine = new Ordine();
					List<Prodotto> prodotti = new ArrayList<>();
					while (result.next()) {
						prodotti.add(prodottoDAO.prendiProdottoByIdProdottoFornitore(Integer.parseInt(result.getString("IdProdotto")), 
								Integer.parseInt(result.getString("IdFornitore"))));
					}
					ordine.setId(idOrdine);
					ordine.setProdotti(prodotti);
					ordine.setData(result.getDate("Data"));
					ordine.setFornitore(fornitoreDAO.prendiFornitoreById(IdForn));
				}
			}
			
			
		}
		return ordini;
	}
	
}
