package myauction;

import java.sql.*;
import oracle.jdbc.driver.OracleDriver;

public class Session {
	private String username;
	private String password;
	private Connection db;

	public Session() {
		try {
			DriverManager.registerDriver(new OracleDriver());
			db = DriverManager.getConnection("jdbc:oracle:thin:@db10.cs.pitt.edu:1521:dbclass", "jdg39", "3555593");
		} catch (Exception e) {}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Connection getDb() {
		return db;
	}

	public void close() {
		try {
			db.close();
		} catch (Exception e) {}
	}
}