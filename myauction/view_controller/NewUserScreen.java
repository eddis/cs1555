package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import myauction.CLIObject;
import myauction.Session;

public class NewUserScreen extends Screen {
	private CLIObject createUserBox;
	private CLIObject headerBox;
	private String userTable;
	private static PreparedStatement checkEmailStatement = null;
	private static PreparedStatement checkUsernameStatement = null;
	private static PreparedStatement addUserStatement = null;



	public NewUserScreen(Session session) {
		super(session);
		headerBox = new CLIObject(WIDTH, 2);
		headerBox.setLine(0, "Previous (<)                    Create New User                               ");
		headerBox.setLine(1, "------------------------------------------------------------------------------");


		createUserBox = new CLIObject(WIDTH, 10);
		createUserBox.setLine(0, "---New User Credentials---------------------");
		createUserBox.setLine(1, "|                                          |");
		createUserBox.setLine(2, "| Is user an administrator (y/n)? |_       |");
		createUserBox.setLine(3, "| Name: ____________________               |");
		createUserBox.setLine(4, "| Email: ____________________              |");
		createUserBox.setLine(5, "| Address: ______________________________  |");
		createUserBox.setLine(6, "| Username: __________                     |");
		createUserBox.setLine(7, "| Password: __________                     |");
		createUserBox.setLine(8, "|                                          |");
		createUserBox.setLine(9, "--------------------------------------------");

		addScreenObject(headerBox, new Point(1, 1));
		addScreenObject(createUserBox, new Point(3, 6));


	}

	public int run() {
		checkEmailStatement = null;
		checkUsernameStatement = null;
		addUserStatement = null;
		draw();
		String admin = getInput();
		if (isPrevious(admin)) {
			reset();
			return ADMIN;
		}
		userTable = setAdministrator(admin);
		clear();
		
		createUserBox.setLine(3, "| Name: |___________________               |");
		draw();
		String name = getInput();
		if (isPrevious(name)) {
			reset();
			return ADMIN;
		}
		setName(name);
		clear();

		createUserBox.setLine(4, "| Email: |___________________               |");
		draw();
		String email = getInput();
		if (isPrevious(email)) {
			reset();
			return ADMIN;
		}
		clear();
		while(!checkEmail(email)) {
			updateStatus(email + " is not a valid email, please choose another.");
			draw();
			email = getInput();
			if (isPrevious(email)) {
				reset();
				return ADMIN;
			}
			clear();
		}
		setEmail(email);

		
		createUserBox.setLine(5, "| Address: |_____________________________  |");
		draw();
		String address = getInput();
		if (isPrevious(address)){
			reset();
			return ADMIN;
		}
		setAddress(address);
		clear();

		createUserBox.setLine(6, "| Username: |_________                     |");
		draw();
		String username = getInput();
		if (isPrevious(username)){
			reset();
			return ADMIN;
		}
		while(!checkUsername(username)) {
			updateStatus(username + " is not a valid username, please choose another.");
			draw();
			username = getInput();
			if (isPrevious(username)){
				reset();
				return ADMIN;
			}
			clear();
		}
		setUsername(username);

		createUserBox.setLine(7, "| Password: |_________                     |");
		draw();
		String password = getInput();
		if (isPrevious(password)) {
			reset();
			return ADMIN;
		}
		setPassword(password);

		int rowAdded = addUser(username, password, name, address, email);
		if (rowAdded > 0){
			updateStatus("You have added" + username + "successfully!");
		}
		return NEW_USER;
		
	}
	public void reset(){
		setAdministrator("");
		setName("");
		setEmail("");
		setUsername("");
		setPassword("");
	}

	public boolean isPrevious(String input) {
		if (input.equals("<")) {
			return true;
		}
		return false;
	}

	public int addUser(String username, String password, String name, String address, String email) {
		try {

			if (addUserStatement == null) {
				String insertUser = "insert into "+ userTable + " values (?, ?, ?, ?, ?)";
				addUserStatement = session.getDb().prepareStatement(insertUser);
			}
			addUserStatement.setString(1, username);
			addUserStatement.setString(2, password);
			addUserStatement.setString(3, name);
			addUserStatement.setString(4, address);
			addUserStatement.setString(5, email);

			return addUserStatement.executeUpdate();

		} catch(SQLException e){
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}

		return -1;
	}

	public String setAdministrator(String isAdmin) {
		if (isAdmin.equals("")) {
			createUserBox.setLine(2, "| Is user an administrator (y/n)? |_       |");
			return "";
		} else if (isAdmin.equals("y")){
			createUserBox.setLine(2, "| Is user an administrator (y/n)? y        |");
			return "administrator";
		}
		createUserBox.setLine(2, "| Is user an administrator (y/n)? n        |");
		return "customer";
	}

	public void setName(String name) {
		int difference = 20 -name.length();
		String line = "| Name:  "+ name;
		for(int i = 0; i < difference; i++) {
			line += "_";
		}
		for (int i = 0; i < 15; i++ ){
			line += " ";
		}
		line += "|";
		createUserBox.setLine(3, line);
	}

	public void setEmail(String email) {
		int difference = 20 -email.length();
		String line = "| Email: "+ email;
		for (int i = 0; i < difference; i++) {
			line += "_";
		}
		for (int i = 0; i < 14; i++ ){
			line += " ";
		}
		line += "|";
		createUserBox.setLine(4, line);

	}

	public boolean checkEmail(String email) {
		try {
			ResultSet results;
			if (checkEmailStatement == null) {
				String checkEmail = "select count(*) as num_emails from "+ userTable + " where email = ?";
				checkEmailStatement = session.getDb().prepareStatement(checkEmail);
			}

			checkEmailStatement.setString(1, email);

			results = checkEmailStatement.executeQuery();
			results.next();
			if (results.getInt("num_emails") <= 0){
				return true;
			}
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}
		return false;
	}

	public void setAddress(String address) {
		int difference = 20 -address.length();
		String line = "| Address: "+ address;
		for (int i = 0; i < difference; i++) {
			line += "_";
		}
		line += "  |";
		createUserBox.setLine(5, line);

	}

	public void setUsername(String username) {
		int difference = 10 -username.length();
		String line = "| Username: "+ username;
		for (int i = 0; i < difference; i++) {
			line += "_";
		}
		for (int i = 0; i < 21; i++ ){
			line += " ";
		}
		line += "|";
		createUserBox.setLine(6, line);
	}

	public void setPassword(String password) {
		int difference = 10 - password.length();
		String line = "| Password: ";
		for (int i = 0; i< password.length(); i++) {
			line += "*";
		}
		for (int i = 0; i < difference; i++) {
			line += "_";
		}
		for (int i = 0; i < 21; i++ ){
			line += " ";
		}
		line += "|";
		createUserBox.setLine(7, line);
	}

	public boolean checkUsername(String username) {
		try {
			ResultSet results;
			if (checkUsernameStatement == null) {
				String checkUser = "select count(*) as num_emails from " + userTable +" where login = ?";
				checkUsernameStatement = session.getDb().prepareStatement(checkUser);
			}

			checkUsernameStatement.setString(1, username);

			results = checkUsernameStatement.executeQuery();
			results.next();
			if (results.getInt("num_emails") <= 0){
				return true;
			}
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}
		return false;
	}

}