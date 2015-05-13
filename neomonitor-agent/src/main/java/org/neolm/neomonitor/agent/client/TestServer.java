package org.neolm.neomonitor.agent.client;

import java.util.List;

import org.neolm.neomonitor.agent.service.IAgentSelf;
import org.neolm.neomonitor.agent.service.IJavaProcess;
import org.neolm.neomonitor.agent.service.ILocalVM;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public class TestServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String  ip = args.length>0?args[0]:"127.0.0.1:8091";
		ip ="10.7.5.79:19091";
		RmiProxyFactoryBean rpfb = new RmiProxyFactoryBean();
		//18214
		//

		Object obj = rpfb.getObject();

		//ILocalVM vm = (ILocalVM)obj;
		
		rpfb.setServiceUrl("rmi://"+ ip +"/localvm");
		rpfb.setServiceInterface(ILocalVM.class);

		rpfb.afterPropertiesSet();

		obj = rpfb.getObject();

		ILocalVM vm = (ILocalVM)obj;
		System.out.println(vm.getAllProcesses());
		vm.refreshVms();
		System.out.println("done");
		//System.out.println(agentSelf.alive());
		// --- javaproc
		rpfb.setServiceUrl("rmi://"+ ip +"/javaproc");
		rpfb.setServiceInterface(IJavaProcess.class);
		rpfb.afterPropertiesSet();
		obj = rpfb.getObject();
		IJavaProcess jp = (IJavaProcess)obj;
		//System.out.println(jp.createConnetion(18214, "org.neolm.neomonitor.agent.server.NeoAgentServer"));
		//System.out.println(jp.createConnetion(12572, "com.asiainfo.appframe.ext.exeframe.task.TaskFrameWork SO_TASK 1 0"));
		//System.out.println(jp.getMBeanInfo(12572,  "java.lang:type=Threading"));
		
		
		String[] tmp = new String[2];
		tmp[0] = "boolean";
		tmp[1] = "boolean";
		Boolean[] thread = new Boolean[2];
		thread[0] = true ;
		thread[1] = true ;
		List result = jp.fetchOberseveObj("*:*@com.asiainfo.appframe.ext.exeframe.task.TaskFrameWork TERM_TASK 1 0") ;
		//System.out.println(jp.invokeMBeanOperation(12572, "java.lang:type=Threading", "dumpAllThreads", thread, tmp));
		System.out.println(result);
		String[] tmp2 = new String[1];
		tmp2[0] = "java.lang.String";
		String[] params = new String[1];
		params[0] = "";
		System.out.println(jp.invokeMBeanOperation(13238 , "fcks:name=fcks", "printSQLSummary", params, tmp2));
		
		
		

	}

}
