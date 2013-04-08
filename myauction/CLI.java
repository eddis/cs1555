package myauction;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Scanner;

public class CLI {
	private int width;
	private int height;
	private ArrayList<CLIObject> screenObjects;
	private ArrayList<Point> screenObjectCoords;
	protected Scanner in;

	public CLI(int width, int height) {
		this.width = width;
		this.height = height;
		screenObjects = new ArrayList<CLIObject>();
		screenObjectCoords = new ArrayList<Point>();
	 	in = new Scanner(System.in);
	}

	/* Adds a screen object to the interface and specifies its coordinate in it

	   * Width is in terms of characters
	   * Height is in terms of lines
	*/
	public void addScreenObject(CLIObject screenObject, Point coord) {
		screenObjects.add(screenObject);
		screenObjectCoords.add(coord);
	}

	public void sortByX(ArrayList<Point> coords, ArrayList<CLIObject> screenObjects) {
    	for (int i = 0; i < coords.size() - 1; i++) {
    		int smallestIndex = i;
    		for (int j = i + 1; j < coords.size(); j++) {
    			if (coords.get(j).x < coords.get(smallestIndex).x) {
    				smallestIndex = j;
    			}
    		}
    		Point tempCoord = coords.get(i);
    		CLIObject tempScreenObject = screenObjects.get(i);
    		coords.set(i, coords.get(smallestIndex));
    		screenObjects.set(i, screenObjects.get(smallestIndex));
    		coords.set(smallestIndex, tempCoord);
    		screenObjects.set(smallestIndex, tempScreenObject);
    	}
	}

	public void clear() {
		char escCode = 0x1B;
		System.out.print(String.format("%c[%dF", escCode, height));
	}

	public void draw() {
		ArrayList<Point> sortedScreenObjectCoords = new ArrayList(screenObjectCoords);
		ArrayList<CLIObject> sortedScreenObjects = new ArrayList(screenObjects);
		sortByX(sortedScreenObjectCoords, sortedScreenObjects);

		for (int i = 0; i < height; i++) {
			int curX = 0;
			for (int j = 0; j < sortedScreenObjects.size(); j++) {
				CLIObject screenObject = sortedScreenObjects.get(j);
				Point screenObjectCoord = sortedScreenObjectCoords.get(j);

				if (i >= screenObjectCoord.y && i < screenObjectCoord.y + screenObject.getHeight()) {
					while (curX < screenObjectCoord.x) {
						System.out.printf(" ");
						curX++;
					}

					int drawnChars = screenObject.drawNextLine();
					curX += drawnChars;
				}
			}

			while (curX < width) {
				System.out.printf(" ");
				curX++;
			}

			System.out.println();
		}
	}
}