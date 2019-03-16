package com.connection;

import java.sql.Connection;
import java.sql.DriverManager;

import com.base.ObjectBase;

public class ConnectionCreater extends ObjectBase{
	
	final String JDBC_DRIVER = getObjectPath("JDBC_DRIVER");
	final String DB_URL = getObjectPath("DB_URL");
	final String SCHEMA = getObjectPath("SCHEMA");
	// Database credentials
	final String USER = getObjectPath("USER");
	final String PASSWORD = getObjectPath("PASSWORD");
	final String URL = DB_URL + "/" + SCHEMA;
	
	public Connection createConnection() {


		Connection connection = null;
		
		try {Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(URL, USER, PASSWORD);
		
		connection.setAutoCommit(false);
		
		}catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
		return connection;
	}

}
