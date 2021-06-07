package it.polimi.tiw.progetto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;;

public class IndirizzoDAO {
	private Connection connection;
	
	public IndirizzoDAO(Connection connection) {
		this.connection = connection;
	}

	public int prendiIdIndirizzoByParam(String citta, String via, String cap, int numero) throws SQLException{
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
					return (result.getInt("Id"));
				}
				else
				{
					aggiungiIndirizzo(citta, via, cap, numero);
					prendiIdIndirizzoByParam(citta, via, cap, numero);
				}
			}
		}
		return -1; //non ci arriverò mai
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
