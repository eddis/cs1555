package myauction;

import java.awt.Point;

public class CLIObject {
	private int maxWidth;
	private int height;
	private String[] text;
	private int curLineIndex;

	public CLIObject(int maxWidth, int height) {
		this.maxWidth = maxWidth;
		this.height = height;
		text = new String[height];
		curLineIndex = 0;
	}

	public int getHeight() {
		return height;
	}

	public void setLine(int i, String line) {
		if (line.length() > maxWidth) {
			line = line.substring(0, maxWidth);
		}
		text[i] = line;
	}

	/*
		Draws the next line of text and returns the amount of characters it wrote
	*/
	public int drawNextLine() {
		String line = text[curLineIndex];

		System.out.printf(line);

		curLineIndex++;
		if (curLineIndex == height) {
			curLineIndex = 0;
		}

		return line.length();
	}
}
