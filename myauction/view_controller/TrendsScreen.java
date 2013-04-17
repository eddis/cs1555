package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import myauction.CLIObject;
import myauction.Session;
import myauction.QueryLoader;

//TODO: take into account wrong user input
//      when user enters not a number

public class TrendsScreen extends Screen {
    private CLIObject chooseParamsBox;
    private CLIObject topLeafCategoriesBox;
    private CLIObject topRootCategoriesBox;
    private CLIObject topBiddersBox;
    private CLIObject topBuyersBox;
    private int k;
    private int x;
    private ArrayList<String> topLeafCategories;
    private ArrayList<String> topRootCategories;
    private ArrayList<String> topBidders;
    private ArrayList<String> topBuyers;
    private PreparedStatement topLeafStatement = null;
    private PreparedStatement topRootStatement = null;
    private PreparedStatement topBiddersStatement = null;
    private PreparedStatement topBuyersStatement = null;

    public TrendsScreen(Session session) {
        super(session);
        CLIObject headerBox = new CLIObject(WIDTH, 2);
        headerBox.setLine(0, "Previous (<)                  Trends                                       ");
        headerBox.setLine(1, "----------------------------------------------------------------------------");

        chooseParamsBox = new CLIObject(WIDTH, 2);
        chooseParamsBox.setLine(0, "   Choose K (top k): |_          Choose X (in the last X months): __");
        chooseParamsBox.setLine(1, "----------------------------------------------------------------------------");

        topLeafCategoriesBox = new CLIObject(WIDTH, 7);
        topLeafCategoriesBox.setLine(0, "Top K Leaf Cat. in the past X months");
        for (int i = 1; i < 7; i++) {
            topLeafCategoriesBox.setLine(i, "");
        }
        topRootCategoriesBox = new CLIObject(WIDTH, 7);
        topRootCategoriesBox.setLine(0, "Top K Root Cat. in the past X months");
        for (int i = 1; i < 7; i++) {
            topRootCategoriesBox.setLine(i, "");
        }
        topBiddersBox = new CLIObject(WIDTH, 7);
        topBiddersBox.setLine(0, "Top K Bidders in the past X months");
        for (int i = 1; i < 7; i++) {
            topBiddersBox.setLine(i, "");
        }
        topBuyersBox = new CLIObject(WIDTH, 7);
        topBuyersBox.setLine(0, "Top K Buyers in the past X months");
        for (int i = 1; i < 7; i++) {
            topBuyersBox.setLine(i, "");
        }

        CLIObject divider = new CLIObject(WIDTH, 16);
        for (int i = 0; i < 16; i++) {
            divider.setLine(i, "|");
        }

        addScreenObject(headerBox, new Point(originX, originY));
        addScreenObject(chooseParamsBox, new Point(originX, originY+2));
        addScreenObject(topLeafCategoriesBox, new Point(originX, originY + 4));
        addScreenObject(topRootCategoriesBox, new Point(originX, originY + 12));
        addScreenObject(topBiddersBox, new Point(originX + 39, originY + 4));
        addScreenObject(topBuyersBox, new Point(originX + 39, originY + 12));
        addScreenObject(divider, new Point(originX + 38, originY + 4));

        setPreparedStatements();


    }

    public void setPreparedStatements() {
        try {
            if (topRootStatement == null) {
                topRootStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("myauction/queries/topRootCategories.sql"));
            }
            if (topLeafStatement == null) {
                topLeafStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("myauction/queries/topLeafCategories.sql"));
            }
            if (topBiddersStatement == null) {
                topBiddersStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("myauction/queries/topBidders.sql"));
            }
            if (topBuyersStatement == null) {
                topBuyersStatement = session.getDb().prepareStatement(QueryLoader.loadQuery("myauction/queries/topBuyers.sql"));
            }
            

        } catch (SQLException e){
            while (e != null) {
                debug.println(e.toString());
                debug.flush();
                e = e.getNextException();
            }
        }
    }

    public void reset() {
        ArrayList<String> empty = new ArrayList<String>();
        setBox(topLeafCategoriesBox, empty);
        setBox(topRootCategoriesBox, empty);
        setBox(topBiddersBox, empty);
        setBox(topBuyersBox, empty);

    }

    public int run() {
        reset();
        draw();
        boolean finishedHere = false;
        while (finishedHere == false) {  
            try{
                String topK = getInput();
                if (topK.equals("<")) {
                    return ADMIN;
                }
                k = Integer.parseInt(topK);
                if (k > 6) {
                    k = 6;
                }
                chooseParamsBox.setLine(0, "   Choose K (top k): "+ k +"           Choose X (in the last X months): |_");
                updateStatus("");
                draw();

                String pastX = getInput();
                if (pastX.equals("<")) {
                    return ADMIN;
                }
                x = Integer.parseInt(pastX);

                topLeafCategories = runQuery(k, x, topLeafStatement);
                topRootCategories = runQuery(k, x, topRootStatement);
                topBuyers = runQuery(k, x, topBuyersStatement);
                topBidders = runQuery(k, x, topBiddersStatement);
                setBox(topLeafCategoriesBox, topLeafCategories);
                setBox(topRootCategoriesBox, topRootCategories);
                setBox(topBiddersBox, topBidders);
                setBox(topBuyersBox, topBuyers);
                chooseParamsBox.setLine(0, "   Choose K (top k): |_           Choose X (in the last X months): __");
                updateStatus("K = "+ k + ", X = " + x +".");
                draw();
            } catch (Exception e) {
                    updateStatus("Wrong user input, please enter a number or previous (<)");
                    draw();
            }
        } 
        return TRENDS;
    }

    public ArrayList<String> runQuery(int k, int x, PreparedStatement sqlStatement) {
        ArrayList<String> topKList = new ArrayList<String>();
        try {
            sqlStatement.setInt(1, x);
            sqlStatement.setInt(2, k);

            ResultSet results = sqlStatement.executeQuery();

            while (results.next()) {
                String top = results.getString("top_k");
                topKList.add(top);
            }
        } catch (SQLException e) {
            debug.println(e.toString());
            debug.flush();
            e = e.getNextException();
        }
        return topKList;
    }

    public void setBox(CLIObject box, ArrayList<String> top) {
        for (int i = 0; i < top.size(); i++) {
            box.setLine(i+1, "   "+ top.get(i));
        }
        for (int i = top.size()+1; i < 7; i++) {
            box.setLine(i, "");
        }
    }
}