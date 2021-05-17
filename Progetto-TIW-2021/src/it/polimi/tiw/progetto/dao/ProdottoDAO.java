package it.polimi.tiw.progetto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.progetto.beans.Prodotto;


public class ProdottoDAO {
	private Connection connection;
	
	public ProdottoDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Prodotto> prendi5Prodotti() throws SQLException {
		List<Prodotto> prodotti = new ArrayList<Prodotto>();
		
		String query = "SELECT * FROM prodotto WHERE Categoria = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, "Libri");
			try (ResultSet result = pstatement.executeQuery();) {
				int i = 0;
				while (result.next() && i<5) {
					Prodotto prodotto = new Prodotto();
					prodotto.setID(result.getInt("Id"));
					prodotto.setNome(result.getString("Nome"));
					prodotto.setDescrizione(result.getString("Descrizione"));
					prodotto.setCategoria(result.getString("Categoria"));
					prodotto.setImmagine(result.getString("Immagine"));
					prodotti.add(prodotto);
					i++;
				}
			}
		}
		return prodotti;
	}
}
