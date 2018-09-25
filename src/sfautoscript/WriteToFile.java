package sfautoscript;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class WriteToFile {
	public  void write(String strContent) {

		BufferedWriter bufferedWriter = null;
		try {
			// String strContent = "This example shows how to write string content to a file";
			File myFile = new File("D:/Salesforce-Zendesk Automation/Salesforce_Data.csv");
			// check if file exist, otherwise create the file before writing
			if (!myFile.exists()) {
				myFile.createNewFile();
			}
			Writer writer = new FileWriter(myFile);
			bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(strContent);
			System.out.println("CSV file created successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try{
				if(bufferedWriter != null) bufferedWriter.close();
			} catch(Exception ex){

			}
		}

		ReadCsv1 read=new ReadCsv1();
		
		
		try 
		{
			read.compare();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
