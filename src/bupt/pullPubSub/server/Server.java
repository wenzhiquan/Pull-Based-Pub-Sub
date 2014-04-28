package bupt.pullPubSub.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class Server extends JFrame{

	private static final long serialVersionUID = 1L;
	private static String username;				//store client's user name
	private JTextArea jta = new JTextArea();	//create a text area
	private int port = 8000;					//set the port
	
	public Server(){
		//Place text area on the frame
		setLayout(new BorderLayout());
		
		add(new JScrollPane(jta), BorderLayout.CENTER);
		
		setTitle("Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		try{
			//Create a server socket
			final ServerSocket serverSocket = new ServerSocket(port);
			jta.append("Server is created at " + new Date() + "\n");
			
			addWindowListener(new WindowListener(){

				public void windowClosing(WindowEvent e) {
						try {
							if(serverSocket != null){
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
			
			while(true){
				//Listen for a connection request
				Socket socket = serverSocket.accept();
				
				//Create a new thread for the connection
				HandleAClient task = new HandleAClient(socket);
				
				//Start a new thread
				new Thread(task).start();
			}			
		}
		catch(IOException ex){
			System.err.println(ex);
		}
	}

	class HandleAClient implements Runnable {
		private Socket socket;

		public HandleAClient(Socket socket){
			this.socket = socket;
		}
		
		public void run() {
				try{
					//Create data input and output stream
					final DataInputStream fromClient = new DataInputStream(socket.getInputStream());
					final DataOutputStream toClient = new DataOutputStream(socket.getOutputStream());
					
					while(true){
						//receive the client's type
						String clientType = fromClient.readUTF();
						
						//if client's type is login
						if(clientType.equals("login")){
							Login login = new Login(fromClient, toClient);
							username = login.getUsername();
							jta.append("user " + login.getUsername() + " login!");
						}//if client's type is register
						else if(clientType.equals("regist")){
							Register regist = new Register(fromClient, toClient);
							if(regist.isResult())
								jta.append("user " + regist.getUser().getUsername() + " regitered!");
						}//if client's type is publish
						else if(clientType.equals("Publish")){
							new ReceiveFile(fromClient, toClient, username);
						}//if client's type is pull information
						else if(clientType.equals("PullInfo")){
							new SendFile(fromClient, toClient, username);
						}//if client's type is change password
						else if(clientType.equals("ChangePwd")){
							new ChangePwd(fromClient, toClient, username);
						}//if client's type is get subscribe classes
						else if(clientType.equals("getClass")){
							new GetClass(fromClient, toClient, username);
						}//if client's type is change subscribe classes
						else if(clientType.equals("ChangeClass")){
							new ChangeClass(fromClient, toClient, username);
						}else if(clientType.equals("socketClose")){
							if(socket != null)
								socket.close();
							System.out.println("socket closed");
							break;
						}
					}
			}
			catch(IOException ex){
				System.err.println(ex);
			}
		}
	
//		/*settle user's login behave*/
//		public void login(DataInputStream fromClient, DataOutputStream toClient){
//			//Create data input and output stream
//			try {
//				
//				try{
//					User user = new User();
//					//Receive user name and password from client
//					user.setUsername(fromClient.readUTF());
//					user.setPassword(fromClient.readUTF());
//					
//					//search the user's name and password whether in the database
//					User userSearch = UserDao.getInstance().selectUserByName(user.getUsername());
//					if(userSearch != null){
//						//if exist, return "true" and user's type to client
//						if(user.getPassword().equals(userSearch.getPassword())){
//							toClient.writeUTF("true");
//							username = userSearch.getUsername();
//							jta.append("user " + username + " login!\n");
//						}
//						//if does not exist, return "false" and 0 to client
//						else{
//							toClient.writeUTF("false");
//						}
//					}else
//						toClient.writeUTF("false");
//				}catch(InterruptedIOException ex){
//					System.err.print("timeout on read");
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			
//		}
//		
//		/*settle user's register behave*/
//		public void regist(DataInputStream fromClient, DataOutputStream toClient){
//			User user = new User();
//			boolean isExist = false;
//			
//			 
//			try {				
//				try{
//					//Receive user name and password from client
//					user.setUsername(fromClient.readUTF());
//					user.setPassword(fromClient.readUTF());
//					
//					//search whether the username is existed
//					List<User> users = UserDao.getInstance().getAllUsers();
//					
//					for(User u:users){
//						if(user.getUsername().equals(u.getUsername())){
//							isExist = true;
//							break;
//						}
//					}
//					//if username does not exist
//					if(!isExist){
//						//if insert successfully
//						if(UserDao.getInstance().saveUser(user)){
//							toClient.writeUTF("true");
//							jta.append("user's name is: " + user.getUsername() + "\n");
//							jta.append("Password is: " + user.getPassword() + "\n");
//						}
//						//if insert failed
//						else{
//							toClient.writeUTF("false");
//						}
//					}
//					//if username has existed
//					else{
//						toClient.writeUTF("exist");
//					}
//				}catch(InterruptedIOException ex){
//					System.err.print("timeout on read");
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			
//		}
//		
//		/*get user's subscribe classes*/
//		public void getClass(DataInputStream fromClient, DataOutputStream toClient){
//			try {
//				 //use subscribe list to store user's all subscribe classes
//				 List<Subscribe> subscribeList = SubscribeDao.getInstance().selectSubscribeByUserName(username);
//				 
//				 String subscribeClasses = "";
//				 //write all subscribe classes into a string
//				 for(int i = 0;i<subscribeList.size();i++)
//					 subscribeClasses = subscribeClasses.concat(subscribeList.get(i).getTopicName());
//				 //write the string to client
//				 toClient.writeUTF(subscribeClasses);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		/*change user's subscribe classes*/
//		public void changeClass(DataInputStream fromClient, DataOutputStream toClient){
//			boolean result = false;
//			String subClasses;
//			
//			try {
//				
//				try{
//					//get user's subscribe classes from client
//					subClasses = fromClient.readUTF();
//					//delete the old subscribe classes
//					SubscribeDao.getInstance().deleteSubscribe(username);
//					//if client subscribe nothing
//					if(subClasses.equals("")){
//						toClient.writeUTF("true");
//					}
//					else{
//						//if client subscribe news
//						if(subClasses.contains("N")){
//							result = SubscribeDao.getInstance().saveSubscribe(username, "News");
//						}
//						//if client subscribe books
//						if(subClasses.contains("B")){
//							result = SubscribeDao.getInstance().saveSubscribe(username, "Books");
//						}
//						//if client subscribe sports
//						if(subClasses.contains("S")){
//							result = SubscribeDao.getInstance().saveSubscribe(username, "Sports");
//						}
//						
//						SimpleDateFormat format=new SimpleDateFormat("yyyyMMddhhmmss");
//						String sdf = format.format(new Date());
//						//if write data into database successfully
//						if(result == true){
//							//write true to client
//							toClient.writeUTF("true");
//							toClient.writeUTF(sdf);
//						}
//						//if write data into database failed
//						else
//							//write false to client
//							toClient.writeUTF("false");
//					}
//				}catch(InterruptedIOException ex){
//					System.err.print("timeout on read");
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		/*change user's password*/
//		public void changePwd(DataInputStream fromClient, DataOutputStream toClient){
//			try {
//				
//				try{
//					 //get old password of the user from client
//					 String oldPwd = fromClient.readUTF();
//					 //if the old password is right
//					 if(oldPwd.equals(UserDao.getInstance().selectUserByName(username).getPassword())){
//						 //write true to client
//						 toClient.writeUTF("true");
//						 //get new password from client and update the password of the user
//						 if(UserDao.getInstance().updateUser(fromClient.readUTF(), username))
//							 //if success, write true to client
//							 toClient.writeUTF("true");
//						 else{
//							 //if fail, write true to client
//							 toClient.writeUTF("false");
//						 }
//					 }
//					//if the old password is wrong
//					 else{
//						 //write false to client
//						 toClient.writeUTF("false");
//					 }
//				}catch(InterruptedIOException ex){
//					System.err.print("timeout on read");
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		public void sendFile(DataInputStream fromClient, DataOutputStream toClient) {
//			String contentToServer;
//			
//			List<Subscribe> subscribeList = new ArrayList<Subscribe>();
//			List<Message> messageList = new ArrayList<Message>();
//			
//			try {
//							
//				try{
//					contentToServer = "";
//					//get user's subscribe classes by user name
//					subscribeList = SubscribeDao.getInstance().selectSubscribeByUserName(username);
//					
//					String startTime = fromClient.readUTF();
//					SimpleDateFormat format=new SimpleDateFormat("yyyyMMddhhmmss");
//					String sdf = format.format(new Date());
//					
//					if(subscribeList.size() != 0){
//						toClient.writeUTF("haveSubscribe");
//						for(int i = 0;i<subscribeList.size();i++){
//							messageList = MessageDao.getInstance().selectMessageByTopicNameAndRefreshTime(
//									subscribeList.get(i).getTopicName(), startTime, sdf);
//							
//							if(messageList.size() > 0){
//								String content = null;  
//								String header;
//								String topicName = subscribeList.get(i).getTopicName();
//								
//								for(int j = 0;j<messageList.size();j++){
//									header = "";
//									header = header.concat(topicName + ("\n--------------------\n"));
//									content = messageList.get(j).getMsgContent();
//									contentToServer = contentToServer.concat(header);
//							        contentToServer = contentToServer.concat(content + '\n');
//							        contentToServer = contentToServer.concat("\n");
//								}
//							}
//						}
//						toClient.writeUTF(contentToServer);
//						toClient.writeUTF(sdf);
//					}else
//						toClient.writeUTF("noSubscribe");
//					
//					
//					
//					
//				}catch(InterruptedIOException ex){
//					System.err.print("timeout on read");
//				}
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		}
//
//		public void receiveFile(DataInputStream fromClient, DataOutputStream toClient) {
//		        try {
//		            try {
//		            	socket.setSoTimeout(timeout);
//		            	
//		            	try{
//			            	String topicName = fromClient.readUTF();
//			            	String msgContent = fromClient.readUTF();
//			            	
//			            	SimpleDateFormat format=new SimpleDateFormat("yyyyMMddhhmmss");
//							String sdf = format.format(new Date());
//			            	
//			                Message message = new Message();
//			                message.setMsgContent(msgContent);
//			                message.setPublisher(username);
//			                message.setTopicName(topicName);
//			                message.setPubTime(sdf);
//			                
//			                boolean isSuccess = MessageDao.getInstance().saveMessage(message);
//			                System.out.println(isSuccess);
//			                if(isSuccess){
//			                	toClient.writeUTF("true");
//			                }
//			                else{
//			                	toClient.writeUTF("false");
//			                }
//		            	}catch(InterruptedIOException ex){
//							System.err.print("timeout on read");
//						}
//		            }catch(Exception e){
//		            	System.err.println(e);
//		            }
//		        } catch (Exception e) {
//		        	System.err.println(e);
//		        }
//		    }
}
	
	public static void main(String[] args) {
		new Server();
	}

}
