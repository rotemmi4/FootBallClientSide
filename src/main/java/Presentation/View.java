package Presentation;

import Service.Client;
import Service.Presenter;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class View extends Observable implements IView{

    private static View view = null;




    private View() {
    }

    public static View getInstance() {
        if (view == null) {
            view = new View();
            return view;
        }

        return view;
    }

    public void setStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
                ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Back");
                alert.setContentText("Are you sure you want to exit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    setChanged();
                    notifyObservers("disconnect");
                    // ... user chose OK
                    // Close program
                } else {
                    // ... user chose CANCEL or closed the dialog
                    windowEvent.consume();
                }
            }
        });
    }

    public void alert(String messageText, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setContentText(messageText);
        alert.showAndWait();
        alert.close();

    }

    public void switchTo(ActionEvent actionEvent , String fxmlName , int width, int height, String title){
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(view);
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("/" + fxmlName).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root, width, height);
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }


    /**
     * =============================================================================================
     * =======================================  Guest Window ===========================================
     * =============================================================================================
     * =============================================================================================
     * =============================================================================================
     **/
    public Button login_signInBtn;
    public Button gotoRegister;
    public Button gotoSearch;
    public TextField login_username_txtfld;
    public PasswordField login_password_txtfld;
    private boolean login_successful = false;

    public enum userInstance {fan ,associationUser , referee, teamMember , systemManager , blank};

    public userInstance ui;

    public void setUi(userInstance ui) {
        this.ui = ui;
    }

    public void setLogin_successful(boolean login_successful) {
        this.login_successful = login_successful;
    }

    public void displayRegisterWindow(ActionEvent actionEvent) {
        switchTo(actionEvent , "Register.fxml" , 800, 484, "Register Your User");
        register_occupation_choiceBox.getItems().addAll("Fan", "TeamMember" , "Referee" , "Association");

        register_verification_txtfield.textProperty().addListener( ((observable, oldValue, newValue) -> {

            if(newValue.equals("Player") && register_occupation_choiceBox.getValue().equals("TeamMember")){
                register_rolesList.getItems().addAll("GK" , "RB" , "CB", "LB", "RM", "CDM" , "CM", "CAM", "LM",
                        "RW", "LW", "CF", "ST");
            }
            else if(newValue.equals("Coach") && register_occupation_choiceBox.getValue().equals("TeamMember") ){
                register_rolesList.getItems().addAll("Head Coach","Assistant Coach", "GoalKeepers' Coach" , "Defenders' Coach"
                        ,"Midfielders' Coach", "Strikers' Coach");
            }
            else if(newValue.equals("Referee") && register_occupation_choiceBox.getValue().equals("Referee") ){
                register_rolesList.getItems().addAll("Main Referee", "Side Referee");
            }
            else{ register_rolesList.getItems().clear(); }
        }) );

    }

    public ProgressBar progBar;
    public Task task;

    public void setTaskUp(){
        task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                int max = 30;
                for (int i = 0; i <= max; i++) {
                    if(isCancelled()){
                        break;
                    }
                    updateProgress( i , max);
                    Thread.sleep(1000);
                }
                return null;
            }
        };

        progBar = new ProgressBar();
        progBar.setProgress(0);
        progBar.progressProperty().bind(task.progressProperty());
        Thread taskTh = new Thread(task);
        taskTh.start();
    }

    public Button continueBTN;

    public void pressedLogInBtn(ActionEvent actionEvent) {

//        switchTo(actionEvent, "testLogin.fxml" , 600 , 400 , "Logging In");
//        continueBTN.setDisable(false);


        boolean filled_userName = false, filled_password = false;
        if(!login_username_txtfld.getText().trim().isEmpty()){
            filled_userName = true;
        }else{
            alert("please enter username" , Alert.AlertType.WARNING);
        }

        if(!login_password_txtfld.getText().trim().isEmpty()){
            filled_password = true;
        }else{
            alert("please enter password" , Alert.AlertType.WARNING);
        }

        if(filled_userName && filled_password){
//            setTaskUp();
            setChanged();
            notifyObservers("login");
            if(login_successful){
//                continueBTN.setDisable(false);
                switch (ui) {
                    case systemManager:
                        switchTo(actionEvent, "SystemManager.fxml", 600, 400, "Welcome " + login_username_txtfld.getText() + " !");
                        unregisterToMatchNotification.setDisable(true);
                        listAlert.getItems().clear();
                        break;
                    case associationUser:
                        switchTo(actionEvent, "Association.fxml", 600, 400, "Welcome " + login_username_txtfld.getText() + " !");
                        unregisterToMatchNotification.setDisable(true);
                        listAlert.getItems().clear();
                        break;
                    case teamMember:
                        switchTo(actionEvent, "TeamMember.fxml", 600, 400, "Welcome " + login_username_txtfld.getText() + " !");
                        initTeamMember();
                        unregisterToMatchNotification.setDisable(true);
                        listAlert.getItems().clear();
                        break;
                    case referee:
                        switchTo(actionEvent, "Referee.fxml", 600, 400, "Welcome " + login_username_txtfld.getText() + " !");
                        unregisterToMatchNotification.setDisable(true);
                        listAlert.getItems().clear();
                        break;
                    case fan:
                        switchTo(actionEvent, "Fan.fxml", 600, 400, "Welcome " + login_username_txtfld.getText() + " !");
                        unregisterToMatchNotification.setDisable(true);
                        listAlert.getItems().clear();
                        break;
                }
//                unregisterToMatchNotification.setDisable(true);
            }
        }
    }

