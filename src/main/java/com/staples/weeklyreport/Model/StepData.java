package com.staples.weeklyreport.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

@Document(collection = "StepData")
public class StepData {

    @Id
    private String businessUnit;
    private ArrayList<String> context;
    //private ArrayList<Asset> assets;
    private ArrayList<Packaging> packagings;
    private ArrayList<Product> products;
    //private ArrayList<PublicationBrand> brands;

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

//    public ArrayList<Asset> getAssets() {
//        return assets;
//    }
//
//    public void setAssets(ArrayList<Asset> assets) {
//        this.assets = assets;
//    }

    public ArrayList<Packaging> getPackagings() {
        return packagings;
    }

    public void setPackagings(ArrayList<Packaging> packagings) {
        this.packagings = packagings;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

//    public ArrayList<PublicationBrand> getBrands() {
//        return brands;
//    }
//
//    public void setBrands(ArrayList<PublicationBrand> brands) {
//        this.brands = brands;
//    }


    public ArrayList<String> getContext() {
        return context;
    }

    public void setContext(ArrayList<String> context) {
        this.context = context;
    }


}
