/**
 * Author: Hao Yu Yeh 
 * Date: 2016¦~10¤ë12¤é 
 * Project: Assignment2 of Distributed System 
 * Comment: this class is used to store the configuration of a client
 */

public class State {

	private String username;
	private String password;
	private String identity;
	private String roomId;
	
	public State(String username, String password, String identity, String roomId) {
		this.username = username;
		this.password = password;
		this.identity = identity;
		this.roomId = roomId;
		
	}
	
	public synchronized String getRoomId() {
		return roomId;
	}
	public synchronized void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getIdentity() {
		return identity;
	}
	
	
}
