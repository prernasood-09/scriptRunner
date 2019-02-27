package com.connection;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

import com.base.ObjectBase;
import com.scriptRunner.FileReader;

public class FileExecuter extends ObjectBase {

	final String JDBC_DRIVER = getObjectPath("JDBC_DRIVER");
	final String DB_URL = getObjectPath("DB_URL");
	final String SCHEMA = getObjectPath("SCHEMA");

	// Database credentials
	final String USER = getObjectPath("USER");
	final String PASSWORD = getObjectPath("PASSWORD");
	final String URL = DB_URL + "/" + SCHEMA;
	private Scanner input;

	public static void main(String[] args) throws ClassNotFoundException, IOException {
    	
		
		FileExecuter fileExecuter = new FileExecuter();
		
		fileExecuter.input = new Scanner(System.in);
		
    	System.out.print("Do you want to rollBack immediately if any error is reported in any of the files (yes/No) ?\n");
    	String rollBack = fileExecuter.input.next().toLowerCase();
		
    	if("yes".equals(rollBack)) {
    		fileExecuter.createConnectionAndExecuteFiles(rollBack);
    	}else {
    		fileExecuter.createConnectionAndExecuteFiles("No");
    	}

	}

	public void createConnectionAndExecuteFiles(String rollBack) throws ClassNotFoundException, IOException {

		Connection connection = null;
		Statement statement = null;

		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			statement = connection.createStatement();
			
			connection.setAutoCommit(false);
			
			FileReader fileReader = new FileReader();

			List<String[]> executableStatement = fileReader.getFilesFromDirectory();
			
			int executeFailed = Statement.EXECUTE_FAILED;
			
			try {
				for (int i = 0; i < executableStatement.size(); i++) {

					for (int j = 0; j < executableStatement.get(i).length; j++) {
						executableStatement.get(i)[j] = (executableStatement.get(i)[j].replace("$$", "").trim());
						if(executableStatement.get(i)[j].length()>0) {
							statement.addBatch(executableStatement.get(i)[j]);
					//		System.out.println(executableStatement.get(i)[j]);
						}
					}
				}
			  int result[] = statement.executeBatch();
			  System.out.println( "Batch has managed to process {" + result.length + "} queries successfully..");
			}

			catch (BatchUpdateException ex) {
              
				if("No".equals(rollBack)) {
					int[] updateCount = ex.getUpdateCounts();
					
					int count = 1;
					int failedEnteries = 0;
					for (int i : updateCount) {
						
						if (i == executeFailed) {
							failedEnteries++ ;
							connection.rollback();
							System.err.println("Error on request " + count + ": Execution failed \nSQLException: " + ex.getErrorCode() 
							                      +  " - " + ex.getMessage());	
							
						} else {
							
							//	System.out.println("Request " + count + ": OK");
								statement.close();
						}
						count++;
					}
					System.out.println( "Batch has managed to process {" + ex.getUpdateCounts().length + "} queries, with errors in {" + failedEnteries + "} queries.");

				}
				else {
					System.out.println("Something Went Wrong!!");
					System.exit(0);
				}
				
			} finally {
				connection.commit();
				connection.close();
			}
		} catch (SQLException se) {

			se.printStackTrace();

		}

	}
}
 
