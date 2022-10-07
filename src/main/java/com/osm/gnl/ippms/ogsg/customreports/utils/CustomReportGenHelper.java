/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.customreports.utils;

import com.osm.gnl.ippms.ogsg.domain.customreports.CustomRepGenBean;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomReportObjectAttr;
import com.osm.gnl.ippms.ogsg.domain.report.ReportGeneratorBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;

import java.util.*;

public abstract class CustomReportGenHelper {

    public static List<CustomReportObjectAttr> treatList(List<CustomReportObjectAttr> rList, BusinessCertificate bc, boolean pFilterSchool, boolean pChangeDefDisplayName) {
        List<CustomReportObjectAttr> wRetList = new ArrayList<>();
        for (CustomReportObjectAttr c : rList) {
            //c.setSqlName(c.getPrefDisplayName());
            if (c.getPrefDisplayName().indexOf("MDA") != -1) {
                if (!bc.isCivilService()) {
                    c.setPrefDisplayName(c.getPrefDisplayName().replaceAll("MDA", bc.getMdaTitle()));
                    if (pChangeDefDisplayName)
                        c.setDefDisplayName(c.getDefDisplayName().replaceAll("MDA", bc.getMdaTitle()));
                }
            }
            if (c.getPrefDisplayName().equalsIgnoreCase("Employee Type")) {
                if (!bc.isCivilService()) {
                    c.setPrefDisplayName("Pensioner Type");
                    if (pChangeDefDisplayName)
                        c.setDefDisplayName(c.getPrefDisplayName());
                }

            }
            if (c.getCustomReportObject().isControlEntity()) {
                c.setColumnType("ce");
            }
            if (pFilterSchool)
                if (c.getPrefDisplayName().indexOf("School") != -1)
                    if (bc.isPensioner())
                        continue;


            wRetList.add(c);
        }
        return wRetList;
    }

    public static Comparable untreat(String str, BusinessCertificate bc) {
        if (!bc.isCivilService()) {
            if (str.toUpperCase().indexOf(bc.getMdaTitle()) != -1)
                str = str.replaceAll(bc.getMdaTitle(), "MDA");

            if (str.equalsIgnoreCase("Pensioner Type"))
                str = "Employee Type";
         }



        return str;

    }

    public static String getAbstractParentTable(BusinessCertificate businessCertificate, String pTableName) {

        if (pTableName.equalsIgnoreCase("abstract_spec_allowance") || pTableName.equalsIgnoreCase("abstract_paycheck_garnishment")
                || pTableName.equalsIgnoreCase("abstract_paycheck_deduction")) {
            if (businessCertificate.isCivilService()) {
                return "ippms_paychecks_info p";
            } else if (businessCertificate.isSubeb()) {
                return "ippms_paychecks_subeb_info p";
            } else if (businessCertificate.isStatePension()) {
                return "ippms_paychecks_pension_info p";
            } else if (businessCertificate.isLocalGovtPension()) {
                return "ippms_paychecks_blgp_info p";
            } else if (businessCertificate.isLocalGovt()) {
                return "ippms_paychecks_lg_info p";
            }

        }
        return "";
    }

