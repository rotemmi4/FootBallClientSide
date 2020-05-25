package Domain;

//import Data.SystemDB.UserDaoMdb;
//import Domain.AlertSystem.AlertPop;
//import Domain.AlertSystem.AlertSystem;
import Domain.AssociationManagement.League;
import Domain.AssociationManagement.Match;
import Domain.ClubManagement.TeamInfo;
import Domain.User.*;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;

public class Model extends Observable {

    //private UserDaoMdb db;
    //private AlertSystem alertSystem;
    private TeamMember tm;
    private AssociationUser au;
    private Referee ref;
    private SystemManager sys;
    private Fan fan;

    //private ArrayList<AlertPop> alerts;//*****************************

    private int currentSeasonYear;

    public Model() {
        //db = UserDaoMdb.getInstance();
        //alertSystem= AlertSystem.getInstance();

        //this.alerts=new ArrayList<AlertPop>();//*******************


    }

    /**
     * =============================================================================================
     * =======================================  Register ===========================================
     * =============================================================================================
     **/

    public boolean addUser(String firstName, String lastName, String userName, String password, String occupation, String birthday, String email, String verificationCode, String role) {
        if (!db.isUserExist(userName)) {
            SystemManager systemManager = (SystemManager) db.getUser("God");
            systemManager.addNewUserToDB(firstName, lastName, userName, password, occupation, birthday, email, verificationCode, role);
            setChanged();
            notifyObservers("User added successfully");
            return true;
        } else {
            setChanged();
            notifyObservers("user already exist");
            return false;
        }
    }

    public boolean loginUser(String username, String password) {
        tm = null;
        au = null;
        ref = null;
        sys = null;
        fan = null;
        if (db.isUserExist(username)) {
            Fan user = db.getUser(username);
            //find instance of user
            if (user.getPassword().equals(password)) {
                if (user.getOccupation().equals("TeamMember")) {
                    tm = (TeamMember) user;
                    setChanged();
                    notifyObservers(tm);
                    return true;
                } else if (user.getOccupation().equals("Association")) {
                    currentSeasonYear = db.getTheCurrentSeason();
                    au = (AssociationUser) user;
                    setChanged();
                    notifyObservers(au);
                    return true;
                } else if (user.getOccupation().equals("Referee")) {
                    ref = (Referee) user;
                    setChanged();
                    notifyObservers(ref);
                    return true;
                } else if (user.getOccupation().equals("SystemManager")) {
                    sys = (SystemManager) user;
                    setChanged();
                    notifyObservers(sys);
                    return true;
                } else {
                    fan = user;
                    setChanged();
                    notifyObservers(user);
                    return true;
                }
            } else { //wrong pass
                setChanged();
                notifyObservers("login failed, wrong password");
                return false;
            }
        } else {
            setChanged();
            notifyObservers("login failed, user doesn't exist");
            return false;
        }
    }

    public boolean checkIfSeasonExist(int year) {
        if (db.isSeasonExist(year)) {
            setChanged();
            notifyObservers("this season already exist, try again next year");
            return true;
        } else {
            return false;
        }
    }


    public void addSeason(int year, double sumOfIncome) {//need to check
        if (au.createSeasonAndBudget(year, sumOfIncome)) {
            UserDaoMdb.getInstance().addSeason(year, sumOfIncome);
            setChanged();
            notifyObservers("season was added to system!");
        }
    }


    public boolean addTeam(String teamName, int esYear, boolean isActive, String city,String owner, String court) {
        if (!db.isTeamExist(teamName) && !db.checkOwnerNotHaveTeam(owner)) {
            //TeamInfo newTeam = (TeamInfo) db.getTeam(teamName);
            db.addTeam(teamName,esYear,isActive, city,owner,court);
            setChanged();
            notifyObservers("team was added to system!");
            return true;
        } else {
            setChanged();
            notifyObservers("team already exist");
            return false;
        }
    }




    public String ownerTeamName(String userName) {
        if (tm.getTeam()!= null ) {
            return tm.getTeam().getTeamName();
        }
        else return "";
    }

