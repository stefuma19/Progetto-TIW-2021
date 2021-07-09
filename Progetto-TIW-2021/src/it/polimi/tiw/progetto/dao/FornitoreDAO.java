package it.polimi.tiw.progetto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.progetto.beans.Fornitore;
import it.polimi.tiw.progetto.beans.Range;
import it.polimi.tiw.progetto.utils.IdException;

public class FornitoreDAO {

	private Connection connection;
	
	public FornitoreDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Fornitore prendiFornitoreById(int id) throws SQLException, IdException{
		//TODO: query da cambiare? 
		String query = "select * from fornitore f join politica po on po.Id=f.IdPoliticaForn "  //TODO: da testare
				+ "where f.Id= ? "; 
		Fornitore fornitore = new Fornitore();
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results
					throw new IdException();
				while (result.next()) {
					fornitore.setNome(result.getString("NomeFor"));
					fornitore.setValutazione(result.getString("Valutazione"));
					fornitore.setSoglia((result.getString("Soglia") == null) ? -1 : Integer.parseInt(result.getString("Soglia")));
					fornitore.setID(result.getInt("Id"));
				}
			}
		}
		
		List<Range> fasce = new ArrayList<Range>();
		query = "select * from fornitore fo, politica po, fascia fa, composizione co "
				+ "where po.Id=fo.IdPoliticaForn and fa.IdFascia=co.IdFasceComp and co.IdPoliticaComp=po.Id and fo.Id = ?"; 
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, id);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Range fascia = new Range(result.getInt("IdFascia"),result.getInt("Min"),result.getInt("Max"),result.getInt("Prezzo"));
					fasce.add(fascia);
				}
			}
		}
		fornitore.setPolitica(fasce);
		
		return fornitore;
	}
	
	public boolean esisteFornitore(int idForn) throws SQLException {
		String query = "select * from fornitore fo where fo.Id = ?"; 
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idForn);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results
					return false;
				return true;
			}
		}
	}
}
