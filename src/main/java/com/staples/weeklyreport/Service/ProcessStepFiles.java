//package com.staples.weeklyreport.Service;
//
//import com.staples.weeklyreport.Utilities.FileUtilities;
//import com.staples.weeklyreport.Utilities.FilterFiles;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.logging.Logger;
//
//public class ProcessStepFiles {
//
//    static final Logger LOGGER = Logger.getLogger(ProcessStepFiles.class.getName());
//    String outputFolderPath = "/Users/srramasa/Documents/OwnProj/MongoDBReport/Output";
//    String inputFolderPath = "/Users/srramasa/Documents/OwnProj/MongoDBReport/Input";
//    private FileUtilities fileUtility = new FileUtilities();
//
//    public void processXml() throws IOException {
//
//        ParseXml parseObj;
//        File inputFolder = new File(inputFolderPath);
//        if(!inputFolder.exists()) {
//            LOGGER.severe("Invalid Input Folder Location - Job aborted" );
//            System.exit(0);
//        }
//
//        //Check output folder
//        File outputfolder = new File(outputFolderPath);
//        if (!outputfolder.exists()) {
//            LOGGER.severe("Invalid Output Folder Location - Job aborted" );
//            System.exit(0);
//        }
//
//        //Check and create archive folder if needed
//        String inputArchiveFolderPath = inputFolderPath+File.separator+"Archive";
//        File inputArchiveFolder = new File(inputArchiveFolderPath);
//        if(!inputArchiveFolder.exists()) {
//            inputArchiveFolder.mkdir();
//        }
//
//        String outputArchiveFolderPath = outputFolderPath+File.separator+"Archive";
//        File outputArchiveFolder = new File(outputArchiveFolderPath);
//        if(!outputArchiveFolder.exists()) {
//            outputArchiveFolder.mkdir();
//        }
//
//        // Check and create unzip folder if needed
//        String unzipFolderPath = inputArchiveFolderPath + File.separator + "unzip";
//        File unzipFolder = new File(unzipFolderPath);
//        if (!unzipFolder.exists()) {
//            unzipFolder.mkdir();
//        }
//
//        // Filtering zip files - Unzipping, placing XMl in unzip folder & moving zip file to archive folder
//        FilterFiles fileFilter = new FilterFiles(".zip");
//        File[] listOfFiles = inputFolder.listFiles(fileFilter);
//        if (listOfFiles.length == 0) {
//            LOGGER.severe("No zip files exists in the input directory.");
//            System.exit(0);
//        } else {
//            String zipFileName = null;
//            LOGGER.info("Number of ZIP Files:" + listOfFiles.length);
//            LOGGER.info("Unzip in progress.");
//            for (File zipFile : listOfFiles) {
//                zipFileName = zipFile.getName();
//                LOGGER.info("Unzipping file: " + zipFileName);
//                fileUtility.unzip(zipFile.getAbsolutePath(), unzipFolder.getAbsolutePath());
//                //fileUtility.archiveFile(inputArchiveFolderPath, zipFile);
//            }
//        }
//
//        // Filtering XMLs
//        fileFilter = new FilterFiles(".xml");
//        File[] xmlFileList = unzipFolder.listFiles(fileFilter);
//        if (xmlFileList.length == 0) {
//            LOGGER.severe("No XML files available for conversion.");
//            System.exit(0);
//        } else {
//            boolean deleteStatus = false;
//            LOGGER.info("Number of extracted XMLs:" + xmlFileList.length);
//            Arrays.sort(xmlFileList); // Sorting all the XML files
//            String buName = null;
//
//
//            for (File xmlFile : xmlFileList) {
//                LOGGER.info("Processing XML:" + xmlFile.getName());
//                buName = fileUtility.getContextName(xmlFile.getName());
//                System.out.println(buName);
//                String collectionName = buName.split("-")[1];
//                System.out.println("CollectionName : " + collectionName);
//                //parse xml
//                LOGGER.info("XML Parsing - Started");
//                ParseXml xmlParserObject = new ParseXml(xmlFile);
//                LOGGER.info("XML Parsing - Complete");
//                ReportGenerationService ser = new ReportGenerationService();
//                ser.processXml(xmlParserObject,buName);
//
//                //Delete the XML post conversion
//                deleteStatus = xmlFile.delete();
//                if (!deleteStatus)
//                    LOGGER.severe("Deletion of extracted XML failed.");
//            }
//
//        }
//    }
//
//
//}
