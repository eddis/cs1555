package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import myauction.CLIObject;
import myauction.Session;

public class LoginScreen extends Screen {
	private CLIObject loginBox;
	private static PreparedStatement isCustomerStatement = null;
	private static PreparedStatement isAdminStatement = null;

	public LoginScreen(Session session) {
		super(session);

		loginBox = new CLIObject(WIDTH, 6);
		loginBox.setLine(0, "---Login----------------");
		loginBox.setLine(1, "|                      |");
		loginBox.setLine(2, "| Username: |_________ |");
		loginBox.setLine(3, "| Password: __________ |");
		loginBox.setLine(4, "|                      |");
		loginBox.setLine(5, "------------------------");

		addScreenObject(loginBox, new Point(28, 9));
	}

	public int run() {
		draw();

		String username = getInput();

		setUsername(username);
		loginBox.setLine(3, "| Password: |_________ |");
		draw();

		String password = getInput();

		setPassword(password);
		draw();

		session.setUsername(username);
		session.setPassword(password);

		// validate login
		int result = checkUser(username, password);
		if (result == -1) {
			updateStatus("Username/password incorrect!");
			setUsername("");
			setPassword("");
			return LOGIN;
		}

		if (result == 1) {
			return ADMIN;
		}
		return CUSTOMER;
	}

	public int checkUser(String username, String password) {
		try {
			ResultSet results;

			if (isCustomerStatement == null) {
				isCustomerStatement = session.getDb().prepareStatement("select count(*) as num_customers from customer where login = ? and password = ?");
			}

			if (isAdminStatement == null) {
				isAdminStatement = session.getDb().prepareStatement("select count(*) as num_admins from administrator where login = ? and password = ?");
			}

			isCustomerStatement.setString(1, username);
			isCustomerStatement.setString(2, password);

			results = isCustomerStatement.executeQuery();
			results.next();
			if (results.getInt("num_customers") > 0) {
				return 0;
			}

			isAdminStatement.setString(1, username);
			isAdminStatement.setString(2, password);

			results = isAdminStatement.executeQuery();
			results.next();
			if (results.getInt("num_admins") > 0) {
				return 1;
			}
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}

		return -1;
	}

	public void setUsername(String username) {
		int difference = 10 - username.length();
		String line = "| Username: " + username;
		for (int i = 0; i < difference; i++) {
			line += "_";
		}
		line += " |";
		loginBox.setLine(2, line);
	}

	public void setPassword(String password) {
		int difference = 10 - password.length();
		String line = "| Password: ";
		for (int i = 0; i < password.length(); i++) {
			line += "*";
		}
		for (int i = 0; i < difference; i++) {
			line += "_";
		}
		line += " |";
		loginBox.setLine(3, line);
	}
}