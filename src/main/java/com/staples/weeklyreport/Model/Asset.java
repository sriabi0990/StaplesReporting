package com.staples.weeklyreport.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Asset {
    private String id;
    private String assetType;
    private HashMap<String,String> name;
    private ArrayList<HashMap<String,String>> assetPushLocation;
    private HashMap<String,Object> values;
    public static final String objectType = "Asset";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public HashMap<String, String> getName() {
        return name;
    }

    public void setName(HashMap<String, String> name) {
        this.name = name;
    }

    public ArrayList<HashMap<String, String>> getAssetPushLocation() {
        return assetPushLocation;
    }

    public void setAssetPushLocation(ArrayList<HashMap<String, String>> assetPushLocation) {
        this.assetPushLocation = assetPushLocation;
    }


    public HashMap<String, Object> getValues() {
        return values;
    }

    public void setValues(HashMap<String, Object> values) {
        this.values = values;
    }

    public static String getObjectType() {
        return objectType;
    }
}