    public static String getAbstractTableName(BusinessCertificate businessCertificate, String pTableName) {
        if (pTableName.equalsIgnoreCase("abstract_paycheck_info")) {
            if (businessCertificate.isCivilService()) {
                return "ippms_paychecks_info";
            } else if (businessCertificate.isSubeb()) {
                return "ippms_paychecks_subeb_info";
            } else if (businessCertificate.isStatePension()) {
                return "ippms_paychecks_pension_info";
            } else if (businessCertificate.isLocalGovtPension()) {
                return "ippms_paychecks_blgp_info";
            } else if (businessCertificate.isLocalGovt()) {
                return "ippms_paychecks_lg_info";
            }
        } else if (pTableName.equalsIgnoreCase("abstract_paycheck_spec_allow")) {
            if (businessCertificate.isCivilService()) {
                return "ippms_paycheck_spec_allow";
            } else if (businessCertificate.isSubeb()) {
                return "ippms_paycheck_spec_allow_subeb";
            } else if (businessCertificate.isStatePension()) {
                return "ippms_paycheck_spec_allow_pen";
            } else if (businessCertificate.isLocalGovtPension()) {
                return "ippms_paycheck_spec_allow_blgp";
            } else if (businessCertificate.isLocalGovt()) {
                return "ippms_paycheck_spec_allow_lg";
            }

        } else if (pTableName.equalsIgnoreCase("abstract_paycheck_garnishment")) {
            if (businessCertificate.isCivilService()) {
                return "ippms_paycheck_garnishments";
            } else if (businessCertificate.isSubeb()) {
                return "ippms_paycheck_garnishments_subeb";
            } else if (businessCertificate.isStatePension()) {
                return "ippms_paycheck_garnishments_pen";
            } else if (businessCertificate.isLocalGovtPension()) {
                return "ippms_paycheck_garnishments_blgp";
            } else if (businessCertificate.isLocalGovt()) {
                return "ippms_paycheck_garnishments_lg";
            }

        } else if (pTableName.equalsIgnoreCase("abstract_paycheck_deduction")) {

            if (businessCertificate.isCivilService()) {
                return "ippms_paycheck_deductions";
            } else if (businessCertificate.isSubeb()) {
                return "ippms_paycheck_deductions_subeb";
            } else if (businessCertificate.isStatePension()) {
                return "ippms_paycheck_deductions_pension";
            } else if (businessCertificate.isLocalGovtPension()) {
                return "ippms_paycheck_deductions_blgp";
            } else if (businessCertificate.isLocalGovt()) {
                return "ippms_paycheck_deductions_lg";
            }

        }else if(pTableName.equalsIgnoreCase("abstract_garnishment_info")){
            if (businessCertificate.isCivilService()) {
                return "ippms_garnishment_info";
            } else if (businessCertificate.isSubeb()) {
                return "ippms_garnishment_info_subeb";
            } else if (businessCertificate.isLocalGovt()) {
                return "ippms_garnishment_info_lg";
            } else if (businessCertificate.isPensioner()) {
                return "ippms_garnishment_info_pen";
            }
        }else if(pTableName.equalsIgnoreCase("abstract_spec_allow_info")){
            if (businessCertificate.isCivilService()) {
                return "ippms_spec_allow_info";
            } else if (businessCertificate.isSubeb()) {
                return "ippms_spec_allow_info_subeb";
            } else if (businessCertificate.isPensioner()) {
                return "ippms_spec_allow_info_pen";
            }   else if (businessCertificate.isLocalGovt()) {
                return "ippms_spec_allow_info_lg";
            }
        }else if(pTableName.equalsIgnoreCase("abstract_deduction_info")){
            if (businessCertificate.isCivilService()) {
                return "ippms_deduction_info";
            } else if (businessCertificate.isSubeb()) {
                return "ippms_deduction_info_subeb";
            } else if (businessCertificate.isPensioner()) {
                return "ippms_deduction_info_pen";
            }  else if (businessCertificate.isLocalGovt()) {
                return "ippms_deduction_info_lg";
            }
        }
        return "";
    }

