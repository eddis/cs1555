package myauction.view_controller;

import java.awt.Point;
import java.sql.*;/

import myauction.CLIObject;
import myauction.Session;

public class ProductStatsScreen extends Screen {
    private CLIObject headerBox;
    private CLIObject listBox;
    private CLIObject productsBox;
    private boolean listAllProducts;
    private String customerLogin;
    private int curPage;
    private ArrayList<Product> products;
    private PreparedStatement listAllProductsStatement = null;
    private PreparedStatement listByCustomerStatment = null;
    private PreparedStatement checkCustomerStatment = null;




    public ProductStatsScreen(Session session) {
        super(session);
        headerBox = new CLIObject(WIDTH, 2);
        headerBox.setLine(0, " Previous (<)                 Product Statistics                            ");
        headerBox.setLine(1, " ---------------------------------------------------------------------------");

        listBox = new CLIObject(WIDTH, 5);
        listBox.setLine(0, "                                                                            ");
        listBox.setLine(1, " |X| List all products (a)");
        listBox.setLine(2, " | | List products by customer login (c)         customer: __________");
        listBox.setLine(3, "                                                                            ");
        listBox.setLine(4, "----------------------------------------------------------------------------");

        productsBox = new CLIObject(WIDTH, 17);


        listAllProducts = true;
        customer = ""
        curPage = 1;

        listAllProductsStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("select auction_id from product"));
        listByCystomerStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("select auction_id from product where login = ?"));
        checkCustomerStatement = session.getDb().prepareStatement("select count(*) as num_customers from customer where login = ?");

        addScreenObject(headerBox, new Point(originX, originY));
        addScreenObject(listBox, new Point(originX, originY + 2));
        addScreenObject(productsBox, new Point(originX, originY + 6));


    }

    public int run() {

        String option = getInput();



        return 0;
    }

    public boolean checkCustomer(String customer) {
        try {
            ResultSet results;
            checkCustomer.setString(1, customer);
            results = checkCustomer.executeQuery();
            if (results.getInt("num_customers") > 0){
                return true;
            }
        } catch (SQLException e) {
            while (e != null) {
                System.out.println(e.toString());
                e = e.getNextException();
            }
        }
        return false;
        }

    }

    public void listProducts() {
        products = new ArrayList<Product>;

        try {
            ResultSet results;

            if (listAllProducts) {
               results = listAllProductsStatement.executeQuery();
            } else {
                listByCustomerStatement.setString(1, customerLogin);
                results = listByCustomerStatement.
            }
            while (results.next()) {
                int auctionId = results.getInt("product.auction_id");
                Product product = new Product(session.getDb(), auctionId);
                products.add(product);
            }

        } catch(SQLException e) {
            while (e != null) {
                System.out.println(e.toString());
                e = e.getNextException();
            }
        }
    }
}