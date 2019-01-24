import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeamInfo {
	private String PPG;
	private String RPG;
	private String APG;
	private String OPG;
	private String name;
	
	public TeamInfo(String n) {
		this.name = n;
	}
	
	public String getPPG() {
		return PPG;
	}
	
	public String getRPG() {
		return RPG;
	}
	
	public String getAPG() {
		return APG;
	}
	
	public String getOPG() {
		return OPG;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPPG(String g) {
		this.PPG = g;
	}
	
	public void setRPG(String g) {
		this.RPG = g;
	}

	public void setAPG(String g) {
		this.APG = g;
	}

	public void setOPG(String g) {
		this.OPG = g;
	}
	
	//Gets the info for the team
	public void getInfo(String html) {
		Pattern p1;
		Matcher m1;
		
		p1 = Pattern.compile("(<span class=\"columns small-12 stats-team-summary__title\">)([A-Z\\s\\.]{3,15})(</span>)");
		Pattern avrPattern = Pattern.compile("(<span class=\"columns small-12 stats-team-summary__value\">)(\\d{1,2}[a-z]{2})(</span>)");
		Matcher avrMatcher = avrPattern.matcher(html);
		m1 = p1.matcher(html);
		while (m1.find()) {
			switch(m1.group(2)) {
				case("PPG"):
					//p2 = Pattern.compile("(\"player-stats__stat-value\">)([0-9]{1,2}\\.[0-9])(</span>)");
					//m2 = p2.matcher(html);
					if(avrMatcher.find())
						this.PPG = avrMatcher.group(2);
					break;
				case("RPG"):
					//p2 = Pattern.compile("(\"player-stats__stat-value\">)([0-9]{1,2}\\.[0-9])(</span>)");
					//m2 = p2.matcher(html);
					if(avrMatcher.find())
						this.RPG = avrMatcher.group(2);
					break;
				case("APG"):
					//p2 = Pattern.compile("(\"player-stats__stat-value\">)([0-9]{1,2}\\.[0-9])(</span>)");
					//m2 = p2.matcher(html);
					if(avrMatcher.find())
						this.APG = avrMatcher.group(2);
					break;
				case("OPG"):
					//p2 = Pattern.compile("(\"player-stats__stat-value\">)([0-9]{1,2}\\.[0-9])(</span>)");
					//m2 = p2.matcher(html);
					if(avrMatcher.find())
						this.OPG = avrMatcher.group(2);
					break;
				default:
					System.out.println("Info not available: " + m1.group(2));//debug
					break;
			}	
		}
	}
}
