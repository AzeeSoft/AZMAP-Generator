package com.azeesoft.mapdatagenerator.java.controllers.templates;
/**
 * Created by azizt on 9/2/2017.
 */

import com.azeesoft.mapdatagenerator.java.managers.PlacemarkDataBuilder;
import com.azeesoft.mapdatagenerator.java.others.AZMAPFormat;
import com.azeesoft.mapdatagenerator.java.tools.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class PlacemarkItemController implements Initializable {

    private int placemarkIndex = -1;
    private JSONObject placemarkObject;

    private OnEditPlacemarkClickListener onEditPlacemarkClickListener;

    @FXML
    private Label placemarkLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setPlacemarkObject(int placemarkIndex, JSONObject placemarkObject, OnEditPlacemarkClickListener onEditPlacemarkClickListener) {
        this.placemarkIndex= placemarkIndex;
        this.placemarkObject = placemarkObject;
        setOnEditPlacemarkClickListener(onEditPlacemarkClickListener);
        refreshItem();
    }

    public JSONObject getPlacemarkObject() {
        return placemarkObject;
    }

    public void setOnEditPlacemarkClickListener(OnEditPlacemarkClickListener onEditPlacemarkClickListener) {
        this.onEditPlacemarkClickListener = onEditPlacemarkClickListener;
    }

    public void refreshItem(){
        String name = placemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME, "");
        placemarkLabel.setText(name);
    }

    @FXML
    void editPlacemark(ActionEvent event) {
        if(onEditPlacemarkClickListener!=null){
            onEditPlacemarkClickListener.onEditPlacemarkClicked(placemarkIndex, placemarkObject);
        }
    }

    public interface OnEditPlacemarkClickListener{
        void onEditPlacemarkClicked(int index, JSONObject placemarkObject);
    }
}
