import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.Component;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ProcessGUI extends JFrame {

	private JPanel contentPane;
	private JTable tblPlayerInfo;
	private JTable tblTeamInfo;
	private static ProcessGUI frame;
	private JButton btnLoadData;
	private JButton btnSaveData;
	private DefaultTableModel playerModel;
	private DefaultTableModel teamModel;
	private JScrollPane scrollPlayer;
	private JScrollPane scrollTeam;
	private JButton btnSearchPlayer;
	private JButton btnSearchTeam;
	private JButton btnDelete;
	private JButton btnModify;
	
	public static void createAndShowGUI(Process p) {
		frame = new ProcessGUI(p);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(false);
    }
	
	public static void enableGUI() {
		frame.setVisible(true);
	}
	
	public ProcessGUI(Process p) {
		initialize(p);
		eventHandler(p);
	}

	private void initialize(Process p) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 837, 556);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		String[] columnTeam = {"Name","PPG","RPG","APG","PIE"};
		String[] columnPlayer = {"Name","Age","DOB","Height","Weight","Points","Assists","Rebounds","Prior","Draft","PIE"};
		playerModel = new DefaultTableModel(columnPlayer,0){
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		teamModel = new DefaultTableModel(columnTeam,0){
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
				
		tblPlayerInfo = new JTable(playerModel);
		tblPlayerInfo.setFillsViewportHeight(true);
		tblPlayerInfo.setColumnSelectionAllowed(true);
		tblPlayerInfo.setCellSelectionEnabled(true);

		tblTeamInfo = new JTable(teamModel);
		tblTeamInfo.setFillsViewportHeight(true);
		tblTeamInfo.setColumnSelectionAllowed(true);
		tblTeamInfo.setCellSelectionEnabled(true);
		
		scrollPlayer = new JScrollPane(tblPlayerInfo);

		scrollTeam = new JScrollPane(tblTeamInfo);
		
		JLabel lblPlayerInfo = new JLabel("Player Info");
		lblPlayerInfo.setFont(new Font("Verdana", Font.PLAIN, 18));
		
		JLabel lblTeamInfo = new JLabel("Team Info");
		lblTeamInfo.setFont(new Font("Verdana", Font.PLAIN, 18));
		
		btnSearchPlayer = new JButton("Search Player");

		btnSearchPlayer.setFont(new Font("Verdana", Font.PLAIN, 13));
		
		btnSearchTeam = new JButton("Search Team");

		btnSearchTeam.setFont(new Font("Verdana", Font.PLAIN, 13));
		
		JLabel lblWelcomeToThe = new JLabel("Welcome to the NBA Stats");
		lblWelcomeToThe.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblWelcomeToThe.setHorizontalTextPosition(SwingConstants.CENTER);
		lblWelcomeToThe.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcomeToThe.setFont(new Font("Verdana", Font.PLAIN, 20));
		
		btnDelete = new JButton("Delete");

		btnDelete.setFont(new Font("Verdana", Font.PLAIN, 13));
		
		btnModify = new JButton("Modify");

		btnModify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnModify.setFont(new Font("Verdana", Font.PLAIN, 13));
		
		btnLoadData = new JButton("Load Data");
		
		btnSaveData = new JButton("Save Data");


		btnLoadData.setFont(new Font("Verdana", Font.PLAIN, 13));
		btnSaveData.setFont(new Font("Verdana", Font.PLAIN, 13));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(17)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblTeamInfo, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
							.addGap(23))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblPlayerInfo)
							.addGap(18)))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(tblTeamInfo, GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
						.addComponent(tblPlayerInfo, GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPlayer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrollTeam, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(4))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(38)
					.addComponent(btnSearchPlayer, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
					.addGap(18)
					.addComponent(btnSearchTeam, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
					.addGap(18)
					.addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
					.addGap(18)
					.addComponent(btnModify, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
					.addGap(100)
					.addComponent(btnLoadData, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnSaveData, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
					.addGap(41))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(276)
					.addComponent(lblWelcomeToThe, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGap(266))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(9)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblWelcomeToThe)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnSearchPlayer, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnSearchTeam, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnDelete, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnModify, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)))
						.addComponent(btnLoadData, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSaveData, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(10)
							.addComponent(tblPlayerInfo, GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
							.addGap(26)
							.addComponent(tblTeamInfo, GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(81)
							.addComponent(lblPlayerInfo, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
							.addGap(11)
							.addComponent(scrollPlayer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(174)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblTeamInfo, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
								.addComponent(scrollTeam, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(91))))
		);
		contentPane.setLayout(gl_contentPane);
	}

	private void eventHandler(Process p) {
		btnSearchPlayer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String player = JOptionPane.showInputDialog("Enter your player to search");
				try {
					if(player != null && !player.isEmpty())
						p.processPlayerInfo(player.trim());
				} catch (SQLException e) {
					Log.log(Level.SEVERE,"Couldn't connect to the database");
				}
			}
		});
		
		btnSearchTeam.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String team = JOptionPane.showInputDialog("Enter your player to search");
				try {
					if(team != null && !team.isEmpty())
						p.processTeamInfo(team.trim());
				} catch (SQLException e) {
					Log.log(Level.SEVERE,"Couldn't connect to the database");
				}
			}
		});
		
		btnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String delete = JOptionPane.showInputDialog("Enter Player/Team name to delete");
				try {
					if(delete != null && !delete.isEmpty())
						p.processDelete(delete.trim());
				} catch (SQLException e1) {
					Log.log(Level.SEVERE,"Couldn't connect to the database");
				}
			}
		});
		
		btnModify.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String modify = JOptionPane.showInputDialog("Enter Player/Team to modify");
				try {
					if(modify != null && !modify.isEmpty())
						p.processModify(modify.trim());
				} catch (SQLException e1) {
					Log.log(Level.SEVERE,"Couldn't connect to the database");
				}
			}
		});
		
		btnLoadData.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				playerModel.setRowCount(0);
				teamModel.setRowCount(0);
				if(p.getProgramSettings().getStorageMethod() == "ht") {
					DataStorage ds = p.getInfo();
					ProgramSettings ps = p.getProgramSettings();
					if(ds == null || ds.getContent().size() == 0) {
						ds.loadData(ps.getFilePath());	
						p.getInfo().setLoaded(true);
					}else
						p.getInfo().setLoaded(true);
					Set<Integer> keys = ds.getContent().keySet();
					if(!p.getProgramSettings().getEnableAdmin()) {
						for(Integer key: keys) {
							Data d = ds.getContent().get(key);
							if(d.getUser().equals(p.getUserProcess())) {
								if(d.getPlayerInfo() != null) {
									PlayerInfo player = d.getPlayerInfo();
									playerModel.addRow(new Object[] {player.getName(),player.getAge(),player.getDOB(),player.getHeight(),player.getWeight(),player.getPoints(),
											player.getAssists(),player.getRebounds(),player.getPrior(),player.getDraft(),player.getPIE()});
								}else {
									TeamInfo team = d.getTeamInfo();
									teamModel.addRow(new Object[] {team.getName(),team.getPPG(),team.getRPG(),team.getAPG(),team.getOPG()});
								}
							}
						}
					}else {
						for(Integer key: keys) {
							Data d = ds.getContent().get(key);

							if(d.getPlayerInfo() != null) {
								PlayerInfo player = d.getPlayerInfo();
								playerModel.addRow(new Object[] {player.getName(),player.getAge(),player.getDOB(),player.getHeight(),player.getWeight(),player.getPoints(),
										player.getAssists(),player.getRebounds(),player.getPrior(),player.getDraft(),player.getPIE()});
							}else {
								TeamInfo team = d.getTeamInfo();
								teamModel.addRow(new Object[] {team.getName(),team.getPPG(),team.getRPG(),team.getAPG(),team.getOPG()});
							}
						}
					}
				}
				else if(p.getProgramSettings().getStorageMethod() == "db") {
					if(p.getDb() != null) {
						int userID = p.getDb().getUserID(p.getUserProcess());
						Statement search = p.getDb().getStatement();
						String sqlPlayer = "Select * FROM PlayerInfo WHERE User_idUser = " + userID;
						String sqlTeam = "Select * FROM TeamInfo WHERE User_idUser = " + userID;
						try {
							ResultSet rsPlayer = search.executeQuery(sqlPlayer);
							buildTableModel(rsPlayer,playerModel);
							rsPlayer.close();
							ResultSet rsTeam = search.executeQuery(sqlTeam);
							buildTableModel(rsTeam, teamModel);
							rsTeam.close();
						} catch (SQLException e) {
							if(p.getProgramSettings().getDebug())
								e.printStackTrace();
							Log.log(Level.SEVERE, "Coudln't connect to the database");
						}
					}
				}
				else {
					DataStorage ds = p.getInfo();
					ProgramSettings ps = p.getProgramSettings();
					if(ds == null || ds.getContent().size() == 0) {
						ds.loadData(ps.getFilePath());	
						p.getInfo().setLoaded(true);
					}else
						p.getInfo().setLoaded(true);
					Set<Integer> keys = ds.getContent().keySet();
					for(Integer key: keys) {
						Data d = ds.getContent().get(key);
						if(d.getUser().equals(p.getUserProcess())) {
							if(d.getPlayerInfo() != null) {
								PlayerInfo player = d.getPlayerInfo();
								playerModel.addRow(new Object[] {player.getName(),player.getAge(),player.getDOB(),player.getHeight(),player.getWeight(),player.getPoints(),
										player.getAssists(),player.getRebounds(),player.getPrior(),player.getDraft(),player.getPIE()});
							}else {
								TeamInfo team = d.getTeamInfo();
								teamModel.addRow(new Object[] {team.getName(),team.getPPG(),team.getRPG(),team.getAPG(),team.getOPG()});
							}
						}
					}
					if(p.getDb() != null) {
						int userID = p.getDb().getUserID(p.getUserProcess());
						Statement search = p.getDb().getStatement();
						String sqlPlayer = "Select * FROM PlayerInfo WHERE User_idUser = " + userID;
						String sqlTeam = "Select * FROM TeamInfo WHERE User_idUser = " + userID;
						try {
							ResultSet rsPlayer = search.executeQuery(sqlPlayer);
							buildTableModel(rsPlayer,playerModel);
							rsPlayer.close();
							ResultSet rsTeam = search.executeQuery(sqlTeam);
							buildTableModel(rsTeam, teamModel);
							rsTeam.close();
						} catch (SQLException e) {
							if(p.getProgramSettings().getDebug())
								e.printStackTrace();
							Log.log(Level.SEVERE, "Couldn't connect to the database");
						}
					}
				}
				
				tblPlayerInfo = new JTable(playerModel);
				tblTeamInfo = new JTable(teamModel);
				scrollPlayer.add(tblPlayerInfo);
				scrollTeam.add(tblTeamInfo);
				frame.getContentPane().add(new JScrollPane(tblPlayerInfo));
				frame.getContentPane().add(scrollTeam);
				}

		});
		
		btnSaveData.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				p.getInfo().saveData(p.getProgramSettings().getFilePath());
			}
		});
	}
	
	//Idea from https://stackoverflow.com/questions/10620448/most-simple-code-to-populate-jtable-from-resultset
	public void buildTableModel(ResultSet rs, DefaultTableModel dtm)
	        throws SQLException {

	    ResultSetMetaData metaData = rs.getMetaData();

	    int columnCount = metaData.getColumnCount();

	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	            vector.add(rs.getObject(columnIndex));
	        }
	        dtm.addRow(vector);
	    }

	}
}
