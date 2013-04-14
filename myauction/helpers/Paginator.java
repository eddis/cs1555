package myauction.helpers;

import java.util.ArrayList;

public class Paginator<T> {
	public ArrayList<T> paginate(ArrayList<T> list, int page, int numPerPage) {
		ArrayList<T> paginatedList = new ArrayList<T>();

		int startIndex = (page - 1) * numPerPage;
		if (startIndex < list.size()) {
			int endIndex = startIndex + numPerPage - 1;
			if (endIndex >= list.size()) {
				endIndex = list.size() - 1;
			}

			for (int i = startIndex; i <= endIndex; i++) {
				paginatedList.add(list.get(i));
			}
		}

		return paginatedList;
	}

	public String getPageMenu(ArrayList<T> list, int page, int numPerPage) {
		String menu = "";

		int curPageStartIndex = (page - 1) * numPerPage;
		int prevPageStartIndex = curPageStartIndex - numPerPage;
		int nextPageStartIndex = curPageStartIndex + numPerPage;

		if (prevPageStartIndex < 0) {
			menu += "                               ";
		} else {
			menu += String.format(" Previous Page (p%d)            ", page - 1);
		}

		menu += String.format("Current Page (p%d)             ", page);

		if (nextPageStartIndex >= list.size()) {
			menu += "               ";
		} else {
			menu += String.format("Next Page (p%d) ", page + 1);
		}

		return menu;
	}
}