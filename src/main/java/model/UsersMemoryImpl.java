package model;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


//TODO Streams!!
public class UsersMemoryImpl implements UsersDao{

	private static UsersMemoryImpl instance;

	private UsersMemoryImpl(){
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

	public List<User> getAllUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public boolean hasRights(String name, String role){
		boolean allowed = false;
		for (User user : getAllUsers()) {
			if ( user.getName().equals(name)){
				return user.getRoles().contains(role);
			}
		}
		return false;
	}

	public boolean exists(String name){
		return getUser(name)!=null;

	}

	public boolean exists(String name, String pwd) {
		for (User user : getAllUsers()) {
			if (user.getName().equals(name) ){
				return true;
			}
		}
		return false;
	}


	public boolean addUser(User user) {
		if (getUser(user.getName())!= null){
			getAllUsers().add(user);
			return true;
		}
		return false;


	}

	public User getUser(String name) {
		for (User user : getAllUsers()) {
			if (user.getName().equals(name)){
				return user;
			}
		}
		return null;

	}

	public boolean updateUser(User user) {
		ListIterator<User> iterator = getAllUsers().listIterator();
		while (iterator.hasNext()) {
			User next = iterator.next();
			if (next.equals(user)) {
				//Replace element
				iterator.set(user);
				return true;
			}
		}
		return false;

	}

	public boolean deleteUser(String name) {

		ListIterator<User> iterator = getAllUsers().listIterator();
		while (iterator.hasNext()) {
			User next = iterator.next();
			if (next.getName().equals(name)) {
				iterator.remove();
				return true;
			}
		}
		return false;

	}
	public static UsersMemoryImpl getInstance(){
		if(instance == null) {
			instance = new UsersMemoryImpl();
		}
		return instance;


	}
}
