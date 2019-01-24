import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;

public class DataStorage {
	
	private Hashtable<Integer, Data> content;
	private boolean loaded = false;
	
	public DataStorage() {
		content = new Hashtable<Integer, Data>();
	}
	
	//If the DataStorage is not loaded, this indicates that do not write to the csv file
	//so, it will avoid writing empty
	public void setLoaded(boolean bol) {
		loaded = bol;
	}
	
	public Hashtable<Integer,Data> getContent(){
		return content;
	}
	
	//Adds a Data object to the HashTable if the user and player name is not alread in it
	public int addElement(Data elem) {
		int success = -1;
		if(!content.containsKey(elem.getID())) {
			if(!elem.getPlayerName().isEmpty()) {
				content.put(elem.getID(), elem);
				success = 0;
			}
			else if(!elem.getTeamName().isEmpty()){
				content.put(elem.getID(), elem);
				success = 0;
			}
			else {
				success = -1;
			}
		}
		else {
			success = -2;
			System.out.println("The element is already in the Storage");//debug
		}
		return success;
	}
	
	
	//checks if the element id is in the hashtable and deletes it
	public int deleteElement(int id) {
		int success = -1;
		if(content.containsKey(id)) {
			content.remove(id);
			success = 0;
		}
		else {
			System.out.println("The Data storage does not contain the value");//debug
		}
		return success;
	}
	
	//Gets a Data object from the hashtable, given the id
	public Data getElement(int id) {
		if(content.containsKey(id)) {
			return content.get(id);
		}
		else
			return null;
	}
	
	//Checks if the data is a Player or a Team, then proceeds to modify it
	public int modifyElement(int id, String[] values) {
		//content.replace(id, newValue);
		int success = -1;
		Data old = getElement(id);
		if(old == null) {
			return -1;
		}
		if(old.getPlayerInfo() != null) {
			PlayerInfo newPlayer = old.getPlayerInfo();
			for(int i = 0; i < values.length; i+=2) {
				switch(values[i].trim().toLowerCase()) {
					case("height"):
						newPlayer.setHeight(values[i+1]);
						break;
					case("weight"):
						newPlayer.setWeight(values[i+1]);
						break;
					case("age"):
						newPlayer.setAge(values[i+1]);
						break;
					case("dob"):
						newPlayer.setDOB(values[i+1]);
						break;
					case("prior"):
						newPlayer.setPrior(values[i+1]);
						break;
					case("draft"):
						newPlayer.setDraft(values[i+1]);
						break;
					case("points"):
						newPlayer.setPoints(values[i+1]);
						break;
					case("rebounds"):
						newPlayer.setRebounds(values[i+1]);
						break;
					case("assists"):
						newPlayer.setAssists(values[i+1]);
						break;
					case("pie"):
						newPlayer.setPIE(values[i+1]);
						break;
					default:
						System.out.println("Modification not recognized");
				}
			}
			Data newValue = new Data(old.getUser());
			newValue.setPlayerInfo(newPlayer);
			newValue.setID();
			content.replace(id, newValue);
			success = 0;
			//player = new PlayerInfo(old.getPlayerName());
		}
		else if (old.getTeamInfo() != null){
			TeamInfo newTeam = old.getTeamInfo();
			for(int i = 0; i < values.length; i+=2) {
				switch(values[i].trim().toLowerCase()) {
					case("ppg"):
						newTeam.setPPG(values[i+1]);
						break;
					case("rpg"):
						newTeam.setRPG(values[i+1]);
						break;
					case("apg"):
						newTeam.setAPG(values[i+1]);
						break;
					case("opg"):
						newTeam.setOPG(values[i+1]);
						break;
					default:
						System.out.println("Modification not recognized");
				}
			}
			Data newValue = new Data(old.getUser());
			newValue.setTeamInfo(newTeam);
			newValue.setID();
			content.replace(id, newValue);
			success = 0;
		}
		else {
			return -1;
		}
		return success;
	}
	
	
	//Saves the data to the data.csv
	public void saveData(String filePath) {
		
		FileWriter writer = null;
		if(!loaded) return;
		try {
            writer = new FileWriter(filePath);

            Set<Integer> keys = content.keySet();
            //Header: id,path or text,user,timestamp
            //Write a new data object list to the CSV file
            for (Integer key : keys) {
                //writer.append(String.valueOf(key));
                //writer.append(",");
                
                writer.append(content.get(key).getUser());
                writer.append(",");
                writer.append(content.get(key).getTimestamp());
                writer.append(",");
            	
                /*if(content.get(key).getText() != null || !content.get(key).getText().isEmpty())
                	writer.append(content.get(key).getText());
                else if(content.get(key).getImage() != null || !content.get(key).getImage().isEmpty())
                	writer.append(content.get(key).getImage());
                else
                	writer.append("");*/

                if(!content.get(key).getPlayerString().isEmpty()) {
                	//System.out.println(content.get(key).getPlayerInfo().length());//Debug
                	writer.append("Player");
                    writer.append(",");
                	writer.append(content.get(key).getPlayerString());
                }
                else if (!content.get(key).getTeamString().isEmpty()) {
                	//System.out.println(content.get(key).getTeamInfo());//Debug
                	writer.append("Team");
                    writer.append(",");
                	writer.append(content.get(key).getTeamString());
                }
                else
                	writer.append("");
                writer.append("\n");
            }
            
        	writer.flush();
        	writer.close();
            System.out.println("Data was saved succesfully");//Debug

        } catch (Exception e) {
            System.out.println("Error in saving the data");//Debug
            Log.log(Level.SEVERE, "Error in saving the data " + filePath);
        }
	}

	//Loads the data from the data.csv file
	public int loadData(String filePath) {	
		int remIndex = 0;
		Scanner sc = null;
		try {
			File dataFile = new File(filePath);
			if(!dataFile.exists()) return 1; //If the database or file does not exist, exit! And report it to the User
			sc = new Scanner(dataFile);
			
		    while (sc.hasNextLine()) {
		        // find next line
		    	String[] line = sc.nextLine().split(",");
		    	String user = line[0];
		    	Data d = new Data(user);
		    	String timeStamp = line[1];
		    	d.setTimestamp(timeStamp);
		    	if(line[2].equals("Team")) {
		    		TeamInfo team = new TeamInfo(line[3]);
		    		team.setPPG(line[4]);
		    		team.setRPG(line[5]);
		    		team.setAPG(line[6]);
		    		team.setOPG(line[7]);
		    		d.setTeamInfo(team);
		    		d.setID();
		    	}
		    	else if(line[2].equals("Player")) {
		    		PlayerInfo player = new PlayerInfo(line[3]);
		    		player.setAge(line[4]);
		    		player.setDOB(line[5]);
		    		player.setHeight(line[6]);
		    		player.setWeight(line[7]);
		    		player.setPoints(line[8]);
		    		player.setAssists(line[9]);
		    		player.setRebounds(line[10]);
		    		player.setPrior(line[11]);
		    		player.setDraft(line[12]);
		    		player.setPIE(line[13]);
		    		d.setPlayerInfo(player);
		    		d.setID();
		    	}
		    	else 
		    		throw new IllegalArgumentException("Argument is not a Player or a Team");
		    	//d.setText(line[1]);
		    	this.addElement(d);
		    	loaded = true;
		    }
		    System.out.println("Data loaded successfully");
		    return remIndex;
		} catch (FileNotFoundException e) {
			Log.log(Level.SEVERE, "Couldn't load data");
			e.printStackTrace();
			return -1;
		} finally {
			if(sc != null) {
				sc.close();
			}
		}
	}
}
