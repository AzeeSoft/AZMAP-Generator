package com.azeesoft.mapdatagenerator.java.managers;

import com.azeesoft.mapdatagenerator.java.others.AZMAPFormat;
import com.azeesoft.mapdatagenerator.java.tools.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by azizt on 9/1/2017.
 */
public class PlacemarkDataBuilder {

    public static class PlacemarkData {
        public String name = "";
        public String description ="";
        public String latitude = "0.0";
        public String longitude = "0.0";
    }

    ArrayList<PlacemarkData> placemarkDataArrayList;

    public PlacemarkDataBuilder() {
        placemarkDataArrayList = new ArrayList<>();
    }

    public void addPlacemarkData(PlacemarkData placemarkData) {
        placemarkDataArrayList.add(placemarkData);
    }

    private JSONObject convertToJSONObject(PlacemarkData placemarkData) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AZMAPFormat.Placemark.Keys.NAME, placemarkData.name);
        jsonObject.put(AZMAPFormat.Placemark.Keys.DESCRIPTION, placemarkData.description);
        jsonObject.put(AZMAPFormat.Placemark.Keys.LATITUDE, placemarkData.latitude);
        jsonObject.put(AZMAPFormat.Placemark.Keys.LONGITUDE, placemarkData.longitude);
        return jsonObject;
    }

    public JSONObject buildJSON(){
        JSONArray placemarkJSONArray = new JSONArray();
        for(PlacemarkData placemarkData: placemarkDataArrayList){
            try {
                placemarkJSONArray.put(convertToJSONObject(placemarkData));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(AZMAPFormat.Keys.PLACEMARKS, placemarkJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
