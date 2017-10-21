package model.session;

import java.util.Calendar;

/**
 * Created by danielruizm on 10/16/17.
 */
public class Session {

    private static long SESSION_EXPIRATION = 5*60*1000; //5 mins in millisecs

    long lastAction; // The time this session has been created

    public Session(){
        this.lastAction = getTimeInMillis();
    }

    public long getLastAction() {
        return lastAction;
    }
    public void setLastAction(long lastAction) {
        this.lastAction = lastAction;
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
