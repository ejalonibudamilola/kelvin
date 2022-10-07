package com.osm.gnl.ippms.ogsg.domain.beans;




import com.osm.gnl.ippms.ogsg.domain.deduction.AbstractDeductionEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class UtilityBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8179744126047096712L;
	
	private HashMap<Integer,List<Long>> wInnerMap;
	
	public UtilityBean(){}
	
	public void addToMap(List<Long> pList){
		if(wInnerMap == null)
			wInnerMap = new HashMap<>();
		wInnerMap.put(wInnerMap.size() + 1, pList);
	}
	
	public Set<Integer> getKeySet(){
		if(wInnerMap != null){
			return wInnerMap.keySet();
		}
		return null;
	}
	
    public Object[] getValueAsObjectArray(Integer pKey){
    	if(wInnerMap != null && this.wInnerMap.containsKey(pKey)){
    		return this.wInnerMap.get(pKey).toArray();
    	}
    	return null;
    }

	public void makeMapMap(HashMap<Long, Long> wEDIIList)
	{
		 if(wEDIIList != null ){
			 Set<Long> wKeySet = wEDIIList.keySet();
			 int count = 0;
			 int wTotNum = wKeySet.size();
			 HashMap<Long,Long> wFilterMap = new HashMap<>();
			 List<Long> wAddList = new ArrayList<>();
			 for(Long wInt : wKeySet){
			    	Long wValue = wEDIIList.get(wInt);
			    	count++;
			    	 
			    	if(wFilterMap.containsKey(wInt))
			    		continue;
			    	else{
			    		if(wAddList.size() == 900 || count == wTotNum){
			    			this.addToMap(wAddList);
			    			wAddList = new ArrayList<>();
			    		}else{
			    			wAddList.add(wValue);
			    		}
			    	}
			    	 
			  }
			 if(!wAddList.isEmpty()){
				 this.addToMap(wAddList);
	    			wAddList = new ArrayList<>();
			 }
		 }
		
	}
	public void makeEmpDeductionIdMap(HashMap<Long, AbstractDeductionEntity> wEDIIList)
	{
		if(wEDIIList != null ){
			Set<Long> wKeySet = wEDIIList.keySet();
			int count = 0;
			int wTotNum = wKeySet.size();
			HashMap<Long,Long> wFilterMap = new HashMap<Long,Long>();
			List<Long> wAddList = new ArrayList<Long>();
			for(Long wInt : wKeySet){
				AbstractDeductionEntity wValue = wEDIIList.get(wInt);
				count++;

				if(wFilterMap.containsKey(wInt))
					continue;
				else{
					if(wAddList.size() == 900 || count == wTotNum){
						this.addToMap(wAddList);
						wAddList = new ArrayList<Long>();
					}else{
						wAddList.add(wValue.getId());
					}
				}

			}
			if(!wAddList.isEmpty()){
				this.addToMap(wAddList);
				wAddList = new ArrayList<Long>();
			}
		}

	}
}
