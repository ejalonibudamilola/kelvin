package com.osm.gnl.ippms.ogsg.controllers.hr;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.HRService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.MPBAMiniBean;
import com.osm.gnl.ippms.ogsg.domain.hr.MDAPMiniBeanHolder;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.MdaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Damilola Ejalonibu
 */
@Controller
@RequestMapping("/mdapSummary.do")
@SessionAttributes(types = MDAPMiniBeanHolder.class)
public class MDAPSummaryOverviewFormController extends BaseController {

	@Autowired
	private HRService hrService;
	 
	public MDAPSummaryOverviewFormController(){}

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(Model pModel,HttpServletRequest pRequest,HttpServletResponse pResponse) throws Exception {
		SessionManagerService.manageSession(pRequest, pModel);
	      BusinessCertificate bc = super.getBusinessCertificate(pRequest);

		
		MDAPMiniBeanHolder wMBH = new MDAPMiniBeanHolder();
	    List<MPBAMiniBean> wStoreList = new ArrayList<>();
	    List<MdaType> wList = this.genericService.loadAllObjectsWithSingleCondition(MdaType.class,getBusinessClientIdPredicate(pRequest),null);
	    if(wList.size() == 1){
			//Get all MDAs with Type ID...
			List<MdaInfo> wMdas = this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,getBusinessClientIdPredicate(pRequest),"name");
			//Iterate through them to get the Females and Males
			for(MdaInfo m : wMdas){
				List<MPBAMiniBean> wAgeFemaleList = this.hrService.getMDAPDetailsByGenderAndCode(IConstants.FEMALE,null, bc, m);
				List<MPBAMiniBean> wAgeMaleList = this.hrService.getMDAPDetailsByGenderAndCode(IConstants.MALE,null, bc, m);

				wStoreList.addAll(getDisplayBeanList(wAgeFemaleList,wAgeMaleList));
			}
		}else{
			for(MdaType m : wList){

				List<MPBAMiniBean> wAgeFemaleList = this.hrService.getMDAPDetailsByGenderAndCode(IConstants.FEMALE,m, bc,null);
				List<MPBAMiniBean> wAgeMaleList = this.hrService.getMDAPDetailsByGenderAndCode(IConstants.MALE,m, bc,null);

				wStoreList.addAll(getDisplayBeanList(wAgeFemaleList,wAgeMaleList));

			}
		}

		//Now we have everything now set necessary values...
	    wMBH = setTotals(wMBH,wStoreList);
		
		pModel.addAttribute("miniBean", wMBH);
		addRoleBeanToModel(pModel, pRequest);
		return "hr_mda/mdapDetailsForm";
		
	}

	private MDAPMiniBeanHolder setTotals(MDAPMiniBeanHolder pMBH,
										 List<MPBAMiniBean> pStoreList){
		Collections.sort(pStoreList);
		Double tM = 0.0D;
		Double tF = 0.0D;
		for(MPBAMiniBean m : pStoreList){
			pMBH.setTotalNoOfFemales(pMBH.getTotalNoOfFemales() + m.getNoOfFemales());
			pMBH.setTotalNoOfMales(pMBH.getTotalNoOfMales() + m.getNoOfMales());
			pMBH.setTotalStaffStrength(pMBH.getTotalStaffStrength() + m.getStaffStrength());
			tM = tM + m.getMalePercentage();
			tF = tF + m.getFemalePercentage();
			pMBH.setTotalMalePercentage(tM);
			pMBH.setTotalFemalePercentage(tF);
		}
		pMBH.setMpbaMiniBeanList(pStoreList);
		return pMBH;
	}

	private List<MPBAMiniBean> getDisplayBeanList(
			List<MPBAMiniBean> pAgeFemaleList, List<MPBAMiniBean> pAgeMaleList)
	{
		 
		HashMap<Long,MPBAMiniBean> wFillMap = new HashMap<Long,MPBAMiniBean>();
           
		 for(MPBAMiniBean m : pAgeFemaleList ){
			
			 if(wFillMap.containsKey(m.getId())){
				 MPBAMiniBean n = wFillMap.get(m.getId());
				 n.setNoOfFemales(n.getNoOfFemales()+m.getNoOfFemales());
				 n.setFemalePercentage(m.getFemalePercentage());
				 n.setStaffStrength(n.getStaffStrength() + m.getNoOfFemales());
				 wFillMap.put(n.getId(), n);
			 }else{
				 //This should typically not happen often.
				 m.setStaffStrength(m.getStaffStrength() + m.getNoOfFemales());
				 wFillMap.put(m.getId(), m);
			 }
			 
		 }
		 for(MPBAMiniBean m : pAgeMaleList ){
			 
			 if(wFillMap.containsKey(m.getId())){
				 MPBAMiniBean n = wFillMap.get(m.getId());
				 n.setNoOfMales(m.getNoOfMales());
				 n.setMalePercentage(m.getMalePercentage());
				 n.setStaffStrength(n.getStaffStrength() + m.getNoOfMales());
				 wFillMap.put(n.getId(), n);
			 }else{
				 //This should typically not happen often.
				 m.setStaffStrength(m.getStaffStrength() + m.getNoOfMales());
				 wFillMap.put(m.getId(), m);
			 }
			 
		 }
		 //Now create a list and return the list...
		 
		return createListFromHashMap(wFillMap);
	}

	private List<MPBAMiniBean> createListFromHashMap(HashMap<Long, MPBAMiniBean> pFillMap){
		List<MPBAMiniBean> wRetList = new ArrayList<MPBAMiniBean>();
		Set<Entry<Long,MPBAMiniBean>> set = pFillMap.entrySet();
		 Iterator<Entry<Long, MPBAMiniBean>> i = set.iterator();
		 
		 while(i.hasNext()){
			 Entry<Long,MPBAMiniBean> me = i.next();
			 me.getValue().setDisplayStyle("reportOdd");
			 wRetList.add(me.getValue()); 
			}
			
		return wRetList;	
	 }
		 
	}

