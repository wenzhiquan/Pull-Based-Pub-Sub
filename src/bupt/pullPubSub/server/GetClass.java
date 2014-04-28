package bupt.pullPubSub.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import bupt.pullPubSub.database.Subscribe;
import bupt.pullPubSub.database.SubscribeDao;

public class GetClass {
	public GetClass(DataInputStream fromClient, DataOutputStream toClient, String username){
		try {
			 //use subscribe list to store user's all subscribe classes
			 List<Subscribe> subscribeList = SubscribeDao.getInstance().selectSubscribeByUserName(username);
			 
			 String subscribeClasses = "";
			 //write all subscribe classes into a string
			 System.out.println(subscribeList.size());
			 for(int i = 0;i<subscribeList.size();i++)
				 subscribeClasses = subscribeClasses.concat(subscribeList.get(i).getTopicName());
			 //write the string to client
			 toClient.writeUTF(subscribeClasses);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
