package it.polimi.tiw.progetto.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Queue;

import it.polimi.tiw.progetto.beans.Prodotto;


public class ProdottoDAO {
	private Connection connection;
	
	public ProdottoDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Prodotto> prendiProdotti(Queue<Integer> presenti, int quantita) throws SQLException {
		List<Prodotto> prodotti = new ArrayList<Prodotto>();
		String query;
		boolean valido = presenti!=null && !presenti.isEmpty();
		if(valido) {
			String valoriNotIn = "?";
			for(int i=1;i<presenti.size();i++) {
				valoriNotIn += ",?";
			}
			query = "select * "
					+ "from prodotto p join vendita v1 on p.Id=v1.IdProdotto "
					+ "where Categoria = ? and Id not in ("+valoriNotIn+") and Prezzo =	(select min(Prezzo) from vendita v2	where v2.IdProdotto = v1.IdProdotto) "
					+ "order by RAND() limit ?"; 
		}
		else {
			query = "select * "
				+ "from prodotto p join vendita v1 on p.Id=v1.IdProdotto "
				+ "where Categoria = ? and Prezzo =	(select min(Prezzo) from vendita v2	where v2.IdProdotto = v1.IdProdotto) "
				+ "order by RAND() limit ?"; 
		}
		
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, "Libri");
			if(valido) {
				int i=0;
				Object[] array = presenti.toArray();
				while(i<presenti.size()) {
					pstatement.setInt(i+2, (Integer)array[i]);
					i++;
				}
				pstatement.setInt(i+2, quantita);
			}
			else 
				pstatement.setInt(2, quantita);
			try (ResultSet result = pstatement.executeQuery();) {
				int i = 0;
				while (result.next() && i<quantita) {
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
	
	public List<Prodotto> prendiProdottiCercati(String parolaChiave) throws SQLException{
		List<Prodotto> prodotti = new ArrayList<Prodotto>();
		String parametro = "%"+parolaChiave+"%";
		//TODO: query da cambiare? 
		String query = "select * from prodotto p join vendita v1 on p.Id=v1.IdProdotto "
				+ "where (Nome LIKE ? or Descrizione LIKE ?) and Prezzo =	(select min(Prezzo) from vendita v2	where v2.IdProdotto = v1.IdProdotto) "
				+ "order by Prezzo"; 
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, parametro);
			pstatement.setString(2, parametro);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
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
				}
			}
		}
		return prodotti;
	}
	
	public List<Prodotto> prendiOfferteById(int ID) throws SQLException{
		List<Prodotto> prodotti = new ArrayList<Prodotto>();
		//TODO: query da cambiare? 
		String query = "select * from prodotto p, vendita v, fornitore f "
				+ "where p.Id=v.IdProdotto and v.IdFornitore=f.Id and p.Id = ? "
				+ "order by Prezzo"; 
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, ID);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Prodotto prodotto = new Prodotto();
					prodotto.setID(result.getInt("Id"));
					prodotto.setNome(result.getString("Nome"));
					prodotto.setDescrizione(result.getString("Descrizione"));
					prodotto.setCategoria(result.getString("Categoria"));	
					prodotto.setPrezzo(result.getFloat("Prezzo"));
					prodotto.setFornitore(result.getString("NomeFor"));
					Blob immagineBlob= result.getBlob("Immagine");
					byte[] byteData = immagineBlob.getBytes(1, (int) immagineBlob.length()); 
					String immagine = new String(Base64.getEncoder().encode(byteData));					
					prodotto.setImmagine(immagine);
					prodotti.add(prodotto);
				}
			}
		}
		return prodotti;
	}
	
	public Prodotto prendiProdottoById(int ID) throws SQLException{
		Prodotto prodotto = new Prodotto();
		String query = "select * "
				+ "from prodotto p join vendita v1 on p.Id=v1.IdProdotto "
				+ "where p.Id = ? and Prezzo = (select min(Prezzo) from vendita v2	where v2.IdProdotto = v1.IdProdotto) "; 
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, ID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					prodotto.setID(result.getInt("Id"));
					prodotto.setNome(result.getString("Nome"));
					prodotto.setDescrizione(result.getString("Descrizione"));
					prodotto.setCategoria(result.getString("Categoria"));	
					prodotto.setPrezzo(result.getFloat("Prezzo"));
					Blob immagineBlob= result.getBlob("Immagine");
					byte[] byteData = immagineBlob.getBytes(1, (int) immagineBlob.length()); 
					String immagine = new String(Base64.getEncoder().encode(byteData));					
					prodotto.setImmagine(immagine);
				}
			}
		}
		return prodotto;
	}
}
