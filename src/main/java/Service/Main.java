package Service;


import Presentation.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Main extends Application {
    private ServerSocket serverSocket;
    private Socket socket;
    private DataInputStream input ;
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
                input = new DataInputStream( socket.getInputStream() );
                output = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    // Receive message from the client
                    String message = input.readUTF();
                    if(message.equals("test Alert")){
                        System.out.println("get alert!!!!!!!!!!");
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
}
