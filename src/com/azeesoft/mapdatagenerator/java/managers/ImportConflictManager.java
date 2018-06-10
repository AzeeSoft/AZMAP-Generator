package com.azeesoft.mapdatagenerator.java.managers;

import com.azeesoft.mapdatagenerator.java.others.AZMAPFormat;
import com.azeesoft.mapdatagenerator.java.tools.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by azizt on 9/2/2017.
 */
public class ImportConflictManager {

    public static class Conflict {
        public enum ConflictType {NONE, NAME_CONFLICT, COORDINATE_CONFLICT, MISSING_CONFLICT}

        public ConflictType conflictType = ConflictType.NONE;

        public boolean remove = false;

        public JSONObject existingPlacemarkObject;
        public JSONObject importingPlacemarkObject;
    }

    private JSONObject existingData;
    private JSONObject importingData;

    private ArrayList<Integer> returningPlacemarkIndices = new ArrayList<>();

    public ImportConflictManager(JSONObject existingData, JSONObject importingData) {
        this.existingData = existingData;
        this.importingData = importingData;
    }

    public ArrayList<Conflict> findConflicts() {
        ArrayList<Conflict> conflicts = new ArrayList<>();
        JSONArray placemarkArray = existingData.optJSONArray(AZMAPFormat.Keys.PLACEMARKS);
        if (placemarkArray != null) {
            for (int i = 0; i < placemarkArray.length(); i++) {
                try {
                    JSONObject placemarkObject = placemarkArray.getJSONObject(i);
                    Conflict conflict = checkConflict(placemarkObject);
                    if (conflict.conflictType != Conflict.ConflictType.NONE) {
                        conflicts.add(conflict);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return conflicts;
    }

    public ArrayList<Integer> getReturningPlacemarkIndices() {
        return returningPlacemarkIndices;
    }

    private Conflict checkConflict(JSONObject placemarkObject) {
        Conflict conflict = new Conflict();
        conflict.existingPlacemarkObject = placemarkObject;

        String name = placemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME, "");
        String latitude = placemarkObject.optString(AZMAPFormat.Placemark.Keys.LATITUDE, "");
        String longitude = placemarkObject.optString(AZMAPFormat.Placemark.Keys.LONGITUDE, "");

        JSONArray placemarkArray = importingData.optJSONArray(AZMAPFormat.Keys.PLACEMARKS);

        for (int i = 0; i < placemarkArray.length(); i++) {

            try {
                JSONObject importingPlacemarkObject = placemarkArray.getJSONObject(i);
                String importingName = importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.NAME, "");
                String importingLatitude = importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LATITUDE, "");
                String importingLongitude = importingPlacemarkObject.optString(AZMAPFormat.Placemark.Keys.LONGITUDE, "");

                boolean namesMatch = false;
                boolean coordinatesMatch = false;
                if (importingName.equals(name)) {
                    namesMatch = true;
                }
                if (importingLatitude.equals(latitude) && importingLongitude.equals(longitude)) {
                    coordinatesMatch = true;
                }

                if (namesMatch && !coordinatesMatch) {
                    conflict.importingPlacemarkObject = importingPlacemarkObject;
                    conflict.conflictType = Conflict.ConflictType.COORDINATE_CONFLICT;
                    returningPlacemarkIndices.add(i);
                    return conflict;
                } else if (!namesMatch && coordinatesMatch) {
                    conflict.importingPlacemarkObject = importingPlacemarkObject;
                    conflict.conflictType = Conflict.ConflictType.NAME_CONFLICT;
                    returningPlacemarkIndices.add(i);
                    return conflict;
                } else if (namesMatch && coordinatesMatch) {
                    conflict.importingPlacemarkObject = importingPlacemarkObject;
                    conflict.conflictType = Conflict.ConflictType.NONE;
                    returningPlacemarkIndices.add(i);
                    return conflict;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        conflict.importingPlacemarkObject = new JSONObject();
        conflict.conflictType = Conflict.ConflictType.MISSING_CONFLICT;
        return conflict;
    }
}
