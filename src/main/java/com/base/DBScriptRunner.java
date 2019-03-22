package com.base;

import java.io.IOException;
import java.util.Scanner;

import com.FileReader.FileGetter;

public class DBScriptRunner extends ObjectBase {

	private Scanner input;
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		FileGetter fileGetter = new FileGetter();
		DBScriptRunner dbScriptRunner = new DBScriptRunner();
		dbScriptRunner.input = new Scanner(System.in);
		
		System.out.println("Do you want to exit from application on any error in any file ? (yes/no)\n\n");
        String rollBack = dbScriptRunner.input.next().toLowerCase();
        
        //fileGetter.getFilesFromDirectory();
        
	
    	if(dbScriptRunner.getObjectPath("RESPONSE").equals(rollBack)) {
    		fileGetter.getFilesFromDirectory(rollBack);
    	}else {
    		fileGetter.getFilesFromDirectory("No");
    	}

	}

}
