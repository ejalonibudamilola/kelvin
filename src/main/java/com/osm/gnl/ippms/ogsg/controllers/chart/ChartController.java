package com.osm.gnl.ippms.ogsg.controllers.chart;

import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.chart.ChartMiniBean;
import com.osm.gnl.ippms.ogsg.domain.chart.SingleChartBean;
import com.osm.gnl.ippms.ogsg.domain.chart.ChartTableBean;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


public class ChartController extends BaseController {

    public ModelAndView chartReport(SingleChartBean cBean, HttpServletRequest request, Model pModel,
                                    LinkedHashMap<String, String> displayData, ChartMiniBean cMB){
        ModelAndView model = new ModelAndView();
        String tableTitle = displayData.remove("Table Title");
        model.addObject("cBean",cBean);
        model.addObject("tableTitle",tableTitle);
        model.addObject("displayData",displayData);
        model.addObject("cMB",cMB);
        addRoleBeanToModel(pModel, request);
        model.setViewName("chart/singleChartView");
        return model;
    }

    public ModelAndView chartTable(ChartTableBean table, HttpServletRequest request, Model pModel){
        ModelAndView model = new ModelAndView();

        List<LinkedHashMap<String, String>> data = new ArrayList<>();
        LinkedHashMap<String, String> dataMap = null;
        List<LinkedHashMap<String, Object>> tableData;
        tableData = table.getTableData();
        for(Map<String, Object> x : tableData) {
            dataMap = new LinkedHashMap<>();
                for(String key : x.keySet()){
                String value = x.get(key).toString();
                dataMap.put(key, value);
            }
            data.add(dataMap);
        }

        model.addObject("title", table.getTitle());
        model.addObject("tableHeader", table.getHeaders());
        model.addObject("tHeaderSize", table.getHeaders().size());
        model.addObject("tableData", data);
        model.addObject("tDataSize", data.size());
        addRoleBeanToModel(pModel, request);
        model.setViewName("chart/modalDisplay");
        return model;
    }

}
