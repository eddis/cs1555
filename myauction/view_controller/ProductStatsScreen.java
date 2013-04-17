package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;

import myauction.CLIObject;
import myauction.Session;
import myauction.QueryLoader;
import myauction.model.Product;
import myauction.helpers.Paginator;

public class ProductStatsScreen extends Screen {
    private CLIObject headerBox;
    private CLIObject listBox;
    private CLIObject productsBox;
    private boolean listAllProducts;
    private String customerLogin;
    private int curPage;
    private Paginator<Product> paginator;
    private ArrayList<Product> products;
    private PreparedStatement listAllProductsStatement = null;
    private PreparedStatement listByCustomerStatement = null;
    private PreparedStatement checkCustomerStatement = null;

    public ProductStatsScreen(Session session) {
        super(session);
        headerBox = new CLIObject(WIDTH, 2);
        headerBox.setLine(0, " Previous (<)                 Product Statistics                            ");
        headerBox.setLine(1, " ---------------------------------------------------------------------------");

        listBox = new CLIObject(WIDTH, 5);
        listBox.setLine(0, "                                                                           ");
        listBox.setLine(1, " |X| List all products (a)");
        listBox.setLine(2, " | | List products by customer login (c)         customer: __________");
        listBox.setLine(3, "                                                                           ");
        listBox.setLine(4, " --------------------------------------------------------------------------");

        productsBox = new CLIObject(WIDTH, 13);
        productsBox.setLine(0, "");
        productsBox.setLine(1, " No products to show.");
        for (int i = 2; i < 13; i++) {
            productsBox.setLine(i, "");
        }


        listAllProducts = true;
        customerLogin = "";
        curPage = 1;

        setPreparedStatements();

        addScreenObject(headerBox, new Point(originX, originY));
        addScreenObject(listBox, new Point(originX, originY + 2));
        addScreenObject(productsBox, new Point(originX, originY + 7));


    }

    public void setPreparedStatements() {
        try {
            if (listAllProductsStatement == null) { 
                listAllProductsStatement = session.getDb().prepareStatement("select auction_id from product");
            }
            if (listByCustomerStatement == null) {
                listByCustomerStatement = session.getDb().prepareStatement("select auction_id from product where seller = ?");
            }
            if (checkCustomerStatement == null){
                checkCustomerStatement = session.getDb().prepareStatement("select count(*) as num_customers from customer where login = ?");
            }
        } catch (SQLException e){
            while (e != null) {
                debug.println(e.toString());
                debug.flush();
                e = e.getNextException();
            }
        }
    }
    public void reset(){
        listAllProducts = true;
        customerLogin = "";
        products = new ArrayList<Product>();
        curPage = 1;
        paginator = new Paginator<Product>();
        listProducts();
    }

    public int run() {
        reset();
        draw();
        boolean finished = false;
        while(!finished){
            String option = getInput();
            if (option.equals("<")) {
                finished = true;
            }
            if (option.equals("c")) {
                listAllProducts = false;
                listBox.setLine(1, " | | List all products (a)");
                listBox.setLine(2, " |X| List products by customer login (c)         customer: |_________");
                draw();
                String customer = getInput();
                if (customer.equals("<")) {
                    finished = true;
                }
                while (!checkCustomer(customer)) {
                    updateStatus(customer + " is not a valid customer login, please enter another. ");
                    draw();
                    customer = getInput();
                    if (customer.equals("<")) {
                        finished = true;
                    }
                }
                updateStatus("");
                listBox.setLine(2, " |X| List products by customer login (c)         customer: " + customer);
                customerLogin = customer;
                curPage = 1;
            } else if (option.equals("a")) {
                listBox.setLine(1, " |X| List all products (a)");
                listBox.setLine(2, " | | List products by customer login (c)         customer: __________");
                listAllProducts = true;
                curPage = 1;
            }  else if (option.startsWith("p")) {
                option = option.substring(1, option.length());
                curPage = Integer.parseInt(option);
            }
            listProducts();
            draw();
        }
        return ADMIN;
    }

    public boolean checkCustomer(String customer) {
        try {
            ResultSet results;
            checkCustomerStatement.setString(1, customer);
            results = checkCustomerStatement.executeQuery();
            results.next();
            if (results.getInt("num_customers") > 0){
                return true;
            }
        } catch (SQLException e) {
            while (e != null) {
                debug.println(e.toString());
                debug.flush();
                e = e.getNextException();
            }
        }
        return false;
    }


    public void listProducts() {
        products = new ArrayList<Product>();

        try {
            ResultSet results;

            if (listAllProducts) {
               results = listAllProductsStatement.executeQuery();
            } else {
                listByCustomerStatement.setString(1, customerLogin);
                results = listByCustomerStatement.executeQuery();
            }
            while (results.next()) {
                int auctionId = results.getInt("auction_id");
                Product product = new Product(session.getDb(), auctionId);
                products.add(product);
            } 

        } catch(SQLException e) {
            while (e != null) {
                debug.println(e.toString());
                debug.flush();
                e = e.getNextException();
            }
        }
        for (int i = 0; i < products.size(); i++) {
            debug.println(products.get(i).name);
        }
        debug.flush();

        if (products.size() <= 0) {
            productsBox.setLine(0, "");
            productsBox.setLine(1, " No products to show.");
            for (int i = 2; i < 13; i++){
                productsBox.setLine(i, "");
            }
        } else {
            productsBox.setLine(0, "      Name | Status | Highest Bid Amount | Buyer or Highest Bidder");
            productsBox.setLine(1, "");
            ArrayList<Product> productsOnPage = paginator.paginate(products, curPage, 5);
            int lineOffset = 0;
            for (int i = 0; i < productsOnPage.size(); i++) {
                Product product = productsOnPage.get(i);
                //lineOffset works now
                lineOffset = i * 2 - 1;
                productsBox.setLine(lineOffset + 2, " " + product.name + " | "
                                                  + product.status+ " | $" 
                                                  + product.amount + " | "
                                                  + product.getHighestBidder());
            }
            //clear the lines of productBox if there are not as many products as before
            int lastLine = productsOnPage.size() * 2 + 1;
            for (int i = lastLine; i< 12; i++){
                productsBox.setLine(i, "");
            }
            productsBox.setLine(12, paginator.getPageMenu(products, curPage, 5));

        }

    }
}