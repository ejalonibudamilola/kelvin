/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.payroll.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

@NoArgsConstructor
@Getter
@Setter
public class IppmsExcelFormatter {

    private String imageFile;
    private Vector<String> mainHeadings;
    private Vector<String> colHeaders;
    private List<String> numericColumns;
    private List<HashMap<String,Object>> valueList;
    private List<HashMap<String,Object>> totalsList;

    public void addToValueList(HashMap<String,Object> addMap){
        if(this.valueList == null)
            this.valueList = new ArrayList<>();
        this.valueList.add(addMap);
    }
}
