import java.awt.Point;

// TODO: add status bar along top that will contain the "logout" option, amongst others
public class Screen extends CLI {
	protected static final int WIDTH = 80;
	protected static final int HEIGHT = 25;

	public static final int LOGIN = 0;
	public static final int CUSTOMER = 1;
	public static final int ADMIN = 2;
	public static final int BROWSE_PRODUCTS = 3;
	public static final int SEARCH_PRODUCTS = 4;
	public static final int START_AUCTION = 5;
	public static final int VIEW_ONGOING = 6;
	public static final int VIEW_CLOSED = 7;

	private CLIObject statusBox;

	public Screen() {
		super(WIDTH, HEIGHT);

		CLIObject borderTop = new CLIObject(WIDTH, 1);
		CLIObject borderBottom = new CLIObject(WIDTH, 1);
		borderTop.setLine(0, "----------------------------------myAuction------------------------------------");
		borderBottom.setLine(0, "-------------------------------------------------------------------------------");

		CLIObject borderLeft = new CLIObject(WIDTH, 22);
		CLIObject borderRight = new CLIObject(WIDTH, 22);
		for (int i = 0; i < 22; i++) {
			borderLeft.setLine(i, "|");
			borderRight.setLine(i, "|");
		}

		CLIObject statusBox = new CLIObject(WIDTH, 2);
		statusBox.setLine(0, "");
		statusBox.setLine(1, "-------------------------------------------------------------------------------");

		CLIObject prompt = new CLIObject(WIDTH, 1);
		prompt.setLine(0, ">");

		addScreenObject(borderTop, new Point(0, 0));
		addScreenObject(borderLeft, new Point(0, 1));
		addScreenObject(borderRight, new Point(78, 1));
		addScreenObject(borderBottom, new Point(0, 23));
		addScreenObject(prompt, new Point(0, 24));
	}

	public String getInput() {
		// place cursor on prompt line
		char escCode = 0x1B;
		System.out.print(String.format("%c[%dF", escCode, 1));
		System.out.print(String.format("%c[%dG", escCode, 2));
	
		// get the user input
		String input = in.next();

		// clear the input
		System.out.print(String.format("%c[%dK", escCode, 0));
		System.out.print(String.format("%c[%dF", escCode, 1));
		System.out.print(String.format("%c[%dK", escCode, 0));
	
		// place cursor back where it was before prompt
		System.out.print(String.format("%c[%dE", escCode, 1));
	
		return input;
	}

	public void updateStatus(String statusText) {
		statusBox.setLine(0, statusText);
	}
	
	public int run() {
		return 0;
	}
}