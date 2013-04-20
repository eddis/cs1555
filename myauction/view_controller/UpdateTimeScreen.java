package myauction.view_controller;

import java.awt.Point;
import java.sql.*;
import myauction.CLIObject;
import myauction.Session;
import myauction.QueryLoader;


public class UpdateTimeScreen extends Screen {
	private CLIObject headerBox;
	private CLIObject updateTimeBox;
	private PreparedStatement updateTimeStatement;
	private String curTime;
	private PreparedStatement curTimeStatement;



	public UpdateTimeScreen(Session session) {
		super(session);


		headerBox = new CLIObject(WIDTH, 2);
		headerBox.setLine(0, "Previous (<)                  Update System Time                            ");
		headerBox.setLine(1, "----------------------------------------------------------------------------");

		updateTimeBox = new CLIObject(WIDTH, 11);
		updateTimeBox.setLine(0,  "---Update Date---------------------");
		updateTimeBox.setLine(1,  "|                                 |");
		updateTimeBox.setLine(2,  "| Month (MM) : |_                 |");
		updateTimeBox.setLine(3,  "| Day (DD) : __                   |");
		updateTimeBox.setLine(4,  "| Year (YYYY) : ____              |");
		updateTimeBox.setLine(5,  "|                                 |");
		updateTimeBox.setLine(6,  "| Hour (hh) : __                  |");
		updateTimeBox.setLine(7,  "| Minute (mm) : __                |");
		updateTimeBox.setLine(8,  "| Seconds (ss) : __               |");
		updateTimeBox.setLine(9,  "|                                 |");
		updateTimeBox.setLine(10, "-----------------------------------");

		addScreenObject(updateTimeBox, new Point(originX + 2, originY + 3));

	}
	public void reset(){
		setMonth("|_");
		setDay("__");
		setYear("____");
		setHour("__");
		setMinute("__");
		setSecond("__");
		curTime = getCurrentTime();
		updateStatus("Current Time: " + curTime);
	}

	public int run() {
		reset();
		draw();
		String month = getInput();
		if (month.equals("<")) {
			return ADMIN;
		}
		setMonth(month);
		
		updateTimeBox.setLine(3, "| Day (DD) : |_                   |");
		draw();
		String day = getInput();
		if (day.equals("<")) {
			return ADMIN;
		}
		setDay(day);

		updateTimeBox.setLine(4, "| Year (YYYY) : |___              |");
		draw();
		String year = getInput();
		if (month.equals("<")) {
			return ADMIN;
		}
		setYear(year);

		updateTimeBox.setLine(6, "| Hour (hh): |_                   |");
		draw();
		String hour = getInput();
		if (hour.equals("<")) {
			return ADMIN;
		}
		setHour(hour);
		

		updateTimeBox.setLine(7, "| Minute (mm): |_                 |");
		draw();
		String minute = getInput();
		if (minute.equals("<")){
			return ADMIN;
		}
		setMinute(minute);

		updateStatus("");
		updateTimeBox.setLine(8, "| Second (ss): |_                 |");
		draw();
		String second = getInput();
		if (second.equals("<")){
			return ADMIN;
		}
		setSecond(second);
		String time = month + "/" + day +"/" + year + " " + hour + ":" + minute + ":" + second; 
		int rowUpdate = updateTime(time);
		if (rowUpdate > 0) {
			curTime = time;
			updateStatus("You have changed the system time to: " + time);
		}

		return UPDATE_TIME;
	}

	public int updateTime(String time) {
		try{
			if (updateTimeStatement == null) {
				updateTimeStatement = session.getDb().prepareStatement("update system_time set current_time = to_date(?, 'MM/DD/YYYY HH24:MI:SS')");
			}
			updateTimeStatement.setString(1, time);
			return updateTimeStatement.executeUpdate();
		} catch (SQLException e) {
            while (e != null) {
                debug.println(e.toString());
                debug.flush();
                e = e.getNextException();
            }
		}
		return -1;
	}
	public String getCurrentTime(){
			try {
			if (curTimeStatement == null) {
				curTimeStatement = session.getDb().prepareStatement("select to_char(current_time, 'MM/DD/YYYY HH24:MI:SS') as cur_time from system_time");
			}
			ResultSet results = curTimeStatement.executeQuery();
			results.next();
			curTime = results.getString("cur_time");
		} catch (SQLException e) {
			debug.println(e.toString());
          	debug.flush();
            e = e.getNextException();
		}
		return curTime;
	}

	public void setMonth(String month) {
		String line = "| Month (MM): " + month;
		for (int i = month.length(); i < 20; i++) {
			line += " ";
		}
		line += "|";
		updateTimeBox.setLine(2, line);
	}

	public void setDay(String day) {
		String line = "| Day (DD): " + day;
		for (int i = day.length(); i < 22; i++) {
			line += " ";
		}
		line += "|";
		updateTimeBox.setLine(3, line);
	}

	public void setYear(String year) {
				String line = "| Year (YYYY): " + year;
		for (int i = year.length(); i < 19; i++) {
			line += " ";
		}
		line += "|";
		updateTimeBox.setLine(4, line);
	}

	public void setHour(String hour) {
		String line = "| Hour (hh): " + hour;
		for (int i = hour.length(); i < 21; i++) {
			line += " ";
		}
		line += "|";
		updateTimeBox.setLine(6, line);
	}

	public void setMinute(String minute) {
		String line = "| Minute (mm) : " + minute;
		for (int i = minute.length(); i < 18; i++) {
			line += " ";
		}
		line += "|";
		updateTimeBox.setLine(7, line);
	}

	public void setSecond(String second) {
		String line = "| Second (ss) : " + second;
		for (int i = second.length(); i < 18; i++) {
			line += " ";
		}
		line += "|";
		updateTimeBox.setLine(8, line);
	}
}