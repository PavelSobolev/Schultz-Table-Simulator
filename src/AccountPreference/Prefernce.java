package AccountPreference;

import enumerations.StringSource;
import enumerations.AfterClickEffects;
import java.awt.Point;
import java.util.ArrayList;

/**
 * class for storing and usage of account preference
 * @author Pavel Sobolev
 */
public final class Prefernce
{
    // attributes of preferneces
    /**
     * how many cells in the Schulte table
     */
    private final int meshCount;     
    private final int winSize;
    /**
     * array of buttons positions
     */
    private final ArrayList<Point> meshPos;
    /**
     * size of one cell in the table
     */
    private final int meshSize;
    /**
     * what to show in the table (letters, numbers, hieroglyphs)
     */
    private final StringSource symbolSource;
    /**
     * should content of the cell be rotated (with random angle from the set:+-90,+-180,+-270)
     */
    private final boolean rotate;
    /**
     * is timer visible during the process of a drill
     */
    private final boolean timerVisible;
    /**
     * what program does after correct cell is clicked (hide it, pale it, does nothing)
     */
    private final AfterClickEffects afterClick;
    
    public static int ShifY = 5;
    /**
     * is this first usage of the preference object after program has been launched
     */
    private final boolean isInitial;
    
    
    /**
     * constructor of the class
     * @param mCount how many cells are in the Schulte table
     * @param mSize size of one cell in the table
     * @param sRes what to show in the table (letters, numbers, hieroglyphs)
     * @param rotation should content of the cell be rotated (with random angle from the set:+-90,+-180,+-270)
     * @param tmVisible is timer visible during the process of a drill
     * @param after what program does after correct cell is clicked (hide it, pale it, does nothing)
     * @param isInit is this first usage of the preference object after program has been launched
     */
    public Prefernce(int mCount, int mSize, StringSource sRes, boolean rotation, boolean tmVisible, 
            AfterClickEffects after, boolean isInit)
    {
        meshCount = mCount;
        meshSize = mSize;
        winSize = meshCount*meshSize;
        symbolSource = sRes;
        meshPos = new ArrayList<>();
        rotate = rotation;
        timerVisible = tmVisible;
        afterClick = after;
        isInitial = isInit;
        
        int posx;
        int posy;
        
        for(int i=0; i<meshCount; i++)
        {
            posx = meshSize*i+5;
            posy = 3;
            for(int j=0; j<meshCount; j++)
            {
                posy = meshSize*j+ShifY;
                Point p = new Point(posx,posy);
                meshPos.add(p);
            }
        }
    }
    
    /// property getters 
    
    public int getMeshCount() 
    {
        return meshCount;
    }

    public int getWinSize() 
    {
        return winSize;
    }

    public ArrayList<Point> getMeshPos() 
    {
        return meshPos;
    }

    public int getMeshSize() 
    {
        return meshSize;
    }

    public StringSource getSymbolSource() 
    {
        return symbolSource;
    }

    public boolean isRotate() 
    {
        return rotate;
    }

    public boolean isTimerVisible() {
        return timerVisible;
    }

    public AfterClickEffects getAfterClick() {
        return afterClick;
    }

    public boolean isIsInitial() {
        return isInitial;
    }
    
    /**
     * allows to get member of AfterClickEffects enum by its string representation
     * @param name by its string representation AfterClickEffects enum member constant
     * @return member of AfterClickEffects enum by its string representation
     */
    public static AfterClickEffects getAfterActionsByName(String name)
    {        
        if (name.equals("Do nothing")) return AfterClickEffects.AsIs;
        if (name.equals("Pale the cell")) return AfterClickEffects.Pale;
        if (name.equals("Hide the cell")) return AfterClickEffects.Dissapearing;
        if (name.equals("Mix symbols in the table")) 
            return AfterClickEffects.Mixed;
        else 
            return AfterClickEffects.AsIs;            
    }
    
    /**
     * allows to get member of StringSource enum by its string representation
     * @param name by its string representation StringSource enum member constant
     * @return member of StringSource enum by its string representation
     */
    public static StringSource getStrSourceByName(String name)
    {
        if (name.equals("Consecutive integers")) return StringSource.Numbers;
        if (name.equals("Hierogliphs")) return StringSource.Hierogliphs;
        if (name.equals("Random set of letters and signs")) 
            return StringSource.Letters;
        else 
            return StringSource.Letters;            
    }
}
