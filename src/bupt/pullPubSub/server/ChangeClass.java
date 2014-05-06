package bupt.pullPubSub.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import bupt.pullPubSub.database.SubscribeDao;

public class ChangeClass {
	private boolean result = false;
	private String subClasses;

	public ChangeClass(DataInputStream fromClient, DataOutputStream toClient,
			String username) {

		try {

			try {

				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
				String sdf = format.format(new Date());
				// get user's subscribe classes from client
				subClasses = fromClient.readUTF();
				int selectedNum = fromClient.readInt();
				// delete the old subscribe classes
				SubscribeDao.getInstance().deleteSubscribe(username);
				// if client subscribe nothing
				if (subClasses.equals("")) {
					toClient.writeUTF("true");
					toClient.writeUTF(sdf);
				} else {
					// if client subscribe news
					String[] s;
					s = subClasses.split("\n");
					for (int i = 0; i < selectedNum; i++) {
						result = SubscribeDao.getInstance().saveSubscribe(
								username, s[i]);
					}

					// if write data into database successfully
					if (result == true) {
						// write true to client
						toClient.writeUTF("true");
						toClient.writeUTF(sdf);
					}
					// if write data into database failed
					else
						// write false to client
						toClient.writeUTF("false");
				}
			} catch (InterruptedIOException ex) {
				System.err.print("timeout on read");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getSubClasses() {
		return subClasses;
	}

	public void setSubClasses(String subClasses) {
		this.subClasses = subClasses;
	}

}
