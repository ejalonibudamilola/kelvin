package com.osm.gnl.ippms.ogsg.domain.report;

import com.osm.gnl.ippms.ogsg.domain.beans.PayRecordBean;
import com.osm.gnl.ippms.ogsg.domain.customreports.CustomReportObjectAttr;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

/**
 * Kasumu Taiwo
 * 12-2020
 */
@NoArgsConstructor
@Getter
@Setter
public class ReportGeneratorBean implements Serializable {

        private List<Map<String, Object>> tableHeaders;
        private List<Map<String, Object>> tableSubHeaders;
        private List<Map<String, Object>> tableData;
        private List<Map<String, Object>> tableSubData;
         private boolean useTableSubData;
        private Map<String, Object> singleTableData;
        private BusinessCertificate businessCertificate;
        private String reportTitle;
        public List<String> mainHeaders;
        public List<String> mainHeaders2;
        private int tableType;
        private String groupBy;
        private String subGroupBy;
        private String doubleSubGroupBy;
        private String watermark;
        private PayRecordBean payBean;
        private EmployeePayMiniBean employeePayMiniBean;
        private boolean outputInd;
        private int dataType;
        private boolean string;
        private boolean integer;
        private boolean decimal;
        private String headerName;
        private int totalInd;
        private DeductionDetailsBean deductionDetailsBean;
        private EmployeePayBean employeePayBean;
        private Set<String> groupedKeySet;
        private Set<String> SubGroupedKeySet;
        private Set<String> doubleSubGroupedKeySet;
        private boolean customReport;
        private boolean cancelWarningIssued;
        private List<CustomReportObjectAttr> headersList;
        private String reportId;
        private boolean showLink;
        private boolean noWrap;
        private LinkedHashMap<String, List<String>> mdaSchoolMap;
        private int unUsedHeaders;
        private boolean checkRotate;


        public ReportGeneratorBean(String  headerName, int totalInd) {
                this.headerName = headerName;
                this.totalInd = totalInd;
        }

        public ReportGeneratorBean(String  headerName, int totalInd, int dataType) {
                this.headerName = headerName;
                this.totalInd = totalInd;
                this.dataType = dataType;
        }

        public boolean isString() {
                string = dataType == 0 ? true : false;
                return string;
        }

        public boolean isInteger() {
                integer = dataType == 1 ? true : false;
                return integer;
        }

        public boolean isDecimal() {
                decimal = dataType == 2 ? true : false;
                return decimal;
        }

}
