import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPasswordField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class LoginGUI extends JFrame {

	private JPanel contentPane;
	private JTextField txtUser;
	private JLabel lblNbaStats;
	private JPasswordField passwordField;
	private JButton btnLogIn;
	private JButton btnSignUp;
	private JButton btnGuest;
	private static LoginGUI frame;
	
	public static void enableGUI() {
		frame.setVisible(true);
	}
	
	public static void createAndShowGUI(Process p) {
		frame = new LoginGUI(p);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
    }
	
	public LoginGUI(Process p) {
		initComponents();
		createEvents(p);
	}

	private void initComponents() {
		setTitle("WebCrawler");
		setResizable(false);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 310, 256);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblUsername = new JLabel("username");
		
		JLabel lblPassword = new JLabel("password");
		
		txtUser = new JTextField();
		txtUser.setColumns(10);
		
		lblNbaStats = new JLabel("NBA Stats");
		lblNbaStats.setFont(new Font("Trebuchet MS", Font.PLAIN, 24));
		
		btnLogIn = new JButton("Sign In");
		
		passwordField = new JPasswordField();
		
		btnSignUp = new JButton("Sign Up");
		btnGuest = new JButton("Guest");
		btnGuest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblPassword)
							.addGap(18)
							.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblUsername)
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNbaStats)
								.addComponent(txtUser, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(49, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addContainerGap(69, Short.MAX_VALUE)
					.addComponent(btnLogIn)
					.addGap(18)
					.addComponent(btnSignUp)
					.addGap(18)
					.addComponent(btnGuest)
					.addGap(57))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNbaStats)
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUsername)
						.addComponent(txtUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPassword)
						.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnLogIn)
						.addComponent(btnSignUp)
						.addComponent(btnGuest))
					.addGap(31))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	
	
	private void createEvents(Process p) {
		btnLogIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				p.getProgramSettings();
				boolean exist =	User.checkIfExtist(txtUser.getText(),String.valueOf(passwordField.getPassword()), p.getProgramSettings().getAdminFile());
				if(!exist) {
					JOptionPane.showMessageDialog(null, "The user doesn't exist! Try again");
				}
				else {
					if(p.getDb()!= null) {
						Statement update = p.getDb().getStatement();
						String userSQL = "INSERT IGNORE INTO USER (USERNAME) VALUES (" + "\'" + txtUser.getText() + "\'"  +  ")";
						try {
							update.executeUpdate(userSQL);
						} catch (SQLException e1) {
							if(p.getProgramSettings().getDebug())
								e1.printStackTrace();
							Log.log(Level.SEVERE,"Couldn't connect to the database");
						}
					}
					JOptionPane.showMessageDialog(null,"Welcome: " + txtUser.getText());
					p.setUser(txtUser.getText(), String.valueOf(passwordField.getPassword()));
					frame.dispose();
					ProcessGUI.enableGUI();
				}
			}
		});
		
		btnGuest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				GuestGUI.createAndShowGUI(p);
				frame.dispose();
			}
		});
		
		btnSignUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SignUpGUI.createAndShowGUI(p.getProgramSettings().getAdminFile());
				frame.setVisible(false);
			}
		});	
		
	}
}
