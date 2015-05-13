package org.neolm.neomonitor.agent.jvm;

import ibm.tools.attach.J9VirtualMachine;

import java.io.File;
import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;
import org.neolm.neomonitor.agent.service.impl.JavaProcessImpl;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.management.ConnectorAddressLink;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
/**
 * @Title LocalVirtualMachine.java
 * @Description JVM
 * @author neolm
 * @date 2014-10-09
 * @version V2.0
 */
public class LocalVirtualMachine {

	private static Logger logger = Logger.getLogger(LocalVirtualMachine.class);

	private String address;
	private String commandLine;
	private String displayName;
	private int vmid;
	private Boolean isAttachSupported;

	public LocalVirtualMachine(int vmid, String commandLine) {
		this.vmid = vmid;
		this.commandLine = commandLine;
		//this.address = connectorAddress;
		//this.isAttachSupported = canAttach;
		this.displayName = getDisplayName(commandLine);
	}

	private static String getDisplayName(String commandLine) {
		// trim the pathname of jar file if it's a jar
		String[] res = commandLine.split(" ", 2);
		if (res[0].endsWith(".jar")) {
			File jarfile = new File(res[0]);
			String displayName = jarfile.getName();
			if (res.length == 2) {
				displayName += " " + res[1];
			}
			return displayName;
		}
		return commandLine;
	}

	public int vmid() {
		return vmid;
	}

	public boolean isManageable() {
		return (address != null);
	}

	public Boolean isAttachable() {
		return isAttachSupported;
	}

	

