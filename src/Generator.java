import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class Generator {
	private static final Random RANDOM = new SecureRandom();
	private static MessageDigest md;
	
	protected static String getSaltString() 
	{
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 32) // length of the random string.
        { 
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
	
    public static String getSHA256SecurePassword(String password, String salt) throws NoSuchAlgorithmException
    {
        String generatedPassword = null;
        md = MessageDigest.getInstance("SHA-256");
        String passwordToHash = password + salt;
		byte[] bytes = md.digest(passwordToHash.getBytes());
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < bytes.length; i++)
		{
		    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		generatedPassword = sb.toString();
        return generatedPassword;
    }
    
    public static String format(String username, String salt, String hash, int imp)
    {
    	return username + ", " + salt + ", " + hash + ", " + Integer.toString(imp);
    }
	
	static void writeFile(String filename, String context)
	{
	 	try{
	     	PrintWriter outputStream = new PrintWriter(filename);
	     	outputStream.println(context);	
	     	outputStream.close();	//Need to flush content into the file
	 	} catch (FileNotFoundException e){
	 		e.printStackTrace();
	 	}
	}
	
	public static String[] readFile(String filename) throws Exception
	{
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) 
		{
		    String line;
		    int counter = 0;
		    String[] arr = new String[2]; 
		    while ((line = br.readLine()) != null) 
		    {
			    // process the line.
		    	arr = line.split(", ");
		    	counter++;
		    }
		    print(arr);
		    return arr;
		}
	}
	
	public static void print(String[] context)
	{
		for(int i = 0; i < context.length; i++)
	    	System.out.println(context[i]);
	}

	public static void main(String[] args) throws Exception
	{
		if (args.length != 2) 
        {
            System.err.println("Usage: java <password | implementation (0 - 4)>");
        } else 
        {
			String username = "RiceChau";
			String password = args[0];
			int implement = Integer.parseInt(args[1]);
			String salt = getSaltString();
			
	        String securePassword = getSHA256SecurePassword(password, salt);
	        
	        String contextToSave = format(username, salt, securePassword, implement);
	        System.out.println(contextToSave);
	        
	        writeFile("pwd.txt", contextToSave);
	        //String[] arr = readFile("pwd.txt");
	        
	        //String test = getSHA256SecurePassword(password, arr[1]);
	        //System.out.println(test);
        }
	}
}
