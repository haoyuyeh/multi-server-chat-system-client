
/**
 * Author: Hao Yu Yeh
 * Date: 2016年10月12日
 * Project: Assignment2 of Distributed System
 * Comment: this class is used to implement the GUI of chat client and uses SSL socket for connection
 */

import java.awt.EventQueue;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

@SuppressWarnings("serial")
public class clientGUI extends JFrame {

	private final static int inputLimit = 500;
	// determine whether the servers configuration is loaded
	private boolean serverSet = false;
	private String authenticServerAddr;
	private int authenticServerPort;
	private JPanel contentPane;
	public static JTextField textField;
	public static JTextArea textArea;
	public static boolean connected = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					clientGUI frame = new clientGUI();
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
	public clientGUI() {
		setResizable(false);
		setBackground(Color.DARK_GRAY);
		setTitle("Chat Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 881, 610);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("FirstStep");
		menuBar.add(mnFile);

		// load all servers' configuration
		JMenuItem mntmLoadserverlist = new JMenuItem("LoadServerConfigurationFile");
		mntmLoadserverlist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// using JFileChooser to load the server configuration file
				try {
					JFileChooser fileChooser = new JFileChooser();// 宣告filechooser
					int returnValue = fileChooser.showOpenDialog(null);// 叫出filechooser
					if (returnValue == JFileChooser.APPROVE_OPTION) // 判斷是否選擇檔案
					{
						File selectedFile = fileChooser.getSelectedFile();// 指派給File
						Scanner read;
						read = new Scanner(
								new FileInputStream(selectedFile.getPath()));
						String str = "";
						// read all the data in the file
						while (read.hasNextLine()) {
							str = read.nextLine();
							String[] content;
							content = str.split("\t");
							if (content.length == 3) {
								// authentic server
								if (str.startsWith("s0")) {
									authenticServerAddr = content[1];
									authenticServerPort = Integer.parseInt(content[2]);
									break;
								}
							}
						}
						read.close();
						serverSet = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mnFile.add(mntmLoadserverlist);

		JMenu mnSecondstep = new JMenu("SecondStep");
		menuBar.add(mnSecondstep);

		JMenuItem mntmLogIn = new JMenuItem("Log in");
		mntmLogIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (serverSet) {
					JFrame login = new JFrame("Log in");
					login.setVisible(true);
					login.setBounds(100, 100, 600, 200);
					login.getContentPane().setLayout(new GridLayout(5, 1));

//					JPanel p1 = new JPanel();
//					p1.setLayout(new GridLayout(1, 2));
//					JLabel l1 = new JLabel("chat server list: ",
//							SwingConstants.CENTER);
//					p1.add(l1);
//					@SuppressWarnings({ "unchecked", "rawtypes" })
//					JComboBox sList = new JComboBox(serverName);
//					p1.add(sList);
//					login.getContentPane().add(p1);

					JPanel p2 = new JPanel();
					p2.setLayout(new GridLayout(1, 2));
					JLabel l2 = new JLabel("user name: ",
							SwingConstants.CENTER);
					p2.add(l2);
					JTextField tf2 = new JTextField();
					tf2.setToolTipText(
							" at least 3 characters and no more than 16 characters long");
					p2.add(tf2);
					login.getContentPane().add(p2);

					JPanel p3 = new JPanel();
					p3.setLayout(new GridLayout(1, 2));
					JLabel l3 = new JLabel("password: ", SwingConstants.CENTER);
					p3.add(l3);
					JPasswordField tf3 = new JPasswordField();
					p3.add(tf3);
					login.getContentPane().add(p3);

					JPanel p4 = new JPanel();
					p4.setLayout(new GridLayout(1, 2));
					JLabel l4 = new JLabel("ID: ", SwingConstants.CENTER);
					p4.add(l4);
					JTextField tf4 = new JTextField();
					tf4.setToolTipText(
							" at least 3 characters and no more than 16 characters long");
					p4.add(tf4);
					login.getContentPane().add(p4);

					JPanel p5 = new JPanel();
					p5.setLayout(new GridLayout(1, 2));
					JLabel l5 = new JLabel("debug mode: ",
							SwingConstants.CENTER);
					p5.add(l5);
					String names[] = { "true", "false" };
					@SuppressWarnings({ "unchecked", "rawtypes" })
					JComboBox debug = new JComboBox(names);
					p5.add(debug);
					login.getContentPane().add(p5);

					// connect to server
					JButton b6 = new JButton("connect");
					b6.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							setTitle("Chat Client_" + tf4.getText());
							//Location of the Java keystore file containing the collection of 
							//certificates trusted by this application (trust store).
							// for jar file
							String path = clientGUI.class.getResource("").getPath().replaceAll("%20", " ").replaceAll("/bin", "")+"mykeystore";
							System.setProperty("javax.net.ssl.trustStore", path);
							// for eclipse run
//							System.setProperty("javax.net.ssl.trustStore", "lib/mykeystore");
							
							System.setProperty("javax.net.debug","all");
							
							
							
							SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
							
							boolean debugMode = names[debug.getSelectedIndex()]
									.equals("true");
							try {
								// connect to authentic server for validation of log in
								SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(authenticServerAddr, authenticServerPort);
								State state = new State(tf2.getText(),
										new String(tf3.getPassword()),
										tf4.getText(), "");
								// start sending thread
								MessageSendThread messageSendThread = new MessageSendThread(
										sslsocket, state, debugMode);
								Thread sendThread = new Thread(
										messageSendThread);
								sendThread.start();

								// start receiving thread
								Thread receiveThread = new Thread(
										new MessageReceiveThread(sslsocket, state,
												messageSendThread, debugMode));
								receiveThread.start();
							} catch (Exception e2) {
								JOptionPane.showMessageDialog(null,
										"Communication Error: " + e2.getMessage(), "Warning",
										JOptionPane.WARNING_MESSAGE);
								System.exit(1);
							}
							login.dispose();
						}
					});
					login.getContentPane().add(b6);
				} else {
					JOptionPane.showMessageDialog(null, "Start from FirstStep!",
							"Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		mnSecondstep.add(mntmLogIn);

		JMenu mnOthers = new JMenu("Others");
		menuBar.add(mnOthers);

		JMenuItem mntmClearscreen = new JMenuItem("ClearScreen");
		mntmClearscreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText(null);
			}
		});
		mnOthers.add(mntmClearscreen);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 723, 0 };
		gbl_contentPane.rowHeights = new int[] { 240, 240, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 3;
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.weighty = 3.0;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);

		textArea = new JTextArea();
		textArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		textArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane.setViewportView(textArea);
		textArea.setBounds(10, 10, 713, 424);

		textField = new JTextField();
		textField.setToolTipText("Input character limit is 500.");
		// only can input after connected and limit the input length
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				if (connected) {
					String str = textField.getText();
					if (str.length() >= inputLimit) {
						arg0.consume();
					}
				} else {
					arg0.consume();
				}
			}
		});
		textField.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.weighty = 1.0;
		gbc_textField.weightx = 1.0;
		gbc_textField.fill = GridBagConstraints.BOTH;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 3;
		contentPane.add(textField, gbc_textField);
	}
}
