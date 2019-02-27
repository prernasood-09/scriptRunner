package com.scriptRunner;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.base.ObjectBase;

public class FileReader extends ObjectBase {
	
  List<String> filesInDirectory = new ArrayList<String>();
		
  public List<String[]> getFilesFromDirectory() throws IOException {
	  
	    File fileLocation = new File(getObjectPath("location"));
	    FileReader fileReader = new FileReader();
		List<String[]> query = fileReader.readFile(fileLocation);
		
		return query;
  }
  		
   public List<String[]> readFile(File fileLocation) throws IOException {
	  

	    List<String[]> filesData = new ArrayList<String[]>();
		List<String> filesInDirectory = new ArrayList<String>();
		String[] finalQuery = null;	
		File[] files = fileLocation.listFiles();
              
		filesInDirectory = getFiles(files,0);
			
		if (filesInDirectory.size() >=1) {
			for(String file : filesInDirectory){
		          if(file.endsWith(".sql")) {
						try(BufferedReader buffer  = new BufferedReader(new InputStreamReader(new FileInputStream(file),"ISO-8859-1"))){	
							   StringBuffer stringBuffer = new StringBuffer();
							   String line;
							   while ((line = buffer.readLine()) != null) {
									stringBuffer.append(line.trim());
									stringBuffer.append("\n");
								}
								buffer.close();
							
								finalQuery = stringBuffer.toString().split("DELIMITER|$$");
								if(finalQuery != null) {
									filesData.add(finalQuery);
								}
								
							} catch (IOException e) {
							e.printStackTrace();
						}
					
				    }
			      }
		}
		else {
			System.out.println("Directory is Empty!!!");
		}
		
		return filesData;
   }	
   
   public  List<String> getFiles(File[] files,int level) {
	  
	   if(files!= null) {
			for(File file : files){
			    if(file.isDirectory())  
	             {  
			        getFiles(file.listFiles(), level + 1); 
	             } 
			    else if(file.isFile()){
			    	filesInDirectory.add(file.getAbsolutePath());
			    }
			}
       }
	   
	   return filesInDirectory;
   }
   
}