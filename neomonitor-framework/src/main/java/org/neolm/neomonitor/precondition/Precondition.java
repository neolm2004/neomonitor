package org.neolm.neomonitor.precondition;

import java.util.Map;

public interface Precondition {
	
	public Boolean doJudge(String script ,Map<String,Object> param);

}
