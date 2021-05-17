package it.polimi.tiw.progetto.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

public class GestoreConnessione {

	public static Connection getConnection(ServletContext context) throws UnavailableException {
		Connection connection = null;
		try {

			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Impossibile caricare il driver del database");
		} catch (SQLException e) {
			throw new UnavailableException("Impossibile connettersi al database");
		}
		return connection;
	}

	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
}
