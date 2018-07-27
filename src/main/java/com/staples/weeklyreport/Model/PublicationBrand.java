package com.staples.weeklyreport.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class PublicationBrand {
    private String id;
    private HashMap<String,String> name;
    private HashMap<String, ArrayList> assetCrossReference;
    public static final String objectType = "PublicationBrand";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, String> getName() {
        return name;
    }

    public void setName(HashMap<String, String> name) {
        this.name = name;
    }

    public HashMap<String, ArrayList> getAssetCrossReference() {
        return assetCrossReference;
    }

    public void setAssetCrossReference(HashMap<String, ArrayList> assetCrossReference) {
        this.assetCrossReference = assetCrossReference;
    }

    public static String getObjectType() {
        return objectType;
    }
}

