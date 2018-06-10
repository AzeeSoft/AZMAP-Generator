package com.azeesoft.mapdatagenerator.java.others;

/**
 * Created by azizt on 9/4/2017.
 */
public class AZMAPFormat {
    public static class Keys {
        public static String PLACEMARKS = "placemarks";
    }

    public static class Placemark {
        public enum Types {BUILDING, PARKING_LOT, STORE, RESTAURANT, ATM, OTHER}

        public enum Subtypes {BUILDING_LIBRARIES, BUILDING_HOUSING, BUILDING_ATHELETIC_FACILITIES, BUILDING_RECREATIONAL, BUILDING_ACADEMIC, BUILDING_ADMINISTRATIVE, BUILDING_CHURCH, OTHER}

        public static class Keys {
            public static String NAME = "name";
            public static String DESCRIPTION = "description";
            public static String LATITUDE = "latitude";
            public static String LONGITUDE = "longitude";
            public static String IMAGES = "images";
            public static String PLACEMARK_TYPE = "placemark_type";
            public static String PLACEMARK_SUBTYPE = "placemark_subtype";
            public static String LOOKUP_TERMS= "lookup_terms";
            public static String PLACEMARK_INFOS = "placemark_infos";
        }
    }

    public static class Image {
        public enum Types {RESOURCE, BINARY}

        public static class Keys {
            public static String TYPE = "type";
            public static String DATA = "data";
        }
    }

    public static class PlacemarkInfo {
        public static class InfoObject {
            public static String KEY = "key";
            public static String VALUE = "value";
        }
    }
}
