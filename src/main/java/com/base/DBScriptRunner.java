package com.base;

import java.io.IOException;
import java.util.Scanner;

import com.FileReader.FileGetter;

public class DBScriptRunner extends ObjectBase {

	private Scanner input;
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		DBScriptRunner dbScriptRunner = new DBScriptRunner();
		FileGetter fileGetter = new FileGetter();
	
		dbScriptRunner.input = new Scanner(System.in);
		
		System.out.println("Do you want to exit from application on any error in any file ? (yes/no)\n\n");
        String rollBack = dbScriptRunner.input.next().toLowerCase();
        
	
    	if(dbScriptRunner.getObjectPath("RESPONSE").equals(rollBack)) {
    		
    		fileGetter.getFilesFromDirectory(rollBack);
    		
    		System.err.println("\n completed!!!");
    		
    	}else {
    		
    		fileGetter.getFilesFromDirectory("No");
    		
    		System.err.println("\n completed!!!");
    	}

	}

}
