package db;


import accountsInfo.AccountStatistics;
import accountsInfo.AccountStatisticsBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.util.Pair;
import enumerations.AfterClickEffects;
import AccountPreference.Prefernce;
import enumerations.StringSource;


/**
 * class establishes connection with local SQLite database
 * @author Pavel Sobolev
 */
public class DBConnection {
    
    /**
     * parameter for constructing local DB path
     */
    private String UserDir = System.getenv("userprofile") + "\\Schulte\\";
    /**
     * default user name
     */
    private String UserName = System.getProperty("user.name");
    /**
     * the ability to use BD after starting of the program
     */
    public static boolean CanUseDB = true;
    /**
     * object for operating connection with local DB
     */
    private Connection conn;    
    /**
     * current account ID
     */
    private String UserId;
    

    /**
     * creates connection with DB
     * creates DB it does not exist 
     * creates new tables and inserts default user account and default user preference
     * @throws IOException 
     */
    public DBConnection() throws IOException
    {        
        // detect OS version; if not windows, use different way of defining of path to the database
        if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
        {
            UserDir = System.getenv("userprofile") + "\\Schulte\\";
            System.out.println(UserDir);
        }    
        else
        {
            UserDir = System.getenv("HOME") + "/Schulte/";
            System.out.println(UserDir);
        }
        
        Path UserDirPath = Paths.get(UserDir);
        
        boolean isNewDB = false;
        if (!Files.exists(UserDirPath))
        {
            isNewDB = true;
            Files.createDirectory(UserDirPath);
        }
                
        String url = "jdbc:sqlite:" + UserDir + "AccountStat.db";
        
        if (!Files.exists(Paths.get(UserDir + "AccountStat.db"))) isNewDB = true;
        
        try 
        {
            conn = DriverManager.getConnection(url);
        } 
        catch (SQLException ex) 
        {
            CanUseDB = false;
            return;
        }        
        
        if (isNewDB) // if DB does not exist on the computer - create DB and insert default account and default settings
        {
            String CreateQuery[];
            try 
            {
                CreateQuery = Files.readAllLines(Paths.get(this.getClass().getResource("CreateDB.sql").toURI())).stream().collect(Collectors.joining("\n")).split(";");
            } 
            catch (URISyntaxException ex) 
            {
                CanUseDB = false;
                return;
            }

            try 
            {
                // create structure of new user database
                for(String query : CreateQuery) conn.createStatement().execute(query);         
                
                // insert new data: default account and default settings                
                PreparedStatement newAccountStatement = conn.prepareStatement("INSERT INTO Accounts(Name,CreationDate, ExitDate,ExitTime) VALUES(?,?,?,?)");
                newAccountStatement.setString(1, "The one");
                newAccountStatement.setDate(2, Date.valueOf(LocalDate.now()));
                newAccountStatement.setDate(3, Date.valueOf(LocalDate.now()));
                newAccountStatement.setTime(4, Time.valueOf(LocalTime.now()));
                newAccountStatement.executeUpdate();
                
                PreparedStatement newSettings = 
                        conn.prepareStatement("INSERT INTO AccountPref(AccID, meshCount, meshSize,  symbolSource,             rotate, afterClick) " + 
                                                               "VALUES(1,     5,         80,        'Consecutive integers',   1,      'Pale the cell')");
                newSettings.executeUpdate();
            } 
            //finally //
            catch (SQLException ex) 
            {
                CanUseDB = false;
                return;
            }
        }        
    }
    
    // isStart - 
    // UId - 
    /**
     * read preferences of the account form DB (if there is no preference, returns default preference) 
     * @param isStart read setting just when program starts (UId is not considered)
     * @param UId account id
     * @return reference to newly constructed Preference class instance
     */
    public Prefernce readPreference(boolean isStart, String UId)
    {
        Prefernce resultPref = null;
        boolean result = true;
        if (!CanUseDB) 
        {
            return new Prefernce(5, 70, StringSource.Numbers, false, true, 
                    AfterClickEffects.Pale, false);
        }
             
        try
        {
            // find the account which was used the last time and open its settings 
            String getLastUser = "select * from Accounts order by ExitDate, ExitTime desc Limit 1";
            Statement st = conn.createStatement();
            ResultSet lastAccount = st.executeQuery(getLastUser);
            UserName = lastAccount.getString("Name");
            UserId = (!isStart)?UId:lastAccount.getString("ID");            
            
            String getUserPref = "select * from AccountPref where AccID=" + UserId;
            ResultSet userPref = st.executeQuery(getUserPref);
            
            resultPref = new Prefernce(
                    userPref.getInt("meshCount"),
                    userPref.getInt("meshSize"),
                    Prefernce.getStrSourceByName(userPref.getString("symbolSource")),
                    userPref.getBoolean("rotate"),
                    true,
                    Prefernce.getAfterActionsByName(userPref.getString("afterClick")),
                    !isStart);
            result = true;
        }
        catch(SQLException ex)
        {
            result = false;
        }
        
        if (!result)
            return new Prefernce(5, 70, StringSource.Hierogliphs, false, true, 
                    AfterClickEffects.Pale, false);
        else 
            return resultPref;
    }
    
