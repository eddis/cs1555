package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import myauction.CLIObject;
import myauction.Session;
import myauction.QueryLoader;
import myauction.model.Product;

public class BrowseProductsScreen extends Screen {
	private CLIObject sortByBox;
	private CLIObject categoriesBox;
	private CLIObject productsBox;
	private boolean sortByHighest;
	private String curCategory;
	private ArrayList<String> childCategories;
	private ArrayList<Product> products;
	private static PreparedStatement rootCategoriesStatement = null;
	private static PreparedStatement childCategoriesStatement = null;
	private static PreparedStatement parentCategoryStatement = null;
	private static PreparedStatement listByHighestStatement = null;
	private static PreparedStatement listByAlphabetStatement = null;

	public BrowseProductsScreen(Session session) {
		super(session);

		CLIObject headerBox = new CLIObject(WIDTH, 2);
		headerBox.setLine(0, " Previous (<)                   Browsing Products                           ");
		headerBox.setLine(1, " ---------------------------------------------------------------------------");

		sortByBox = new CLIObject(WIDTH, 6);
		sortByBox.setLine(0, "                      ");
		sortByBox.setLine(1, "       Sort By        ");
		sortByBox.setLine(2, " | | Highest Bid (h)  ");
		sortByBox.setLine(3, " |X| Alphabetical (a) ");
		sortByBox.setLine(4, "                      ");
		sortByBox.setLine(5, " ---------------------");

		CLIObject sortCategoryDivider = new CLIObject(WIDTH, 6);
		sortCategoryDivider.setLine(0, "|");
		sortCategoryDivider.setLine(1, "|");
		sortCategoryDivider.setLine(2, "|");
		sortCategoryDivider.setLine(3, "|");
		sortCategoryDivider.setLine(4, "|");
		sortCategoryDivider.setLine(5, "-");

		categoriesBox = new CLIObject(WIDTH, 6);
		categoriesBox.setLine(0, "                                                      ");
		categoriesBox.setLine(1, "                         Category                     ");
		categoriesBox.setLine(2, " * <None>                                      Up (u) ");
		categoriesBox.setLine(3, "                                                      ");
		categoriesBox.setLine(4, "                                                      ");
		categoriesBox.setLine(5, "----------------------------------------------------- ");

		productsBox = new CLIObject(WIDTH, 16);
		productsBox.setLine(0, "");
		productsBox.setLine(1, "No products to show.");
		for (int i = 2; i < 16; i++) {
			productsBox.setLine(i, "                                                                              ");
		}

		addScreenObject(headerBox, new Point(originX, originY));
		addScreenObject(sortByBox, new Point(originX, originY + 2));
		addScreenObject(sortCategoryDivider, new Point(originX + 22, originY + 2));
		addScreenObject(categoriesBox, new Point(originX + 23, originY + 2));
		addScreenObject(productsBox, new Point(originX, originY + 7));

		sortByHighest = false;
		curCategory = "";
		childCategories = null;
		products = new ArrayList<Product>();
	}

	public void updateCategories() {
		if (curCategory.equals("")) {
			categoriesBox.setLine(2, " * <None>                                      Up (u) ");

			childCategories = getRootCategories();
		} else {
			String line = " * " + curCategory;
			for (int i = curCategory.length(); i < 38; i++) {
				line += " ";
			}
			line += "Up (u) ";
			categoriesBox.setLine(2, line);

			childCategories = getChildCategories(curCategory);
		}

		String[] childLines = new String[]{"", ""};
		int curLine = 0;
		for (int i = 0; i < childCategories.size(); i++) {
			if (childLines[curLine].length() + childCategories.get(i).length() > 53) {
				curLine++;
			}
			childLines[curLine] += childCategories.get(i) + " (c" + i + ") ";
		}

		categoriesBox.setLine(3, childLines[0]);
		categoriesBox.setLine(4, childLines[1]);
	}

	public void updateSortBy() {
		if (sortByHighest) {
			sortByBox.setLine(2, " |X| Highest Bid (h)  ");
			sortByBox.setLine(3, " | | Alphabetical (a) ");
		} else {
			sortByBox.setLine(2, " | | Highest Bid (h)  ");
			sortByBox.setLine(3, " |X| Alphabetical (a) ");
		}
	}