    public static String generateSqlCondition(CustomReportObjectAttr c) {
        String whereClause = "";


        if (c.isUsesIn()) {
            whereClause = "and " + c.getCustomReportObject().getAka() + "." + c.getCustomReportObject().getTableId() + " " + c.getReportFilterObj().getCondition() + " " + treatInCondition(c.getReportFilterObj().getControlEntityIds());
        } else if (c.isUsesBetween()) {
            whereClause = " and " + c.getCustomReportObject().getAka() + "." + c.getAttrName() + " " + treatBetweenCondition(c);
        } else if (c.getReportFilterObj().getCondition().equalsIgnoreCase("like")) {
            if(c.isNameType()){
                whereClause = " and upper(" + c.getNameTypeColumn() + ") like '%" + c.getReportFilterObj().getValue1().toString().toUpperCase() + "%'";
            }else{
                whereClause = " and upper(" + c.getCustomReportObject().getAka() + "." + c.getAttrName() + ") like '%" + c.getReportFilterObj().getValue1().toString().toUpperCase() + "%'";
            }

        } else if (c.getReportFilterObj().getCondition().equalsIgnoreCase("greater than")) {
            if (c.getColumnType().equalsIgnoreCase("LocalDate") || c.getColumnType().equalsIgnoreCase("String"))
                whereClause = " and " + c.getCustomReportObject().getAka() + "." + c.getAttrName() + " > '" + c.getReportFilterObj().getValue1() + "'";
            else
                whereClause = " and " + c.getCustomReportObject().getAka() + "." + c.getAttrName() + " > " + c.getReportFilterObj().getValue1();
        } else if (c.getReportFilterObj().getCondition().equalsIgnoreCase("less than")) {
            if (c.getColumnType().equalsIgnoreCase("LocalDate") || c.getColumnType().equalsIgnoreCase("String"))
                whereClause = " and " + c.getCustomReportObject().getAka() + "." + c.getAttrName() + " < '" + c.getReportFilterObj().getValue1() + "'";
            else
                whereClause = " and " + c.getCustomReportObject().getAka() + "." + c.getAttrName() + " < " + c.getReportFilterObj().getValue1();
        } else if (c.getReportFilterObj().getCondition().equalsIgnoreCase("equals")) {
            if (c.getColumnType().equalsIgnoreCase("string") || c.getColumnType().equalsIgnoreCase("LocalDate")) {
                if(c.isNameType()){
                    whereClause = " and trim(upper(" + c.getNameTypeColumn() + ")) = '" + c.getReportFilterObj().getValue1().toString().trim().toUpperCase() + "'";
                }else{
                    if(c.getColumnType().equalsIgnoreCase("string"))
                        whereClause = " and trim(upper(" + c.getCustomReportObject().getAka() + "." + c.getAttrName() + ")) = '" + c.getReportFilterObj().getValue1().toString().trim().toUpperCase() + "'";
                    else
                        whereClause = " and " + c.getCustomReportObject().getAka() + "." + c.getAttrName() + " = '" + c.getReportFilterObj().getValue1() + "'";
                }

            }else
                whereClause = " and " + c.getCustomReportObject().getAka() + "." + c.getAttrName() + " = " + c.getReportFilterObj().getValue1() + "";
        }

        return whereClause;
    }

    private static String treatBetweenCondition(CustomReportObjectAttr reportObjectAttr) {

        String wRetVal = "";
        String[] split = ((String) reportObjectAttr.getReportFilterObj().getValue1()).split("and");

        if (reportObjectAttr.getColumnType().equalsIgnoreCase("LocalDate")) {
            wRetVal = "between '" + split[0].trim() + "' and '" + split[1].trim() + "'";
        } else {
            wRetVal = ">= " + split[0].trim() + " and " + reportObjectAttr.getCustomReportObject().getAka() + "." + reportObjectAttr.getAttrName() + " <= " + split[1].trim() + "";
        }
        return wRetVal;
    }

