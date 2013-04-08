import java.awt.Point;

public class Screen extends CLI {
	protected static final int WIDTH = 80;
	protected static final int HEIGHT = 24;

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

		addScreenObject(borderTop, new Point(0, 0));
		addScreenObject(borderLeft, new Point(0, 1));
		addScreenObject(borderRight, new Point(78, 1));
		addScreenObject(borderBottom, new Point(0, 23));
	}
}