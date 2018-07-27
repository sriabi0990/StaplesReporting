package com.staples.weeklyreport.Controller;
import com.staples.weeklyreport.Model.StepData;
import com.staples.weeklyreport.Service.ProcessStepFiles;
import com.staples.weeklyreport.Service.ReportGenerationService;
import com.staples.weeklyreport.Utilities.FileUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:63342")
public class HelloController {
    static final Logger LOGGER = Logger.getLogger(HelloController.class.getName());

    @Autowired
    ReportGenerationService repoService;
    String OUTPUT_FOLDER_PATH = "/Users/srramasa/Documents/OwnProj/MongoDBReport/Output";
    List<String> files = new ArrayList<String>();

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping(value = "/listbusinessunits", method = RequestMethod.GET, produces = "application/json")
    public List<String> getBu() {
        List<String> attributes = repoService.getBu();
        return attributes;
    }
    @RequestMapping(value = "/listattributes/{type}", method = RequestMethod.GET, produces = "application/json")
    public List<String> getAttributes(@PathVariable String type) {
        List<String> attributes = repoService.getAttributes(type);
        return attributes;
    }

    @RequestMapping(value = "/generateReport/{bu}")
    public void generateReport(@PathVariable String bu,
                               @RequestParam(value="productcolumns") List<String> productColumns,
                               @RequestParam(value="packagecolumns") List<String> packageColumns ) throws IOException {

        StepData data = repoService.generateReport(bu);
        repoService.generateReport(packageColumns,productColumns,data);

    }

    @RequestMapping(value = "/processxml")
    public void processXml() throws IOException {

        repoService.processXml();

    }

    @RequestMapping(value="/download/{bu}", method = RequestMethod.GET)
    public ResponseEntity<Resource>  downloadFile(HttpServletResponse response, @PathVariable("bu") String bu) throws IOException {

        File dir = new File(OUTPUT_FOLDER_PATH);
        LOGGER.info("BU:" + bu);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.contains(bu);
            }
        });
        File downloadFile;
        System.out.println(Arrays.toString(files));
        if(files.length == 0) {
            throw new RuntimeException("File not found");
//            String errorMessage = "Sorry. The file you are looking for does not exist";
//            System.out.println(errorMessage);
//            OutputStream outputStream = response.getOutputStream();
//            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
//            outputStream.close();
//            return;
        }
        else if(files.length == 1) {
            downloadFile = files[0];
        }
        else {
            FileUtilities util = new FileUtilities();
            util.zipFiles(OUTPUT_FOLDER_PATH, files, bu);
            downloadFile = new File(OUTPUT_FOLDER_PATH + "/bu.zip");
        }

        Resource resource = new UrlResource(downloadFile.toURI());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFile.getName() + "\"")
                .body(resource);

//        String mimeType= URLConnection.guessContentTypeFromName(downloadFile.getName());
//        if(mimeType==null){
//            System.out.println("mimetype is not detectable, will take default");
//            mimeType = "application/octet-stream";
//        }
//
//        System.out.println("mimetype : "+mimeType);
//
//        response.setContentType(mimeType);
//
//        /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
//            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
//        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + downloadFile.getName() +"\""));
//
//
//        /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
//        //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
//
//        response.setContentLength((int)downloadFile.length());
//
//        InputStream inputStream = new BufferedInputStream(new FileInputStream(downloadFile));
//
//        //Copy bytes from source to destination(outputstream in this example), closes both streams.
//        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }

}
