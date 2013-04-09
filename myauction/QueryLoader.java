package myauction;

import java.io.FileReader;
import java.util.Scanner;

public class QueryLoader {
	public static String loadQuery(String fileName) {
		String query = "";

		Scanner file;
		try {
			file = new Scanner(new FileReader(fileName));

			while (file.hasNextLine()) {
				query += file.nextLine() + " ";
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return query;
	}
}