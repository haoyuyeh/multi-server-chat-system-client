/**
 * Author: Hao Yu Yeh 
 * Date: 2016¦~10¤ë12¤é 
 * Project: Assignment2 of Distributed System 
 * Comment: this class is used to transform the message into JSon format
 */

import org.json.simple.JSONObject;

public class ClientMessages {
	
	@SuppressWarnings("unchecked")
	public static JSONObject getConnectRequest() {
		JSONObject connect = new JSONObject();
		connect.put("type", "connect");
		return connect;		
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getJoinRoomRequest(String roomid) {
		JSONObject join = new JSONObject();
		join.put("type", "join");
		join.put("roomid", roomid);
		return join;		
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getListRequest() {
		JSONObject list = new JSONObject();
		list.put("type", "list");
		return list;		
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getWhoRequest() {
		JSONObject who = new JSONObject();
		who.put("type", "who");
		return who;		
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getCreateRoomRequest(String roomid) {
		JSONObject create_room = new JSONObject();
		create_room.put("type", "createroom");
		create_room.put("roomid", roomid);
		return create_room;		
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getDeleteRoomRequest(String roomid) {
		JSONObject delete = new JSONObject();
		delete.put("type", "deleteroom");
		delete.put("roomid", roomid);
		return delete;		
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getMessage(String content) {
		JSONObject message = new JSONObject();
		message.put("type", "message");
		message.put("content", content);
		return message;		
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getQuitRequest() {
		JSONObject quit = new JSONObject();
		quit.put("type", "quit");
		return quit;		
	}

	@SuppressWarnings("unchecked")
	public static JSONObject getAuthenticationRequest(String username, String password) {
		JSONObject authentication = new JSONObject();
		authentication.put("type", "authentication");
		authentication.put("username", username);
		authentication.put("password", password);
		return authentication;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getRegistrationRequest(String username, String password) {
		JSONObject registration = new JSONObject();
		registration.put("type", "registration");
		registration.put("username", username);
		registration.put("password", password);
		return registration;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getNewIdentityRequest(String identity) {
		JSONObject newIdentity = new JSONObject();
		newIdentity.put("type", "newidentity");
		newIdentity.put("identity", identity);
		return newIdentity;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getMoveJoinRequest(String identity, String former, String roomid) {
		JSONObject movejoin = new JSONObject();
		movejoin.put("type", "movejoin");
		movejoin.put("identity", identity);
		movejoin.put("former", former);
		movejoin.put("roomid", roomid);
		return movejoin;
	}
}
