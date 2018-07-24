package gui;

import db.DBConnection;
import java.awt.Point;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import enumerations.AfterClickEffects;
import AccountPreference.Prefernce;
import schulte.Schulte;
import enumerations.StringSource;


/**
 * creates Schulte table in the form of table of buttons
 * @author Pavel Sobolev
 */
public final class MeshPane extends Pane {
    
    //public static ArrayList<String> SequenceSet;
    private ArrayList<Button> ButtonMesh;
    private final Prefernce UserPreference;
    private Button CentralButton;
    public String StartSymbol;
    private int CurrentCell = 0;
    private Schulte parentWindow = null;
    private LocalTime ClickTime;
    private final ArrayList<Long> Durations;
    private final DBConnection DBConn;

    /**
     * setter for ClickTime property
     * @param ClickTime new Click time
     */
    public void setClickTime(LocalTime ClickTime) {
        this.ClickTime = ClickTime;
    }        
    
    /**
     * constructs new Schulte table 
     * @param uPref user preference influencing the appearance and behavior of the Shulte table 
     * @param pWindow reference to the main window
     * @param conn reference to DB connection
     */
    public MeshPane(Prefernce uPref, Schulte pWindow, DBConnection conn)
    {
        super();
        UserPreference = uPref;
        CurrentCell = 0;
        parentWindow = pWindow;
        GenerateButtonMesh();
        Durations = new ArrayList<>();
        DBConn = conn;
    }

    /**
     * gets reference to central button of the table
     * @return reference to central button of the table of type Button
     */
    public Button getCentralButton() {
        return CentralButton;
    }
        
    /**
     * creates numeric sequence (of integers converted to String) for method buildMeshSymbolsSet
     * @return ArrayList<String> of created data
     */
    private ArrayList<String> buildNumberSequence()
    {
        ArrayList<String> res = new ArrayList<>();
        int count = UserPreference.getMeshCount()*UserPreference.getMeshCount();
        Random r = new Random();
        String testSym = "";
        
        for(int i=0; i<count; i++)
        {
            do
            {
                testSym = Integer.toString(r.nextInt(count-1) + 1);    
            }
            while(res.contains(testSym));
            if(i==count/2)
                res.add("-0-");
            else
                res.add(testSym);
        }
        
        return res;
    }
    
    /**
     * creates String sequence (for hierarchies and letters) for method buildMeshSymbolsSet
     * @return ArrayList<String> of created data
     */
    private ArrayList<String> buildSymbolSequence() throws URISyntaxException, IOException
    {
        ArrayList<String> res = new ArrayList<>();
        
        int count = UserPreference.getMeshCount()*UserPreference.getMeshCount();
        Random r = new Random();
        String testSym = "";
        
        String ResourceFileName = null;
        if (UserPreference.getSymbolSource()==StringSource.Letters)
            ResourceFileName = "ABC.txt";
        else
            ResourceFileName = "HIE.txt";
        
        String Letters[] = Files.readAllLines(
                        Paths.get(this.getClass().getResource(ResourceFileName).toURI()))
                        .get(0).split(";");
        int LettersCount = Letters.length;
                
        for(int i=0; i<count; i++)
        {
            do
            {
                testSym = Letters[r.nextInt(LettersCount)];    
            }
            while(res.contains(testSym));
            if(i==count/2)
                res.add("-0-");
            else
                res.add(testSym);
        }
        
        return res;
    }
    
    /**
     * creates array for controlling process of searching symbols in Schulte table
     * @return ArrayList<String> of created data
     */
    private ArrayList<String> buildMeshSymbolsSet() 
    {   
        if (UserPreference.getSymbolSource()==StringSource.Numbers)
        {
            return buildNumberSequence();
        }
        else
        {            
            try
            {
                return buildSymbolSequence();
            }
            catch(URISyntaxException | IOException ex)
            {
                return buildNumberSequence();
            }
        }
    }
    
