package Service;

import Presentation.View;
import javafx.scene.control.Alert;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import java.util.*;

public class Presenter implements Observer {

    private Client client;
    private View view;
    private String username;
    private String verificationCode;
    private String teamName;
    private String leagueName;
    private String season;
    private ArrayList<String> courts=new ArrayList<String>();
    private ArrayList<String> coachsInDB=new ArrayList<String>();
    private ArrayList<String> playersInDB=new ArrayList<String>();
    private ArrayList<String> ownersInDB=new ArrayList<String>();
    private ArrayList<String> managersInDB=new ArrayList<String>();
    private ArrayList<String> teamAssets=new ArrayList<String>();


    public Presenter(Client client,View view) {
        this.client=client;
        this.view = view;
        this.username="";
    }

    @Override
    public void update(Observable o, Object arg) {

        /**
         * ================
         * Model Update
         * ================
         * */
       // if (client != null && o == client) {
//
//            /**
//             * Register
//             */
//            if (arg.equals("user already exist")) {
//                view.alert("Username already exist, pick another username", Alert.AlertType.ERROR);
//            } else if (arg.equals("User added successfully")) {
//                view.alert("User added successfully to the system", Alert.AlertType.INFORMATION);
//            }
//
//            /**
//             * Login
//             */
//
//            if (arg.equals("login failed, user doesn't exist")) {
//                view.alert("Username doesn't exist, try again", Alert.AlertType.ERROR);
//                view.setUi(View.userInstance.blank);
//            } else if (arg.equals("login failed, wrong password")) {
//                view.alert("Wrong password, try again", Alert.AlertType.ERROR);
//                view.setUi(View.userInstance.blank);
//            } else {
//                if (arg.equals("Association")) {
//                    view.setUi(View.userInstance.associationUser);
//                } else if (arg.equals("Referee")) {
//                    view.setUi(View.userInstance.referee);
//                } else if (arg.equals("TeamMember")) {
//                    view.setUi(View.userInstance.teamMember);
//                } else if (arg.equals("SystemManager")) {
//                    view.setUi(View.userInstance.systemManager);
//                } else if(arg.equals("Fan")) {
//                    view.setUi(View.userInstance.fan);
//                }
//                view.setLogin_successful(true);
//            }
//
//            if (arg.equals("season was added to system!")) {
//                view.setDoesSeasonExist(false);
//            }
//
//            if (arg.equals("team was added to system!")) {
//                view.setValidate_team(true);
//            }
//
//            if (arg.equals("The League " + view.getLeagueName() + " was added!")) {
//                view.alert("The League " + view.getLeagueName() + " was added!", Alert.AlertType.INFORMATION);
//            }
//
//            if(arg.equals("odd number of teams in league")){
//                view.alert("The League has odd number of teams\nPlease add one more team or remove one", Alert.AlertType.WARNING);
//            }
//
//            if(arg.equals("team isnt complete")){
//                view.alert("The team does not complete, check players,coaches and owners", Alert.AlertType.WARNING);
//            }
//
//            //
//            if(arg.equals("team was added successfully")){
//                view.alert("The team was added successfully", Alert.AlertType.INFORMATION);
//            }
//
        //}


        /**
         * ==============
         * View Update
         * =============
         * */

        if (view != null && o == view) {
           if (arg.equals("register")) {
                ArrayList<String> details = view.getRegisterDetails();
                String ans=client.openConnection("addUser"+":"+details.get(0)+":"+details.get(1)+":"+details.get(2)+":"+details.get(3)+":"+details.get(4)+":"+details.get(5)
                        +":"+details.get(6)+":"+details.get(7)+":"+details.get(8));
               username = details.get(2);

               if (ans.equals("user already exist")) {
                    client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                    view.alert("Username already exist, pick another username", Alert.AlertType.ERROR);
                } else if (ans.equals("User added successfully")) {
                    client.openConnection("checkEventLogs"+":"+username+":"+" Registered to the system");
                    view.alert("User added successfully to the system", Alert.AlertType.INFORMATION);
                }
                view.setValidate_user(true);
            }
            if (arg.equals("login")) {
                ArrayList<String> details = view.getLoginDetails();
                String ans = client.openConnection("loginUser" + ":" + details.get(0) + ":" + details.get(1));
                String[] splittedAns = splitData(ans);
                username = details.get(0);
                if(ans.equals("TeamMember")) {
                    teamName = splittedAns[5];
                }
                if (ans.equals("login failed, user doesn't exist")) {
                    client.openConnection("checkErrorLogs"+":"+"loginError"+":"+"login failed, user doesn't exist");
                    view.alert("Username doesn't exist, try again", Alert.AlertType.ERROR);
                    view.setUi(View.userInstance.blank);
                } else if (ans.equals("login failed, wrong password")) {
                    client.openConnection("checkErrorLogs"+":"+"loginError"+":"+"login failed, wrong password");
                    view.alert("Wrong password, try again", Alert.AlertType.ERROR);
                    view.setUi(View.userInstance.blank);
                } else {
                    if (ans.equals("Association")) {
                        view.setUi(View.userInstance.associationUser);
                    } else if (ans.equals("Referee")) {
                        view.setUi(View.userInstance.referee);
                    } else if (splittedAns[0].equals("TeamMember")) {
                        view.setPlayer(splittedAns[2]);
                        view.setOwner(splittedAns[3]);
                        view.setTeamManager(splittedAns[4]);
                        view.setCoach(splittedAns[1]);
                        view.setTeamStatus(splittedAns[6]);
                        view.setOwnerTeamName(splittedAns[5]);
                        view.setUi(View.userInstance.teamMember);
                    } else if (ans.equals("SystemManager")) {
                        view.setUi(View.userInstance.systemManager);
                    } else if (ans.equals("Fan")) {
                        view.setUi(View.userInstance.fan);
                    }
                    client.openConnection("checkEventLogs"+":"+username+":"+" Logged into the system");
                    view.setLogin_successful(true);
                }
                view.setLogin_successful(true);
            }
            else if(arg.equals("get requests for referee")){
                String ans=client.openConnection("getRefReqs"+":"+this.username);
                String[] splittedAns=splitData(ans);
                for(int i=0;i<splittedAns.length;i++) {
                    if(!splittedAns[i].equals("")) {
                        view.refsProposals.getItems().add(splittedAns[i]);
                    }
                }
            }
            else if(arg.equals(view.approvedReq)){
                String ans=client.openConnection("refApprovesToJudge"+":"+this.username+":"+view.approvedReq);
            }
            else
                username="";


            /**
             * Association
             */

            else if(arg instanceof Double){
                String sumOfIncome = String.valueOf(view.getSumOfIncome());
                Calendar cal = new GregorianCalendar();
                String year =  String.valueOf(cal.get(Calendar.YEAR) + 1);
                String serverAnswer = client.openConnection("checkIfSeasonExist"+":"+year);
                if(serverAnswer.equals("false")){
                    serverAnswer = client.openConnection("addSeason"+":"+year+":"+sumOfIncome);
                    if(serverAnswer.equals("added"))
                        view.setDoesSeasonExist(false);
                    else
                        view.setDoesSeasonExist(true);
                }else if(serverAnswer.equals("true")){
                    view.setDoesSeasonExist(true);
                }
            }

            else if (arg.equals("get refs in db")) {
                String serverAns = client.openConnection("getAllReferees");
                String[] refList = serverAns.split(":");
                for (int i = 0; i < refList.length; i++) {
                    view.candidateRefs.getItems().add(refList[i]);
                }
            }

            else if (arg.equals(view.refUsernameToNominate)) {
                String serverAns = client.openConnection("inviteRefereeToJudge"+":"+ username +":"+view.refUsernameToNominate);
                if(serverAns.equals("true")){
                    view.alert("Invite sent", Alert.AlertType.INFORMATION);
                }
                else{
                    view.alert("Try Again", Alert.AlertType.WARNING);
                }
            }

            else if (arg.equals("load team requests")) {
                String serverAns = client.openConnection("getTeamReqs"+":"+ username);
                String[] reqList = serverAns.split(":");
                for (int i = 0; i < reqList.length; i++) {
                    view.requestsList.getItems().add(reqList[i]);
                }
            }
            else if(arg.equals(view.selectedReq)){
                String serverAns = client.openConnection("checkTeamRegistration"+":"+ view.selectedReq);
                if(serverAns.equals("team was added successfully")){
                    view.alert("team was added to chosen league", Alert.AlertType.INFORMATION);
                    view.wasTeamAdded = true;
                }
                else{
                    view.alert("something went wrong, check your team has enough owners,coaches and players", Alert.AlertType.WARNING);
                    view.wasTeamAdded = false;
                }
            }
            else if (arg.equals(view.getYearPicked())) {
                String serverAns = client.openConnection("getCurrentSeason");
                if (Integer.parseInt(serverAns) == view.getYearPicked()) {
                    view.setCurrentSeason(true);
                }
                else {
                    view.setCurrentSeason(false);
                    view.alert("pick another season", Alert.AlertType.INFORMATION);
                }
            }
            else if (arg.equals(view.getLeagueName())) {
                String serverAns = client.openConnection("isLeagueExist"+":"+view.getLeagueName());
                if (serverAns.equals("true")) {
                    view.setLeagueExist(true);
                } else {
                    view.setLeagueExist(false);
                }
            }
            else if (arg.equals("add League")) {
                String serverAns = client.openConnection("addLeagueToDB"+":"+view.getNewLeagueDetails());
                if(serverAns.equals("true")){
                    view.alert("League added successfully", Alert.AlertType.INFORMATION);
                }
                else{
                    view.alert("League wasn't added", Alert.AlertType.WARNING);
                }
            }
            else if(arg.equals("fill leagues and refs list")){
                String leaguesAns = client.openConnection("showLeaguesInSeason");
                String[] leagueList = leaguesAns.split(":");
                for(int i = 0; i<leagueList.length; i++){
                    view.addRef_leagueList.getItems().add(leagueList[i]);
                }
                String refsAns = client.openConnection("showAllRefs");
                String[] refsList = refsAns.split(":");
                for(int i = 0; i<refsList.length; i++){
                    view.addRef_refsList.getItems().add(refsList[i]);
                }
            }
            else if (arg.equals(view.selectedLeague + " " + view.selectedRef)) {
                String serverAns = client.openConnection("addRefToLeague"+":"+view.selectedLeague+":"+view.selectedRef);
                if(serverAns.equals("true")){
                    view.wasRefAddedToLeage = true;
                    view.alert("Referee added successfully to League", Alert.AlertType.INFORMATION);
                }
                else{
                    view.wasRefAddedToLeage = false;
                    view.alert("Referee wasn't added to League", Alert.AlertType.WARNING);
                }
            }
            else if(arg.equals("show league list")){
                String leaguesAns = client.openConnection("showLeaguesInSeason");
                String[] leagueList = leaguesAns.split(":");
                for(int i = 0; i<leagueList.length; i++){
                    view.changeLeagePolicy.getItems().add(leagueList[i]);
                }
            }
            else if(arg.equals("change points policy")){
                String serverAns = client.openConnection("changePointsForLeague"+":"+view.leagueChangePoints+":"+view.newPointsWin
                        +":"+view.newPointsDraw+":"+view.newPointsLoss+":"+view.tieBreaker_goalDifference+":"+view.tieBreaker_directResults);
                if(serverAns.equals("true")){
                    view.alert("Points policy was changed in league - "+view.leagueChangePoints, Alert.AlertType.INFORMATION);
                }
            }

            else if (arg.equals("change game schedule policy")) {
                String serverAns = client.openConnection("changeRoundsForLeague"+":"+view.leagueChangeRounds+":"+view.newRounds);
                if(serverAns.equals("true")){
                    view.alert("Game policy was changed in league - "+view.leagueChangeRounds, Alert.AlertType.INFORMATION);
                }
            }
            else if (arg.equals("get leagues in db")) {
                String leaguesAns = client.openConnection("showLeaguesInSeason");
                String[] leagueList = leaguesAns.split(":");
                for(int i = 0; i<leagueList.length; i++){
                    view.scheduleGames_leagueList.getItems().add(leagueList[i]);
                }
            }
            else if(arg.equals(view.leagueNameToSchedule)){
                String serverAns = client.openConnection("createScheduleToLeague"+":"+view.leagueNameToSchedule);
                if(serverAns.equals("true")){
                    view.wasScheduleCreated = true;
                    view.alert("New games scheduling created", Alert.AlertType.INFORMATION);
                }
                else{
                    view.wasScheduleCreated = false;
                    view.alert(serverAns, Alert.AlertType.WARNING);
                }
            }




/*

            if (arg instanceof Double) {
                double sumOfIncome = view.getSumOfIncome();
                Calendar cal = new GregorianCalendar();
                int year = cal.get(Calendar.YEAR) + 1;
                if (!model.checkIfSeasonExist(year)) {
                    model.addSeason(year, sumOfIncome);
                } else {
                    view.setDoesSeasonExist(true);
                }
            }

            if (arg.equals(view.getYearPicked())) {
                if (model.getCurrentSeason() == view.getYearPicked()) {
                    view.setCurrentSeason(true);
                }
            }
            if (arg.equals(view.getLeagueName())) {
                if (model.isLeagueExist(view.getLeagueName())) {
                    view.setLeagueExist(true);
                } else {
                    view.setLeagueExist(false);
                }
            }

            if (arg.equals("add League")) {
                model.addLeagueToDB(view.getNewLeagueDetails());
            }


            if (arg.equals("get refs in db")) {
                ArrayList<Referee> refsInDB = model.getAllReferees();
                for (int i = 0; i < refsInDB.size(); i++) {
                    view.candidateRefs.getItems().add(refsInDB.get(i).getUserName() + ", " + refsInDB.get(i).getFirstName()
                            + " " + refsInDB.get(i).getLastName() + ", " + refsInDB.get(i).getRefereeRole());
                }
            }

            if (arg.equals("get available coachs")) {
                ArrayList<TeamMember> coachsInDB = model.getCoachs();
                for (int i = 0; i < coachsInDB.size(); i++) {
                    view.coachList.add(coachsInDB.get(i).getUserName() + ", " + coachsInDB.get(i).getFirstName()
                            + " " + coachsInDB.get(i).getLastName() + " - " + coachsInDB.get(i).getTeamRole());
                }
            }

            if (arg.equals("get available players")) {
                ArrayList<TeamMember> playersInDB = model.getPlayers();
                for (int i = 0; i < playersInDB.size(); i++) {
                    view.playerList.add(playersInDB.get(i).getUserName() + ", " + playersInDB.get(i).getFirstName()
                            + " " + playersInDB.get(i).getLastName() + " - " + playersInDB.get(i).getRoleOnCourt());
                }
            }

            if (arg.equals("get available owners")) {
                ArrayList<TeamMember> ownersInDB = model.getOwners();
                for (int i = 0; i < ownersInDB.size(); i++) {
                    view.ownerList.add(ownersInDB.get(i).getUserName() + ", " + ownersInDB.get(i).getFirstName()
                            + " " + ownersInDB.get(i).getLastName());
                }
            }

            if (arg.equals("get available managers")) {
                ArrayList<TeamMember> managersInDB = model.getManagers();
                for (int i = 0; i < managersInDB.size(); i++) {
                    view.managerList.add(managersInDB.get(i).getUserName() + ", " + managersInDB.get(i).getFirstName()
                            + " " + managersInDB.get(i).getLastName());
                }
            }

            if (arg.equals("createTeam")) {
                ArrayList<String> details = view.getTeamDetails();
                if (model.addTeam(details.get(0), Integer.parseInt(details.get(1)), Boolean.parseBoolean(details.get(2)), details.get(3), details.get(4), details.get(5))) {
                    view.setValidate_team(true);
                }
            }


            if(arg.equals("courtByCity")){
                ArrayList<String> details = view.getTeamDetails();
                if(model.chooseCourt(details.get(3))!=null){
                    view.setCourts(model.chooseCourt(details.get(3)) );
                }
            }

            if(arg.equals("teamMember")){
                String teamName = model.ownerTeamName(view.getLoginDetails().get(0));
                boolean isTeamActive= model.ownerTeamStatus(view.getLoginDetails().get(0));
                view.setTeamStatus(isTeamActive);
                view.setOwnerTeamName(teamName);
                view.setCoach(model.isCoach());
                view.setOwner(model.isOwner());
                view.setPlayer(model.isPlayer());
                view.setTeamManager(model.isTeamManager());
            }


            if (arg.equals(view.refUsernameToNominate)) {
                model.inviteRefereeToJudge(view.refUsernameToNominate);
            }

            if (arg.equals("add "+ view.getAssetNameToAdd())) {
                if(view.getAssetToAdd().equals("Owner")){
                    model.addOwnerToTeam(view.getAssetNameToAdd());
                }
                if(view.getAssetToAdd().equals("Player")){
                    model.addPlayerToTeam(view.getAssetNameToAdd(), view.getAssetRole());
                }
                if(view.getAssetToAdd().equals("Coach")){
                    model.addCoachToTeam(view.getAssetNameToAdd(), view.getAssetRole());
                }
                if(view.getAssetToAdd().equals("Manager")){
                    model.addManagerToTeam(view.getAssetNameToAdd(),view.ownerP_CHKBX.isSelected(),view.playerP_CHKBX.isSelected(),view.coachP_CHKBX.isSelected(),view.managerP_CHKBX.isSelected() );
                }
            }

            if (arg.equals("remove "+ view.getAsserNameToRemove())) {
                model.removeAsset(view.getAsserNameToRemove());
            }


            if(arg.equals("changeTeamStatus")){
                boolean newStatus = model.changeTeamStatus();
                view.setTeamStatus(newStatus);
            }

            if(arg.equals("allTeamAsset")){
                ArrayList<String> teamAssets =model.getTeamAssets();
                for(int i = 0; i<teamAssets.size(); i++){
                    view.allTeamMembers.getItems().add(teamAssets.get(i));
                }
            }


//            /*            */
//             * association



//            if (arg instanceof Double) {
//                double sumOfIncome = view.getSumOfIncome();
//                Calendar cal = new GregorianCalendar();
//                int year = cal.get(Calendar.YEAR) + 1;
//                if (!model.checkIfSeasonExist(year)) {
//                    model.addSeason(year, sumOfIncome);
//                } else {
//                    view.setDoesSeasonExist(true);
//                }
//            }
//
//            if (arg.equals(view.getYearPicked())) {
//                if (model.getCurrentSeason() == view.getYearPicked()) {
//                    view.setCurrentSeason(true);
//                }
//            }
//            if (arg.equals(view.getLeagueName())) {
//                if (model.isLeagueExist(view.getLeagueName())) {
//                    view.setLeagueExist(true);
//                } else {
//                    view.setLeagueExist(false);
//                }
//            }
//
//            if (arg.equals("add League")) {
//                model.addLeagueToDB(view.getNewLeagueDetails());
//            }
//
//
//            if (arg.equals("get refs in db")) {
//                ArrayList<Referee> refsInDB = model.getAllReferees();
//                for (int i = 0; i < refsInDB.size(); i++) {
//                    view.candidateRefs.getItems().add(refsInDB.get(i).getUserName() + ", " + refsInDB.get(i).getFirstName()
//                            + " " + refsInDB.get(i).getLastName() + ", " + refsInDB.get(i).getRefereeRole());
//                }
//            }
//
            if (arg.equals("get available coachs")) {
                 String ans = client.openConnection("getCoachs");
                if (!ans.equals("")){
                    String[] splittedans = splitData(ans);
                    for(int i=0; i<splittedans.length;i++) {
                        coachsInDB.add(splittedans[i]);
                    }
                }
                view.coachList.addAll(coachsInDB);
            }

            if (arg.equals("get available players")) {
                String ans = client.openConnection("getPlayers");
                if (!ans.equals("")){
                    String[] splittedans = splitData(ans);
                    for(int i=0; i<splittedans.length;i++) {
                        playersInDB.add(splittedans[i]);
                    }
                }

                view.playerList.addAll(playersInDB);

            }

            if (arg.equals("get available owners")) {
                String ans = client.openConnection("getOwners");
                if (!ans.equals("")){
                    String[] splittedans = splitData(ans);
                    for(int i=0; i<splittedans.length;i++) {
                        ownersInDB.add(splittedans[i]);
                    }
                }
                view.ownerList.addAll(ownersInDB);

            }
            if (arg.equals("get available managers")) {
                String ans = client.openConnection("getManagers");
                if (!ans.equals("")){
                    String[] splittedans = splitData(ans);
                    for(int i=0; i<splittedans.length;i++) {
                        managersInDB.add(splittedans[i]);
                    }
                }
                view.managerList.addAll(managersInDB);
            }
//
//            if (arg.equals("get available players")) {
//                ArrayList<TeamMember> playersInDB = model.getPlayers();
//                for (int i = 0; i < playersInDB.size(); i++) {
//                    view.playerList.add(playersInDB.get(i).getUserName() + ", " + playersInDB.get(i).getFirstName()
//                            + " " + playersInDB.get(i).getLastName() + " - " + playersInDB.get(i).getRoleOnCourt());
//                }
//            }
//
//            if (arg.equals("get available owners")) {
//                ArrayList<TeamMember> ownersInDB = model.getOwners();
//                for (int i = 0; i < ownersInDB.size(); i++) {
//                    view.ownerList.add(ownersInDB.get(i).getUserName() + ", " + ownersInDB.get(i).getFirstName()
//                            + " " + ownersInDB.get(i).getLastName());
//                }
//            }
//
//            if (arg.equals("get available managers")) {
//                ArrayList<TeamMember> managersInDB = model.getManagers();
//                for (int i = 0; i < managersInDB.size(); i++) {
//                    view.managerList.add(managersInDB.get(i).getUserName() + ", " + managersInDB.get(i).getFirstName()
//                            + " " + managersInDB.get(i).getLastName());
//                }
//            }
//
            if (arg.equals("createTeam")) {
                ArrayList<String> details = view.getTeamDetails();
                String ans = client.openConnection("addTeam" + ":" + details.get(0) + ":" + details.get(1) + ":" + details.get(2) + ":" + details.get(3) + ":" + details.get(4) + ":" + details.get(5));
                if (ans.equals("team was added to system!")) {
                    client.openConnection("checkEventLogs"+":"+username+":"+teamName+" added to the system");
                    //view.setValidate_team(true);
                }
                else if (ans.equals("team already exist")) {
                    client.openConnection("checkErrorLogs" + ":" + username + ":" + teamName + " team already exist");
                    view.alert("team already exist", Alert.AlertType.ERROR);
                }
                view.setValidate_team(true);
            }

            if(arg.equals("courtByCity")) {
                ArrayList<String> details = view.getTeamDetails();
                String ans = client.openConnection("chooseCourt" + ":" + details.get(3));
                if (!ans.equals("")){
                    String[] splittedans = splitData(ans);
                    for(int i=0; i<splittedans.length;i++) {
                        courts.add(splittedans[i]);
                    }
                    view.setCourts(courts);
                }
            }


//            if(arg.equals("teamMember")){
//                String teamName = model.ownerTeamName(view.getLoginDetails().get(0));
//                boolean isTeamActive= model.ownerTeamStatus(view.getLoginDetails().get(0));
//                view.setTeamStatus(isTeamActive);
//                view.setOwnerTeamName(teamName);
//                view.setCoach(model.isCoach());
//                view.setOwner(model.isOwner());
//                view.setPlayer(model.isPlayer());
//                view.setTeamManager(model.isTeamManager());
//            }
//
//
//            if (arg.equals(view.refUsernameToNominate)) {
//                model.inviteRefereeToJudge(view.refUsernameToNominate);
//            }
//
            if (arg.equals("add "+ view.getAssetNameToAdd())) {

                if(view.getAssetToAdd().equals("Owner")){
                    String ans = client.openConnection("addOwnerToTeam" + ":" + view.getAssetNameToAdd()+":"+username);
                    if (ans.equals("Owner added Successful")) {
                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                        view.alert("Owner added Successful", Alert.AlertType.INFORMATION);
                    }
                    else if (ans.equals("Owner added isn't Successful")) {
                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                        view.alert("Owner added isn't Successful", Alert.AlertType.INFORMATION);
                    }
                }
                if(view.getAssetToAdd().equals("Player")) {
                    String ans = client.openConnection("addPlayerToTeam" + ":" + view.getAssetNameToAdd() + ":" + view.getAssetRole() + ":" + username);
                    if (ans.equals("Player added Successful")) {
                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                        view.alert("Player added Successful", Alert.AlertType.INFORMATION);
                    } else if (ans.equals("Player added isn't Successful")) {
                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                        view.alert("Player added isn't Successful", Alert.AlertType.INFORMATION);
                    }
                }
                if(view.getAssetToAdd().equals("Coach")){
                    String ans = client.openConnection("addCoachToTeam" + ":" + view.getAssetNameToAdd() +":"+ view.getAssetRole()+":"+username);
                    if (ans.equals("Coach added Successful")) {
                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                        view.alert("Coach added Successful", Alert.AlertType.INFORMATION);
                    } else if (ans.equals("Coach added isn't Successful")) {
                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                        view.alert("Coach added isn't Successful", Alert.AlertType.INFORMATION);
                    }
                }
                if(view.getAssetToAdd().equals("Manager")){
                    String ans = client.openConnection("addManagerToTeam" + ":" + view.getAssetNameToAdd() +":"+ String.valueOf(view.ownerP_CHKBX.isSelected())+":"+
                            String.valueOf(view.playerP_CHKBX.isSelected())+":"+ String.valueOf(view.coachP_CHKBX.isSelected())+":"+ String.valueOf(view.managerP_CHKBX.isSelected())+":"+username);
                    if (ans.equals("Manager added Successful")) {
                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                        view.alert("Manager added Successful", Alert.AlertType.INFORMATION);
                    } else if (ans.equals("Manager added isn't Successful")) {
                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                        view.alert("Manager added isn't Successful", Alert.AlertType.INFORMATION);
                    }
                }
            }

            if (arg.equals("remove "+ view.getAsserNameToRemove())) {
                    String ans = client.openConnection("removeAsset" + ":" + view.getAsserNameToRemove() + ":" + username);
                    if (ans.equals("Remove Successful")) {
                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                        view.alert("Remove Successful", Alert.AlertType.INFORMATION);
                    } else if (ans.equals("Remove isn't Successful")) {
                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                        view.alert("Remove isn't Successful", Alert.AlertType.INFORMATION);
                    } else if (ans.equals("The user is not nominate by: " + view.getAsserNameToRemove() + " or the team must have at least one owner")) {
                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
                        view.alert("The user is not nominate by: " + view.getAsserNameToRemove() + " or the team must have at least one owner", Alert.AlertType.INFORMATION);
                    }
            }



            if (arg.equals("changeTeamStatus")) {
                String newStatus = client.openConnection("changeTeamStatus:" + teamName);
                view.setTeamStatus(newStatus);
            }

            if(arg.equals("allTeamAsset")){
                teamAssets.clear();
                String ans =client.openConnection("getTeamAssets"+":"+username);
//                if (ans.equals("Remove Successful")) {
//                    //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
//                    view.alert("Remove Successful for team "+teamName, Alert.AlertType.INFORMATION);
//                }
//                else if (ans.equals("Remove isn't Successful")) {
//                        //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
//                        view.alert("Remove isn't Successful for team "+teamName, Alert.AlertType.INFORMATION);
//                    }
//                else if (ans.equals("The user is not nominate by: "+ meytalm +"or the team must have at least one coach")) {
//                    //client.openConnection("checkErrorLogs"+":"+"registerError"+":"+"user already exist");
//                    view.alert("The user is not nominate by: meytalm or the team must have at least one coach "+teamName, Alert.AlertType.INFORMATION);
//                }
                if(!ans.equals("")) {
                    String[] splittedans=splitData(ans);
                    for (int i = 0; i < splittedans.length; i++) {
                        teamAssets.add(splittedans[i]);
                    }
                    view.allTeamMembers.getItems().addAll(teamAssets);
                }
            }

//            if(arg.equals("change points policy")){
//                model.changePointsForLeague(view.leagueChangePoints, Integer.parseInt(view.newPointsWin), Integer.parseInt(view.newPointsDraw),
//                        Integer.parseInt(view.newPointsLoss) ,view.tieBreaker_goalDifference,view.tieBreaker_directResults);
//            }
//
//            if(arg.equals("show league list")){
//                ArrayList<String> leagueChangeP =model.showLeagueList();
//                for(int i = 0; i<leagueChangeP.size(); i++){
//                    view.changeLeagePolicy.getItems().add(leagueChangeP.get(i));
//                }
//            }
//
//            if(arg.equals("get leagues in db")){
//                ArrayList<String> leagueChangeP =model.showLeagueList();
//                for(int i = 0; i<leagueChangeP.size(); i++){
//                    view.scheduleGames_leagueList.getItems().add(leagueChangeP.get(i));
//                }
//            }
//
//            if(arg.equals("change game scedule policy")){
//                model.changeRoundsForLeague(view.leagueChangePoints, Integer.parseInt(view.newRounds));
//            }
//
//            if(arg.equals(view.leagueNameToSchedule)){
//                model.createScheduleToLeague(view.leagueNameToSchedule);
//            }
//
//            if(arg.equals("fill leagues and refs list")){
//                ArrayList<String> leagueChangeP =model.showLeaguesInSeason();
//                for(int i = 0; i<leagueChangeP.size(); i++){
//                    view.addRef_leagueList.getItems().add(leagueChangeP.get(i));
//                }
//
//                ArrayList<Referee> allRefs = model.showAllRefs();
//                for(int i = 0; i<allRefs.size(); i++){
//                    view.addRef_refsList.getItems().add(allRefs.get(i).getUserName()+" - "+allRefs.get(i).getFirstName()+" "+
//                            allRefs.get(i).getLastName()+" - "+allRefs.get(i).getRefereeRole());
//                }
//            }
//
//            if(arg.equals(view.selectedLeague+" "+view.selectedRef)){
//                model.addRefToLeague(view.selectedLeague, view.selectedRef);
//            }
//
//            if(arg.equals("load team requests")){
//                ArrayList<String> allTeamReq = model.getTeamReqs();
//                for(int i = 0; i<allTeamReq.size(); i++){
//                    view.requestsList.getItems().add(allTeamReq.get(i));
//                }
//
//            }
//
//            if(arg.equals(view.selectedReq)){
//                model.checkTeamRegistration(view.selectedReq);
//            }
//
//            if(arg.equals("get requests for referee")){
//                ArrayList<String> appReq = model.getRefReqs();
//                for(int i = 0; i<appReq.size(); i++){
//                    view.refsProposals.getItems().add(appReq.get(i));
//                }
//            }
//
            if(arg.equals(view.approvedReq)){
                String ans=client.openConnection("refApprovesToJudge"+":"+this.username+":"+view.approvedReq);
            }


        }





    }



    public static String[] splitData(String data){
        return data.split(":");
    }



}