    public boolean ownerTeamStatus(String userName) {
        if (tm.getTeam()!= null ) {
            return tm.getTeam().isTeamActiveStatus();
        }
        return false;
    }

    public ArrayList<String> chooseCourt(String city){
        if(city!=null){
            ArrayList<String> courts = new ArrayList<>();
            courts = db.getCourtByCity(city);
            if(courts!=null){
                return courts;
            }
        }
        return null;
    }



    public int getCurrentSeason(){
        return db.getTheCurrentSeason();
    }


    public boolean isLeagueExist(String leagueName) {
        League tmp = db.getLeagueByName(leagueName);
        if(tmp==null){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean changeTeamStatus(){
        if(tm.getTeam().isTeamActiveStatus()){
            db.updateUserDetails(tm.getUserName(), false,"teams","TeamActiveStatus");
            tm.CloseTeam();
            //tm.getTeam().setTeamActiveStatus(false);
            return false;
        }
        else {
            db.updateUserDetails(tm.getUserName(), true,"teams","TeamActiveStatus");
            //tm.getTeam().setTeamActiveStatus(true);
            tm.OpenTeam();
            return true;
        }
    }

    public boolean isPlayer(){return tm.isPlayer();}
    public boolean isCoach(){return tm.isCoach();}
    public boolean isOwner(){return tm.isOwner();}
    public boolean isTeamManager(){return tm.isTeamManager();}

    //start check

    public boolean addOwnerToTeam(String newOwner) {
        if(db.isUserExist(newOwner)) {
            TeamMember userExists = (TeamMember) db.getUser(newOwner);
            if (tm.AddOwner(userExists)) {
                db.updateUserDetails(newOwner, tm.getTeam().getTeamName(), "owners", "CurrentTeam");
                db.updateUserDetails(newOwner, tm.getUserName(), "owners", "EmployedBy");
                return true;
            }
        }
        return false;
    }
    public boolean addCoachToTeam(String newCoach, String role){
        if(db.isUserExist(newCoach)) {
            TeamMember userExists = (TeamMember) db.getUser(newCoach);
            if (tm.AddCoach(userExists, "", role)) {
                db.updateUserDetails(newCoach, tm.getTeam().getTeamName(),"coaches","CurrentTeam");
                db.updateUserDetails( newCoach,tm.getUserName() ,"coaches","EmployedBy");
                return true;
            }
        }
        return false;
    }

    public boolean addPlayerToTeam(String newPlayer,String role){
        if(db.isUserExist(newPlayer)) {
            TeamMember player = (TeamMember) db.getUser(newPlayer);
            if (tm.AddPlayer(player, role)) {
                db.updateUserDetails(newPlayer, tm.getTeam().getTeamName(),"players","CurrentTeam");
                db.updateUserDetails( newPlayer,tm.getUserName() ,"players","EmployedBy");
                return true;
            }
        }
        return false;
    }
    public boolean addManagerToTeam(String user, boolean ownerP, boolean playerP, boolean coachP, boolean teamMP){
        if(db.isUserExist(user)) {
            TeamMember teamManager = (TeamMember) db.getInstance().getUser(user);
            if (tm.AddTeamManager(teamManager, ownerP, playerP, coachP, teamMP)) {
                db.updateUserDetails(user, tm.getTeam().getTeamName(),"teamManagers","CurrentTeam");
                db.updateUserDetails(user, tm.getUserName(),"teamManagers","EmployedBy");

                db.updateUserDetails(user, coachP,"teamManagers","CoachPermission");
                db.updateUserDetails(user, playerP,"teamManagers","PlayerPermission");
                db.updateUserDetails(user, teamMP,"teamManagers","TeamManagerPermission");
                db.updateUserDetails(user, ownerP,"teamManagers","OwnerPermission");
                return true;
            }
        }
        return false;
    }


    public void addLeagueToDB(ArrayList<String> newLeagueDetails) {
        int year = Integer.parseInt(newLeagueDetails.get(0));
        String leagueName = newLeagueDetails.get(1);
        String startDate = newLeagueDetails.get(8);
        int w = Integer.parseInt(newLeagueDetails.get(3));
        int l = Integer.parseInt(newLeagueDetails.get(5));
        int d = Integer.parseInt(newLeagueDetails.get(4));
        boolean goalD = false;
        boolean hTh = false;
        if(newLeagueDetails.get(6).equals("Goal Difference")){
            goalD = true;
        }else{

            hTh = true;
        }
        int rounds = Integer.parseInt(newLeagueDetails.get(7).substring(0,1));
        int numTeams = Integer.parseInt(newLeagueDetails.get(2));

        if(au.addNewLeagueToSeason(year,leagueName,startDate,w,l,d,goalD,hTh,rounds,numTeams)){
            UserDaoMdb.getInstance().addLeagueAndPolicyToSeason(year, leagueName, startDate,
                    w, l, d, goalD, hTh,
                    rounds, numTeams);
                setChanged();
            notifyObservers("The League "+ leagueName +" was added!");
        }
    }

    public ArrayList<Referee> getAllReferees() {
        return db.getAllReferees();
    }

    public ArrayList<TeamMember> getCoachs() { return db.getAvailableCoaches(); }

    public ArrayList<TeamMember> getPlayers() { return db.getAvailablePlayers(); }

    public ArrayList<TeamMember> getOwners() { return db.getAvailableOwners(); }

    public ArrayList<TeamMember> getManagers() { return db.getAvailableManagers(); }


    public void setRefToSeason(String refUsernameToNominate) {
        Referee ref = (Referee)UserDaoMdb.getInstance().getUser(refUsernameToNominate);
        int season = db.getTheCurrentSeason();
        if(au.NominateReferee(ref)){
            if(ref.getRefereeRole().equals("Main Referee")){
                db.updateUserDetails(refUsernameToNominate,season,"referees","SeasonYear");
            }
            else if(ref.getRefereeRole().equals("Side Referee")){
                UserDaoMdb.getInstance().updateUserDetails(refUsernameToNominate,season,"referees","SeasonYear");

            }


        }
    }

    public ArrayList<League> getAllLeagues() {
        return db.getAllLeaguesInCurrentSeason();
    }

    public void createScheduleToLeague(String leagueNameToSchedule) {
        ArrayList<League> leaguesInDB =  AssociationUser.getBm().get(currentSeasonYear).getBudget().getSeason().getAllLeagues();
        League league = null;
        for (int i = 0; i < leaguesInDB.size(); i++) {
            if(leaguesInDB.get(i).getLeagueName().equals(leagueNameToSchedule)){
                league = leaguesInDB.get(i);
            }
        }
        if(league != null){
            Vector<TeamInfo> teamsInLeague = league.getTeams();
            if( teamsInLeague.size() % 2 == 0 ){
                if(league.getMainReferees().size() == (league.getTeams().size()/2)){
                    if(league.getSideReferees().size() == (league.getTeams().size()/2)*3){
                        au.updateGameScheduling(league);
                        ArrayList<ArrayList<Match>> leagueGames = league.getFixtures();
                        for (int i = 0; i < leagueGames.size(); i++) {
                            for (int j = 0; j < leagueGames.get(0).size(); j++) {
                                db.addGameDetails(currentSeasonYear,league.getLeagueName(),leagueGames.get(i).get(j).getMatchDate().toString(),
                                        leagueGames.get(i).get(j).getHomeTeam().getTeamName(), leagueGames.get(i).get(j).getAwayTeam().getTeamName(),
                                        leagueGames.get(i).get(j).getHomeTeam().getTeamCourt().getCourtName(),
                                        leagueGames.get(i).get(j).getMainRef().getUserName(), leagueGames.get(i).get(j).getSideRefs().get(0).getUserName(),
                                        leagueGames.get(i).get(j).getSideRefs().get(1).getUserName(),leagueGames.get(i).get(j).getSideRefs().get(2).getUserName());
                            }
                        }
                    }else{
                        setChanged();
                        notifyObservers("add more side referees - total main referees should be equals to ( (number of teams in league/2) * 3)");
                    }
                }
                else{
                    setChanged();
                    notifyObservers("add more main referees - total main referees should be equals to (number of teams in league/2)");
                }
            }
            else{
                setChanged();
                notifyObservers("odd number of teams in league");
            }


        }

    }

    public ArrayList<String> getTeamAssets() {
        ArrayList<String> allTm= new ArrayList<>();
        for (int i = 0; i < tm.getTeam().getTeamOwners().size(); i++) {
            String name = ((TeamMember)tm.getTeam().getTeamOwners().get(i)).getUserName() + ", " +
                    ((TeamMember)tm.getTeam().getTeamOwners().get(i)).getFirstName() + ", " +
                    ((TeamMember)tm.getTeam().getTeamOwners().get(i)).getLastName() + " - Owner" ;
            allTm.add(name);
        }
        for (int i = 0; i < tm.getTeam().getTeamManagers().size(); i++) {
            String name = ((TeamMember)tm.getTeam().getTeamManagers().get(i)).getUserName() + ", " +
                    ((TeamMember)tm.getTeam().getTeamManagers().get(i)).getFirstName() + ", " +
                    ((TeamMember)tm.getTeam().getTeamManagers().get(i)).getLastName() + " - Team Manager";
            allTm.add(name);
        }

        for (int i = 0; i < tm.getTeam().getTeamCoaches().size(); i++) {
            String name = ((TeamMember)tm.getTeam().getTeamCoaches().get(i)).getUserName() + ", " +
                    ((TeamMember)tm.getTeam().getTeamCoaches().get(i)).getFirstName() + ", " +
                    ((TeamMember)tm.getTeam().getTeamCoaches().get(i)).getLastName() + " - " +
                    ((TeamMember)tm.getTeam().getTeamCoaches().get(i)).getTeamRole();
            allTm.add(name);
        }

        for (int i = 0; i < tm.getTeam().getTeamPlayers().size(); i++) {
            String name = ((TeamMember)tm.getTeam().getTeamPlayers().get(i)).getUserName() + ", " +
                    ((TeamMember)tm.getTeam().getTeamPlayers().get(i)).getFirstName() + ", " +
                    ((TeamMember)tm.getTeam().getTeamPlayers().get(i)).getLastName() + " - Player: " +
                    ((TeamMember)tm.getTeam().getTeamPlayers().get(i)).getTeamRole();
            allTm.add(name);
        }

        allTm.add(tm.getTeam().getTeamHomeCourt().getCourtName()+ " - Home Court" );
        return allTm;
    }

    public boolean removeAsset(String name){

        for (int i = 0; i < tm.getTeam().getTeamCoaches().size(); i++) {
            if(((TeamMember)tm.getTeam().getTeamCoaches().get(i)).getUserName().equals(name)){
                TeamMember coach = (TeamMember)db.getUser(name);
                if (tm.RemoveCoach(coach))
                    db.updateUserDetails(name, "","coaches","CurrentTeam");
                    db.updateUserDetails( name,"" ,"coaches","EmployedBy");
            }
        }
        for (int i = 0; i < tm.getTeam().getTeamOwners().size(); i++) {
            if(((TeamMember)tm.getTeam().getTeamOwners().get(i)).getUserName().equals(name)){
                TeamMember owner = (TeamMember)db.getUser(name);
                if(tm.RemoveOwner(owner)){
                    db.updateUserDetails(name, "","owners","CurrentTeam");
                    db.updateUserDetails(name, "","owners","EmployedBy");
                }
            }
        }
        for (int i = 0; i < tm.getTeam().getTeamPlayers().size(); i++) {
            if (((TeamMember) tm.getTeam().getTeamPlayers().get(i)).getUserName().equals(name)) {
                TeamMember player = (TeamMember) db.getUser(name);
                if (tm.RemovePlayer(player)) {
                    db.updateUserDetails(name, "","players","CurrentTeam");
                    db.updateUserDetails(name,"" ,"players","EmployedBy");
                }
            }
        }
        for (int i = 0; i < tm.getTeam().getTeamManagers().size(); i++) {
            if(((TeamMember)tm.getTeam().getTeamManagers().get(i)).getUserName().equals(name)){
                TeamMember teamManager = (TeamMember) db.getUser(name);
                if (tm.RemoveTeamManager(teamManager)) {
                    db.updateUserDetails(name, "","owners","CurrentTeam");
                    db.updateUserDetails(name, "","owners","EmployedBy");
                    db.updateUserDetails(name, false,"teamManagers","CoachPermission");
                    db.updateUserDetails(name, false,"teamManagers","PlayerPermission");
                    db.updateUserDetails(name, false,"teamManagers","TeamManagerPermission");
                    db.updateUserDetails(name, false,"teamManagers","OwnerPermission");
                }
            }
        }
        //coart?

        return false;
    }

//    public void addRefToLeague2(String selectedLeague, String selectedRef) {
//        if(au != null){
//            ArrayList<League> leagues = AssociationUser.getBm().get(currentSeasonYear).getBudget().getSeason().getAllLeagues();
//            for (int i = 0; i < leagues.size(); i++) {
//                if(selectedLeague.equals(leagues.get(i).getLeagueName())){
//                    int lastChar = selectedRef.indexOf("-");
//                    String refUserName = selectedRef.substring(0,lastChar).trim();
//                    Referee ref = (Referee) db.getUser(refUserName);
//                    League leg = leagues.get(i);
//                    if(au.addRefereeToLeague(leg,ref)) {
//                        //leagues.get(i).addMainRefereeToLeague(ref);
//                        db.updateUserDetails(refUserName, leagues.get(i).getLeagueName(), "referees", "LeagueName");
//                    }
//                }
//            }
//        }
//    }

//    public void addRefToLeague(String selectedLeague, String selectedRef) {
//        if(au != null){
//            ArrayList<League> leagues = AssociationUser.getBm().get(currentSeasonYear).getBudget().getSeason().getAllLeagues();
//            for (int i = 0; i < leagues.size(); i++) {
//                if(selectedLeague.equals(leagues.get(i).getLeagueName())){
//                    int lastChar = selectedRef.indexOf("-");
//                    String refUserName = selectedRef.substring(0,lastChar).trim();
//                    Referee ref = (Referee) db.getUser(refUserName);
//                    leagues.get(i).addMainRefereeToLeague(ref);
//                    db.updateUserDetails(refUserName,leagues.get(i).getLeagueName(),"referees","LeagueName");
//                }
//            }
//        }
//    }

//    public void checkTeamRegistration(String selectedReq) {//"Register Team - TeamName: Liverpool ,ToLeague: Premier League"
//        String[] makaf = selectedReq.split("-");
//        makaf[1].trim();
//        String[] psik = makaf[1].split(",");
//        psik[0].trim();
//        String[] dots1 = psik[0].split(":");
//        String[] dots2 = psik[1].split(":");
//        String teamName = dots1[1].trim();
//        String leagueName = dots2[1].trim();
//        TeamInfo t = db.getTeam(teamName);
//        League l = db.getLeagueByName(leagueName);
//        if(t!=null && l!=null){
//            if(l.addTeam(t)){
//                db.addTeamToLeague(t.getTeamName(),currentSeasonYear,l.getLeagueName());
//                setChanged();
//                notifyObservers("team was added successfully");
//            }
//            else{
//                setChanged();
//                notifyObservers("team isnt complete");
//            }
//        }
//
//
//    }

    public void changePointsForLeague(String leagueName, int pointsForWin, int pointsForDraw, int pointsForLoss, boolean goalDiffTieBreaker, boolean directResultsTieBreaker){
//      au.updateRankAndPointsPolicy(leagueName, pointsForWin,pointsForDraw,pointsForLoss,goalDiffTieBreaker,directResultsTieBreaker);
        League league = db.getLeagueByName(leagueName);
        au.updateRankAndPointsPolicy(league, pointsForWin,pointsForDraw,pointsForLoss,goalDiffTieBreaker,directResultsTieBreaker);
        db.updateUserDetails(leagueName, pointsForWin,"seasonsAndLeaguesPolicy","PointsPerWin");
        db.updateUserDetails(leagueName, pointsForDraw,"seasonsAndLeaguesPolicy","PointsPerLoss");
        db.updateUserDetails(leagueName, pointsForLoss,"seasonsAndLeaguesPolicy","PointsPerDraw");
        db.updateUserDetails(leagueName, goalDiffTieBreaker,"seasonsAndLeaguesPolicy","DifferenceGoals");
        db.updateUserDetails(leagueName, directResultsTieBreaker,"seasonsAndLeaguesPolicy","StraightMeets");
    }

    public ArrayList<String> showLeagueList(){
        return db.getAllLeaguesToChangePolicy();
    }

    public void changeRoundsForLeague(String leagueName, int rounds){
        League league = db.getLeagueByName(leagueName);
        au.updateGameScheduling(league, rounds);
        db.updateUserDetails(leagueName, rounds,"seasonsAndLeaguesPolicy","NumOfRounds");
    }


    public ArrayList<Referee> showAllRefs() {
        ArrayList<Referee> all = db.getAllRefereesWithSeasonWithoutLeague();
        return all;
    }

    public ArrayList<String> showLeaguesInSeason() {
        ArrayList<String> leagues = new ArrayList<>();
        if(au != null){
            ArrayList<League> l = AssociationUser.getBm().get(currentSeasonYear).getBudget().getSeason().getAllLeagues();
            if(l.size()>0) {
                for (int i = 0; i < l.size(); i++) {
                    leagues.add(l.get(i).getLeagueName());
                }
            }
        }
        return leagues;
    }

    public void addRefToLeague(String selectedLeague, String selectedRef) {
        if(au != null){
            ArrayList<League> leagues = AssociationUser.getBm().get(currentSeasonYear).getBudget().getSeason().getAllLeagues();
            for (int i = 0; i < leagues.size(); i++) {
                if(selectedLeague.equals(leagues.get(i).getLeagueName())){
                    int lastChar = selectedRef.indexOf("-");
                    String refUserName = selectedRef.substring(0,lastChar).trim();
                    Referee ref = (Referee) db.getUser(refUserName);
                    leagues.get(i).addMainRefereeToLeague(ref);
                    db.updateUserDetails(refUserName,leagues.get(i).getLeagueName(),"referees","LeagueName");
                }
            }
        }
    }

    public ArrayList<String> getTeamReqs() {
        return db.getAllTeamReqs(au.getUserName());
    }


    public void checkTeamRegistration(String selectedReq) {//"Register Team - TeamName: Liverpool ,ToLeague: Premier League"

        String[] makaf = selectedReq.split("-");
        makaf[1].trim();
        String[] psik = makaf[1].split(",");
        psik[0].trim();
        String[] dots1 = psik[0].split(":");
        String[] dots2 = psik[1].split(":");
        String teamName = dots1[1].trim();
        String leagueName = dots2[1].trim();
        TeamInfo t = db.getTeam(teamName);
        League l = db.getLeagueByName(leagueName);
        if(t!=null && l!=null){
            if(l.addTeam(t)){
                db.addTeamToLeague(t.getTeamName(),currentSeasonYear,l.getLeagueName());
                setChanged();
                notifyObservers("team was added successfully");
            }
            else{
                setChanged();
                notifyObservers("team isnt complete");
            }
        }


    }

    /**
     *=================================================================
     * ============= ALERT FUNCTION ===================================
     * ================================================================
     */

    private void addAlertToDB(String content, String type, ArrayList<String> users){
        for (String user:users) {
            db.addUserAlert(user,type,content,false);
        }
    }

    public void Subscribe(String username){
        db.updateUserDetails(username,true,"users","AssignToAlerts");
    }

    public void Unsubscribe(String username){
        db.updateUserDetails(username,false,"users","AssignToAlerts");
    }

    public void getAlertsFromBD(String username){
        this.alerts.addAll(db.getAllUserAlerts(username));
        setChanged();
        notifyObservers("new alerts");
    }

    public ArrayList<AlertPop> getAlert(){
        return this.alerts;
    }

    public void addAlertTODB(ArrayList<String> users, String type,String content){
        for (String user:users) {
            db.addUserAlert(user,type,content,false);
        }
    }

}
