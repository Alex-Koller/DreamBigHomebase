package com.dreambig.homebase.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeHelper {
	
	public static String timestampToString(Long timestamp) {
		if(timestamp == null) return "N/A";
		
		Date date = new Date(timestamp);
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		return df.format(date);
	}

}
