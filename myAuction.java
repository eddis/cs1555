import myauction.view_controller.*;
import myauction.Session;

public class myAuction {
	public static void main(String[] args) {
		Session session = new Session();
		Screen[] screens = new Screen[]{
			new LoginScreen(session),
			new CustomerScreen(session),
			new AdminScreen(session),
			new BrowseProductsScreen(session),
			new SearchProductsScreen(session),
			new StartAuctionScreen(session),
			new ViewOngoingScreen(session),
			new ViewClosedScreen(session)
		};

		Screen screen = screens[Screen.LOGIN];
		while (true) {
			int nextScreen = screen.run();
			screen = screens[nextScreen];
		}
	}
}