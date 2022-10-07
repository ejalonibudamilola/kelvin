package com.osm.gnl.ippms.ogsg.domain.chart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ChartTableBean {
    private String title;
    private List<String> headers;
    private List<LinkedHashMap<String, Object>> tableData;
    private List<String> dataCol1;
    private List<String> dataCol2;
}

