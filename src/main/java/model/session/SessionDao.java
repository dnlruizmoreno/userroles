package model.session;

import java.util.HashMap;
import java.util.Map;

public interface SessionDao {

    Map<String, Session> getSessions();

    void createSession(String key);

    Session getSession(String key);

    boolean hasSession(String key);

    void deleteSession(String key);

    void refreshSession(String key);
}
