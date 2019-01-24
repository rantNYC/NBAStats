import java.sql.SQLException;
import java.util.logging.Level;

/**
 * @author Rafael Ninalaya
 *
 */
public class Program{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
				
		try {
			String inPath = "";
			String outPath = "";
			String enableGUI = "";
			String enableAdmin = "";
			String storage = "";
			String email = "";
			String debug = "";
			for(int i = 0; i < args.length; ++i) {
			    switch(args[i].toLowerCase()) { 
			    	case("-i"):
			    		if(i < args.length)
			    			inPath = args[++i];
			    		break;
			    	case("-a"):
			    		if(i < args.length)
			    			enableAdmin = args[++i];
			    		break;
			    	case("-o"):
			    		if(i < args.length)
			    			outPath = args[++i];
			    		break;
			    		
			    	case("-p"):
			    		if(i < args.length)
			    			enableGUI = args[++i];
			    		break;
			    	case("-h"):
			    		if(i < args.length)
			    			storage = args[++i];
			    		break;
			    	case("-e"):
			    		if(i < args.length)
			    			email = args[++i];
			    		break;
			    	case("-d"):
			    		if(i < args.length)
			    			debug = args[++i];
			    		break;
			    	default:
			    		System.out.println("Command not recornigzed: " + args[i]);
			    		Log.log(Level.SEVERE,"Command not recognized: " + args[i]);
			    		System.exit(-2);//Bad Parameters
			    		break;
			    }
			}
			
			ProgramSettings programSettings = new ProgramSettings(inPath, outPath);
			programSettings.setEnableGUI(enableGUI);
			programSettings.setEnableAdmin(enableAdmin);
			programSettings.setStorageMethod(storage);
			programSettings.setEmail(email);
			programSettings.setEnableDebug(debug);
			Process process;
			process = new Process(programSettings);
			if(programSettings.getEnableGUI()) {
				process.startGUI();
			}
			else {
				//Checks if the admin file exists. The no gui mode does not create new users, it can only be done by the gui
				int problem = programSettings.CheckAdminFile();
				if(problem != 0)
					System.exit(problem);//No user file
				process.startNoGUI();
			}
		}catch(Exception ex) {
		}
	}
}
