package it.polimi.tiw.progetto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.progetto.beans.Indirizzo;;

public class IndirizzoDAO {
	private Connection connection;
	
	public IndirizzoDAO(Connection connection) {
		this.connection = connection;
	}

	public int prendiIdIndirizzoByParam(String citta, String via, String cap, int numero) throws SQLException{
		int idDaRitornare = -1;
		String query = "select Id "
				+ "from indirizzo ind "
				+ "where ind.Citta = ? and ind.via = ? and ind.cap = ? and ind.numero= ? "; 
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, citta);
			pstatement.setString(2, via);
			pstatement.setString(3, cap);
			pstatement.setInt(4, numero);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					idDaRitornare = (result.getInt("Id"));
				}
				else
				{
					aggiungiIndirizzo(citta, via, cap, numero);
					return prendiIdIndirizzoByParam(citta, via, cap, numero);
				}
			}
		}
		return idDaRitornare; 
	}
	
	public Indirizzo prendiIndirizzoById(int Id) throws SQLException{
		Indirizzo indirizzo = new Indirizzo();
		String query = "select * "
				+ "from indirizzo ind "
				+ "where ind.Id= ? "; 
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, Id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					indirizzo.setId(Id);
					indirizzo.setCitta(result.getString("Citta"));
					indirizzo.setVia(result.getString("Via"));
					indirizzo.setCap(result.getString("Cap"));
					indirizzo.setNumero(result.getInt("Numero"));
				}
			}
		}
		return indirizzo; 
	}
	
	public void aggiungiIndirizzo(String citta, String via, String cap, int numero) throws SQLException{
		String query = "INSERT INTO indirizzo (Citta, Via, Cap, Numero) VALUES( ? , ? , ? , ? )";
		
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, citta);
			pstatement.setString(2, via);
			pstatement.setString(3, cap);
			pstatement.setInt(4, numero);
			pstatement.executeUpdate();
		}
	}
	
}
