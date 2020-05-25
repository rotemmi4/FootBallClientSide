package Service;

import Domain.Model;
import Presentation.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Model model = new Model();


    @Override
    public void start(Stage primaryStage) throws Exception{


        primaryStage.setTitle("Welcome");
        FXMLLoader fxmlLoader = new FXMLLoader();

        fxmlLoader.setController(View.getInstance());
        Parent root = fxmlLoader.load(getClass().getResource("/Guest.fxml").openStream());



        Presenter presenter = new Presenter(model, View.getInstance());

        model.addObserver(presenter);
        View.getInstance().addObserver(presenter);
        View.getInstance().setTaskUp();


        View.getInstance().setStageCloseEvent(primaryStage);

        primaryStage.setScene(new Scene(root, 710, 636));
        primaryStage.show();


    }




    public static void main(String[] args) {
        launch(args);

    }
}
