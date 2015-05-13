package org.neolm.neomonitor.precondition;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neolm.neomonitor.dao.NmonConfigOpPreconditionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Component;

@Component(value = "jdbcPrecondition")
public class JdbcPrecondition implements Precondition {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private NmonConfigOpPreconditionDao nmonConfigOpPreconditionDao;

	@Override
	public Boolean doJudge(String script, Map<String, Object> param) {
		// TODO Auto-generated method stub
		
		Map<String,?> res = (Map<String,?> )nmonConfigOpPreconditionDao.queryForMap(script, param) ;
		return Integer.parseInt(res.get("RESULT").toString())==0?true:false;
	}

}
