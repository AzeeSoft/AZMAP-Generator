package com.azeesoft.mapdatagenerator.java.controllers.templates;
/**
 * Created by azizt on 9/4/2017.
 */

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class InfoObjectController implements Initializable {

    @FXML
    AnchorPane infoObjectRoot;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public AnchorPane getInfoObjectRoot() {
        return infoObjectRoot;
    }
}
