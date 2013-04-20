package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import myauction.CLIObject;
import myauction.model.Product;
import myauction.Session;
import myauction.QueryLoader;
import myauction.helpers.validators.*;


public class CloseAuctionScreen extends Screen {
    private int auctionId;
    private Product product;
    private CLIObject productInfoBox;
    private CLIObject closeAuctionBox;
    private static PreparedStatement secondHighestBidStatement;
    private static CallableStatement sellProductStatement;
    private static CallableStatement withdrawAuctionStatement;
    private static SpecialCharDetector prevDetector = new SpecialCharDetector("<");
    private String finalAmount;
    private String highestBidder;
    private boolean sellProduct = false;

    public CloseAuctionScreen(Session session) {
        super(session);
        CLIObject headerBox = new CLIObject(WIDTH, 2);
        headerBox.setLine(0, "Previous (<)                Closing Auction                                 ");
        headerBox.setLine(1, "----------------------------------------------------------------------------");

        productInfoBox = new CLIObject(WIDTH, 3);
        productInfoBox.setLine(0, "Name: ");
        productInfoBox.setLine(1, "Final amount: ");
        productInfoBox.setLine(2, "Highest bidder: ");

        closeAuctionBox = new CLIObject(WIDTH, 3);
        closeAuctionBox.setLine(0, "Do you want to:");
        closeAuctionBox.setLine(1, "| | Sell Product (s)?");
        closeAuctionBox.setLine(2, "| | Withdraw auction (w)?");


        setSQLStatements();

        addScreenObject(headerBox, new Point(originX, originY));
        addScreenObject(productInfoBox, new Point(originX + 2, originY + 4));
        addScreenObject(closeAuctionBox, new Point(originX + 2, originY + 9));



    }

    public void reset() {
        sellProduct = false;
        auctionId = session.getSelectedAuctionId();
        product = new Product(session.getDb(), auctionId);
        productInfoBox.setLine(0, "Name: " + product.name);
        finalAmount = getFinalAmount();
        highestBidder = product.getHighestBidder();
        productInfoBox.setLine(1, "Final amount: " + finalAmount);
        productInfoBox.setLine(2, "Highest bidder: " + highestBidder);
    }

    public int run() {
        reset();
        draw();
        try{
            String option = getInput();
            prevDetector.validate(option);

            if (option.equals("s")){
                sellProduct = true;
            }
            closeAuction();
        } catch (SpecialCharException e) {
            if (e.getMessage().equals("<")) {
                return VIEW_CLOSED;
            }
        } catch (Exception e){
            debug.println(e.getMessage());
            debug.flush();

        }
        return CLOSE_AUCTION;
    }

    public void setSQLStatements() {
        try {
            if (secondHighestBidStatement == null) {
                secondHighestBidStatement = session.getDb().prepareStatement("select Second_Highest_Bid(?) as second_highest from dual");
            }
            if (withdrawAuctionStatement == null) {
                withdrawAuctionStatement = session.getDb().prepareCall("{call withdraw_product(?)}");
            }
            if (sellProductStatement == null) {
                sellProductStatement = session.getDb().prepareCall("{call sell_product(?)}");
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
        if (numberOfBids < 2) {
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
    public void closeAuction(){
        try {
            if (sellProduct){
                sellProductStatement.setInt(1, auctionId);
                sellProductStatement.execute();
                updateStatus("Product is sold!");
            }
            else{
                withdrawAuctionStatement.setInt(1, auctionId);
                withdrawAuctionStatement.execute();
                updateStatus("Product is withdrawn");
            }
        } catch(SQLException e){
                debug.println(e.toString());
                debug.flush();
                e = e.getNextException();
        }
    }
     
}