/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.customreports.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomRepGenBean;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomRepObjRel;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomReportObjectAttr;
import com.osm.gnl.ippms.ogsg.domain.customreports.ReportFilterObj;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;

import java.time.LocalDate;
import java.util.*;

public class CustomReportGenUtil {
    //@Value("${current_schema}")
    private String CURR_SCHEMA = "gov_ippms_ogs";

    private List<CustomReportObjectAttr> headerObjects = new ArrayList<>();
    private List<CustomReportObjectAttr> filterObjects = new ArrayList<>();
    private List<String> originalTables = new ArrayList<>();
    private String mdaJoinStr;
    private HashMap<Long, CustomRepObjRel> masterMap;
    private boolean excludeSalaryEmployeeJoin;
    boolean salaryCROAdded,employeeCROAdded, paycheckCROAdded,canMergeName,canSelectStatus;

    public CustomRepGenBean createCustomReportBean(GenericService genericService,BusinessCertificate bc,String headersList, String filtersList) throws Exception {
        String str2[] = headersList.split(",");

        boolean sumDoubleFields = false;
        List<CustomReportObjectAttr> tableJoin = new ArrayList<>();
        int row = 0;
        for (String str : Arrays.asList(str2)) {
            CustomReportObjectAttr croa = genericService.loadObjectWithSingleCondition(CustomReportObjectAttr.class, CustomPredicate.procurePredicate("prefDisplayName", CustomReportGenHelper.untreat(str, bc)));
            croa.setColumn(++row);
            if(croa.isBaseNameType()){
                if(!canMergeName)
                    canMergeName = true;
                if(!canSelectStatus)
                    canSelectStatus = true;
            }
            headerObjects.add(croa);
        }

        headerObjects = CustomReportGenHelper.treatList(headerObjects,bc,false,true);
        StringTokenizer stringTokenizer = new StringTokenizer(filtersList, ",");
        String str1;
        while (stringTokenizer.hasMoreTokens()) {
            str1 = stringTokenizer.nextToken();
            StringTokenizer stringTokenizer1 = new StringTokenizer(str1, ":");
            int i = 0;
            boolean usingIn = false;
            boolean usingBetween = false;
            CustomReportObjectAttr croa = null;

            while (stringTokenizer1.hasMoreTokens()) {
                String _str = stringTokenizer1.nextToken();

                if (i == 0) {
                    croa = genericService.loadObjectById(CustomReportObjectAttr.class, Long.valueOf(_str));

                    croa.setReportFilterObj(new ReportFilterObj());
                } else if (i == 1) {
                    croa.getReportFilterObj().setCondition(_str);
                    if (_str.equalsIgnoreCase("in")) {
                        croa.setUsesIn(true);
                        usingIn = true;
                    }
                    if (_str.equalsIgnoreCase("between")) {
                        croa.setUsesBetween(true);
                        usingBetween = true;
                    }
                    if(_str.equalsIgnoreCase("less than" ) || _str.equalsIgnoreCase("greater than")){
                        croa.setUsesGreaterOrLessThan(true);
                    }
                } else if (i == 2) {
                    if (usingIn) {
                        croa = setValuesForIn(croa, _str);

                        break;
                    }
                    if (usingBetween) {
                        String value = setValuesForBetween(_str);
                        croa.getReportFilterObj().setValue1(value);

                        break;
                    }
                    croa.getReportFilterObj().setValue1(_str);
                } else if (i == 3) {
                    croa.getReportFilterObj().setValue2(_str);
                }
                i++;
            }
            filterObjects.add(croa);
        }

        for (CustomReportObjectAttr c : filterObjects) {
            if (c.isUsesBetween() && c.getColumnType().equalsIgnoreCase("LocalDate")) {
                String[] value1 = ((String) c.getReportFilterObj().getValue1()).split("and");
                LocalDate from = PayrollBeanUtils.setDateFromStringCustomReporter(value1[0]);
                LocalDate to = PayrollBeanUtils.setDateFromStringCustomReporter(value1[1]);
                if (to.getMonthValue() != from.getMonthValue()) {
                    c.setMustSumDoubleFields(true);
                    sumDoubleFields = true;
                    break;
                }
            }else if(c.isUsesGreaterOrLessThan() && c.getColumnType().equalsIgnoreCase("LocalDate")){
                c.setMustSumDoubleFields(true);
                sumDoubleFields = true;
                break;
            }

        }
        List<String> headers = new ArrayList<>();
        List<CustomReportObjectAttr> orderList = new ArrayList<>();
        Set<String> tables = new HashSet<>();
        originalTables = new ArrayList<>();
        //originalTables.add("ippms_mda_dept_map");

        String selectStr = "select ";
        String _as = "val";
        String fromStr = "";
        String groupByClause = " Group By ";
        String orderBy = "";
        boolean groupBy = false;
        HashMap<String, String> addendum = new HashMap<>();
        String addendums;
        List<String> groupByFields = new ArrayList<>();
        int i = 0;
        String tableName;
        HashMap<Integer,String> orderMap = new HashMap<>();
        boolean addBankBranchTable = false;
        boolean maritalStatusSelected = false;
        boolean hireTableCROAdded = false;

        for (CustomReportObjectAttr c : headerObjects) {

            headers.add(c.getPrefDisplayName());
            addendums = _as + i++;
            if (c.getColumnType().equalsIgnoreCase("Double") && sumDoubleFields)
                selectStr += "sum(" + c.getCustomReportObject().getAka() + "." + c.getAttrName() + ") as " + addendums + ", ";
            else {
                if (!c.isNameType()) {
                    selectStr += c.getCustomReportObject().getAka() + "." + c.getAttrName() + " as " + addendums + ", ";
                } else {
                    selectStr += c.getNameTypeColumn() + " as " + addendums + ", ";

                }
                if (sumDoubleFields){
                    if(!c.isNameType())
                        groupByFields.add(c.getCustomReportObject().getAka() + "." + c.getAttrName());
                    else
                        groupByFields.add(c.getSplitNameTypeColumns());
                }

            }
            orderMap.put(i,addendums);
            addendum.put(addendums, c.getColumnType());
            if (c.getCustomReportObject().isAbstractTable()) {

                if (c.getCustomReportObject().getTableName().equalsIgnoreCase(IConstants.ABSTRACT_PAYCHECK))
                    paycheckCROAdded = true;
                tableName = CustomReportGenHelper.getAbstractTableName(bc, c.getCustomReportObject().getTableName());
            }else
                tableName = c.getCustomReportObject().getTableName();
            if (!tables.contains(tableName + " " + c.getCustomReportObject().getAka())) {
                tables.add(tableName + " " + c.getCustomReportObject().getAka());
                originalTables.add(tableName);
                tableJoin.add(c);
                if(tableName.equalsIgnoreCase(IConstants.HIRE_INFO_TABLE))
                   hireTableCROAdded = true;
                else if(tableName.equalsIgnoreCase(IConstants.SAL_INFO_TABLE))
                    salaryCROAdded = true;
                else if(tableName.equalsIgnoreCase(IConstants.EMP_INFO_TABLE))
                    employeeCROAdded = true;


            }
            if(c.isOrderable())
                orderList.add(c);

            if (c.getPrefDisplayName().equalsIgnoreCase("Bank Name") ||
                    c.getPrefDisplayName().equalsIgnoreCase("BVN") ||
                    c.getPrefDisplayName().equalsIgnoreCase("Account Number")) {
                if(!addBankBranchTable){
                    addBankBranchTable = true;
                    if (!tables.contains(IConstants.BANK_BRANCH_TABLE+" "+IConstants.BANK_BRANCH_AKA)) {
                        tables.add(IConstants.BANK_BRANCH_TABLE + " " + IConstants.BANK_BRANCH_AKA);
                        originalTables.add(IConstants.BANK_BRANCH_TABLE);
                    }
                    if(!tables.contains(IConstants.BANK_INFO_TABLE+" "+IConstants.BANK_INFO_AKA)) {
                        tables.add(IConstants.BANK_INFO_TABLE + " " + IConstants.BANK_INFO_AKA);
                        originalTables.add(IConstants.BANK_INFO_TABLE);
                    }
                }

            }
            //account for Marital Status...
            if (c.getPrefDisplayName().equalsIgnoreCase("Marital Status")) {
                maritalStatusSelected = true;
                if(!tables.contains(IConstants.HIRE_INFO_TABLE+" "+IConstants.HIRE_INFO_AKA)){
                    tables.add(IConstants.HIRE_INFO_TABLE+" "+IConstants.HIRE_INFO_AKA);
                    originalTables.add(IConstants.HIRE_INFO_TABLE);
                 }

            }

        }

        if(IppmsUtils.isNotNullOrEmpty(orderList)){
            Collections.sort(orderList,Comparator.comparing(CustomReportObjectAttr::getOrderPosition));
            int x = 0;
            for(CustomReportObjectAttr c : orderList){
                x += 1;
                if(c.isNameType()){
                    orderBy += c.getSplitNameTypeColumns();
                }else{
                    orderBy += c.getCustomReportObject().getAka()+"."+c.getAttrName();
                }

                if(x > 1 || x < orderList.size())
                    orderBy += ",";


            }
        }


      //  System.out.println("Select Str here is " + selectStr);

        selectStr = selectStr.replaceAll(", $", " ");
     //   System.out.println("Select Str is now " + selectStr);
        boolean usePaycheckTable = false;
         for (CustomReportObjectAttr c : filterObjects) {
            if (c.getCustomReportObject().isAbstractTable())
                tableName = CustomReportGenHelper.getAbstractTableName(bc, c.getCustomReportObject().getTableName());
            else
                tableName = c.getCustomReportObject().getTableName();

            if (!tables.contains(tableName + " " + c.getCustomReportObject().getAka())) {
                tables.add(tableName + " " + c.getCustomReportObject().getAka());
                originalTables.add(tableName);
                tableJoin.add(c);
            }
            if(c.getPrefDisplayName().equalsIgnoreCase("Pay Period")){
                usePaycheckTable = true;
            }

        }
        if(!usePaycheckTable && addBankBranchTable){
            //this means we need to add Payment Information Method Info
            if(!tables.contains(IConstants.PAY_INFO_TABLE+" "+IConstants.PAY_INFO_AKA))
               tables.add(IConstants.PAY_INFO_TABLE+" "+IConstants.PAY_INFO_AKA);
        }
        StringBuilder all = makeJoinMap(tableJoin, genericService);

        if(addBankBranchTable){

               all.append(" and ");


            if(usePaycheckTable){
                all.append("p.branch_inst_id = "+IConstants.BANK_BRANCH_AKA+".branch_inst_id");

            }else{
                all.append(IConstants.PAY_INFO_AKA+".branch_inst_id = "+IConstants.BANK_BRANCH_AKA+".branch_inst_id");
            }
            all.append(" and ");
            all.append("b.bank_inst_id = "+IConstants.BANK_BRANCH_AKA+".bank_inst_id");
            all.append(" and ");
        }
        if(maritalStatusSelected && !hireTableCROAdded){

            all.append("h.marital_status_inst_id = m.marital_status_inst_id");
            all.append(" and ");
        }
        tables = accountForMdaInfo(bc, tables, tableJoin, filterObjects, headerObjects);
        all = accountForAbstractObjects(all, tableJoin);
        fromStr += "from ";
        for (String d : tables) {
            fromStr += CURR_SCHEMA+"." + d + ",";
        }
        fromStr = fromStr.substring(0, fromStr.lastIndexOf(","));
        String whereClause = "where ";
        whereClause += all.toString() + "" + mdaJoinStr + " ";


        //Do we treat the MDAs here?


        for (CustomReportObjectAttr c : filterObjects) {
            whereClause += CustomReportGenHelper.generateSqlCondition(c);

        }

        whereClause += " and ";
        for (CustomReportObjectAttr c : tableJoin) {
            if (c.getCustomReportObject().isUseClientId()) {
                whereClause += c.getCustomReportObject().getAka() + ".business_client_inst_id = " + bc.getBusinessClientInstId();
                break;
            }
        }
        excludeSalaryEmployeeJoin = this.salaryCROAdded && this.employeeCROAdded && this.paycheckCROAdded;
        if(excludeSalaryEmployeeJoin){
            whereClause = whereClause.replaceAll("and e.salary_info_inst_id = si.salary_info_inst_id ","");
        }
        if (IppmsUtils.isNotNullOrEmpty(groupByFields)) {
            groupBy = true;
            int total = groupByFields.size();
            int x = 0;
            for (String s : groupByFields) {
                x += 1;
                if (x == total)
                    groupByClause += (s);
                else
                    groupByClause += (s + ",");
            }
        }

        selectStr = selectStr.replaceAll("and $", "");

        String finalSql = selectStr + " " + fromStr + " " + whereClause ;
        if(groupBy)
            finalSql +=  " " + groupByClause;
        else
            groupByClause = null;


        if(IppmsUtils.isNotNullOrEmpty(orderBy)){
            orderBy = orderBy.substring(0,orderBy.lastIndexOf(","));
            orderBy = " order by "+orderBy;
            finalSql += orderBy;
        }
        //System.out.println("Final SQL = "+finalSql);
        CustomRepGenBean customRepGenBean = new CustomRepGenBean();
        customRepGenBean.setActiveInactive(canSelectStatus);
        customRepGenBean.setCanMergeNames(canMergeName);
        customRepGenBean.setSqlStr(finalSql);
        customRepGenBean.setOrderMap(orderMap);
        customRepGenBean.setFieldsSummed(sumDoubleFields);
        customRepGenBean.setFromStr(fromStr);
        customRepGenBean.setGroupByClause(groupByClause);
        customRepGenBean.setSelectStr(selectStr);
        customRepGenBean.setWhereClause(whereClause);
        customRepGenBean.setOrderByStr(orderBy);
        customRepGenBean.setTablesList(originalTables);
        customRepGenBean.setHeaderObjects(headerObjects);
        customRepGenBean.setFilterObjects(filterObjects);
        customRepGenBean.setAliasesMap(addendum);
        return  customRepGenBean;
    }

