package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import myauction.CLIObject;
import myauction.Session;
import myauction.QueryLoader;
import myauction.model.Product;
import myauction.helpers.Paginator;
import myauction.helpers.validators.*;

public class ViewOngoingScreen extends Screen {
	private CLIObject selectionBox;
	private CLIObject productsBox;
	private ArrayList<Product> products;
	private int curPage;
	private Paginator<Product> paginator;
	private static PreparedStatement listOngoingStatement = null;

	private static SpecialCharDetector prevDetector = new SpecialCharDetector("<");
	private static IntegerValidator auctionValidator = new IntegerValidator("auction", 0, Integer.MAX_VALUE);

	public ViewOngoingScreen(Session session) {
		super(session);

		CLIObject headerBox = new CLIObject(WIDTH, 2);
		headerBox.setLine(0, " Previous (<)                Viewing Ongoing Auctions                        ");
		headerBox.setLine(1, "-----------------------------------------------------------------------------");

		selectionBox = new CLIObject(WIDTH, 3);
		selectionBox.setLine(0, "");
		selectionBox.setLine(1, "");
		selectionBox.setLine(2, "-----------------------------------------------------------------------------");

		productsBox = new CLIObject(WIDTH, 14);
		productsBox.setLine(0, "");
		productsBox.setLine(1, " No auctions to show.");
		for (int i = 2; i < 14; i++) {
			productsBox.setLine(i, "");
		}

		addScreenObject(headerBox, new Point(originX, originY));
		addScreenObject(selectionBox, new Point(originX, originY + 2));
		addScreenObject(productsBox, new Point(originX, originY + 5));

		reset();
	}

	public int run() {
		int nextScreen = VIEW_ONGOING;

        reset();

        boolean finished = false;
	    while (!finished) {
	        try {
	        	listProducts();
	        	draw();

	        	String option = getInput();
	        	prevDetector.validate(option);

	        	if (option.matches("p\\d+")) {
					option = option.substring(1, option.length());
					curPage = Integer.parseInt(option);
	        	}
			} catch (SpecialCharException e) {
				if (e.getMessage().equals("<")) {
					nextScreen = CUSTOMER;
					finished = true;
				}
			} catch (ValidationException e) {
				updateStatus(e.getMessage());
			} catch (Exception e) {
				debug.println(e.toString());
			}
        }

        return nextScreen;
    }

    public void clearProductsBox() {
    	for (int i = 0; i < 14; i++) {
    		productsBox.setLine(i, "");
    	}
    }

	public void listProducts() {
		products = new ArrayList<Product>();
		clearProductsBox();

		try {
			ResultSet results;

			if (listOngoingStatement == null) {
				listOngoingStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("myauction/queries/listOngoing.sql"));
			}

			listOngoingStatement.setString(1, session.getUsername());

			results = listOngoingStatement.executeQuery();

			while (results.next()) {
				int auctionId = results.getInt("auction_id");
				Product product = new Product(session.getDb(), auctionId);
				products.add(product);
			}
		} catch (SQLException e) {
			while (e != null) {
				debug.println(e.toString());
				debug.flush();
				e = e.getNextException();
			}
		}

		if (products.size() <= 0) {
			productsBox.setLine(1, " No auctions to show.");
		} else {
			productsBox.setLine(0, " ID | Name | Desc. | Amount | Min. Price | Start Date | Num. Days");
			productsBox.setLine(1, " -------------------------------------------------------------------------");

			ArrayList<Product> productsOnPage = paginator.paginate(products, curPage, 5);
			for (int i = 0; i < productsOnPage.size(); i++) {
				Product product = productsOnPage.get(i);
				int lineOffset = i * 2;
				productsBox.setLine(lineOffset + 2, " " + product.auctionId + " | "
												  + product.getDisplayName() + " | "
											   	  + product.getBriefDescription() + " | $"
											      + product.amount + " | $"
											      + product.minPrice + " | "
											      + product.startDate + " | "
											      + product.numberOfDays);
			}
			productsBox.setLine(13, paginator.getPageMenu(products, curPage, 5));
		}
	}

	public void reset() {
		products = new ArrayList<Product>();
		curPage = 1;
		paginator = new Paginator<Product>();
	}
}