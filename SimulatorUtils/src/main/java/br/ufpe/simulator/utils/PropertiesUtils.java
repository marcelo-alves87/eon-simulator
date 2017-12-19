package br.ufpe.simulator.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

	public static Properties createPropertiesFromResource(String resourcePath) {
		Properties prop = new Properties();
		try {
			InputStream stream = new FileInputStream(resourcePath);
			prop.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
