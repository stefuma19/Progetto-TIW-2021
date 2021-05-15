package it.polimi.tiw.progetto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.progetto.beans.Utente;

public class UtenteDAO {
	private Connection con;

	public UtenteDAO(Connection connection) {
		this.con = connection;
	}

	public Utente controllaCredenziali(String email, String psw) throws SQLException {
		String query = "SELECT  id, nome, cognome FROM utente  WHERE email = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, email);
			pstatement.setString(2, psw);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Utente utente = new Utente();
					utente.setId(result.getInt("id"));
					utente.setNome(result.getString("nome"));
					utente.setCognome(result.getString("cognome"));
					return utente;
				}
			}
		}
	}
}