    /**
     * mixes elements from given array for showing in the Schulte table
     * @param Source
     * @return 
     */
    private ArrayList<String> MixSymbolSequence(ArrayList<String> Source)
    {
        ArrayList<String> OutSymbols = new ArrayList<>();
        
        if (UserPreference.getSymbolSource()!=StringSource.Numbers)
        {
            Random R = new Random();        
            for(String S : Source)
            {
                int i = 0;

                do
                {
                    i = R.nextInt(Source.size());
                }
                while(OutSymbols.contains(Source.get(i)));
                OutSymbols.add(Source.get(i));
            }
        }
        else
        {                        
            Stream.iterate(1,n->n+1).limit(Source.size()-1).forEach(x->OutSymbols.add(x.toString()));
            OutSymbols.add(0, "-0-");            
        }        
        return OutSymbols;
    }
    
    /**
     * method creates table with buttons and sets their behavior (on click reactions)
     */
    private void GenerateButtonMesh()   
    {
        ButtonMesh = new ArrayList<>(); //array of buttons
        ArrayList<String> OutSymbols = buildMeshSymbolsSet();
        ArrayList<String> Mixture = MixSymbolSequence(OutSymbols);
        
        if (Mixture.get(0).equals("-0-"))
        {
            StartSymbol = Mixture.get(1);
            CurrentCell++;
        }
        else
        {
            StartSymbol = Mixture.get(0);
            CurrentCell = 0;
        }        
        
        int i = 0;
        for(Point p : UserPreference.getMeshPos())
        {
            Button b = new Button();
            if (!OutSymbols.get(i).equals("-0-"))
            {
                // if not central cell: set symbol and other properties
                double angles[] = {90,-90,180,-180,-270,270};                               
                if (!UserPreference.isIsInitial()) 
                {
                    b.setText(OutSymbols.get(i));
                }
                else
                {
                    b.setText("");
                }
                b.setTranslateX(p.x);
                b.setTranslateY(p.y);
                b.setMinSize(UserPreference.getMeshSize(), UserPreference.getMeshSize());
                b.setMaxSize(UserPreference.getMeshSize(), UserPreference.getMeshSize());
                b.setTextAlignment(TextAlignment.CENTER);
                b.setFont(new Font(UserPreference.getMeshSize()/2.6));
                if (UserPreference.isRotate()) b.setRotate(angles[new Random().nextInt(6)]);
                b.setStyle("-fx-text-fill: #0000ff");
                b.setOnAction(event->
                    {               
                        // if correct symbol is found
                        if(b.getText().equals(CentralButton.getText()))
                        {   
                            // get time from prevoius search period
                            long span = ChronoUnit.MILLIS.between(ClickTime, LocalTime.now());
                            ClickTime = LocalTime.now();
                            Durations.add(span);
                            
                            // set properties of buttons according to current account preferences
                            if (UserPreference.getAfterClick()==AfterClickEffects.Pale)
                            {
                                b.setStyle("-fx-text-fill: #ff0000; -fx-background-color: #ffffff");
                                b.setDisable(true);
                            }
                            if (UserPreference.getAfterClick()==AfterClickEffects.Dissapearing)
                            {
                                b.setVisible(false);
                            }
                            
                            if (UserPreference.isRotate()) b.setRotate(0);
                            
                            CurrentCell++;
                            
                            if (CurrentCell==Mixture.size())
                            {
                                CentralButton.setText("");
                                CentralButton.setStyle("-fx-background-color: #ffffff");
                                CentralButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("thumbup.png"))));
                                parentWindow.StopDrill(Durations);
                                return;
                            }
                            
                            if (Mixture.get(CurrentCell).equals("-0-")) CurrentCell++;
                            CentralButton.setText(Mixture.get(CurrentCell));
                        }
                }); // -----------------------------------------------------------------------------
                ButtonMesh.add(b);
                i++;
            }
            else
            {
                b.setText("");
                b.setTranslateX(p.x);
                b.setTranslateY(p.y);
                b.setMinSize(UserPreference.getMeshSize(), UserPreference.getMeshSize());
                b.setMaxSize(UserPreference.getMeshSize(), UserPreference.getMeshSize());
                b.setTextAlignment(TextAlignment.CENTER);
                b.setFont(new Font(UserPreference.getMeshSize()/2.6));
                b.setStyle("-fx-text-fill: #ffffff; -fx-background-color: #ff0000");
                CentralButton = b;
                ButtonMesh.add(b);
                i++;
            }            
        }
        
        getChildren().addAll(ButtonMesh);
        //getChildren().add(CentralButton);
    }
}
 