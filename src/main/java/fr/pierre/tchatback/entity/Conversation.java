package fr.pierre.tchatback.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;

import com.sun.istack.NotNull;

@Entity
public class Conversation {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotNull
    private String name;
    
    @NotNull
    private Date creationDate;
    

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(joinColumns = @JoinColumn(name = "conversation_id", referencedColumnName = "id" ),
    inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    Set<User> users;

	public Conversation() {
		super();
	}

	public Conversation(String name, Date creationDate) {
		super();
		this.name = name;
		this.creationDate = creationDate;
	}

    @PrePersist
    protected void prePersist() {
        if (this.creationDate == null) creationDate = new Date();
    }

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

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	public void addUser(User user) {
		this.users.add(user);
	}
	
	public void removeUser(User user) {
		this.users.remove(user);
	}
	
	public boolean isUserPresent(User user) {
		return this.users.contains(user);
	}
    
    

}
