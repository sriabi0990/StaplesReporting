package com.staples.weeklyreport.Controller;
import com.staples.weeklyreport.Model.StepData;
import com.staples.weeklyreport.Service.ReportGenerationService;
import com.staples.weeklyreport.Utilities.FileUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

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

    @RequestMapping(value = "/getBGPReport", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getAttributes() {
        String attributes = repoService.getBGPReport();
        return attributes;
    }

    @RequestMapping(value = "/runBGPScript")
    public void runBGPScript() {
        repoService.runBGPScript();
    }

    @RequestMapping(value = "/generateReport/{bu}")
    public void generateReport(@PathVariable String bu,
                               @RequestParam(value="productcolumns") List<String> productColumns,
                               @RequestParam(value="packagecolumns") List<String> packageColumns ) throws IOException {

        StepData data = repoService.generateReport(bu);
        if(data == null) {
            LOGGER.severe("No data in db");
        }
        else
            repoService.generateReport(packageColumns,productColumns,data);

    }

    @RequestMapping(value = "/processxml/{bu}")
    public void processXml(@PathVariable("bu") String bu) throws IOException {

        repoService.processXml(bu);

    }

    @RequestMapping(value = "/listXMLFiles/{bu}")
    public List<String> listInputFiles(@PathVariable("bu") String bu) throws IOException {

        return repoService.listInputFiles(bu);

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
    }

}
