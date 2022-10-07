package com.osm.gnl.ippms.ogsg.validators.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.validators.base.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@Component
public class TransferValidator extends BaseValidator {
    @Autowired
	protected TransferValidator(GenericService genericService) {
		super(genericService);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return HiringInfo.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

	}

	public void validate(Object pTarget, Errors pErrors, BusinessCertificate businessCertificate) {
		HiringInfo wEHB = (HiringInfo) pTarget;
		String wName = "";
		if(wEHB.isPensionerType())
			wName = wEHB.getPensioner().getDisplayNameWivTitlePrefixed();
		else
			wName =  wEHB.getEmployee().getDisplayNameWivTitlePrefixed();

		if (IppmsUtils.isNullOrLessThanOne(wEHB.getMdaId())) {
			pErrors.rejectValue("mdaId", "Required.Value",
					"Please select 'New MDA' to Transfer " +wName);
			return;
		}
		if (IppmsUtils.isNullOrLessThanOne(wEHB.getDepartmentId())) {
			// Check if this MDA has departments attached to it or not.
			List<?> wMappedDepts = genericService.loadAllObjectsWithSingleCondition(MdaDeptMap.class,CustomPredicate.procurePredicate("mdaInfo.id", wEHB.getMdaId()), "name");
			if (wMappedDepts == null || wMappedDepts.isEmpty())
				pErrors.rejectValue("", "No.Dept",
						"There are no departments mapped to this "+businessCertificate.getMdaTitle()+". Please add a department and retry transfer.");
			else
				pErrors.rejectValue("departmentId", "Required.Value",
						"Please select the Department in the MDA to Transfer "
								+ wName);
			return;
		}
		// --Check whether the Guy is going to a School or not
		/**
		 * The Logic is simple. 1. Employee can be in a School Enabled MDA and going to
		 * another school. 2. Employee may not be in a school enabled MDA and the
		 * Employee is going to a School Enabled MDA.
		 * For now restrict to only CASP,SUBEB and LG.
		 */

		if(!businessCertificate.isPensioner()) {
			MdaInfo wMdaInfo = new MdaInfo();
			try {
				wMdaInfo = genericService.loadObjectById(MdaInfo.class, wEHB.getMdaId());
			} catch (Exception e) {

				e.printStackTrace();
			}
			if (wMdaInfo.isSchoolAttached()) {

				if (IppmsUtils.isNullOrLessThanOne(wEHB.getSchoolId()) && wEHB.getSchoolTransfer() == 0) {

					pErrors.rejectValue("schoolTransfer", "Required.Value",
							"Please indicate if A School Transfer Is Required or not for  "
									+ wName);
					return;


				}
				if (wEHB.getSchoolTransfer() == 1 && IppmsUtils.isNullOrLessThanOne(wEHB.getSchoolId())) {

					pErrors.rejectValue("schoolId", "Required.Value", "Please select the School to Transfer "
							+ wName);
					return;

				}
			}
			if (wEHB.getTransferDate() == null) {
				pErrors.rejectValue("transferDate", "Required.Value", "Please select the Date this transfer was approved.");
				return;
			}

			MdaDeptMap wMdaDeptMap = new MdaDeptMap();
			try {
				wMdaDeptMap = genericService.loadObjectUsingRestriction(MdaDeptMap.class, Arrays.asList(CustomPredicate.procurePredicate("mdaInfo.id",
						wEHB.getMdaId()), CustomPredicate.procurePredicate("department.id", wEHB.getDepartmentId())));
			} catch (InstantiationException | IllegalAccessException e) {

				e.printStackTrace();
			}
			if (wEHB.getEmployee().getMdaDeptMap().getId().equals(wMdaDeptMap.getId())) {
				if (wMdaDeptMap.getMdaInfo().isSchoolAttached()) {
					if (wEHB.getSchoolId() != null && wEHB.getEmployee().getSchoolInfo() != null
							&& wEHB.getSchoolId().equals(wEHB.getEmployee().getSchoolInfo().getId())) {
						pErrors.rejectValue("transferDate", "Required.Value", "Employee is already in "
								+ wEHB.getEmployee().getSchoolInfo().getName() + " - Transfer denied.");
						return;
					}
				} else {
					pErrors.rejectValue("transferDate", "Required.Value",
							"Employee is already in " + wEHB.getEmployee().getMdaDeptMap().getMdaInfo().getName()
									+ " and Department " + wEHB.getEmployee().getMdaDeptMap().getDepartment().getName()
									+ " - Transfer denied.");
					return;
				}
			}
		}

		LocalDate wToday = LocalDate.now();

		if ((wEHB.getTransferDate().isAfter(wToday)) || (wEHB.getTransferDate().compareTo(wToday) == 0)) {
			pErrors.rejectValue("transferDate", "Required.Value", "Transfer Approval Date must be before today.");
			return;
		}
	}
}