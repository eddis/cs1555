package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import myauction.CLIObject;
import myauction.Session;
import myauction.QueryLoader;
import myauction.model.Product;
import myauction.helpers.Paginator;

public class SearchProductsScreen extends Screen {
	private CLIObject searchBox;
	private CLIObject productsBox;
	private ArrayList<Product> products;
	private int cursorAt;
	private String searchTerm1;
	private String searchTerm2;
	private int curPage;
	private Paginator<Product> paginator;
	private static PreparedStatement listBySearchStatement = null;

	public SearchProductsScreen(Session session) {
		super(session);

		CLIObject headerBox = new CLIObject(WIDTH, 2);
		headerBox.setLine(0, " Previous (<)          Searching Products by Description                     ");
		headerBox.setLine(1, "-----------------------------------------------------------------------------");

		searchBox = new CLIObject(WIDTH, 4);
		searchBox.setLine(0, "");
		searchBox.setLine(1, " Search for products containing: ______________ (s1) and ______________ (s2) ");
		searchBox.setLine(2, "");
		searchBox.setLine(3, "-----------------------------------------------------------------------------");

		productsBox = new CLIObject(WIDTH, 14);
		productsBox.setLine(0, "");
		productsBox.setLine(1, " No products to show.");
		for (int i = 2; i < 14; i++) {
			productsBox.setLine(i, "");
		}

		addScreenObject(headerBox, new Point(originX, originY));
		addScreenObject(searchBox, new Point(originX, originY + 2));
		addScreenObject(productsBox, new Point(originX, originY + 6));

		reset();
	}

	public void updateSearchBox() {
		String searchField1 = searchTerm1;
		String searchField2 = searchTerm2;

		switch (cursorAt) {
		case 0:
			searchField1 += "|";
			break;
		case 1:
			searchField2 += "|";
			break;
		default:

		}

		int field1Length = searchField1.length();
		int field2Length = searchField2.length();

		for (int i = 0; i < 14 - field1Length; i++) {
			searchField1 += "_";
		}

		for (int i = 0; i < 14 - field2Length; i++) {
			searchField2 += "_";
		}

		searchBox.setLine(1, " Search for products containing: " + searchField1 + " (s1) and " + searchField2 + " (s2) ");
	}


	public int run() {
        reset();

        boolean finished = false;
        while (!finished) {
        	updateSearchBox();
        	listProducts();
        	draw();

            String option = getInput();
            if (option.equals("<")) {
                finished = true;
            } else if (option.startsWith("p")) {
				option = option.substring(1, option.length());
				curPage = Integer.parseInt(option);
			} else if (option.equals("s1")) {
            	cursorAt = 0;
            	searchTerm1 = "";
            	updateSearchBox();

            	draw();

            	searchTerm1 = getInput();
            	if (searchTerm1.equals("<")) {
            		finished = true;
            	}
            } else if (option.equals("s2")) {
            	cursorAt = 1;
            	searchTerm2 = "";
            	updateSearchBox();

            	draw();

            	searchTerm2 = getInput();
            	if (searchTerm2.equals("<")) {
            		finished = true;
            	}
            } else {
            	if (cursorAt == 0) {
            		searchTerm1 = option;
            	} else if (cursorAt == 1) {
            		searchTerm2 = option;
            	}
            }
        }

        return CUSTOMER;
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

			if (listBySearchStatement == null) {
				listBySearchStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("myauction/queries/productSearch.sql"));
			}

			listBySearchStatement.setString(1, "%" + searchTerm1 + "%");
			listBySearchStatement.setString(2, "%" + searchTerm2 + "%");

			results = listBySearchStatement.executeQuery();

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
			productsBox.setLine(1, " No products to show.");
		} else {
			productsBox.setLine(0, " ID | Name | Desc. | Amount | Min. Price | Start Date | Num. Days | Seller");
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
											      + product.numberOfDays + " | "
											      + product.seller);
			}
			productsBox.setLine(13, paginator.getPageMenu(products, curPage, 5));
		}
	}

	public void reset() {
		searchTerm1 = "";
		searchTerm2 = "";
		cursorAt = 0;
		products = new ArrayList<Product>();
		curPage = 1;
		paginator = new Paginator<Product>();
	}
}