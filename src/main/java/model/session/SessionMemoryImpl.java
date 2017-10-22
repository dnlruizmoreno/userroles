package model.session;

import model.user.User;

import java.util.HashMap;

public class SessionMemoryImpl implements SessionDao{

    private static SessionMemoryImpl instance;

    private HashMap<String,Session> sessionHashMap;


    public HashMap<String, Session> getSessionHashMap() {
        return sessionHashMap;
    }

    public void setSessionHashMap(HashMap<String, Session> sessionHashMap) {
        this.sessionHashMap = sessionHashMap;
    }

    private SessionMemoryImpl(){
        sessionHashMap = new HashMap<>();
    }


    public static SessionDao getInstance(){
        if(instance == null) {
            instance = new SessionMemoryImpl();
        }
        return instance;


    }

    @Override
    public HashMap<String, Session> getSessions() {
        return sessionHashMap;
    }

    @Override
    public void createSession(String key) {
        sessionHashMap.put(key, new Session());
    }

    @Override
    public Session getSession(String key) {
        return sessionHashMap.get(key);
    }

    @Override
    public boolean hasSession(String key) {
        return sessionHashMap.containsKey(key) && sessionHashMap.get(key).isAlive();
    }

    @Override
    public void deleteSession(String key) {
        sessionHashMap.remove(key);

    }

    @Override
    public void refreshSession(String key) {
        if (sessionHashMap.containsKey(key)){
            getSession(key).refresh();
        }
    }
}
