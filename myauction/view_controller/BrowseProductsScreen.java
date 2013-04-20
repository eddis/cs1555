package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import myauction.CLIObject;
import myauction.Session;
import myauction.QueryLoader;
import myauction.model.Product;
import myauction.helpers.Paginator;

public class BrowseProductsScreen extends Screen {
	private static PreparedStatement rootCategoriesStatement = null;
	private static PreparedStatement childCategoriesStatement = null;
	private static PreparedStatement parentCategoryStatement = null;
	private static PreparedStatement listByHighestStatement = null;
	private static PreparedStatement listByAlphabetStatement = null;
	private static PreparedStatement listByHighestAllStatement = null;
	private static PreparedStatement listByAlphabetAllStatement = null;

	private CLIObject sortByBox;
	private CLIObject categoriesBox;
	private CLIObject productsBox;
	private boolean sortByHighest;
	private String curCategory;
	private ArrayList<String> childCategories;
	private ArrayList<Product> products;
	private int curPage;
	private Paginator<Product> paginator;

	public BrowseProductsScreen(Session session) {
		super(session);

		CLIObject headerBox = new CLIObject(WIDTH, 2);
		headerBox.setLine(0, " Previous (<)                 Browsing Products                              ");
		headerBox.setLine(1, "-----------------------------------------------------------------------------");

		sortByBox = new CLIObject(WIDTH, 6);
		sortByBox.setLine(0, "                      ");
		sortByBox.setLine(1, "       Sort By        ");
		sortByBox.setLine(2, " | | Highest Bid (h)  ");
		sortByBox.setLine(3, " |X| Alphabetical (a) ");
		sortByBox.setLine(4, "                      ");
		sortByBox.setLine(5, "----------------------");

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
		categoriesBox.setLine(2, " * <None>                            Up To Parent (u) ");
		categoriesBox.setLine(3, "                                                      ");
		categoriesBox.setLine(4, "                                                      ");
		categoriesBox.setLine(5, "------------------------------------------------------");

		productsBox = new CLIObject(WIDTH, 12);
		productsBox.setLine(0, "");
		productsBox.setLine(1, " No products to show.");
		for (int i = 2; i < 12; i++) {
			productsBox.setLine(i, "");
		}

		addScreenObject(headerBox, new Point(originX, originY));
		addScreenObject(sortByBox, new Point(originX, originY + 2));
		addScreenObject(sortCategoryDivider, new Point(originX + 22, originY + 2));
		addScreenObject(categoriesBox, new Point(originX + 23, originY + 2));
		addScreenObject(productsBox, new Point(originX, originY + 8));

		reset();
	}

	public void updateCategories() {
		if (curCategory.equals("")) {
			categoriesBox.setLine(2, " * <None>                            Up To Parent (u) ");

			childCategories = getRootCategories();
		} else {
			String line = " * " + curCategory;
			for (int i = curCategory.length(); i < 34; i++) {
				line += " ";
			}
			line += "Up To Parent (u) ";
			categoriesBox.setLine(2, line);

			childCategories = getChildCategories(curCategory);
		}

		String[] childLines = new String[]{" ", " "};
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

			if (listByHighestAllStatement == null) {
				listByHighestAllStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("myauction/queries/listByHighestAll.sql"));
			}

			if (listByAlphabetAllStatement == null) {
				listByAlphabetAllStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("myauction/queries/listByAlphabetAll.sql"));
			}

			if (sortByHighest) {
				if (curCategory.equals("")) {
					results = listByHighestAllStatement.executeQuery();
				} else {
					listByHighestStatement.setString(1, curCategory);
					results = listByHighestStatement.executeQuery();
				}
			} else {
				if (curCategory.equals("")) {
					results = listByAlphabetAllStatement.executeQuery();
				} else {
					listByAlphabetStatement.setString(1, curCategory);
					results = listByAlphabetStatement.executeQuery();
				}
			}

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

		for (Product product : products) {
			debug.println(product.getDisplayName());
		}
		debug.flush();

		if (products.size() <= 0) {
			productsBox.setLine(0, "");
			productsBox.setLine(1, " No products to show.");
		} else {
			productsBox.setLine(0, " Name | Desc. | Amount | Min. Price | Start Date | Num. Days | Seller");
			productsBox.setLine(1, " --------------------------------------------------------------------");

			ArrayList<Product> productsOnPage = paginator.paginate(products, curPage, 4);
			for (int i = 0; i < productsOnPage.size(); i++) {
				Product product = productsOnPage.get(i);
				int lineOffset = i * 2;
				productsBox.setLine(lineOffset + 2, " " + product.getDisplayName() + " | "
											   	  + product.getBriefDescription() + " | $"
											      + product.amount + " | $"
											      + product.minPrice + " | "
											      + product.startDate + " | "
											      + product.numberOfDays + " | "
											      + product.seller + " ("
											      + product.auctionId + ")");
			}
			productsBox.setLine(11, paginator.getPageMenu(products, curPage, 4));
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
				debug.println(e.toString());
				debug.flush();
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

			childCategoriesStatement.setString(1, category);

			results = childCategoriesStatement.executeQuery();
			while (results.next()) {
				categories.add(results.getString("name"));
			}
		} catch (SQLException e) {
			while (e != null) {
				debug.println(e.toString());
				debug.flush();
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

			parentCategoryStatement.setString(1, category);

			results = parentCategoryStatement.executeQuery();
			results.next();
			parent = results.getString("parent_category");
			if (parent == null) {
				parent = "";
			}
		} catch (SQLException e) {
			while (e != null) {
				debug.println(e.toString());
				debug.flush();
				e = e.getNextException();
			}
		}

		return parent;
	}

	public void reset() {
		sortByHighest = false;
		curCategory = "";
		childCategories = null;
		products = new ArrayList<Product>();
		curPage = 1;
		paginator = new Paginator<Product>();
	}

	public int run() {
		int nextScreen = BROWSE_PRODUCTS;

		reset();

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
					curPage = 1;
				} else if (option.equals("a")) {
					sortByHighest = false;
					curPage = 1;
				} else if (option.equals("u")) {
					// find the parent category and redisplay hierarchy
					curCategory = getParentCategory(curCategory);
					curPage = 1;

				} else if (option.matches("c\\d+")) {
					// select the child category at the given index and redisplay hierarchy
					option = option.substring(1, option.length());
					int childCategory = Integer.parseInt(option);
					curCategory = childCategories.get(childCategory);
					curPage = 1;
				} else if (option.startsWith("p")) {
					option = option.substring(1, option.length());
					curPage = Integer.parseInt(option);
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
				debug.println(e.toString());
				updateStatus("YOU DUN GOOFED");
				nextScreen = BROWSE_PRODUCTS;
			}
		}

		return nextScreen;
	}
}