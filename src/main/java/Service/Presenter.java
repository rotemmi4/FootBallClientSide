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
    private ArrayList<String> courts = new ArrayList<String>();
    private ArrayList<String> coachsInDB = new ArrayList<String>();
    private ArrayList<String> playersInDB = new ArrayList<String>();
    private ArrayList<String> ownersInDB = new ArrayList<String>();
    private ArrayList<String> managersInDB = new ArrayList<String>();
    private ArrayList<String> teamAssets = new ArrayList<String>();


    public Presenter(Client client, View view) {
        this.client = client;
        this.view = view;
        //this.username="";
    }

    @Override
    public void update(Observable o, Object arg) {
        /**
         * ==============
         * View Update
         * =============
         **/
        if (view != null && o == view) {

            /**
             * register
             **/
            if (arg.equals("register")) {
                ArrayList<String> details = view.getRegisterDetails();
                String ans = null;
                try {
                    ans = client.openConnection("addUser" + ":" + details.get(0) + ":" + details.get(1) + ":" + details.get(2) + ":" + details.get(3) + ":" + details.get(4) + ":" + details.get(5)
                            + ":" + details.get(6) + ":" + details.get(7) + ":" + details.get(8));
                } catch (Exception e) {
                    view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                }
                username = details.get(2);

                if (ans.equals("user already exist")) {
                    try {
                        client.openConnection("checkErrorLogs" + ":" + "registerError" + ":" + "user already exist");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    view.alert("Username already exist, pick another username", Alert.AlertType.ERROR);
                } else if (ans.equals("User added successfully")) {
                    try {
                        client.openConnection("checkEventLogs" + ":" + username + ":" + " Registered to the system");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    view.alert("User added successfully to the system", Alert.AlertType.INFORMATION);
                }
                view.setValidate_user(true);
            }
            /**
             * login
             **/
            if (arg.equals("login")) {
                ArrayList<String> details = view.getLoginDetails();
                String ans = null;
                try {
                    ans = client.openConnection("loginUser" + ":" + details.get(0) + ":" + details.get(1));
                } catch (Exception e) {
                    view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                }
                String[] splittedAns = ans.split(":");
                    username = details.get(0);
                    if (ans.equals("TeamMember")) {
                        teamName = splittedAns[5];
                    }
                    if (ans.equals("login failed, user doesn't exist")) {
                        try {
                            client.openConnection("checkErrorLogs" + ":" + "loginError" + ":" + "login failed, " + username + " doesn't exist");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("Username doesn't exist, try again", Alert.AlertType.ERROR);
                        view.setUi(View.userInstance.blank);
                    } else if (ans.equals("login failed, wrong password")) {
                        try {
                            client.openConnection("checkErrorLogs" + ":" + "loginError" + ":" + "login failed, wrong password");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("Wrong password, try again", Alert.AlertType.ERROR);
                        view.setUi(View.userInstance.blank);
                    } else if (ans.equals("Failed to connect the DB!")) {
                        try {
                            client.openConnection("checkErrorLogs" + ":" + "loginError" + ":" + "Failed to connect the DB!");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        //view.alert("Failed to connect the DB!", Alert.AlertType.ERROR);
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
                        try {
                            client.openConnection("checkEventLogs" + ":" + username + ":" + " Logged into the system");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.setLogin_successful(true);
                    }
                    view.setLogin_successful(true);
                }
            /**
             * get requests for referee
             **/
                else if (arg.equals("get requests for referee")) {
                    String ans = null;
                    try {
                        ans = client.openConnection("getRefReqs" + ":" + this.username);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    String[] splittedAns = ans.split(":");
                    for (int i = 0; i < splittedAns.length; i++) {
                        if (!splittedAns[i].equals("")) {
                            view.refsProposals.getItems().add(splittedAns[i]);
                        }
                    }
                }
            /**
             * approvedReq
             **/
                else if (arg.equals(view.approvedReq)) {
                    try {
                        String ans = client.openConnection("refApprovesToJudge" + ":" + this.username + ":" + view.approvedReq);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                } else if (arg.equals("disconnect")) {
                    //client.closeConnection();
                }
                /**
                 * Association
                 **/
                else if (arg instanceof Double) {
                    String sumOfIncome = String.valueOf(view.getSumOfIncome());
                    Calendar cal = new GregorianCalendar();
                    String year = String.valueOf(cal.get(Calendar.YEAR) + 1);
                    String serverAnswer = null;
                    try {
                        serverAnswer = client.openConnection("checkIfSeasonExist" + ":" + year);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (serverAnswer.equals("false")) {
                        try {
                            serverAnswer = client.openConnection("addSeason" + ":" + year + ":" + sumOfIncome);
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        if (serverAnswer.equals("added")) {
                            try {
                                client.openConnection("checkEventLogs" + ":" + username + ":" + " added new Season to the system");
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                            view.setDoesSeasonExist(false);
                        } else{
                            try {
                                client.openConnection("checkErrorLogs" + ":" + username + ":" + " new Season wasn't added to the system");
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                            view.setDoesSeasonExist(true);
                    }
                    } else if (serverAnswer.equals("true")) {
                        view.setDoesSeasonExist(true);
                    }
                } else if (arg.equals("get refs in db")) {
                    String serverAns = null;
                    try {
                        serverAns = client.openConnection("getAllReferees");
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    String[] refList = serverAns.split(":");
                    for (int i = 0; i < refList.length; i++) {
                        view.candidateRefs.getItems().add(refList[i]);
                    }
                } else if (arg.equals(view.refUsernameToNominate)) {
                    String serverAns = null;
                    try {
                        serverAns = client.openConnection("inviteRefereeToJudge" + ":" + username + ":" + view.refUsernameToNominate);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (serverAns.equals("true")) {
                        view.alert("Invite sent", Alert.AlertType.INFORMATION);
                    } else {
                        view.alert("Try Again", Alert.AlertType.WARNING);
                    }
                } else if (arg.equals("load team requests")) {
                    String serverAns = null;
                    try {
                        serverAns = client.openConnection("getTeamReqs" + ":" + username);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    String[] reqList = serverAns.split(":");
                    for (int i = 0; i < reqList.length; i++) {
                        view.requestsList.getItems().add(reqList[i]);
                    }
                } else if (arg.equals(view.selectedReq)) {
                    String serverAns = null;
                    try {
                        serverAns = client.openConnection("checkTeamRegistration" + ":" + view.selectedReq);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (serverAns.equals("team was added successfully")) {
                        view.alert("team was added to chosen league", Alert.AlertType.INFORMATION);
                        try {
                            client.openConnection("checkEventLogs" + ":" + username + ":" + " team "+teamName+" was added to league "+leagueName);
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.wasTeamAdded = true;
                    } else {
                        view.alert("something went wrong, check your team has enough owners,coaches and players", Alert.AlertType.WARNING);
                        try {
                            client.openConnection("checkErrorLogs" + ":" + username + ":" + " something went wrong team was'nt added to chosen league");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.wasTeamAdded = false;
                    }
                } else if (arg.equals(view.getYearPicked())) {
                    String serverAns = null;
                    try {
                        serverAns = client.openConnection("getCurrentSeason");
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (Integer.parseInt(serverAns) == view.getYearPicked()) {
                        view.setCurrentSeason(true);
                    } else {
                        view.setCurrentSeason(false);
                        view.alert("pick another season", Alert.AlertType.INFORMATION);
                    }
                } else if (arg.equals(view.getLeagueName())) {
                    String serverAns = null;
                    try {
                        serverAns = client.openConnection("isLeagueExist" + ":" + view.getLeagueName());
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (serverAns.equals("true")) {
                        view.setLeagueExist(true);
                    } else {
                        view.setLeagueExist(false);
                    }
                } else if (arg.equals("add League")) {
                    String serverAns = null;
                    try {
                        serverAns = client.openConnection("addLeagueToDB" + ":" + view.getNewLeagueDetails());
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (serverAns.equals("true")) {
                        try {
                            client.openConnection("checkEventLogs" + ":" + username + ":" + " League added to the system");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("League added successfully", Alert.AlertType.INFORMATION);
                    } else {
                        try {
                            client.openConnection("checkErrorLogs" + ":" + username + ":" + " League wasn't added to the system");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("League wasn't added", Alert.AlertType.WARNING);
                    }
                } else if (arg.equals("fill leagues and refs list")) {
                    String leaguesAns = null;
                    try {
                        leaguesAns = client.openConnection("showLeaguesInSeason");
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    String[] leagueList = leaguesAns.split(":");
                    for (int i = 0; i < leagueList.length; i++) {
                        view.addRef_leagueList.getItems().add(leagueList[i]);
                    }
                    String refsAns = null;
                    try {
                        refsAns = client.openConnection("showAllRefs");
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    String[] refsList = refsAns.split(":");
                    for (int i = 0; i < refsList.length; i++) {
                        view.addRef_refsList.getItems().add(refsList[i]);
                    }
                } else if (arg.equals(view.selectedLeague + " " + view.selectedRef)) {
                    String serverAns = null;
                    try {
                        serverAns = client.openConnection("addRefToLeague" + ":" + view.selectedLeague + ":" + view.selectedRef);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (serverAns.equals("true")) {
                        view.wasRefAddedToLeage = true;
                        try {
                            client.openConnection("checkEventLogs" + ":" + username + ":" + " added Referee successfully to League");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("Referee added successfully to League", Alert.AlertType.INFORMATION);
                    } else {
                        view.wasRefAddedToLeage = false;
                        try {
                            client.openConnection("checkEventLogs" + ":" + username + ":" + " added Referee to League wasn't successfully");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("Referee wasn't added to League", Alert.AlertType.WARNING);
                    }
                } else if (arg.equals("show league list")) {
                    String leaguesAns = null;
                    try {
                        leaguesAns = client.openConnection("showLeaguesInSeason");
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    String[] leagueList = leaguesAns.split(":");
                    for (int i = 0; i < leagueList.length; i++) {
                        view.changeLeagePolicy.getItems().add(leagueList[i]);
                    }
                } else if (arg.equals("change points policy")) {
                    String serverAns = null;
                    try {
                        serverAns = client.openConnection("changePointsForLeague" + ":" + view.leagueChangePoints + ":" + view.newPointsWin
                                + ":" + view.newPointsDraw + ":" + view.newPointsLoss + ":" + view.tieBreaker_goalDifference + ":" + view.tieBreaker_directResults);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (serverAns.equals("true")) {
                        try {
                            client.openConnection("checkEventLogs" + ":" + username + ":" + " Points policy was changed in league - " + view.leagueChangePoints);
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("Points policy was changed in league - " + view.leagueChangePoints, Alert.AlertType.INFORMATION);
                    }
                } else if (arg.equals("change game schedule policy")) {
                    String serverAns = null;
                    try {
                        serverAns = client.openConnection("changeRoundsForLeague" + ":" + view.leagueChangeRounds + ":" + view.newRounds);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (serverAns.equals("true")) {
                        try {
                            client.openConnection("checkEventLogs" + ":" + username + ":" + " Game policy was changed in league - " + view.leagueChangeRounds);
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("Game policy was changed in league - " + view.leagueChangeRounds, Alert.AlertType.INFORMATION);
                    }
                } else if (arg.equals("get leagues in db")) {
                    String leaguesAns = null;
                    try {
                        leaguesAns = client.openConnection("showLeaguesInSeason");
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    String[] leagueList = leaguesAns.split(":");
                    for (int i = 0; i < leagueList.length; i++) {
                        view.scheduleGames_leagueList.getItems().add(leagueList[i]);
                    }
                } else if (arg.equals(view.leagueNameToSchedule)) {
                    String serverAns = null;
                    try {
                        serverAns = client.openConnection("createScheduleToLeague" + ":" + view.leagueNameToSchedule);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (serverAns.equals("true")) {
                        view.wasScheduleCreated = true;
                        try {
                            client.openConnection("checkEventLogs" + ":" + username + ":" + " New games scheduling created in the system");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("New games scheduling created", Alert.AlertType.INFORMATION);
                    } else {
                        view.wasScheduleCreated = false;
                        try {
                            client.openConnection("checkErrorLogs" + ":" + username + ":" + " New games scheduling wasn't created in the system");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert(serverAns, Alert.AlertType.WARNING);
                    }
                }
                /**
                 * Team
                 **/
                if (arg.equals("get available coachs")) {
                    coachsInDB.clear();
                    String ans = null;
                    try {
                        ans = client.openConnection("getCoachs");
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (!ans.equals("")) {
                        String[] splittedans = ans.split(":");
                        for (int i = 0; i < splittedans.length; i++) {
                            coachsInDB.add(splittedans[i]);
                        }
                    }
                    view.coachList.addAll(coachsInDB);
                }

                if (arg.equals("get available players")) {
                    playersInDB.clear();
                    String ans = null;
                    try {
                        ans = client.openConnection("getPlayers");
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (!ans.equals("")) {
                        String[] splittedans = ans.split(":");
                        for (int i = 0; i < splittedans.length; i++) {
                            playersInDB.add(splittedans[i]);
                        }
                    }
                    view.playerList.addAll(playersInDB);
                }

            if (arg.equals("get available owners")) {
                    ownersInDB.clear();
                    String ans = null;
                    try {
                        ans = client.openConnection("getOwners");
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (!ans.equals("")) {
                        String[] splittedans = ans.split(":");
                        for (int i = 0; i < splittedans.length; i++) {
                            ownersInDB.add(splittedans[i]);
                        }
                    }
                    view.ownerList.addAll(ownersInDB);
                }
                if (arg.equals("get available managers")) {
                    managersInDB.clear();
                    String ans = null;
                    try {
                        ans = client.openConnection("getManagers");
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (!ans.equals("")) {
                        String[] splittedans = ans.split(":");
                        for (int i = 0; i < splittedans.length; i++) {
                            managersInDB.add(splittedans[i]);
                        }
                    }
                    view.managerList.addAll(managersInDB);
                }

                if (arg.equals("createTeam")) {
                    ArrayList<String> details = view.getTeamDetails();
                    String ans = null;
                    try {
                        ans = client.openConnection("addTeam" + ":" + details.get(0) + ":" + details.get(1) + ":" + details.get(2) + ":" + details.get(3) + ":" + details.get(4) + ":" + details.get(5));
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    teamName=details.get(0);
                    if (ans.equals("team was added to system!")) {
                        try {
                            client.openConnection("checkEventLogs" + ":" + username + ":" + teamName + " added to the system");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                    } else if (ans.equals("team already exist")) {
                        try {
                            client.openConnection("checkErrorLogs" + ":" + username + ":" + teamName + " team already exist");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("team already exist", Alert.AlertType.ERROR);
                    }
                    view.setValidate_team(true);
                }

                if (arg.equals("courtByCity")) {
                    ArrayList<String> details = view.getTeamDetails();
                    String ans = null;
                    try {
                        ans = client.openConnection("chooseCourt" + ":" + details.get(3));
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (!ans.equals("")) {
                        String[] splittedans = ans.split(":");
                        for (int i = 0; i < splittedans.length; i++) {
                            courts.add(splittedans[i]);
                        }
                        view.setCourts(courts);
                    }
                }

                if (arg.equals("add " + view.getAssetNameToAdd())) {
                    if (view.getAssetToAdd().equals("Owner")) {
                        String ans = null;
                        try {
                            ans = client.openConnection("addOwnerToTeam" + ":" + view.getAssetNameToAdd() + ":" + username);
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        String[] splittedans=ans.split(":");
                        teamName=splittedans[0];
                        if (splittedans[1].equals("Owner added Successful")) {
                            try {
                                client.openConnection("checkEventLogs" + ":" + username + ":" + view.getAssetNameToAdd() + " added to the team "+teamName);
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                            view.alert("Owner added Successful", Alert.AlertType.INFORMATION);
                        } else if (splittedans[1].equals("Owner added isn't Successful")) {
                            try {
                                client.openConnection("checkErrorLogs" + ":" + username + ":" + view.getAssetNameToAdd() + " already exist");
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                            view.alert("Owner added isn't Successful", Alert.AlertType.INFORMATION);
                        }
                    }
                    if (view.getAssetToAdd().equals("Player")) {
                        String ans = null;
                        try {
                            ans = client.openConnection("addPlayerToTeam" + ":" + view.getAssetNameToAdd() + ":" + view.getAssetRole() + ":" + username);
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        String[] splittedans=ans.split(":");
                        teamName=splittedans[0];
                        if (splittedans[1].equals("Player added Successful")) {
                            try {
                                client.openConnection("checkEventLogs" + ":" + username + ":" + view.getAssetNameToAdd() + " added to the team "+teamName);
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                            view.alert("Player added Successful", Alert.AlertType.INFORMATION);
                        } else if (splittedans[1].equals("Player added isn't Successful")) {
                            try {
                                client.openConnection("checkErrorLogs" + ":" + username + ":" + view.getAssetNameToAdd() + " cant be added to the team "+teamName);
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                            view.alert("Player added isn't Successful", Alert.AlertType.INFORMATION);
                        }
                    }
                    if (view.getAssetToAdd().equals("Coach")) {
                        String ans = null;
                        try {
                            ans = client.openConnection("addCoachToTeam" + ":" + view.getAssetNameToAdd() + ":" + view.getAssetRole() + ":" + username);
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        String[] splittedans=ans.split(":");
                        teamName=splittedans[0];
                        if (splittedans[1].equals("Coach added Successful")) {
                            try {
                                client.openConnection("checkEventLogs" + ":" + username + ":" + view.getAssetNameToAdd() + " added to the team "+teamName);
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                            view.alert("Coach added Successful", Alert.AlertType.INFORMATION);
                        } else if (splittedans[1].equals("Coach added isn't Successful")) {
                            try {
                                client.openConnection("checkErrorLogs" + ":" + username + ":" + view.getAssetNameToAdd() + " already exist");
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                            view.alert("Coach added isn't Successful", Alert.AlertType.INFORMATION);
                        }
                    }
                    if (view.getAssetToAdd().equals("Manager")) {
                        String ans = null;
                        try {
                            ans = client.openConnection("addManagerToTeam" + ":" + view.getAssetNameToAdd() + ":" + String.valueOf(view.ownerP_CHKBX.isSelected()) + ":" +
                                    String.valueOf(view.playerP_CHKBX.isSelected()) + ":" + String.valueOf(view.coachP_CHKBX.isSelected()) + ":" + String.valueOf(view.managerP_CHKBX.isSelected()) + ":" + username);
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        String[] splittedans=ans.split(":");
                        teamName=splittedans[0];
                        if (splittedans[1].equals("Manager added Successful")) {
                            try {
                                client.openConnection("checkEventLogs" + ":" + username + ":" + view.getAssetNameToAdd() + " added to the team "+teamName);
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                            view.alert("Manager added Successful", Alert.AlertType.INFORMATION);
                        } else if (splittedans[1].equals("Manager added isn't Successful")) {
                            try {
                                client.openConnection("checkErrorLogs" + ":" + username + ":" + view.getAssetNameToAdd() + " already exist");
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                            view.alert("Manager added isn't Successful", Alert.AlertType.INFORMATION);
                        }
                    }
                }

                if (arg.equals("remove " + view.getAsserNameToRemove())) {
                    String ans = null;
                    try {
                        ans = client.openConnection("removeAsset" + ":" + view.getAsserNameToRemove() + ":" + username);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    String[] splittedans=ans.split(":");
                    teamName=splittedans[0];
                    if (splittedans[1].equals("Remove Successful")) {
                        view.alert("Remove Successful", Alert.AlertType.INFORMATION);
                        if(splittedans[2].equals("false")) {
                            try {
                                client.openConnection("checkEventLogs" + ":" + username + ":" + view.getAsserNameToRemove() + " removed from the team " + teamName);
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                        }
                        else{
                            try {
                                client.openConnection("checkEventLogs" + ":" + username + ":" + view.getAsserNameToRemove()+splittedans[2]+ " removed from the team " + teamName);
                            } catch (Exception e) {
                                view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                            }
                        }
                    }
                    else if (splittedans[1].equals("Remove isn't Successful")) {
                        try {
                            client.openConnection("checkErrorLogs" + ":" + username + ":" + view.getAsserNameToRemove() + " isnt removed from team "+teamName);
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("Remove isn't Successful", Alert.AlertType.INFORMATION);
                    } else if (splittedans[1].equals("The user is not nominate by: " + view.getAsserNameToRemove() + " or the team must have at least one owner")) {
                        try {
                            client.openConnection("checkErrorLogs" + ":" + username + ":" + "The user is not nominate by " + view.getAsserNameToRemove() + " or the team must have at least one owner");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("The user is not nominate by: " + view.getAsserNameToRemove() + " or the team must have at least one owner", Alert.AlertType.INFORMATION);
                    }
                }

                if (arg.equals("changeTeamStatus")) {
                    String newStatus = null;
                    try {
                        newStatus = client.openConnection("changeTeamStatus:" + teamName);
                    } catch (Exception e) {
                        view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                    }
                    if (newStatus.equals("true")) {
                        try {
                            client.openConnection("checkEventLogs" + ":" + username + ":" + teamName + " status is active");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("Remove Successful", Alert.AlertType.INFORMATION);
                    } else if (newStatus.equals("false")) {
                        try {
                            client.openConnection("checkEventLogs" + ":" + username + ":" + teamName + " status is inactive");
                        } catch (Exception e) {
                            view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                        }
                        view.alert("Remove isn't Successful", Alert.AlertType.INFORMATION);
                    }
                    view.setTeamStatus(newStatus);
                }

            if (arg.equals("allTeamAsset")) {
                teamAssets.clear();
                String ans = null;
                try {
                    ans = client.openConnection("getTeamAssets" + ":" + username);
                } catch (Exception e) {
                    view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                }
                if (!ans.equals("")) {
                    String[] splittedans = ans.split(":");
                    for (int i = 0; i < splittedans.length; i++) {
                        teamAssets.add(splittedans[i]);
                    }
                    view.allTeamMembers.getItems().addAll(teamAssets);
                }
            }

            if (arg.equals(view.approvedReq)) {
                try {
                    String ans = client.openConnection("refApprovesToJudge" + ":" + this.username + ":" + view.approvedReq);
                } catch (Exception e) {
                    view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                }
            }

            if (arg.equals("tm get leagues")) {
                String leaguesAns = null;
                try {
                    leaguesAns = client.openConnection("showLeaguesInSeason");
                } catch (Exception e) {
                    view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                }
                String[] leagueList = leaguesAns.split(":");
                for (int i = 0; i < leagueList.length; i++) {
                    view.requestLeagueList.getItems().add(leagueList[i]);
                }
            }

            if (arg.equals("add team to league request")) {
                String ans=null;
                try {
                    ans = client.openConnection("addTeamToLeagueRequest" + ":" + view.ownerteamName + ":" + view.leagueToAdd1);
                    if (ans.equals("true")){
                        view.alert("request sent successfully", Alert.AlertType.INFORMATION);
                    }
                } catch (Exception e) {
                    view.alert("can't connect to the DB or the Server", Alert.AlertType.ERROR);
                }
            }


        }


    }
}

    //        /**
//         * ================
//         * Model Update
//         * ================
//         * */
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