//    public void switchFromLoginToUser(ActionEvent actionEvent){
//        switch (ui){
//            case systemManager:
//                switchTo(actionEvent,"SystemManager.fxml",600,400,"Welcome "+login_username_txtfld.getText()+" !");
//                break;
//            case associationUser:
//                switchTo(actionEvent,"Association.fxml",600,400,"Welcome "+login_username_txtfld.getText()+" !");
//                break;
//            case teamMember:
//                switchTo(actionEvent,"TeamMember.fxml",600,400,"Welcome "+login_username_txtfld.getText()+" !");
//                initTeamMember();
//                break;
//            case referee:
//                switchTo(actionEvent,"Referee.fxml",600,400,"Welcome "+login_username_txtfld.getText()+" !");
//                break;
//            case fan:
//                switchTo(actionEvent,"Fan.fxml",600,400,"Welcome "+login_username_txtfld.getText()+" !");
//                break;
//        }
//    }

    public void backToLoginScreen(ActionEvent actionEvent){
        switchTo(actionEvent, "Guest.fxml", 800 , 484, "Welcome");
        gotoSearch.setDisable(true);
        setChanged();
        notifyObservers("LogOut");

    }

    public ArrayList<String> getLoginDetails() {
        ArrayList<String> details = new ArrayList<>();
        details.add(login_username_txtfld.getText());
        details.add(login_password_txtfld.getText());
        return details;
    }



    /**
     * =============================================================================================
     * =======================================  Register Window ===========================================
     * =============================================================================================
     * =============================================================================================
     * =============================================================================================
     **/

    public TextField register_firstName_txtfield;
    public TextField register_lastName_txtfield;
    public TextField register_username_txtfield;
    public PasswordField register_password_txtfield;
    public TextField register_email_txtfield;
    public DatePicker register_birthday_txtfield;
    public TextField register_verification_txtfield;
    public ListView<String> register_rolesList = new ListView<>();
    public ChoiceBox<String> register_occupation_choiceBox = new ChoiceBox<>();
    private boolean validate_user = false;
    public Button backToLogin;

    public void setValidate_user(boolean validate_user) {
        this.validate_user = validate_user;
    }


    public void backToGuestScreen(ActionEvent actionEvent) {
        boolean filled_firstname = false, filled_lastname = false, filled_username = false, filled_password = false, filled_email = false
                , filled_birthday  = false, filled_verification = false;

        if(!register_firstName_txtfield.getText().trim().isEmpty()){
            filled_firstname = true;
        }else{
            alert("please enter first name" , Alert.AlertType.WARNING);
        }

        if(!register_lastName_txtfield.getText().trim().isEmpty()){
            filled_lastname = true;
        }else{
            alert("please enter last name" , Alert.AlertType.WARNING);
        }

        if(!register_username_txtfield.getText().trim().isEmpty()){
            filled_username = true;
        }else{
            alert("please enter username" , Alert.AlertType.WARNING);
        }

        if(!register_password_txtfield.getText().trim().isEmpty()){
            filled_password = true;
        }else{
            alert("please enter password" , Alert.AlertType.WARNING);
        }

        if(!register_email_txtfield.getText().trim().isEmpty()){
            String email = register_email_txtfield.getText();
            String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if(matcher.matches()){
                filled_email = true;
            }else{
                alert("please enter email by the following rules:\n " +
                        "1) A-Z characters allowed\n" +
                        "2) a-z characters allowed\n" +
                        "3) 0-9 numbers allowed\n" +
                        "4) Additionally email may contain only dot(.), dash(-) and underscore(_)\n" +
                        "5) Rest all characters are not allowed." , Alert.AlertType.WARNING);
            }
        }else{
            alert("please enter email" , Alert.AlertType.WARNING);
        }

//        if(!register_occupation_txtfield.getText().trim().isEmpty()){
//            filled_occupation = true;
//        }else{
//            alert("please enter occupation" , Alert.AlertType.WARNING);
//        }

//        if(!register_birthday_txtfield.getText().trim().isEmpty()){
//            String day = register_birthday_txtfield.getText();
//            String regex = "^[0-3][0-9]/[0-3][0-9]/(?:[0-9][0-9])?[0-9][0-9]$";
//            Pattern pattern = Pattern.compile(regex);
//            Matcher matcher = pattern.matcher(day);
//            if(matcher.matches()){
//                filled_birthday = true;
//            }else{
//                alert("please enter a date by the format - dd/MM/yyyy" , Alert.AlertType.WARNING);
//            }
//        }else{
//            alert("please enter a date by the format - dd/MM/yyyy" , Alert.AlertType.WARNING);
//        }
//
        if(!register_birthday_txtfield.getValue().toString().isEmpty()){
            filled_birthday = true;
        }else{
            alert("please enter a date by the format - dd/MM/yyyy" , Alert.AlertType.WARNING);
        }


        if(!register_verification_txtfield.getText().trim().isEmpty()){
            String verification = register_verification_txtfield.getText();
            if(verification.equals("#") || verification.equals("Referee") ||  verification.equals("MainReferee") || verification.equals("SideReferee") || verification.equals("Coach") ||
                    verification.equals("Association") || verification.equals("Owner") || verification.equals("Player") || verification.equals("TeamManager") ){
                filled_verification = true;
            }else{
                alert("please enter # if you dont have a verification code." , Alert.AlertType.WARNING);
            }
        }else{
            alert("please enter a verification code" , Alert.AlertType.WARNING);
        }


        if(filled_firstname && filled_lastname && filled_username && filled_password && filled_email &&
                filled_birthday && filled_verification){
            setChanged();
            notifyObservers("register");
            if(validate_user){
                switchTo(actionEvent, "Guest.fxml", 600 , 400, "Welcome");
                gotoSearch.setDisable(true);

            }
        }
    }

    @Override
    public ArrayList<String> getRegisterDetails() {
        ArrayList<String> details = new ArrayList<>();
        details.add(register_firstName_txtfield.getText());
        details.add(register_lastName_txtfield.getText());
        details.add(register_username_txtfield.getText());
        details.add(register_password_txtfield.getText());
        details.add(register_occupation_choiceBox.getValue());
        details.add(register_birthday_txtfield.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        details.add(register_email_txtfield.getText());
        details.add(register_verification_txtfield.getText());
        details.add(register_rolesList.getSelectionModel().getSelectedItem());
        return details;
    }



//    public Button subscribe;//*************** Subscribe to alerts
    /**
     * =============================================================================================
     * =======================================  Association Window =====================================
     * =============================================================================================
     * =============================================================================================
     * =============================================================================================
     **/

    public Button association_createNewSeason;
    public Button association_addLeagueToCurrentSeason;
    public Button association_nominateRef;
    public Button association_changePointsPolicy;
    public Button association_changeGamePolicy;
    public Button association_createGameSchedule;
    public Button association_manageFinance;
    public Button association_logOut;
    public Button association_createLeague;


    public void createNewSeason (ActionEvent actionEvent) {
        switchTo(actionEvent,"Association_createNewSeason.fxml",600 , 400, "Create Season");
    }

    public void backtoLogin (ActionEvent actionEvent) {
        switchTo(actionEvent,"Guest.fxml" , 600, 400 , "Welcome");
        gotoSearch.setDisable(true);
        setChanged();
        notifyObservers("LogOut");


    }

    public void addLeagueToCurrentSeason (ActionEvent ae){
        switchTo(ae, "association_addLeagueToCurrentSeason.fxml", 600, 400, "Add League To Current Season");
    }

    public void createLeague (ActionEvent ae){
        switchTo(ae, "Association_createLeagueAndAddToSeason.fxml", 600, 480, "Create League");
        createLeague_chooseTieBreaker.getItems().addAll("Goal Difference" , "Head to head games");
        createLeague_rounds.getItems().addAll("1 - one game against each team","2 - home/away games against each team");
    }

    public void nominateRefBTN (ActionEvent ae){
        switchTo(ae, "Association_nominateRef.fxml", 600, 505, "Nominate Referee To League");
        setChanged();
        notifyObservers("get refs in db");
    }

    public void association_createGameScheduleBTN(ActionEvent ae){
        switchTo(ae, "Association_scheduleGames.fxml", 600, 400, "Schedule League Games");
        setChanged();
        notifyObservers("get leagues in db");
    }

    /**
     * association_createNewSeason
     */
    public TextField createNewSeason_sponsers_txtfld;
    public TextField createNewSeason_toto_txtfld;
    public TextField createNewSeason_ministry_txtfld;
    private boolean doesSeasonExist = true;
    private double sumOfIncome;
    private int currentSeason;

    public int getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(int currentSeason) {
        this.currentSeason = currentSeason;
    }

    public double getSumOfIncome() {
        return sumOfIncome;
    }

    public void setDoesSeasonExist(boolean doesSeasonExist) {
        this.doesSeasonExist = doesSeasonExist;
    }

    public void createNewSeasonBTN (ActionEvent actionEvent){
        boolean filled_sponsers = false, filled_toto = false, filled_ministry = false;
        sumOfIncome = 0;

        if(!createNewSeason_sponsers_txtfld.getText().trim().isEmpty()){
            if(createNewSeason_sponsers_txtfld.getText().matches("[0-9]+")){
                sumOfIncome += Double.parseDouble(createNewSeason_sponsers_txtfld.getText());
                filled_sponsers = true;
            }
        }else{
            alert("please enter sponsers income" , Alert.AlertType.WARNING);
        }

        if(!createNewSeason_toto_txtfld.getText().trim().isEmpty()){
            if(createNewSeason_toto_txtfld.getText().matches("[0-9]+")){
                sumOfIncome += Double.parseDouble(createNewSeason_toto_txtfld.getText());
                filled_toto = true;
            }
        }else{
            alert("please enter toto income" , Alert.AlertType.WARNING);
        }

        if(!createNewSeason_ministry_txtfld.getText().trim().isEmpty()){
            if(createNewSeason_ministry_txtfld.getText().matches("[0-9]+")){
                sumOfIncome += Double.parseDouble(createNewSeason_ministry_txtfld.getText());
                filled_ministry = true;
            }
        }else{
            alert("please enter ministry income" , Alert.AlertType.WARNING);
        }

        if( filled_ministry && filled_sponsers && filled_toto ){
            setChanged();
            notifyObservers(sumOfIncome);
            if(!doesSeasonExist){
                alert("season was added successfully" , Alert.AlertType.INFORMATION);
                switchTo(actionEvent, "Association.fxml", 600, 400, "Association Management");
            }
            else{
                alert("season is already added" , Alert.AlertType.INFORMATION);
                switchTo(actionEvent, "Association.fxml", 600, 400, "Association Management");
            }
        }

    }

    /**
     * association_createLeague
     */

    public TextField createLeague_leagueName;
    public TextField createLeague_numberTeams;
    public TextField createLeague_pointsWin;
    public TextField createLeague_pointsLoss;
    public TextField createLeague_pointsDraw;
    private DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public ChoiceBox<String> createLeague_chooseTieBreaker = new ChoiceBox<>();
    public ChoiceBox<String> createLeague_rounds = new ChoiceBox<>();

    public DatePicker createLeague_startDate;
    private int yearPicked;
    public Button createLeague_backBTN;
    public Button createSeason_createBTN;

    public int getYearPicked() {
        return yearPicked;
    }

    public void backToAssociation(ActionEvent ae){
        switchTo(ae, "Association.fxml", 600, 400, "Association Management");
        setRegNotificationButt();
    }

    private boolean isLeagueExist = false,isCurrentSeason = false;
    private String leagueName = "";

    public String getLeagueName() {
        return leagueName;
    }

    public void setCurrentSeason(boolean currentSeason) {
        isCurrentSeason = currentSeason;
    }

    public void setLeagueExist(boolean leagueExist) {
        isLeagueExist = leagueExist;
    }

    public void createTheLeague(ActionEvent ae){
        boolean leagueAndSeasonOK = false, teamsNum = false, pointsW = false,
                pointsL = false, pointsD = false , tie= false, rounds=false;

        //check league name doest exist in current Season in DB
        if(!createLeague_leagueName.getText().trim().isEmpty() && !createLeague_startDate.getValue().toString().isEmpty()){
            //check year
            String year = createLeague_startDate.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).substring(6);
            yearPicked = Integer.parseInt(year)+1;
            setChanged();
            notifyObservers(yearPicked);
            if(isCurrentSeason){
                leagueName = createLeague_leagueName.getText();
                setChanged();
                notifyObservers(leagueName);
                if(!isLeagueExist){
                    leagueAndSeasonOK = true;
                }
            }
            else{
                alert("you cant add league to current season" , Alert.AlertType.WARNING);
            }
        }
        else{
            alert("please enter leagueName/starting date" , Alert.AlertType.WARNING);
        }

        //check num teams is number, also points pre W/L/D
        if(!createLeague_numberTeams.getText().trim().isEmpty()){
            if(createLeague_numberTeams.getText().matches("[0-9]+")){
                teamsNum = true;
            }
        }else{
            alert("please enter number of teams in the league" , Alert.AlertType.WARNING);
        }

        if(!createLeague_pointsWin.getText().trim().isEmpty()){
            if(createLeague_pointsWin.getText().matches("[0-9]+")){
                pointsW = true;
            }
        }else{
            alert("please enter points per win" , Alert.AlertType.WARNING);
        }

        if(!createLeague_pointsDraw.getText().trim().isEmpty()){
            if(createLeague_pointsDraw.getText().matches("[0-9]+")){
                pointsD = true;
            }
        }else{
            alert("please enter points per draw" , Alert.AlertType.WARNING);
        }

        if(!createLeague_pointsLoss.getText().trim().isEmpty()){
            if(createLeague_pointsLoss.getText().matches("[0-9]+")){
                pointsL = true;
            }
        }else{
            alert("please enter points per loss" , Alert.AlertType.WARNING);
        }


        //tie breaker and num rounds has value
        if(createLeague_chooseTieBreaker.getValue() != null){
            tie = true;
        }else{
            alert("please choose tie breaker" , Alert.AlertType.WARNING);
        }

        if(createLeague_rounds.getValue() != null){
            rounds = true;
        }else{
            alert("please choose number of rounds" , Alert.AlertType.WARNING);
        }

        if(leagueAndSeasonOK && tie && rounds && pointsD && pointsL && pointsW && teamsNum){
            setChanged();
            notifyObservers("add League");
            switchTo(ae,"Association.fxml" , 600 , 400 , "Association Management" );
        }
    }

    public String getNewLeagueDetails() {
        StringBuilder details = new StringBuilder();
        details.append(String.valueOf(yearPicked)).append(":").
                append(createLeague_leagueName.getText()).append(":").
                append(createLeague_startDate.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append(":").
                append(createLeague_pointsWin.getText()).append(":").
                append(createLeague_pointsLoss.getText()).append(":").
                append(createLeague_pointsDraw.getText()).append(":").
                append(createLeague_chooseTieBreaker.getValue()).append(":").
                append(createLeague_rounds.getValue()).append(":").
                append(createLeague_numberTeams.getText());

        return details.toString();
    }

//        ArrayList<String> details = new ArrayList<>();
//        details.add(String.valueOf(yearPicked)); //1
//        details.add(createLeague_leagueName.getText());//2
//        details.add(createLeague_numberTeams.getText()); //10
//        details.add(createLeague_pointsWin.getText()); //4
//        details.add(createLeague_pointsDraw.getText()); // 6
//        details.add(createLeague_pointsLoss.getText()); // 5
//        details.add(createLeague_chooseTieBreaker.getValue()); //7-8
//        details.add(createLeague_rounds.getValue()); //9
//        details.add(); //3



    /**
     * association_nominateReferee
     */
    public ListView<String> candidateRefs = new ListView<>();
    public String refUsernameToNominate;

    public void nominateRefToSeason(ActionEvent ae){
        String refDetails = candidateRefs.getSelectionModel().getSelectedItem();

        if(refDetails!= null && !refDetails.isEmpty()){
            int comma = refDetails.indexOf(",");
            refUsernameToNominate = refDetails.substring(0,comma);

            setChanged();
            notifyObservers(refUsernameToNominate);

            int toRemoveRef = candidateRefs.getSelectionModel().getSelectedIndex();
            candidateRefs.getItems().remove(toRemoveRef);
        }
    }

    /**
     * association_scheduleGames
     */

    public ListView<String> scheduleGames_leagueList = new ListView<>();
    public String leagueNameToSchedule;
    public boolean wasScheduleCreated = false;

    public void createSchedule (ActionEvent ae){
        leagueNameToSchedule = scheduleGames_leagueList.getSelectionModel().getSelectedItem();
        if(leagueNameToSchedule != null && !leagueNameToSchedule.isEmpty()){
            setChanged();
            notifyObservers(leagueNameToSchedule);

            if(wasScheduleCreated){
                int toRemove = scheduleGames_leagueList.getSelectionModel().getSelectedIndex();
                scheduleGames_leagueList.getItems().remove(toRemove);
            }

        }
    }


    /**
     * Association_addRefereeToLeague
     */

    public Button backToasso;

    public void switchToAddRefToLeague (ActionEvent actionEvent){
        switchTo(actionEvent,"Association_addRefereeToLeague.fxml", 600, 400, "Add referee to league");
        setChanged();
        notifyObservers("fill leagues and refs list");
    }

    public ListView<String> addRef_leagueList = new ListView<>();
    public ListView<String> addRef_refsList = new ListView<>();
    public Button addRef;
    public String selectedLeague;
    public String selectedRef;
    public boolean wasRefAddedToLeage = false;

    public void addRefToLeague(ActionEvent actionEvent){
        selectedLeague = addRef_leagueList.getSelectionModel().getSelectedItem();
        selectedRef = addRef_refsList.getSelectionModel().getSelectedItem();
        if(selectedLeague != null && !selectedLeague.isEmpty() && selectedRef != null && !selectedRef.isEmpty() ){
            setChanged();
            notifyObservers(selectedLeague+" "+selectedRef);

            if(wasRefAddedToLeage){
                int toRemove = addRef_refsList.getSelectionModel().getSelectedIndex();
                addRef_refsList.getItems().remove(toRemove);
            }


        }

    }

    /**
     * Associaction_AddTeamToLeagueScreen
     */

    public ListView<String> requestsList = new ListView<>();
    public Button approveReq;
    public String selectedReq;

    public Button backback;


    public void switchtoAddTeamToLeagueScreen(ActionEvent actionEvent){
        switchTo(actionEvent,"Association_AddTeamLeague.fxml",600,400,"Approve Team Request");
        setChanged();
        notifyObservers("load team requests");
    }

    public boolean wasTeamAdded = false;

    public void approveRequest(ActionEvent actionEvent){
        selectedReq = requestsList.getSelectionModel().getSelectedItem();
        if(selectedReq != null && !selectedReq.isEmpty() ){
            setChanged();
            notifyObservers(selectedReq);
            if(wasTeamAdded){
                int toRemove = requestsList.getSelectionModel().getSelectedIndex();
                requestsList.getItems().remove(toRemove);
            }
        }

    }


    /**
     * =============================================================================================
     * =======================================  Referee Window =====================================
     * =============================================================================================
     * =============================================================================================
     * =============================================================================================
     **/

    public Button referee_updateInfo;
    public Button referee_logout;
    public ListView<String> refsProposals = new ListView<>();
    public Button approveRequestToBeRef;
    public String approvedReq;
    public Button referee_viewManageGames;
    public Button gotoApprove;


    public void gotoApproveReq(ActionEvent actionEvent){
        switchTo(actionEvent,"Referee_approve.fxml",600,400,"Join A Season");
        setChanged();
        notifyObservers("get requests for referee");
    }

    public boolean isMainReferee = false;
    public ListView<String> gamesList = new ListView<>();
    public Button addEvents;
    public Button backToReferee;
    public String gameToManage;
    public ListView<String> playerList1 = new ListView<>();
    public ListView<String> playerList2 = new ListView<>();

    public void gotoManageGames(ActionEvent actionEvent){
        switchTo(actionEvent, "Referee_viewGames.fxml",600,400,"Games To View/Manage");
        setChanged();
        notifyObservers("get games of referee");
        if(!isMainReferee){
            addEvents.setDisable(true);
        }else
            addEvents.setDisable(false);

    }

    public void backToViewGames(ActionEvent actionEvent) {
        switchTo(actionEvent, "Referee_viewGames.fxml", 600, 400, "Games To View/Manage");
    }

    public void switchToEventsManage(ActionEvent actionEvent){
        gameToManage = gamesList.getSelectionModel().getSelectedItem();
        if(gameToManage != null && !gameToManage.isEmpty() ){
            switchTo(actionEvent, "Referee_addAlertsToGame.fxml",1001,400,"Games To View/Manage");
            setChanged();
            notifyObservers(gameToManage);
        }
    }

    public String playerScored;
    public String playerOffside;
    public String playerFoul;
    public String playerYellow;
    public String playerRed;
    public String playerInjured;
    public String playerIn;
    public String playerOut;

    public void addGoalEventToGame(ActionEvent actionEvent){
        playerScored = playerList1.getSelectionModel().getSelectedItem();
        if(playerScored != null && !playerScored.isEmpty() ){
            setChanged();
            notifyObservers(playerScored);
        }
    }

    public void addOffsideEventToGame(ActionEvent actionEvent){
        playerOffside = playerList1.getSelectionModel().getSelectedItem();
        if(playerOffside != null && !playerOffside.isEmpty() ){
            setChanged();
            notifyObservers(playerOffside);
        }
    }

    public void addFoulEventToGame(ActionEvent actionEvent){
        playerFoul = playerList1.getSelectionModel().getSelectedItem();
        if(playerFoul != null && !playerFoul.isEmpty() ){
            setChanged();
            notifyObservers(playerFoul);
        }
    }

    public void addYellowEventToGame(ActionEvent actionEvent){
        playerYellow = playerList1.getSelectionModel().getSelectedItem();
        if(playerYellow != null && !playerYellow.isEmpty() ){
            setChanged();
            notifyObservers(playerYellow);
        }
    }

    public void addRedEventToGame(ActionEvent actionEvent){
        playerRed = playerList1.getSelectionModel().getSelectedItem();
        if(playerRed != null && !playerRed.isEmpty() ){
            setChanged();
            notifyObservers(playerRed);
        }
    }

    public void addInjuryEventToGame(ActionEvent actionEvent){
        playerInjured = playerList1.getSelectionModel().getSelectedItem();
        if(playerInjured != null && !playerInjured.isEmpty() ){
            setChanged();
            notifyObservers(playerInjured);
        }
    }

    public void addSubstituteEventToGame(ActionEvent actionEvent){
        playerOut = playerList1.getSelectionModel().getSelectedItem();
        playerIn = playerList2.getSelectionModel().getSelectedItem();

        if(playerOut != null && !playerOut.isEmpty()  && playerIn != null && !playerIn.isEmpty()){
            String team1 = playerOut.split(" - ")[0];
            String team2 = playerIn.split(" - ")[0];

            if(team1.equals(team2)){
                setChanged();
                notifyObservers(playerOut+"~"+playerIn);
            }else{
                alert("cant substitute players from different teams", Alert.AlertType.WARNING);
            }

        }
    }

    public void approveBTN(ActionEvent actionEvent){
        approvedReq = refsProposals.getSelectionModel().getSelectedItem();
        if(approvedReq != null && !approvedReq.isEmpty() ){
            setChanged();
            notifyObservers(approvedReq);

            int toRemove = refsProposals.getSelectionModel().getSelectedIndex();
            refsProposals.getItems().remove(toRemove);
        }
    }

    public void backToReferee(ActionEvent ae){
        switchTo(ae, "Referee.fxml", 600, 400, "Welcome " + login_username_txtfld.getText() + " !");
        setRegNotificationButt();
    }










    /**
     * =============================================================================================
     * =======================================  team member Window =====================================
     * =============================================================================================
     * =============================================================================================
     * =============================================================================================
     **/

    public Button tm_updateInfo;
    public Button tm_personalPage;
    public Button tm_addAsset;
    public Button tm_createTeam;
    public Button tm_logOut;
    public Button tm_deleteAsset;
    public Button tm_updateAsset;
    public Button tm_changeStatus;
   // public Button tm_setBuget;
    public Button tm_addToLeague;
    public CheckBox activate_CHKBX;
    public ChoiceBox<String> homeCourt_CBX;

    public TextField teamName_txtfld;
    public TextField city_txtfld;
    public TextField establishYear_txtfld;
    private boolean validTeam = true;
    private ArrayList<String> courtsByCity;
    private String teamStatus;
    public String ownerteamName="";
    String isPlayer="";
    String isCoach="";
    String isOwner="";
    String isTeamManager="";

    ////////////////////////////////Create Team////////////////////////////////

    public void setPlayer(String player) {
        isPlayer = player;
    }

    public void setCoach(String coach) {
        isCoach = coach;
    }

    public void setOwner(String owner) {
        isOwner = owner;
    }

    public void setTeamManager(String teamManager) {
        isTeamManager = teamManager;
    }


    public void setValidate_team(boolean validTeam) {
        this.validTeam = validTeam;
    }

    public void setCourts(ArrayList<String> courts){
        this.courtsByCity = courts;
    }

    public void createTeam (ActionEvent actionEvent) {
        if(ownerteamName.equals("null")){
            switchTo(actionEvent,"createTeam.fxml",600 , 400, "Create Team");

            city_txtfld.textProperty().addListener( ((observable, oldValue, newValue) -> {
                if(!newValue.isEmpty() && city_txtfld.getText()!=null){
                    courtOptions();
                }
            }) );
        }
        else {
            alert("you already have a team called -" + ownerteamName , Alert.AlertType.WARNING);
        }
    }

    public void cancleTM (ActionEvent actionEvent) {
        coachList.clear();
        ownerList.clear();
        playerList.clear();
        switchTo(actionEvent,"teamMember.fxml",600 , 400, "Team Member");
        initTeamMember();
        setRegNotificationButt();
    }

    public ArrayList<String> getTeamDetails() {
        ArrayList<String> details = new ArrayList<>();
        details.add(teamName_txtfld.getText());
        details.add(establishYear_txtfld.getText());
        details.add(String.valueOf(activate_CHKBX.isSelected()));
        details.add(city_txtfld.getText());
        details.add(login_username_txtfld.getText());
        details.add(homeCourt_CBX.getSelectionModel().getSelectedItem());
        return details;
    }


    public void courtOptions(){
        setChanged();
        notifyObservers("courtByCity");
        homeCourt_CBX.getSelectionModel().clearSelection();
        homeCourt_CBX.getItems().clear();

        for (String courtName : courtsByCity){
            homeCourt_CBX.getItems().add(courtName);
        }
    }

    public void createNewTeam (ActionEvent actionEvent){
        boolean filled_teamName = false, filled_city = false, filled_establishYear=false, filled_homeCourt=false;
        if(!teamName_txtfld.getText().trim().isEmpty()){
            filled_teamName = true;
        }else{
            alert("please enter team name" , Alert.AlertType.WARNING);
        }
        if(!city_txtfld.getText().trim().isEmpty()){
            filled_city=true;
        }else{
            alert("please enter team city" , Alert.AlertType.WARNING);
        }
        if(!establishYear_txtfld.getText().trim().isEmpty()){
            if(establishYear_txtfld.getText().matches("[0-9]+")){
                filled_establishYear = true;
            }
            else{
                alert("please enter correct establish year" , Alert.AlertType.WARNING);
            }
        }
        else{
            alert("please enter establish year" , Alert.AlertType.WARNING);
        }
        if(homeCourt_CBX.getSelectionModel().getSelectedItem()!=null){
            filled_homeCourt = true;
        }else{
            alert("please enter homeCourt" , Alert.AlertType.WARNING);
        }
        if(filled_city && filled_establishYear && filled_homeCourt && filled_teamName){
            setChanged();
            notifyObservers("createTeam");

            if(validTeam){
                ownerteamName = teamName_txtfld.getText();
                teamStatus= String.valueOf(activate_CHKBX.isSelected());
                alert("team was added successfully" , Alert.AlertType.INFORMATION);
                switchTo(actionEvent, "TeamMember.fxml", 600, 400, "Hello "+login_username_txtfld);
            }
            else{
                alert("team already exist" , Alert.AlertType.INFORMATION);
                switchTo(actionEvent, "TeamMember.fxml", 600, 400, "Team Member");
            }
        }
        initTeamMember();
    }

    public void changeTeamStatus(){
        if(ownerteamName!= ""){
            if(teamStatus.equals("true")){ //team is active/open
                int input = JOptionPane.showConfirmDialog(null, "team status is active , do you want to close the team?");
                // 0=yes, 1=no, 2=cancel
                if(input==0){
                    setChanged();
                    notifyObservers("changeTeamStatus");
                    teamStatus="false";
                    tm_addAsset.setDisable(true);
                    tm_deleteAsset.setDisable(true);
                    tm_addToLeague.setDisable(true);
                    tm_updateAsset.setDisable(true);
                    tm_changeStatus.setDisable(false);
                }
                else{

                }
            }
            else{               //team is closed
                int input = JOptionPane.showConfirmDialog(null, "team status is inactive, do you want to active the team?");
                if(input==0){
                    setChanged();
                    notifyObservers("changeTeamStatus");
                    teamStatus="true";
                    tm_addAsset.setDisable(false);
                    tm_deleteAsset.setDisable(false);
                    tm_addToLeague.setDisable(false);
                    tm_updateAsset.setDisable(false);
                    tm_changeStatus.setDisable(false);
                }

            }
        }
        else {
            alert("you don't have a team" , Alert.AlertType.INFORMATION);
        }
    }

    public void setTeamStatus(String status){
        teamStatus = status;
    }

    public void setOwnerTeamName(String name){
        ownerteamName = name;
    }

    public Boolean teamMemberID(String str){
        if(str.equals("true"))
            return true;
        else
            return false;
    }

    public void initTeamMember(){
        //setChanged();
        //notifyObservers("teamMember");
        //player or coach
        tm_personalPage.setDisable(true);
        tm_updateInfo.setDisable(true);
        if(teamMemberID(isCoach)||teamMemberID(isPlayer)){
            tm_createTeam.setDisable(true);
            tm_addAsset.setDisable(true);
            tm_deleteAsset.setDisable(true);
            tm_addToLeague.setDisable(true);
            tm_updateAsset.setDisable(true);
            tm_changeStatus.setDisable(true);
        }
        //Team Manager
        if(teamMemberID(isTeamManager) ){
            tm_createTeam.setDisable(true);
            tm_addAsset.setDisable(false);
            tm_addToLeague.setDisable(false);
            tm_deleteAsset.setDisable(false);
            tm_updateAsset.setDisable(true);
            tm_changeStatus.setDisable(false);
        }
        //owner
        if(teamMemberID(isOwner)) {
            tm_createTeam.setDisable(false);
            tm_addAsset.setDisable(false);
            tm_addToLeague.setDisable(false);
            tm_deleteAsset.setDisable(false);
            tm_updateAsset.setDisable(true);
            tm_changeStatus.setDisable(false);
        }

        if(teamMemberID(isOwner) && ownerteamName.equals("null")){ //owner have no team
            tm_addAsset.setDisable(true);
            tm_deleteAsset.setDisable(true);
            tm_addToLeague.setDisable(true);
            tm_updateAsset.setDisable(true);
            tm_changeStatus.setDisable(true);
        }

        if(teamMemberID(isOwner) && teamMemberID(teamStatus)==false){ //owner have inactive team
            tm_addAsset.setDisable(true);
            tm_deleteAsset.setDisable(true);
            tm_addToLeague.setDisable(true);
            tm_updateAsset.setDisable(true);
            tm_changeStatus.setDisable(false);
        }
        if(!registerNotification){
            unregisterToMatchNotification.setDisable(true);
        }else{
            registerToMatchNotification.setDisable(true);
        }
    }

    ///////////////////////////////////////////add asset////////////////////////////////////////

    public ListView<String> allTeamMembers = new ListView<>();
    public ListView<String> assetList = new ListView<>();
    public ArrayList<String> coachList= new ArrayList<>();
    public ArrayList<String> playerList= new ArrayList<>();
    public ArrayList<String> ownerList= new ArrayList<>() ;
    public ArrayList<String> managerList= new ArrayList<>() ;
    private String asserNameToAdd;
    private String assetRole;
    private String assetToAdd;
    public CheckBox managerP_CHKBX;
    public CheckBox playerP_CHKBX;
    public CheckBox ownerP_CHKBX;
    public CheckBox coachP_CHKBX;

    public String getAssetNameToAdd() { return asserNameToAdd;  }
    public String getAssetRole() { return assetRole;  }
    public String getAssetToAdd() { return assetToAdd; }

    public void setAssetList(List<String> list){
        assetList.getItems().clear();
        assetList.getItems().addAll(list);
        //assetList=new ListView<>();

    }

    public void addAssetToTeam(ActionEvent ae){
        allTeamMembers.getItems().clear();
        switchTo(ae, "Team_AddAsset.fxml", 600, 400, "Add Asset");
        managerP_CHKBX.setDisable(true);
        playerP_CHKBX.setDisable(true);
        coachP_CHKBX.setDisable(true);
        ownerP_CHKBX.setDisable(true);
    }

    public void addCoach(){
        coachList.clear();
        assetToAdd= "Coach";
        managerP_CHKBX.setDisable(true);
        playerP_CHKBX.setDisable(true);
        coachP_CHKBX.setDisable(true);
        ownerP_CHKBX.setDisable(true);
        if(coachList.isEmpty()){  //emptyList
            setChanged();
            notifyObservers("get available coachs");
            setAssetList(coachList);
        }
        else {
            setAssetList(coachList);
        }
    }

    public void addPlayer(){
        playerList.clear();
        assetToAdd= "Player";
        managerP_CHKBX.setDisable(true);
        playerP_CHKBX.setDisable(true);
        coachP_CHKBX.setDisable(true);
        ownerP_CHKBX.setDisable(true);
        if(playerList.isEmpty()){  //emptyList
            setChanged();
            notifyObservers("get available players");
            setAssetList(playerList);
        }
        else {
            setAssetList(playerList);
        }
    }

    public void addOwner(){
        ownerList.clear();
        assetToAdd= "Owner";
        managerP_CHKBX.setDisable(true);
        playerP_CHKBX.setDisable(true);
        coachP_CHKBX.setDisable(true);
        ownerP_CHKBX.setDisable(true);
        if(ownerList.isEmpty()){  //emptyList
            setChanged();
            notifyObservers("get available owners");
            setAssetList(ownerList);
        }
        else {
            setAssetList(ownerList);
        }
    }

    public void addManager(){
        managerList.clear();
        assetToAdd= "Manager";
        managerP_CHKBX.setDisable(false);
        playerP_CHKBX.setDisable(false);
        coachP_CHKBX.setDisable(false);
        ownerP_CHKBX.setDisable(false);
        if(managerList.isEmpty()){  //emptyList
            setChanged();
            notifyObservers("get available managers");
            setAssetList(managerList);
        }
        else {
            setAssetList(managerList);
        }
    }

    public void addAsset(){
        String selectedAsset = assetList.getSelectionModel().getSelectedItem();
        if(selectedAsset!= null && !selectedAsset.isEmpty()){
            int comma = selectedAsset.indexOf(",");
            asserNameToAdd = selectedAsset.substring(0,comma);
            if(selectedAsset.contains(",")){
                int x = selectedAsset.indexOf(",");
                assetRole= selectedAsset.substring(x+1,selectedAsset.length());
            }
            setChanged();
            notifyObservers("add " + asserNameToAdd);

            int removeFromList = assetList.getSelectionModel().getSelectedIndex();
            assetList.getItems().remove(removeFromList);
            if(assetToAdd.equals("Coach")){
                coachList.remove(selectedAsset);
            }
            if(assetToAdd.equals("Manager")){
                managerList.remove(selectedAsset);
            }
            if(assetToAdd.equals("Player")){
                playerList.remove(selectedAsset);
            }
            if(assetToAdd.equals("Owner")){
                ownerList.remove(selectedAsset);
            }
        }
        managerP_CHKBX.setDisable(true);
        playerP_CHKBX.setDisable(true);
        coachP_CHKBX.setDisable(true);
        ownerP_CHKBX.setDisable(true);
    }

    public void showAllTeamAsset(ActionEvent actionEvent){
        allTeamMembers = new ListView<>();
        switchTo(actionEvent, "TeamAssets.fxml", 600, 400,  "Current team assets");
        setChanged();
        notifyObservers("allTeamAsset");
    }

    ///////////////////////////////////Delete Asset///////////////////////////////////////

    private String asserNameToRemove;

    public String getAsserNameToRemove() { return asserNameToRemove; }

    public void RemoveAssetFromTeam(){
        String selectedAsset = allTeamMembers.getSelectionModel().getSelectedItem();
        if(selectedAsset!= null && !selectedAsset.isEmpty()){
            int comma = selectedAsset.indexOf(",");
            asserNameToRemove = selectedAsset.substring(0,comma);

            setChanged();
            notifyObservers("remove "+asserNameToRemove);

            int removeFromList = allTeamMembers.getSelectionModel().getSelectedIndex();
            allTeamMembers.getItems().remove(removeFromList);

            //alert(asserNameToRemove +" removed from team " + ownerteamName , Alert.AlertType.WARNING);

        }
    }


    ///////////////////////////////////team member add team to league//////////////////////////////
    public ListView<String> requestLeagueList = new ListView<>();
    public String leagueToAdd1;

    public void TMaddToLeague(ActionEvent actionEvent){
        requestLeagueList = new ListView<>();
        switchTo(actionEvent, "TeamMember_AddTeamLeague.fxml", 600, 400,  "add " + ownerteamName +" To League");
        setChanged();
        notifyObservers("tm get leagues");
    }

    public void addToLeagueRequest(){

        leagueToAdd1 = requestLeagueList.getSelectionModel().getSelectedItem();
        if(leagueToAdd1 != null && !leagueToAdd1.isEmpty()){
            setChanged();
            notifyObservers("add team to league request");

        }

    }




    ////////////////////////////////////change policy//////////////////////////////////////////////
    public ListView<String> changeLeagePolicy = new ListView<>();
    public TextField newPointsWin_txtfld;
    public TextField newPointsLoss_txtfld;
    public TextField newPointsDraw_txtfld;
    public ChoiceBox<String> TieBreaker_CBX= new ChoiceBox<>();;
    public String newPointsWin;
    public String newPointsLoss;
    public String newPointsDraw;
    public boolean tieBreaker_goalDifference ;
    public boolean tieBreaker_directResults ;
    public String leagueChangePoints;
    public ChoiceBox<String> rounds_CBX= new ChoiceBox<>();;
    public String leagueChangeRounds;
    public String newRounds;



    public void ChangePointsPolicyToLeague (ActionEvent actionEvent) {
        switchTo(actionEvent,"Association_changePointsPolicy.fxml",511 , 500, "Change Points Policy");

        setChanged();
        notifyObservers("show league list");
        TieBreaker_CBX.getItems().addAll("Goal Difference" , "Head to head games");
    }

    public Button backback2;

    public void SetPointsPerGame(ActionEvent actionEvent) {
        newPointsWin = newPointsWin_txtfld.getText();
        newPointsLoss = newPointsLoss_txtfld.getText();
        newPointsDraw = newPointsDraw_txtfld.getText();
        tieBreaker_goalDifference=false ;
        tieBreaker_directResults=false;

        if(newPointsWin.isEmpty()){
            alert("please enter point per win" , Alert.AlertType.WARNING);
        }
        if(newPointsLoss.isEmpty()){
            alert("please enter point per loss" , Alert.AlertType.WARNING);
        }
        if(newPointsDraw.isEmpty()){
            alert("please enter point per draw" , Alert.AlertType.WARNING);
        }
        if(TieBreaker_CBX.getValue() == null){
            alert("please insert tie breaker" , Alert.AlertType.WARNING);
        }
        if(!newPointsWin.matches("[0-9]+") || !newPointsLoss.matches("[0-9]+") || !newPointsDraw.matches("[0-9]+")){
            alert("please enter points in numbers only" , Alert.AlertType.WARNING);
        }

        else {
            String league = changeLeagePolicy.getSelectionModel().getSelectedItem();
            if (league != null && !league.isEmpty()) {
                if(TieBreaker_CBX.getValue().equals("Goal Difference")){
                    tieBreaker_goalDifference=true;

                }
                else if(TieBreaker_CBX.getValue().equals("Head to head games")){
                    tieBreaker_directResults=true;
                }
                leagueChangePoints=league;
                setChanged();
                notifyObservers("change points policy");
                switchTo(actionEvent, "Association.fxml", 600, 400,  "Welcome "+login_username_txtfld.getText()+" !");

            }
            else {
                alert("Please choose league" , Alert.AlertType.WARNING);

            }
        }
    }

    public Button backbackback;

    public void ChangeRoundsPolicyToLeague (ActionEvent actionEvent) {
        switchTo(actionEvent,"Association_changeGameScedulePolicy.fxml",450 , 460, "Change Game Scedule Policy");

        setChanged();
        notifyObservers("show league list");
        rounds_CBX.getItems().addAll("1 - one game against each team","2 - home/away games against each team");
    }

    public void SetRoundsPerGame(ActionEvent actionEvent) {
        if(rounds_CBX.getValue() == null){
            alert("please choose rounds" , Alert.AlertType.WARNING);
        }
        else {
            String league = changeLeagePolicy.getSelectionModel().getSelectedItem();
            if (league != null && !league.isEmpty()) {
                leagueChangeRounds=league;
                newRounds = rounds_CBX.getValue().substring(0,1);


                setChanged();
                notifyObservers("change game schedule policy");
                switchTo(actionEvent, "Association.fxml", 600, 400,  "Welcome "+login_username_txtfld.getText()+" !");

            }
            else {
                alert("Please choose league" , Alert.AlertType.WARNING);

            }
        }
    }


    /**
     * =============================================================================================
     * =======================================  Notification Window ================================
     * =============================================================================================
     * =============================================================================================
     * =============================================================================================
     **/

    public ListView<String> listAlert=new ListView<String>();
    public String userType="";
    public boolean registerNotification=false;
    public Button registerToMatchNotification=new Button();
    public Button unregisterToMatchNotification=new Button();

    public void showNotification(ActionEvent actionEvent){

        switchTo(actionEvent, "Notification.fxml",600 , 400, "Notification");
        setChanged();
        notifyObservers("alert screen");

    }


    private void setRegNotificationButt(){
        if(registerNotification){
            registerToMatchNotification.setDisable(true);
            unregisterToMatchNotification.setDisable(false);
        }
        else{
            registerToMatchNotification.setDisable(false);
            unregisterToMatchNotification.setDisable(true);
        }
    }
    public void backTM(ActionEvent actionEvent){
        if(userType.equals("TeamMember")) {
            switchTo(actionEvent, "TeamMember.fxml", 600, 400, "Team Member");
            initTeamMember();
        }else if(userType.equals("Referee")) {
            switchTo(actionEvent, "Referee.fxml", 600, 400, "Referee");
        }else if(userType.equals("Association")) {
            switchTo(actionEvent, "Association.fxml", 600, 400, "Association");
        }else if(userType.equals("SystemManager")) {
            switchTo(actionEvent, "SystemManager.fxml", 600, 400, "SystemManager");
        } else if(userType.equals("Fan")) {
            switchTo(actionEvent, "Fan.fxml", 600, 400, "Fan");
        }

    }
    public void addAlerts(ArrayList<String> alerts){
        for (String alert :alerts) {
            if(!alert.equals("")&&!alert.equals(" "))
                listAlert.getItems().add(alert);
        }
    }
    public void registerToGamesNotification(){
        int input = JOptionPane.showConfirmDialog(null, "Are you sure you want to sign up and receive notifications for all games in the association?");
        // 0=yes, 1=no, 2=cancel
        if(input==0){
            registerToMatchNotification.setDisable(true);
            unregisterToMatchNotification.setDisable(false);
            registerNotification=true;
            setChanged();
            notifyObservers("Register to Notification");

        }
    }
    public void unregisterToGamesNotification(){
        int input = JOptionPane.showConfirmDialog(null, "Are you sure you want to disconnect and stop receive notifications for all games in the association?");
        // 0=yes, 1=no, 2=cancel
        if(input==0){
            registerToMatchNotification.setDisable(false);
            unregisterToMatchNotification.setDisable(true);
            registerNotification=false;
            setChanged();
            notifyObservers("Unregister to Notification");

        }
    }
    public void setUserType(String type){
        userType=type;
    }

//    public void pressSubscribe(ActionEvent actionEvent){
//        setChanged();
//        notifyObservers("pressSubscribe");
//    }
//
//    public void subscribe(){
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("APP Dialog");
//        alert.setHeaderText("System Message");
//        alert.setContentText("NOTICE: You Registered  to Matchs's Notifications");
//        alert.show();
//        subscribe.setDisable(true);
//
//    }
}