package com.FileReader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import com.base.ObjectBase;

public class FileGetter extends ObjectBase  {
	
	public void getFilesFromDirectory(String rollBack) throws IOException, ClassNotFoundException {

		File fileLocation = new File(getObjectPath("location").trim());
		
		FileGetter fileGetter = new FileGetter();
		
		File[] files = null;
				
		if (fileLocation != null) {
			if (fileLocation.isDirectory()) {
				files = fileLocation.listFiles();
				if (files.length > 0) {
					Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
					fileGetter.getFiles(rollBack,files, 0);
				}
			} else if (fileLocation.isFile()) {
				files = new File[] { fileLocation };
				fileGetter.getFiles(rollBack,files, 0);
			} else {
				System.out.println("Location Not Found. Please check the path!");
			}
		}
	}

	public void getFiles(String rollBack,File[] files, int level) throws ClassNotFoundException, IOException {

		String filesInDirectory;
		FileReader fileReader = new FileReader();
		
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
				//	getFiles(rollBack, file.listFiles(), (level + 1));
				} else {
					filesInDirectory = (file.getAbsolutePath());
					fileReader.readFile(rollBack, filesInDirectory);
				}
			}
		}

	}

	
}
