package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import myauction.CLIObject;
import myauction.model.Product;
import myauction.Session;
import myauction.QueryLoader;

public class CustomerScreen extends Screen {
	private CLIObject productsBox;
	private CLIObject myAuctionsBox;
	private CLIObject suggestionsBox;
	private ArrayList<Product> suggestedProducts;
	private static PreparedStatement findClosedStatement = null;
	private static PreparedStatement listSuggestedStatement = null;

	public CustomerScreen(Session session) {
		super(session);

		productsBox = new CLIObject(WIDTH, 6);
		productsBox.setLine(0, "---Products-------------");
		productsBox.setLine(1, "|                      |");
		productsBox.setLine(2, "| Browse Products (b)  |");
		productsBox.setLine(3, "| Search Products (s)  |");
		productsBox.setLine(4, "|                      |");
		productsBox.setLine(5, "------------------------");

		myAuctionsBox = new CLIObject(WIDTH, 7);
		myAuctionsBox.setLine(0, "---My Auctions---------------");
		myAuctionsBox.setLine(1, "|                           |");
		myAuctionsBox.setLine(2, "| Start Auction (n)         |");
		myAuctionsBox.setLine(3, "| View Ongoing Auctions (o) |");
		myAuctionsBox.setLine(4, "| View Closed Auctions (c)  |");
		myAuctionsBox.setLine(5, "|                           |");
		myAuctionsBox.setLine(6, "-----------------------------");

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

		suggestedProducts = new ArrayList<Product>();
	}

	public void addSuggestedProduct(Product product) {
		suggestedProducts.add(product);

		int line = (suggestedProducts.size() - 1) % 2 + suggestedProducts.size() - 1 + 4;
		suggestionsBox.setLine(line, "| " + product.getDisplayName() + " | $"
										  + product.getPrice() + " | " 
										  + product.getBids() + " | "
										  + product.getEndDate() + " ("
										  + product.auctionId + ")");
	}

	public void reset() {
		suggestedProducts = new ArrayList<Product>();
	}

	public int run() {
		int nextScreen = CUSTOMER;

		reset();

		findClosed();
		listSuggestions();

		draw();

		String option;
		try {
			option = getInput();

			if (option.equals("b")) {
				nextScreen = BROWSE_PRODUCTS;
			} else if (option.equals("s")) {
				nextScreen = SEARCH_PRODUCTS;
			} else if (option.equals("n")) {
				nextScreen = START_AUCTION;
			} else if (option.equals("o")) {
				nextScreen = VIEW_ONGOING;
			} else if (option.equals("c")) {
				nextScreen = VIEW_CLOSED;
			} else {
				int suggestedProductAuctionId = Integer.parseInt(option);
				session.setSelectedAuctionId(suggestedProductAuctionId);

				nextScreen = AUCTION;
			}
		} catch (Exception e) {
			nextScreen = CUSTOMER;
		}

		return nextScreen;
	}

	public void findClosed() {
		// looks for closed auctions and updates the status bar
		try {
			if (findClosedStatement == null) {
				findClosedStatement = session.getDb().prepareStatement("select count(*) as num_closed from customer join product on customer.login = product.seller where customer.login = ? and product.status = 'close'");
			}

			findClosedStatement.setString(1, session.getUsername());

			ResultSet results = findClosedStatement.executeQuery();
			// FIXME: account for empty
			results.next();
			int closedAuctionCount = results.getInt("num_closed");
			updateStatus("You have " + closedAuctionCount + " closed auction(s) that need tending to!");
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}
	}

	public void listSuggestions() {
		try {
			if (listSuggestedStatement == null) {
				listSuggestedStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("myauction/queries/listSuggested.sql"));
			}

			listSuggestedStatement.setString(1, session.getUsername());
			listSuggestedStatement.setString(2, session.getUsername());

			ResultSet results = listSuggestedStatement.executeQuery();
			while (results.next()) {
				int suggestedAuctionId = results.getInt("suggested_auction");
				Product suggestedProduct = new Product(session.getDb(), suggestedAuctionId);
				addSuggestedProduct(suggestedProduct);
			}
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}
	}
}