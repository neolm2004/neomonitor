package org.neolm.neomonitor.agent.service;

import java.util.List;
import java.util.Map;

public interface ILocalVM {
	
	public List<Map<String,Object>> getAllProcesses();

	public Boolean attach(String cli);
	
	public Map<String,Object> invoke(Integer pid , String method ,List<Map<String,Object>> params);

	public void refreshVms();



}
