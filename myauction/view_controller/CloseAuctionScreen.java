package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import myauction.CLIObject;
import myauction.model.Product;
import myauction.Session;
import myauction.QueryLoader;

public class CloseAuctionScreen extends Screen {
    private int auctionId;
    private Product product;
    private CLIObject productInfoBox;
    private PreparedStatement secondHighestBidStatement;
    String finalAmount;

    public CloseAuctionScreen(Session session) {
        super(session);
        CLIObject headerBox = new CLIObject(WIDTH, 2);
        headerBox.setLine(0, "Previous (<)                Closing Auction                                 ");
        headerBox.setLine(1, "----------------------------------------------------------------------------");

        productInfoBox = new CLIObject(WIDTH, 5);
        productInfoBox.setLine(0, "Name: ");
        productInfoBox.setLine(1, "Final amount: ");
        productInfoBox.setLine(2, "Highest bidder: ");

        setSQLStatements();

        addScreenObject(productInfoBox, new Point(originX, originY));
        addScreenObject(productInfoBox, new Point(originX + 2, originY + 4));



    }

    public void reset() {
        auctionId = session.getSelectedAuctionId();
        product = new Product(session.getDb(), auctionId);
        productInfoBox.setLine(0, "Name: " + product.name);
        finalAmount = getFinalAmount();
        productInfoBox.setLine(1, "Final amount: " + finalAmount);
    }

    public int run() {
        reset();
        draw();
        getInput();
        return 0;
    }

    public void setSQLStatements() {
        try {
            if (secondHighestBidStatement == null) {
                secondHighestBidStatement = session.getDb().prepareStatement("select Second_Highest_Bid(?) as second_highest from dual");
            }
        } catch (SQLException e) {
            while (e != null) {
                debug.println(e.toString());
                debug.flush();
                e = e.getNextException();
            }
        }
    }

    public String getFinalAmount() {
        int numberOfBids = product.getBids();
        if (numberOfBids > 2) {
            finalAmount = product.getPrice();
        } else {
            finalAmount = getSecondHighestBid();
        }
        return finalAmount;
    }

    public String getSecondHighestBid() {
        int secondHighestBidasInt = 0;
        try {
            secondHighestBidStatement.setInt(1, auctionId);
            ResultSet results = secondHighestBidStatement.executeQuery();
            results.next();
            secondHighestBidasInt = results.getInt("second_highest");
        } catch (SQLException e) {
            while (e != null) {
                debug.println(e.toString());
                debug.flush();
                e = e.getNextException();
            }
        }
        String secondHighestBid = Integer.toString(secondHighestBidasInt);
        return secondHighestBid;
    }
     
}