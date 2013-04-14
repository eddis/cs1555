package myauction.model;

import java.sql.*;

public class Product {
	private Connection db;

	public int auctionId;
	public String name;
	public String description;
	public String seller;
	public Date startDate;
	public int numberOfDays;
	public int minPrice;
	public String status;
	public String buyer;
	public Date sellDate;
	public int amount;

	private static PreparedStatement findProductStatement = null;
	private static PreparedStatement productBidsStatement = null;
	private static PreparedStatement calcEndDateStatement = null;

	public Product(Connection db, int id) {
		this.db = db;

		try {
			if (findProductStatement == null) {
				findProductStatement = db.prepareStatement("select * from product where auction_id = ?");
			}

			findProductStatement.setInt(1, id);

			ResultSet results = findProductStatement.executeQuery();
			// FIXME: handle case where there are no results
			results.next();

			auctionId = results.getInt("auction_id");
			name = results.getString("name");
			description = results.getString("description");
			seller = results.getString("seller");
			startDate = results.getDate("start_date");
			numberOfDays = results.getInt("number_of_days");
			minPrice = results.getInt("min_price");
			status = results.getString("status");
			buyer = results.getString("buyer");
			sellDate = results.getDate("sell_date");
			amount = results.getInt("amount");
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}
	}

	public String getDisplayName() {
		if (name.length() > 12) {
			return name.substring(0, 12);
		}
		return name;
	}

	public String getBriefDescription() {
		if (description.length() > 15) {
			return description.substring(0, 15);
		}
		return description;
	}

	public String getPrice() {
		if (amount == 0) {
			return "Min. Price: " + minPrice;
		}
		return "" + amount;
	}

	public int getBids() {
		int numBids = 0;

		// query to get numBids
		try {
			if (productBidsStatement == null) {
				productBidsStatement = db.prepareStatement("select count(*) as num_bids from product join bidlog on product.auction_id = bidlog.auction_id where product.auction_id = ?");
			}

			productBidsStatement.setInt(1, auctionId);

			ResultSet results = productBidsStatement.executeQuery();
			results.next();
			numBids = results.getInt("num_bids");
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}

		return numBids;
	}

	public Date getEndDate() {
		Date endDate = null;

		// query for computing end date
		try {
			if (calcEndDateStatement == null) {
				calcEndDateStatement = db.prepareStatement("select start_date + number_of_days as end_date from product where auction_id = ?");
			}

			calcEndDateStatement.setInt(1, auctionId);

			ResultSet results = calcEndDateStatement.executeQuery();
			results.next();
			endDate = results.getDate("end_date");
		} catch (SQLException e) {
			while (e != null) {
				System.out.println(e.toString());
				e = e.getNextException();
			}
		}

		return endDate;
	}

	public String getHighestBidder() {
		String highestBidder = null;
		if (status.equals('sold')) {
			return buyer;
		}
		try {
			highestBidderStatement = db.prepareStatement(QueryLoader.loadQuery("myauction/queries/highestBidder.sql"));
			highestBidderStatement.setInt(1, auctionId);
			ResultSet results = highestBidderStatement.executeQuery();
			results.next();
			highestBidder = results.getString("highest_bidder");

		} catch (SQLException e) {
			while (e != null ) {
				debug.println(e.toString());
				debug.flush();
				e = e.getNextException();
			}
		}
		if (highestBidder == null) {
			highestBidder = "";
		}
		return highestBidder;
	}
}