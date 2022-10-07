/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.NamedEntityLong;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.contribution.domain.CompContEmployeeMap;
import com.osm.gnl.ippms.ogsg.domain.garnishment.AbstractGarnishmentEntity;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.tax.domain.CompanyContributionInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;

import java.util.*;

@NoArgsConstructor
@Getter
@Setter
public class DeductContGarnishBean extends NamedEntityLong {

	
	private Set<CompContEmployeeMap> compContEmpMap;
	private Set<CompanyContributionInfo> compContInfo;
	private Hashtable<Long, CompanyContributionInfo> compEmpHashMap;
	private String contributionAmount;
	private String contributionAmountStr;
	private String contributionPayType;
	private boolean editMode;
	private Set<AbstractDeductionEntity> empDeductInfo;
	private Set<AbstractGarnishmentEntity> empGarnishInfo;
	private List<AbstractDeductionEntity> expDedInfoList;
	private List<AbstractGarnishmentEntity> expLoanList;
	private List<AbstractSpecialAllowanceEntity> expSpecAllowInfo;
	private boolean hasExpDeductions;
	private boolean hasExpLoan;
	private boolean hasExpSpecAllow;
	private HiringInfo hiringInfo;
	 
	 
	private boolean mustDeletePaycheck;
	private String negativeNetPayStr;
	private Long negPayId;
	private Object netDifference;
	private String pensionName;
	private boolean showContributionRow;
	private boolean showNegativeNetPay;
	private boolean showNegativePayMsg;
	private String someName;
	private Set<AbstractSpecialAllowanceEntity> specialAllowanceInfo;


	public void addEmpDeductionInfo(List<AbstractDeductionEntity> p) {
		this.empDeductInfo = new HashSet<>(p);
	}

	public void addEmpGarnishInfo(List<AbstractGarnishmentEntity> p) {
		this.empGarnishInfo = new HashSet<>(p);
	}

	public void addSpecialAllowanceInfo(List<AbstractSpecialAllowanceEntity> pList) {
		this.specialAllowanceInfo = new HashSet<AbstractSpecialAllowanceEntity>(pList);
	}
	public void addCompEmpHashMap(Long pKey, CompanyContributionInfo pCompContInfo) {
		if (this.compEmpHashMap == null) {
			this.compEmpHashMap = new Hashtable<Long, CompanyContributionInfo>();
		}
		this.compEmpHashMap.put(pKey, pCompContInfo);
	}

	public CompanyContributionInfo getCompanyContributionInfo(int id, boolean ignoreNew) {
		for (CompanyContributionInfo e : getCompContInfoInternal()) {
			if (((!ignoreNew) || (!e.isNewEntity())) && (e.getId().intValue() == id)) {
				return e;
			}
		}

		return null;
	}


	public CompContEmployeeMap getCompContEmployeeMap(int id, boolean ignoreNew) {
		for (CompContEmployeeMap e : getCompContEmployeeMapInternal()) {
			if (((!ignoreNew) || (!e.isNewEntity())) && (e.getId().intValue() == id)) {
				return e;
			}
		}

		return null;
	}

	private Collection<CompContEmployeeMap> getCompContEmployeeMapInternal() {
		if (this.compContEmpMap == null) {
			this.compContEmpMap = new HashSet<CompContEmployeeMap>();
		}
		return this.compContEmpMap;
	}

	public List<CompContEmployeeMap> getCompContEmpMap() {
		List<CompContEmployeeMap> sortedCoyContInfo = new ArrayList<CompContEmployeeMap>(
				getCompContEmployeeMapInternal());
		return sortedCoyContInfo;
	}

	public List<CompanyContributionInfo> getCompContInfo() {
		List<CompanyContributionInfo> sortedCoyContInfo = new ArrayList<CompanyContributionInfo>(
				getCompContInfoInternal());
		PropertyComparator.sort(sortedCoyContInfo, new MutableSortDefinition("name", true, true));
		return sortedCoyContInfo;
	}

	private Collection<CompanyContributionInfo> getCompContInfoInternal() {
		if (this.compContInfo == null) {
			this.compContInfo = new HashSet<CompanyContributionInfo>();
		}
		return this.compContInfo;
	}


	public List<AbstractDeductionEntity> getEmpDeductInfo() {
		List<AbstractDeductionEntity> sortedList = new ArrayList<AbstractDeductionEntity>(getEmpDeductInfoInternal());
		PropertyComparator.sort(sortedList, new MutableSortDefinition("name", true, true));
		return sortedList;
	}

	private Collection<AbstractDeductionEntity> getEmpDeductInfoInternal() {
		if (this.empDeductInfo == null) {
			this.empDeductInfo = new HashSet<>();
		}
		return this.empDeductInfo;
	}

	public AbstractDeductionEntity getEmpDeductionInfo(int id) {
		return getEmpDeductionInfo(id, false);
	}

	public AbstractDeductionEntity getEmpDeductionInfo(int id, boolean ignoreNew) {
		for (AbstractDeductionEntity e : getEmpDeductInfoInternal()) {
			if (((!ignoreNew) || (!e.isNewEntity())) && (e.getId().intValue() == id)) {
				return e;
			}
		}

		return null;
	}

	public List<AbstractGarnishmentEntity> getEmpGarnishInfo() {
		List<AbstractGarnishmentEntity> sortedList = new ArrayList<>(getEmpGarnishInfoInternal());
		PropertyComparator.sort(sortedList, new MutableSortDefinition("name", true, true));
		return sortedList;
	}

	private Collection<AbstractGarnishmentEntity> getEmpGarnishInfoInternal() {
		if (this.empGarnishInfo == null) {
			this.empGarnishInfo = new HashSet<>();
		}
		return this.empGarnishInfo;
	}

	public AbstractGarnishmentEntity getEmpGarnishmentInfo(int id) {
		return getEmpGarnishmentInfo(id, false);
	}

	public AbstractGarnishmentEntity getEmpGarnishmentInfo(int id, boolean ignoreNew) {
		for (AbstractGarnishmentEntity e : getEmpGarnishInfoInternal()) {
			if (((!ignoreNew) || (!e.isNewEntity())) && (e.getId().intValue() == id)) {
				return e;
			}
		}

		return null;
	}



	public Set<AbstractSpecialAllowanceEntity> getSpecialAllowanceInfo() {
		List<AbstractSpecialAllowanceEntity> sortedList = new ArrayList<>(getSpecialAllowanceInfoInternal());
		PropertyComparator.sort(sortedList, new MutableSortDefinition("description", true, true));
		return this.specialAllowanceInfo;
	}

	private Collection<AbstractSpecialAllowanceEntity> getSpecialAllowanceInfoInternal() {
		if (this.specialAllowanceInfo == null)
			this.specialAllowanceInfo = new HashSet<AbstractSpecialAllowanceEntity>();
		return this.specialAllowanceInfo;
	}

	public boolean isEditMode() {
		if ((getMode() != null) && (getMode().equalsIgnoreCase("e")))
			this.editMode = true;
		return this.editMode;
	}


}