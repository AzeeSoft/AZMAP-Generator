package com.azeesoft.mapdatagenerator.java.tools;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by azizt on 7/21/2017.
 */
public class StaticMethods {
    public static void debug(String msg) {
        debug(Constants.App.DEFAULT_DEBUG_TAG, msg);
    }

    public static void debug(String tag, String msg){
        if(Constants.App.DEBUG) {
            System.out.println(tag + ": " + msg);
        }
    }

    public static void logToFile(String msg){
        debug(msg);
    }

    public static String removeNewLines(String str){
        return str.replaceAll("\n", "").replaceAll("\r", "");
    }

    public static File openFilePicker(Stage owner, String title, String filterDescription, String extensionFilter) {
        ArrayList<String> filterList = new ArrayList<>();
        filterList.add(extensionFilter);
        return openFilePicker(owner, title, filterDescription, filterList);
    }

    public static File openFilePicker(Stage owner, String title, String filterDescription, ArrayList<String> extensionFilter) {
        Preferences preferences = StaticMethods.getAppPreferences();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterDescription, extensionFilter));

        String lastDir = preferences.get(Constants.App.PreferencesInfo.Keys.FILE_CHOOSER_LAST_PATH, null);
        if(lastDir != null) {
//            StaticMethods.debug("Last Dir" + lastDir);
            fileChooser.setInitialDirectory(new File(lastDir));
        }

        File file =  fileChooser.showOpenDialog(owner);

        if(file!=null) {
            saveFileChooserLastPath(file.getParent());
        }

        return file;
    }

    public static File openFileSaver(Stage owner, String title, String filterDescription, String extensionFilter) {
        Preferences preferences = StaticMethods.getAppPreferences();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterDescription, extensionFilter));

        String lastDir = preferences.get(Constants.App.PreferencesInfo.Keys.FILE_CHOOSER_LAST_PATH, null);
        if(lastDir != null) {
//            StaticMethods.debug("Last Dir" + lastDir);
            fileChooser.setInitialDirectory(new File(lastDir));
        }

        File file =  fileChooser.showSaveDialog(owner);

        if(file!=null) {
            saveFileChooserLastPath(file.getParent());
        }

        return file;
    }

    private static void saveFileChooserLastPath(String lastPath){
        Preferences preferences = getAppPreferences();
        preferences.put(Constants.App.PreferencesInfo.Keys.FILE_CHOOSER_LAST_PATH, lastPath);
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Node> getBlurrableNodes(Node node){
        ArrayList<Node> blurrableNodes = new ArrayList<>();
        if(node.getStyleClass().contains("unblurrable")){
            Parent parent = (Parent) node;
            parent.getChildrenUnmodifiable().iterator().forEachRemaining(new Consumer<Node>() {
                @Override
                public void accept(Node node) {
                    if(node.getStyleClass().contains("blurrable")){
                        blurrableNodes.add(node);
                    }
                }
            });
        }else{
            blurrableNodes.add(node);
        }

        return blurrableNodes;
    }

    public static void blurNode(Node node){
        ArrayList<Node> blurrableNodes = getBlurrableNodes(node);
        ColorAdjust adj = new ColorAdjust();
        GaussianBlur blur = new GaussianBlur(10);
        adj.setInput(blur);

        for(Node blurrableNode: blurrableNodes){
            blurrableNode.setEffect(adj);
        }
    }

    public static void unblurNode(Node node){
        ArrayList<Node> blurrableNodes = getBlurrableNodes(node);
        ColorAdjust adj = new ColorAdjust();
        GaussianBlur blur = new GaussianBlur(0);
        adj.setInput(blur);

        for(Node blurrableNode: blurrableNodes){
            blurrableNode.setEffect(adj);
        }
    }

    public static Preferences getAppPreferences() {
        return Preferences.userRoot().node(Constants.App.PreferencesInfo.NODE_NAME);
    }

    public static void resetPreferences(){
        Preferences preferences = getAppPreferences();
        try {
            preferences.clear();
            preferences.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

    }
}
