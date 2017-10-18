package model;

import java.util.List;

/**
 * Created by danielruizm on 10/18/17.
 */
public interface UsersDao {
    
   List<User> getAllUsers();

   void addUser( User user );

   User getUser( String name );

   void updateUser( User user );

   void deleteUser( String name );

   boolean hasRights(String name, String role);

   boolean exists(String name);

    boolean exists(String name, String pwd);
}
