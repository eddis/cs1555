import java.awt.Point;
import java.util.Scanner;

public class myAuction {
	public static void main(String[] args) {
		LoginScreen screen = new LoginScreen();
		Scanner in = new Scanner(System.in);

		while (true) {
			screen.draw();
			//screen.input();

			char escCode = 0x1B;
			int row = 10; int column = 10;
			System.out.print(String.format("%c[%d;%df",escCode,row,column));
			
			in.next();
		}
	}
}