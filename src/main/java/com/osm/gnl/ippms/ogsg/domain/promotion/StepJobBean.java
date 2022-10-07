package com.osm.gnl.ippms.ogsg.domain.promotion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_manual_step_job")
@SequenceGenerator(name = "stepJobSeq", sequenceName = "ippms_manual_step_job_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class StepJobBean  extends AbstractJobBean
{
 
  @Id
  @GeneratedValue(generator = "stepJobSeq", strategy = GenerationType.SEQUENCE)
  @Column(name = "step_job_bean_inst_id", nullable = false, unique = true,updatable = false)
  private Long id;

  public StepJobBean(Long pId) {
	  this.id = pId;
  }

  public boolean isNewEntity() {
	  return this.id == null;
  }
}