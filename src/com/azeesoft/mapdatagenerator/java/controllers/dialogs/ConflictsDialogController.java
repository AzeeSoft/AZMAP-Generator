package com.azeesoft.mapdatagenerator.java.controllers.dialogs;
/**
 * Created by azizt on 9/2/2017.
 */

import com.azeesoft.mapdatagenerator.java.managers.ImportConflictManager;
import com.azeesoft.mapdatagenerator.java.others.AZMAPFormat;
import com.azeesoft.mapdatagenerator.java.tools.Constants;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class ConflictsDialogController extends BaseDialogController {

    private ArrayList<ImportConflictManager.Conflict> conflicts;
    private ArrayList<Integer> returningIndices;
    private JSONObject existingJSONData;
    private JSONObject importingJSONData;

    @FXML
    private VBox newPlacemarkContainer;

    @FXML
    private GridPane newPlacemarkGP;

    @FXML
    private VBox nameConflictsContainer;

    @FXML
    private GridPane nameConflictsGP;

    @FXML
    private VBox coordinatesConflictsContainer;

    @FXML
    private GridPane coordinateConflictsGP;

    @FXML
    private VBox missingConflictsContainer;

    @FXML
    private GridPane missingConflictsGP;

    private OnConflictsResolvedListener onConflictsResolvedListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameConflictsContainer.managedProperty().bind(nameConflictsContainer.visibleProperty());
        coordinatesConflictsContainer.managedProperty().bind(coordinatesConflictsContainer.visibleProperty());
        missingConflictsContainer.managedProperty().bind(missingConflictsContainer.visibleProperty());
        newPlacemarkContainer.managedProperty().bind(newPlacemarkContainer.visibleProperty());
    }

    public void initConflicts(JSONObject existingJSONData, JSONObject importingJSONData, ArrayList<ImportConflictManager.Conflict> conflicts, ArrayList<Integer> returningIndices) {
        this.existingJSONData = existingJSONData;
        this.importingJSONData = importingJSONData;
        this.conflicts = conflicts;
        this.returningIndices = returningIndices;
        refreshConflicts();
    }

    private void refreshConflicts() {

        for (ImportConflictManager.Conflict conflict : conflicts) {

            Label existingData = getCustomLabel(
                    conflict.existingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME) + "\n" +
                            "(" + conflict.existingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LATITUDE) + ", " +
                            conflict.existingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LONGITUDE) + ")");

            Label importingData = getCustomLabel(
                    conflict.importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME) + "\n" +
                            "(" + conflict.importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LATITUDE) + ", " +
                            conflict.importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LONGITUDE) + ")");


            Label willBeUpdated = getCustomLabel("Will be Updated");
            willBeUpdated.setTextFill(Paint.valueOf("#1db254"));
            switch (conflict.conflictType) {
                case NAME_CONFLICT:
                    willBeUpdated.setText("Name will be updated");
                    nameConflictsGP.getRowConstraints().add(new RowConstraints());
                    nameConflictsGP.addRow(nameConflictsGP.getRowConstraints().size(), existingData, importingData, willBeUpdated);
                    break;
                case COORDINATE_CONFLICT:
                    willBeUpdated.setText("Coordinates will be updated");
                    coordinateConflictsGP.getRowConstraints().add(new RowConstraints());
                    coordinateConflictsGP.addRow(coordinateConflictsGP.getRowConstraints().size(), existingData, importingData, willBeUpdated);
                    break;
                case MISSING_CONFLICT:
                    JFXCheckBox checkBox = new JFXCheckBox("Remove");
                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            conflict.remove = newValue;
                        }
                    });
                    checkBox.setTextFill(Paint.valueOf("#eeeeee"));
                    checkBox.setPadding(new Insets(5));
                    missingConflictsGP.getRowConstraints().add(new RowConstraints());
                    missingConflictsGP.addRow(missingConflictsGP.getRowConstraints().size(), existingData, checkBox);
                    break;
            }
        }

        JSONArray importingPlacemarksArray = importingJSONData.optJSONArray(AZMAPFormat.Keys.PLACEMARKS);
        for (int i = 0; i < importingPlacemarksArray.length(); i++) {
            if(!returningIndices.contains(i)){
                try {
                    JSONObject importingPlacemarkObject = importingPlacemarksArray.getJSONObject(i);
                    Label importingData = getCustomLabel(
                            importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME) + "\n" +
                                    "(" + importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LATITUDE) + ", " +
                                    importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LONGITUDE) + ")");
                    Label willBeUpdated = getCustomLabel("Will be Updated");
                    willBeUpdated.setTextFill(Paint.valueOf("#1db254"));

                    willBeUpdated.setText("Placemark will be added");
                    newPlacemarkGP.getRowConstraints().add(new RowConstraints());
                    newPlacemarkGP.addRow(newPlacemarkGP.getRowConstraints().size(), importingData, willBeUpdated);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        nameConflictsContainer.setVisible(nameConflictsGP.getRowConstraints().size() > 1);
        coordinatesConflictsContainer.setVisible(coordinateConflictsGP.getRowConstraints().size() > 1);
        missingConflictsContainer.setVisible(missingConflictsGP.getRowConstraints().size() > 1);
        newPlacemarkContainer.setVisible(newPlacemarkGP.getRowConstraints().size() > 1);
    }

    private Label getCustomLabel(String s) {
        Label label = new Label(s);
        label.setTextFill(Paint.valueOf("#eeeeee"));
        label.setFont(new Font("Quicksand Bold", 16));
        label.setPadding(new Insets(5));
        return label;
    }

    private void setMissingCheckBoxes(boolean check) {
        missingConflictsGP.getChildren().iterator().forEachRemaining(new Consumer<Node>() {
            @Override
            public void accept(Node node) {
                if (node instanceof JFXCheckBox) {
                    JFXCheckBox checkBox = (JFXCheckBox) node;
                    checkBox.setSelected(check);
                }
            }
        });
    }

    public void setOnConflictsResolvedListener(OnConflictsResolvedListener onConflictsResolvedListener) {
        this.onConflictsResolvedListener = onConflictsResolvedListener;
    }

    public int getPlacemarkIndex(JSONArray placemarkArray, String name) {
        for (int i = 0; i < placemarkArray.length(); i++) {
            try {
                JSONObject existingPlacemarkObject = placemarkArray.getJSONObject(i);
                String existingName = existingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME, "");

                if (existingName.equals(name)) {
                    return i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public int getPlacemarkIndex(JSONArray placemarkArray, String latitude, String longitude) {
        for (int i = 0; i < placemarkArray.length(); i++) {
            try {
                JSONObject existingPlacemarkObject = placemarkArray.getJSONObject(i);
                String existingLatitude = existingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LATITUDE, "");
                String existingLongitude = existingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LONGITUDE, "");

                if (existingLatitude.equals(latitude) && existingLongitude.equals(longitude)) {
                    return i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @FXML
    void ignoreAllMissing(ActionEvent event) {
        setMissingCheckBoxes(false);
    }

    @FXML
    void removeAllMissing(ActionEvent event) {
        setMissingCheckBoxes(true);
    }

    @FXML
    void resolve() {
        JSONArray placemarksArray = existingJSONData.optJSONArray(AZMAPFormat.Keys.PLACEMARKS);
        for (ImportConflictManager.Conflict conflict : conflicts) {
            switch (conflict.conflictType) {
                case NAME_CONFLICT:
                    int nIndex = getPlacemarkIndex(placemarksArray, conflict.importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LATITUDE), conflict.importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LONGITUDE));
                    try {
                        conflict.existingPlacemarkObject.put(AZMAPFormat.Placemark.Keys.NAME, conflict.importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME));
                        placemarksArray.put(nIndex, conflict.existingPlacemarkObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case COORDINATE_CONFLICT:
                    int cIndex = getPlacemarkIndex(placemarksArray, conflict.importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME));
                    try {
                        conflict.existingPlacemarkObject.put(AZMAPFormat.Placemark.Keys.LATITUDE, conflict.importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LATITUDE));
                        conflict.existingPlacemarkObject.put(AZMAPFormat.Placemark.Keys.LONGITUDE, conflict.importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LONGITUDE));
                        placemarksArray.put(cIndex, conflict.existingPlacemarkObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MISSING_CONFLICT:
                    if (conflict.remove) {
                        int mIndex = getPlacemarkIndex(placemarksArray, conflict.existingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME));
                        placemarksArray.remove(mIndex);
                    }
                    break;
            }
        }

        try {
            existingJSONData.put(AZMAPFormat.Keys.PLACEMARKS, placemarksArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (onConflictsResolvedListener != null) {
            onConflictsResolvedListener.onConflictsResolved(existingJSONData);
            cancel();
        }
    }

    @FXML
    void cancel() {
        getDialogStage().close();
    }

    public interface OnConflictsResolvedListener {
        void onConflictsResolved(JSONObject resolvedJSONData);
    }
}
