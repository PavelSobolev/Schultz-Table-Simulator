package schulte;

//icons source: https://www.iconfinder.com/iconsets/ui-essence

import accountsInfo.AccountStatistics;
import AccountPreference.Prefernce;
import gui.MeshPane;
import db.DBConnection;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

/**
 * Main class.
 * Describes main window and its behavior
 * @author Pavel Sobolev
 */
public class Schulte extends Application {

    
    /**
     * describes setting of current user
     */
    private Prefernce UserPreference = null;
        
    // GUI controls 
    private MeshPane ButtonsPane;
    private VBox mainContainer;
    private Scene scene;
    private Stage PrimaryStage;        
    private LocalTime StartMoment;
    private Button mStartStopButton;
    private MenuItem mEnterAccount;
    private MenuItem mNewAccount;
    private MenuItem mSettings;
    private MenuItem mShowHelp;
    private ListView accountsList;
    
    // global object for DB connection
    private final DBConnection AccDb;
    
    // doing actions with timer
    private Timeline timer;
    private KeyFrame kf;
    
    // global service members
    private boolean runnig = false;
    private boolean checkFlag = true;
    private boolean checkBtn = true;
    
    
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * starts application and creates connection to database
     * @throws IOException 
     */
    public Schulte() throws IOException
    {     
        AccDb = new DBConnection();        
    }
    
    /**
     * creates main menu in main window
     */
    public void createMainMenu()
    {
        // create menu bar
        MenuBar menuBar = new MenuBar();
        
        Menu mFile = new Menu("File");
        mEnterAccount = new MenuItem("Choose account ...",
                new ImageView(new Image(getClass().getResourceAsStream("accounts.png"))));
        mEnterAccount.setAccelerator(KeyCodeCombination.keyCombination("CTRL+A"));
        mEnterAccount.setOnAction(enterEvent->
            { // performs after clicking on this item
                Dialog<Integer> dialog = new Dialog<>();
                dialog.setTitle("Choose or add account");
                dialog.setHeaderText("Choose your account or create a new one");
                dialog.setResizable(false);
                Stage alertStage = (Stage)dialog.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("accounts.png")));

                VBox dialogContent = new VBox();
                
                accountsList = new ListView();
                for(Pair<String,Integer> P : AccDb.getAccountsList())
                {
                    accountsList.getItems().add(P.getKey());
                }
                                                
                ToolBar toolBar = new ToolBar();
                Button addButton = new Button("", new ImageView(new Image(getClass().getResourceAsStream("add.png"))));
                addButton.setTooltip(new Tooltip("Create new account"));
                addButton.setOnAction(event->
                {
                    checkFlag = true;
                    
                    TextInputDialog nameInputDialog = new TextInputDialog("");                    
                    nameInputDialog.getEditor().setOnInputMethodTextChanged(value->{
                        nameInputDialog.setHeaderText("Enter name for new account");
                    });
                    final Button btOk = (Button) nameInputDialog.getDialogPane().lookupButton(ButtonType.OK);

                    btOk.addEventFilter(ActionEvent.ACTION, eventA -> {
                            // Check whether some conditions are fulfilled
                            String name = nameInputDialog.getEditor().getText();
                            
                            String allowed = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM ";
                            checkFlag = true;
                            for(int i=0; i<name.length();i++)
                            {
                                if(!allowed.contains(name.substring(i, i+1)))
                                {
                                    checkFlag = false;
                                    break;
                                }
                            }
                            if (!checkFlag) 
                            {          
                                nameInputDialog.setHeaderText("Attention!\nOnly letters are allowed!");                                
                                eventA.consume();
                                return;
                            }
                            
                            if (nameInputDialog.getEditor().getText().trim().length()==0) 
                            {                                
                                nameInputDialog.setHeaderText("Attention!\nNo name was provided!");                                
                                eventA.consume();                                
                                return;
                            }
                            
                            if (nameInputDialog.getEditor().getText().trim().length()>15) 
                            {                                
                                nameInputDialog.setHeaderText("Attention!\nNot more than 25 letters allowed.");                                
                                eventA.consume();
                                return;
                            }
                            
                            checkFlag = true;
                            for(Pair<String,Integer> P : AccDb.getAccountsList())
                            {
                                if (P.getKey().equals(name.trim())) 
                                {
                                    checkFlag = false;
                                    break;
                                }
                            }
                            
                            if (!checkFlag) 
                            {          
                                nameInputDialog.setHeaderText("Attention!\nThis name is already used!");                                
                                eventA.consume();
                                return;
                            }                                                        
                            
                            checkFlag = true;
                            checkBtn = true;
                            
                            // add account  ------------------  
                            AccDb.writeAccountName(nameInputDialog.getEditor().getText().trim());
                            
                            accountsList.getItems().clear();
                            for(Pair<String,Integer> P : AccDb.getAccountsList())
                            {
                                accountsList.getItems().add(P.getKey());
                            }
                            
                            for(int i=0; i<accountsList.getItems().size(); i++)
                            {
                                if (accountsList.getItems().get(i).toString().equals(nameInputDialog.getEditor().getText().trim()))
                                {
                                    accountsList.getSelectionModel().select(i);
                                    accountsList.getFocusModel().focus(i);
                                    break;
                                }
                            }
                        }
                    ); 
                    
                    nameInputDialog.setTitle("Adding new account");
                    nameInputDialog.setHeaderText("Enter name for new account");
                    Optional<String> resultName = nameInputDialog.showAndWait();                    
                });  // -----------  end of addButton.setOnAction(event->
                
                toolBar.getItems().add(addButton);
                                                
                dialogContent.getChildren().addAll(toolBar,accountsList);
                
                dialog.getDialogPane().setContent(dialogContent);

                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
                dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
                
                ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.APPLY);
                dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

