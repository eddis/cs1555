package myauction.model;

public class Product {
	public String auctionId;
	public String name;
	public String description;
	public String seller;
	public String startDate;
	public String numberOfDays;
	public String minPrice;
	public String status;
	public String buyer;
	public String sellDate;
	public String amount;

	public String numBids;

	public Product() {

	}

	public String getPrice() {
		if (amount.equals("")) {
			return "Min. Price: " + minPrice;
		}
		return amount;
	}

	public String getBids() {
		// query to get numBids
		return "";
	}

	public String getEndDate() {
		// query for computing end date
		return "";
	}
}