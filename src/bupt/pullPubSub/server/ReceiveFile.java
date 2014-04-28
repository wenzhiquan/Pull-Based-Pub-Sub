package bupt.pullPubSub.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InterruptedIOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bupt.pullPubSub.database.Message;
import bupt.pullPubSub.database.MessageDao;

public class ReceiveFile {
	
	public ReceiveFile(DataInputStream fromClient, DataOutputStream toClient, String username) {
        try {
            try {
            	
            	try{
	            	String topicName = fromClient.readUTF();
	            	String msgContent = fromClient.readUTF();
	            	
	            	SimpleDateFormat format=new SimpleDateFormat("yyyyMMddhhmmss");
					String sdf = format.format(new Date());
	            	
	                Message message = new Message();
	                message.setMsgContent(msgContent);
	                message.setPublisher(username);
	                message.setTopicName(topicName);
	                message.setPubTime(sdf);
	                
	                boolean isSuccess = MessageDao.getInstance().saveMessage(message);
	                System.out.println(isSuccess);
	                if(isSuccess){
	                	toClient.writeUTF("true");
	                }
	                else{
	                	toClient.writeUTF("false");
	                }
            	}catch(InterruptedIOException ex){
					System.err.print("timeout on read");
				}
            }catch(Exception e){
            	System.err.println(e);
            }
        } catch (Exception e) {
        	System.err.println(e);
        }
    }
}