    private static String treatInCondition(List<Long> longList) {
        StringBuilder wRetVal = new StringBuilder();
        wRetVal.append("(");
        for (Long l : longList) {
            wRetVal.append(l);
            wRetVal.append(",");
        }
        String str = wRetVal.toString();
        str = str.replaceAll(",$", "");
        str += ")";

        return str;
    }
    public static ReportGeneratorBean makeReportGeneratorBean(CustomRepGenBean customRepGenBean, BusinessCertificate businessCertificate) {
        ReportGeneratorBean rt = new ReportGeneratorBean();

        List<ReportGeneratorBean> list = new ArrayList<>();
        String groupBy = null;
        boolean groupByFound = false;
        List<CustomReportObjectAttr> headersList = new ArrayList<>();
        for(CustomReportObjectAttr c : customRepGenBean.getHeaderObjects()){
            if(c.getCustomReportObject() != null){
                if(c.getCustomReportObject().isControlEntity()){
                    if(c.getDefDisplayName().equalsIgnoreCase("Mda Name")){
                        if(!customRepGenBean.isUseDefInd()){
                            groupBy = c.getPrefDisplayName();
                        }else{
                            groupBy = c.getDefDisplayName();
                        }
                        groupByFound = true;
                    }else if (!c.getDefDisplayName().equalsIgnoreCase("Mda Name") && !groupByFound){
                        if(!customRepGenBean.isUseDefInd()){
                            groupBy = c.getPrefDisplayName();
                        }else{
                            groupBy = c.getDefDisplayName();
                        }
                    }
                }
            }
            if(customRepGenBean.isUseDefInd()){
                c.setUsingDef(true);
                if(c.getColumnType().equalsIgnoreCase("Double"))
                    list.add(new ReportGeneratorBean(c.getDefDisplayName(), 2));
                else if(c.getColumnType().equalsIgnoreCase("Long"))
                    list.add(new ReportGeneratorBean(c.getDefDisplayName(), 1));
                else if((groupByFound == true) && (c.getDefDisplayName().equalsIgnoreCase("Mda Name")))
                    list.add(new ReportGeneratorBean(c.getDefDisplayName(), 3));
                else
                    list.add(new ReportGeneratorBean(c.getDefDisplayName(), 0));
            }else{

                if(c.getColumnType().equalsIgnoreCase("Double"))
                    list.add(new ReportGeneratorBean(c.getPrefDisplayName(), 2));
                else if(c.getColumnType().equalsIgnoreCase("Long"))
                    list.add(new ReportGeneratorBean(c.getPrefDisplayName(), 1));
                else if((groupByFound == true) && (c.getDefDisplayName().equalsIgnoreCase("Mda Name")))
                    list.add(new ReportGeneratorBean(c.getPrefDisplayName(), 3));
                else
                    list.add(new ReportGeneratorBean(c.getPrefDisplayName(), 0));
            }
            headersList.add(c);
        }

        Collections.sort(headersList,Comparator.comparing(CustomReportObjectAttr::getOrderPosition));

        HashMap<String, Integer> uniqueSet = new HashMap<>();
        if(groupByFound == true) {
            int i = 1;
            for (Map<String, Object> m : customRepGenBean.getResultsList()) {
                uniqueSet.put((String) m.get(groupBy), i++);
            }
        }

        List<Map<String, Object>> bankHeaders = new ArrayList<>();
        for(ReportGeneratorBean head : list){
            if(head.getTotalInd() != 3) {
                Map<String, Object> mappedHeader = new HashMap<>();
                mappedHeader.put("headerName", head.getHeaderName());
                mappedHeader.put("totalInd", head.getTotalInd());
                bankHeaders.add(mappedHeader);
            }
        }

        for(ReportGeneratorBean head : list){
            if(head.getTotalInd() == 3) {
                Map<String, Object> mappedHeader = new HashMap<>();
                mappedHeader.put("headerName", head.getHeaderName());
                mappedHeader.put("totalInd", head.getTotalInd());
                bankHeaders.add(mappedHeader);
            }
        }



        List<String> mainHeaders = new ArrayList<>();
        mainHeaders.add(customRepGenBean.getMainHeader());
        if(IppmsUtils.isNotNullOrEmpty(customRepGenBean.getHeader1()))
            mainHeaders.add(customRepGenBean.getHeader1());
        if(IppmsUtils.isNotNullOrEmpty(customRepGenBean.getHeader2()))
            mainHeaders.add(customRepGenBean.getHeader2());
        if(IppmsUtils.isNotNullOrEmpty(customRepGenBean.getHeader3()))
            mainHeaders.add(customRepGenBean.getHeader3());


        rt.setCustomReport(true);
        int tableType = 0;
        if(groupBy != null){
            tableType = 1;
            rt.setGroupedKeySet(uniqueSet.keySet());
        }
        rt.setBusinessCertificate(businessCertificate);
        rt.setGroupBy(groupBy);
        rt.setReportTitle(customRepGenBean.getFileName());
        rt.setMainHeaders(mainHeaders);
        rt.setSubGroupBy(null);
        rt.setTableData(customRepGenBean.getResultsList());
        rt.setTableHeaders(bankHeaders);
        rt.setHeadersList(headersList);
        rt.setOutputInd(true);
        rt.setTableType(tableType);

        return rt;
    }
}
