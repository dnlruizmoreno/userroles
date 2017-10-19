package model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

	@Expose
	private String name;
	@Expose
	private List<String> roles;
	@Expose(serialize = false)
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;

		User user = (User) o;

		return getName().equals(user.getName());
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}
