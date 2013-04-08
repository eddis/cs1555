package myauction.view_controller;

import java.awt.Point;
import myauction.CLIObject;

public class LoginScreen extends Screen {
	private CLIObject loginBox;

	public LoginScreen() {
		super();

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

		clear();
		loginBox.setLine(3, "| Password: |_________ |");
		draw();

		String password = getInput();
		setPassword(password);

		clear();

		// TODO: validate login
		boolean isAdmin = false;
		if (isAdmin) {
			return ADMIN;
		}
		return CUSTOMER;
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