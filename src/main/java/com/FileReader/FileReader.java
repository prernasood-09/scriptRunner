package com.FileReader;

import java.io.BufferedReader;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.connection.FileExecuter;

public class FileReader{

	
	public void readFile(String rollBack,String file) throws ClassNotFoundException, IOException {

		List<String[]> filesData = new ArrayList<String[]>();
		String[] finalQuery = null;
		String fileName = "";
		
		FileExecuter fileExecuter = new FileExecuter();
		
		if ((file.trim()).endsWith(".sql")) {
			fileName = new File(file).getName();
			System.out.println("\nExecuting File - " + fileName);

			try (BufferedReader buffer = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), "ISO-8859-1"))) {
				StringBuffer stringBuffer = new StringBuffer();
				String line;
				while ((line = buffer.readLine()) != null) {
					stringBuffer.append(line.trim());
					stringBuffer.append("\n");
				}
				buffer.close();

				finalQuery = stringBuffer.toString().split("DELIMITER|$$");
				if (finalQuery != null) {
					filesData.add(finalQuery);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			fileExecuter.executeFiles(rollBack,filesData, fileName);

		}

	}

}