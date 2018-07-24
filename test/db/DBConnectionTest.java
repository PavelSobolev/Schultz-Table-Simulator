
package db;

import AccountPreference.Prefernce;
import accountsInfo.AccountStatistics;
import java.io.IOException;
import java.util.ArrayList;
import javafx.util.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class DBConnectionTest {
    
    public DBConnectionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of readPreference method, of class DBConnection.
     */
    @Test
    public void testReadPreference() throws IOException {
        System.out.println("readPreference");
        boolean isStart = false;
        String UId = "";
        DBConnection instance = new DBConnection();
        Prefernce expResult = null;
        Prefernce result = instance.readPreference(isStart, UId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of writeAccountExit method, of class DBConnection.
     */
    @Test
    public void testWriteAccountExit() throws IOException {
        System.out.println("writeAccountExit");
        DBConnection instance = new DBConnection();
        instance.writeAccountExit();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUserName method, of class DBConnection.
     */
    @Test
    public void testGetUserName() throws IOException {
        System.out.println("getUserName");
        DBConnection instance = new DBConnection();
        String expResult = "";
        String result = instance.getUserName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setUserName method, of class DBConnection.
     */
    @Test
    public void testSetUserName() throws IOException {
        System.out.println("setUserName");
        String UserName = "";
        DBConnection instance = new DBConnection();
        instance.setUserName(UserName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setUserId method, of class DBConnection.
     */
    @Test
    public void testSetUserId() throws IOException {
        System.out.println("setUserId");
        String UserId = "";
        DBConnection instance = new DBConnection();
        instance.setUserId(UserId);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUserId method, of class DBConnection.
     */
    @Test
    public void testGetUserId() throws IOException {
        System.out.println("getUserId");
        DBConnection instance = new DBConnection();
        String expResult = "";
        String result = instance.getUserId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of writePrefence method, of class DBConnection.
     */
    @Test
    public void testWritePrefence() throws IOException {
        System.out.println("writePrefence");
        Prefernce UserPrefernce = null;
        DBConnection instance = new DBConnection();
        instance.writePrefence(UserPrefernce);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAccountsList method, of class DBConnection.
     */
    @Test
    public void testGetAccountsList() throws IOException {
        System.out.println("getAccountsList");
        DBConnection instance = new DBConnection();
        ArrayList<Pair<String, Integer>> expResult = null;
        ArrayList<Pair<String, Integer>> result = instance.getAccountsList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of writeAccountName method, of class DBConnection.
     */
    @Test
    public void testWriteAccountName() throws IOException {
        System.out.println("writeAccountName");
        String name = "";
        DBConnection instance = new DBConnection();
        instance.writeAccountName(name);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of writeSessionResults method, of class DBConnection.
     */
    @Test
    public void testWriteSessionResults() throws IOException {
        System.out.println("writeSessionResults");
        ArrayList<Long> Durations = null;
        DBConnection instance = new DBConnection();
        instance.writeSessionResults(Durations);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUserPrefernece method, of class DBConnection.
     */
    @Test
    public void testGetUserPrefernece() throws IOException {
        System.out.println("getUserPrefernece");
        DBConnection instance = new DBConnection();
        Prefernce expResult = null;
        Prefernce result = instance.getUserPrefernece();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAccountStatistics method, of class DBConnection.
     */
    @Test
    public void testGetAccountStatistics() throws IOException {
        System.out.println("getAccountStatistics");
        DBConnection instance = new DBConnection();
        ArrayList<AccountStatistics> expResult = null;
        ArrayList<AccountStatistics> result = instance.getAccountStatistics();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
