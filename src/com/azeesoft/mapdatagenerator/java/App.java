package com.azeesoft.mapdatagenerator.java;

import com.azeesoft.mapdatagenerator.java.controllers.BaseWindowController;
import com.azeesoft.mapdatagenerator.java.controllers.CreateAZMAPWindowController;
import com.azeesoft.mapdatagenerator.java.tools.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        CreateAZMAPWindowController createAZMAPWindowController = (CreateAZMAPWindowController) CreateAZMAPWindowController.createWindow("create_azmap_window", primaryStage);
        if (createAZMAPWindowController != null) {
            createAZMAPWindowController.showStage();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
