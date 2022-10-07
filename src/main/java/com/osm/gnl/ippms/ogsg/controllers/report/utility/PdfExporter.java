package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;

@Controller
public class PdfExporter extends BaseController {

    @RequestMapping({"/exportPdfToView.do"})
    public void exportPdfToView(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ReportGeneratorBean rt = (ReportGeneratorBean) getSessionAttribute(request, "pdfSession");

        new PdfSimpleGeneratorClass().getPdf(response, request, rt);




//        String title = (String) getSessionAttribute(request, "title");
//        String fileLocation = (String) getSessionAttribute(request, "currentFileLocation");
//
//        response.setHeader("Expires", "0");
//        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
//        response.setHeader("Pragma", "public");
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename="+title);
//
//        FileInputStream baos = new FileInputStream(fileLocation);
//
//        OutputStream os = response.getOutputStream();
//
//        os = response.getOutputStream();
//        byte[] buffer = new byte[8192];
//        int bytesRead;
//
//        while ((bytesRead = baos.read(buffer)) != -1) {
//            os.write(buffer, 0, bytesRead);
//        }
//
//
//        try {
//            os.close();
//        } catch (Exception wEx) {
//
//        }


    }
}
