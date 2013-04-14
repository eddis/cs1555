package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import myauction.CLIObject;
import myauction.Session;

public class UpdateTimeScreen extends Screen {
	private CLIObject headerBox;
	private CLIObject updateTimeBox;


	public UpdateTimeScreen(Session session) {
		super(session);

		headerBox = new CLIObject(WIDTH, 2);
		headerBox.setLine(0, "Previous (<)                  Update System Time                            ");
		headerBox.setLine(1, "----------------------------------------------------------------------------");

		updateTimeBox.setLine(0, "---Update Date---------------------");
		updateTimeBox.setLine(1, "|                                 |");
		updateTimeBox.setLine(2, "| Month (MM) : |_                 |");
		updateTimeBox.setLine(3, "| Day (DD) : __                   |");
		updateTimeBox.setLine(4, "| Year (YYYY) : ____              |");
		updateTimeBox.setLine(5, "|                                 |");
		updateTimeBox.setLine(6, "| Hour (hh) : __                  |");
		updateTimeBox.setLine(7, "| Minute (mm) : __                |");
		updateTimeBox.setLine(8, "| Seconds (ss) : __               |");
		updateTimeBox.setLine(9, "|                                 |");
		updateTimeBox.setLine(10, "-----------------------------------");

	}

	public int run() {
		return 0;
	}
}