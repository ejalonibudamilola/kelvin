package com.osm.gnl.ippms.ogsg.domain.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportStatus {

    private boolean finished;
    private int reportSize;

    public boolean isFinished() {
        return this.finished;
    }

}

