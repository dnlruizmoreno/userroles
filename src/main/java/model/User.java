package model;

import java.util.List;

public class User {
	
	private List<String> roles;
	private String name;
	private String password;
	
	
	
	public User(){
		super();
	}
	public User(String name, String password, List<String> roles) {
		super();
		this.roles = roles;
		this.name = name;
		this.password = password;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
