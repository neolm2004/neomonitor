package org.neolm.neomonitor.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.apache.log4j.Logger;

public class NeomonitorBaseDao  {
	
	private Logger logger = Logger.getLogger(this.getClass()) ;
	

	@Autowired
	protected NamedParameterJdbcTemplate npJdbcTemplate ;

	
	public List<?> queryForList(String sql , Map<String,?> paramMap){
		logger.debug(sql);
		return npJdbcTemplate.queryForList(sql, paramMap) ;
	}
		
	public Map<String,?> queryForMap(String sql , Map<String,?> paramMap){
		logger.debug(sql);
		return npJdbcTemplate.queryForMap(sql, paramMap) ;
	}
	
	public List<?> query(String sql , Map<String,?> paramMap ){
		logger.debug(sql);
		return npJdbcTemplate.queryForList(sql, paramMap) ;
	}

	public int execute(String sql , Map<String,?> paramMap ){
		logger.debug(sql);
		
		return npJdbcTemplate.update(sql, paramMap) ;
	}
	

}
