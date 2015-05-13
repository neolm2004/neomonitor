package org.neolm.neomonitor.agent.service.impl;

import org.neolm.neomonitor.agent.service.IAgentSelf;
import org.springframework.stereotype.Component;

@Component
public class AgentSelfImpl implements IAgentSelf {

	@Override
	public boolean alive() {
		// TODO Auto-generated method stub
		return true;
	}

}
