package it.polimi.tiw.progetto.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import it.polimi.tiw.progetto.beans.Prodotto;


public class ProdottoDAO {
	private Connection connection;
	
	public ProdottoDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Prodotto> prendi5Prodotti() throws SQLException {
		List<Prodotto> prodotti = new ArrayList<Prodotto>();
		
		String query = "select * "
				+ "from prodotto p join vendita v1 on p.Id=v1.IdProdotto "
				+ "where Categoria = ? and Prezzo =	(select min(Prezzo) from vendita v2	where v2.IdProdotto = v1.IdProdotto) "
				+ "order by RAND() limit 5"; 
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
					prodotto.setPrezzo(result.getFloat("Prezzo"));
					Blob immagineBlob= result.getBlob("Immagine");
					byte[] byteData = immagineBlob.getBytes(1, (int) immagineBlob.length()); 
					String immagine = new String(Base64.getEncoder().encode(byteData));					
					prodotto.setImmagine(immagine);
					prodotti.add(prodotto);
					i++;
				}
			}
		}
		return prodotti;
	}
}
