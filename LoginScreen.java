import java.awt.Point;

public class LoginScreen extends Screen {
	public LoginScreen() {
		super();

		CLIObject loginBox = new CLIObject(WIDTH, 6);
		loginBox.setLine(0, "---Login----------------");
		loginBox.setLine(1, "|                      |");
		loginBox.setLine(2, "| Username: __________ |");
		loginBox.setLine(3, "| Password: __________ |");
		loginBox.setLine(4, "|                      |");
		loginBox.setLine(5, "------------------------");

		addScreenObject(loginBox, new Point(28, 9));
	}
}