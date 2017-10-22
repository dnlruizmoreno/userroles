package model.session;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.HashMap;


public class SessionMemoryImplTest extends TestCase {

    public static final String KEY_SESSION_EXPIRED = "two";
    public static final String TESTKEY_SESSION_ALIVE = "test";
    public static final String KEY_NON_EXISTENT = "NOTEXISTS";
    HashMap<String, Session> sessionsTest;

    private static long SESSION_EXPIRATED = (5L*60*1000)+1; //5 mins in millisecs
    private Session sessionTest;

    private void createSessions(){
        sessionsTest = new HashMap<>();
        sessionTest= new Session();
        Session sessionExpired = new Session();
        sessionExpired.setLastAction(sessionTest.getLastAction()-SESSION_EXPIRATED);
        Session session3 = new Session();
        sessionsTest.put(TESTKEY_SESSION_ALIVE, sessionTest);
        sessionsTest.put(KEY_SESSION_EXPIRED, sessionExpired);
        sessionsTest.put("last", session3);
        SessionMemoryImpl sessionDao = (SessionMemoryImpl) SessionMemoryImpl.getInstance();
        sessionDao.setSessionHashMap(sessionsTest);
    }
    public void testGetSessions() throws Exception {
        createSessions();
        SessionDao sessionDao = SessionMemoryImpl.getInstance();
        Assert.assertEquals(sessionDao.getSessions().size(),sessionsTest.size() );
    }

    public void testCreateSession() throws Exception {
        createSessions();
        SessionDao sessionDao = SessionMemoryImpl.getInstance();
        int precount = sessionDao.getSessions().size();
        sessionDao.createSession(KEY_NON_EXISTENT);
        Assert.assertNotNull(sessionDao.getSession(KEY_NON_EXISTENT));
        Assert.assertEquals(sessionDao.getSessions().size(), precount+1);

    }

    public void testGetSession() throws Exception {
        createSessions();
        SessionDao sessionDao = SessionMemoryImpl.getInstance();
        Assert.assertEquals(sessionDao.getSession(TESTKEY_SESSION_ALIVE), sessionTest);

    }

    public void testHasSession() throws Exception {
        createSessions();
        SessionDao sessionDao = SessionMemoryImpl.getInstance();
        Assert.assertTrue(sessionDao.hasSession(TESTKEY_SESSION_ALIVE));
        Assert.assertFalse(sessionDao.hasSession(KEY_NON_EXISTENT));
        //Expired
        Assert.assertFalse(sessionDao.hasSession(KEY_SESSION_EXPIRED));
    }

    public void testDeleteSession() throws Exception {
        createSessions();
        String keyToDelete= "TODELETE";
        SessionDao sessionDao = SessionMemoryImpl.getInstance();
        Assert.assertFalse(sessionDao.hasSession(keyToDelete));
        sessionDao.createSession(keyToDelete);
        Assert.assertTrue(sessionDao.hasSession(keyToDelete));
        sessionDao.deleteSession(keyToDelete);
        Assert.assertFalse(sessionDao.hasSession(keyToDelete));

    }

    public void testRefreshSession() throws Exception {
        createSessions();
        SessionDao sessionDao = SessionMemoryImpl.getInstance();
        Assert.assertFalse(sessionDao.hasSession(KEY_SESSION_EXPIRED));
        sessionDao.refreshSession(KEY_SESSION_EXPIRED);
        Assert.assertTrue(sessionDao.hasSession(KEY_SESSION_EXPIRED));



    }

}