package bupt.pullPubSub.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

import bupt.pullPubSub.database.UserDao;

public class ChangePwd {
	public ChangePwd(DataInputStream fromClient, DataOutputStream toClient, String username){
		try {
			
			try{
				 //get old password of the user from client
				 String oldPwd = fromClient.readUTF();
				 //if the old password is right
				 if(oldPwd.equals(UserDao.getInstance().selectUserByName(username).getPassword())){
					 //write true to client
					 toClient.writeUTF("true");
					 //get new password from client and update the password of the user
					 if(UserDao.getInstance().updateUser(fromClient.readUTF(), username))
						 //if success, write true to client
						 toClient.writeUTF("true");
					 else{
						 //if fail, write true to client
						 toClient.writeUTF("false");
					 }
				 }
				//if the old password is wrong
				 else{
					 //write false to client
					 toClient.writeUTF("false");
				 }
			}catch(InterruptedIOException ex){
				System.err.print("timeout on read");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
