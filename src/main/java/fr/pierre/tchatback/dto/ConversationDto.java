package fr.pierre.tchatback.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class ConversationDto {
	
    private int id;
    private String name;
    private Date creationDate;
    Set<UserDto> users;
    List<MessageDto> messages;
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Set<UserDto> getUsers() {
		return users;
	}
	public void setUsers(Set<UserDto> users) {
		this.users = users;
	}
	public List<MessageDto> getMessages() {
		return messages;
	}
	public void setMessages(List<MessageDto> messages) {
		this.messages = messages;
	}
	

}
