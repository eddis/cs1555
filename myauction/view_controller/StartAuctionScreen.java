package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import myauction.CLIObject;
import myauction.Session;
import myauction.helpers.validators.*;

public class StartAuctionScreen extends Screen {
	private static PreparedStatement rootCategoriesStatement = null;
	private static PreparedStatement childCategoriesStatement = null;
	private static PreparedStatement parentCategoryStatement = null;
	private static CallableStatement startAuctionStatement = null;

	private static SpecialCharDetector prevDetector = new SpecialCharDetector("<");
	private static LengthValidator nameValidator = new LengthValidator("name", 1, 10);
	private static LengthValidator descriptionValidator = new LengthValidator("description", 1, 30);
	private static IntegerValidator minPriceValidator = new IntegerValidator("minPrice", 0, Integer.MAX_VALUE);
	private static IntegerValidator numDaysValidator = new IntegerValidator("numDays", 1, 31);
	private static LengthValidator categoryValidator = new LengthValidator("category", 1, 20);

	private static final int ENTER_NAME = 0;
	private static final int ENTER_DESC = 1;
	private static final int ENTER_MIN = 2;
	private static final int ENTER_NUM_DAYS = 3;
	private static final int ENTER_CATEGORY = 4;
	private static final int COMPLETED = 5;

	private CLIObject newAuctionBox;
	private CLIObject categoriesBox;

	private int state;
	private String curCategory;
	private ArrayList<String> childCategories;
	private String name;
	private String description;
	private int minPrice;
	private String minPriceStr;
	private int numDays;
	private String numDaysStr;
	private String category;

	public StartAuctionScreen(Session session) {
		super(session);

		CLIObject headerBox = new CLIObject(WIDTH, 2);
		headerBox.setLine(0, "Previous (<)                  Starting New Auction                          ");
		headerBox.setLine(1, "----------------------------------------------------------------------------");

		newAuctionBox = new CLIObject(WIDTH, 9);
		newAuctionBox.setLine(0, "---New Auction----------------------------------------");
		newAuctionBox.setLine(1, "|                                                    |");
		newAuctionBox.setLine(2, "| Name:           |________________________________  |");
		newAuctionBox.setLine(3, "| Description:    _________________________________  |");
		newAuctionBox.setLine(4, "| Minimum Price:  _________________________________  |");
		newAuctionBox.setLine(5, "| Number of Days: _________________________________  |");
		newAuctionBox.setLine(6, "| Category:       _________________________________  |");
		newAuctionBox.setLine(7, "|                                                    |");
		newAuctionBox.setLine(8, "------------------------------------------------------");

		categoriesBox = new CLIObject(WIDTH, 6);
		categoriesBox.setLine(0, "                                                      ");
		categoriesBox.setLine(1, "                         Category                     ");
		categoriesBox.setLine(2, " * <None>                            Up To Parent (u) ");
		categoriesBox.setLine(3, "                                                      ");
		categoriesBox.setLine(4, "                                                      ");
		categoriesBox.setLine(5, "------------------------------------------------------");

		addScreenObject(headerBox, new Point(originX, originY));
		addScreenObject(newAuctionBox, new Point(originX + 1, originY + headerBox.getHeight() + 1));
		addScreenObject(categoriesBox, new Point(originX + 1, originY + newAuctionBox.getHeight() + 3));

		reset();
	}

	public void reset() {
		curCategory = "";
		childCategories = null;
		name = "";
		description = "";
		minPrice = 0;
		numDays = 0;
		minPriceStr = "";
		numDaysStr = "";
		category = "";
		state = ENTER_NAME;
	}

	public int run() {
		reset();

		while (state != COMPLETED) {
			updateNewAuction();
			updateCategories();
			draw();

			String option;
			try {
				option = getInput();
				prevDetector.validate(option);

				switch (state) {
				case ENTER_NAME:
					name = nameValidator.validate(option);
					state = ENTER_DESC;
					break;
				case ENTER_DESC:
					description = descriptionValidator.validate(option);
					state = ENTER_MIN;
					break;
				case ENTER_MIN:
					minPrice = minPriceValidator.validate(option);
					minPriceStr = "" + minPrice;
					state = ENTER_NUM_DAYS;
					break;
				case ENTER_NUM_DAYS:
					numDays = numDaysValidator.validate(option);
					numDaysStr = "" + numDays;
					state = ENTER_CATEGORY;
					break;
				case ENTER_CATEGORY:
					if (option.equals("u")) {
						// find the parent category and redisplay hierarchy
						curCategory = getParentCategory(curCategory);
						break;
					} else if (option.matches("c\\d+")) {
						// select the child category at the given index and redisplay hierarchy
						option = option.substring(1, option.length());
						int childCategory = Integer.parseInt(option);
						curCategory = childCategories.get(childCategory);
						break;
					}

					category = categoryValidator.validate(option);

					if (startAuctionStatement == null) {
						startAuctionStatement = session.getDb().prepareCall("{call put_product(?,?,?,?,?,?)}");
					}
					startAuctionStatement.setString(1, session.getUsername());
					startAuctionStatement.setString(2, name);
					startAuctionStatement.setString(3, description);
					startAuctionStatement.setString(4, category);
					startAuctionStatement.setInt(5, minPrice);
					startAuctionStatement.setInt(6, numDays);

					startAuctionStatement.execute();

					updateStatus("New auction created!");
					reset();
					break;
				default:
					state = COMPLETED;
				}
			} catch (SpecialCharException e) {
				if (e.getMessage().equals("<")) {
					return CUSTOMER;
				}
			} catch (ValidationException e) {
				updateStatus(e.getMessage());

				if (e.getFlag().equals("name")) {
					state = ENTER_NAME;
				} else if (e.getFlag().equals("description")) {
					state = ENTER_DESC;
				} else if (e.getFlag().equals("minPrice")) {
					state = ENTER_MIN;
				} else if (e.getFlag().equals("numDays")) {
					state = ENTER_NUM_DAYS;
				} else if (e.getFlag().equals("category")) {
					state = ENTER_CATEGORY;
				}
			} catch (Exception e) {
				debug.println(e.toString());
				updateStatus("Failed to create auction!");
				reset();
			}
		}

		return CUSTOMER;
	}

	private String padField(String prefix, int field, String fieldValue) {
		String line = prefix + fieldValue;

		if (field == state) {
			line += "|";
			for (int i = 0; i < 33 - fieldValue.length(); i++) {
				line += "_";
			}
		} else {
			for (int i = 0; i < 34 - fieldValue.length(); i++) {
				line += "_";
			}
		}

		line += " |";

		return line;
	}

	public void updateNewAuction() {
		newAuctionBox.setLine(2, padField("| Name:           ", ENTER_NAME, name));
		newAuctionBox.setLine(3, padField("| Description:    ", ENTER_DESC, description));
		newAuctionBox.setLine(4, padField("| Minimum Price:  ", ENTER_MIN, minPriceStr));
		newAuctionBox.setLine(5, padField("| Number of Days: ", ENTER_NUM_DAYS, numDaysStr));
		newAuctionBox.setLine(6, padField("| Category:       ", ENTER_CATEGORY, category));
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
}