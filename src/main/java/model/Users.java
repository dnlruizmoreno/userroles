package model;

import java.util.ArrayList;
import java.util.List;

public class Users {

	public Users(){
		ArrayList<String> listRolesUno = new ArrayList<String>();
		listRolesUno.add("admin");
		User uno = new User("admin","password",listRolesUno);
		ArrayList<String> listRolesDos = new ArrayList<String>();
		listRolesDos.add("page1");
		listRolesDos.add("page2");
		listRolesDos.add("page3");
		User dos = new User("name","password",listRolesDos);
		ArrayList<User> users = new ArrayList();
		users.add(uno);
		users.add(dos);
		setUsers(users);
		
	}
	private List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public boolean hasRights(String name, String password, String role){
		boolean allowed = false;
		for (User user : getUsers()) {
			if (user.getPassword().equals(password) && user.getName().equals(name)){
				allowed = user.getRoles().contains(role);
				break;
			}
		}
		return allowed;
	}
}
