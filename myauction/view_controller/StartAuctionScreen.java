package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import myauction.CLIObject;
import myauction.Session;

public class StartAuctionScreen extends Screen {
	public StartAuctionScreen(Session session) {
		super(session);

		headerBox = new CLIObject(WIDTH, 2);
		headerBox.setLine(0, "Previous (<)                  Starting New Auction                          ");
		headerBox.setLine(1, "----------------------------------------------------------------------------");
put_product(seller in varchar2, name in varchar2, description in varchar2, category in varchar2, min_price in int, num_days in int)

		createUserBox = new CLIObject(WIDTH, 10);
		createUserBox.setLine(0, "---New Auction Info----------------------------------");
		createUserBox.setLine(1, "|                                                   |");
		createUserBox.setLine(3, "| Name:           ____________________              |");
		createUserBox.setLine(4, "| Description:    ________________________________  |");
		createUserBox.setLine(5, "| Minimum Price:  ____________________              |");
		createUserBox.setLine(6, "| Number of Days: ____________________              |");
		createUserBox.setLine(8, "|                                                   |");
		createUserBox.setLine(9, "-----------------------------------------------------");
	}

	public int run() {
		int nextScreen = BROWSE_PRODUCTS;

		reset();

		boolean finishedHere = false;
		while (!finishedHere) {
			updateCategories();

			draw();

			String option;
			try {
				option = getInput();

				if (option.equals("u")) {
					// find the parent category and redisplay hierarchy
					curCategory = getParentCategory(curCategory);
				} else if (option.startsWith("c")) {
					// select the child category at the given index and redisplay hierarchy
					option = option.substring(1, option.length());
					int childCategory = Integer.parseInt(option);
					curCategory = childCategories.get(childCategory);
				} else if (option.startsWith("<")) {
					// go back to the customer screen
					nextScreen = CUSTOMER;
					finishedHere = true;
				} 
			} catch (Exception e) {
				debug.println(e.toString());
				updateStatus("YOU DUN GOOFED");
				nextScreen = BROWSE_PRODUCTS;
			}
		}

		return nextScreen;
	}
}