package com.azeesoft.mapdatagenerator.java.controllers.templates;
/**
 * Created by azizt on 9/4/2017.
 */

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PlacemarkImageItemController implements Initializable {

    private OnRemoveImageClickedListener onRemoveImageClickedListener;
    private AnchorPane root;

    @FXML
    ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initItem(AnchorPane root, Image image){
        this.root = root;
        imageView.setImage(image);
    }

    public void setOnRemoveImageClickedListener(OnRemoveImageClickedListener onRemoveImageClickedListener) {
        this.onRemoveImageClickedListener = onRemoveImageClickedListener;
    }

    @FXML
    public void remove(){
        if(onRemoveImageClickedListener !=null){
            onRemoveImageClickedListener.onRemoveImageClicked(root);
        }
    }

    public interface OnRemoveImageClickedListener {
        void onRemoveImageClicked(AnchorPane root);
    }
}
