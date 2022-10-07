/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.customreports.utils;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.CustomReportService;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomRepGenBean;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomRepObjRel;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomReportObjectAttr;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public abstract class CustomReportFinalizer {

     public static CustomRepGenBean processReport(CustomRepGenBean customRepGenBean, CustomReportService customReportService, BusinessCertificate businessCertificate,GenericService genericService) throws Exception {


         if(customRepGenBean.isCountInd()){
             makeGroupByClause(customRepGenBean, businessCertificate,genericService);
         }
         if(customRepGenBean.isActiveInactive())
             addActiveInactiveFilter(customRepGenBean,businessCertificate);

         return  customReportService.executeCustomReportSql(customRepGenBean);

     }

    private static void makeGroupByClause(CustomRepGenBean customRepGenBean, BusinessCertificate bc, GenericService genericService) throws Exception {

        String fieldToSum = makeFieldToSum(customRepGenBean, bc,genericService);
         if(customRepGenBean.isFieldsSummed()){
             String val = "val"+(customRepGenBean.getAliasesMap().keySet().size());
             customRepGenBean.setSelectStr(customRepGenBean.getSelectStr()+", "+fieldToSum+" as "+ val);
             customRepGenBean.getAliasesMap().put(val,"Long");
             customRepGenBean.getOrderMap().put(customRepGenBean.getAliasesMap().size(),val);
             CustomReportObjectAttr c = new CustomReportObjectAttr();
             c.setColumnType("Long");
             c.setColumn(customRepGenBean.getAliasesMap().size());
             c.setPrefDisplayName("Count");
             c.setDefDisplayName("Count");
             customRepGenBean.getHeaderObjects().add(c);
         }else {
             HashMap<String, String> addendum = new HashMap<>();
             String addendums = "";
             String _as = "val";
             List<String> groupByFields = new ArrayList<>();
             HashMap<Integer,String> orderMap = new HashMap<>();
             String selectStr = "SELECT ";
             int i = 0;

             for (CustomReportObjectAttr c : customRepGenBean.getHeaderObjects()) {

                 addendums = _as + i++;
                 if (c.getColumnType().equalsIgnoreCase("Double"))
                     selectStr += "sum(" + c.getCustomReportObject().getAka() + "." + c.getAttrName() + ") as " + addendums + ", ";
                 else {
                     selectStr += c.getCustomReportObject().getAka() + "." + c.getAttrName() + " as " + addendums + ", ";
                     groupByFields.add(c.getCustomReportObject().getAka() + "." + c.getAttrName());
                 }

                 addendum.put(addendums, c.getColumnType());
                 orderMap.put(i,addendums);
             }
             selectStr += fieldToSum;


             String countVal = "val" + (addendum.keySet().size());
             addendum.put(countVal, "Long");
             CustomReportObjectAttr c = new CustomReportObjectAttr();

             c.setColumnType("Long");
             c.setColumn(addendum.size());
             c.setPrefDisplayName("Count");
             c.setDefDisplayName("Count");
             customRepGenBean.getHeaderObjects().add(c);
             customRepGenBean.setAliasesMap(addendum);
             customRepGenBean.setSelectStr(selectStr);
             customRepGenBean.setOrderMap(orderMap);
          }

    }
    private static void addActiveInactiveFilter(CustomRepGenBean customRepGenBean, BusinessCertificate businessCertificate) {


         if(customRepGenBean.getStatusInd() != 2){
             if(businessCertificate.isPensioner())
                 customRepGenBean.setWhereClause(customRepGenBean.getWhereClause()+" and p.status_ind = "+customRepGenBean.getStatusInd());
             else
                 customRepGenBean.setWhereClause(customRepGenBean.getWhereClause()+" and e.status_ind = "+customRepGenBean.getStatusInd());
             String finalSql = customRepGenBean.getSelectStr() + " " + customRepGenBean.getFromStr() + " " + customRepGenBean.getWhereClause();
             if(IppmsUtils.isNotNullOrEmpty(customRepGenBean.getGroupByClause() ))
                 finalSql += " " + customRepGenBean.getGroupByClause();
             if(IppmsUtils.isNotNullOrEmpty(customRepGenBean.getOrderByStr()))
                 finalSql += customRepGenBean.getOrderByStr();

             customRepGenBean.setSqlStr(finalSql);
         }
    }


    private static String makeFieldToSum(CustomRepGenBean customRepGenBean, BusinessCertificate bc, GenericService genericService) throws Exception {
          String wRetVal;

        CustomReportObjectAttr customReportObjectAttr = null;
          boolean firstGuy = true;
          for(CustomReportObjectAttr c : customRepGenBean.getHeaderObjects()){
              if(c.getCustomReportObject().getHierarchyInd() <= 8 && firstGuy){
                  customReportObjectAttr = c;
                  firstGuy = false;
              }else{
                 if( c.getCustomReportObject().getHierarchyInd() < c.getCustomReportObject().getHierarchyInd())
                     customReportObjectAttr = c;

              }
          }
          if(customReportObjectAttr == null)
              throw new Exception("Count field could not be determined");
          switch(customReportObjectAttr.getCustomReportObject().getHierarchyInd()){
              case 1: case 4: case 5: case 6: case 7: //Employee
                  wRetVal = "count("+customReportObjectAttr.getCustomReportObject().getAka()+".employee_inst_id)";
                  break;
              case 2:
                  wRetVal = "count("+customReportObjectAttr.getCustomReportObject().getAka()+".pensioner_inst_id)";
                  break;
              case 3:
                  if(bc.isPensioner()){
                      wRetVal = "count("+customReportObjectAttr.getCustomReportObject().getAka()+".pensioner_inst_id)";
                  }else{
                      wRetVal = "count("+customReportObjectAttr.getCustomReportObject().getAka()+".employee_inst_id)";
                  }
                  break;
              default:
                   wRetVal = determineValue(customReportObjectAttr, bc,genericService);

          }

          return  wRetVal;
    }

    private static String determineValue(CustomReportObjectAttr customReportObjectAttr, BusinessCertificate bc,GenericService genericService) {
         String wRetVal;

         List<CustomRepObjRel> customRepObjRelList = genericService.loadAllObjectsUsingRestrictions(CustomRepObjRel.class, Arrays.asList(
                 CustomPredicate.procurePredicate("masterCustomRepObj.id",customReportObjectAttr.getCustomReportObject().getId()),
                 CustomPredicate.procurePredicate("masterCol",bc.getEmployeeTableIdJoinStr())), null);

         if(IppmsUtils.isNotNullOrEmpty(customRepObjRelList)){
             wRetVal = "count("+customReportObjectAttr.getCustomReportObject().getAka()+"."+bc.getEmployeeTableIdJoinStr()+")";
         }else{
             wRetVal = "count("+customReportObjectAttr.getCustomReportObject().getAka()+"."+customReportObjectAttr.getCustomReportObject().getTableId()+")";
         }

         return wRetVal;
    }


}
