import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Cracker 
{
    private char[] charset;

    private int min; //var added for min char length
    private int max; //var added for max char length
    private static MessageDigest md;
    static ArrayList<String> combination = new ArrayList<String>();
    
    private static final String NUMERIC_ALPHABET = "0123456789";
    private static final String LOWER_CASE_ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER_CASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SPECIAL_CASE = "$#%&*()";
    public Cracker(String implement) 
    {
    	switch(Integer.parseInt(implement))
    	{
    	case 0:
    		charset = LOWER_CASE_ALPHABET.toCharArray();
    		break;
    	case 1:
    		charset = (LOWER_CASE_ALPHABET + UPPER_CASE_ALPHABET).toCharArray();
    		break;
    	case 2:
    		charset = (LOWER_CASE_ALPHABET + UPPER_CASE_ALPHABET + NUMERIC_ALPHABET).toCharArray();
    		break;
    	case 3:
    		charset = (LOWER_CASE_ALPHABET + UPPER_CASE_ALPHABET + NUMERIC_ALPHABET + SPECIAL_CASE).toCharArray();
    		break;
    	}  	
        min = 2; //char min start
        max = 4; //char max end 
    }
    
    public void generate(String str, int pos, int length) 
    {
        if (length == 0) 
        {
            combination.add(str);
            System.out.println("Generating: " + str);
        } else 
        {
            //This if statement resets the char position back to the very first character in the character set ('A'), which makes this a complete solution to an all combinations bruteforce! 
            if (pos != 0) 
            {
                pos = 0;
            }

            for (int i = pos; i < charset.length; i++) 
            {
                generate(str + charset[i], i, length - 1);	//recur
            }
        }
    }
    
	public static String[] readFile(String filename) throws Exception
	{
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) 
		{
		    String line;
		    int counter = 0;
		    String[] arr = new String[3]; 
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
	
    public static boolean cracking(String hash, String salt, String imp) throws NoSuchAlgorithmException
    {
    	String generatedPassword = null;
        md = MessageDigest.getInstance("SHA-256");
        
        Cracker crack = new Cracker(imp);
        for (int length = crack.min; length <= crack.max; length++) // Change crack.min and crack.max for number of characters to bruteforce. 
        	crack.generate("", 0, length); //prepend_string, pos, length
        
        long startTime = System.currentTimeMillis();
        for(int i = 0; i < Cracker.combination.size(); i++)
        {
        	System.out.println("Comparing: " + Cracker.combination.get(i));
        	String passwordToCrack = Cracker.combination.get(i) + salt;
			byte[] bytes = md.digest(passwordToCrack.getBytes());

			StringBuilder sb = new StringBuilder();
			for(int j = 0; j < bytes.length; j++)
			{
			    sb.append(Integer.toString((bytes[j] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
			if(generatedPassword.equals(hash))
			{
				System.out.println("Found");
				long stopTime = System.currentTimeMillis();
		        float elapsedTime = stopTime - startTime;
		        System.out.println("Cracking time: " + elapsedTime);
		        System.out.println("Number of trials: " + (i + 1)); //Since counting from 0
				return true;
			}
        }
        return false;
    }
	
	public static void main(String[] args) throws Exception 
	{
		// TODO Auto-generated method stub
		String[] arr = readFile("pwd.txt");
		boolean check = cracking(arr[2], arr[1], arr[3]);
		if(check == false)
		{
			System.out.println("CRACK FAILED");
		}		
	}
}
