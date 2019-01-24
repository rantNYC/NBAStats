import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class Process {
	private ProgramSettings programSettings;
	private DataStorage info;
	private StringBuilder script;
	private User user;
	private DataBase db = null;
	private Email email;
	
	public DataStorage getInfo() {
		return info;
	}
	
	public String getUserProcess() {
		return user.getUsername();
	}
	
	public void setUser(String un, String pw) {
		user = new User(un,pw);
	}
	
	public ProgramSettings getProgramSettings() {
		return programSettings;
	}
	
	public DataBase getDb(){
		return db;
	}
	
	
	//Starts the database if the mode is db or bo
	public Process(ProgramSettings settings) {
		this.programSettings = settings;
		info = new DataStorage();
		if(programSettings.getStorageMethod() == "db" || programSettings.getStorageMethod() == "bo")
		try {
			this.db = new DataBase();
		} catch (SQLException e) {
			if(programSettings.getDebug())
				e.printStackTrace();
			Log.log(Level.SEVERE,"Couldn't initialize the database");
			System.exit(-3);//Database error
		}
		if(programSettings.getEmail() != null && !programSettings.getEmail().isEmpty()) {
			email = new Email(programSettings.getEmail());
		}
	}
	
	//Starts an instance of the GUI
	public void startGUI() {
		Process p = this;
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginGUI.createAndShowGUI(p);
                ProcessGUI.createAndShowGUI(p);
            }
        });
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		        public void run() {
		        	if(email != null && Email.textMessage != null && !Email.textMessage.toString().equals("")) {
		        		email.sendEmail();
		        	}
		        }
		    }, "Shutdown-thread"));
	}
	
	//If the user decides to not start the GUI, all the process are read from a .txt file
	public void startNoGUI(){
		try {
			String[] userCredentials = askUserCredentials();
			if(User.checkIfExtist(userCredentials[0], userCredentials[1], programSettings.getAdminFile())) {
				if(programSettings.getEnableAdmin()) {
					info = new DataStorage();
					int code = info.loadData(programSettings.getFilePath());
					info.setLoaded(true);
					if(code == -1) {
						throw new IOException("Error loading the data storage, please contact with support");
					}
					else if(code == 1) {
						System.out.println("Storage is empty!");
					}
					user = new Admin(userCredentials[0],userCredentials[1]);
					if(db!= null) {
						Statement update = db.getStatement();
						String userSQL = "INSERT IGNORE INTO USER (USERNAME) VALUES (" + "\'" + user.getUsername() + "\'"  +  ")";
						update.executeUpdate(userSQL);
					}
					
					int error = readQueries();
					if(error == 0){
						Email.textMessage.append("Sucessfully stored");
						info.saveData(programSettings.getFilePath());
						if(email!= null)
							email.sendEmail();
					}
					else
						Log.log(Level.SEVERE,"Error while reading the queries");
				}
				else {
					info = new DataStorage();
					int code = info.loadData(programSettings.getFilePath());
					info.setLoaded(true);
					if(code == -1) {
						throw new IOException("Error loading the data storage, please contact with support");
					}
					else if(code == 1) {
						System.out.println("Storage is empty!");
					}
					user = new User(userCredentials[0],userCredentials[1]);
					if(db!= null) {
						Statement update = db.getStatement();
						String userSQL = "INSERT IGNORE INTO USER (USERNAME) VALUES (" + "\'" + user.getUsername() + "\'"  +  ")";
						update.executeUpdate(userSQL);
					}
					int error = readQueries();
					if(error == 0) {
						Email.textMessage.append("Sucessfully stored");
						info.saveData(programSettings.getFilePath());
						if(email!= null)
							email.sendEmail();
					}
					else
						Log.log(Level.SEVERE,"Error while reading the queries");
					}
			} 
			else {
				Log.log(Level.SEVERE,"The user:" + userCredentials[0] + " does not exist");
				throw new IllegalArgumentException("This User does not exist!");	
			}
		}catch(IOException ex) {
			if(programSettings.getDebug())
				ex.printStackTrace();
			Log.log(Level.SEVERE, "Coulnd't start the process without a gui");
		} catch (SQLException e) {
			Log.log(Level.SEVERE, "Couldn't add the user to the database");
			if(programSettings.getDebug())
				e.printStackTrace();
		}
	}
			
	//Process the home url and retrieves the .js information of the teams and players
	//If the html is already downloaded it, it will use it instead of connecting to the web
	public StringBuilder processURL(String urlPath, String html) {
		StringBuilder page = new StringBuilder();
		String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6";
		try {
			File file = new File(programSettings.getDataLocation()+ File.separator + html);
			if(file.exists()) {
				page = loadDownloadedHtml(file);
				return page;
			}
			URL url = new URL(urlPath);
			URLConnection connect = url.openConnection();
			connect.setRequestProperty("User-Agent", USER_AGENT);
			BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			
			FileWriter fw = new FileWriter(file);
			BufferedWriter bwFile = new BufferedWriter(fw);
			
			String inputLine;
	        while ((inputLine = in.readLine()) != null) {
				bwFile.write(inputLine);
				bwFile.newLine();
				page.append(inputLine);
			}
	        in.close();
	        
			bwFile.close();
			in.close();
		}
		catch (MalformedURLException ex) {
			if(programSettings.getDebug())
				ex.printStackTrace();//Only for Debugging
			Log.log(Level.SEVERE,"Malformed URL" + urlPath);
			page = null;
		}
		catch (IOException ex) {
			if(programSettings.getDebug())
				ex.printStackTrace();//Only for Debugging
			Log.log(Level.SEVERE,"Couldn't write file " + html);
			page = null;
		}
		return page;
	}
	
	//Loads an existing html in the local machine
	private StringBuilder loadDownloadedHtml(File file) {
		StringBuilder page = new StringBuilder();
		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while( ( line = reader.readLine() ) != null ) {
				page.append( line );
		    }
			reader.close();
		} catch (FileNotFoundException e) {
			Log.log(Level.SEVERE,"Couldn't find the file" + file.getName());
			if(programSettings.getDebug())
				e.printStackTrace();//debug
		} catch (IOException e) {
			Log.log(Level.SEVERE,"Couldn't read the file" + file.getName());
			if(programSettings.getDebug())
				e.printStackTrace();//Debug
		}
		return page;
	}

	//If the user decides to enter without a GUI, the program will ask for the credentials
	public String[] askUserCredentials() {
		String[] user = new String[2];
		System.out.println("Enter your username: ");
		Scanner scanner = new Scanner(System.in);
		user[0] = scanner.nextLine();
		System.out.println("Enter your password: ");
		user[1] = scanner.nextLine();
		scanner.close();
		
		return user;
	}
	
	//Read the queries inside the input file
	public int readQueries() {
		int sucess = -1;
		try {
			if(programSettings.getInputFile() == null)
				return -1;
			Scanner sc = new Scanner(programSettings.getInputFile());
			
			while (sc.hasNextLine()) {
				String[] query = sc.nextLine().split(":");
		    	processQuery(query[0], query[1]);
		    }
			sc.close();
			sucess = 0;
		} catch (FileNotFoundException e) {
			Log.log(Level.SEVERE,"Coudln't find the file: " + programSettings.getInputFile());
			if(programSettings.getDebug())
				e.printStackTrace();//Debug
		} catch (SQLException e) {
			Log.log(Level.SEVERE,"SQL Exception: " + e.getMessage());
			if(programSettings.getDebug())
				e.printStackTrace();//Debug
		}
		return sucess;
	}
	
	//Get the hashcode of the user and the data information. It can be either a team or a player
	private int getHashCode(String user, String name) {
		final int prime = 31;
	    int result = 1;
	    result = prime * result + ((user == null) ? 0 : user.hashCode());
	    result = prime * result + ((name == null || name.isEmpty()) ? 0 : name.hashCode());
	    return result;
	}
	
	
	//Process the Player query and stores it in the hashtable or/and database
	public void processPlayerInfo(String value) throws SQLException {
		PlayerInfo player = searchPlayer(value.trim());
		if(player == null) {
			return;
		}
		Data playerData = new Data(user.getUsername());
		playerData.setPlayerInfo(player);
		playerData.setID();
		if(programSettings.getStorageMethod() == "ht") {
			if(programSettings.getOutputFile() != null) {
				playerData.writeOutputFile(programSettings.getOutputFile());
			}
			
			int add = info.addElement(playerData);
			if(add == 0) {
				Log.log(Level.INFO, " Adding " + value);
			}
			else if(add == -2) {
				Log.log(Level.WARNING, " The element is already in the Storage");
			}
			else {
				Log.log(Level.WARNING,"No Data will be added");
			}
			Email.textMessage.append("Sucessfully added Player: " + value + "\r\n");
		}
		else if(programSettings.getStorageMethod() == "db") {
			
			int userID = db.getUserID(playerData.getUser());
			if(userID == -1) {
				//Log.log(Level.SEVERE,"User does not exist in the database!");
				return;
			}
			Statement update = db.getStatement();
			String playerSQL = "INSERT IGNORE INTO PLAYERINFO (PlayerName, PlayerAge, PlayerDOB,"
					+ "PlayerHeight, PlayerWeight, PlayerPoints, PlayerAssists, PlayerRebounds, PlayerPrior,"
					+ "PlayerDraft, PlayerPIE, PlayerTimestamp, User_idUser) VALUES (" + playerData.getPlayerStringSQL() + "," 
					+ "'" + playerData.getTimestamp() + "'" + "," + "'" + userID + "'"+")";
			update.executeUpdate(playerSQL);
			Log.log(Level.INFO, " Adding to Database " + value);
			Email.textMessage.append("Sucessfully added Player to the database: " + value + "\r\n");
		}
		else if(programSettings.getStorageMethod() == "bo") {
			if(programSettings.getOutputFile() != null)
				playerData.writeOutputFile(programSettings.getOutputFile());
			
			int add = info.addElement(playerData);
			if(add == 0) {
				Log.log(Level.INFO, ": Adding " + value);
			}
			else if(add == -2) {
				Log.log(Level.WARNING, ": The element is already in the Storage");
			}
			else {
				Log.log(Level.WARNING,": No Data will be added");
			}
			Email.textMessage.append("Sucessfully added Player: " + value + "\r\n");
			
			int userID = db.getUserID(playerData.getUser());
			if(userID == -1) {
				return;
			}
			Statement update = db.getStatement();
			String playerSQL = "INSERT IGNORE INTO PLAYERINFO (PlayerName, PlayerAge, PlayerDOB,"
					+ "PlayerHeight, PlayerWeight, PlayerPoints, PlayerAssists, PlayerRebounds, PlayerPrior,"
					+ "PlayerDraft, PlayerPIE, PlayerTimestamp, User_idUser) VALUES (" + playerData.getPlayerStringSQL() + "," 
					+ "'" + playerData.getTimestamp() + "'" + "," + "'" + userID + "'"+")";
			update.executeUpdate(playerSQL);
			Log.log(Level.INFO, " Adding to Database " + value);
			Email.textMessage.append("Sucessfully added Player to the database: " + value + "\r\n");
		}
		else {
			throw new IllegalArgumentException("Storage method not recognized!");
		}
	}
	
	//Process the Team query and saves it to the Database or/and hashtable
	public void processTeamInfo(String value) throws SQLException {
		TeamInfo team = searchTeam(value.trim());
		if(team == null) {
			return;
		}
		Data teamData = new Data(user.getUsername());
		teamData.setTeamInfo(team);
		teamData.setID();
		if(programSettings.getStorageMethod() == "ht") {
			if(programSettings.getOutputFile() != null)
				teamData.writeOutputFile(programSettings.getOutputFile());
			int add = info.addElement(teamData);
			if(add == 0) {
				Log.log(Level.INFO, ": Adding " + value);
			}
			else if(add == -2) {
				Log.log(Level.WARNING, ": The element is already in the Storage");
			}
			else {
				Log.log(Level.WARNING,"No Data will be added");
			}
			Email.textMessage.append("Sucessfully added Team: " + value + "\r\n");
		}
		else if(programSettings.getStorageMethod() == "db") {
			int userID = db.getUserID(teamData.getUser());
			
			if(userID == -1) {
				return;
			}
			
			Statement update = db.getStatement();
			String playerSQL = "INSERT IGNORE INTO TEAMINFO (TeamName, TeamPPG, TeamRPG,"
					+ "TeamAPG, TeamOPG, TeamTimestamp, User_idUser) VALUES (" + teamData.getTeamStringSQL() + "," 
					+ "'" + teamData.getTimestamp() + "'" + "," + "'" + userID + "'"+")";
			update.executeUpdate(playerSQL);
			Log.log(Level.INFO, " Adding to Database " + value);
			Email.textMessage.append("Sucessfully added Team to the database: " + value + "\r\n");
		}
		else if(programSettings.getStorageMethod() == "bo") {
			if(programSettings.getOutputFile() != null)
				teamData.writeOutputFile(programSettings.getOutputFile());
			int add = info.addElement(teamData);
			if(add == 0) {
				Log.log(Level.INFO, ": Adding " + value);
			}
			else if(add == -2) {
				Log.log(Level.WARNING, ": The element is already in the Storage");
			}
			else {
				Log.log(Level.WARNING,"No Data will be added");
			}
			Email.textMessage.append("Sucessfully added Player: " + value + "\r\n");
			int userID = db.getUserID(teamData.getUser());
			
			if(userID == -1) {
				Log.log(Level.SEVERE,"User does not exist in the database!");
				Email.textMessage.append("User does not exist in the database");
				return;
			}
			
			Statement update = db.getStatement();
			String playerSQL = "INSERT IGNORE INTO TEAMINFO (TeamName, TeamPPG, TeamRPG,"
					+ "TeamAPG, TeamOPG, TeamTimestamp, User_idUser) VALUES (" + teamData.getTeamStringSQL() + "," 
					+ "'" + teamData.getTimestamp() + "'" + "," + "'" + userID + "'"+")";
			update.executeUpdate(playerSQL);
			Log.log(Level.INFO, " Adding to Database " + value);
			Email.textMessage.append("Sucessfully added Player to the database: " + value + "\r\n");
		}
		else {
			throw new IllegalArgumentException("Storage method not recognized!");
		}
	}
	
	//Deletes an entry in the database or/and hashtable
	public void processDelete(String value) throws SQLException {
		if(programSettings.getStorageMethod() == "ht") {
			int hashed = getHashCode(user.getUsername(),value.trim());
			int delete = info.deleteElement(hashed);
			if(delete == 0) {
				Log.log(Level.INFO,"the value: " + value.trim() + " was deleted succesfully for user " + user.getUsername());
				Email.textMessage.append("Sucessfully deleted value: " + value + "\r\n");
			}
			else {
				Log.log(Level.WARNING,"No value deleted because the value: " + value.trim() + " does not exist for the user " + user.getUsername());
			}	
		}else if(programSettings.getStorageMethod() == "db") {
			int userID = db.getUserID(user.getUsername());
			if(userID == -1) {
				return;
			}
			
			Statement search = db.getStatement();
			String sql = "Select IDPLAYERINFO FROM PlayerInfo WHERE User_idUser = " + userID + " AND PlayerName = '" + value.trim() +"'";
			ResultSet rs = search.executeQuery(sql);
			if(rs.next()) {
				int playerID = rs.getInt("IDPLAYERINFO");
				Statement delete = db.getStatement();
				String deleteSQL = "DELETE FROM PLAYERINFO WHERE IDPLAYERINFO = " + playerID;
				delete.executeUpdate(deleteSQL);
				rs.close();
				Log.log(Level.FINE,"the value: " + value.trim() + " was deleted in the database succesfully for user " + user.getUsername());
				Email.textMessage.append("Sucessfully deleted value from the database: " + value + "\r\n");
			}
			else {
				rs.close();
				sql = "Select IDTEAMINFO FROM TEAMINFO WHERE User_idUser = " + userID + " AND TEAMNAME = '" + value.trim() + "'";
				rs = search.executeQuery(sql);
				if(rs.next()) {
					int teamID = rs.getInt("IDTEAMINFO");
					Statement delete = db.getStatement();
					String deleteSQL = "DELETE FROM TEAMINFO WHERE IDTEAMINFO = " + teamID;
					delete.executeUpdate(deleteSQL);
					Log.log(Level.FINE,"the value: " + value.trim() + " was succesfully deleted in the database for user " + user.getUsername());
					Email.textMessage.append("Sucessfully deleted value from the database: " + value + "\r\n");
				}
				else {
					rs.close();
					Log.log(Level.WARNING,"the value: " + value.trim() + " does not exist in the database for user " + user.getUsername());
				}
			}
			
		}else if(programSettings.getStorageMethod() == "bo") {
			int hashed = getHashCode(user.getUsername(),value.trim());
			int deleteHT = info.deleteElement(hashed);
			if(deleteHT == 0) {
				Log.log(Level.INFO,"the value: " + value.trim() + " was deleted succesfully for user " + user.getUsername());
				Email.textMessage.append("Sucessfully deleted value: " + value + "\r\n");
			}
			else {
				Log.log(Level.WARNING,"No value deleted because the value: " + value.trim() + " does not exist for the user " + user.getUsername());
			}	
			
			int userID = db.getUserID(user.getUsername());
			if(userID == -1) {
				return;
			}
			
			Statement search = db.getStatement();
			String sql = "Select IDPLAYERINFO FROM PlayerInfo WHERE User_idUser = " + userID + " AND PlayerName = '" + value.trim() + "'";
			ResultSet rs = search.executeQuery(sql);
			if(rs.next()) {
				int playerID = rs.getInt("IDPLAYERINFO");
				Statement delete = db.getStatement();
				String deleteSQL = "DELETE FROM PLAYERINFO WHERE IDPLAYERINFO = " + playerID;
				delete.executeUpdate(deleteSQL);
				rs.close();
				Log.log(Level.FINE,"the value: " + value.trim() + " was succesfully deleted in the database for user " + user.getUsername());
				Email.textMessage.append("Sucessfully value Player from the database: " + value + "\r\n");
			}
			else {
				rs.close();
				sql = "Select IDTEAMINFO FROM TEAMINFO WHERE User_idUser = " + userID + " AND TEAMNAME = '" + value.trim() + "'";
				rs = search.executeQuery(sql);
				if(rs.next()) {
					int teamID = rs.getInt("IDTEAMINFO");
					Statement delete = db.getStatement();
					String deleteSQL = "DELETE FROM TEAMINFO WHERE IDTEAMINFO = " + teamID;
					delete.executeUpdate(deleteSQL);
					Log.log(Level.FINE,"the value: " + value.trim() + " was succesfully deleted in the database for user " + user.getUsername());
					Email.textMessage.append("Sucessfully deleted value from the database: " + value + "\r\n");
				}
				else {
					rs.close();
					Log.log(Level.WARNING,"the value: " + value.trim() + " does not exist in the database for user " + user.getUsername());
				}
			}
			
		}else {
			throw new IllegalArgumentException("Storage method not recognized!");
		}
	}
	
	//Modifies an entry from the database and/or hashtable
	public void processModify(String value) throws SQLException{
		if(programSettings.getStorageMethod() == "ht") {
			String[] changes = value.trim().split("-");
			if(changes.length != 2) {
				Log.log(Level.WARNING,"Please enter player/team name and the modification of the form Player/Team - modification data");
				return;
			}
			String name = changes[0];
			String[] valuesToChange = changes[1].trim().split(" ");
			if(valuesToChange.length%2 != 0) {
				Log.log(Level.WARNING,"Please enter the correct number of modification of the form: \"Modification\" \"Data\" for the value: " + name);
				return;
			}
			int hash = getHashCode(user.getUsername(),name.trim());
			int modify = info.modifyElement(hash, valuesToChange);
			if(modify == 0) {
				Log.log(Level.INFO,"the value: " + value.trim() + " was succesfully modified for user " + user.getUsername());
				Email.textMessage.append("Sucessfully modified value: " + value + "\r\n");
			}
			else {
				Log.log(Level.WARNING,"No value modified because the value: " + value.trim() + " does not exist for the user " + user.getUsername());
			}
		}
		else if(programSettings.getStorageMethod() == "db") {
			String[] changes = value.trim().split("-");
			if(changes.length != 2) {
				Log.log(Level.WARNING,"Please enter player/team name and the modification of the form Player/Team - modification data");
				return;
			}
			String name = changes[0];
			String[] valuesToChange = changes[1].trim().split(" ");
			if(valuesToChange.length%2 != 0) {
				Log.log(Level.WARNING,"Please enter the correct number of modification of the form: \"Modification\" \"Data\" for the value: " + name);
				return;
			}
			
			int userID = db.getUserID(user.getUsername());
			if(userID == -1) {
				return;
			}
			
			Statement search = db.getStatement();
			String sql = "Select IDPLAYERINFO FROM PlayerInfo WHERE User_idUser = " + userID + " AND PlayerName = '" + name.trim() + "'";
			ResultSet rs = search.executeQuery(sql);
			Statement modify = db.getStatement();
			
			if(rs.next()) {
				int playerID = rs.getInt("IDPLAYERINFO");
				for(int i = 0; i < valuesToChange.length; i+=2) {
					switch(valuesToChange[i].trim().toLowerCase()) {
						case("height"):
							sql = "UPDATE PlayerInfo SET PlayerHeight = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modify.executeUpdate(sql);
							break;
						case("weight"):
							sql = "UPDATE PlayerInfo SET PlayerWeight = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modify.executeUpdate(sql);
							break;
						case("age"):
							sql = "UPDATE PlayerInfo SET PlayerAge = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modify.executeUpdate(sql);
							break;
						case("dob"):
							sql = "UPDATE PlayerInfo SET PlayerDOB = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modify.executeUpdate(sql);
							break;
						case("prior"):
							sql = "UPDATE PlayerInfo SET PlayerPrior = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modify.executeUpdate(sql);
							break;
						case("draft"):
							sql = "UPDATE PlayerInfo SET PlayerDraft = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modify.executeUpdate(sql);
							break;
						case("points"):
							sql = "UPDATE PlayerInfo SET PlayerPoints = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modify.executeUpdate(sql);
							break;
						case("rebounds"):
							sql = "UPDATE PlayerInfo SET PlayerRebounds = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modify.executeUpdate(sql);
							break;
						case("assists"):
							sql = "UPDATE PlayerInfo SET PlayerAssists = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modify.executeUpdate(sql);
							break;
						case("pie"):
							sql = "UPDATE PlayerInfo SET PlayerPIE = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modify.executeUpdate(sql);
							break;
						default:
							Log.log(Level.WARNING,"Modification not recognized");
					}
				}
				rs.close();
				Log.log(Level.FINE,"the value: " + value.trim() + " was succesfully modified in the database for user " + user.getUsername());
				Email.textMessage.append("Sucessfully modified value in the database: " + value + "\r\n");
			}
			else {
				rs.close();
				sql = "Select IDTEAMINFO FROM TEAMINFO WHERE User_idUser = " + userID + " AND TEAMNAME = '" + name.trim() + "'";
				rs = search.executeQuery(sql);
				if(rs.next()) {
					int teamID = rs.getInt("IDTEAMINFO");
					for(int i = 0; i < valuesToChange.length; i+=2) {
						switch(valuesToChange[i].trim().toLowerCase()) {
						case("ppg"):
							sql = "UPDATE TeamInfo SET TeamPPG = '" + valuesToChange[i+1] + "' WHERE IDTEAMINFO = " + teamID;
							modify.executeUpdate(sql);
							break;
						case("rpg"):
							sql = "UPDATE TeamInfo SET TeamRPG = '" + valuesToChange[i+1] + "' WHERE IDTEAMINFO = " + teamID;
							modify.executeUpdate(sql);
							break;
						case("apg"):
							sql = "UPDATE TeamInfo SET TeamAPG = '" + valuesToChange[i+1] + "' WHERE IDTEAMINFO = " + teamID;
							modify.executeUpdate(sql);
							break;
						case("opg"):
							sql = "UPDATE TeamInfo SET TeamOPG = '" + valuesToChange[i+1] + "' WHERE IDTEAMINFO = " + teamID;
							modify.executeUpdate(sql);
							break;
						default:
							System.out.println("Modification not recognized");
						}
					}
					rs.close();
					Log.log(Level.FINE,"the value: " + value.trim() + " was succesfully modified in the database for user " + user.getUsername());
					Email.textMessage.append("Sucessfully modified value in the database: " + value + "\r\n");
				}
				else {
					rs.close();
					Log.log(Level.WARNING,"the value: " + value.trim() + " does not exist in the database for user " + user.getUsername());
				}
			}
		}
		else if(programSettings.getStorageMethod() == "bo") {
			String[] changes = value.trim().split("-");
			if(changes.length != 2) {
				Log.log(Level.WARNING,"Please enter player/team name and the modification of the form Player/Team - modification data");
				return;
			}
			String name = changes[0];
			String[] valuesToChange = changes[1].trim().split(" ");
			if(valuesToChange.length%2 != 0) {
				Log.log(Level.WARNING,"Please enter the correct number of modification of the form: \"Modification\" \"Data\" for the value: " + name);
				return;
			}
			int hash = getHashCode(user.getUsername(),name.trim());
			int modify = info.modifyElement(hash, valuesToChange);
			if(modify == 0) {
				Log.log(Level.INFO,"the value: " + value.trim() + " was succesfully modified for user " + user.getUsername());
				Email.textMessage.append("Sucessfully modified value: " + value + "\r\n");
			}
			else {
				Log.log(Level.WARNING,"No value modified because the value: " + value.trim() + " does not exist for the user " + user.getUsername());
			}
			
			int userID = db.getUserID(user.getUsername());
			if(userID == -1) {
				return;
			}
			
			Statement search = db.getStatement();
			String sql = "Select IDPLAYERINFO FROM PlayerInfo WHERE User_idUser = " + userID + " AND PlayerName = '" + name.trim() + "'";
			ResultSet rs = search.executeQuery(sql);
			Statement modifyBO = db.getStatement();
			
			if(rs.next()) {
				int playerID = rs.getInt("IDPLAYERINFO");
				for(int i = 0; i < valuesToChange.length; i+=2) {
					switch(valuesToChange[i].trim().toLowerCase()) {
						case("height"):
							sql = "UPDATE PlayerInfo SET PlayerHeight = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modifyBO.executeUpdate(sql);
							break;
						case("weight"):
							sql = "UPDATE PlayerInfo SET PlayerWeight = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modifyBO.executeUpdate(sql);
							break;
						case("age"):
							sql = "UPDATE PlayerInfo SET PlayerAge = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modifyBO.executeUpdate(sql);
							break;
						case("dob"):
							sql = "UPDATE PlayerInfo SET PlayerDOB = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modifyBO.executeUpdate(sql);
							break;
						case("prior"):
							sql = "UPDATE PlayerInfo SET PlayerPrior = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modifyBO.executeUpdate(sql);
							break;
						case("draft"):
							sql = "UPDATE PlayerInfo SET PlayerDraft = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modifyBO.executeUpdate(sql);
							break;
						case("points"):
							sql = "UPDATE PlayerInfo SET PlayerPoints = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modifyBO.executeUpdate(sql);
							break;
						case("rebounds"):
							sql = "UPDATE PlayerInfo SET PlayerRebounds = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modifyBO.executeUpdate(sql);
							break;
						case("assists"):
							sql = "UPDATE PlayerInfo SET PlayerAssists = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modifyBO.executeUpdate(sql);
							break;
						case("pie"):
							sql = "UPDATE PlayerInfo SET PlayerPIE = '" + valuesToChange[i+1] + "' WHERE IDPLAYERINFO = " + playerID;
							modifyBO.executeUpdate(sql);
							break;
						default:
							System.out.println("Modification not recognized");
					}
				}
				rs.close();
				Log.log(Level.FINE,"the value: " + value.trim() + " was succesfully modified in the database for user " + user.getUsername());
				Email.textMessage.append("Sucessfully modified value in the database: " + value + "\r\n");
			}
			else {
				rs.close();
				sql = "Select IDTEAMINFO FROM TEAMINFO WHERE User_idUser = " + userID + " AND TEAMNAME = '" + name.trim() + "'";
				rs = search.executeQuery(sql);
				if(rs.next()) {
					int teamID = rs.getInt("IDTEAMINFO");
					for(int i = 0; i < valuesToChange.length; i+=2) {
						switch(valuesToChange[i].trim().toLowerCase()) {
						case("ppg"):
							sql = "UPDATE TeamInfo SET TeamPPG = '" + valuesToChange[i+1] + "' WHERE IDTEAM	INFO = " + teamID;
							modifyBO.executeUpdate(sql);
							break;
						case("rpg"):
							sql = "UPDATE TeamInfo SET TeamRPG = '" + valuesToChange[i+1] + "' WHERE IDTEAM	INFO = " + teamID;
							modifyBO.executeUpdate(sql);
							break;
						case("apg"):
							sql = "UPDATE TeamInfo SET TeamAPG = '" + valuesToChange[i+1] + "' WHERE IDTEAM	INFO = " + teamID;
							modifyBO.executeUpdate(sql);
							break;
						case("opg"):
							sql = "UPDATE TeamInfo SET TeamOPG = '" + valuesToChange[i+1] + "' WHERE IDTEAM	INFO = " + teamID;
							modifyBO.executeUpdate(sql);
							break;
						default:
							System.out.println("Modification not recognized");
						}
					}
					rs.close();
					Log.log(Level.FINE,"the value: " + value.trim() + " was succesfully modified in the database for user " + user.getUsername());
					Email.textMessage.append("Sucessfully modified value in the database: " + value + "\r\n");
				}
				else {
					rs.close();
					Log.log(Level.WARNING,"the value: " + value.trim() + " does not exist in the database for user " + user.getUsername());
				}
			}
		}
		else {
			Log.log(Level.SEVERE,"Storage method not recognized!");
			throw new IllegalArgumentException("Storage method not recognized!");
		}
	}
	
	//Process the Query sequentially
	public void processQuery(String key, String value) throws SQLException {
		switch(key.trim().toLowerCase()) {
			case("player info"):
				processPlayerInfo(value.trim());
				break;
			case("team info"):
				processTeamInfo(value.trim());
				break;
			case("delete"):
				processDelete(value.trim());
				break;
			case("modify"):
				processModify(value.trim());
				break;
		}
	}
	
	//Gets the player id from the .js file that contains the values of the players/teams
	public String getPlayerID(String str, StringBuilder d) {
		String id = "";
		String[] names = str.trim().split(" ");
		if(names.length != 2) {
			return id;
		}
		String format = names[1] + ", "  + names[0];
		Pattern p = Pattern.compile("([0-9]{4,7})(,\")"+format);
		Matcher m = p.matcher(d.toString());
		if (m.find()) {
			Log.log(Level.FINEST,"ID found for player: " + str);
	      id = m.group(1);
	    }
		return id;
	}
	
	//Gets the team id from the .js file that contains the values of the players/teams
	public String getTeamID(String str, StringBuilder d) {
		String id = "";
		String[] names = str.trim().split(" ");
		String format;
		if(names.length == 2) {
			format = "\"" +names[0] + "\",\""  + names[1] + "\"";
		}
		else if(names.length == 3){
			format = "\"" +names[0] + " "  + names[1] + "\"," + "\"" + names[2] + "\"";
		}else {
			return id;
		}
		
		Pattern p = Pattern.compile("([0-9]{10})(\",\"[A-Z]{3}\",\")" + names[names.length-1].trim().toLowerCase()+ "\"," + format);
		Matcher m = p.matcher(d.toString());
		if (m.find()) {
	      Log.log(Level.FINEST,"ID found for team: " + str);
	      id = m.group(1);
	    }
		return id;
	}
	
	//Gets the .js file containing the player/teams id that it is used to build the html for the player/team
	public StringBuilder getScript(String script) {
		StringBuilder data = new StringBuilder();
		String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6";
		try {
			URL url = new URL(script);
			URLConnection connect = url.openConnection();
			connect.setRequestProperty("User-Agent", USER_AGENT);
			BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
				
			String inputLine;
	        while ((inputLine = in.readLine()) != null) {
				data.append(inputLine);
			}
	        in.close();
		}
		catch (MalformedURLException ex) {
			if(programSettings.getDebug())
				ex.printStackTrace();
			Log.log(Level.SEVERE,"Malformed URL" + script);
			data = null;
		} catch (IOException e) {
			Log.log(Level.SEVERE,"Couldn't access the URL" + script);
			data = null;
			if(programSettings.getDebug())
				e.printStackTrace();
		}
		return data;
	}
	
	//Searchs the player in the .js file and then retrieves the html file and gets the information
	public PlayerInfo searchPlayer(String str) {
		String path;
		PlayerInfo player = new PlayerInfo(str);
		if(script == null || script.toString().isEmpty()) {
			path = programSettings.getUrl() + "js/data/ptsd/stats_ptsd.js";
			script = processURL(path, "stats.txt");
		}
			
		String playerURL = getPlayerID(str,script);
		if(playerURL.equals("")) {
			Log.log(Level.WARNING,"Player is not available, try another name or check the spelling: " + str);
			return null;
		}
		
		path = programSettings.getUrl() + "/player/" + playerURL;
		String info = processURL(path, str + ".html").toString();
		
		player.getInfo(info);
		String url = player.getPicture(info);
		String image = DownloadImage(url, str);
		if(!image.isEmpty()) {
			player.setPicture(image);
			Log.log(Level.FINE,"Image Downloaded " + str);
		}else
			Log.log(Level.WARNING,"Cannot download the image " + str);
		return player;
	}
	
	//Downloads the Player profile picture in .png format
	private String DownloadImage(String u, String fn) {
		String imageOutput = "";
		String extension = u.substring(u.lastIndexOf(".")+1);
		try {
			URL url = new URL(u);
			imageOutput = programSettings.getDataLocation() + File.separator + fn.trim() + "." + extension;
			File file = new File(imageOutput);
			File local = new File(fn.trim() + "." + extension);
			if(file.exists()) {
				return imageOutput;
			}
			BufferedImage image = null;
			image = ImageIO.read(url);
	
			System.out.println("Downloading image: " + fn);
			Log.log(Level.FINEST,"Downloading image: " + fn);
			ImageIO.write(image, extension, file);
			ImageIO.write(image, extension, local);
		} catch (IOException ex) {
			if(programSettings.getDebug())
				ex.printStackTrace();
			Log.log(Level.SEVERE,"Failed to download image: " + fn.trim());
		}
		return imageOutput;
	}

	//Same as search player
	public TeamInfo searchTeam(String str) {
		String path;
		TeamInfo team = new TeamInfo(str);
		if(script == null || script.toString().isEmpty()) {
			path = programSettings.getUrl() + "js/data/ptsd/stats_ptsd.js";
			script = processURL(path, "stats.txt");
		}
			
		String teamURL = getTeamID(str,script);
		if(teamURL.equals("")) {
			Log.log(Level.WARNING,"Team is not available, try another name or check the spelling: " + str);
			return null;
		}
		
		path = programSettings.getUrl() + "/team/" + teamURL;
		String info = processURL(path, str + ".html").toString();
		
		team.getInfo(info);
		return team;
	}
}
