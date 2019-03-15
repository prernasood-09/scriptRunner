package com.connection;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.base.ObjectBase;
import com.scriptRunner.FileReader;

public class FileExecuter extends ObjectBase {

	final String JDBC_DRIVER = getObjectPath("JDBC_DRIVER");
	final String DB_URL = getObjectPath("DB_URL");
	final String SCHEMA = getObjectPath("SCHEMA");
	final String PATTERN1 = "(?i)INTO @\\w+";
	final String PATTERN2 = "(?i)@\\w+";

	// Database credentials
	final String USER = getObjectPath("USER");
	final String PASSWORD = getObjectPath("PASSWORD");
	final String URL = DB_URL + "/" + SCHEMA;

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		FileReader fileReader = new FileReader();
		fileReader.getFilesFromDirectory();

	}

	public void createConnectionAndExecuteFiles(List<String[]> executableStatement, String fileName)
			throws ClassNotFoundException, IOException {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		Statement statement = null;

		try {

			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			connection.setAutoCommit(false);

			int executeFailed = Statement.EXECUTE_FAILED;
			
			statement = connection.createStatement();
			int result[] = null;
			try {
				for (int i = 0; i < executableStatement.size(); i++) {

					for (int j = 0; j < executableStatement.get(i).length; j++) {
						executableStatement.get(i)[j] = (executableStatement.get(i)[j].replace("$$", "").trim());
						if (executableStatement.get(i)[j].length() > 1) {
							if ((executableStatement.get(i)[j].toLowerCase()).startsWith(("select"))) {
		
								String matchedPattern = "";
								String queryArray[] = executableStatement.get(i)[j].split("\n");
								 Map<String, String> patternMap = new HashMap<String, String>();
								
								for (int m = 0; m < queryArray.length; m++) {
									String replacedQueryArray = "";
									Pattern pattern1 = Pattern.compile(PATTERN1);
									Matcher matchPattern1 = pattern1.matcher(queryArray[m]);
									replacedQueryArray = ("(".concat(queryArray[m].replaceAll(PATTERN1, "")).concat(")").replace(";", ""));
									if (matchPattern1.find()) {
										matchedPattern = matchPattern1.group(0);
										patternMap.put(matchedPattern, replacedQueryArray);
										for (int n = m + 1; n < queryArray.length; n++) {
											String query = "";
											if ((queryArray[n].toLowerCase()).startsWith("insert")
													&& (queryArray[n].contains(matchedPattern.replace("INTO ", "")))) {
												for ( Map.Entry<String, String> entry : patternMap.entrySet()) {
													query = queryArray[n].replaceAll(entry.getKey().replace("INTO", ""), entry.getValue());
												}	
												preparedStatement = connection.prepareStatement(query);
												preparedStatement.addBatch();
											}
										}
									}

								}
								result = preparedStatement.executeBatch();

							} else {
								
								statement.addBatch(executableStatement.get(i)[j]);
							//	System.out.println(executableStatement.get(i)[j]);
							}
							result = statement.executeBatch();
						}
					}
				}
				
				System.out.println("Batch has managed to process {" + result.length + "} queries from file " + fileName
						+ " successfully..");
			}

			catch (BatchUpdateException ex) {

				int[] updateCount = ex.getUpdateCounts();

				int count = 1;
				int failedEnteries = 0;
				for (int i : updateCount) {

					if (i == executeFailed) {
						failedEnteries++;
						connection.rollback();
						System.err.println("\nError on request " + count + " in file " + fileName + " \nSQLException: "
								+ ex.getErrorCode() + " - " + ex.getMessage());

					} else {

						// System.out.println("Request " + count + ": OK");
						statement.close();
					}
					count++;
				}
				System.out.println("\nBatch has managed to process {" + ex.getUpdateCounts().length
						+ "} queries from file " + fileName + " , with errors in {" + failedEnteries + "} queries.\n");

			} finally {
				connection.commit();
				connection.close();
			}
		} catch (SQLException se) {

			se.printStackTrace();

		}

	}
}
