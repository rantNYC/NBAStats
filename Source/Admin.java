public class Admin extends User{

	public Admin(String user, String nick) {
		super(user,nick);
	}
	
	//Indicates if the current user is an admin
	@Override
	public boolean isSuperuser() {
		return true;
	}
}
