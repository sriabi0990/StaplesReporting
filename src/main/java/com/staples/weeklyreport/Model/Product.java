package com.staples.weeklyreport.Model;
import java.util.ArrayList;
import java.util.HashMap;

public class Product {
    private String id;
    private HashMap<String,String> name;
    private HashMap<String, ArrayList> classificationReference;
    private HashMap<String,Object> values;
    private HashMap<String, ArrayList> assetCrossReference;
    private HashMap<String, ArrayList> productCrossReference;
    public static final String objectType = "Product";

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

    public HashMap<String, ArrayList> getProductCrossReference() {
        return productCrossReference;
    }

    public void setProductCrossReference(HashMap<String, ArrayList> productCrossReference) {
        this.productCrossReference = productCrossReference;
    }

    public HashMap<String, ArrayList> getClassificationReference() {
        return classificationReference;
    }

    public void setClassificationReference(HashMap<String, ArrayList> classificationReference) {
        this.classificationReference = classificationReference;
    }

    public HashMap<String, Object> getValues() {
        return values;
    }

    public void setValues(HashMap<String, Object> values) {
        this.values = values;
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
