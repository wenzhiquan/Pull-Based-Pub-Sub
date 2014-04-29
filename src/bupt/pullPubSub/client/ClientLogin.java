package bupt.pullPubSub.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class ClientLogin extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private static int screenWidth;
	private static int screenHeight;
	private static int windowWidth;
	private static int windowHeight;
	
	private String serverAddress = "localhost";
	private int serverPort = 8000;
	private int timeout = 10000;
	
	
	public ClientLogin(){
		//set layout as BorderLayout
		setLayout(new BorderLayout(5, 5));
		//get screen's width and height
				screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
				screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
				
				//get program window's width and height
				windowWidth = 300;
				windowHeight = 150;
				
				//put components into the frame
				JPanel nameAndPwdPanel = new JPanel();
				JPanel buttonPanel = new JPanel();
				
				add(nameAndPwdPanel, BorderLayout.CENTER);
				add(buttonPanel, BorderLayout.SOUTH);
				
				nameAndPwdPanel.setLayout(new GridLayout(2, 2, 5, 5));
				JLabel nameLabel = new JLabel("username: ");
				JLabel pwdLabel = new JLabel("password: ");
				final JTextField nameTF = new JTextField(10);
				final JPasswordField pwdPF = new JPasswordField(10);
				nameAndPwdPanel.add(nameLabel);
				nameAndPwdPanel.add(nameTF);
				nameAndPwdPanel.add(pwdLabel);
				nameAndPwdPanel.add(pwdPF);
						
				JButton resetBtn = new JButton("reset");
				JButton loginBtn = new JButton("login");
				JButton registBtn = new JButton("regist");
				buttonPanel.add(resetBtn);
				buttonPanel.add(loginBtn);
				buttonPanel.add(registBtn);
				
				setTitle("Login");
				setLocation((screenWidth - windowWidth)/2, (screenHeight - windowHeight)/2);
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setSize(windowWidth, windowHeight);
				setVisible(true);
				
				
		final Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(serverAddress, serverPort));
			//Create an input stream to receive
			final DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			//Create an output stream to send data to the server
			final DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			
			//add actionListener of two buttons
			
			resetBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					nameTF.setText(null);
					pwdPF.setText(null);
				}});
			
			loginBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//Get the username and password
					String username = nameTF.getText();
					String password = new String(pwdPF.getPassword());
					
					try{
					
						try{
							//write client's type, username, password to server
							toServer.writeUTF("login");
							toServer.writeUTF(username);
							toServer.writeUTF(password);
							toServer.flush();
							String result = fromServer.readUTF();
							//read user's type from server
							if(result.equals("true")){
								//if user's type is 1, create publisher's client
								String topics = fromServer.readUTF();
								int topicNum = fromServer.readInt();
								new Client(username, topics, topicNum);
								if(socket != null)
									socket.close();
								dispose();
							}
							else{
								//if username or password is wrong, give a notice
								JOptionPane.showMessageDialog(null, "Illegal user name or password!");
							}
						}catch(InterruptedIOException ex){
							System.err.print("timeout on read");
						}
					}
					catch(IOException ex){
						System.err.println(ex);
					}
			}
			
		});
			
			//add a action listener to register button
			registBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					System.out.println("regist button pressed");
					new ClientRegist();
				}
			});
			
			addWindowListener(new WindowListener(){

				public void windowClosing(WindowEvent e) {
						try {
							toServer.writeUTF("socketClose");
							if(socket != null)
								socket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
				@Override
				public void windowOpened(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void windowClosed(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowIconified(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowDeiconified(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowActivated(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void windowDeactivated(WindowEvent e) {
					// TODO Auto-generated method stub
					
				}

				
				
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Server is closed! Please try latter!");
			e1.printStackTrace();
		}
		try {
			socket.setSoTimeout(timeout);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ClientLogin();

	}

}
