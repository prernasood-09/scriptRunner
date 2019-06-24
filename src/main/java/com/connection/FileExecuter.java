package com.connection;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.base.ObjectBase;

public class FileExecuter extends ObjectBase {

	public void executeFiles(String rollBack, List<String[]> executableStatementsList, String fileName)
			throws ClassNotFoundException, IOException {

		ConnectionCreater connectionCreater = new ConnectionCreater();
		
		PreparedStatement preparedStatement = null;
		Statement statement = null;

		try {

			int executionFailed = Statement.EXECUTE_FAILED;
			Connection connection = connectionCreater.createConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			int result[] = null;
			try {
				for (int i = 0; i < executableStatementsList.size(); i++) {

					for (int j = 0; j < executableStatementsList.get(i).length; j++) {
						executableStatementsList.get(i)[j] = (executableStatementsList.get(i)[j].replace("$$", "")
								.trim());
						if (executableStatementsList.get(i)[j].length() > 1) {
							if ((executableStatementsList.get(i)[j].toLowerCase()).startsWith(getObjectPath("STARTS_WITH_SELECT")) || (executableStatementsList.get(i)[j].toLowerCase()).startsWith(getObjectPath("STARTS_WITH_INSERT"))) {

								String matchedPattern = "";
								String queryArray[] = executableStatementsList.get(i)[j].split(";");
								Map<String, String> patternMap = new HashMap<String, String>();

								for (int m = 0; m < queryArray.length; m++) {
									String replacedQuery = "";
									Pattern pattern = Pattern.compile(getObjectPath("MATCHING_PATTERN1"));
									Matcher matchPattern = pattern.matcher(queryArray[m]);
									replacedQuery = ("("
											.concat(queryArray[m].replaceAll(getObjectPath("MATCHING_PATTERN1"), ""))
											.concat(")").replace(";", ""));
									if (matchPattern.find()) {
										matchedPattern = matchPattern.group(0);
										patternMap.put(matchedPattern, replacedQuery);
									}
									String query = "";
									if ((queryArray[m].toLowerCase().trim()).startsWith(getObjectPath("STARTS_WITH_INSERT")) && ((queryArray[m].toLowerCase()).contains(getObjectPath("CONTAINS_@")))) {
										query = query.concat(queryArray[m]);
										for (int n = m; n < queryArray.length; n++) {
											for (Map.Entry<String, String> entry : patternMap.entrySet()) {
												if ((queryArray[m]).contains(entry.getKey()
														.replace(getObjectPath("REPLACE_WITH_INTO"), ""))) {
													query = (queryArray[m].replaceAll(entry.getKey()
															 .replace(getObjectPath("REPLACE_WITH_INTO"), ""),entry.getValue()));
													queryArray[m] = query ;
													
												}else {
													System.out.println(query);
												}
												System.out.println(query);
												 preparedStatement = connection.prepareStatement(query);
												 preparedStatement.addBatch();
												 result = preparedStatement.executeBatch();
												
											}
											m++;
										}
										
									} else if (!((queryArray[m].toLowerCase()).contains(getObjectPath("CONTAINS_@"))) && ((queryArray[m].toLowerCase().trim()).startsWith(getObjectPath("STARTS_WITH_INSERT")))) {
										query = query.concat(queryArray[m]);
										System.out.println(query);
										connection.setAutoCommit(true);
										statement.addBatch(query);
										result = statement.executeBatch();
									}
								}

							} else {

								statement.addBatch(executableStatementsList.get(i)[j]);
							 // System.out.println(executableStatement.get(i)[j]);
								result = statement.executeBatch();
							}							
						}
					}
				}

				System.out.println("Batch has managed to process {" + result.length + "} queries from file " + fileName
						+ " successfully..");
			}

			catch (BatchUpdateException ex) {
				connection.setAutoCommit(false);
				int[] updateCount = ex.getUpdateCounts();

				int count = 1;
				int failedEnteries = 0;
				for (int i : updateCount) {

					if (i == executionFailed) {
						failedEnteries++;
						connection.rollback();
						System.err.println("\nError on request " + count + " in file " + fileName + " \nSQLException: "
								+ ex.getErrorCode() + " - " + ex.getMessage());
						if (getObjectPath("RESPONSE").equals(rollBack)) {
							System.err.println("\n\nApplication exists due to error in file : " + fileName);
							System.exit(failedEnteries);
						}

					} else {
						connection.commit();
					 // System.out.println("Request " + count + ": OK");
						statement.close();
					}
					count++;
				}
				System.out.println("\nBatch has managed to process {" + ex.getUpdateCounts().length
						+ "} queries from file " + fileName + " , with errors in {" + failedEnteries + "} queries.\n");

			} finally {
				connection.close();
			}
		} catch (SQLException se) {

			se.printStackTrace();

		}

	}
}
