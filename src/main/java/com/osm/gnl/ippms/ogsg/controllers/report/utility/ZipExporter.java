package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@Controller
public class ZipExporter extends BaseController {

    @RequestMapping({"/exportZipFileToView.do"})
    public void exportZipFileToView(@RequestParam ("file") String filename, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename="+filename);


//        File currDir = new File(".");
//        String path = currDir.getAbsolutePath();
//        String fileLocation = path.substring(0, path.length() - 1) + filename;

       // File currDir = new File(this.getClass().getClassLoader().getResource(".").getFile()+"/"+filename);
      //  String fileLocation = currDir.getAbsolutePath();
        File currDir = new File(request.getServletContext().getRealPath(File.separator)+filename);
        String fileLocation = currDir.getPath();
        //String fileLocation = path.substring(0, path.length() - 1) +filename;

        FileInputStream baos = new FileInputStream(fileLocation);
        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = baos.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
    }



}
