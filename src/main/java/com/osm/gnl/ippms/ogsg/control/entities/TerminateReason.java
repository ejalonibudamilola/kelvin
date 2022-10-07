package com.osm.gnl.ippms.ogsg.control.entities;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_terminate_reason")
@SequenceGenerator(name = "termReasonSeq", sequenceName = "ippms_terminate_reason_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class TerminateReason  extends AbstractControlEntity
{
	
  private static final long serialVersionUID = -5860721437050027375L;
  
  @Id
  @GeneratedValue(generator = "termReasonSeq", strategy = GenerationType.SEQUENCE)
  @Column(name = "term_reason_inst_id")
  private Long id;
  
  @Column(name = "reason", length = 30, nullable = false)
  private String name;
  
  @Column(name = "reinstate_enabled", columnDefinition = "integer default '0'")
  private int reinstatementInd;

  @Column(name="description", nullable=false)
  private String description;

  /**
   * This indicator will filter a terminated employee from being paid
   * whether he is on contract or not...
   */
  @Column(name="finalized_ind",columnDefinition = "integer default '0'")
  private int finalizedInd;

  
  @Transient
  private boolean notReinstateable;


  public TerminateReason(Long pId) {
	  this.id = pId;
  }


  public boolean isNotReinstateable() {
      this.notReinstateable = this.reinstatementInd == 1;
    return this.notReinstateable;
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