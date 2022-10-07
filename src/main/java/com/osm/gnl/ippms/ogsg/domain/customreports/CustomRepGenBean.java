/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.customreports;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class CustomRepGenBean implements Serializable {

    private List<String> tablesList;
    private boolean fieldsSummed;
    private String selectStr;
    private String fromStr;
    private String whereClause;
    private String groupByClause;
    private List<CustomReportObjectAttr> headerObjects;
    private List<CustomReportObjectAttr> filterObjects;
    private Map<String, String> aliasesMap;
    private String sqlStr;
    private String fileName;
    private boolean activeInactive;
    private boolean canMergeNames;
    private int mergeNameInd;
    private int statusInd;
    private boolean countInd;
    private boolean cancelWarningIssued;
    private boolean useDefInd;
    private String mainHeader;
    private String header1;
    private String header2;
    private String header3;
    private String title;
    private Map<Integer, String> orderMap;
    private String orderByStr;
    private List<Map<String,Object>> resultsList;

}
