import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;

public class User{
	
	private String username;
	private String password;
   
    public User(String nick, String pass){
       this.username = nick;
       this.password = pass;

       System.out.println( "Welcome: " + username);
    }
    
    public String getUsername() {
    	return username;
    }
    
    public boolean authenticate(String un, String pw) {
		return un.equals(this.username) && pw.equals(this.password);
	}
    
    public boolean isSuperuser() {
    	return false;
    }
    
	public static boolean checkIfExtist(String un, String pw, String filePath) {
		boolean exist = false;
		try {
			File users = new File(filePath);
			if(!users.exists()) return false;
			Scanner sc = new Scanner(users);
			String[] value = new String[2];
			while (sc.hasNextLine()) {    
		    	value = sc.nextLine().split(",");
		    	String unencrypted = decypt(value[1]); 
		    	if(value[0].equals(un) && unencrypted.equals(pw)) {
		    		exist = true;
		    		return exist;
		    	}
		    }
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.log(Level.SEVERE, "Login failed because the user doesn't exist");
		}
		return exist;
	}
	
	public static void saveUser(String un, String pw, String filePath) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(filePath,true);
			writer.append(un);
			writer.append(",");
			writer.append(encrypt(pw));
			writer.append("\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.log(Level.SEVERE, "Couldn't create user: " + un);
		}
	}

	//Based on https://stackoverflow.com/questions/29226813/simple-encryption-in-java-no-key-password
	private static String encrypt(String pw) {
		String b64encoded = Base64.getEncoder().encodeToString(pw.getBytes());
		// Reverse the string
		String reverse = new StringBuffer(b64encoded).reverse().toString();

		StringBuilder tmp = new StringBuilder();
		final int OFFSET = 4;
		for (int i = 0; i < reverse.length(); i++) {
			tmp.append((char)(reverse.charAt(i) + OFFSET));
		}
		return tmp.toString();
	}
	
	private static String decypt(String secret) {
		StringBuilder tmp = new StringBuilder();
		final int OFFSET = 4;
		for (int i = 0; i < secret.length(); i++) {
			tmp.append((char)(secret.charAt(i) - OFFSET));
		}

		String reversed = new StringBuffer(tmp.toString()).reverse().toString();
		return new String(Base64.getMimeDecoder().decode(reversed.getBytes()));
	}
}
