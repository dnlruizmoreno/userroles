package model.user;

import controller.utils.ConstantsCommon;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import static controller.utils.ConstantsCommon.ROLE_PAGE_3;

public class UsersMemoryImplTest extends TestCase {

   List<User> usersTest;


    private void createUsers() {
        ArrayList<String> listRolesUno = new ArrayList<String>();
        listRolesUno.add(ConstantsCommon.ROLE_ADMIN);
        listRolesUno.add(ConstantsCommon.ROLE_PAGE_1);
        User uno = new User("admin","password",listRolesUno);
        ArrayList<String> listRolesDos = new ArrayList<String>();
        listRolesDos.add(ConstantsCommon.ROLE_PAGE_1);
        listRolesDos.add(ConstantsCommon.ROLE_PAGE_2);
        listRolesDos.add(ROLE_PAGE_3);
        User dos = new User("dos","password",listRolesDos);
        ArrayList<String> listRolesTres = new ArrayList<String>();
        listRolesTres.add(ConstantsCommon.ROLE_PAGE_2);
        User tres = new User("tres","password",listRolesTres);
        usersTest = new ArrayList();
        usersTest.add(uno);
        usersTest.add(dos);
        usersTest.add(tres);
        UsersMemoryImpl userDao = (UsersMemoryImpl) UsersMemoryImpl.getInstance();
        userDao.setUsers(usersTest);
    }

    public void testGetAllUsers() throws Exception {

        createUsers();
        UsersDao user = UsersMemoryImpl.getInstance();
        Assert.assertEquals(user.getAllUsers().size(), usersTest.size());

    }

    public void testHasRights() throws Exception {
        createUsers();
        UsersDao user = UsersMemoryImpl.getInstance();
        Assert.assertTrue(user.hasRights(usersTest.get(0).getName(), usersTest.get(0).getRoles().get(0)));
        Assert.assertFalse((user.hasRights(usersTest.get(0).getName(), "UNEXISTENROLE")));
    }

    public void testExists() throws Exception {
        createUsers();
        UsersDao user = UsersMemoryImpl.getInstance();
        Assert.assertTrue(user.exists(usersTest.get(0).getName()));
        Assert.assertFalse(user.exists("NOUSER"));

    }
    public void testExistsPWD() throws Exception {
        createUsers();
        UsersDao user = UsersMemoryImpl.getInstance();
        Assert.assertTrue(user.exists(usersTest.get(0).getName(), usersTest.get(0).getPassword()));
        Assert.assertFalse(user.exists("NOUSER", "NOPWD"));
    }

    public void testAddUser() throws Exception {
        createUsers();
        UsersDao user = UsersMemoryImpl.getInstance();
        int preaddcount = user.getAllUsers().size();
        User newUser = new User("new", "pwd", null);
        user.addUser(newUser);
        int postaddcount = user.getAllUsers().size();
        Assert.assertEquals(preaddcount+1,postaddcount );
        Assert.assertTrue(user.exists("new","pwd" ));
    }

    public void testGetUser() throws Exception {

        createUsers();
        UsersDao user = UsersMemoryImpl.getInstance();

    }

    public void testUpdateUser() throws Exception {

        createUsers();
        UsersDao user = UsersMemoryImpl.getInstance();

    }

    public void testDeleteUser() throws Exception {

        createUsers();
        UsersDao user = UsersMemoryImpl.getInstance();
        int preaddcount = user.getAllUsers().size();
        user.deleteUser("admin");
        int postaddcount = user.getAllUsers().size();
        Assert.assertEquals(preaddcount-1,postaddcount);
        Assert.assertFalse(user.exists("admin"));

    }

    public void testGetInstance() throws Exception{
        Assert.assertEquals(UsersMemoryImpl.getInstance(), UsersMemoryImpl.getInstance());
    }



}