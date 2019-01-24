import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerInfo {
	private String height;
	private String weight;
	private String DOB;
	private String fullName;
	private String age;	
	private String points;
	private String rebounds;
	private String assists;
	private String PIE;
	private String prior;
	private String draft;
	private String portrait;
	
	public PlayerInfo(String n) {
		fullName = n;  
	}

	public void setHeight(String s) {
		this.height = s;
	}
	
	public void setWeight(String s) {
		this.weight = s;
	}
	
	public void setDOB(String s) {
		this.DOB = s;
	}
	
	public void setAge(String s) {
		this.age = s;
	}
	
	public void setPoints(String s) {
		this.points = s;
	}
	
	public void setAssists(String s) {
		this.assists = s;
	}
	
	public void setRebounds(String s) {
		this.rebounds = s;
	}
	
	public void setPIE(String s) {
		this.PIE = s;
	}
	
	public void setPrior(String s) {
		this.prior = s;
	}
	
	public void setDraft(String s) {
		this.draft = s;
	}
	
	public void setPicture(String s) {
		this.portrait = s;
	}
	
	public String getName() {
		return fullName;
	}
	
	public String getHeight() {
		return height;
	}
	
	public String getWeight() {
		return weight;
	}
	
	public String getDOB() {
		return DOB;
	}
	
	public String getAge() {
		return age;
	}
	
	public String getPoints() {
		return points;
	}
	
	public String getRebounds() {
		return rebounds;
	}
	
	public String getAssists() {
		return assists;
	}
	
	public String getPIE() {
		return PIE;
	}
	
	public String getPrior() {
		return prior;
	}
	
	public String getDraft() {
		return draft;
	}
	
	public String getPortrait() {
		return portrait;
	}
	
	//Searchs in the html for the url of the player and retrives it
	public String getPicture(String html) {
		String imageURL = "";
		Pattern p = Pattern.compile("(<meta property=\"og:image\" content=\")(https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b[-a-zA-Z0-9@:%_\\+.~#?&//=]*)(\" />)");
		Matcher m = p.matcher(html);
		if(m.find()) {
			imageURL = m.group(2);
		}
		return imageURL;
	}
	
	//Gets the player information from the html page of the player
	public void getInfo(String html) {
		Pattern p1;
		Pattern p2;
		Matcher m1;
		Matcher m2;
		
		p1 = Pattern.compile("(\"player-stats__stat-title\">)([A-Z]{2,5})(</div>)");
		Pattern avrPattern = Pattern.compile("(\"player-stats__stat-value\">)([0-9]{1,2}\\.[0-9])(</span>)");
		Matcher avrMatcher = avrPattern.matcher(html);
		m1 = p1.matcher(html);
		while (m1.find()) {
			switch(m1.group(2)) {
				case("PTS"):
					if(avrMatcher.find())
						this.points = avrMatcher.group(2);
					break;
				case("REB"):
					if(avrMatcher.find())
						this.rebounds = avrMatcher.group(2);
					break;
				case("AST"):
					if(avrMatcher.find())
						this.assists = avrMatcher.group(2);
					break;
				case("PIE"):
					if(avrMatcher.find())
						this.PIE = avrMatcher.group(2);
					break;
				case("HT"):
					p2 = Pattern.compile("(\"player-stats__stat-value\">)([0-9]-[0-9]{1,2})(</span>)");
					m2 = p2.matcher(html);
					if(m2.find())
						this.height = m2.group(2);
					break;
				case("WT"):
					p2 = Pattern.compile("(\"player-stats__stat-value\">)([0-9]{3} )(<span class=\"player-stats__lbs\">)");
					m2 = p2.matcher(html);
					if(m2.find())
						this.weight = m2.group(2).trim();
					break;
				case("AGE"):
					p2 = Pattern.compile("(\"player-stats__stat-value\">)([0-9]{2})(</span>)");
					m2 = p2.matcher(html);
					if(m2.find())
						this.age = m2.group(2);
					break;
				case("BORN"):
					p2 = Pattern.compile("(\"player-stats__stat-value\">)([0-9]{2}/[0-9]{2}/[0-9]{4})(</span>)");
					m2 = p2.matcher(html);
					if(m2.find())
						this.DOB = m2.group(2);
					break;
				case("PRIOR"):
					p2 = Pattern.compile("(\"player-stats__stat-value\">)(\\s?\\w+/\\w+\\s?)(</span>)");
					m2 = p2.matcher(html);
					if(m2.find())
						this.prior = m2.group(2).trim();
					break;
				case("DRAFT"):
					p2 = Pattern.compile("(\"player-stats__stat-value\">)([0-9]{4} Rnd \\d{1,2} Pick \\d{1,2})(</span>)");
					m2 = p2.matcher(html);
					if(m2.find())
						this.draft = m2.group(2);
					break;
				default:
					System.out.println("Info not available: " + m1.group(2));//debug
					break;
			}		
	    }
	}
}