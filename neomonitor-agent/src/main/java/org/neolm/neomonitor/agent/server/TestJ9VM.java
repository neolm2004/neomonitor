package org.neolm.neomonitor.agent.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ibm.tools.attach.J9VirtualMachine;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.agent.jvm.LocalVirtualMachine;

import com.sun.tools.attach.VirtualMachineDescriptor;

public class TestJ9VM {
	
	private static Logger logger = Logger.getLogger(TestJ9VM.class);


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {

			logger.info("I am about to fetching the list on j9vm.");
			//List<VirtualMachineDescriptor> list = J9VirtualMachine.list();
			//logger.info("List size : "+list.size());
			
			//Date date = new Date(System.currentTimeMillis());
			Date date = new Date(Long.parseLong("1418743409616"));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//rightNow.setTimeInMillis(System.currentTimeMillis());//这里没必要
			String now = formatter.format(date);
			System.out.println(now);
		} catch (Exception e) {
			logger.error("Caught exception " + e.toString());
		}

	}

}
