package Compare.pack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import au.com.bytecode.opencsv.CSVReader;

public class ReadCsv1 {

	private static final String FILE_PATH1 = "D:\\Salesforce-Zendesk Automation\\File1.csv";
	private static final String FILE_PATH2 = "D:\\Salesforce-Zendesk Automation\\File2.csv";

	public static void compare(String[] args) throws Exception {

		CSVReader reader1 = new CSVReader(new FileReader(FILE_PATH1));
		CSVReader reader2 = new CSVReader(new FileReader(FILE_PATH2));
		System.out.println(checkIsSame(reader1, reader2) == true ? "Both files are Same." : "\n FILES ARE NOT SAME");
	}

	static boolean checkIsSame(CSVReader reader1, CSVReader reader2) throws Exception {
		boolean isTrue = true;
		int row = 0;
		while (true) {
			String[] nextLine1 = null;
			String[] nextLine2 = null;

			nextLine1 = reader1.readNext();
			nextLine2 = reader2.readNext();
			if (nextLine1 == null && nextLine2 == null) {
				break;
			}
			if (nextLine1 == null || nextLine2 == null) {
				isTrue = false;
				System.out.println("Failed, Mismatch found in the files");
				break;
			}
			// System.out.println(nextLine1[0] + " " + nextLine2[0]);
			if (nextLine1.length != nextLine2.length) {
				System.out.println("Failed, because of column size missmatch " + nextLine1.length + " " + nextLine2.length
						+ " at row " + row);
				isTrue = false;
			}
			int len = nextLine1.length;
			// System.out.println("----------------------------------");
			for (int i = 0; i < len; i++) {

				// System.out.println(nextLine1[i] + " " + nextLine2[i]);
				if ((nextLine1[i] == null && nextLine2[i] != null) || (nextLine1[i] != null && nextLine2[i] == null)) {
					System.out.println("Failed,  because of column have mismatch value " + nextLine1[i] + " "
							+ nextLine2[i] + "(" + row + "," + i + ")");
					isTrue = false;

				} else if ((nextLine1[i] == null && nextLine2[i] == null)) {
					continue;
				} else if (!nextLine1[i].equals(nextLine2[i])) {
					System.out.println("Failed,  because of column have mismatch value " + nextLine1[i] + " "
							+ nextLine2[i] + "(" + row + "," + i + ")");
					isTrue = false;
				}
			}
			row++;
		}
		return isTrue;
	}

}
