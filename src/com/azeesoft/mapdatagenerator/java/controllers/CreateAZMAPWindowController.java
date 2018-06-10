package com.azeesoft.mapdatagenerator.java.controllers;
/**
 * Created by azizt on 9/1/2017.
 */

import com.azeesoft.mapdatagenerator.java.managers.PlacemarkDataBuilder;
import com.azeesoft.mapdatagenerator.java.tools.StaticMethods;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateAZMAPWindowController extends BaseWindowController {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        StaticMethods.resetPreferences();
    }

    @FXML
    public void openAZMAP() {
        File file = StaticMethods.openFilePicker(getStage(), "Open JSON", "AZMAP Files", "*.azmap");
        if (file != null) {
            JSONObject jsonObject = getJSONDataFromAZMAP(file);
            if(jsonObject!=null){
                openEditor(jsonObject);
            }
        }
    }

    @FXML
    public void newAZMAP(){
        PlacemarkDataBuilder placemarkDataBuilder = new PlacemarkDataBuilder();
        JSONObject jsonObject = placemarkDataBuilder.buildJSON();
        openEditor(jsonObject);
    }

    private JSONObject getJSONDataFromAZMAP(File file){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String jsonStr = bufferedReader.readLine();
            bufferedReader.close();
            return new JSONObject(jsonStr);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void openEditor(JSONObject jsonData){
        EditorWindowController editorWindowController = (EditorWindowController) EditorWindowController.createWindow("editor_window", getStage());
        if (editorWindowController != null) {
            editorWindowController.setMainJsonData(jsonData);
            editorWindowController.showStage();
        }
    }

    @Override
    public void initStage(Stage stage, Parent root) {
        super.initStage(stage, root);
        getStage().setTitle("Create/Open AZMAP");
    }
}