    private List<String> accountForBankInfo(BusinessCertificate bc, List<String> tables, List<CustomReportObjectAttr> tableJoin, List<CustomReportObjectAttr> filterObjects, List<CustomReportObjectAttr> headerObjects) {
        CustomReportObjectAttr filterObj = null;
        boolean proceed = false;
        for (CustomReportObjectAttr c : filterObjects) {
            if (c.getPrefDisplayName().equalsIgnoreCase("Bank Name") ||
                    c.getPrefDisplayName().equalsIgnoreCase("BVN") ||
                    c.getPrefDisplayName().equalsIgnoreCase("Account Number")) {
                //This means we are filtering by MDA.
                proceed = true;
                break;
            }
        }
        if(!proceed)
            return tables;

        boolean mdaSet = false;
        if(filterObj != null) {
            for (CustomReportObjectAttr c : tableJoin) {
                if (c.getCustomReportObject().isDependentEntity()) {
                    String tableName = CustomReportGenHelper.getAbstractParentTable(bc, c.getCustomReportObject().getTableName());
                    if (!tables.contains(tableName)) {
                        tables.add(tableName);

                    } else {
                        if (!tables.contains("ippms_mda_dept_map mdm")) {
                            tables.add("ippms_mda_dept_map mdm");

                            if(!tables.contains("ippms_mda_info mi"))
                                tables.add("ippms_mda_info mi");

                            mdaJoinStr = tableName.substring(tableName.indexOf(" ")) + ".mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
                            mdaSet = true;
                            break;
                        }
                    }

                } else {
                    //Check if this guy is related to the MDA Dept Map.
                    CustomRepObjRel customRepObjRel = this.masterMap.get(c.getCustomReportObject().getId());
                    if (customRepObjRel != null) {
                        if (customRepObjRel.getChildCol().equalsIgnoreCase("mda_dept_map_inst_id")) {
                            if (!tables.contains("ippms_mda_dept_map mdm")) {
                                tables.add("ippms_mda_dept_map mdm");

                                if(!tables.contains("ippms_mda_info mi"))
                                    tables.add("ippms_mda_info mi");

                                mdaJoinStr = c.getCustomReportObject().getAka() + ".mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
                                mdaSet = true;
                                break;
                            }
                        }
                    }
                }

            }
        }else{
            //filter Objects does not contain MDA Name...check if header type does...
            for (CustomReportObjectAttr c : headerObjects) {
                CustomRepObjRel customRepObjRel = this.masterMap.get(c.getCustomReportObject().getId());
                if(customRepObjRel == null)
                    continue;
                else
                {
                    if (customRepObjRel.getChildCol().equalsIgnoreCase("mda_dept_map_inst_id")) {
                        if (!tables.contains("ippms_mda_dept_map mdm")) {
                            tables.add("ippms_mda_dept_map mdm");

                            if(!tables.contains("ippms_mda_info"))
                                tables.add("ippms_mda_info mi");

                            mdaJoinStr = c.getCustomReportObject().getAka() + ".mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
                            mdaSet = true;
                            break;
                        }
                    }
                }
            }
        }
        if(!mdaSet){
            //Set it Manually.
            if(tables.contains("ippms_paychecks_info p") || tables.contains("ippms_paychecks_lga_info p")
                    || tables.contains("ippms_paychecks_pension_info p") || tables.contains("ippms_paychecks_subeb_info p")
                    || tables.contains("ippms_paychecks_blgp_info p")){
                if (!tables.contains("ippms_mda_dept_map mdm"))
                    tables.add("ippms_mda_dept_map mdm");
                if(!tables.contains("ippms_mda_info mi"))
                    tables.add("ippms_mda_info mi");
                mdaJoinStr = "p.mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
            }else if(tables.contains("ippms_employee e")){
                if (!tables.contains("ippms_mda_dept_map mdm"))
                    tables.add("ippms_mda_dept_map mdm");
                if(!tables.contains("ippms_mda_info"))
                    tables.add("ippms_mda_info mi");
                mdaJoinStr = "e.mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
            }else  if(tables.contains("ippms_pensioner pen")){
                if (!tables.contains("ippms_mda_dept_map mdm"))
                    tables.add("ippms_mda_dept_map mdm");
                if(!tables.contains("ippms_mda_info mi"))
                    tables.add("ippms_mda_info mi");
                mdaJoinStr = "pen.mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
            }else  if(tables.contains("ippms_hire_info h")){
                if (!tables.contains("ippms_mda_dept_map mdm"))
                    tables.add("ippms_mda_dept_map mdm");
                if(!tables.contains("ippms_mda_info mi"))
                    tables.add("ippms_mda_info mi");
                mdaJoinStr = "h.mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
            }
        }
        return tables;
    }


