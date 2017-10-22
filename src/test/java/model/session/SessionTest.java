package model.session;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SessionTest extends TestCase {
    private static long SESSION_EXPIRATED = (5L*60*1000)+1; //5 mins in millisecs

    public void testIsAlive() throws Exception {

        Session session = new Session();
        Session sessionExpired = new Session();
        sessionExpired.setLastAction(session.getLastAction()-SESSION_EXPIRATED);
        Assert.assertFalse(sessionExpired.isAlive());
    }

}