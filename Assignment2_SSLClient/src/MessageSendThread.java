
/**
 * Author: Hao Yu Yeh 
 * Date: 2016¦~10¤ë12¤é 
 * Project: Assignment2 of Distributed System 
 * Comment: this class is used to send message from text input to chat server
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

public class MessageSendThread implements Runnable {

	private SSLSocket sslsocket;

	private DataOutputStream out;

	private State state;

	private boolean debug;

	public MessageSendThread(SSLSocket socket, State state, boolean debug) throws IOException {
		this.sslsocket = socket;
		this.state = state;
		out = new DataOutputStream(sslsocket.getOutputStream());
		this.debug = debug;
		//Location of the Java keystore file containing the collection of 
		//certificates trusted by this application (trust store).
		// for jar file
		String path = MessageSendThread.class.getResource("").getPath().replaceAll("%20", " ").replaceAll("/bin", "")+"mykeystore";
		System.setProperty("javax.net.ssl.trustStore", path);
		// for eclipse run
//		System.setProperty("javax.net.ssl.trustStore", "lib/mykeystore");
		
		System.setProperty("javax.net.debug","all");
	}

	@Override
	public void run() {

		try {
			// send the #authentication command
			MessageSend(sslsocket, "#authentication " + state.getUsername()
			+ " " + state.getPassword());
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		// listen for input
		clientGUI.textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String msg = clientGUI.textField.getText();
				try {
					MessageSend(sslsocket, msg);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,
							"Communication Error: " + e.getMessage(), "Warning",
							JOptionPane.WARNING_MESSAGE);
					System.exit(1);
				}
				clientGUI.textField.setText(null);
			}
		});
	}

	private void send(JSONObject obj) throws IOException {
		if (debug) {
			clientGUI.textArea.append("Sending: " + obj.toJSONString() + "\n");
		}
		out.write((obj.toJSONString() + "\n").getBytes("UTF-8"));
		out.flush();
	}

	// send command and check validity
	public void MessageSend(SSLSocket socket, String msg) throws IOException {
		JSONObject sendToServer = new JSONObject();
		String[] array = msg.split(" ");
		if (!array[0].startsWith("#")) {
			sendToServer = ClientMessages.getMessage(msg);
			send(sendToServer);
		} else if (array.length == 1) {
			if (array[0].startsWith("#list")) {
				sendToServer = ClientMessages.getListRequest();
				send(sendToServer);
			} else if (array[0].startsWith("#quit")) {
				sendToServer = ClientMessages.getQuitRequest();
				send(sendToServer);
			} else if (array[0].startsWith("#who")) {
				sendToServer = ClientMessages.getWhoRequest();
				send(sendToServer);
			} else {
				clientGUI.textArea.append("Invalid command!\n");
			}
		} else if (array.length == 2) {
			if (array[0].startsWith("#joinroom")) {
				sendToServer = ClientMessages.getJoinRoomRequest(array[1]);
				send(sendToServer);
			} else if (array[0].startsWith("#createroom")) {
				sendToServer = ClientMessages.getCreateRoomRequest(array[1]);
				send(sendToServer);
			} else if (array[0].startsWith("#deleteroom")) {
				sendToServer = ClientMessages.getDeleteRoomRequest(array[1]);
				send(sendToServer);
			} else if (array[0].startsWith("#newidentity")) {
				sendToServer = ClientMessages.getNewIdentityRequest(array[1]);
				send(sendToServer);
			} else {
				clientGUI.textArea.append("Invalid command!\n");
			}
		} else if (array.length == 3) {
			if (array[0].startsWith("#authentication")) {
				sendToServer = ClientMessages.getAuthenticationRequest(array[1],
						array[2]);
				send(sendToServer);
			} else if (array[0].startsWith("#registration")) {
				sendToServer = ClientMessages.getRegistrationRequest(array[1],
						array[2]);
				send(sendToServer);
			} else {
				clientGUI.textArea.append("Invalid command!\n");
			}
		} else {
			clientGUI.textArea.append("Invalid command!\n");
		}

	}

	public void switchServer(SSLSocket temp_socket, DataOutputStream temp_out)
			throws IOException {
		// switch server initiated by the receiving thread
		// need to use synchronize
		synchronized (out) {
			out.close();
			out = temp_out;
		}
		sslsocket = temp_socket;
	}
}
