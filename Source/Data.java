import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public class Data {
	
	private int id;
	private String user;
	private String timestamp;
	private PlayerInfo player;
	private TeamInfo team;
	
	public Data(String u) {
		user = u;
		timestamp = String.valueOf(java.time.LocalDateTime.now());
	}
	@Override
	public int hashCode() {
		final int prime = 31;
	    int result = 1;
	    result = prime * result + ((user == null) ? 0 : user.hashCode());
	    if(player != null) {
		    result = prime * result + ((player.getName() == null || player.getName().isEmpty()) ? 0 : player.getName().hashCode());
	    }
	    else {
	    	result = prime * result + ((team.getName() == null || team.getName().isEmpty()) ? 0 : team.getName().hashCode());
	    }
	    return result;
	}
	
	//A value is equal if the player or the team information is equal. Also, the user has to be the same
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (!(obj instanceof Data))
	        return false;
	    if (obj == this)
	        return true;
	    
	    return this.getUser() == ((Data) obj).getUser() &&
	    		((this.player != null && ((Data) obj).player != null && this.player.getName() == ((Data) obj).player.getName())
	    				|| (this.team != null && ((Data) obj).team != null && this.team.getName() == ((Data) obj).team.getName()));
	}
	
	public void setTimestamp(String time) {
		this.timestamp = time;
	}
	
	public void setPlayerInfo(PlayerInfo p) {
		this.player = p;
	}
	
	public void setTeamInfo(TeamInfo t) {
		this.team = t;
	}
	
	public void setID() {
		this.id = hashCode();
	}
	
	public String getPlayerName() {
		if(player != null) {
			return player.getName();
		}
		else
			return "";
	}
	
	public String getTeamName() {
		if(team != null) {
			return team.getName();
		}
		else
			return "";
	}
	
	public PlayerInfo getPlayerInfo() {
		return this.player;
	}
	
	public TeamInfo getTeamInfo() {
		return this.team;
	}
	
	//Creates SQL strings to use with the database
	public String getPlayerStringSQL() {
		if(player != null)
			return "'"  + player.getName() + "'"  + ","+
			"'"  + player.getAge() + "'"  + "," +
			"'"  + player.getDOB() + "'"  + "," +
			"'"  + player.getHeight() + "'"  + "," +
			"'"  + player.getWeight() + "'"  + "," +
			"'"  + player.getPoints() + "'"  + "," +
			"'"  + player.getAssists() + "'"  + "," +
			"'"  + player.getRebounds() + "'"  + "," +
			"'"  + player.getPrior() + "'"  + "," +
			"'"  + player.getDraft() + "'"  + "," +
			"'"  + player.getPIE() + "'" ;
		else
			return "";
	}
	
	public String getPlayerString() {
		if(player != null)
			return player.getName() + ","+
					player.getAge() + "," +
					player.getDOB() + "," +
					player.getHeight() + "," +
					player.getWeight() + "," +
					player.getPoints() + "," +
					player.getAssists() + "," +
					player.getRebounds() + "," +
					player.getPrior() + "," +
					player.getDraft() + "," +
					player.getPIE();
		else
			return "";
	}
	
	public String getTeamStringSQL() {
		if(team != null)
			return "'"  +  team.getName() + "'"  + ","+
			"'"  + team.getPPG() + "'"  + "," +
			"'"  + team.getRPG() + "'"  + "," +
			"'"  + team.getAPG() + "'"  + "," +
			"'"  + team.getOPG() + "'";
		else
			return "";
	}
	
	public String getTeamString() {
		if(team != null)
			return team.getName() + ","+
					team.getPPG() + "," +
					team.getRPG() + "," +
					team.getAPG() + "," +
					team.getOPG();
		else
			return "";
	}
	
	public String getUser() {
		return this.user;
	}
	
	public String getTimestamp() {
		return this.timestamp;
	}
	
	public int getID() {
		return this.id;
	}
	
	//Write to the output file, if it doesn't exist, it creates one in append mode
	public void writeOutputFile(File outputFile) {
		FileWriter fw = null;
		try {
			if(outputFile == null) {
				throw new IOException("No output file");
			}
			
			fw = new FileWriter(outputFile, true);
			BufferedWriter writer = new BufferedWriter(fw);
			if(team != null) {
				String[] stats = getTeamString().split(",");
				writer.write("Team Information:");
				writer.newLine();
				writer.write("Team Name: " + stats[0]);
				writer.newLine();
				writer.write("Team PPG: " + stats[1]);
				writer.newLine();
				writer.write("Team RPG: " + stats[2]);
				writer.newLine();
				writer.write("Team APG: " + stats[3]);
				writer.newLine();
				writer.write("Team OPG: " + stats[4]);
				writer.newLine();
				writer.write("------------------------------------------------");
				writer.newLine();
				writer.close();
			}
			else if(player != null) {
				String[] stats = getPlayerString().split(",");
				writer.write("Player Information:");
				writer.newLine();
				writer.write("Player Name: " + stats[0]);
				writer.newLine();
				writer.write("Player Age: " + stats[1]);
				writer.newLine();
				writer.write("Player DOB: " + stats[2]);
				writer.newLine();
				writer.write("Player Height: " + stats[3]);
				writer.newLine();
				writer.write("Player Weight: " + stats[4]);
				writer.newLine();
				writer.write("Player Points: " + stats[5]);
				writer.newLine();
				writer.write("Player Assists: " + stats[6]);
				writer.newLine();
				writer.write("Player Rebounds: " + stats[7]);
				writer.newLine();
				writer.write("Player Prior: " + stats[8]);
				writer.newLine();
				writer.write("Player Draft: " + stats[9]);
				writer.newLine();
				writer.write("Player PIE: " + stats[10]);
				writer.newLine();
				writer.write("------------------------------------------------");
				writer.newLine();
				writer.close();
			}
			else {
				writer.close();
				Log.log(Level.INFO,"No Information to save");
		
			}
			Log.log(Level.INFO,"Data was saved to the output file");
		}
		catch(IOException ex) {
			Log.log(Level.SEVERE, "Couldn't write to the output file " + outputFile.getName());
		}
	}

}
