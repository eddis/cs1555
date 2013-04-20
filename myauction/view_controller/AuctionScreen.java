package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import myauction.CLIObject;
import myauction.Session;
import myauction.model.Product;
import java.text.SimpleDateFormat;

public class AuctionScreen extends Screen {
	private int auctionId;
	private Product product;
	private String currentAmount;
	private CLIObject auctionInfoBox;
	private CLIObject bidBox;
	private static PreparedStatement insertBidStatement;
	private static PreparedStatement curTimeStatement;
	private SimpleDateFormat dateFormat; 

	public AuctionScreen(Session session) {
		super(session);
		dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		CLIObject headerBox = new CLIObject(WIDTH, 2);
		headerBox.setLine(0, "Previous (<)                  Viewing Auction                               ");
		headerBox.setLine(1, "----------------------------------------------------------------------------");

		auctionInfoBox = new CLIObject(WIDTH, 9);
		auctionInfoBox.setLine(0, "Name: ");
		auctionInfoBox.setLine(1, "Description: ");
		auctionInfoBox.setLine(2, "Seller: ");
		auctionInfoBox.setLine(3, "Start Date: ");
		auctionInfoBox.setLine(4, "End Date: ");
		auctionInfoBox.setLine(5, "Min. Price: ");
		auctionInfoBox.setLine(6, "Current amount: ");
		auctionInfoBox.setLine(7, "Highest bidder: ");
		auctionInfoBox.setLine(8, "Number of Bids: ");

		bidBox = new CLIObject(WIDTH, 1);
		bidBox.setLine(0, "Enter amount to bid (must be greater than current amount): |__");


		addScreenObject(headerBox, new Point(originX, originY));
		addScreenObject(auctionInfoBox, new Point(originX + 2, originY + 4));
		addScreenObject(bidBox, new Point(originX + 2, originY + 14));

		setSQLStatements();
	}

	public void reset() {
		auctionId = session.getSelectedAuctionId();
		product = new Product(session.getDb(), auctionId);
		currentAmount = getCurrentAmount();
		auctionInfoBox.setLine(0, "Name:           " + product.name);
		auctionInfoBox.setLine(1, "Description:    " + product.description);
		auctionInfoBox.setLine(2, "Seller:         " + product.seller);
		auctionInfoBox.setLine(3, "Start Date:     " + product.startDate);
		auctionInfoBox.setLine(4, "End Date:       " + product.getEndDate());
		auctionInfoBox.setLine(5, "Min. Price:     " + product.minPrice);
		auctionInfoBox.setLine(6, "Current amount: " + currentAmount);
		auctionInfoBox.setLine(7, "Highest bidder: " + product.getHighestBidder());
		auctionInfoBox.setLine(8, "Number of bids: " + product.getBids());

	}

	public int run() {
		reset();
		draw();
		String userPrice = getInput();
		int userBid = Integer.parseInt(userPrice);
		int rowAdded = insertUserBid(userBid);
		if (rowAdded > 0) {
			updateStatus("You have bid " + userBid + "on "+ product.getDisplayName() + "sucessfully");
		}
		else {
			updateStatus("Bid not placed.");
		}

		return AUCTION;
	}

	public void setSQLStatements() {
		try {
			if (insertBidStatement == null) {
				insertBidStatement = session.getDb().prepareStatement("insert into bidlog values (bidlog_bidsn_sequence.nextval, ?, ?, ?, ?)");
			}
			if (curTimeStatement == null) {
				curTimeStatement = session.getDb().prepareStatement("select current_time as cur_time from system_time");
			}
		} catch (SQLException e) {
				debug.println(e.toString());
				debug.flush();
				e = e.getNextException();
		}
	}

	public void insertUserBid(int userBid) {
		try {
			insertBidStatement.setInt(1, auctionId);
			insertBidStatement.setString(2, session.getUsername());
			insertBidStatement.setDate(3, getCurrentTime());
			insertBidStatement.setInt(4, userBid);

			int rowAdded = insertBidStatement.executeUpdate();
		} catch (SQLException e) {
			debug.println(e.toString());
			debug.flush();
			e = e.getNextException();
		}

	}

	public Date getCurrentTime() {
		Date curTime = null;
		try {
			ResultSet results = curTimeStatement.executeQuery();
			results.next();
			curTime = results.getDate("cur_time");
		} catch (SQLException e){
			debug.println(e.toString());
			debug.flush();
			e = e.getNextException();
		} 
		return curTime;
	}

	public String getCurrentAmount() {
		if (product.getBids() == 0) {
			return "";
		} else {
			return product.getPrice();
		}
	}
}