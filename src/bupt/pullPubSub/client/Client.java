package bupt.pullPubSub.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Client extends JFrame {

	/**
	 * 
	 */
	// private String serverAddress = "localhost";
	// private int serverPort = 8000;
	private int timeout = 10000;

	private static final long serialVersionUID = 1L;
	private JTabbedPane jtp = new JTabbedPane();
	private ScrollPane spInfo = new ScrollPane();
	private JPanel jpInfo = new JPanel();
	private JTextArea jta = new JTextArea();
	private JButton refreshBtn = new JButton();

	private JPanel jpSub = new JPanel();
	private JPanel subClasses = new JPanel();
	private JButton subSubmit = new JButton("Submit");

	private JPanel jpPub = new JPanel();
	private JTextField jtfTitle = new JTextField();
	private JTextArea jtfContent = new JTextArea();
	private ScrollPane spPub = new ScrollPane();
	private JPanel pubClasses = new JPanel();
	private JPanel pubContentPane = new JPanel();
	private JButton pubSubmit = new JButton("Submit");

	private JPanel jpChangePwd = new JPanel();
	private JPanel jpChangePwdJT = new JPanel();
	private JPasswordField jpfOldPwd = new JPasswordField();
	private JPasswordField jpfNewPwd = new JPasswordField();
	private JPasswordField jpfConfirmPwd = new JPasswordField();
	private JButton confirmBtn = new JButton();
	private JLabel jlOldPwd = new JLabel();
	private JLabel jlNewPwd = new JLabel();
	private JLabel jlConfirmPwd = new JLabel();

	private JCheckBox[] jck;
	private JRadioButton[] jrb;
	private ButtonGroup group = new ButtonGroup();

	private Socket socket;

	private int windowWidth = 320;
	private int windowHeight = 450;

	private String subToServer;
	private int resendTimes;
	private static int resendMaxTimes = 5;

	public Client() {

	}

	public Client(final String username, final String topics,
			final int topicNum, Socket socket) {
		/* initialize the publisher's client */
		initialFrame(username, topics, topicNum);

		this.socket = socket;

		initFuntions(username, topics, topicNum);
	}

	private void initFuntions(final String username, final String topics,
			final int topicNum) {
		try {
			final DataInputStream fromServer = new DataInputStream(
					socket.getInputStream());
			final DataOutputStream toServer = new DataOutputStream(
					socket.getOutputStream());

			try {
				socket.setSoTimeout(timeout);
			} catch (SocketException e1) {
				e1.printStackTrace();
			}

			/* only one radio button can be chose */

			for (int i = 0; i < topicNum; i++) {
				group.add(jrb[i]);
			}

			refreshBtn.addActionListener(new RefreshHandler(fromServer,
					toServer, username));

			// listen the pub submit button
			pubSubmit.addActionListener(new PubSubmitHandler(fromServer,
					toServer, topics, topicNum));

			// listen the sub submit button
			subSubmit.addActionListener(new SubSubmitHandler(fromServer,
					toServer, topics, username, topicNum));

			confirmBtn.addActionListener(new ConfirmHandler(fromServer,
					toServer));

			jtp.addChangeListener(new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					int selectedIndex = jtp.getSelectedIndex();

					if (selectedIndex == 1) {
						try {

							try {
								toServer.writeUTF("getClass");
								String subscribeClasses = fromServer.readUTF();
								String[] s;
								s = topics.split("\n");
								for (int i = 0; i < topicNum; i++) {
									if (subscribeClasses.contains(s[i])) {
										jck[i].setSelected(true);
									}
								}
							} catch (InterruptedIOException ex) {
								System.err.print("timeout on read");
							}
						} catch (IOException ex) {
							System.err.println(ex);
						}
					}
				}

			});

			addWindowListener(new WindowListener() {

				public void windowClosing(WindowEvent e) {
					try {
						if (socket != null)
							socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				@Override
				public void windowOpened(WindowEvent e) {

				}

				@Override
				public void windowClosed(WindowEvent e) {

				}

				@Override
				public void windowIconified(WindowEvent e) {

				}

				@Override
				public void windowDeiconified(WindowEvent e) {

				}

				@Override
				public void windowActivated(WindowEvent e) {

				}

				@Override
				public void windowDeactivated(WindowEvent e) {

				}

			});
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null,
					"Create I/O stream failed! Please try again!");s
			e1.printStackTrace();
		}
	}

	public void initialFrame(String username, String topics, int topicNum) {

		jpInfo.setLayout(new BorderLayout());
		jta.setSize(10, 5);
		jta.setLineWrap(true);
		jta.setEditable(false);
		refreshBtn.setText("refresh");
		jpInfo.add(spInfo, BorderLayout.CENTER);
		jpInfo.add(refreshBtn, BorderLayout.SOUTH);
		spInfo.add(jta);

		String[] s;
		s = topics.split("\n");

		jck = new JCheckBox[topicNum];
		for (int i = 0; i < topicNum; i++) {
			jck[i] = new JCheckBox();
			jck[i].setText(s[i]);
			subClasses.add(jck[i]);
		}

		jrb = new JRadioButton[topicNum];
		for (int i = 0; i < topicNum; i++) {
			jrb[i] = new JRadioButton();
			jrb[i].setText(s[i]);
			pubClasses.add(jrb[i]);
		}

		subClasses.setLayout(new GridLayout(10, 3));
		subClasses.setBorder(new TitledBorder("Classes"));
		jpSub.setLayout(new BorderLayout());
		jpSub.add(subClasses, BorderLayout.CENTER);
		jpSub.add(subSubmit, BorderLayout.SOUTH);

		jtfTitle.setText("Title");
		jtfContent.setText("Content");
		spPub.add(jtfContent);

		pubClasses.setLayout(new GridLayout(2, 4));
		pubClasses.setBorder(new TitledBorder("Classes"));
		pubContentPane.setLayout(new BorderLayout());
		pubContentPane.add(spPub, BorderLayout.CENTER);
		pubContentPane.add(pubClasses, BorderLayout.SOUTH);
		jpPub.setLayout(new BorderLayout());
		jpPub.add(jtfTitle, BorderLayout.NORTH);
		jpPub.add(pubContentPane, BorderLayout.CENTER);
		jpPub.add(pubSubmit, BorderLayout.SOUTH);

		jlOldPwd.setText("Old password:");
		jlNewPwd.setText("New password:");
		jlConfirmPwd.setText("Confirm password:");
		jpfOldPwd.setSize(10, 10);
		jpfNewPwd.setSize(10, 10);
		jpfConfirmPwd.setSize(10, 10);
		confirmBtn.setText("confirm");
		jpChangePwdJT.setLayout(new GridLayout(6, 1, 5, 5));
		jpChangePwdJT.add(jlOldPwd);
		jpChangePwdJT.add(jpfOldPwd);
		jpChangePwdJT.add(jlNewPwd);
		jpChangePwdJT.add(jpfNewPwd);
		jpChangePwdJT.add(jlConfirmPwd);
		jpChangePwdJT.add(jpfConfirmPwd);
		jpChangePwd.setLayout(new BorderLayout());
		jpChangePwd.add(jpChangePwdJT, BorderLayout.CENTER);
		jpChangePwd.add(confirmBtn, BorderLayout.SOUTH);

		add(jtp, BorderLayout.CENTER);
		jtp.add(jpInfo, "Info");
		jtp.add(jpSub, "SubClass");
		jtp.add(jpPub, "Pub");
		jtp.add(jpChangePwd, "ChangePwd");
		jtp.setToolTipTextAt(0, "Info");
		jtp.setToolTipTextAt(1, "SubClass");
		jtp.setToolTipTextAt(2, "Pub");
		jtp.setToolTipTextAt(2, "ChangePwd");

		setTitle(username + "\'s Client");
		setSize(windowWidth, windowHeight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public String getStartTime(String username) {
		String startTime = "0";

		File file = new File(username + "_startTimeToRecvMsg.txt");

		if (file.exists()) {
			try {
				Scanner input = new Scanner(file);
				// Read data from a file
				if (input.hasNext())
					startTime = input.nextLine();

				// Close the file
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return startTime;
	}

	public void storeStartTime(String startTime, String username) {

		File file = new File(username + "_startTimeToRecvMsg.txt");

		PrintWriter output;

		try {
			output = new PrintWriter(file);

			// Write formatted output to the file
			output.println(startTime);

			// Close the file
			output.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

	}

	private class ConfirmHandler implements ActionListener {
		private DataInputStream fromServer;
		private DataOutputStream toServer;

		public ConfirmHandler(DataInputStream fromServer,
				DataOutputStream toServer) {
			this.fromServer = fromServer;
			this.toServer = toServer;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {

				try {

					String oldPwd = new String(jpfOldPwd.getPassword());
					String newPwd = new String(jpfNewPwd.getPassword());
					String confirmPwd = new String(jpfConfirmPwd.getPassword());

					if (oldPwd.equals(newPwd)) {
						JOptionPane
								.showMessageDialog(null,
										"New password is same as the old one!try again!");
						jpfOldPwd.setText(null);
						jpfNewPwd.setText(null);
						jpfConfirmPwd.setText(null);
					} else {
						toServer.writeUTF("ChangePwd");
						toServer.writeUTF(oldPwd);
						if (fromServer.readUTF().equals("true")) {
							if (newPwd.equals(confirmPwd)) {
								toServer.writeUTF(newPwd);
								if (fromServer.readUTF().equals("true")) {
									JOptionPane.showMessageDialog(null,
											"Successful!");
									jpfOldPwd.setText(null);
									jpfNewPwd.setText(null);
									jpfConfirmPwd.setText(null);
								} else
									JOptionPane.showMessageDialog(null,
											"failed!");
							} else {
								JOptionPane
										.showMessageDialog(null,
												"two new password are not matched!Try again!");
								jpfNewPwd.setText(null);
								jpfConfirmPwd.setText(null);
							}
						} else {
							JOptionPane.showMessageDialog(null,
									"Wrong old password!Try again!");
							jpfOldPwd.setText(null);
						}
					}
				} catch (InterruptedIOException ex) {
					System.err.print("timeout on read");
				}
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}

	}

	private class PubSubmitHandler implements ActionListener {
		private DataInputStream fromServer;
		private DataOutputStream toServer;
		private int topicNum;
		private String topics;

		public PubSubmitHandler(DataInputStream fromServer,
				DataOutputStream toServer, String topics, int topicNum) {
			this.fromServer = fromServer;
			this.toServer = toServer;
			this.topicNum = topicNum;
			this.topics = topics;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean isClassChosen;
			String pubContentToServer;

			isClassChosen = false;
			pubContentToServer = "";

			try {

				for (int i = 0; i < topicNum; i++) {
					if (jrb[i].isSelected() == true)
						isClassChosen = true;
				}

				if (isClassChosen == true) {
					try {
						// send file to server
						toServer.writeUTF("Publish");
						// write file's class
						String[] f;
						f = topics.split("\n");
						for (int i = 0; i < topicNum; i++) {
							if (jrb[i].isSelected() == true)
								toServer.writeUTF(f[i]);
						}

						// split data with "\n"
						pubContentToServer = pubContentToServer.concat(jtfTitle
								.getText() + "\n");
						String[] s = jtfContent.getText().split("\n");
						s = jtfContent.getText().split("\n");
						for (String x : s) {
							pubContentToServer = pubContentToServer.concat(x
									+ "\n");
						}

						toServer.writeUTF(pubContentToServer);

						toServer.flush();

						if (fromServer.readUTF().equals("true"))
							JOptionPane.showMessageDialog(null,
									"Publish successfully!");
						else
							JOptionPane.showMessageDialog(null,
									"Publish failed!");

					} catch (InterruptedIOException ex) {
						System.err.print("timeout on read");
					}
				} else
					JOptionPane.showMessageDialog(null,
							"No class is chosen! Please choose one.");

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

	private class SubSubmitHandler implements ActionListener {
		private DataInputStream fromServer;
		private DataOutputStream toServer;
		private int topicNum;
		private String topics;
		private String username;

		public SubSubmitHandler(DataInputStream fromServer,
				DataOutputStream toServer, String topics, String username,
				int topicNum) {
			this.fromServer = fromServer;
			this.toServer = toServer;
			this.topicNum = topicNum;
			this.topics = topics;
			this.username = username;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				subToServer = "";
				String[] s;
				int selectedNum = 0;
				s = topics.split("\n");
				for (int i = 0; i < topicNum; i++) {
					if (jck[i].isSelected() == true) {
						subToServer = subToServer.concat(s[i] + "\n");
						selectedNum++;
					}
				}

				try {
					toServer.writeUTF("ChangeClass");
					toServer.writeUTF(subToServer);
					toServer.writeInt(selectedNum);

					if (fromServer.readUTF().equals("true")) {
						JOptionPane.showMessageDialog(null, "Successful!");
						storeStartTime(fromServer.readUTF(), username);
					} else
						JOptionPane.showMessageDialog(null, "Failed!");

				} catch (InterruptedIOException ex) {
					System.err.print("timeout on read");
				}
			} catch (IOException ex) {

			}
		}

	}

	private class RefreshHandler implements ActionListener {
		private DataInputStream fromServer;
		private DataOutputStream toServer;
		private String username;

		public RefreshHandler(DataInputStream fromServer,
				DataOutputStream toServer, String username) {
			this.fromServer = fromServer;
			this.toServer = toServer;
			this.username = username;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String[] s;

			try {
				resendTimes = 0;
				try {
					toServer.writeUTF("PullInfo");

					toServer.writeUTF(getStartTime(username));

					if (fromServer.readUTF().equals("noSubscribe")) {
						JOptionPane.showMessageDialog(null,
								"No subscribe! Please select at least one!");
						toServer.writeUTF("ack");
						while (true) {
							try {
								socket.setSoTimeout(timeout);
								if (fromServer.readUTF().equals("ack"))
									break;
							} catch (SocketException ex) {
								System.out
										.println("Did recieve ack, send message again.");
								toServer.writeUTF("ack");
								resendTimes++;
								if (resendTimes == resendMaxTimes)
									break;
							}
						}
					} else {
						s = fromServer.readUTF().split("\n");
						if (s.length == 1) {
							fromServer.readUTF();
							JOptionPane.showMessageDialog(null,
									"No new message! Please try latter!");
							toServer.writeUTF("ack");
							while (true) {
								try {
									socket.setSoTimeout(timeout);
									if (fromServer.readUTF().equals("ack"))
										break;
								} catch (SocketException ex) {
									System.out
											.println("Did recieve ack, send message again.");
									toServer.writeUTF("ack");
									resendTimes++;
									if (resendTimes == resendMaxTimes)
										break;
								}
							}
						} else {
							for (String x : s) {
								jta.append(x + "\n");
							}
							jta.append("\n");

							storeStartTime(fromServer.readUTF(), username);
							toServer.writeUTF("ack");
							while (true) {
								try {
									socket.setSoTimeout(timeout);
									if (fromServer.readUTF().equals("ack"))
										break;
								} catch (SocketException ex) {
									System.out
											.println("Did recieve ack, send message again.");
									toServer.writeUTF("ack");
									resendTimes++;
									if (resendTimes == resendMaxTimes)
										break;
								}
							}
						}
					}
				} catch (InterruptedIOException ex) {
					System.err.print("timeout on read\n");
				}
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}

	}
}
