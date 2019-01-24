import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author Rafael Ninalaya
 * Contains all the information for the program to run
 */
public class ProgramSettings {

	private File inFile;
	private File outFile;
	private String storageMethod;
	private boolean enableGUI;
	private boolean enableAdmin;
	private String email;
	private boolean debug;
	
	private final String dataLocation = System.getProperty("user.home") + File.separator + "WebMining"; 
	private final String adminFile = dataLocation + File.separator + "Superuser.sts";
	private final String fileName = "data.csv";
	final private String url = "https://stats.nba.com/";
	
	public ProgramSettings(String in, String out) {
		File directory = new File(dataLocation);
	    if (!directory.exists()){
	    	directory.mkdir();
	    }
		if(in.isEmpty()) {
			inFile = null;
			Log.log(Level.WARNING, "No input file specified, no queries wil be processed from the input file");
		}
		else
			inFile = new File(in);
		if(out.isEmpty()) {
			outFile = null;
			Log.log(Level.WARNING, "No out file specified, nothing will be written to the output file for the user");
		}
		else
			outFile = new File(out);
		enableGUI = true;
		enableAdmin = false;
		storageMethod = "ht";
		email = "";
		debug = false;
	}

	//Checks if the admin file exists, and creates one. Also, it checks if it is not empty
	public int CheckAdminFile() {
		File users = new File(adminFile);
		try {
			if(!users.exists()) {
				users.createNewFile();
				Log.log(Level.INFO, "Creating users file! Please create an account using the GUI");
				return -1;
			}
			else {
				BufferedReader br = new BufferedReader(new FileReader(users)); 
				if (br.readLine() == null) {
				    System.out.println("There are no users. Please create an account using the GUI");
				    return -1;
				}
				return 0;
			}
		} catch (IOException e) {
			Log.log(Level.SEVERE,"Error creating the users file!");
			e.printStackTrace();
			return -2; //No users file exist or hidden	
		}	
	}
	
	public void setInputFile(String input) {
		try {
			if(input == null || input.isEmpty())
				throw new FileNotFoundException("The input file is invalid");
			inFile = new File(input);
		}
		catch(FileNotFoundException ex) {
			if(getDebug())
				System.out.println(ex.getStackTrace());//Only for debug
			Log.log(Level.SEVERE,"Input file is invalid");
		}
	}
	
	public File getInputFile() {
		return inFile;
	}
	
	public void setOutputFile(String output) {
		try {
			if(output == null || output.isEmpty())
				throw new FileNotFoundException("The out file is invalid");
			inFile = new File(output);
		}
		catch(FileNotFoundException ex) {
			if(getDebug())
				System.out.println(ex.getStackTrace());//Only for debug
			Log.log(Level.SEVERE,"Output file is invalid");
		}
	}
	
	public File getOutputFile() {
		return outFile;
	}
	
	public void setEmail(String e) {
		email = e;
	}
	
	public String getEmail() {
		return email;
	}

	public void setStorageMethod(String storage) {
		switch(storage.trim().toLowerCase()) {
			case("ht"):
				storageMethod = "ht";
				break;
			case("db"):
				storageMethod = "db";
				break;
			case("bo"):
				storageMethod = "bo";
				break;
			default:
				storageMethod = "ht";
		}
	}
	
	public String getStorageMethod() {
		return storageMethod;
	}
	
	public void setEnableGUI(String enable) {
		if(enable.equals("1")) {
			enableGUI = true;
		}
		else if(enable.equals("0")) {
			enableGUI = false;
		}
		else {
			enableGUI = true;
		}
	}
	
	public boolean getEnableGUI() {
		return enableGUI;
	}
	
	public void setEnableAdmin(String enable) {
		if(enable.equals("1")) {
			enableAdmin = true;
		}
		else if(enable.equals("0")) {
			enableAdmin = false;
		}
		else {
			enableAdmin = false;
		}
	}
	
	public void setEnableDebug(String enable) {
		if(enable.equals("1")) {
			debug = true;
		}
		else {
			debug = false;
		}
	}
	
	public boolean getEnableAdmin() {
		return enableAdmin;
	}
	
	public boolean getDebug() {
		return debug;
	}
	
	public String getAdminFile(){
		return adminFile;
	}
	
	public String getFilePath() {
		return dataLocation + File.separator + fileName;
	}
	
	public String getDataLocation() {
		return dataLocation;
	}
	
	public String getUrl() {
		return url;
	}
}
