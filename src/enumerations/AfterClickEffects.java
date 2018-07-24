package enumerations;

/**
 * set of constants for describing of actions after user clicks a cell in the Schulte table 
 * @author Pavel Sobolev
 */
public enum AfterClickEffects {    
    AsIs("Do nothing"), Dissapearing("Hide the cell"), Pale("Pale the cell"), Mixed("Mix symbols in the table"); 
    private String effectName;
    
    /**
     * set String equivalent for every constant in the enumeration
     * @param name name of constant to be set
     */
    private AfterClickEffects(String name)
    {
        effectName = name;
    }
    
    /**
     * method returns String representation of a constant
     * @return name representation of a constant
     */
    public String getName()
    {
        return effectName;
    }
}
