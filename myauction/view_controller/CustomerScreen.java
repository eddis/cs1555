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

	public int run() {
		int nextScreen = CUSTOMER;

		findClosedAuctions();
		findSuggestions();

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
		} finally {
			clear();
		}

		return nextScreen;
	}

	public void findClosedAuctions() {
		// TODO: looks for closed auctions and updates the status bar
	}

	public void findSuggestions() {
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