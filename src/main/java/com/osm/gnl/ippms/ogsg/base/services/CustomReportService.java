package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.domain.customreports.CustomRepGenBean;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomReportObjectAttr;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("customService")
@Repository
@Transactional(readOnly = true)
public class CustomReportService {

    private final GenericService genericService;

    @Autowired
    public CustomReportService(GenericService genericService){
         this.genericService = genericService;
    }

    public  CustomRepGenBean executeCustomReportSql(CustomRepGenBean customRepGenBean) {
        List<Map<String, Object>> wRetVal = new ArrayList<>();

        NativeQuery nativeQuery = makeNativeQuery(customRepGenBean);
        List<Object[]> rows = nativeQuery.list();

        Map<String, Object> wInnerMap;
        Object obj;
        for (Object[] row : rows) {
             wInnerMap = new HashMap<>();
            for (CustomReportObjectAttr c : customRepGenBean.getHeaderObjects()) {
                  obj = row[c.getColumn() - 1];
                  if(c.isNameType()){
                      obj = makeName(obj);
                  }
                if (customRepGenBean.isUseDefInd())
                    wInnerMap.put(c.getDefDisplayName(), obj);
                else
                    wInnerMap.put(c.getPrefDisplayName(), obj);

            }
            wRetVal.add(wInnerMap);
        }

        customRepGenBean.setResultsList(wRetVal);
        return customRepGenBean;
    }

    private static Object makeName(Object obj) {
        StringTokenizer stringTokenizer = new StringTokenizer((String)obj,"%");
        int i = 0;
        String lastName= null,firstName= null,initials = null;
        while(stringTokenizer.hasMoreTokens()) {
            if (i == 0) {
                lastName = stringTokenizer.nextToken();
                i++;
                continue;
            } else if (i == 1){
                firstName = stringTokenizer.nextToken();
            i++;
            continue;
          }else if(i == 2){
                initials = stringTokenizer.nextToken();
                break;
            }
        }
        return PayrollHRUtils.createDisplayName(lastName,firstName,initials);
    }

    private  NativeQuery makeNativeQuery(CustomRepGenBean customRepGenBean) {

        NativeQuery nativeQuery = genericService.getCurrentSession().createNativeQuery(customRepGenBean.getSqlStr());

        List<Integer> integerList = new ArrayList<>(customRepGenBean.getOrderMap().keySet());

        Collections.sort(integerList);

        String addendum;
        for (Integer s : integerList) {
            addendum = customRepGenBean.getOrderMap().get(s);
            nativeQuery = nativeQuery.addScalar(addendum, getType(customRepGenBean.getAliasesMap().get(addendum)));
        }

        return nativeQuery;


    }

    private  Type getType(String s) {

        if (s.equalsIgnoreCase("String"))
            return StandardBasicTypes.STRING;
        else if (s.equalsIgnoreCase("Long"))
            return StandardBasicTypes.LONG;
        else if (s.equalsIgnoreCase("Double"))
            return StandardBasicTypes.DOUBLE;
        else if (s.equalsIgnoreCase("LocalDate"))
            return LocalDateType.INSTANCE;
        else
            return StandardBasicTypes.STRING;
    }


}
