package com.azeesoft.mapdatagenerator.java.controllers.dialogs;
/**
 * Created by azizt on 9/4/2017.
 */

import com.azeesoft.mapdatagenerator.java.controllers.templates.PlacemarkImageItemController;
import com.azeesoft.mapdatagenerator.java.others.AZMAPFormat;
import com.azeesoft.mapdatagenerator.java.tools.Constants;
import com.azeesoft.mapdatagenerator.java.tools.StaticMethods;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ResourceBundle;

public class EditPlacemarkDialogController extends BaseDialogController {

    private JSONObject placemarkObject;

    private OnPlacemarkSavedListener onPlacemarkSavedListener;

    @FXML
    private Label nameLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label latitudeLabel;

    @FXML
    private Label longitudeLabel;

    @FXML
    private JFXComboBox<AZMAPFormat.Placemark.Types> placemarkTypeCB;

    @FXML
    private JFXComboBox<AZMAPFormat.Placemark.Subtypes> placemarkSubtypeCB;

    @FXML
    private JFXTextArea lookupTermTA;

    @FXML
    private GridPane infoObjectsGP;

    @FXML
    private FlowPane imagesFP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initPlacemarkObject(JSONObject placemarkObject) {
        this.placemarkObject = placemarkObject;
        refreshDialog();
    }

    public void setOnPlacemarkSavedListener(OnPlacemarkSavedListener onPlacemarkSavedListener) {
        this.onPlacemarkSavedListener = onPlacemarkSavedListener;
    }

    public void refreshDialog() {
        String name = placemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME, "");
        String description = placemarkObject.optString(AZMAPFormat.Placemark.Keys.DESCRIPTION, "");
        String latitude = placemarkObject.optString(AZMAPFormat.Placemark.Keys.LATITUDE, "");
        String longitude = placemarkObject.optString(AZMAPFormat.Placemark.Keys.LONGITUDE, "");

        nameLabel.setText(name);
        descriptionLabel.setText(description);
        latitudeLabel.setText(latitude);
        longitudeLabel.setText(longitude);

        refreshPlacemarkTypes();
        refreshPlacemarkSubtypes();

        String placemarkTypeStr = placemarkObject.optString(AZMAPFormat.Placemark.Keys.PLACEMARK_TYPE, "");
        if (!placemarkTypeStr.isEmpty()) {
            placemarkTypeCB.getSelectionModel().select(AZMAPFormat.Placemark.Types.valueOf(placemarkTypeStr));
        }

        String placemarkSubtypeStr = placemarkObject.optString(AZMAPFormat.Placemark.Keys.PLACEMARK_SUBTYPE, "");
        if (!placemarkSubtypeStr.isEmpty()) {
            placemarkSubtypeCB.getSelectionModel().select(AZMAPFormat.Placemark.Subtypes.valueOf(placemarkSubtypeStr));
        }

        String lookupTerms = placemarkObject.optString(AZMAPFormat.Placemark.Keys.LOOKUP_TERMS, "");
        lookupTermTA.setText(lookupTerms);

