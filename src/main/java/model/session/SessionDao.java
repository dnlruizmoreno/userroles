package model.session;

import model.user.User;

import java.util.HashMap;

public interface SessionDao {

    HashMap<String, Session> getSessions();

    void createSession(String key);

    Session getSession(String key);

    boolean hasSession(String key);

    void deleteSession(String key);

    void refreshSession(String key);
}
