package com.azeesoft.mapdatagenerator.java.tools;

/**
 * Created by azizt on 7/21/2017.
 */
public class Constants {

    public static class App {
        final public static boolean DEV_MODE = false;
        final public static boolean RESET_APP = false;
        final public static boolean DEBUG = true;
        final public static String TITLE = "Map Data Generator";
        final public static String DEFAULT_DEBUG_TAG = "MDG Debug";
        final public static String LAUNCHER_ICON_FILE_NAME = "launcher_icon.png";

        public static class Paths {
            public static class Layouts {
                final public static String BASE = "/com/azeesoft/mapdatagenerator/res/layouts/";
                final public static String DIALOGS = BASE + "dialogs/";
                final public static String ROOTPAGES = BASE + "rootpages/";
                final public static String TEMPLATES = BASE + "templates/";
            }

            final public static String IMAGES = "/com/azeesoft/mapdatagenerator/res/images/";
        }

        public static class PreferencesInfo {
            final public static String NODE_NAME = "AZMapDataGenerator";

            public static class Keys {
                public static String FILE_CHOOSER_LAST_PATH = "file_chooser_last_path";
            }
        }
    }

    public static class Local {
        public static class Paths {
//            final public static String TIMECARD_BASE = "timecard_data/";
        }
    }


    /*public static class Rest {
        public static class Paths {
//            final public static String ROOT = "http://azee.me/azeesoft/timecard/";
            final public static String ROOT = "https://azeesoft.com/timecard/";
//            public static String ROOT = "https://azeesoft.com/timecard_new/";
            final public static String ROOT_PHP = ROOT + "php/";
        }
    }*/
}
