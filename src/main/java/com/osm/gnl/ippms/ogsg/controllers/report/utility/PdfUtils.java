package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

public abstract class PdfUtils {


    public static Document makeHeaderFile(BusinessCertificate bc, PdfPCell pdfPCell, Document pdfReport, PdfPTable head) throws IOException, DocumentException {
        File imgDir = ResourceUtils.getFile("classpath:static/images/"+bc.getClientReportLogo());
        String imgPath = imgDir.getAbsolutePath();
        Image img2 = Image.getInstance(imgPath);
        img2.setAlignment(Element.ALIGN_CENTER);
        img2.setWidthPercentage(80);

        pdfPCell.setBorder(Rectangle.NO_BORDER);
        pdfPCell.addElement(img2);
        head.addCell(pdfPCell);
        pdfReport.add(head);
        return  pdfReport;
    }
}
