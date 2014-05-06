package bupt.pullPubSub.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class Server extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextArea jta = new JTextArea(); // create a text area
	private int port = 8000; // set the port

	public Server() {
		// Place text area on the frame
		setLayout(new BorderLayout());

		add(new JScrollPane(jta), BorderLayout.CENTER);

		setTitle("Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		try {
			// Create a server socket
			final ServerSocket serverSocket = new ServerSocket(port);
			jta.append("Server is created at " + new Date() + "\n");

			addWindowListener(new WindowListener() {

				public void windowClosing(WindowEvent e) {
					try {
						if (serverSocket != null) {
							serverSocket.close();
							System.out.println("Server socket closed.");
						}

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

			while (true) {
				// Listen for a connection request
				Socket socket = serverSocket.accept();

				// Create a new thread for the connection
				HandleAClient task = new HandleAClient(socket);

				// Start a new thread
				new Thread(task).start();
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	class HandleAClient implements Runnable {
		private Socket socket;
		private String username; // store client's user name

		public HandleAClient(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				// Create data input and output stream
				final DataInputStream fromClient = new DataInputStream(
						socket.getInputStream());
				final DataOutputStream toClient = new DataOutputStream(
						socket.getOutputStream());

				while (true) {
					// receive the client's type
					String clientType = fromClient.readUTF();

					// if client's type is login
					if (clientType.equals("login")) {
						Login login = new Login(fromClient, toClient);
						username = login.getUsername();
						jta.append("user " + login.getUsername() + " login!\n");
					}// if client's type is register
					else if (clientType.equals("regist")) {
						Register regist = new Register(fromClient, toClient);
						if (regist.isResult())
							jta.append("user " + regist.getUser().getUsername()
									+ " regitered!\n");
					}// if client's type is publish
					else if (clientType.equals("Publish")) {
						new ReceiveFile(fromClient, toClient, username);
					}// if client's type is pull information
					else if (clientType.equals("PullInfo")) {
						new SendFile(fromClient, toClient, username, socket);
					}// if client's type is change password
					else if (clientType.equals("ChangePwd")) {
						new ChangePwd(fromClient, toClient, username);
					}// if client's type is get subscribe classes
					else if (clientType.equals("getClass")) {
						System.out.println("getclass: " + username);
						new GetClass(fromClient, toClient, username);
					}// if client's type is change subscribe classes
					else if (clientType.equals("ChangeClass")) {
						new ChangeClass(fromClient, toClient, username);
					}
				}
			} catch (IOException ex) {
//				System.err.println(ex);
			} finally {
				if (socket != null)
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				System.out.println("socket closed");
			}
		}
	}

	public static void main(String[] args) {
		new Server();
	}

}