	public void attachVM() throws IOException {
		VirtualMachine vm = null;
		String jvmName = System.getProperty("java.vm.name", "not specified");
		String name = String.valueOf(vmid);
		String agent = null;
		isAttachSupported =false ;
		if (!"IBM J9 VM".equals(jvmName)) {
			try {
				vm = VirtualMachine.attach(name);
				isAttachSupported = true ;
				logger.debug("Attached VirtualMachine " + name);
			} catch (AttachNotSupportedException x) {
				logger.error("AttachNotSupportedException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("IOException", e);
				
			}

			String home = vm.getSystemProperties().getProperty("java.home");

			// Normally in ${java.home}/jre/lib/management-agent.jar but might
			// be in ${java.home}/lib in build environments.

			// String agent = home + File.separator + "jre" + File.separator +
			// "lib" + File.separator + "management-agent.jar";
			agent = home + File.separator + "jre" + File.separator + "lib"
					+ File.separator + "management-agent.jar";

			File f = new File(agent);

			if (!f.exists()) {
				agent = home + File.separator + "lib" + File.separator
						+ "management-agent.jar";
				f = new File(agent);
				if (!f.exists()) {
					logger.error("Management agent not found");
					throw new IOException("Management agent not found");
				}
			}

			agent = f.getCanonicalPath();

			try {

				vm.loadAgent(agent, "com.sun.management.jmxremote");
				logger.debug("VirtualMachine LoadAgent. ");
			} catch (AgentLoadException x) {
				logger.error("AgentLoadException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			} catch (AgentInitializationException x) {
				logger.error("AgentInitializationException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			}

			// get the connector address
			Properties agentProps = vm.getAgentProperties();

			address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);
			vm.detach();
		}
		// ibm aix j9vm
		if (address == null) {

			try {
				vm = J9VirtualMachine.attach(name);
				isAttachSupported = true ;
				logger.debug("Attached J9VirtualMachine " + name);
			} catch (AttachNotSupportedException x) {
				// TODO Auto-generated catch block
				logger.error("AttachNotSupportedException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			}

			agent = "instrument,"
					+ vm.getSystemProperties().getProperty("java.home")
					+ File.separator + "lib" + File.separator
					+ "management-agent.jar=";
			try {
				vm.loadAgentLibrary(agent);
			} catch (AgentLoadException x) {
				logger.error("AgentLoadException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			} catch (AgentInitializationException x) {
				logger.error("AgentInitializationException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			}
			address = vm.getSystemProperties().getProperty(
					LOCAL_CONNECTOR_ADDRESS_PROP);
			logger.debug("fetch address on j9vm :"+ commandLine + " | "+address);
			vm.detach();
		}
	}

	public void startManagementAgent() throws IOException {
		if (address != null) {
			// already started
			return;
		}

		if (!isAttachable()) {
			throw new IOException("This virtual machine \"" + vmid
					+ "\" does not support dynamic attach.");
		}

		loadManagementAgent();
		// fails to load or start the management agent
		if (address == null) {
			// should never reach here
			throw new IOException("Fails to find connector address");
		}
	}

	public String connectorAddress() {
		// return null if not available or no JMX agent
		return address;
	}

	public String displayName() {
		return displayName;
	}

	public String toString() {
		return commandLine;
	}

	// This method returns the list of all virtual machines currently
	// running on the machine
	public static Map<Integer, LocalVirtualMachine> getAllVirtualMachines() {
		Map<Integer, LocalVirtualMachine> map = new HashMap<Integer, LocalVirtualMachine>();
		getMonitoredVMs(map);
		getAttachableVMs(map);
		logger.debug(" Get AllVirtualMachines done .");
		return map;
	}

	private static void getMonitoredVMs(Map<Integer, LocalVirtualMachine> map) {
		MonitoredHost host;
		Set vms;
		try {
			host = MonitoredHost.getMonitoredHost(new HostIdentifier(
					(String) null));
			logger.debug("Getting MonitoredHost.");
			vms = host.activeVms();
			logger.debug("Vm set size : " + vms.size());
		} catch (java.net.URISyntaxException sx) {
			logger.error("URISyntaxException :", sx);
			throw new InternalError(sx.getMessage());
		} catch (MonitorException mx) {
			logger.error("MonitorException :", mx);
			throw new InternalError(mx.getMessage());
		}
		for (Object vmid : vms) {
			logger.debug("vmid:" + vmid.toString());
			if (vmid instanceof Integer) {
				int pid = ((Integer) vmid).intValue();
				String name = vmid.toString(); // default to pid if name not
												// available
				boolean attachable = false;
				String address = null;
				try {
					MonitoredVm mvm = host
							.getMonitoredVm(new VmIdentifier(name));
					// use the command line as the display name
					name = MonitoredVmUtil.commandLine(mvm);
					String args = MonitoredVmUtil.mainArgs(mvm);

					attachable = MonitoredVmUtil.isAttachable(mvm);
					address = ConnectorAddressLink.importFrom(pid);
					mvm.detach();

				} catch (Exception x) {
					// ignore
					logger.error("Exception :", x);
				}
				logger.debug("Detected process " + vmid + " [" + name + "].");
				map.put((Integer) vmid, new LocalVirtualMachine(pid, name
						));
			}
		}
	}
	
	public static Map<Integer,LocalVirtualMachine> fetchMonitoredVMs() {
		Map<Integer,LocalVirtualMachine> lvms = new HashMap<Integer,LocalVirtualMachine>();
		MonitoredHost host;
		Set vms;
		try {
			host = MonitoredHost.getMonitoredHost(new HostIdentifier(
					(String) null));
			logger.debug("Getting MonitoredHost.");
			vms = host.activeVms();
			logger.debug("Vm set size : " + vms.size());
		} catch (java.net.URISyntaxException sx) {
			logger.error("URISyntaxException :", sx);
			throw new InternalError(sx.getMessage());
		} catch (MonitorException mx) {
			logger.error("MonitorException :", mx);
			throw new InternalError(mx.getMessage());
		}
		for (Object vmid : vms) {
			logger.debug("vmid:" + vmid.toString());
			if (vmid instanceof Integer) {
				int pid = ((Integer) vmid).intValue();
				String name = vmid.toString(); // default to pid if name not
												// available
				boolean attachable = false;
				String address = null;
				try {
					MonitoredVm mvm = host
							.getMonitoredVm(new VmIdentifier(name));
					// use the command line as the display name
					name = MonitoredVmUtil.commandLine(mvm);
					String args = MonitoredVmUtil.mainArgs(mvm);

					attachable = MonitoredVmUtil.isAttachable(mvm);
					address = ConnectorAddressLink.importFrom(pid);
					mvm.detach();

				} catch (Exception x) {
					// ignore
					logger.error("Exception :", x);
				}
				logger.debug("Detected process " + vmid + " [" + name + "].");
				lvms.put((Integer) vmid, new LocalVirtualMachine(pid, name
						));
			}
		}
		
		return lvms;
	}

	private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";

	public static List<VirtualMachineDescriptor> getLocalVMs() {
		String jvmName = System.getProperty("java.vm.name", "not specified");
		List<VirtualMachineDescriptor> vms = null;
		if ("IBM J9 VM".equals(jvmName)) {
			logger.debug("Fetching list of java processes on J9VM.");
			vms = J9VirtualMachine.list();
			logger.debug("OK! All of java processes on J9VM are fetched.");
		} else {
			vms = VirtualMachine.list();
		}

		logger.debug("VirtualMachine List size : " + vms.size());

		return vms;

	}

	private static void getAttachableVMs(Map<Integer, LocalVirtualMachine> map) {

		String jvmName = System.getProperty("java.vm.name", "not specified");
		List<VirtualMachineDescriptor> vms = null;
		if ("IBM J9 VM".equals(jvmName)) {
			logger.info("listing ...");
			vms = J9VirtualMachine.list();
			logger.info("listed");
		} else {
			vms = VirtualMachine.list();
		}

		logger.info("VirtualMachine List size : " + vms.size());

		for (VirtualMachineDescriptor vmd : vms) {
			try {
				Integer vmid = Integer.valueOf(vmd.id());
				logger.debug("Getting  VirtualMachineDescriptor: " + vmid);
				if (!map.containsKey(vmid)) {
					boolean attachable = false;
					String address = null;
					try {
						VirtualMachine vm = null;
						if ("IBM J9 VM".equals(jvmName)) {
							vm = J9VirtualMachine.attach(vmd);
						} else {
							vm = VirtualMachine.attach(vmd);
						}

						attachable = true;
						Properties agentProps = vm.getAgentProperties();
						address = (String) agentProps
								.get(LOCAL_CONNECTOR_ADDRESS_PROP);
						vm.detach();
					} catch (AttachNotSupportedException x) {
						// not attachable
						logger.error("Not supported :", x);
					} catch (IOException x) {
						// ignore
						logger.error("IOException :", x);
					}
					map.put(vmid,
							new LocalVirtualMachine(vmid.intValue(), vmd
									.displayName()));
				}
			} catch (NumberFormatException e) {
				// do not support vmid different than pid
				logger.error("NumberFormatException :", e);
			}
		}
	}

	public static LocalVirtualMachine getLocalVirtualMachine(int vmid) {
		Map<Integer, LocalVirtualMachine> map = getAllVirtualMachines();
		LocalVirtualMachine lvm = map.get(vmid);

		if (lvm != null) {
			// Check if the VM is attachable but not included in the list
			// if it's running with a different security context.
			// For example, Windows services running
			// local SYSTEM account are attachable if you have Adminstrator
			// privileges.
			boolean attachable = false;
			String address = null;
			String name = String.valueOf(vmid); // default display name to pid
			try {
				VirtualMachine vm = VirtualMachine.attach(name);
				attachable = true;
				Properties agentProps = vm.getAgentProperties();
				address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);
				vm.detach();
				lvm = new LocalVirtualMachine(vmid, name);
			} catch (AttachNotSupportedException x) {
				// not attachable

			} catch (IOException x) {
				// ignore

			}
		}
		return lvm;
	}

	// load the management agent into the target VM
	private void loadManagementAgent() throws IOException {
		VirtualMachine vm = null;
		String jvmName = System.getProperty("java.vm.name", "not specified");
		String name = String.valueOf(vmid);
		String agent = null;
		if (!"IBM J9 VM".equals(jvmName)) {
			try {
				vm = VirtualMachine.attach(name);
				logger.debug("Attached VirtualMachine " + name);
			} catch (AttachNotSupportedException x) {
				logger.error("AttachNotSupportedException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			}

			String home = vm.getSystemProperties().getProperty("java.home");

			// Normally in ${java.home}/jre/lib/management-agent.jar but might
			// be in ${java.home}/lib in build environments.

			// String agent = home + File.separator + "jre" + File.separator +
			// "lib" + File.separator + "management-agent.jar";
			agent = home + File.separator + "jre" + File.separator + "lib"
					+ File.separator + "management-agent.jar";

			File f = new File(agent);

			if (!f.exists()) {
				agent = home + File.separator + "lib" + File.separator
						+ "management-agent.jar";
				f = new File(agent);
				if (!f.exists()) {
					logger.error("Management agent not found");
					throw new IOException("Management agent not found");
				}
			}

			agent = f.getCanonicalPath();

			try {

				vm.loadAgent(agent, "com.sun.management.jmxremote");
				logger.debug("VirtualMachine LoadAgent. ");
			} catch (AgentLoadException x) {
				logger.error("AgentLoadException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			} catch (AgentInitializationException x) {
				logger.error("AgentInitializationException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			}

			// get the connector address
			Properties agentProps = vm.getAgentProperties();

			address = (String) agentProps.get(LOCAL_CONNECTOR_ADDRESS_PROP);
			vm.detach();
		}
		// ibm aix j9vm
		if (address == null) {

			try {
				vm = J9VirtualMachine.attach(name);
				logger.debug("Attached J9VirtualMachine " + name);
			} catch (AttachNotSupportedException x) {
				// TODO Auto-generated catch block
				logger.error("AttachNotSupportedException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			}

			agent = "instrument,"
					+ vm.getSystemProperties().getProperty("java.home")
					+ File.separator + "lib" + File.separator
					+ "management-agent.jar=";
			try {
				vm.loadAgentLibrary(agent);
			} catch (AgentLoadException x) {
				logger.error("AgentLoadException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			} catch (AgentInitializationException x) {
				logger.error("AgentInitializationException", x);
				IOException ioe = new IOException(x.getMessage());
				ioe.initCause(x);
				throw ioe;
			}
			address = vm.getSystemProperties().getProperty(
					LOCAL_CONNECTOR_ADDRESS_PROP);
			logger.debug(address);
			vm.detach();
		}
	}

}