    private StringBuilder accountForAbstractObjects(StringBuilder all, List<CustomReportObjectAttr> tableJoin) {

        if(!all.toString().endsWith(" and ") && all.toString().trim().length() > 0)
            all.append(" and ");
        for(CustomReportObjectAttr c : tableJoin){
            if(c.getCustomReportObject().isDependentEntity()){
                all.append("p.paychecks_inst_id = "+c.getCustomReportObject().getAka()+".paycheck_inst_id");
                all.append(" and ");
            }
        }

        return all;
    }

    private Set<String> accountForMdaInfo(BusinessCertificate bc, Set<String> tables, List<CustomReportObjectAttr> tableJoin,
                                           List<CustomReportObjectAttr> filterObjects, List<CustomReportObjectAttr> headerObjects) throws Exception {

        CustomReportObjectAttr filterObj = null;
        CustomRepObjRel customRepObjRel;
        for (CustomReportObjectAttr c : filterObjects) {
            if (c.getDefDisplayName().equalsIgnoreCase("MDA Name")) {
                //This means we are filtering by MDA.
                filterObj = c;
                break;
            }
        }
        boolean mdaSet = false;
        if(filterObj != null) {
            for (CustomReportObjectAttr c : tableJoin) {
                if (c.getCustomReportObject().isDependentEntity()) {
                    String tableName = CustomReportGenHelper.getAbstractParentTable(bc, c.getCustomReportObject().getTableName());
                    if (!tables.contains(tableName)) {
                        tables.add(tableName);

                    } else {
                        if (!tables.contains("ippms_mda_dept_map mdm")) {
                            tables.add("ippms_mda_dept_map mdm");

                            if(!tables.contains("ippms_mda_info mi"))
                                tables.add("ippms_mda_info mi");

                            mdaJoinStr = tableName.substring(tableName.indexOf(" ")) + ".mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
                            mdaSet = true;
                            break;
                        }
                    }

                } else {
                    //Check if this guy is related to the MDA Dept Map.

                       customRepObjRel = this.masterMap.get(c.getCustomReportObject().getId());

                    if (customRepObjRel != null) {
                        if (customRepObjRel.getChildCol().equalsIgnoreCase("mda_dept_map_inst_id")) {
                            if (!tables.contains("ippms_mda_dept_map mdm")) {
                                tables.add("ippms_mda_dept_map mdm");

                                if(!tables.contains("ippms_mda_info mi"))
                                    tables.add("ippms_mda_info mi");

                                mdaJoinStr = c.getCustomReportObject().getAka() + ".mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
                                mdaSet = true;
                                break;
                            }
                        }
                    }
                }

            }
        }else{
            //filter Objects does not contain MDA Name...check if header type does...
            for (CustomReportObjectAttr c : headerObjects) {
                  customRepObjRel = this.masterMap.get(c.getCustomReportObject().getId());
                if(customRepObjRel == null)
                    continue;
                else
                {
                    if (customRepObjRel.getChildCol().equalsIgnoreCase("mda_dept_map_inst_id")) {
                        if (!tables.contains("ippms_mda_dept_map mdm")) {
                            tables.add("ippms_mda_dept_map mdm");

                            if(!tables.contains("ippms_mda_info mi"))
                                tables.add("ippms_mda_info mi");

                            mdaJoinStr = c.getCustomReportObject().getAka() + ".mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
                            mdaSet = true;
                            break;
                        }
                    }
                }
            }
        }
        if(!mdaSet){
            //Set it Manually.
            if(tables.contains("ippms_paychecks_info p") || tables.contains("ippms_paychecks_lga_info p")
                    || tables.contains("ippms_paychecks_pension_info p") || tables.contains("ippms_paychecks_subeb_info p")
                    || tables.contains("ippms_paychecks_blgp_info p")){
                if (!tables.contains("ippms_mda_dept_map mdm"))
                    tables.add("ippms_mda_dept_map mdm");
                if(!tables.contains("ippms_mda_info mi"))
                    tables.add("ippms_mda_info mi");
                mdaJoinStr = "p.mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
            }else if(tables.contains("ippms_employee e")){
                if (!tables.contains("ippms_mda_dept_map mdm"))
                    tables.add("ippms_mda_dept_map mdm");
                if(!tables.contains("ippms_mda_info mi"))
                    tables.add("ippms_mda_info mi");
                mdaJoinStr = "e.mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
            }else  if(tables.contains("ippms_pensioner pen")){
                if (!tables.contains("ippms_mda_dept_map mdm"))
                    tables.add("ippms_mda_dept_map mdm");
                if(!tables.contains("ippms_mda_info mi"))
                    tables.add("ippms_mda_info mi");
                mdaJoinStr = "pen.mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
            }else  if(tables.contains("ippms_hire_info h")){
                if (!tables.contains("ippms_mda_dept_map mdm"))
                    tables.add("ippms_mda_dept_map mdm");
                if(!tables.contains("ippms_mda_info mi"))
                    tables.add("ippms_mda_info mi");
                mdaJoinStr = "h.mda_dept_map_inst_id = mdm.mda_dept_map_inst_id and mdm.mda_inst_id = mi.mda_inst_id ";
            }
        }
        return tables;
    }


