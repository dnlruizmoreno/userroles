package model.user;

import controller.utils.ConstantsCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;



public class UsersMemoryImpl implements UsersDao{

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


	private static UsersMemoryImpl instance;

	private List<User> users;

	public List<User> getUsers() {
		return users;
	}

	private UsersMemoryImpl(){
		ArrayList<String> listRolesUno = new ArrayList<String>();
		listRolesUno.add(ConstantsCommon.ROLE_ADMIN);
		listRolesUno.add(ConstantsCommon.ROLE_PAGE_1);
		User uno = new User("admin","password",listRolesUno);
		ArrayList<String> listRolesDos = new ArrayList<String>();
		listRolesDos.add(ConstantsCommon.ROLE_PAGE_1);
		listRolesDos.add(ConstantsCommon.ROLE_PAGE_2);
		listRolesDos.add(ConstantsCommon.ROLE_PAGE_3);
		User dos = new User("dos","password",listRolesDos);
		ArrayList<String> listRolesTres = new ArrayList<String>();
		listRolesTres.add(ConstantsCommon.ROLE_PAGE_2);
		User tres = new User("tres","password",listRolesTres);
		users = new ArrayList();
		users.add(uno);
		users.add(dos);
		users.add(tres);

		LOGGER.debug("There are {} users", users.size() );
		for (User user:users) {
			LOGGER.debug("user {}, pwd {}", user.getName(), user.getPassword());
			for(String role:user.getRoles()){
				LOGGER.debug("has roles {}", role);
			}
		}

	}

	public List<User> getAllUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public boolean hasRights(String name, String role){
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
		if (null == getUser(user.getName())){
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
		if (exists(user.getName())) {
			ListIterator<User> iterator = getAllUsers().listIterator();
			while (iterator.hasNext()) {
				User next = iterator.next();
				if (next.equals(user)) {
					//Replace element
					iterator.set(user);
					return true;
				}
			}
		}
		getAllUsers().add(user);
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
	public static UsersDao getInstance(){
		if(instance == null) {
			instance = new UsersMemoryImpl();
		}
		return instance;


	}
}