    /**
     * writes to DB information when current account quit the application of current account is changed 
     */
    public void writeAccountExit()
    {
        if (!CanUseDB) return;                
        
        PreparedStatement updateExitData;
        try 
        {
            updateExitData = conn.prepareStatement("UPDATE Accounts Set ExitDate=?,ExitTime=? where ID = ?");
            
            updateExitData.setDate(1, Date.valueOf(LocalDate.now()));
            updateExitData.setTime(2, Time.valueOf(LocalTime.now()));
            updateExitData.setInt(3, Integer.parseInt(UserId));
            updateExitData.executeUpdate();
        } 
        catch (SQLException ex)
        {
            CanUseDB = false;
        }               
    }

    /**
     * gets current user name
     * @return current user name
     */
    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }   
    
    /**
     * change ID for currently chosen account (after changing)
     * @param UserId new ID 
     */
    public void setUserId(String UserId) {
        this.UserId = UserId;
    }
        
    /**
     * gets current user ID
     * @return current account ID
     */
    public String getUserId() {
        return UserId;
    }
            
    /**
     * writes any changes of current account preferences 
     * @param UserPrefernce reference Preference for current account
     */
    public void writePrefence(Prefernce UserPrefernce)
    {
        if (!CanUseDB) return;
        
        PreparedStatement newSettings;
        try 
        {
            newSettings = conn.prepareStatement("UPDATE AccountPref Set meshCount=?,meshSize=?,symbolSource=?,rotate=?,afterClick=? where AccID = ?");
            
            newSettings.setInt(1, UserPrefernce.getMeshCount());
            newSettings.setInt(2, UserPrefernce.getMeshSize());
            newSettings.setString(3, UserPrefernce.getSymbolSource().getName());
            newSettings.setInt(4, UserPrefernce.isRotate()?1:0);
            newSettings.setString(5, UserPrefernce.getAfterClick().getName());
            newSettings.setInt(6, Integer.parseInt(UserId));
            newSettings.executeUpdate();
        } 
        catch (SQLException ex) 
        {
            CanUseDB = false;
            return;           
        }                
    }
    
    /**
     * constructs the list of all available accounts
     * @return ArrayList<Pair<String,Integer>>
     */
    public ArrayList<Pair<String,Integer>> getAccountsList()
    {
        ArrayList<Pair<String,Integer>> resList = new ArrayList<>();

        String getLastUser = "select * from Accounts order by Name";
        try 
        {
            Statement st = conn.createStatement();
            ResultSet listOfAccounts = st.executeQuery(getLastUser);
            while (listOfAccounts.next())
            {
                Pair<String,Integer> P = 
                        new Pair<>(listOfAccounts.getString("Name"),listOfAccounts.getInt("Id"));
                resList.add(P);               
            }
        } 
        catch (SQLException ex) 
        {
            Pair<String,Integer> P = new Pair<>("The one",1);
            resList.clear();
            resList.add(P);
        }
        
        return resList;
    }
    
    /**
     * writes data for new user account (also creates new default preferences)
     * @param name String with new account name
     */
    public void writeAccountName(String name)
    {
        try 
        {
            // insert new data: default account and default settings                
            PreparedStatement newAccountStatement = conn.prepareStatement("INSERT INTO Accounts(Name,CreationDate, ExitDate, ExitTime) VALUES(?,?,?,?)");
            newAccountStatement.setString(1, name);
            newAccountStatement.setDate(2, Date.valueOf(LocalDate.now()));
            newAccountStatement.setDate(3, Date.valueOf(LocalDate.now()));
            newAccountStatement.setTime(4, Time.valueOf(LocalTime.now()));
            newAccountStatement.executeUpdate();
            
            PreparedStatement getAccountIDStatement = conn.prepareStatement("select id from Accounts where Name=?");
            getAccountIDStatement.setString(1, name);
            ResultSet account = getAccountIDStatement.executeQuery();
            
            int Id = account.getInt("Id");
            
            PreparedStatement newSettings = 
                    conn.prepareStatement("INSERT INTO AccountPref(AccID, meshCount, meshSize,  symbolSource,             rotate, afterClick) " + 
                                                           "VALUES(?,     5,         80,        'Consecutive integers',   1,      'Pale the cell')");
            newSettings.setInt(1, Id);
            newSettings.executeUpdate();            
        } 
        //finally //
        catch (SQLException ex) 
        {
            CanUseDB = false;
        }        
    }
    
    /**
     * writes durations for every time spent by user in the process of searching the next symbol in the Schulte table
     * @param Durations List of type ArrayList<Long> with time spans in msec.
     */
    public void writeSessionResults(ArrayList<Long> Durations) {
        // write new drill        
        //write durations during this drill
        
        try 
        {
            // insert new data: default account and default settings                
            PreparedStatement newDrillStatement = conn.prepareStatement("INSERT INTO Drills(AccId, DriilDate, DrillTime) VALUES(?,?,?)");
            newDrillStatement.setInt(1, Integer.parseInt(UserId));
            newDrillStatement.setDate(2, Date.valueOf(LocalDate.now()));
            newDrillStatement.setTime(3, Time.valueOf(LocalTime.now()));
            newDrillStatement.executeUpdate();
            
            PreparedStatement getDrillIDStatement = conn.prepareStatement("select last_insert_rowid() as id"); //select id from drills where AccId=? order by DriilDate, drilltime desc limit 1"
            //getDrillIDStatement.setInt(1, Integer.parseInt(UserId));
            ResultSet account = getDrillIDStatement.executeQuery();
            
            int DrillId = account.getInt("id");
            
            
            int i = 1;
            for(Long Dur : Durations)
            {                            
                PreparedStatement newResult = 
                    conn.prepareStatement("INSERT INTO Results(DrillID, SearchDuration, SeqNumber) VALUES(?, ?, ?)");
                newResult.setInt(1, DrillId);
                newResult.setInt(2, Dur.intValue());
                newResult.setInt(3, i);
                newResult.executeUpdate();
                i++;
            }            
        } 
        //finally //
        catch (SQLException ex) 
        {
            CanUseDB = false;
        }                
    }

    /**
     * gets preference information for current account 
     * @return new 
     */
    public Prefernce getUserPrefernece() {
        
        Prefernce resultPref = null;
        boolean result = true;
        if (!CanUseDB) 
        {
            return new Prefernce(5, 70, StringSource.Numbers, false, true, 
                    AfterClickEffects.Pale, false);
        }        
        
        try
        {
            Statement st = conn.createStatement();
            
            String getUserPref = "select * from AccountPref where AccID=" + UserId;
            ResultSet userPref = st.executeQuery(getUserPref);
            
            resultPref = new Prefernce(
                    userPref.getInt("meshCount"),
                    userPref.getInt("meshSize"),
                    Prefernce.getStrSourceByName(userPref.getString("symbolSource")),
                    userPref.getBoolean("rotate"),
                    true,
                    Prefernce.getAfterActionsByName(userPref.getString("afterClick")),
                    false);
        }
        catch(SQLException ex)
        {
            result = false;
        }
        
        if (!result)
            return new Prefernce(5, 70, StringSource.Hierogliphs, false, true, 
                    AfterClickEffects.Pale, false);
        else 
            return resultPref;
    }
    
    /**
     * gets average statistics for all drills of current account (for chart construction)
     * @return ArrayList<AccountStatistics> of data about every drill
     */
    public ArrayList<AccountStatistics> getAccountStatistics()
    {
        boolean result = true;
        ArrayList<AccountStatistics> resList = new ArrayList<>();
        
        try
        {
            Statement st = conn.createStatement();           
            
            String getUserStatQuery = "select drills.ID, avg(searchduration) as AvgSearch, sum(searchduration)/1000 as TotalTime, " + 
                    "Drills.DriilDate as DDate, Drills.DrillTime as DTime from results " +
                    " join drills on drills.ID=Results.DrillID " +
                    " where Drills.AccID = " + UserId + 
                    " group by Drills.ID order by DriilDate, DrillTime";
            ResultSet getUserStat = st.executeQuery(getUserStatQuery);
            
            AccountStatisticsBuilder acStatBuilder = new AccountStatisticsBuilder();
            
            while(getUserStat.next())
            {
                resList.add(acStatBuilder.setDrillId(getUserStat.getInt("ID"))
                        .setAvgSearchDuration(getUserStat.getDouble("AvgSearch"))
                        .setTotalDuration(getUserStat.getDouble("TotalTime"))
                        .setDrillDate(getUserStat.getDate("DDate").toLocalDate())
                        .setDrillTime(getUserStat.getTime("DTime").toLocalTime())
                        .build()
                );
            }            
        }
        catch(SQLException ex)
        {
            result = false;
        }        
        return resList;
    }
}
