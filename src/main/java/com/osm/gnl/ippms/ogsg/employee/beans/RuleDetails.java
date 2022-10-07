package com.osm.gnl.ippms.ogsg.employee.beans;

import com.osm.gnl.ippms.ogsg.abstractentities.DependentEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RuleDetails extends DependentEntity
{
  private static final long serialVersionUID = -3432758545240967474L;
  private int applicableObjectInd;
  private boolean deductDevLevy;
  private int monthInd;
  private int yearInd;
  private boolean applyPromotion;
  private boolean applyStepIncrement;
  private Long parentInstId;
  private List<String> applicableObjectList;


}