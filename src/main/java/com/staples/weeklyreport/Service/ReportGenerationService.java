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

    static final Logger LOGGER = Logger.getLogger(ReportGenerationService.class.getName());
    public String propertiesFile = "weeklyreport.properties";
    public String outputFolderPath;
    public String inputFolderPath;
    public String packagingAttributes;
    public String productAttributes;
    public String businessUnits;
    public String bgpFile;
    private final String PACKAGING = "PackagingAttributes";
    private final String PRODUCT = "ProductAttributes";
    private FileUtilities fileUtility = new FileUtilities();
    //private final Path rootLocation = Paths.get(outputFolderPath);

    public ReportGenerationService(){
        try {
            Properties prop = new Properties();
            InputStream input = null;
            input = new FileInputStream(propertiesFile);
            prop.load(input);
            this.outputFolderPath = prop.getProperty("OutputFolderPath");
            this.inputFolderPath = prop.getProperty("InputFolderPath");
            this.businessUnits = prop.getProperty("BusinessUnit");
            this.packagingAttributes = prop.getProperty(PACKAGING);
            this.productAttributes = prop.getProperty(PRODUCT);
            this.bgpFile = prop.getProperty("bgpFile");
        }
        catch (Exception e) {
            LOGGER.severe("Exception while reading prop file");
        }
    }

    public StepData generateReport(String bu) {

        StepData stepData = stepRepo.findByBusinessUnit(bu);
        return stepData;
    }

    public List<String> getBu() {
        List<String> buList = new ArrayList<String>();
        try {
            buList = Arrays.asList(this.businessUnits.split(","));
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return buList;

    }

    public String getBGPReport() {
        String filename = "/Users/srramasa/Documents/OwnProj/mailFile.txt.2018-07-28";
        Boolean htmlcontent = false;
        List<String> content = new ArrayList<String>();
        String lineContents = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String line;
            while ((line = br.readLine()) != null) {

                if(line.contains("Content-Disposition: inline")) {
                    htmlcontent = true;
                    continue;
                }
                if(htmlcontent) {
                    if(line.isEmpty() || line == null || line.length() == 0) {

                    }else {
                        if(line.equals("--FILEBOUNDARY")) {
                            htmlcontent = false;
                        }
                        else {
                            if(line.contains("<b>")) {
                                line = line.replaceAll("td", "th");
                            }
                            content.add(line);
                            lineContents = lineContents+line;
                        }
                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lineContents;
    }


    public List<String> getAttributes(String type) {
        List<String> attributeList = new ArrayList<String>();
        String attributes;
        try {
            LOGGER.info("Type: " +type);

            if(type.equals(PACKAGING)) {

                attributes = this.packagingAttributes;
                LOGGER.info("pack attr " + attributes);
//                attributes = "ID,Name,LENCode,EUCode,LocalCode,PackagingLineLegacy";
                attributeList = Arrays.asList(attributes.split(","));
            }
            if(type.equals(PRODUCT)) {

                attributes = this.productAttributes;
                LOGGER.info("prod attr " + attributes);
//                attributes = "ID,Name,LENCode,ArticleCode,ProductLifeCycleStatus,PrimaryImage,SecondaryImage,PublicationBrand,Packaging_BaseUnit";
                attributeList = Arrays.asList(attributes.split(","));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  attributeList;
    }
    public List<String> listInputFiles(String bu) {

        List<String> files = new ArrayList<String>();
        File inputFolder = new File(inputFolderPath);
        if(!inputFolder.exists()) {
            LOGGER.severe("Invalid Input Folder Location - Job aborted" );
            System.exit(0);
        }
        // Filtering zip files - Unzipping, placing XMl in unzip folder & moving zip file to archive folder
        FilterFiles fileFilter = new FilterFiles(".zip");
        File[] listOfFiles = inputFolder.listFiles(fileFilter);
        if (listOfFiles.length == 0) {
            LOGGER.severe("No zip files exists in the input directory.");
            files = null;
        } else {
            String zipFileName = null;
            LOGGER.info("Number of ZIP Files:" + listOfFiles.length);
            LOGGER.info("Unzip in progress.");
            for (File zipFile : listOfFiles) {
                zipFileName = zipFile.getName();
                if(bu.equals("ALL")) {
                    files.add(zipFileName);
                }
                else {
                    if(bu.equals(fileUtility.getContextName(zipFileName).split("-")[1])) {
                        files.add(zipFileName);
                    }
                }
            }
        }

        return files;
    }
    public void processXml(ParseXml obj, String bu) throws IOException {
        LOGGER.info("Mongo write - Start for BU " + bu);
        StepData dataObj = new StepData();
        dataObj.setBusinessUnit(bu);
        dataObj.setContext(obj.contextList);
//        dataObj.setAssets(obj.assetList);
//        dataObj.setBrands(obj.brandList);
        dataObj.setPackagings(obj.packagingList);
        dataObj.setProducts(obj.productList);
        stepRepo.deleteById(bu);
        stepRepo.insert(dataObj);
        LOGGER.info("Mongo write - Complete");
        }

    public void processXml(String bu) throws IOException {

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
                if(bu.equals("ALL")) {
                    LOGGER.info("Unzipping file: " + zipFileName);
                    fileUtility.unzip(zipFile.getAbsolutePath(), unzipFolder.getAbsolutePath());
                }
                else {
                    if(bu.equals(fileUtility.getContextName(zipFileName).split("-")[1])) {
                        LOGGER.info("Unzipping file: " + zipFileName);
                        fileUtility.unzip(zipFile.getAbsolutePath(), unzipFolder.getAbsolutePath());
                    }
                }
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
                buName = fileUtility.getContextName(xmlFile.getName()).split("-")[1];
                LOGGER.info("File BU : " + buName);

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

    public void runBGPScript() {
        Process p;
        try {
            String[] cmd = { "sh", this.bgpFile};
            p = Runtime.getRuntime().exec(cmd);

//            List<String> cmdList = new ArrayList<String>();
//            // adding command and args to the list
//            cmdList.add("sh");
//            cmdList.add(this.bgpFile);
//            ProcessBuilder pb = new ProcessBuilder(cmdList);
//            p = pb.start();

            BufferedReader reader=new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                LOGGER.info("LINE : " + line);
            }
        }
        catch (Exception e) {
            LOGGER.severe("Process failed");
        }
    }

    public void generateReport(List<String> packageAttributes, List<String> productAttributes, StepData data) throws IOException {

        if(data == null) {
            LOGGER.severe("No data in DB");
        }
        else {

            List<String> contextList = data.getContext();
            String bu = data.getBusinessUnit();
            List<Product> productList = data.getProducts();
            List<Packaging> packagingList = data.getPackagings();
            String productColumnName = null;
            String packageColumnName = null;

            if (productAttributes != null) {

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
                            if (attri.equals("Name")) {
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
            if (packageAttributes != null) {
                if (packageAttributes.contains("ID")) {
                    packageAttributes.remove("ID");
                }

                packageColumnName = "ID|" + productAttributes.toString().replaceAll(",", "|");

                for (String context : contextList) {
                    String fileName = "WeeklyReport_Packaging_" + bu + "_" + context + ".csv";
                    File file = new File(fileName);
                    Files.write(Paths.get(outputFolderPath + "/" + fileName), (packageColumnName.toString() + System.lineSeparator()).getBytes());
                }


                for (Packaging packaging : packagingList) {
                    String id = packaging.getId();
                    HashMap name = packaging.getName();

                    HashMap<String, ArrayList> classification = packaging.getClassificationReference();
                    HashMap values = packaging.getValues();

                    for (String context : contextList) {
                        String fileName = "WeeklyReport_Packaging_" + bu + "_" + context + ".csv";
                        String fileContent = id;
                        for (String attri : packageAttributes) {
                            if (attri.equals("Name")) {
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
                            } else {
                                fileContent = fileContent + "|";
                            }
                        }
                        Files.write(Paths.get(outputFolderPath + "/" + fileName), (fileContent + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
                    }

                }
            }
        }

    }
}
