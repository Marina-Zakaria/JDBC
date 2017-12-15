package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import DBCommands.MyConnection;
import DBCommands.MyDriver;
import DBCommands.QueryController;
import DBCommands.StatementAdapter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.Properties;
import java.awt.event.ActionEvent;

public class DBMS extends JFrame {

	private JPanel contentPane;
	private JTextField query;
	private QueryController controller = new QueryController();
	private String path;
	MyDriver driver = new MyDriver();
	Properties info = new Properties();
	StatementAdapter stmt;
	JTextArea cmdLine = new JTextArea();


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DBMS frame = new DBMS();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DBMS() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 699, 407);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnClearCmdLine = new JButton("Clear cmd line");
		btnClearCmdLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cmdLine.setText("");
			}
		});
		btnClearCmdLine.setBounds(545, 86, 128, 23);
		contentPane.add(btnClearCmdLine);

		cmdLine.setBounds(10, 97, 517, 248);
		contentPane.add(cmdLine);
		cmdLine.setEditable(false);

		query = new JTextField();
		query.setBounds(13, 44, 514, 42);
		contentPane.add(query);
		query.setColumns(10);

		JButton btnExecuteQuery = new JButton("execute query");
		btnExecuteQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String currentQuery = query.getText();
				if (path == null && !currentQuery.toLowerCase().contains("create database")) {
					cmdLine.append("please spesify the data base you want to work on or create a new one \n");
				} else if (currentQuery.toLowerCase().contains("create database")) {
					QueryController controller = new QueryController();
					try {
						controller.executeStructureQuery(currentQuery);
					} catch (SQLException e1) {
						cmdLine.append(e1 + "\n");

					}

					File dbDir = new File(controller.adaptee.getPath(null));
					path=dbDir.getAbsoluteFile().getPath();
					cmdLine.append("database created successfully with path \n"+path);
					info.put("path", dbDir.getAbsoluteFile());
					MyConnection connection;
					try {
						connection = (MyConnection) driver.connect("jdbc:xmldb://localhost", info);
						stmt = (StatementAdapter) connection.createStatement();

					} catch (SQLException e) {
						cmdLine.append(currentQuery+"\n"+e + "\n");
					}

				} else {
					try {
						stmt.execute(currentQuery);
						cmdLine.append(currentQuery+"\n executed successfully \n");
						if (currentQuery.toLowerCase().contains("select")) {
							try {
								Object[][] selected = controller.executeQuery(currentQuery);
								for (int i = 0; i < selected.length; i++) {
									cmdLine.append(selected[i][0] + " " + selected[i][1] + "\n");
								}
							} catch (SQLException e) {
								cmdLine.append(currentQuery+"\n"+"Error Occurred \n");
							}
						}
					} catch (SQLException e) {
						cmdLine.append(currentQuery+"\n"+e + "\n");

					}
				}
			}
		});
		btnExecuteQuery.setBounds(545, 52, 128, 23);
		contentPane.add(btnExecuteQuery);

		JButton btnChooseDataBase = new JButton("choose Data Base");
		btnChooseDataBase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser save = new JFileChooser();
				save.setCurrentDirectory(new File("."));
				int r = save.showOpenDialog(null);
				if (r != JFileChooser.APPROVE_OPTION)
					return;
				path = save.getSelectedFile().getAbsolutePath();
				info.put("path", save.getSelectedFile());
				MyConnection connection;
				try {
					connection = (MyConnection) driver.connect("jdbc:xmldb://localhost", info);
					stmt = (StatementAdapter) connection.createStatement();

				} catch (SQLException e) {
					cmdLine.append(e + "\n");
				}
			}
		});
		btnChooseDataBase.setBounds(272, 10, 172, 23);
		contentPane.add(btnChooseDataBase);
	}
}
