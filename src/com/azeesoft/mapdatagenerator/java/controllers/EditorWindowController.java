package com.azeesoft.mapdatagenerator.java.controllers;
/**
 * Created by azizt on 9/1/2017.
 */

import com.azeesoft.mapdatagenerator.java.controllers.dialogs.ConflictsDialogController;
import com.azeesoft.mapdatagenerator.java.controllers.dialogs.DuplicateDialogController;
import com.azeesoft.mapdatagenerator.java.controllers.dialogs.EditPlacemarkDialogController;
import com.azeesoft.mapdatagenerator.java.controllers.templates.PlacemarkItemController;
import com.azeesoft.mapdatagenerator.java.managers.ImportConflictManager;
import com.azeesoft.mapdatagenerator.java.managers.PlacemarkDataBuilder;
import com.azeesoft.mapdatagenerator.java.others.AZMAPFormat;
import com.azeesoft.mapdatagenerator.java.tools.Constants;
import com.azeesoft.mapdatagenerator.java.tools.StaticMethods;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditorWindowController extends BaseWindowController {

    private JSONObject mainJsonData;

    @FXML
    private VBox placemarksVB;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void initStage(Stage stage, Parent root) {
        super.initStage(stage, root);
        getStage().setTitle("Create/Open AZMAP");
    }

    @Override
    public boolean onClosing() {
        super.onClosing();
        CreateAZMAPWindowController createAZMAPWindowController = (CreateAZMAPWindowController) CreateAZMAPWindowController.createWindow("create_azmap_window", getStage());
        if (createAZMAPWindowController != null) {
            createAZMAPWindowController.showStage();
        }
        return true;
    }

    public void setMainJsonData(JSONObject mainJsonData) {
        this.mainJsonData = mainJsonData;
        refreshEditor();
//        StaticMethods.debug("JSON Data: " + mainJsonData);
    }

    public void insertJsonData(JSONObject jsonData) {
        checkConflicts(jsonData);
    }

    public void checkConflicts(JSONObject importingJSONData) {
        ImportConflictManager importConflictManager = new ImportConflictManager(mainJsonData, importingJSONData);
        ArrayList<ImportConflictManager.Conflict> conflicts = importConflictManager.findConflicts();
        if (conflicts.isEmpty()) {

            updateDescriptions(importingJSONData);
            addNewImportedData(importConflictManager.getReturningPlacemarkIndices(), importingJSONData);
            refreshEditor();
        } else {
            ConflictsDialogController conflictsDialogController = (ConflictsDialogController) ConflictsDialogController.initDialog("conflicts_dialog", getStage());
            if (conflictsDialogController != null) {
                conflictsDialogController.setOnConflictsResolvedListener(new ConflictsDialogController.OnConflictsResolvedListener() {
                    @Override
                    public void onConflictsResolved(JSONObject resolvedJSONData) {
                        setMainJsonData(resolvedJSONData);
                        updateDescriptions(importingJSONData);
                        addNewImportedData(importConflictManager.getReturningPlacemarkIndices(), importingJSONData);
                        refreshEditor();
                    }
                });
                conflictsDialogController.initConflicts(mainJsonData, importingJSONData, conflicts, importConflictManager.getReturningPlacemarkIndices());
                conflictsDialogController.showDialog();
            }
        }
    }

    private void updateDescriptions(JSONObject importingJSONData) {

        JSONArray importingPlacemarksArray = importingJSONData.optJSONArray(AZMAPFormat.Keys.PLACEMARKS);
        JSONArray existingPlacemarksArray = mainJsonData.optJSONArray(AZMAPFormat.Keys.PLACEMARKS);
        for (int i = 0; i < importingPlacemarksArray.length(); i++) {
            try {
                JSONObject importingPlacemarkObject = importingPlacemarksArray.getJSONObject(i);
                String importingName = importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME, "");
                String importingDescription = importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.DESCRIPTION, "");
                for (int j = 0; j < existingPlacemarksArray.length(); j++) {
                    JSONObject existingPlacemarkObject = existingPlacemarksArray.getJSONObject(j);
                    String existingName = existingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME, "");
                    if (importingName.equals(existingName)) {
                        existingPlacemarkObject.put(AZMAPFormat.Placemark.Keys.DESCRIPTION, importingDescription);
                        existingPlacemarksArray.put(j, existingPlacemarkObject);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            mainJsonData.put(AZMAPFormat.Keys.PLACEMARKS, existingPlacemarksArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addNewImportedData(ArrayList<Integer> returningIndices, JSONObject importingJSONData) {
        JSONArray existingPlacemarksArray = mainJsonData.optJSONArray(AZMAPFormat.Keys.PLACEMARKS);
        JSONArray importingPlacemarksArray = importingJSONData.optJSONArray(AZMAPFormat.Keys.PLACEMARKS);
        for (int i = 0; i < importingPlacemarksArray.length(); i++) {
            if (!returningIndices.contains(i)) {
                try {
                    existingPlacemarksArray.put(importingPlacemarksArray.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            mainJsonData.put(AZMAPFormat.Keys.PLACEMARKS, existingPlacemarksArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void refreshEditor() {
        placemarksVB.getChildren().clear();
        try {
            JSONArray jsonArray = mainJsonData.getJSONArray(AZMAPFormat.Keys.PLACEMARKS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject placemarkObject = jsonArray.getJSONObject(i);

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Constants.App.Paths.Layouts.TEMPLATES + "placemark_item.fxml"));
                try {
                    AnchorPane placemarkWrapper = fxmlLoader.load();
                    PlacemarkItemController placemarkItemController = fxmlLoader.getController();
                    placemarkItemController.setPlacemarkObject(i, placemarkObject, new PlacemarkItemController.OnEditPlacemarkClickListener() {
                        @Override
                        public void onEditPlacemarkClicked(int placemarkIndex, JSONObject placemarkObject) {
                            EditPlacemarkDialogController editPlacemarkDialogController = (EditPlacemarkDialogController) EditPlacemarkDialogController.initDialog("edit_placemark_dialog", getStage());
                            if (editPlacemarkDialogController != null) {
                                editPlacemarkDialogController.setOnPlacemarkSavedListener(new EditPlacemarkDialogController.OnPlacemarkSavedListener() {
                                    @Override
                                    public void onPlacemarkSaved(JSONObject placemarkObject) {
                                        try {
                                            JSONArray jsonArray = mainJsonData.getJSONArray(AZMAPFormat.Keys.PLACEMARKS);
                                            jsonArray.put(placemarkIndex, placemarkObject);
                                            mainJsonData.put(AZMAPFormat.Keys.PLACEMARKS, jsonArray);
                                            refreshEditor();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                editPlacemarkDialogController.initPlacemarkObject(placemarkObject);
                                editPlacemarkDialogController.showDialog();
                            }

                        }
                    });
                    placemarksVB.getChildren().add(placemarkWrapper);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJSONDataFromKML(File file) {
        try {
            PlacemarkDataBuilder placemarkDataBuilder = new PlacemarkDataBuilder();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList placemarks = doc.getElementsByTagName("Placemark");
            ArrayList<String> placemarkNames = new ArrayList<>();
            ArrayList<String> duplicateNames = new ArrayList<>();
            ArrayList<String> placemarkCoordinates = new ArrayList<>();
            ArrayList<String> duplicateCoordinates = new ArrayList<>();

            for (int i = 0; i < placemarks.getLength(); i++) {
                org.w3c.dom.Node node = placemarks.item(i);

                if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element placemark = (Element) node;
                    PlacemarkDataBuilder.PlacemarkData placemarkData = new PlacemarkDataBuilder.PlacemarkData();
                    Node tmpNode;

                    tmpNode = placemark.getElementsByTagName("name").item(0);
                    if (tmpNode != null) {
                        placemarkData.name = StaticMethods.removeNewLines(tmpNode.getTextContent()).trim();
                        if (placemarkNames.contains(placemarkData.name)) {
                            duplicateNames.add(placemarkData.name);
                            continue;
                        }
                    }

                    tmpNode = placemark.getElementsByTagName("description").item(0);
                    if (tmpNode != null) {
                        placemarkData.description = StaticMethods.removeNewLines(tmpNode.getTextContent()).trim();
                    }

                    tmpNode = placemark.getElementsByTagName("coordinates").item(0);
                    if (tmpNode != null) {
                        String coordinatesStr = StaticMethods.removeNewLines(tmpNode.getTextContent()).trim();
                        String[] coordinates = coordinatesStr.split(",");

                        placemarkData.longitude = coordinates[0];
                        placemarkData.latitude = coordinates[1];

                        if (placemarkCoordinates.contains(placemarkData.latitude + "," + placemarkData.longitude)) {
                            duplicateCoordinates.add(placemarkData.latitude + ", " + placemarkData.longitude);
                            continue;
                        }
                    }

                    placemarkDataBuilder.addPlacemarkData(placemarkData);
                    placemarkNames.add(placemarkData.name);
                    placemarkCoordinates.add(placemarkData.latitude + "," + placemarkData.longitude);
                }
            }

            if (duplicateNames.isEmpty() && duplicateCoordinates.isEmpty()) {
                return placemarkDataBuilder.buildJSON();
            } else {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("duplicate", true);
                    jsonObject.put("duplicate_names", (Object) duplicateNames);
                    jsonObject.put("duplicate_coordinates", (Object) duplicateCoordinates);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @FXML
    public void importKML() {
        File file = StaticMethods.openFilePicker(getStage(), "Import KML", "KML Files", "*.kml");
        if (file != null) {
            JSONObject jsonObject = getJSONDataFromKML(file);
            if (jsonObject != null) {
                if (jsonObject.optBoolean("duplicate", false)) {
                    ArrayList<String> duplicateNames = (ArrayList<String>) jsonObject.opt("duplicate_names");
                    ArrayList<String> duplicateCoordinates = (ArrayList<String>) jsonObject.opt("duplicate_coordinates");

                    DuplicateDialogController duplicateDialogController = (DuplicateDialogController) DuplicateDialogController.initDialog("duplicate_dialog", getStage());
                    if (duplicateDialogController != null) {
                        duplicateDialogController.setDuplicateData(duplicateNames, duplicateCoordinates);
                        duplicateDialogController.showDialog();
                    }

                } else {
                    insertJsonData(jsonObject);
                }
            }
        }
    }

    @FXML
    public void exportAZMAP() {
        File file = StaticMethods.openFileSaver(getStage(), "Export AZMAP", "AZMAP Files", "*.azmap");
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(mainJsonData.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
