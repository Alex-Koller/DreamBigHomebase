package com.dreambig.homebase.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dreambig.homebase.endpoint.NewsEndpoint;

public class IOUtils {
	
	private static final Logger log = Logger.getLogger(IOUtils.class.getName());
	
	public static String getStringFromInputStream(InputStream is) throws IOException {
 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		}  finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				    log.log(Level.WARNING, "Failed closing buffered reader",e);
				}
			}
		}
 
		return sb.toString();
 
	}	

}
