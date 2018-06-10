package com.azeesoft.mapdatagenerator.java.controllers;
/**
 * Created by azizt on 9/1/2017.
 */

import com.azeesoft.mapdatagenerator.java.tools.Constants;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BaseWindowController implements Initializable {

    public static class WindowProperties {
        public static String CONTROLLER = "controller";
    }

    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initStage(Stage stage, Parent root) {
        this.stage = stage;
        this.stage.setScene(new Scene(root));
        this.stage.getProperties().put(WindowProperties.CONTROLLER, this);
        this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(onClosing()){
                   event.consume();
                }
            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    public void showStage(){
        stage.show();
    }

    public static BaseWindowController createWindow(String layoutName){
        return createWindow(layoutName, new Stage());
    }

    public static BaseWindowController createWindow(String layoutName, Stage stage){
        layoutName = layoutName.replaceAll(".fxml", "");
        FXMLLoader fxmlLoader = new FXMLLoader(BaseWindowController.class.getResource(Constants.App.Paths.Layouts.BASE + layoutName +".fxml"));
        try {
            Parent root = fxmlLoader.load();
            BaseWindowController baseWindowController = fxmlLoader.getController();
            baseWindowController.initStage(stage, root);
            return baseWindowController;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean onClosing(){
        return false;
    }

}