    private StringBuilder makeJoinMap(List<CustomReportObjectAttr> tableJoin, GenericService genericService) {

        HashMap<Long, HashMap<Long, CustomRepObjRel>> relations = makeRelationsMap(tableJoin, genericService);

        Set<Long> keys = relations.keySet();
        StringBuilder stringBuilder = new StringBuilder();
        for (CustomReportObjectAttr c : tableJoin) {
            if (keys.contains(c.getCustomReportObject().getId())) {
                Map<Long, CustomRepObjRel> map = relations.get(c.getCustomReportObject().getId());

                for (Long l : map.keySet()) {
                    CustomRepObjRel customRepObjRel = map.get(l);
                    stringBuilder = buildString(stringBuilder, customRepObjRel);
                }
            }
        }
        //if we get here...make sure StringBuffer is not empty first....
        if(stringBuilder.toString().indexOf("and") != -1)
            stringBuilder = new StringBuilder(stringBuilder.toString().substring(0,stringBuilder.lastIndexOf("and ") - 1 ));
        return stringBuilder;
    }

    private StringBuilder buildString(StringBuilder stringBuilder, CustomRepObjRel customRepObjRel) {
        stringBuilder.append(customRepObjRel.getMasterCustomRepObj().getAka() + "." + customRepObjRel.getMasterCol() + " = " +
                customRepObjRel.getChildCustomRepObj().getAka() + "." + customRepObjRel.getChildCol() + " and ");

        //stringBuilder.re

        return stringBuilder;
    }

