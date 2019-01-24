import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import java.awt.Font;

public class GuestGUI extends JFrame {

	private JPanel contentPane;
	private static GuestGUI frame;
	private DefaultTableModel playerModel;
	private DefaultTableModel teamModel;
	private JTable playerTable;
	private JTable teamTable;
	private JLabel lblPlayerInfo;

	public static void createAndShowGUI(Process p) {
		frame = new GuestGUI(p);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
    }
	
	public GuestGUI(Process p) {
		initComponents(p);
	}
	
	public void initComponents(Process p){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 757, 475);
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

			if(d.getPlayerInfo() != null) {
				PlayerInfo player = d.getPlayerInfo();
				playerModel.addRow(new Object[] {player.getName(),player.getAge(),player.getDOB(),player.getHeight(),player.getWeight(),player.getPoints(),
						player.getAssists(),player.getRebounds(),player.getPrior(),player.getDraft(),player.getPIE()});
			}else {
				TeamInfo team = d.getTeamInfo();
				teamModel.addRow(new Object[] {team.getName(),team.getPPG(),team.getRPG(),team.getAPG(),team.getOPG()});
			}
		}
		playerTable = new JTable(playerModel);
		
		teamTable = new JTable(teamModel);
		
		JLabel lblTeamInfo = new JLabel("Team Info");
		lblTeamInfo.setFont(new Font("Verdana", Font.PLAIN, 24));
		
		lblPlayerInfo = new JLabel("Player Info");
		lblPlayerInfo.setFont(new Font("Verdana", Font.PLAIN, 24));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(playerTable, GroupLayout.DEFAULT_SIZE, 768, Short.MAX_VALUE)
						.addComponent(teamTable, GroupLayout.DEFAULT_SIZE, 768, Short.MAX_VALUE)
						.addComponent(lblPlayerInfo, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTeamInfo, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblPlayerInfo, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
					.addGap(27)
					.addComponent(playerTable, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
					.addGap(36)
					.addComponent(lblTeamInfo)
					.addGap(31)
					.addComponent(teamTable, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
}
