package com.dreambig.homebase.helper;

import java.util.Random;

public class RandomString {

	  private static final char[] constants = {'B','C','D','F','G','H','J','K','L','M','N','P','Q','R','S','T','V','W','X','Z'};
	  private static final char[] vowels = {'A','E','I','O','U'};
	  private final Random random = new Random();
	  private final char[] buf;

	  public RandomString(int length) {
	    buf = new char[length];
	  }

	  public String nextString() {
	    for (int idx = 0; idx < buf.length; ++idx) { 
	      if(idx % 2 == 0) {
	    	  buf[idx] = constants[random.nextInt(constants.length)];
	      }	else {
	    	  buf[idx] = vowels[random.nextInt(vowels.length)];
	      }
	    }
	    
	    return new String(buf);
	  }
	}