                dialog.setResultConverter(
                     b -> {
                        if (b == buttonTypeOk) {
                            Pair<String,Integer> Selected = AccDb.getAccountsList().get(accountsList.getSelectionModel().getSelectedIndex());                            
                            PrimaryStage.setTitle(String.format("Schulte table (user: %s)", Selected.getKey()));
                            AccDb.setUserId(Integer.toString(Selected.getValue()));
                            AccDb.setUserName(Selected.getKey());
                            UserPreference = AccDb.getUserPrefernece();
                            return 0;
                        }
                        return 1;
                });
                
                accountsList.setOnMouseClicked(clickEvent->{
                    if (clickEvent.getClickCount()==2)
                    {
                        Pair<String,Integer> Selected = AccDb.getAccountsList().get(accountsList.getSelectionModel().getSelectedIndex());
                        PrimaryStage.setTitle(String.format("Schulte table (user: %s)", Selected.getKey()));
                        AccDb.setUserId(Integer.toString(Selected.getValue()));
                        AccDb.setUserName(Selected.getKey());
                        UserPreference = AccDb.getUserPrefernece();
                        dialog.close();
                    } 
                }); // end of accountsList.setOnMouseClicked(clickEvent->{

                dialog.showAndWait();                
        }); // end of mEnterAccount.setOnAction(enterEvent->
                        
        // ---------- main window --- menu creation continued      
        
        mNewAccount = new MenuItem("View statistics ...",
                new ImageView(new Image(getClass().getResourceAsStream("graph.png"))));
        mNewAccount.setAccelerator(KeyCodeCombination.keyCombination("CTRL+G"));
        
        mNewAccount.setOnAction(enterEvent->{
            ArrayList<AccountStatistics> accListStat =  AccDb.getAccountStatistics();
            
            if (accListStat.size()==0)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No data.");
                alert.setHeaderText("No completed drills found.");
                alert.setContentText("Complete at least one drill to see your statistics.");
                Stage alertStage = (Stage)alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("semi_eye.png")));
                alert.showAndWait();        
                return;
            }
            
            Dialog<Integer> dialog = new Dialog<>();
                dialog.setTitle("Drill statistics");
                dialog.setHeaderText("Average time searching for " + AccDb.getUserName());
                dialog.setResizable(false);
                Stage alertStage = (Stage)dialog.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("graph.png")));

            VBox dialogContent = new VBox();
            
            //final NumberAxis xAxis = new NumberAxis();
            final CategoryAxis xAxis = new CategoryAxis();
            //xAxis.setTickLabelRotation(180);
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Date and time of the drill");
            //creating the chart
            final LineChart<String,Number> lineChart = new LineChart<>(xAxis,yAxis);

            lineChart.setTitle("Speed of search in every completed drill (msec.)");
            //defining a series
            XYChart.Series series = new XYChart.Series();
            series.setName(AccDb.getUserName());
            //populating the series with data
            for(AccountStatistics stat : accListStat)
            {
                series.getData().add(new XYChart.Data(
                        stat.getDrillDate().toString() + " " + stat.getDrillTime().toString(), stat.getAvgSearchDuration()));
            }
            
            //Scene scene  = new Scene(lineChart,800,600);
            lineChart.getData().add(series);
            dialogContent.getChildren().add(lineChart);
            dialog.getDialogPane().setContent(dialogContent);
            
            ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.APPLY);
            dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
            dialog.showAndWait();
            
        });  // -------------  end of mNewAccount.setOnAction(enterEvent->{
        
        mSettings = new MenuItem("Settings ...",
                new ImageView(new Image(getClass().getResourceAsStream("pref.png"))));
        mSettings.setAccelerator(KeyCodeCombination.keyCombination("CTRL+P"));
        mSettings.setOnAction(event->{
            CreatePrefDialog();
        });
        
        SeparatorMenuItem sp = new SeparatorMenuItem();
        MenuItem mExit = new MenuItem("Exit",
                new ImageView(new Image(getClass().getResourceAsStream("exit.png"))));
        mExit.setOnAction(event->{
            if (AlertOnExit(runnig)) PrimaryStage.close();
            AccDb.writeAccountExit();
        });                
        mExit.setAccelerator(KeyCodeCombination.keyCombination("CTRL+Q"));
        
        //   adding items to the menu
        mFile.getItems().addAll(mEnterAccount,mNewAccount,mSettings,sp,mExit);
        
        Menu mHelp = new Menu("Help");
        mShowHelp = new MenuItem("About Schulte tables ...",
                new ImageView(new Image(getClass().getResourceAsStream("help.png"))));
        mShowHelp.setAccelerator(KeyCodeCombination.keyCombination("F1"));
        mHelp.getItems().add(mShowHelp);
                        
        mHelp.setOnAction(clickEvent->{
            Dialog<Integer> dialog = new Dialog<>();
                dialog.setTitle("Help");
                dialog.setHeaderText("Some facts about method of Schulte tables");
                dialog.setResizable(false);
                Stage alertStage = (Stage)dialog.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("help.png")));

            VBox dialogContent = new VBox();
            
            WebView hlpView = new WebView();
            hlpView.setContextMenuEnabled(false);
            WebEngine engine = hlpView.getEngine();
            
            engine.load(getClass().getResource("help_file.html").toExternalForm());            
            
            dialogContent.getChildren().add(hlpView);
            dialog.getDialogPane().setContent(dialogContent);
            
            ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.FINISH);
            dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
            dialog.showAndWait();
        }); // ------------- mHelp.setOnAction(clickEvent->{
        
        
        menuBar.getMenus().addAll(mFile,mHelp);
        mainContainer.getChildren().add(menuBar);
                
        
        // -------------- menu creation : end        
    }
    
    /**
     * creates toolbar with buttons in the main window
     */
    public void createToolBar()
    {
        // create toolbar ------------------------------ 
        
        ToolBar toolBar = new ToolBar();
        
        Button mTimerButton = new Button("00:00:00", 
                new ImageView(new Image(getClass().getResourceAsStream("timer.png"))));        
        mTimerButton.setDisable(true);
        
        mStartStopButton = new Button("Start new drill", 
                new ImageView(new Image(getClass().getResourceAsStream("start.png"))));              
        
        mStartStopButton.setOnAction(event->{
            if (mStartStopButton.getText().equals("Start new drill"))
            {
                runnig = true;
                mStartStopButton.setText("Stop drill");
                mStartStopButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("stop.png"))));
                mEnterAccount.setDisable(true);
                mNewAccount.setDisable(true);
                mSettings.setDisable(true);
                
                //UserPreference = new Prefernce(5, 70, StringSource.Hierogliphs, false, true, 
                //    AfterClickEffects.Pale, false);
                CreateNewDrill();
                ButtonsPane.setDisable(false);
                
                StartMoment = LocalTime.now();                
                mTimerButton.setText("00:00:00");
                timer = new Timeline();
                timer.setCycleCount(Timeline.INDEFINITE);
                ButtonsPane.getCentralButton().setText(ButtonsPane.StartSymbol);
                ButtonsPane.setClickTime(StartMoment);
                kf = new KeyFrame(Duration.seconds(1),
                    ev->
                    {
                        String ResStr;
                        LocalTime Now = LocalTime.now();
                        long h = ChronoUnit.HOURS.between(StartMoment, Now);
                        long m = ChronoUnit.MINUTES.between(StartMoment, Now);
                        long s = ChronoUnit.SECONDS.between(StartMoment, Now);                        
                        ResStr = (h<10?("0"+h):(""+h)) + ":" + (m<10?("0"+m%60):(""+m%60)) + ":" + (s<10?("0"+s%60):(""+s%60));                        
                        mTimerButton.setText(ResStr);
                        runnig = true;
                }); // ----------end of new KeyFrame(Duration.seconds(1),
                
                timer.getKeyFrames().add(kf);
                timer.playFromStart();
                runnig = true;                
            }
            else
            {
                mStartStopButton.setText("Start new drill");
                mStartStopButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("start.png"))));
                mEnterAccount.setDisable(false);
                mNewAccount.setDisable(false);
                mSettings.setDisable(false);
                ButtonsPane.setDisable(true);
                if (timer!=null)
                {
                    timer.getKeyFrames().clear();
                    timer.stop();
                    timer = null;
                    runnig = false;
                }
            }
        }); // --------------- mStartStopButton.setOnAction(event->{
                
        toolBar.getItems().add(mStartStopButton);
        toolBar.getItems().add(mTimerButton);
        mainContainer.getChildren().add(toolBar);
        
        mainContainer.getChildren().add(ButtonsPane);
    }
    
    /**
     * creates interface for the main window
     * @param primaryStage reference to the main window's content
     * @throws IOException 
     */
    @Override public void start(Stage primaryStage) throws IOException
    {
        PrimaryStage = primaryStage;
        
        UserPreference = AccDb.readPreference(true,"");
                //new Prefernce(5, 80, StringSource.Numbers, false, true, 
                //AfterClickEffects.Dissapearing, true);
  
        mainContainer = new VBox();
        ButtonsPane = new MeshPane(UserPreference,this, AccDb);
        ButtonsPane.setDisable(true);
        
        createMainMenu();
        createToolBar();
                
        scene = new Scene(mainContainer, UserPreference.getWinSize(),
                UserPreference.getWinSize() + 90 + UserPreference.ShifY);
        PrimaryStage.setTitle("Schulte table (user: " + AccDb.getUserName() + ")");
        PrimaryStage.setScene(scene);
        PrimaryStage.setResizable(false);
        PrimaryStage.getIcons().add(new Image(getClass().getResourceAsStream("eye.png")));
        
        PrimaryStage.setOnCloseRequest((WindowEvent closeEv)->{            
            if (!runnig && timer!=null)
            {
                timer.getKeyFrames().clear();
                timer.setCycleCount(0);
                timer = null;
                runnig = false;
            }
            
            if (!AlertOnExit(runnig))
            {
                closeEv.consume();
            }
            
            AccDb.writeAccountExit();
        });

        PrimaryStage.show();
        PrimaryStage.centerOnScreen();
    }
    
    /**
     * stops current drill and saves array of time spans in the database
     * @param Durations array of times of searching in msec. to be saved in database 
     */
    public void StopDrill(ArrayList<Long> Durations)
    {
        mStartStopButton.setText("Start new drill");
        mStartStopButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("start.png"))));
        mEnterAccount.setDisable(false);
        mNewAccount.setDisable(false);
        mSettings.setDisable(false);
        ButtonsPane.setDisable(true);
        if (timer!=null)
        {
            timer.getKeyFrames().clear();
            timer.stop();
            timer = null;
            runnig = false;
        }
        // write result to DB
        AccDb.writeSessionResults(Durations);
    }
    
    /**
     * creates new drill (with table with buttons) according to the preferences of current user  
     */
    public void CreateNewDrill()
    {
        PrimaryStage.setWidth(UserPreference.getWinSize()+15);
        PrimaryStage.setHeight(UserPreference.getWinSize() + 130 + UserPreference.ShifY);
        mainContainer.getChildren().remove(ButtonsPane);
        ButtonsPane = new MeshPane(UserPreference, this, AccDb);
        mainContainer.getChildren().add(ButtonsPane);
        ButtonsPane.setDisable(false);
        PrimaryStage.centerOnScreen();
    }
    
    /**
     * creates dialog window for editing settings of current account
     */
    public void CreatePrefDialog()
    {
        Dialog<Prefernce> dialog = new Dialog<>();
        dialog.setTitle(String.format("App prefernces for account %s", AccDb.getUserName()));
        dialog.setHeaderText("Choose your preferences for practice drills and program behaviour");
        dialog.setResizable(false);
        Stage alertStage = (Stage)dialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("eye.png")));
        
        VBox dialogContent = new VBox();
               
        Label meshCountLabel = new Label("Number of cells in Schulte table: ");
        Label meshSizeLabel = new Label("Preffered size (in pixels) of cell in Schulte table: ");
        Label meshSymbolsLabel = new Label("Symbol set in the Schulte table: ");
        
        ComboBox meshCountComboBox = new ComboBox();
        meshCountComboBox.getItems().add("3 cells");
        meshCountComboBox.getItems().add("5 cells");
        meshCountComboBox.getItems().add("7 cells");
        meshCountComboBox.getItems().add("9 cells");
        meshCountComboBox.setValue(UserPreference.getMeshCount()+" cells");
        
        Slider meshSizeSlider = new Slider();
        meshSizeSlider.setMin(80);
        meshSizeSlider.setMax(100);
        meshSizeSlider.setValue(UserPreference.getMeshSize());
        meshSizeSlider.setShowTickLabels(true);
        meshSizeSlider.setShowTickMarks(true);
        meshSizeSlider.setMajorTickUnit(5);
        meshSizeSlider.setMinorTickCount(1);
        meshSizeSlider.setBlockIncrement(5);
        
        ComboBox meshSymbolSource = new ComboBox();
        meshSymbolSource.getItems().add("Consecutive integers");
        meshSymbolSource.getItems().add("Hierogliphs");
        meshSymbolSource.getItems().add("Random set of letters and signs");
        meshSymbolSource.setValue(UserPreference.getSymbolSource().getName());        
        
        GridPane grid = new GridPane();
        grid.add(meshCountLabel, 1, 1);
        grid.add(meshCountComboBox, 2, 1);
        grid.add(new Label(), 1, 2);
        grid.add(new Label(), 2, 2);
        grid.add(meshSizeLabel, 1, 3);
        grid.add(meshSizeSlider, 2, 3);
        grid.add(new Label(), 1, 4);
        grid.add(new Label(), 2, 4);
        grid.add(meshSymbolsLabel, 1, 5);
        grid.add(meshSymbolSource, 2, 5);
        grid.add(new Label(), 1, 6);
        grid.add(new Label(), 2, 6);
        
        dialogContent.getChildren().add(grid);
        dialogContent.getChildren().add(new Separator());
        dialogContent.getChildren().add(new Label());
        
        Label isRotateLabel = new Label("Show rotated symbols in the table's cells: ");        
        Label afterClickLabel = new Label("After clicking on the cell: ");
        
        CheckBox isRotateCheckBox = new CheckBox();        
        isRotateCheckBox.setSelected(UserPreference.isRotate());        
        
        ComboBox afterActionComboBox = new ComboBox();
        afterActionComboBox.getItems().add("Do nothing");
        afterActionComboBox.getItems().add("Pale the cell");
        afterActionComboBox.getItems().add("Hide the cell");
        //afterActionComboBox.getItems().add("Mix symbols in the table");
        afterActionComboBox.setValue(UserPreference.getAfterClick().getName());
                
        GridPane grid2 = new GridPane();
        grid2.add(isRotateLabel, 1, 1);
        grid2.add(isRotateCheckBox, 2, 1);
        grid2.add(new Label(), 1, 2);
        grid2.add(afterClickLabel, 1, 3);
        grid2.add(afterActionComboBox, 2, 3);
        grid2.add(new Label(), 1, 4);
        
        dialogContent.getChildren().add(grid2);
        dialogContent.getChildren().add(new Separator());
        
        dialog.getDialogPane().setContent(dialogContent);

        ButtonType buttonTypeOk = new ButtonType("Ok", ButtonData.APPLY);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);        
        
        dialog.setResultConverter(new Callback<ButtonType, Prefernce>() {
            @Override
            public Prefernce call(ButtonType b) {
                if (b == buttonTypeOk) {
                    return new Prefernce(
                            Integer.parseInt(meshCountComboBox.getValue().toString().substring(0, 1)), 
                            (int) meshSizeSlider.getValue(), 
                            Prefernce.getStrSourceByName(meshSymbolSource.getValue().toString()), // StringSource.Letters, 
                            isRotateCheckBox.isSelected(), //rotation
                            true,  // timer is visible 
                            Prefernce.getAfterActionsByName(afterActionComboBox.getValue().toString()), //AfterClickEffects.Dissapearing, 
                            false);
                }
                return null;
            }
        });

        Optional<Prefernce> result = dialog.showAndWait();
        if (result.isPresent()) {
            UserPreference = result.get();
            AccDb.writePrefence(UserPreference);
        }
    }
    
    /**
     * check if drill currently active and shows appropriate alert
     * @param state is drill is active now (true if active)
     * @return false if user decided to quit the program (true if other case)
     */
    private boolean AlertOnExit(boolean state)
    {
        if (!state) return true;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm action");
        alert.setHeaderText("Drill is still going on.");
        alert.setContentText("Are you sure you want to quit application?");
        Stage alertStage = (Stage)alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("eye.png")));
        alert.showAndWait().filter(response->response==ButtonType.OK).ifPresent(response->PrimaryStage.close());
        return false;
    }
}
