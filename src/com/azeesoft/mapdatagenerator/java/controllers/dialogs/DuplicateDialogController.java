package com.azeesoft.mapdatagenerator.java.controllers.dialogs;
/**
 * Created by azizt on 9/2/2017.
 */

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DuplicateDialogController extends BaseDialogController{

    @FXML
    private VBox duplicateNamesContainer;

    @FXML
    private VBox duplicateNamesVB;

    @FXML
    private VBox duplicateCoordinatesContainer;

    @FXML
    private VBox duplicateCoordinatesVB;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        duplicateNamesContainer.managedProperty().bind(duplicateNamesContainer.visibleProperty());
        duplicateCoordinatesContainer.managedProperty().bind(duplicateCoordinatesContainer.visibleProperty());
    }

    public void setDuplicateData(ArrayList<String> duplicateNames, ArrayList<String> duplicateCoordinates){
        duplicateNamesContainer.setVisible(!duplicateNames.isEmpty());
        duplicateCoordinatesContainer.setVisible(!duplicateCoordinates.isEmpty());

        for(String name: duplicateNames){
            duplicateNamesVB.getChildren().add(getDuplicateLabel(name));
        }
        for(String coordinate: duplicateCoordinates){
            duplicateCoordinatesVB.getChildren().add(getDuplicateLabel(coordinate));
        }
    }

    public Label getDuplicateLabel(String s){
        Label label = new Label(s);
        label.setTextFill(Paint.valueOf("#eeeeee"));
        label.setFont(new Font("Quicksand Bold", 16));
        return label;
    }

    @Override
    public void initDialogStage(Stage dialogStage, Parent root) {
        super.initDialogStage(dialogStage, root);
        getDialogStage().setTitle("Error while importing KML");
    }
}
