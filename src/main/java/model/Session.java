package model;

import java.util.Calendar;

/**
 * Created by danielruizm on 10/16/17.
 */
public class Session {

    private static long SESSION_EXPIRATION = 5*60*1000; //5 mins in millisecs

    long lastAction; // The time this session has been created
    String user; // User associated to this session

    public Session(String user){
        this.user=user;
        this.lastAction=Session.getTimeInMillis();
    }

    public long getLastAction() {
        return lastAction;
    }
    public void setLastAction(long lastAction) {
        this.lastAction = lastAction;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void refresh() {
        setLastAction(Session.getTimeInMillis());
    }

    private static Long getTimeInMillis(){
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis();
    }

    public boolean isAlive() {
        return (Session.getTimeInMillis()-this.getLastAction()<SESSION_EXPIRATION);

    }
}
