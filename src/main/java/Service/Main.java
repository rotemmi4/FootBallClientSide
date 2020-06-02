package Service;


import Presentation.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class Main extends Application {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader input ;
    private DataOutputStream output;

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("Welcome");
        FXMLLoader fxmlLoader = new FXMLLoader();

        fxmlLoader.setController(View.getInstance());
        Parent root = fxmlLoader.load(getClass().getResource("/Guest.fxml").openStream());

        Client model = new Client();
        Presenter presenter = new Presenter(model,View.getInstance());

        model.addObserver(presenter);
        View.getInstance().addObserver(presenter);
        View.getInstance().setTaskUp();

        View.getInstance().setStageCloseEvent(primaryStage);

        primaryStage.setScene(new Scene(root, 710, 636));
        View.getInstance().gotoSearch.setDisable(true);
        primaryStage.show();

        new Thread( () -> {
            try {
                // Create a server socket
                socket = new Socket("132.72.65.132",9876);

                // Listen for a connection request

                // Create data input and output streams
                input = new BufferedReader((new InputStreamReader((socket.getInputStream()))));
                output = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    // Receive message from the client

                    String message = input.readLine();
                    if(message.contains("ALERT")){
                        String loginUser=presenter.getUsername();
                        String[]s=message.split(",,,");
                        ArrayList<String> add=transferStringToArray(s[2]);
                        if(add.contains(loginUser)) {
                            Platform.runLater(() -> {
                                presenter.newNotification(s[1]);
                            });
                            //View.getInstance().alert(s[1], Alert.AlertType.INFORMATION);
                            System.out.println(s[1]);
                        }
                    }
                }
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }).start();

    }



    public static void main(String[] args) throws IOException {
        launch(args);
    }

    public static ArrayList<String> transferStringToArray(String str){
        ArrayList<String> arr=new ArrayList<String>();
        String[]split=str.split(",,");
        for (String s:split) {
            if(!arr.contains(s)){
                arr.add(s);
            }
        }
        return arr;
    }
}
