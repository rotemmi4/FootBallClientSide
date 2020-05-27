package Service;

import Presentation.View;
import javafx.scene.control.Alert;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Presenter implements Observer {

    private Client client;
    private View view;
    private String username;
    private String verificationCode;
    private String teamName;
    private String leagueName;
    private String season;
    private ArrayList<String> courts=new ArrayList<String>();



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
        if (client != null && o == client) {
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
        }


        /**
         * ==============
         * View Update
         * =============
         * */

        if (view != null && o == view) {
/*            if (arg.equals("register")) {
                ArrayList<String> details = view.getRegisterDetails();
                if (model.addUser(details.get(0), details.get(1), details.get(2), details.get(3), details.get(4), details.get(5)
                        , details.get(6), details.get(7), details.get(8)))
                    view.setValidate_user(true);
            }*/
            if (arg.equals("login")) {
                ArrayList<String> details = view.getLoginDetails();
                String ans = client.openConnection("loginUser" + ":" + details.get(0) + ":" + details.get(1));
                String[] splittedAns = splitData(ans);
                username = details.get(0);
                teamName = splittedAns[5];
                if (ans.equals("login failed, user doesn't exist")) {
                    view.alert("Username doesn't exist, try again", Alert.AlertType.ERROR);
                    view.setUi(View.userInstance.blank);
                } else if (ans.equals("login failed, wrong password")) {
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
                    view.setLogin_successful(true);
                }
                view.setLogin_successful(true);
            } else
                username = "";

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
//            if (arg.equals("get available coachs")) {
//                ArrayList<TeamMember> coachsInDB = model.getCoachs();
//                for (int i = 0; i < coachsInDB.size(); i++) {
//                    view.coachList.add(coachsInDB.get(i).getUserName() + ", " + coachsInDB.get(i).getFirstName()
//                            + " " + coachsInDB.get(i).getLastName() + " - " + coachsInDB.get(i).getTeamRole());
//                }
//            }
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
                    view.setValidate_team(true);
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
//            if (arg.equals("add "+ view.getAssetNameToAdd())) {
//                if(view.getAssetToAdd().equals("Owner")){
//                    model.addOwnerToTeam(view.getAssetNameToAdd());
//                }
//                if(view.getAssetToAdd().equals("Player")){
//                    model.addPlayerToTeam(view.getAssetNameToAdd(), view.getAssetRole());
//                }
//                if(view.getAssetToAdd().equals("Coach")){
//                    model.addCoachToTeam(view.getAssetNameToAdd(), view.getAssetRole());
//                }
//                if(view.getAssetToAdd().equals("Manager")){
//                    model.addManagerToTeam(view.getAssetNameToAdd(),view.ownerP_CHKBX.isSelected(),view.playerP_CHKBX.isSelected(),view.coachP_CHKBX.isSelected(),view.managerP_CHKBX.isSelected() );
//                }
//            }
//
//            if (arg.equals("remove "+ view.getAsserNameToRemove())) {
//                model.removeAsset(view.getAsserNameToRemove());
//            }
//
//
            if (arg.equals("changeTeamStatus")) {
                String newStatus = client.openConnection("changeTeamStatus:" + teamName);
                view.setTeamStatus(newStatus);
            }
//
//            if(arg.equals("allTeamAsset")){
//                ArrayList<String> teamAssets =model.getTeamAssets();
//                for(int i = 0; i<teamAssets.size(); i++){
//                    view.allTeamMembers.getItems().add(teamAssets.get(i));
//                }
//            }
//
//
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
//            if(arg.equals(view.approvedReq)){
//                model.refApprovesToJudge(view.approvedReq);
//            }


        }

    }







    public static String[] splitData(String data){
        return data.split(":");
    }



}