package com.osm.gnl.ippms.ogsg.domain.customreports;

import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReportFilterObj {

    private String condition;
    private Object value1;
    private Object value2;
    private List<String> controlEntities;
    private List<Long> controlEntityIds;
    private boolean hasControlEntities;

    public boolean isHasControlEntities() {
        hasControlEntities = IppmsUtils.isNotNullOrEmpty(controlEntities);
        return hasControlEntities;
    }
}