package fr.pierre.tchatback.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(unique = true)
    private String providerId;
    
    @NotNull
    private String email;
    
    @NotNull
    private String name;
    
    private String pictureUrl;
    
    @NotNull
    private String password;
    
    @NotNull
    private String provider;
    
    @NotNull
    private Date creationDate;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id" ),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();
    
    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private Set<Conversation> conversations = new HashSet<>();

    public User() {
    }
    
    

    public User(String providerId, String email, String name, String pictureUrl, String password, String provider) {
		super();
		this.providerId = providerId;
		this.email = email;
		this.name = name;
		this.pictureUrl = pictureUrl;
		this.password = password;
		this.provider = provider;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    

    public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Set<Conversation> getConversations() {
		return conversations;
	}

	public void setConversations(Set<Conversation> conversations) {
		this.conversations = conversations;
	}
    
    
}

