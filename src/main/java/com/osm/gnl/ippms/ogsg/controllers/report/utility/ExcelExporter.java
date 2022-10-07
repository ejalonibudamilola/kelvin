package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ExcelExporter extends BaseController {

    @RequestMapping({"/exportExcelToView.do"})
    public void exportExcelToView(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String s = (String) getSessionAttribute(request, "title");
        Workbook w = (Workbook) getSessionAttribute(request, "currentWorkBook");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + s);

        w.write(response.getOutputStream());
        w.close();

        response.getOutputStream().close();

        removeSessionAttribute(request,"title");

        removeSessionAttribute(request,"currentWorkBook");

    }
}
