package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import myauction.CLIObject;
import myauction.Session;

public class AdminScreen extends Screen {
    private CLIObject tasksBox;
    private CLIObject statsBox;


	public AdminScreen(Session session) {
		super(session);

		tasksBox = new CLIObject(WIDTH, 6);
		tasksBox.setLine(0, "---Tasks---------------------");
		tasksBox.setLine(1, "|                           |");
		tasksBox.setLine(2, "| New User Registration (n) |");
		tasksBox.setLine(3, "| Update System Time (u)    |");
		tasksBox.setLine(4, "|                           |");
		tasksBox.setLine(5, "-----------------------------");


		statsBox = new CLIObject(WIDTH, 6);
		statsBox.setLine(0, "---Statistics----------------");
		statsBox.setLine(1, "|                           |");
		statsBox.setLine(2, "| Product Statistics (p)    |");
		statsBox.setLine(3, "| Trends (t)                |");
		statsBox.setLine(4, "|                           |");
		statsBox.setLine(5, "-----------------------------");


		addScreenObject(tasksBox, new Point(originX + 3, originY + 6));
		addScreenObject(statsBox, new Point(originX + 3, originY + 13));
	}	
	


	public int run() {
		int nextScreen = ADMIN;

		draw();

		String option;
		option = getInput();

		if (option.equals("n")) {
			nextScreen = NEW_USER;
		} else if (option.equals("u")) {
			nextScreen = UPDATE_TIME;
		} else if (option.equals("p")) {
			nextScreen = PRODUCT_STATS;
		} else if (option.equals("t")) {
			nextScreen = TRENDS;
		} else {

			nextScreen = ADMIN;
		}


		return nextScreen;
	}
}