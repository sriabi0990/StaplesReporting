package com.staples.weeklyreport.Utilities;

import java.io.*;
import java.util.logging.Logger;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

//public class FileUtility {
//
//    static final Logger LOGGER = Logger.getLogger(FileUtility.class.getName());
//
//    public Boolean validateFolders() {
//
//        String inputFolder = "/Users/srramasa/Documents/OwnProj/MongoDBReport/Input";
//        String outputFolder = "/Users/srramasa/Documents/OwnProj/MongoDBReport/Output";
//        String archiveFolder = "/Users/srramasa/Documents/OwnProj/MongoDBReport/Input/Archive";
//
//        if(validateFolder(inputFolder) && validateFolder(outputFolder) && validateFolder(archiveFolder)) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }
//
//    public Boolean validateFolder(String folderPath) {
//        File folder = new File(folderPath);
//        if(!folder.exists()) {
//            LOGGER.severe("Invalid Input Folder Location - Job aborted" );
//            return true;
//        }
//        return true;
//    }
//}



public class FileUtilities {

    static final Logger LOGGER = Logger.getLogger(FileUtilities.class.getName());
    private static final int BUFFER_SIZE = 4096;

    public void unzip(String zipFilePath, String destDirectory){
        LOGGER.info("Entering unzip");
        try {
            File destDir = new File(destDirectory);
            if (!destDir.exists()) {
                destDir.mkdir();
            }
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = destDirectory + File.separator + getContextName(zipFilePath) + "_" + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
        }
        catch (Exception e) {
            FileUtilities.getStackTrace(e);
        }
    }
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public void archiveFile(String archiveFolderPath, File file) {
        boolean renameStatus = false;
        String fileName = file.getName();
        File checkExistance = new File(archiveFolderPath + File.separator + fileName);
        if (checkExistance.exists()) {
            checkExistance.delete();
        }
        // Moving the file to archive folder
        renameStatus = file.renameTo(new File(archiveFolderPath + File.separator + fileName));
        if (!renameStatus) {
            LOGGER.severe("Moving file : "+ fileName+" to archive folder "+ archiveFolderPath + "failed" );
        }
    }

    public void zipFiles(String outputFolder,File[] files, String bu){

        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;
        try {
            File zipfile = new File(outputFolder + "/bu.zip");
            if(zipfile.exists()) {
                zipfile.delete();
            }
            fos = new FileOutputStream(outputFolder + "/bu.zip");
            zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
            for(File input:files){

                fis = new FileInputStream(input);
                ZipEntry ze = new ZipEntry(input.getName());
                LOGGER.info("Zipping the file: "+input.getName());
                zipOut.putNextEntry(ze);
                byte[] tmp = new byte[4*1024];
                int size = 0;
                while((size = fis.read(tmp)) != -1){
                    zipOut.write(tmp, 0, size);
                }
                zipOut.flush();
                fis.close();
            }
            zipOut.close();
            LOGGER.info("Zipped the files");

        } catch (Exception e) {
            LOGGER.severe("Error while zipping files : " +getStackTrace(e));

        } finally{
            try{
                if(fos != null) fos.close();
            } catch(Exception e){
                LOGGER.severe("Error while zipping files : " +getStackTrace(e));
            }
        }

    }

    public String getContextName(String fileName) {
        if (fileName.contains("95425")) {
            return "95425-SEOFR";
        } else if (fileName.contains("95429")) {
            return "954ß29-SEOFR1";
        } else if (fileName.contains("95470")) {
            return "95470-SEOES";
        } else if (fileName.contains("95480")) {
            return "95480-SEOIT";
        } else if (fileName.contains("95485")) {
            return "95485-SEOUK";
        } else if (fileName.contains("95511")) {
            return "95511-SEODE";
        } else if (fileName.contains("96541")) {
            return "96541-SEOPT";
        } else if (fileName.contains("96542")) {
            return "96542-SEONL";
        } else if (fileName.contains("96720")) {
            return "96720-SEANL";
        } else if (fileName.contains("96725")) {
            return "96725-SEAUK";
        } else if (fileName.contains("96730")) {
            return "96730-SEADE";
        } else if (fileName.contains("96775")) {
            return "96775-SEAIT";
        } else if (fileName.contains("96785")) {
            return "96785-SEAFR";
        } else if (fileName.contains("96790")) {
            return "96790-SEAES";
        } else if (fileName.contains("96741")) {
            return "96741-SEAIE";
        } else if (fileName.contains("96763")) {
            return "96763-SEASE";
        } else if (fileName.contains("96770")) {
            return "96770-SEADK";
        } else if (fileName.contains("96752")) {
            return "96752-SEANO";
        } else if (fileName.contains("95465")) {
            return "95465-SEODK";
        } else if (fileName.contains("96773")) {
            return "96773-SEONO";
        } else if (fileName.contains("95460")) {
            return "95460-SEOSE";
        } else if (fileName.contains("96543")) {
            return "96543-SERNL";
        } else if (fileName.contains("96780")) {
            return "96780-SEAPL";
        } else if (fileName.contains("95300")) {
            return "95300-SERDE";
        } else if (fileName.contains("96735")) {
            return "96735-SEAAT";
        } else if (fileName.contains("96751")) {
            return "96751-SERNO";
        } else if (fileName.contains("96753")) {
            return "96753-SEANO1";
        } else if (fileName.contains("96841")) {
            return "96841-SEAFI";
        } else if (fileName.contains("95478")) {
            return "95478-SEPAT";
        } else if (fileName.contains("95479")) {
            return "95479-SEPDE";
        } else if (fileName.contains("95483")) {
            return "95483-SEPCH";
        } else if (fileName.contains("96762")) {
            return "96762-SERSE";
        } else if (fileName.contains("96500")) {
            return "96500-SERPT";
        } else if (fileName.contains("96764")) {
            return "96764-SEASE1";
        } else if (fileName.contains("96772")) {
            return "96772-SEADK1";
        } else if (fileName.contains("96802")) {
            return "96802-InternalEnglish";
        } else {
            return "NNNNN-XXXXX";
        }
    }

    public static String getStackTrace(Exception e)
    {
        StringWriter sWriter = new StringWriter();
        PrintWriter pWriter = new PrintWriter(sWriter);
        e.printStackTrace(pWriter);
        return sWriter.toString();
    }
}


