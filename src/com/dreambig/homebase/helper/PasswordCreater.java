package com.dreambig.homebase.helper;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;



public class PasswordCreater {

	private static final byte[] PASSWORD =  {31,16,87,9,73,10,45,35,111,76,93,18,42,52,32,8};

	 public String createPassword(String phoneNumber) throws FailedCreatingPasswordException {
		 
		 try {
			 Cipher cipher = Cipher.getInstance("AES"); 
			 cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(PASSWORD, "AES")); 
			 byte[] encryptedBytes = cipher.doFinal(phoneNumber.getBytes());
			 return bytesToString(encryptedBytes);
		 } catch(GeneralSecurityException e) {
			 throw new FailedCreatingPasswordException(e.getMessage() + " for " + phoneNumber,e);
		 }
		 
		 
	 }
	 
	 private String bytesToString(byte[] b) {
		byte[] b2 = new byte[b.length + 1];
		b2[0] = 1;
		System.arraycopy(b, 0, b2, 1, b.length);
		return new BigInteger(b2).toString(36);
	 }
	 
	 public static class FailedCreatingPasswordException extends Exception {
		 
		 public FailedCreatingPasswordException(String message, Exception e) {
			 super(message, e);
		 }
	 }

	
}
