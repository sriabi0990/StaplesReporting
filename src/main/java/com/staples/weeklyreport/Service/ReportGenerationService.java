package com.staples.weeklyreport.Service;

import com.staples.weeklyreport.Model.Packaging;
import com.staples.weeklyreport.Model.Product;
import com.staples.weeklyreport.Model.StepData;
import com.staples.weeklyreport.Repository.StepDataRepository;
import com.staples.weeklyreport.Utilities.FileUtilities;
import com.staples.weeklyreport.Utilities.FilterFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Logger;

@EnableMongoRepositories(basePackages = "com.staples.weeklyreport.Repository")
@Service
public class ReportGenerationService {
    @Autowired
    public StepDataRepository stepRepo;

    static final Logger LOGGER = Logger.getLogger(ProcessStepFiles.class.getName());
    String outputFolderPath = "/Users/srramasa/Documents/OwnProj/MongoDBReport/Output";
    String inputFolderPath = "/Users/srramasa/Documents/OwnProj/MongoDBReport/Input";
    String propertiesFile = "weeklyreport.properties";
    String PACKAGING = "PackagingAttributes";
    String PRODUCT = "ProductAttributes";
    String BU = "BusinessUnit";
    private FileUtilities fileUtility = new FileUtilities();
    private final Path rootLocation = Paths.get(outputFolderPath);

    public StepData generateReport(String bu) {

        StepData stepData = stepRepo.findByBusinessUnit(bu);
        return stepData;
    }

