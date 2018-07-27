package com.staples.weeklyreport.Service;

import com.staples.weeklyreport.Model.Asset;
import com.staples.weeklyreport.Model.Packaging;
import com.staples.weeklyreport.Model.Product;
import com.staples.weeklyreport.Model.PublicationBrand;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ParseXml extends DefaultHandler {

    static final Logger LOGGER = Logger.getLogger(ParseXml.class.getName());
    String contextID = null;
    String currentQualifierID = null;
    ArrayList<String> contextList = new ArrayList<String>();
    private HashMap<String, List<String>> qualifierContextMapping = new HashMap<String, List<String>>();
    private boolean packagingFlag;
    private Packaging packageObj = new Packaging();
    private Product productObj = new Product();
    private Asset assetObj = new Asset();
    private PublicationBrand brandObj = new PublicationBrand();
    private boolean publicationBrandFlag;
    private boolean assetFlag;
    private String qualifierID;
    private boolean productFlag;
    private boolean typologyFlag;
    private String typologyId;
    private HashMap<String, String> nameMap = new HashMap<String, String>();
    private String value;
    private HashMap<String, String> typologyName = new HashMap<String, String>();
    private HashMap<String, String> assetPushLocationMap = new HashMap<String, String>();
    private HashMap<String, ArrayList> classificationReferenceMap = new HashMap<String, ArrayList>();
    private String attributeID;
    private HashMap<String, Object> values = new HashMap<String, Object>();
    private boolean valueGroupFlag;
    private boolean multiValueFlag;
    private HashMap<String, String> valueMap = new HashMap<String, String>();
    private HashMap<String, Object> multiValueMap = new HashMap<String, Object>();
    private HashMap<String, Object> valueGroupMap = new HashMap<String, Object>();
    private HashMap<String,ArrayList> assetCrossReferenceMap = new HashMap<String,ArrayList>();
    private HashMap<String,ArrayList> productCrossReferenceMap = new HashMap<String,ArrayList>();
    public ArrayList<Asset> assetList = new ArrayList<Asset>();
    public ArrayList<Product> productList = new ArrayList<Product>();
    public ArrayList<Packaging> packagingList = new ArrayList<Packaging>();
    public ArrayList<PublicationBrand> brandList = new ArrayList<PublicationBrand>();

    public ParseXml(File xmlInputFile) {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {

            FileInputStream xmlInput = new FileInputStream(xmlInputFile);
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse((InputStream) xmlInput, this);
            xmlInput.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Parsing Exception : ", e);
        }
    }

    public void startDocument() throws SAXException {
        LOGGER.info("BEGIN parsing XML");
    }

    public void endDocument() throws SAXException {
        LOGGER.info("End of parsing XML");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (qName.equals("STEP-ProductInformation")) {
            if (attributes.getValue("ContextID") != null) {
                contextID = attributes.getValue("ContextID");
            }
        }

        if (qName.equals("Qualifier")) {
            currentQualifierID = attributes.getValue("ID");
        }

        if (qName.equals("Context")) {
            // Extracting all the configured contexts
            String context = attributes.getValue("ID");

            if (!contextList.contains(context))
                contextList.add(context);

            // Picking different qualifiers and contextIDthe corresponding contexts mapped
            // to it
            ArrayList<String> contextQualifierList;
            contextQualifierList = (ArrayList<String>) qualifierContextMapping.get(currentQualifierID);
            if (contextQualifierList == null) {
                contextQualifierList = new ArrayList<String>();
                contextQualifierList.add(context);
                qualifierContextMapping.put(currentQualifierID, contextQualifierList);
            } else {
                if (!contextQualifierList.contains(context)) {
                    contextQualifierList.add(context);
                    qualifierContextMapping.put(currentQualifierID, contextQualifierList);
                }
            }
        }

        if (qName.equals("Product")) {
            if (attributes.getValue("UserTypeID") != null) {
                if (attributes.getValue("UserTypeID").contains("Packaging.")) {
                    packagingFlag = true;
                    packageObj.setId(attributes.getValue("ID"));
                }
                if (attributes.getValue("UserTypeID").equals("PublicationBrand")) {
                    publicationBrandFlag = true;
                    brandObj.setId(attributes.getValue("ID"));
                }
                if (attributes.getValue("UserTypeID").equals("Product")) {
                    productFlag = true;
                    productObj.setId(attributes.getValue("ID"));

                }
                if (attributes.getValue("UserTypeID").equals("Typology")) {
                    typologyFlag = true;
                    if (contextList.isEmpty()) {
                        contextList.add(contextID);
                    }
                    typologyId = attributes.getValue("ID");

                }
                if (attributes.getValue("UserTypeID").equals("ProductGrid")) {

                }
                if (attributes.getValue("UserTypeID").equals("Department")) {

                }
                if (attributes.getValue("UserTypeID").equals("Division")) {

                }
            }
        }

        if (qName.equals("Name")) {
            if (assetFlag || productFlag || publicationBrandFlag || packagingFlag) {
                if (attributes.getValue("QualifierID") != null) {
                    qualifierID = attributes.getValue("QualifierID");
                }
            }
        }
        if (qName.equals("Asset")) {
            values = new HashMap<String, Object>();
            assetFlag = true;
            assetObj.setId(attributes.getValue("ID"));
            assetObj.setAssetType(attributes.getValue("UserTypeID"));
        }
        if (qName.equals("AssetPushLocation")) {
            if (assetFlag) {
                int attributeLength = attributes.getLength();
                for (int i = 0; i < attributeLength; i++) {
                    assetPushLocationMap.put(attributes.getQName(i), attributes.getValue(i));
                }
            }
        }
        if (qName.equals("ClassificationReference")) {
            ArrayList tempArr = new ArrayList();
            tempArr.add(attributes.getValue("ClassificationID"));

            if(assetFlag) {
                if(values.get("ClassificationID") == null) {
                    values.put("ClassificationID", tempArr);
                }
                else {
                    tempArr.addAll((ArrayList) values.get("ClassificationID"));
                    values.put("ClassificationID", tempArr);
                }

            }
            else if(productFlag || packagingFlag) {
                String type = attributes.getValue("Type").replaceAll("\\.", "_");
                if(classificationReferenceMap.get(type) == null) {
                    classificationReferenceMap.put(type,tempArr);
                }
                else {
                    tempArr.addAll(classificationReferenceMap.get(type));
                    classificationReferenceMap.put(type,tempArr);
                }
            }
        }
        if (qName.equals("AssetCrossReference")) {
            ArrayList tempArr = new ArrayList();
            tempArr.add(attributes.getValue("AssetID"));
            String type = attributes.getValue("Type").replaceAll("\\.", "_");
            if(assetCrossReferenceMap.get(type) == null) {
                assetCrossReferenceMap.put(type,tempArr);
            }
            else {
                tempArr.addAll(assetCrossReferenceMap.get(type));
                assetCrossReferenceMap.put(type,tempArr);
            }
        }
        if (qName.equals("ProductCrossReference")) {
            ArrayList tempArr = new ArrayList();
            tempArr.add(attributes.getValue("ProductID"));
            String type = attributes.getValue("Type").replaceAll("\\.", "_");
            if(productCrossReferenceMap.get(type) == null) {
                productCrossReferenceMap.put(type,tempArr);
            }
            else {
                tempArr.addAll(productCrossReferenceMap.get(type));
                productCrossReferenceMap.put(type,tempArr);
            }
        }
        if (qName.equals("Values")) {

        }
        if (qName.equals("Value")) {
            if (assetFlag || productFlag || publicationBrandFlag || packagingFlag) {
                if (attributes.getValue("AttributeID") != null) {
                    attributeID = attributes.getValue("AttributeID").replaceAll("\\.", "_");
                }
                if (attributes.getValue("DerivedContextID") != null) {
                    qualifierID = attributes.getValue("DerivedContextID");
                }
                if (attributes.getValue("QualifierID") != null) {
                    qualifierID = attributes.getValue("QualifierID");
                }
            }
        }
        if (qName.equals("ValueGroup")) {
            if (assetFlag || productFlag || publicationBrandFlag || packagingFlag) {
                if (attributes.getValue("AttributeID") != null) {
                    attributeID = attributes.getValue("AttributeID").replaceAll("\\.", "_");
                }
                valueGroupFlag = true;
            }
        }
        if (qName.equals("MultiValue")) {
            if (assetFlag || productFlag || publicationBrandFlag || packagingFlag) {
                if (attributes.getValue("AttributeID") != null) {
                    attributeID = attributes.getValue("AttributeID").replaceAll("\\.", "_");
                }
                multiValueFlag = true;
            }
        }

    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equals("Qualifier")) {
            currentQualifierID = null;
        }
        if (qName.equals("Asset")) {
            assetObj.setValues(values);
            assetObj.setName(nameMap);
            assetList.add(assetObj);
            // Reset Value
            assetFlag = false;
            nameMap = new HashMap<String, String>();
            valueMap = new HashMap<String, String>();
            assetObj = new Asset();
            values = new HashMap<String, Object>();
        }
        if (qName.equals("AssetPushLocation")) {
            if (assetFlag) {
                assetPushLocationMap.put("Value", value);
                if (assetObj.getAssetPushLocation() == null) {
                    ArrayList temp = new ArrayList();
                    temp.add(assetPushLocationMap);
                    assetObj.setAssetPushLocation(temp);
                } else {
                    ArrayList temp = assetObj.getAssetPushLocation();
                    temp.add(assetPushLocationMap);
                    assetObj.setAssetPushLocation(temp);
                }
            }
            value = null;
            assetPushLocationMap = new HashMap<String, String>();
        }

        if (qName.equals("Product")) {
            if (packagingFlag) {
                packageObj.setName(nameMap);
                packageObj.setValues(values);
                packageObj.setClassificationReference(classificationReferenceMap);
                packagingList.add(packageObj);

                // ResetValues
                packagingFlag = false;
                nameMap = new HashMap<String, String>();
                valueMap = new HashMap<String, String>();
                packageObj = new Packaging();
                values = new HashMap<String, Object>();
                classificationReferenceMap = new HashMap<>();
            }
            if (publicationBrandFlag) {
                brandObj.setName(nameMap);
                brandObj.setAssetCrossReference(assetCrossReferenceMap);
                brandList.add(brandObj);
                // ResetValue
                nameMap = new HashMap<String, String>();
                publicationBrandFlag = false;
                brandObj = new PublicationBrand();
                values = new HashMap<String, Object>();
                assetCrossReferenceMap = new HashMap<String, ArrayList>();
            }
            if (productFlag) {
                productObj.setName(nameMap);
                productObj.setValues(values);
                productObj.setAssetCrossReference(assetCrossReferenceMap);
                productObj.setClassificationReference(classificationReferenceMap);
                productObj.setProductCrossReference(productCrossReferenceMap);
                productList.add(productObj);
                // Reset Value
                productFlag = false;
                nameMap = new HashMap<String, String>();
                valueMap = new HashMap<String, String>();
                productObj = new Product();
                values = new HashMap<String, Object>();
                classificationReferenceMap = new HashMap<>();
                assetCrossReferenceMap = new HashMap<String, ArrayList>();
                productCrossReferenceMap = new HashMap();
            }
        }
        if (qName.equals("Name")) {
            if (assetFlag || productFlag || publicationBrandFlag || packagingFlag) {
                if (qualifierID != null) {
                    for (String context : getQualifier(qualifierID)) {
                        nameMap.put(context, value);
                    }
                } else {
                    for (String context : contextList) {
                        nameMap.put(context, value);
                    }
                    if (typologyFlag && !productFlag) {
                        typologyName = nameMap;
                    }
                }
                qualifierID = null;
                value = null;
            }
        }
        if (qName.equals("Value")) {
            if (assetFlag || productFlag || publicationBrandFlag || packagingFlag) {
                if (qualifierID != null) {
                    for (String context : getQualifier(qualifierID)) {
                        if (multiValueFlag || valueGroupFlag) {
                            if (valueMap.get(context) != null) {
                                String newValue = valueMap.get(context) + ";" + value;
                                valueMap.put(context, newValue);
                            } else {
                                valueMap.put(context, value);
                            }
                        } else {
                            valueMap.put(context, value);
                        }
                    }
                    if (!(multiValueFlag || valueGroupFlag)) {
                        values.put(attributeID, valueMap);
                        valueMap = new HashMap<String, String>();
                        attributeID = null;
                    }
                } else {
                    if (multiValueFlag) {
                        if (values.get(attributeID) != null) {
                            String newValue = values.get(attributeID) + ";" + value;
                            values.put(attributeID, newValue);
                        } else {
                            values.put(attributeID, value);
                        }

                    } else if (valueGroupFlag) {
                        System.out.println("ValueGroup with no qualifier id");
                    } else {
                        values.put(attributeID, value);
                        attributeID = null;
                    }

                }
                qualifierID = null;
                value = null;
            }
        }
        if (qName.equals("ValueGroup")) {
            if (assetFlag || productFlag || publicationBrandFlag || packagingFlag) {
                valueGroupFlag = false;

                if (multiValueFlag) {
//                    if (values.get(attributeID) != null) {
//                        ArrayList list = new ArrayList();
//                        list.add(values.get(attributeID));
//                        list.add(valueMap);
//                        values.put(attributeID, list);
//                    } else {
//                        values.put(attributeID, valueMap);
//                    }
                } else {
                    values.put(attributeID, valueMap);
                    attributeID = null;
                }
                valueMap = new HashMap<String, String>();
            }
        }
        if (qName.equals("MultiValue")) {
            if (assetFlag || productFlag || publicationBrandFlag || packagingFlag) {
                multiValueFlag = false;
                if (values.get(attributeID) == null) {
                    values.put(attributeID, valueMap);
                    valueMap = new HashMap<String, String>();
                }
                attributeID = null;
            }
        }
        if (qName.equals("STEP-ProductInformation")) {

        }

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (assetFlag || productFlag || publicationBrandFlag || packagingFlag) {

            if (value == null) {
                value = new String();
            }
            value = new String(ch, start, length);
        }

    }

    /**
     * This method validates and returns the exact list of contexts based on Context
     * ID or Qualifier ID
     *
     * @param qualifierName
     * @return
     */
    public List<String> getQualifier(String qualifierName) {
        List<String> validQualifierList = new ArrayList<String>();
        // For exact match
        if (contextList.contains(qualifierName)) {
            validQualifierList.add(qualifierName);
            return validQualifierList;
        }

        // For qualifiers which are mapped to different contexts
        if (qualifierContextMapping.containsKey(qualifierName)) {
            return qualifierContextMapping.get(qualifierName);
        }

        return validQualifierList;
    }
}
