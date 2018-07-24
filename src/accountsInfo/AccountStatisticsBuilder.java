package accountsInfo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * builder class for object of type AccountStatistics
 * @author Pavel Sobolev
 */
public class AccountStatisticsBuilder {
    private Integer DrillID;
    private Double AvgSearchDuration;
    private Double TotalDuration;
    private LocalDate DrillDate;
    private LocalTime DrillTime;    
    
    /**
     * creates AccountStatistics object after setting all needed properties
     * @return new AccountStatistics object
     */
    public AccountStatistics build()
    {
        return new AccountStatistics(DrillID, AvgSearchDuration, 
                TotalDuration, DrillDate, DrillTime);
    }
    
    /// setters for properties - all return refernece to this instance of current class\
    
    public AccountStatisticsBuilder setDrillId(Integer Id)
    {
        DrillID = Id;
        return this;
    }

    public AccountStatisticsBuilder setDrillID(Integer DrillID) {
        this.DrillID = DrillID;
        return this;
    }

    public AccountStatisticsBuilder setAvgSearchDuration(Double AvgSearchDuration) {
        this.AvgSearchDuration = AvgSearchDuration;
        return this;
    }

    public AccountStatisticsBuilder setTotalDuration(Double TotalDuration) {
        this.TotalDuration = TotalDuration;
        return this;
    }

    public AccountStatisticsBuilder setDrillDate(LocalDate DrillDate) {
        this.DrillDate = DrillDate;
        return this;
    }

    public AccountStatisticsBuilder setDrillTime(LocalTime DrillTime) {
        this.DrillTime = DrillTime;
        return this;
    }        
}
