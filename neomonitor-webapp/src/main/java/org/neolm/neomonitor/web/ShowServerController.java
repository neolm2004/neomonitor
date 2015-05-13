package org.neolm.neomonitor.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.dao.NmonConfigServerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class ShowServerController {
	
	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private NmonConfigServerDao nmonConfigServerDao;

	@RequestMapping(method = RequestMethod.GET, value = "list-servers-all")
	@ResponseBody
	public List<Map<String, Object>> listServices() {

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		// paramMap.put("opName", methodName) ;
		List<Map<String, Object>> monServer = nmonConfigServerDao.findServersForList() ;
				

		

		return monServer;

	}

	@RequestMapping(method = RequestMethod.GET, value = "list-grps-all")
	@ResponseBody
	public List<Map<String, Object>> listGroups() {

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		// paramMap.put("opName", methodName) ;
		List<Map<String, Object>> monServer = null;
		//npJdbcTemplate.queryForList(	"select distinct server_grp from nmon_config_server ",			paramMap);

		

		return monServer;

	}

}