    private HashMap<Long, HashMap<Long, CustomRepObjRel>> makeRelationsMap(List<CustomReportObjectAttr> tableJoin, GenericService genericService) {
        HashMap<Long, HashMap<Long, CustomRepObjRel>> wRetMap = new HashMap<>();

         masterMap = new HashMap<>();

        for (CustomReportObjectAttr objectAttr : tableJoin) {
            List<CustomRepObjRel> masters =  genericService.loadAllObjectsWithSingleCondition(CustomRepObjRel.class,   CustomPredicate.procurePredicate("masterCustomRepObj.id", objectAttr.getCustomReportObject().getId()), null);
            List<CustomRepObjRel> children = genericService.loadAllObjectsWithSingleCondition(CustomRepObjRel.class,   CustomPredicate.procurePredicate("childCustomRepObj.id", objectAttr.getCustomReportObject().getId()), null);

            for (CustomRepObjRel c : masters) {

                if(originalTables.contains(c.getChildCustomRepObj().getTableName())){

                    masterMap.put(objectAttr.getCustomReportObject().getId(), c);

                    if (wRetMap.containsKey(c.getMasterCustomRepObj().getId())) {
                        //The key is equivalent to the CustomReportObject.id
                        wRetMap.get(c.getMasterCustomRepObj().getId()).put(c.getChildCustomRepObj().getId(), c);
                    } else {
                        HashMap<Long, CustomRepObjRel> innerMap = new HashMap<>();
                        //The key is also equivalent to the CustomReportObject.id
                        innerMap.put(c.getChildCustomRepObj().getId(), c);
                        wRetMap.put(c.getMasterCustomRepObj().getId(), innerMap);
                    }
                }
            }

            for (CustomRepObjRel c : children) {

                if(originalTables.contains(c.getMasterCustomRepObj().getTableName())){
                    if(!masters.contains(objectAttr.getCustomReportObject().getId())){

                        masterMap.put(objectAttr.getCustomReportObject().getId(), c);

                        if (wRetMap.containsKey(c.getMasterCustomRepObj().getId())) {
                            //The key is equivalent to the CustomReportObject.id
                            wRetMap.get(c.getMasterCustomRepObj().getId()).put(c.getChildCustomRepObj().getId(), c);
                        } else {
                            HashMap<Long, CustomRepObjRel> innerMap = new HashMap<>();
                            //The key is also equivalent to the CustomReportObject.id
                            innerMap.put(c.getChildCustomRepObj().getId(), c);
                            wRetMap.put(c.getMasterCustomRepObj().getId(), innerMap);
                        }
                    }

                }
            }
        }
        return wRetMap;
    }
    private CustomReportObjectAttr setValuesForIn(CustomReportObjectAttr customReportObjectAttr, String str) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(str, "_");
        List<Long> list = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            list.add(Long.valueOf(tokenizer.nextToken()));

        }
        customReportObjectAttr.getReportFilterObj().setControlEntityIds(list);
        return customReportObjectAttr;
    }

    private String setValuesForBetween(String str) {
        String[] split = str.split("_");
       // System.out.println(split[0] + " and " + split[1]);
        String value = split[0].trim() + " and " + split[1].trim();

        return value;
    }


}
