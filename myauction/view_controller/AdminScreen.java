package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import myauction.CLIObject;
import myauction.Session;

public class AdminScreen extends Screen {
	public AdminScreen(Session session) {
		super(session);

		productsBox = new CLIObject(WIDTH, 6);
		productsBox.setLine(0, "---Customers-------------");
		productsBox.setLine(1, "|                      |");
		productsBox.setLine(2, "| Browse Products (b)  |");
		productsBox.setLine(3, "| Search Products (s)  |");
		productsBox.setLine(4, "|                      |");
		productsBox.setLine(5, "------------------------");

		suggestionsBox = new CLIObject(WIDTH, 20);
		suggestionsBox.setLine(0, "|");
		suggestionsBox.setLine(1, "|          Products You Might Like");
		suggestionsBox.setLine(2, "|       Name | Price | Bids | Ends on");
		for (int i = 3; i < 20; i++) {
			suggestionsBox.setLine(i, "|");
		}

		addScreenObject(productsBox, new Point(3, 6));
		addScreenObject(myAuctionsBox, new Point(3, 13));
		addScreenObject(suggestionsBox, new Point(34, 3));
	}

	public int run() {
		return 0;
	}
}