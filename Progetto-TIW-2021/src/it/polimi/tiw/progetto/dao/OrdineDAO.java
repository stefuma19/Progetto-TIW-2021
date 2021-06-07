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
import it.polimi.tiw.progetto.beans.Fornitore;
import it.polimi.tiw.progetto.beans.Ordine;
import it.polimi.tiw.progetto.beans.Prodotto;
import it.polimi.tiw.progetto.beans.Range;
import it.polimi.tiw.progetto.utils.CalcoloCosti;

public class OrdineDAO {
	private Connection connection;
	
	public OrdineDAO(Connection connection) {
		this.connection = connection;
	}
	
	public OrdineDAO() {
		
	}
	
	
	public List<Ordine> prendiOrdiniByIdUtente(int IdUtente) throws SQLException{  
		//TODO: testare
		List<Ordine> ordini = new ArrayList<Ordine>();
		List<Integer> idOrdini = new ArrayList<Integer>();
		//prendo lista di id dei miei ordini e poi per ogni id prendo info prodotti ecc
		String query = "select Id from ordine ord where ord.IdUtente = ? "; 
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, IdUtente);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					idOrdini.add(result.getInt("Id"));
				}
			}
		}

		FornitoreDAO fornitoreDAO = new FornitoreDAO(connection);
		ProdottoDAO prodottoDAO = new ProdottoDAO(connection);
		
		for(Integer idOrdine : idOrdini) {

			Ordine ordine = new Ordine();
			query = "select * from ordine ord join contenuto co on ord.Id=co.IdOrdine where ord.Id= ? "; 
			
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setInt(1, idOrdine);
				try (ResultSet result = pstatement.executeQuery();) {
					List<Prodotto> prodotti = new ArrayList<>();
					while (result.next()) {
						Prodotto prodotto = prodottoDAO.prendiProdottoByIdProdottoFornitore(Integer.parseInt(result.getString("IdProdotto")), 
								Integer.parseInt(result.getString("IdFornitore")));
						prodotto.setQuantita(Integer.parseInt(result.getString("Quantita")));
						prodotti.add(prodotto);

						ordine.setData(result.getDate("Data"));
						ordine.setFornitore(fornitoreDAO.prendiFornitoreById(Integer.parseInt(result.getString("IdFornitore"))));
					}
					ordine.setId(idOrdine);
					ordine.setProdotti(prodotti);
					ordine.setTotale(CalcoloCosti.calcolaTotale(prodotti, ordine.getFornitore()));
				}
			}
			ordini.add(ordine);
			
		}
		return ordini;
	}
	
}