    public List<String> getBu() {
        List<String> buList = new ArrayList<String>();
        String bu;
        try {
            Properties prop = new Properties();
            InputStream input = new FileInputStream(propertiesFile);
            prop.load(input);
            bu = prop.getProperty("BusinessUnit");
            LOGGER.info("BU : " + bu);
            buList = Arrays.asList(bu.split(","));
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return buList;

    }

    public List<String> getAttributes(String type) {
        List<String> attributeList = new ArrayList<String>();
        String attributes;
        try {
            Properties prop = new Properties();
            InputStream input = null;
            input = new FileInputStream(propertiesFile);
            prop.load(input);
            LOGGER.info("Type: " +type);

            if(type.equals(PACKAGING)) {

                attributes = prop.getProperty(PACKAGING);
                LOGGER.info("pack attr " + attributes);
//                attributes = "ID,Name,LENCode,EUCode,LocalCode,PackagingLineLegacy";
                attributeList = Arrays.asList(attributes.split(","));
            }
            if(type.equals(PRODUCT)) {

                attributes = prop.getProperty(PRODUCT);
                LOGGER.info("prod attr " + attributes);
//                attributes = "ID,Name,LENCode,ArticleCode,ProductLifeCycleStatus,PrimaryImage,SecondaryImage,PublicationBrand,Packaging_BaseUnit";
                attributeList = Arrays.asList(attributes.split(","));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  attributeList;
    }

    public void processXml(ParseXml obj, String bu) throws IOException {

        StepData dataObj = new StepData();
        dataObj.setBusinessUnit(bu);
        dataObj.setContext(obj.contextList);
//        dataObj.setAssets(obj.assetList);
//        dataObj.setBrands(obj.brandList);
        dataObj.setPackagings(obj.packagingList);
        dataObj.setProducts(obj.productList);
        stepRepo.deleteById(bu);
        stepRepo.insert(dataObj);
        }

    public void processXml() throws IOException {

        ParseXml parseObj;
        File inputFolder = new File(inputFolderPath);
        if(!inputFolder.exists()) {
            LOGGER.severe("Invalid Input Folder Location - Job aborted" );
            System.exit(0);
        }

        //Check output folder
        File outputfolder = new File(outputFolderPath);
        if (!outputfolder.exists()) {
            LOGGER.severe("Invalid Output Folder Location - Job aborted" );
            System.exit(0);
        }

        //Check and create archive folder if needed
        String inputArchiveFolderPath = inputFolderPath+File.separator+"Archive";
        File inputArchiveFolder = new File(inputArchiveFolderPath);
        if(!inputArchiveFolder.exists()) {
            inputArchiveFolder.mkdir();
        }

        String outputArchiveFolderPath = outputFolderPath+File.separator+"Archive";
        File outputArchiveFolder = new File(outputArchiveFolderPath);
        if(!outputArchiveFolder.exists()) {
            outputArchiveFolder.mkdir();
        }

        // Check and create unzip folder if needed
        String unzipFolderPath = inputArchiveFolderPath + File.separator + "unzip";
        File unzipFolder = new File(unzipFolderPath);
        if (!unzipFolder.exists()) {
            unzipFolder.mkdir();
        }

        // Filtering zip files - Unzipping, placing XMl in unzip folder & moving zip file to archive folder
        FilterFiles fileFilter = new FilterFiles(".zip");
        File[] listOfFiles = inputFolder.listFiles(fileFilter);
        if (listOfFiles.length == 0) {
            LOGGER.severe("No zip files exists in the input directory.");
            System.exit(0);
        } else {
            String zipFileName = null;
            LOGGER.info("Number of ZIP Files:" + listOfFiles.length);
            LOGGER.info("Unzip in progress.");
            for (File zipFile : listOfFiles) {
                zipFileName = zipFile.getName();
                LOGGER.info("Unzipping file: " + zipFileName);
                fileUtility.unzip(zipFile.getAbsolutePath(), unzipFolder.getAbsolutePath());
                //fileUtility.archiveFile(inputArchiveFolderPath, zipFile);
            }
        }

        // Filtering XMLs
        fileFilter = new FilterFiles(".xml");
        File[] xmlFileList = unzipFolder.listFiles(fileFilter);
        if (xmlFileList.length == 0) {
            LOGGER.severe("No XML files available for conversion.");
            System.exit(0);
        } else {
            boolean deleteStatus = false;
            LOGGER.info("Number of extracted XMLs:" + xmlFileList.length);
            Arrays.sort(xmlFileList); // Sorting all the XML files
            String buName = null;


            for (File xmlFile : xmlFileList) {
                LOGGER.info("Processing XML:" + xmlFile.getName());
                buName = fileUtility.getContextName(xmlFile.getName());
                System.out.println(buName);
                String collectionName = buName.split("-")[1];
                System.out.println("CollectionName : " + collectionName);
                buName=collectionName;
                //parse xml
                LOGGER.info("XML Parsing - Started");
                ParseXml xmlParserObject = new ParseXml(xmlFile);
                LOGGER.info("XML Parsing - Complete");
                processXml(xmlParserObject,buName);

                //Delete the XML post conversion
                deleteStatus = xmlFile.delete();
                if (!deleteStatus)
                    LOGGER.severe("Deletion of extracted XML failed.");
            }

        }
    }

    public void generateReport(List<String> packageAttributes, List<String> productAttributes, StepData data) throws IOException {

        List<String> contextList = data.getContext();
        String bu = data.getBusinessUnit();
        List<Product> productList = data.getProducts();
        List<Packaging> packagingList = data.getPackagings();
        String productColumnName = null;
        String packageColumnName = null;

        if(productAttributes != null) {

            if (productAttributes.contains("ID")) {
                productAttributes.remove("ID");
            }

            productColumnName = "ID|" + productAttributes.toString().replaceAll(",", "|");

            for (String context : contextList) {
                String fileName = "WeeklyReport_Products_" + bu + "_" + context + ".csv";
                File file = new File(fileName);
                Files.write(Paths.get(outputFolderPath + "/" + fileName), (productColumnName.toString() + System.lineSeparator()).getBytes());
            }

            for (Product product : productList) {
                String id = product.getId();
                HashMap name = product.getName();
                HashMap<String, ArrayList> productCrossReference = product.getProductCrossReference();
                HashMap<String, ArrayList> assetCrossReference = product.getAssetCrossReference();
                HashMap<String, ArrayList> classification = product.getClassificationReference();
                HashMap values = product.getValues();

                for (String context : contextList) {
                    String fileName = "WeeklyReport_Products_" + bu + "_" + context + ".csv";
                    String fileContent = id;
                    for (String attri : productAttributes
                            ) {
                        if(attri.equals("Name")) {
                            fileContent = fileContent + "|" + name.get(context);
                        } else if (values.get(attri) != null) {
                            if (values.get(attri) instanceof HashMap) {
                                HashMap temp = (HashMap) values.get(attri);
                                fileContent = fileContent + "|" + temp.get(context);
                            } else {
                                fileContent = fileContent + "|" + values.get(attri).toString();
                            }
                        } else if (classification.get(attri) != null) {
                            fileContent = fileContent + "|" + classification.get(attri).toString();
                        } else if (assetCrossReference.get(attri) != null) {
                            fileContent = fileContent + "|" + assetCrossReference.get(attri).toString();
                        } else if (productCrossReference.get(attri) != null) {
                            fileContent = fileContent + "|" + productCrossReference.get(attri).toString();
                        } else {
                            fileContent = fileContent + "|";
                        }
                    }
                    Files.write(Paths.get(outputFolderPath + "/" + fileName), (fileContent + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
                }

            }
        }
        if(packageAttributes != null) {
            if (packageAttributes.contains("ID")) {
                packageAttributes.remove("ID");
            }

            packageColumnName = "ID|" + productAttributes.toString().replaceAll(",", "|");

            for (String context : contextList) {
                String fileName = "WeeklyReport_Packaging_" + bu + "_" + context + ".csv";
                File file = new File(fileName);
                Files.write(Paths.get(outputFolderPath + "/" + fileName), (packageColumnName.toString() + System.lineSeparator()).getBytes());
            }


            for (Packaging packaging: packagingList) {
                String id = packaging.getId();
                HashMap name = packaging.getName();

                HashMap<String, ArrayList> classification = packaging.getClassificationReference();
                HashMap values = packaging.getValues();

                for (String context : contextList) {
                    String fileName = "WeeklyReport_Packaging_" + bu + "_" + context + ".csv";
                    String fileContent = id;
                    for (String attri : packageAttributes) {
                        if(attri.equals("Name")) {
                            fileContent = fileContent + "|" + name.get(context);
                        } else if (values.get(attri) != null) {
                            if (values.get(attri) instanceof HashMap) {
                                HashMap temp = (HashMap) values.get(attri);
                                fileContent = fileContent + "|" + temp.get(context);
                            } else {
                                fileContent = fileContent + "|" + values.get(attri).toString();
                            }
                        } else if (classification.get(attri) != null) {
                            fileContent = fileContent + "|" + classification.get(attri).toString();
                        }  else {
                            fileContent = fileContent + "|";
                        }
                    }
                    Files.write(Paths.get(outputFolderPath + "/" + fileName), (fileContent + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
                }

            }
        }


    }
}
