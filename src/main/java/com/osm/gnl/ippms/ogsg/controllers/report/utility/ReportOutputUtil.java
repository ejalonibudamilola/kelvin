package com.osm.gnl.ippms.ogsg.controllers.report.utility;

import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ReportOutputUtil extends BaseController implements Runnable{

    private final Workbook workbook;
    private String title;
    private final HttpServletResponse response;
    private final HttpServletRequest request;
    private String url;
    private boolean finished;


    public ReportOutputUtil(Workbook workBook, String sheetTitle, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        this.request = httpServletRequest;
        this.response = httpServletResponse;
        this.title = sheetTitle;
        this.workbook = workBook;
    }

    @SneakyThrows
    @Override
    public void run() {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + title);

        try {

            workbook.write(response.getOutputStream());
            workbook.close();
            response.getOutputStream().close();
            this.finished = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public String getUrl(){
        return  this.url;
    }
}
