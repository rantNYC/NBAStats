import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SignUpGUI extends JFrame {

	private JPanel contentPane;
	private JTextField tfUsername;
	private JPasswordField passwordField;
	private JPasswordField passwordFieldConfirm;
	private JButton btnCreate;
	private static SignUpGUI frame;

	public static void createAndShowGUI(String path) {
		frame = new SignUpGUI(path);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
    }
	
	public SignUpGUI(String path) {
		initComponents();
		createEvents(path);
	}
	
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 413, 337);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblCreateANew = new JLabel("Welcome!");
		lblCreateANew.setFont(new Font("Tahoma", Font.PLAIN, 24));
		
		JLabel lblUsername = new JLabel("Username");
		
		JLabel lblPassword = new JLabel("Password");
		
		JLabel lblConfirmPassword = new JLabel("Confirm Password:");
		
		tfUsername = new JTextField();
		tfUsername.setColumns(10);
		
		btnCreate = new JButton("Create");
		
		passwordField = new JPasswordField();
		
		passwordFieldConfirm = new JPasswordField();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblConfirmPassword)
						.addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblUsername))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
							.addComponent(btnCreate)
							.addComponent(lblCreateANew)
							.addComponent(tfUsername, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
							.addComponent(passwordField))
						.addComponent(passwordFieldConfirm, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(38, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblCreateANew)
					.addGap(30)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUsername)
						.addComponent(tfUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPassword)
						.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblConfirmPassword)
						.addComponent(passwordFieldConfirm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
					.addComponent(btnCreate)
					.addGap(32))
		);
		contentPane.setLayout(gl_contentPane);
	}

	private void createEvents(String path) {
		btnCreate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String user = tfUsername.getText();
				String psw = String.valueOf(passwordField.getPassword());
				String confirmPsw = String.valueOf(passwordFieldConfirm.getPassword());
				if(user.isEmpty() && psw.isEmpty() && confirmPsw.isEmpty()){
					JOptionPane.showMessageDialog(null, "Please, enter all the fields", "Field Missing", JOptionPane.ERROR_MESSAGE);
				}
				else if(user.length() < 3 || psw.length() < 3 || confirmPsw.length() < 3) {
					JOptionPane.showMessageDialog(null, "All fields must be more than three characters", "Wrong Input", JOptionPane.ERROR_MESSAGE);
				}
				else {
					if(!psw.equals(confirmPsw)) {
						JOptionPane.showMessageDialog(null, "The passwords do not match! Try again", "Password Mismatch", JOptionPane.ERROR_MESSAGE);
					}
					else {
						boolean exist = User.checkIfExtist(tfUsername.getText(), String.valueOf(passwordField.getPassword()), path);
						if(exist) {
							JOptionPane.showMessageDialog(null, "User already exists", "User Match", JOptionPane.ERROR_MESSAGE);
						}
						else {
							User.saveUser(tfUsername.getText(), String.valueOf(passwordField.getPassword()), path);
							frame.dispose();
							LoginGUI.enableGUI();
						}
					}
				}
			}
		});
	}
}
