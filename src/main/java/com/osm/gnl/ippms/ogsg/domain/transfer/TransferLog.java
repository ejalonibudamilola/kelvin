package com.osm.gnl.ippms.ogsg.domain.transfer;


import com.osm.gnl.ippms.ogsg.abstractentities.AbstractLogEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "ippms_transfer_log")
@SequenceGenerator(name = "transferLogSeq", sequenceName = "ippms_transfer_log_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class TransferLog extends AbstractLogEntity
{
  private static final long serialVersionUID = 4468832724630094314L;
  
  @Id
  @GeneratedValue(generator = "transferLogSeq", strategy = GenerationType.SEQUENCE)
  @Column(name = "transfer_log_inst_id", unique = true, nullable = false)
  private Long id;

  @Column(name = "old_mda", nullable = false, length = 80)
  private String oldMda;
  @Column(name = "new_mda", nullable = false, length = 80)
  private String newMda;
  @Column(name = "employee_name", nullable = false, length = 80)
  private String name;

  @Column(name = "transfer_date" ,nullable = false)
  private LocalDate transferDate;
   
  @Column(name = "obj_ind", columnDefinition="numeric default '0'")
  private int objectInd;

  @Column(name = "business_client_inst_id", nullable = false)
  private Long businessClientId;

  @Transient
  private String specialAuditTime;

  @Transient
  private String transferDateStr;

  @Transient
  protected final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");
  @Transient
  private AbstractEmployeeEntity abstractEmployeeEntity;

  public TransferLog(Long pId) {
	  this.id = pId;
  }
  
  @Override
  public String getAuditTimeStamp()
  {
    if (getTransferDate() != null)
         this.auditTimeStamp = (PayrollHRUtils.getFullDateFormat().format(getTransferDate()) + " " + this.getAuditTime());
    return this.auditTimeStamp;
  }


  public String getTransferDateStr()
  {
    if (getTransferDate() != null)
      this.transferDateStr = (dtf.format(getTransferDate()));
    return this.transferDateStr;
  }
  public AbstractEmployeeEntity getAbstractEmployeeEntity() {
    if(this.abstractEmployeeEntity != null) return this.abstractEmployeeEntity;

    if(this.isPensionerType())
      this.abstractEmployeeEntity = this.getPensioner();
    else
      this.abstractEmployeeEntity =this.getEmployee();
    return abstractEmployeeEntity;
  }
  public boolean isPensionerType(){
    return this.getPensioner() != null && !this.getPensioner().isNewEntity();
  }

@Override
public int compareTo(Object o) {

	return 0;
}
@Override
public boolean isNewEntity() {
	 
	return this.id == null;
}

}