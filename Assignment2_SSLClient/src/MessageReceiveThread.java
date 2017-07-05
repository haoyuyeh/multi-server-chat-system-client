
/**
 * Author: Hao Yu Yeh 
 * Date: 2016¦~10¤ë12¤é 
 * Project: Assignment2 of Distributed System 
 * Comment: this class is used to receive message from chat server to display
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MessageReceiveThread implements Runnable {

	private SSLSocket sslsocket;
	private State state;
	private boolean debug;

	private BufferedReader in;

	private JSONParser parser = new JSONParser();

	private boolean run = true;

	private MessageSendThread messageSendThread;

	public MessageReceiveThread(SSLSocket socket, State state,
			MessageSendThread messageSendThread, boolean debug)
			throws IOException {
		this.sslsocket = socket;
		this.state = state;
		this.messageSendThread = messageSendThread;
		this.debug = debug;
		// Location of the Java keystore file containing the collection of
		// certificates trusted by this application (trust store).
		// for jar file
		String path = MessageReceiveThread.class.getResource("").getPath()
				.replaceAll("%20", " ").replaceAll("/bin", "") + "mykeystore";
		System.setProperty("javax.net.ssl.trustStore", path);
		// for eclipse run
		// System.setProperty("javax.net.ssl.trustStore", "lib/mykeystore");

		System.setProperty("javax.net.debug", "all");
	}

	@Override
	public void run() {

		try {
			this.in = new BufferedReader(
					new InputStreamReader(sslsocket.getInputStream(), "UTF-8"));
			JSONObject message;
			while (run) {
				message = (JSONObject) parser.parse(in.readLine());
				if (debug) {
					clientGUI.textArea.append(
							"Receiving: " + message.toJSONString() + "\n");
				}
				MessageReceive(sslsocket, message);
			}
			System.exit(0);
			in.close();
			sslsocket.close();
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(null,
					"Message Error: " + e.getMessage(), "Warning",
					JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"Communication Error: " + e.getMessage(), "Warning",
					JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}
	}

	public void MessageReceive(SSLSocket socket, JSONObject message)
			throws IOException, ParseException {
		String type = (String) message.get("type");

		// server reply of #authentication
		if (type.equals("authentication")) {
			boolean approved = Boolean
					.parseBoolean((String) message.get("approved"));

			// terminate program if failed
			if (!approved) {
				socket.close();
				JOptionPane.showMessageDialog(null,
						"username or password is incorrect.", "Warning",
						JOptionPane.WARNING_MESSAGE);
				System.exit(1);
			}
			return;
		}

		// server reply of #newidentity
		if (type.equals("newidentity")) {
			boolean approved = Boolean
					.parseBoolean((String) message.get("approved"));

			// terminate program if failed
			if (!approved) {
				socket.close();
				JOptionPane.showMessageDialog(null,
						state.getIdentity() + " already in use.", "Warning",
						JOptionPane.WARNING_MESSAGE);
				System.exit(1);
			} else {
				clientGUI.connected = true;
			}
			return;
		}

		// server reply of #list
		if (type.equals("roomlist")) {
			JSONArray array = (JSONArray) message.get("rooms");
			// print all the rooms
			clientGUI.textArea.append("List of chat rooms:");
			for (int i = 0; i < array.size(); i++) {
				clientGUI.textArea.append(" " + array.get(i));
			}
			clientGUI.textArea.append("\n");
			return;
		}

		// server sends roomchange
		if (type.equals("roomchange")) {

			// identify whether the user has quit!
			if (message.get("roomid").equals("")) {
				// quit initiated by the current client
				if (message.get("identity").equals(state.getIdentity())) {
					clientGUI.textArea
							.append(message.get("identity") + " has quit!\n");
					in.close();
					System.exit(1);
				} else {
					clientGUI.textArea
							.append(message.get("identity") + " has quit!\n");
				}
				// identify whether the client is new or not
			} else if (message.get("former").equals("")) {
				// change state if it's the current client
				if (message.get("identity").equals(state.getIdentity())) {
					state.setRoomId((String) message.get("roomid"));
				}
				clientGUI.textArea.append(message.get("identity") + " moves to "
						+ (String) message.get("roomid") + "\n");
				// identify whether roomchange actually happens
			} else if (message.get("former").equals(message.get("roomid"))) {
				clientGUI.textArea.append("room unchanged\n");
			}
			// print the normal roomchange message
			else {
				// change state if it's the current client
				if (message.get("identity").equals(state.getIdentity())) {
					state.setRoomId((String) message.get("roomid"));
				}

				clientGUI.textArea.append(message.get("identity")
						+ " moves from " + message.get("former") + " to "
						+ message.get("roomid") + "\n");
			}
			return;
		}

		// server reply of #who
		if (type.equals("roomcontents")) {
			JSONArray array = (JSONArray) message.get("identities");
			clientGUI.textArea.append(message.get("roomid") + " contains");
			for (int i = 0; i < array.size(); i++) {
				clientGUI.textArea.append(" " + array.get(i));
				if (message.get("owner").equals(array.get(i))) {
					clientGUI.textArea.append("*");
				}
			}
			clientGUI.textArea.append("\n");
			return;
		}

		// server forwards message
		if (type.equals("message")) {
			clientGUI.textArea.append(message.get("identity") + ": "
					+ message.get("content") + "\n");
			return;
		}

		// server reply of #createroom
		if (type.equals("createroom")) {
			boolean approved = Boolean
					.parseBoolean((String) message.get("approved"));
			String temp_room = (String) message.get("roomid");
			if (!approved) {
				clientGUI.textArea
						.append("Create room " + temp_room + " failed.\n");
			} else {
				clientGUI.textArea
						.append("Room " + temp_room + " is created.\n");
			}
			return;
		}

		// server reply of # deleteroom
		if (type.equals("deleteroom")) {
			boolean approved = Boolean
					.parseBoolean((String) message.get("approved"));
			String temp_room = (String) message.get("roomid");
			if (!approved) {
				clientGUI.textArea
						.append("Delete room " + temp_room + " failed.\n");
			} else {
				clientGUI.textArea
						.append("Room " + temp_room + " is deleted.\n");
			}
			return;
		}

		// server directs the client to another server
		if (type.equals("route")) {
			String temp_room = (String) message.get("roomid");
			String host = (String) message.get("host");
			int port = Integer.parseInt((String) message.get("port"));

			// connect to the new server
			if (debug) {
				clientGUI.textArea.append(
						"Connecting to server " + host + ":" + port + "\n");
			}

			// Create SSL socket and connect it to the remote server
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
					.getDefault();
			SSLSocket temp_socket = (SSLSocket) sslsocketfactory
					.createSocket(host, port);

			// send #movejoin
			DataOutputStream out = new DataOutputStream(
					temp_socket.getOutputStream());
			JSONObject request = ClientMessages.getMoveJoinRequest(
					state.getIdentity(), state.getRoomId(), temp_room);
			if (debug) {
				clientGUI.textArea
						.append("Sending: " + request.toJSONString() + "\n");
			}
			send(out, request);

			// wait to receive serverchange
			BufferedReader temp_in = new BufferedReader(
					new InputStreamReader(temp_socket.getInputStream()));
			JSONObject obj = (JSONObject) parser.parse(temp_in.readLine());

			if (debug) {
				clientGUI.textArea
						.append("Receiving: " + obj.toJSONString() + "\n");
			}

			// serverchange received and switch server
			if (obj.get("type").equals("serverchange")
					&& obj.get("approved").equals("true")) {
				messageSendThread.switchServer(temp_socket, out);
				switchServer(temp_socket, temp_in);
				String serverid = (String) obj.get("serverid");
				clientGUI.textArea.append(state.getIdentity()
						+ " switches to server " + serverid + "\n");
			}
			// receive invalid message
			else {
				temp_in.close();
				out.close();
				temp_socket.close();
				clientGUI.textArea.append("Server change failed\n");
			}
			return;
		}

		// authentic server directs the client to chat server
		if (type.equals("tochatserver")) {
			String host = (String) message.get("host");
			int port = Integer.parseInt((String) message.get("port"));

			// connect to the chat server
			if (debug) {
				clientGUI.textArea.append(
						"Connecting to server " + host + ":" + port + "\n");
			}

			// Create SSL socket and connect it to the chat server
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
					.getDefault();
			SSLSocket temp_socket = (SSLSocket) sslsocketfactory
					.createSocket(host, port);

			// send #connect
			DataOutputStream out = new DataOutputStream(
					temp_socket.getOutputStream());
			JSONObject request = ClientMessages.getConnectRequest();
			if (debug) {
				clientGUI.textArea
						.append("Sending: " + request.toJSONString() + "\n");
			}
			send(out, request);

			// wait to receive connect
			BufferedReader temp_in = new BufferedReader(
					new InputStreamReader(temp_socket.getInputStream()));
			JSONObject obj = (JSONObject) parser.parse(temp_in.readLine());

			if (debug) {
				clientGUI.textArea
						.append("Receiving: " + obj.toJSONString() + "\n");
			}

			// connect received and switch server
			if (obj.get("type").equals("connect")
					&& obj.get("approved").equals("true")) {
				messageSendThread.switchServer(temp_socket, out);
				switchServer(temp_socket, temp_in);
				// send #newidentity
				JSONObject IDrequest = ClientMessages
						.getNewIdentityRequest(state.getIdentity());
				if (debug) {
					clientGUI.textArea.append(
							"Sending: " + IDrequest.toJSONString() + "\n");
				}
				send(out, IDrequest);
			}
			// receive invalid message
			else {
				temp_in.close();
				out.close();
				temp_socket.close();
				JOptionPane.showMessageDialog(null,
						"Chat server connection failed.", "Warning",
						JOptionPane.WARNING_MESSAGE);
				System.exit(1);
			}
			return;
		}

		if (debug) {
			clientGUI.textArea.append("Unknown Message: " + message + "\n");
		}
	}

	public void switchServer(SSLSocket temp_socket, BufferedReader temp_in)
			throws IOException {
		in.close();
		in = temp_in;
		sslsocket.close();
		sslsocket = temp_socket;
	}

	private void send(DataOutputStream out, JSONObject obj) throws IOException {
		out.write((obj.toJSONString() + "\n").getBytes("UTF-8"));
		out.flush();
	}
}
