package com.base;

import java.io.FileInputStream;
import java.util.Properties;

public class ObjectBase {
	
	public String getObjectPath(String path) {
		String propertiesValue = null;
		try {
			
			String dir = System.getProperty("user.dir");
			FileInputStream file = new FileInputStream(dir + "/src/main/java/com/properties/ObjectPath.properties");
			Properties property = new Properties();
			property.load(file);
			propertiesValue = property.getProperty(path);
		
		} catch (Exception e) {
           
			System.out.println(e);
			
		}
		return propertiesValue;

	}


}
