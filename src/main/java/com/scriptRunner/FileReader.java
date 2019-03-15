package com.scriptRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.base.ObjectBase;
import com.connection.FileExecuter;

public class FileReader extends ObjectBase {

	String filesInDirectory;

	public void getFilesFromDirectory() throws IOException, ClassNotFoundException {

		File fileLocation = new File(getObjectPath("location"));
		File[] files = null;
		FileReader fileReader = new FileReader();
		if (fileLocation != null) {
			if (fileLocation.isDirectory()) {
				files = fileLocation.listFiles();
				Arrays.sort(files, new Comparator<File>() {
					public int compare(File f1, File f2) {
						return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
					}
				});
			} else {
				files = new File[] { fileLocation };
			}
			for (File file : files) {
				System.out.println(file.getName());
			}
			fileReader.getFiles(files, 0);
		}
	}

	public void getFiles(File[] files, int level) throws ClassNotFoundException, IOException {

		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					getFiles(file.listFiles(), level + 1);
				} else { 
					filesInDirectory = (file.getAbsolutePath());
					readFile(filesInDirectory);
				}
			}
		}
	
	}

	public void readFile(String file) throws ClassNotFoundException, IOException {

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

			fileExecuter.createConnectionAndExecuteFiles(filesData, fileName);

		}

	}

}