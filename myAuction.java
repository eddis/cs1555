public class myAuction {
	public static void main(String[] args) {
		Screen screen = new LoginScreen();
		while (true) {
			int nextScreen = screen.run();

			switch (nextScreen) {
			case Screen.LOGIN:
				screen = new LoginScreen();
				break;
			case Screen.CUSTOMER:
				screen = new CustomerScreen();
				break;
			case Screen.ADMIN:
				screen = new AdminScreen();
				break;
			case Screen.BROWSE_PRODUCTS:
				screen = new BrowseProductsScreen();
				break;
			case Screen.SEARCH_PRODUCTS:
				screen = new SearchProductsScreen();
				break;
			case Screen.START_AUCTION:
				screen = new StartAuctionScreen();
				break;
			case Screen.VIEW_ONGOING:
				screen = new ViewOngoingScreen();
				break;
			case Screen.VIEW_CLOSED:
				screen = new ViewClosedScreen();
				break;
			}
		}
	}
}