package bupt.pullPubSub.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class ClientRegist extends JFrame{

	private static final long serialVersionUID = 1L;
	private static int screenWidth;
	private static int screenHeight;
	private static int windowWidth;
	private static int windowHeight;
	private String isRegisted;
	
	private String serverAddress = "localhost";
	private int serverPort = 8000;
	private int timeout = 10000;
	
	public ClientRegist(){
		//set layout as BorderLayout
		setLayout(new BorderLayout());
		
		//get screen's width and height
		screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		//get program window's width and height
		windowWidth = 300;
		windowHeight = 150;
		
		//put components into the frame
		JPanel createPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		
		add(createPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		createPanel.setLayout(new GridLayout(2, 2, 5, 5));
		JLabel nameLabel = new JLabel("username: ");
		JLabel pwdLabel = new JLabel("password: ");
		final JTextField nameTF = new JTextField(10);
		final JPasswordField pwdPF = new JPasswordField(10);
		createPanel.add(nameLabel);
		createPanel.add(nameTF);
		createPanel.add(pwdLabel);
		createPanel.add(pwdPF);
				
		JButton resetBtn = new JButton("reset");
		JButton confirmBtn = new JButton("confirm");
		buttonPanel.add(resetBtn);
		buttonPanel.add(confirmBtn);
		
		setTitle("Regist");
		setLocation((screenWidth - windowWidth)/2, (screenHeight - windowHeight)/2);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(windowWidth, windowHeight);
		setVisible(true);
		
		//Create a socket to connect server
		final Socket socket = new Socket();
		
		try {
			socket.connect(new InetSocketAddress(serverAddress, serverPort));
			//Create an input stream
			final DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			//Create an output stream
			final DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			
			resetBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					System.out.println("Reset button pressed.");
					nameTF.setText(null);
					pwdPF.setText(null);
				}});
			
			//add an action listener to confirm button 
			confirmBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					//Send user name and password and type to server
					String username = nameTF.getText();
					String password = new String(pwdPF.getPassword());
					
					try{
						
						try{							
							//write client's type, username, password and user's type to server
							toServer.writeUTF("regist");
							toServer.writeUTF(username);
							toServer.writeUTF(password);
							toServer.flush();
							
							isRegisted  = fromServer.readUTF();
							
							if(isRegisted.equals("exist")){
								JOptionPane.showMessageDialog(null, "Username has existed! Please try again!");
							}
							else if(isRegisted.equals("true")){
								JOptionPane.showMessageDialog(null, "Regist successful!");
								if(socket != null)
									socket.close();
								dispose();
							}
							else{
								JOptionPane.showMessageDialog(null, "Regist failed!");
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
		new ClientRegist();
	}

}
