package enumerations;

/**
 * enum describes types of symbols shown in the Schulte table
 * @author Pavel Sobolev
 */
public enum StringSource 
{
    Numbers("Consecutive integers"), Hierogliphs("Hierogliphs"), Letters("Random set of letters and signs");
    private String sourceName;
    
    /**
     * set String equivalent for every constant in the enumeration
     * @param name 
     */
    private StringSource(String name)
    {
        sourceName = name;
    }

    /**
     * method returns String representation of a constant
     * @return name representation of a constant
     */
    public String getName() {
        return sourceName;
    }
    
}