        infoObjectsGP.getChildren().clear();
        JSONArray placemarkInfos = placemarkObject.optJSONArray(AZMAPFormat.Placemark.Keys.PLACEMARK_INFOS);
        if (placemarkInfos != null) {
            for (int i = 0; i < placemarkInfos.length(); i++) {
                try {
                    JSONObject infoObject = placemarkInfos.getJSONObject(i);

                    String key = infoObject.getString(AZMAPFormat.PlacemarkInfo.InfoObject.KEY);
                    String value = infoObject.getString(AZMAPFormat.PlacemarkInfo.InfoObject.VALUE);

                    addInfoObject(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        JSONArray images = placemarkObject.optJSONArray(AZMAPFormat.Placemark.Keys.IMAGES);
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                try {
                    JSONObject imageObject = images.getJSONObject(i);

                    String imageType = imageObject.optString(AZMAPFormat.Image.Keys.TYPE, "");
                    String imageData = imageObject.optString(AZMAPFormat.Image.Keys.DATA, "");
                    if (!imageType.isEmpty() && !imageData.isEmpty()) {
                        if (AZMAPFormat.Image.Types.valueOf(imageType) == AZMAPFormat.Image.Types.BINARY) {
                            StaticMethods.debug("Adding Image: " + imageData);

                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(imageData));
                            insertImageView(byteArrayInputStream);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void refreshPlacemarkTypes() {
        for (AZMAPFormat.Placemark.Types type : AZMAPFormat.Placemark.Types.values()) {
            placemarkTypeCB.getItems().add(type);
        }
        placemarkTypeCB.valueProperty().addListener(new ChangeListener<AZMAPFormat.Placemark.Types>() {
            @Override
            public void changed(ObservableValue<? extends AZMAPFormat.Placemark.Types> observable, AZMAPFormat.Placemark.Types oldValue, AZMAPFormat.Placemark.Types newValue) {
                refreshPlacemarkSubtypes();
            }
        });
    }

    private void refreshPlacemarkSubtypes() {
        AZMAPFormat.Placemark.Types selectedPlacemarkType = placemarkTypeCB.getSelectionModel().getSelectedItem();
        if (selectedPlacemarkType != null) {
            if (selectedPlacemarkType == AZMAPFormat.Placemark.Types.BUILDING) {
                for (AZMAPFormat.Placemark.Subtypes type : AZMAPFormat.Placemark.Subtypes.values()) {
                    if (type.toString().startsWith("BUILDING_")) {
                        placemarkSubtypeCB.getItems().add(type);
                    }
                }
            } else {
                placemarkSubtypeCB.getItems().clear();
            }
        }
    }

    /*private InfoObjectController createInfoObject(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Constants.App.Paths.Layouts.TEMPLATES + "info_object_item.fxml"));
        try {
            fxmlLoader.load();
            InfoObjectController infoObjectController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public JFXTextField createJFXTextField() {
        JFXTextField jfxTextField = new JFXTextField();
        jfxTextField.setLabelFloat(true);
        jfxTextField.setPadding(new Insets(5));
        jfxTextField.setStyle("-fx-text-fill: #eee");
//        jfxTextField.setTextFill(Paint.valueOf("#eeeeee"));
        jfxTextField.setFont(new Font("Quicksand Bold", 16));
        return jfxTextField;
    }

    public JFXTextArea createJFXTextArea() {
        JFXTextArea jfxTextArea = new JFXTextArea();
        jfxTextArea.setLabelFloat(true);
        jfxTextArea.setPadding(new Insets(5));
        jfxTextArea.setStyle("-fx-text-fill: #eee");
//        jfxTextField.setTextFill(Paint.valueOf("#eeeeee"));
        jfxTextArea.setFont(new Font("Quicksand Bold", 16));
        jfxTextArea.setPrefRowCount(1);
        return jfxTextArea;
    }

    private void addInfoObject(String key, String value) {
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.BOTTOM);
        rowConstraints.setVgrow(Priority.ALWAYS);
        infoObjectsGP.getRowConstraints().add(rowConstraints);
        JFXTextField keyTextField = createJFXTextField();
        JFXTextArea valueTextArea = createJFXTextArea();


        keyTextField.setPromptText("Key");
        valueTextArea.setPromptText("Value");
        keyTextField.setText(key);
        valueTextArea.setText(value);
        infoObjectsGP.addRow(infoObjectsGP.getRowConstraints().size(), keyTextField, valueTextArea);
    }

    private void insertImageView(InputStream inputStream) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, buffer);
            String imageData = Base64.getEncoder().encodeToString(buffer.toByteArray());

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(imageData));
            Image image = new Image(byteArrayInputStream);
            StaticMethods.debug("Input Stream length 1: " + inputStream.available());

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Constants.App.Paths.Layouts.TEMPLATES + "placemark_image_item.fxml"));

            AnchorPane imageWrapper = fxmlLoader.load();
            imageWrapper.getProperties().put("image_data", imageData);
            PlacemarkImageItemController placemarkImageItemController = fxmlLoader.getController();
            placemarkImageItemController.initItem(imageWrapper, image);
            placemarkImageItemController.setOnRemoveImageClickedListener(new PlacemarkImageItemController.OnRemoveImageClickedListener() {
                @Override
                public void onRemoveImageClicked(AnchorPane root) {
                    imagesFP.getChildren().remove(root);
                }
            });
            imagesFP.getChildren().add(imageWrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addInfoObjectRow() {
        addInfoObject("", "");
    }

    @FXML
    public void addNewImage() {
        ArrayList<String> filterList = new ArrayList<>();
        filterList.add("*.bmp");
        filterList.add("*.jpg");
        filterList.add("*.jpeg");
        filterList.add("*.png");

        File imageFile = StaticMethods.openFilePicker(getDialogStage(), "Select Image", "Image Files", filterList);
        try {
            if (imageFile != null) {
                insertImageView(new FileInputStream(imageFile));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void savePlacemark() {
        try {
            AZMAPFormat.Placemark.Types placemarkType = placemarkTypeCB.getSelectionModel().getSelectedItem();
            AZMAPFormat.Placemark.Subtypes placemarkSubtype = placemarkSubtypeCB.getSelectionModel().getSelectedItem();

            if (placemarkType == null) {
                placemarkObject.put(AZMAPFormat.Placemark.Keys.PLACEMARK_TYPE, AZMAPFormat.Placemark.Types.OTHER);
            } else {
                placemarkObject.put(AZMAPFormat.Placemark.Keys.PLACEMARK_TYPE, placemarkType);
            }

            if (placemarkSubtype == null) {
                placemarkObject.put(AZMAPFormat.Placemark.Keys.PLACEMARK_SUBTYPE, AZMAPFormat.Placemark.Subtypes.OTHER);
            } else {
                placemarkObject.put(AZMAPFormat.Placemark.Keys.PLACEMARK_SUBTYPE, placemarkSubtype);
            }

            placemarkObject.put(AZMAPFormat.Placemark.Keys.LOOKUP_TERMS, lookupTermTA.getText());

            JSONArray placemarkInfos = new JSONArray();
            for (int i = 0; i < infoObjectsGP.getChildren().size(); i++) {
                JFXTextField keyTF = (JFXTextField) infoObjectsGP.getChildren().get(i);
                i++;
                JFXTextArea valueTA = (JFXTextArea) infoObjectsGP.getChildren().get(i);

                String key = keyTF.getText();
                String value = valueTA.getText();

                if (!key.isEmpty() && !value.isEmpty()) {
                    JSONObject infoObject = new JSONObject();
                    infoObject.put(AZMAPFormat.PlacemarkInfo.InfoObject.KEY, key);
                    infoObject.put(AZMAPFormat.PlacemarkInfo.InfoObject.VALUE, value);

                    placemarkInfos.put(infoObject);
                }
            }

            placemarkObject.put(AZMAPFormat.Placemark.Keys.PLACEMARK_INFOS, placemarkInfos);

            JSONArray images = new JSONArray();
            for (int i = 0; i < imagesFP.getChildren().size(); i++) {
                AnchorPane imageWrapper = (AnchorPane) imagesFP.getChildren().get(i);
                String imageData = (String) imageWrapper.getProperties().getOrDefault("image_data", "");

                if (!imageData.isEmpty()) {
//                    StaticMethods.debug("Input Stream length: " + inputStream.available());
                    JSONObject imageObject = new JSONObject();

                    imageObject.put(AZMAPFormat.Image.Keys.TYPE, AZMAPFormat.Image.Types.BINARY);
                    imageObject.put(AZMAPFormat.Image.Keys.DATA, imageData);

                    StaticMethods.debug("Adding Image 1: " + imageData);

                    images.put(imageObject);
                }
            }

            placemarkObject.put(AZMAPFormat.Placemark.Keys.IMAGES, images);


            if (onPlacemarkSavedListener != null) {
                onPlacemarkSavedListener.onPlacemarkSaved(placemarkObject);
                getDialogStage().close();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnPlacemarkSavedListener {
        void onPlacemarkSaved(JSONObject placemarkObject);
    }
}
