package fr.pierre.tchatback.dto;

import java.util.Date;

public class MessageDto {

	private int id;
    private String text;
    private Date creationDate;
    private UserDto user;
    private boolean currentUser=false;
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public UserDto getUser() {
		return user;
	}
	public void setUser(UserDto user) {
		this.user = user;
	}
	public boolean isCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(boolean currentUser) {
		this.currentUser = currentUser;
	}
	
    
}
