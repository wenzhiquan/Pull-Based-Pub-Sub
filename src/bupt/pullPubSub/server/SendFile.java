package bupt.pullPubSub.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bupt.pullPubSub.database.Message;
import bupt.pullPubSub.database.MessageDao;
import bupt.pullPubSub.database.Subscribe;
import bupt.pullPubSub.database.SubscribeDao;

public class SendFile {
	private String contentToServer;
	
	private List<Subscribe> subscribeList = new ArrayList<Subscribe>();
	private List<Message> messageList = new ArrayList<Message>();
	
	public SendFile(DataInputStream fromClient, DataOutputStream toClient, String username) {
		
		try {
						
			try{
				contentToServer = "";
				//get user's subscribe classes by user name
				subscribeList = SubscribeDao.getInstance().selectSubscribeByUserName(username);
				
				String startTime = fromClient.readUTF();
				SimpleDateFormat format=new SimpleDateFormat("yyyyMMddhhmmss");
				String sdf = format.format(new Date());
				
				if(subscribeList.size() != 0){
					toClient.writeUTF("haveSubscribe");
					for(int i = 0;i<subscribeList.size();i++){
						messageList = MessageDao.getInstance().selectMessageByTopicNameAndRefreshTime(
								subscribeList.get(i).getTopicName(), startTime, sdf);
						
						if(messageList.size() > 0){
							String content = null;  
							String header;
							String topicName = subscribeList.get(i).getTopicName();
							
							for(int j = 0;j<messageList.size();j++){
								header = "";
								header = header.concat(topicName + ("\n--------------------\n"));
								content = messageList.get(j).getMsgContent();
								contentToServer = contentToServer.concat(header);
						        contentToServer = contentToServer.concat(content + '\n');
						        contentToServer = contentToServer.concat("\n");
							}
						}
					}
					toClient.writeUTF(contentToServer);
					toClient.writeUTF(sdf);
				}else
					toClient.writeUTF("noSubscribe");
				
				
				
				
			}catch(InterruptedIOException ex){
				System.err.print("timeout on read");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
