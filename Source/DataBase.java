import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;

public class DataBase {

	private Connection conn;
	private Statement  statement;
	private final String jdbcDriver = "com.mysql.cj.jdbc.Driver";
	private final String dbAddress = "jdbc:mysql://localhost:3306/StatsNBA?createDatabaseIfNotExist=true";
	
	
	//Creates a Database and tables if these do not exist
	public DataBase() throws SQLException {
		String[] userCredentials = askDBCredentials();
		try {
	        Class.forName(jdbcDriver);
	        
	        Connection con = DriverManager.getConnection(dbAddress, userCredentials[0], userCredentials[1]);
	        
    		String table1 = ""
    		+ "CREATE TABLE IF NOT EXISTS `user` ( "
    		+ "  `idUser` int(11) NOT NULL AUTO_INCREMENT, "
    		+ "  `UserName` varchar(100) NOT NULL, "
    		+ "  PRIMARY KEY (`idUser`), "
    		+ "  UNIQUE KEY `idUser_UNIQUE` (`idUser`), "
    		+ "  UNIQUE KEY `UserName_UNIQUE` (`UserName`) "
    		+ ") ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;";
	        
	        String table2 = ""
	        		+ "CREATE TABLE IF NOT EXISTS `playerinfo` ( "
	        		+ "  `idPlayerInfo` int(11) NOT NULL AUTO_INCREMENT, "
	        		+ "  `PlayerName` varchar(100) NOT NULL, "
	        		+ "  `PlayerAge` int(11) NOT NULL, "
	        		+ "  `PlayerDOB` varchar(10) NOT NULL, "
	        		+ "  `PlayerHeight` varchar(5) NOT NULL, "
	        		+ "  `PlayerWeight` varchar(5) NOT NULL, "
	        		+ "  `PlayerPoints` decimal(2,1) NOT NULL, "
	        		+ "  `PlayerAssists` decimal(2,1) NOT NULL, "
	        		+ "  `PlayerRebounds` decimal(2,1) NOT NULL, "
	        		+ "  `PlayerPrior` varchar(100) NOT NULL, "
	        		+ "  `PlayerDraft` varchar(100) NOT NULL, "
	        		+ "  `PlayerPIE` decimal(2,1) NOT NULL, "
	        		+ "  `PlayerTimestamp` varchar(45) NOT NULL, "
	        		+ "  `User_idUser` int(11) NOT NULL, "
	        		+ "  PRIMARY KEY (`idPlayerInfo`,`User_idUser`), "
	        		+ "  UNIQUE KEY `idPlayerInfo_UNIQUE` (`idPlayerInfo`), "
	        		+ "  UNIQUE KEY `index4` (`PlayerName`,`User_idUser`), "
	        		+ "  KEY `fk_PlayerInfo_User_idx` (`User_idUser`), "
	        		+ "  CONSTRAINT `fk_PlayerInfo_User` FOREIGN KEY (`User_idUser`) REFERENCES `user` (`iduser`) "
	        		+ ") ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;";


	        String table3 = ""
	        		+ "CREATE TABLE IF NOT EXISTS `teaminfo`( "
	        		+ "  `idTeamInfo` int(11) NOT NULL AUTO_INCREMENT, "
	        		+ "  `TeamName` varchar(100) NOT NULL, "
	        		+ "  `TeamPPG` varchar(5) NOT NULL, "
	        		+ "  `TeamRPG` varchar(5) NOT NULL, "
	        		+ "  `TeamAPG` varchar(5) NOT NULL, "
	        		+ "  `TeamOPG` varchar(5) NOT NULL, "
	        		+ "  `TeamTimestamp` varchar(45) NOT NULL, "
	        		+ "  `User_idUser` int(11) NOT NULL, "
	        		+ "  PRIMARY KEY (`idTeamInfo`,`User_idUser`), "
	        		+ "  UNIQUE KEY `idTeamInfo_UNIQUE` (`idTeamInfo`), "
	        		+ "  UNIQUE KEY `index4` (`TeamName`,`User_idUser`), "
	        		+ "  KEY `fk_TeamInfo_User1_idx` (`User_idUser`), "
	        		+ "  CONSTRAINT `fk_TeamInfo_User1` FOREIGN KEY (`User_idUser`) REFERENCES `user` (`iduser`) "
	        		+ ") ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;";

	        Statement stmt = con.createStatement();
	        stmt.execute(table1);
	        stmt.execute(table2);
	        stmt.execute(table3);
	        con.close();
	    } 

	    catch (ClassNotFoundException e) {
	        Log.log(Level.SEVERE,"Class not found for jdbcDriver");
	    } 
	    catch (SQLException e) {
	    	//e.printStackTrace();
	        Log.log(Level.SEVERE,"Wrong sql statement");
	    }
		
		conn =  DriverManager.getConnection(dbAddress,  userCredentials[0], userCredentials[1]);

		statement = conn.createStatement();
	}
	
	//Gets the user ID from the table users
	public int getUserID(String name) {
		int id = -1;
		Statement getID;
		try {
			getID = conn.createStatement();
			ResultSet rs = getID.executeQuery("SELECT IDUSER FROM USER WHERE USER.USERNAME = " + "'" + name + "'");
			if(rs.next()) id = rs.getInt("idUser");
			rs.close();
		} catch (SQLException e) {
			Log.log(Level.SEVERE,"Couldn't get user id by name: " + name);
			e.printStackTrace();
		}
		return id;
	}
	
	//Gets the user name from the table users given an ID
	public String getUserName(int userID) {
		String name = "";
		Statement getID;
		try {
			getID = conn.createStatement();
			ResultSet rs = getID.executeQuery("SELECT USERNAME FROM USER WHERE USER.USERNAME = " + userID);
			rs.next();
			name = rs.getString("USERNAME");
			rs.close();
		} catch (SQLException e) {
			Log.log(Level.SEVERE,"Couldn't get user name by ID" + userID);
			e.printStackTrace();
		}
		return name;
	}
	
	public Statement getStatement() {
		return statement;
	}
	
	
	//Asks the user for the Database credentials
	private String[] askDBCredentials() {
		String[] user = new String[2];
		System.out.println("Enter the username for the database: ");
		Scanner sc = new Scanner(System.in);
		user[0] = sc.nextLine();
		System.out.println("Enter the password for the database: ");
		user[1] = sc.nextLine();
		
		return user;
	}
	
}