	public void listProducts() {
		products = new ArrayList<Product>();

		try {
			ResultSet results;

			if (listByHighestStatement == null) {
				listByHighestStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("myauction/queries/listByHighest.sql"));
			}

			if (listByAlphabetStatement == null) {
				listByAlphabetStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("myauction/queries/listByAlphabet.sql"));
			}

			if (sortByHighest) {
				results = listByHighestStatement.executeQuery();
			} else {
				results = listByAlphabetStatement.executeQuery();
			}

			while (results.next()) {
				int auctionId = results.getInt("auction_id");
				Product product = new Product(session.getDb(), auctionId);
				products.add(product);
			}
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}

		// clear product lines
		for (int i = 2; i < 16; i++) {
			productsBox.setLine(i, "");
		}

		if (products.size() <= 0) {
			productsBox.setLine(1, "No products to show.");
		} else {
			for (int i = 0; i < products.size(); i++) {
				Product product = products.get(i);
				int lineOffset = i % 2 + i;
				productsBox.setLine(lineOffset + 2, product.getDisplayName() + " | "
											   	  + product.getBriefDescription() + " | $"
											      + product.amount + " | $"
											      + product.minPrice + " | "
											      + product.startDate + " | "
											      + product.numberOfDays + " | "
											      + product.seller + " ("
											      + product.auctionId + ")");
			}
		}

	}

	public ArrayList<String> getRootCategories() {
		ArrayList<String> roots = new ArrayList<String>();

		try {
			ResultSet results;

			if (rootCategoriesStatement == null) {
				rootCategoriesStatement = session.getDb().prepareStatement("select name from category where parent_category is null");
			}

			results = rootCategoriesStatement.executeQuery();
			while (results.next()) {
				roots.add(results.getString("name"));
			}
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}

		return roots;
	}

	public ArrayList<String> getChildCategories(String category) {
		ArrayList<String> categories = new ArrayList<String>();

		try {
			ResultSet results;

			if (childCategoriesStatement == null) {
				childCategoriesStatement = session.getDb().prepareStatement("select name from category where parent_category = ?");
			}

			results = childCategoriesStatement.executeQuery();
			while (results.next()) {
				categories.add(results.getString("name"));
			}
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}

		return categories;
	}

	public String getParentCategory(String category) {
		String parent = "";
		if (category == null || category.equals("")) {
			return parent;
		}

		try {
			ResultSet results;

			if (parentCategoryStatement == null) {
				parentCategoryStatement = session.getDb().prepareStatement("select parent_category from category where name = ?");
			}

			results = parentCategoryStatement.executeQuery();
			results.next();
			parent = results.getString("parent_category");
			if (parent == null) {
				parent = "";
			}
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}

		return parent;
	}

	public int run() {
		int nextScreen = BROWSE_PRODUCTS;

		boolean finishedHere = false;
		while (!finishedHere) {
			updateCategories();
			updateSortBy();
			listProducts();

			draw();

			String option;
			try {
				option = getInput();

				if (option.equals("h")) {
					sortByHighest = true;
				} else if (option.equals("a")) {
					sortByHighest = false;
				} else if (option.equals("u")) {
					// find the parent category and redisplay hierarchy
					curCategory = getParentCategory(curCategory);
				} else if (option.startsWith("c")) {
					// select the child category at the given index and redisplay hierarchy
					option = option.substring(1, option.length()-1);
					int childCategory = Integer.parseInt(option);
					curCategory = childCategories.get(childCategory);
				} else if (option.startsWith("<")) {
					// go back to the customer screen
					nextScreen = CUSTOMER;
					finishedHere = true;
				} else {
					int auctionId = Integer.parseInt(option);
					session.setSelectedAuctionId(auctionId);

					nextScreen = AUCTION;
					finishedHere = true;
				}
			} catch (Exception e) {
				updateStatus("YOU DUN GOOFED");
				nextScreen = BROWSE_PRODUCTS;
			} finally {
				clear();
			}
		}

		return nextScreen;
	}
}