package accountsInfo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * class used for storing drill  statistics for one account (which is generated by DBMS)
 * @author Pavel Sobolev
 */
public class AccountStatistics {
    // stat items
    private final Integer DrillID;
    private final Double AvgSearchDuration;
    private final Double TotalDuration;
    private final LocalDate DrillDate;
    private final LocalTime DrillTime;

    /**
     * constructor
     * @param DrillID id of drill (retrieved from DB)
     * @param AvgSearchDuration time in msec between two clicks in the process of search  
     * @param TotalDuration  total time spent for the drill 
     * @param DrillDate  date of drill
     * @param DrillTime  time of drill
     */
    public AccountStatistics(Integer DrillID, Double AvgSearchDuration, Double TotalDuration, LocalDate DrillDate, LocalTime DrillTime) {
        this.DrillID = DrillID;
        this.AvgSearchDuration = AvgSearchDuration;
        this.TotalDuration = TotalDuration;
        this.DrillDate = DrillDate;
        this.DrillTime = DrillTime;
    }  

    /**
     * drill id getter
     * @return id of drill
     */
    public Integer getDrillID() {
        return DrillID;
    }

    /**
     * getter
     * @return time in msec between two clicks in the process of search
     */
    public Double getAvgSearchDuration() {
        return AvgSearchDuration;
    }

    /**
     * getter for TotalDuration
     * @return total time spent for the drill
     */
    public Double getTotalDuration() {
        return TotalDuration;
    }

    /**
     * getter for DrillDate
     * @return date of drill
     */
    public LocalDate getDrillDate() {
        return DrillDate;
    }

    /**
     * getter for DrillTime
     * @return time of drill
     */
    public LocalTime getDrillTime() {
        return DrillTime;
    }        
}