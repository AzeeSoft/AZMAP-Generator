package com.azeesoft.mapdatagenerator.java.tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Consumer;

/**
 * Created by azizt on 8/4/2017.
 */
public class CSSObject extends JSONObject {

    public static CSSObject parseInlineCSS(String cssString) {
        CSSObject cssObject = new CSSObject();

        String[] styles = cssString.split(";");
        for(String style: styles){
            String[] data = style.split(":", 2);
            try {

                cssObject.put(data[0], data[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cssObject;
    }

    @Override
    public String toString() {
        return toCSSString();
    }

    private String toCSSString(){
        final StringBuilder cssStringBuilder = new StringBuilder();
        keys().forEachRemaining(new Consumer() {
            @Override
            public void accept(Object o) {
                try {
                    String key = o.toString();
                    String value = getString(key);
                    cssStringBuilder.append(key).append(": ").append(value).append("; ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return cssStringBuilder.toString();
    }

}
