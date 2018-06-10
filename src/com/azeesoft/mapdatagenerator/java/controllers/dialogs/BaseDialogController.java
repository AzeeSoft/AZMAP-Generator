package com.azeesoft.mapdatagenerator.java.controllers.dialogs;
/**
 * Created by azizt on 9/2/2017.
 */

import com.azeesoft.mapdatagenerator.java.controllers.BaseWindowController;
import com.azeesoft.mapdatagenerator.java.tools.Constants;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BaseDialogController implements Initializable {

    private Stage dialogStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initDialogStage(Stage dialogStage, Parent root){
        this.dialogStage = dialogStage;
        this.dialogStage.setScene(new Scene(root));
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void showDialog(){
        dialogStage.show();
    }

    public static BaseDialogController initDialog(String layoutName){
        return initDialog(layoutName, null);
    }

    public static BaseDialogController initDialog(String layoutName, Stage owner){
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        if(owner!=null){
            stage.initOwner(owner);
        }
        layoutName = layoutName.replaceAll(".fxml", "");
        FXMLLoader fxmlLoader = new FXMLLoader(BaseWindowController.class.getResource(Constants.App.Paths.Layouts.DIALOGS + layoutName +".fxml"));
        try {
            Parent root = fxmlLoader.load();
            BaseDialogController baseDialogController = fxmlLoader.getController();
            baseDialogController.initDialogStage(stage, root);
            return baseDialogController;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
