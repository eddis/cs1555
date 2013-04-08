import java.awt.Point;
import java.util.ArrayList;

public class CustomerScreen extends Screen {
	private CLIObject productsBox;
	private CLIObject myAuctionsBox;
	private CLIObject suggestionsBox;
	private ArrayList<Product> suggestedProducts;

	public CustomerScreen() {
		super();

		productsBox = new CLIObject(WIDTH, 6);
		productsBox.setLine(0, "---Products-------------");
		productsBox.setLine(1, "|                      |");
		productsBox.setLine(2, "| Browse Products (1)  |");
		productsBox.setLine(3, "| Search Products (2)  |");
		productsBox.setLine(4, "|                      |");
		productsBox.setLine(5, "------------------------");

		myAuctionsBox = new CLIObject(WIDTH, 7);
		myAuctionsBox.setLine(0, "---My Auctions---------------");
		myAuctionsBox.setLine(1, "|                           |");
		myAuctionsBox.setLine(2, "| Start Auction (3)         |");
		myAuctionsBox.setLine(3, "| View Ongoing Auctions (4) |");
		myAuctionsBox.setLine(4, "| View Closed Auctions (5)  |");
		myAuctionsBox.setLine(5, "|                           |");
		myAuctionsBox.setLine(6, "-----------------------------");

		suggestionsBox = new CLIObject(WIDTH, 22);
		suggestionsBox.setLine(0, "|");
		suggestionsBox.setLine(1, "|          Products You Might Like");
		for (int i = 2; i < 22; i++) {
			suggestionsBox.setLine(i, "|");
		}

		addScreenObject(productsBox, new Point(3, 6));
		addScreenObject(myAuctionsBox, new Point(3, 13));
		addScreenObject(suggestionsBox, new Point(34, 1));

		suggestedProducts = new ArrayList<Product>();
	}

	public void addSuggestedProduct(Product product) {
		suggestedProducts.add(product);

		int line = (suggestedProducts.size() - 1) % 2 + suggestedProducts.size() - 1 + 3;
		suggestionsBox.setLine(line, "| " + product.name + " | "
										  + product.getPrice() + " | " 
										  + product.getBids() + " | Ends on "
										  + product.getEndDate());
	}

	public int run() {
		int nextScreen = CUSTOMER;

		draw();

		int option;
		try {
			option = Integer.parseInt(getInput());

			switch (option) {
			case 1:
				nextScreen = BROWSE_PRODUCTS;
			case 2:
				nextScreen = SEARCH_PRODUCTS;
			case 3:
				nextScreen = START_AUCTION;
			case 4:
				nextScreen = VIEW_ONGOING;
			case 5:
				nextScreen = VIEW_CLOSED;
			}
		} catch (Exception e) {
			nextScreen = CUSTOMER;
		} finally {
			clear();
		}

		return nextScreen;
	}
}