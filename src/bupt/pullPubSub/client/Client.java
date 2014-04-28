package bupt.pullPubSub.client;

import java.awt.*;
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Client extends JFrame{

	/**
	 * 
	 */
	private String serverAddress = "localhost";
	private int serverPort = 8000;
	private int timeout = 10000;
	
	private static final long serialVersionUID = 1L;
	private JTabbedPane jtp = new JTabbedPane();
	private ScrollPane spInfo = new ScrollPane();
	private JPanel jpInfo = new JPanel();
	private JTextArea jta = new JTextArea();
	private JButton refreshBtn = new JButton();
	
	private JPanel jpSub = new JPanel();
	private JPanel subClasses = new JPanel();
	private JCheckBox jckNewsSub = new JCheckBox();
	private JCheckBox jckBooksSub = new JCheckBox();
	private JCheckBox jckSportsSub = new JCheckBox();
	private JButton subSubmit = new JButton("Submit");
	
	private JPanel jpPub = new JPanel();
	private JTextField jtfTitle = new JTextField();
	private JTextArea jtfContent = new JTextArea();
	private JRadioButton jrbNewsPub = new JRadioButton();
	private JRadioButton jrbBooksPub = new JRadioButton();
	private JRadioButton jrbSportsPub = new JRadioButton();
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
	
	private int windowWidth = 320;
	private int windowHeight = 450;
	
	private String subToServer;
	
	public Client(){
		
	}
	
	public Client(final String username){
		
		/*initialize the publisher's client*/
		initialFrame(username);
		
		final Socket socket = new Socket();
		
		try {
			socket.connect(new InetSocketAddress(serverAddress, serverPort));
			final DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			final DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
			
			try {
				socket.setSoTimeout(timeout);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			/*only one radio button can be chose*/
			jrbNewsPub.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					jrbBooksPub.setSelected(false);
					jrbSportsPub.setSelected(false);
				}
				
			});
			
			jrbBooksPub.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					jrbNewsPub.setSelected(false);
					jrbSportsPub.setSelected(false);
				}
				
			});
			
			jrbSportsPub.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					jrbBooksPub.setSelected(false);
					jrbNewsPub.setSelected(false);
				}
				
			});
			
			refreshBtn.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					String[] s;

					try{					
						
						try{
							toServer.writeUTF("PullInfo");
							
							toServer.writeUTF(getStartTime(username));
							
							if(fromServer.readUTF().equals("noSubscribe")){
								JOptionPane.showMessageDialog(null, "No subscribe! Please select at least one!");
							}else{
								s = fromServer.readUTF().split("\n");
								if(s.length == 1){
									JOptionPane.showMessageDialog(null, "No new message! Please try latter!");
								}else{
									for (String x : s) {
										jta.append(x + "\n"); 
								    }
									jta.append("\n");
									
									storeStartTime(fromServer.readUTF(), username);
								}
							}
						}catch(InterruptedIOException ex){
							System.err.print("timeout on read");
						}
					}catch(IOException ex){
						System.err.println(ex);
					}
				}
				
			});
			
			//listen the pub submit button
			pubSubmit.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					boolean isClassChosen;
					String pubContentToServer;
					
					isClassChosen = false;
					pubContentToServer = "";
					
					
					try {
						
		                if(jrbNewsPub.isSelected() || jrbBooksPub.isSelected() || jrbSportsPub.isSelected())
		                	isClassChosen = true;
						
						if(isClassChosen == true){
			                try{
				                //send file to server
				                toServer.writeUTF("Publish");
								//write file's class
								if(jrbNewsPub.isSelected() == true){
									toServer.writeUTF("News");
								}else if(jrbBooksPub.isSelected() == true){
									toServer.writeUTF("Books");
								}else if(jrbSportsPub.isSelected() == true){
									toServer.writeUTF("Sports");
								}
								//split data with "\n"
								String[] s = jtfContent.getText().split("\n");
								s = jtfContent.getText().split("\n");
								for (String x : s) {
									pubContentToServer = pubContentToServer.concat(x + "\n");				      
								}
								
								toServer.writeUTF(pubContentToServer);
					               
				                toServer.flush();
				                
				                if(fromServer.readUTF().equals("true"))
				                	JOptionPane.showMessageDialog(null, "Publish successfully!");
				                else
				                	JOptionPane.showMessageDialog(null, "Publish failed!");
				                
			                }catch(InterruptedIOException ex){
								System.err.print("timeout on read");
							}
						}
						else
							JOptionPane.showMessageDialog(null, "No class is chosen! Please choose one.");
		                
					}
		            catch (Exception ex) {
		            	ex.printStackTrace();
		            }
					

					
				}
				
			});

			//listen the sub submit button
			subSubmit.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					try{
						subToServer = "";
						
						if(jckNewsSub.isSelected() == true){
							subToServer = subToServer.concat("N");
						}
						if(jckBooksSub.isSelected() == true){
							subToServer = subToServer.concat("B");
						}
						if(jckSportsSub.isSelected() == true){
							subToServer = subToServer.concat("S");
						}
						
						try{
							toServer.writeUTF("ChangeClass");
							toServer.writeUTF(subToServer);
							
							if(fromServer.readUTF().equals("true")){
								JOptionPane.showMessageDialog(null, "Successful!");
								storeStartTime(fromServer.readUTF(), username);
							}
							else
								JOptionPane.showMessageDialog(null, "Failed!");
							
							
						}catch(InterruptedIOException ex){
							System.err.print("timeout on read");
						}
					}
					catch(IOException ex){
						
					}
				}
				
			});
			
			confirmBtn.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					try{
						
						try{
							
							String oldPwd = new String(jpfOldPwd.getPassword());
							String newPwd = new String(jpfNewPwd.getPassword());
							String confirmPwd = new String(jpfConfirmPwd.getPassword());
							
							if(oldPwd.equals(newPwd)){
								JOptionPane.showMessageDialog(null, "New password is same as the old one!try again!");
								jpfOldPwd.setText(null);
								jpfNewPwd.setText(null);
								jpfConfirmPwd.setText(null);
							}else{
								toServer.writeUTF("ChangePwd");
								toServer.writeUTF(oldPwd);
								if(fromServer.readUTF().equals("true")){
									if(newPwd.equals(confirmPwd)){
										toServer.writeUTF(newPwd);
										if(fromServer.readUTF().equals("true")){
											JOptionPane.showMessageDialog(null, "Successful!");
											jpfOldPwd.setText(null);
											jpfNewPwd.setText(null);
											jpfConfirmPwd.setText(null);
										}
										else
											JOptionPane.showMessageDialog(null, "failed!");
									}
									else{
										JOptionPane.showMessageDialog(null, "two new password are not matched!Try again!");
										jpfNewPwd.setText(null);
										jpfConfirmPwd.setText(null);
									}
								}
								else{
									JOptionPane.showMessageDialog(null, "Wrong old password!Try again!");
									jpfOldPwd.setText(null);
								}
							}
						}catch(InterruptedIOException ex){
							System.err.print("timeout on read");
						}
					}catch(IOException ex){
						System.err.println(ex);
					}
				}
				
			});
		
			jtp.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent e) {
					int selectedIndex = jtp.getSelectedIndex();

					if(selectedIndex == 1){
						try{
							
							try{								
								toServer.writeUTF("getClass");
								String subscribeClasses = fromServer.readUTF();
								if(subscribeClasses.contains("News"))
									jckNewsSub.setSelected(true);
								if(subscribeClasses.contains("Books"))
									jckBooksSub.setSelected(true);
								if(subscribeClasses.contains("Sports"))
									jckSportsSub.setSelected(true);
							}catch(InterruptedIOException ex){
								System.err.print("timeout on read");
							}
						}catch(IOException ex){
							System.err.println(ex);
						}
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
	}
	
	public void initialFrame(String username){
		jpInfo.setLayout(new BorderLayout());
		jta.setSize(10, 5);
		jta.setLineWrap(true);
		jta.setEditable(false);
		refreshBtn.setText("refresh");
		jpInfo.add(spInfo, BorderLayout.CENTER);
		jpInfo.add(refreshBtn, BorderLayout.SOUTH);
		spInfo.add(jta);
		
		jckNewsSub.setText("News");
		jckBooksSub.setText("Books");
		jckSportsSub.setText("Sports");
		jrbNewsPub.setText("News");
		jrbBooksPub.setText("Books");
		jrbSportsPub.setText("Sports");
		
		subClasses.setBorder(new TitledBorder("Classes"));
		subClasses.add(jckNewsSub);
		subClasses.add(jckBooksSub);
		subClasses.add(jckSportsSub);
		jpSub.setLayout(new BorderLayout());
		jpSub.add(subClasses, BorderLayout.CENTER);
		jpSub.add(subSubmit, BorderLayout.SOUTH);
		
		jtfTitle.setText("Title");
		jtfContent.setText("Content");
		spPub.add(jtfContent);
		pubClasses.add(jrbNewsPub);
		pubClasses.add(jrbBooksPub);
		pubClasses.add(jrbSportsPub);
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
	
	public String getStartTime(String username){
		String startTime = "0";
	
		File file = new File(username + "_startTimeToRecvMsg.txt");
		
		if(file.exists()){
			try {
				Scanner input = new Scanner(file);
				//Read data from a file
				if(input.hasNext())
					startTime = input.nextLine();
				
				//Close the file
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	}
		return startTime;
}
	
	public void storeStartTime(String startTime, String username){
		
		File file = new File(username + "_startTimeToRecvMsg.txt");
		
		PrintWriter output;
		
		try {
			output = new PrintWriter(file);
			
			//Write formatted output to the file
			output.println(startTime);
			
			//Close the file
			output.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		
		
	}
	
	public static void main(String[] args) {
		new Client("admin");
	}

}
