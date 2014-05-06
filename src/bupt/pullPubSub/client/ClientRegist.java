package bupt.pullPubSub.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ClientRegist extends JFrame {

	private static final long serialVersionUID = 1L;
	private static int screenWidth;
	private static int screenHeight;
	private static int windowWidth;
	private static int windowHeight;
	private String isRegisted;
	private JButton resetBtn;
	private JButton confirmBtn;
	private JLabel nameLabel;
	private JLabel pwdLabel;
	private JTextField nameTF;
	private JPasswordField pwdPF;

	private int timeout = 10000;

	public ClientRegist() {

	}

	public ClientRegist(Socket socket) {
		// set layout as BorderLayout
		initFrame();

		try {
			// Create an input stream
			final DataInputStream fromServer = new DataInputStream(
					socket.getInputStream());
			// Create an output stream
			final DataOutputStream toServer = new DataOutputStream(
					socket.getOutputStream());

			resetBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Reset button pressed.");
					nameTF.setText(null);
					pwdPF.setText(null);
				}
			});

			// add an action listener to confirm button
			confirmBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Send user name and password and type to server
					String username = nameTF.getText();
					String password = new String(pwdPF.getPassword());

					try {

						try {
							// write client's type, username, password and
							// user's type to server
							toServer.writeUTF("regist");
							toServer.writeUTF(username);
							toServer.writeUTF(password);
							toServer.flush();

							isRegisted = fromServer.readUTF();

							if (isRegisted.equals("exist")) {
								JOptionPane
										.showMessageDialog(null,
												"Username has existed! Please try again!");
							} else if (isRegisted.equals("true")) {
								JOptionPane.showMessageDialog(null,
										"Regist successful!");
								dispose();
							} else {
								JOptionPane.showMessageDialog(null,
										"Regist failed!");
							}
						} catch (InterruptedIOException ex) {
							System.err.print("timeout on read");
						}
					} catch (IOException ex) {
						System.err.println(ex);
					}

				}
			});

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null,
					"Server is closed! Please try latter!");
			e1.printStackTrace();
		}
		try {
			socket.setSoTimeout(timeout);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void initFrame() {
		// TODO Auto-generated method stub
		setLayout(new BorderLayout());

		// get screen's width and height
		screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

		// get program window's width and height
		windowWidth = 300;
		windowHeight = 150;

		// put components into the frame
		JPanel createPanel = new JPanel();
		JPanel buttonPanel = new JPanel();

		add(createPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		createPanel.setLayout(new GridLayout(2, 2, 5, 5));
		nameLabel = new JLabel("username: ");
		pwdLabel = new JLabel("password: ");
		nameTF = new JTextField(10);
		pwdPF = new JPasswordField(10);
		createPanel.add(nameLabel);
		createPanel.add(nameTF);
		createPanel.add(pwdLabel);
		createPanel.add(pwdPF);

		resetBtn = new JButton("reset");
		confirmBtn = new JButton("confirm");
		buttonPanel.add(resetBtn);
		buttonPanel.add(confirmBtn);

		setTitle("Regist");
		setLocation((screenWidth - windowWidth) / 2,
				(screenHeight - windowHeight) / 2);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(windowWidth, windowHeight);
		setVisible(true);
	}

}
