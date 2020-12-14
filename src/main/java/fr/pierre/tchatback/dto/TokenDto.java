package fr.pierre.tchatback.dto;

public class TokenDto {

    String value;
    int userId;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
    
}
