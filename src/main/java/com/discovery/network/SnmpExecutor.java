package com.discovery.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

public class SnmpExecutor implements Callable<SnmpResult>{
	private String ipAddress;
	private String port;
	private Metrics snmpMetrics;	
	private String snmpResult;
	private Configuration config;
	
	public SnmpExecutor(String ip, String port) {
		this.snmpMetrics = new Metrics("snmp");
		this.ipAddress = ip;  // 127.0.0.1
		this.port = port; // 8001
		this.snmpResult = "";
		this.config = Configuration.getInstance();
	}
	
	@Override
	public SnmpResult call() throws Exception {
		// Execute the snmp
		snmpMetrics.operationStarted();
		log("Initializing SNMP...");
//		runSnmp();
		doSnmpwalk();
		snmpMetrics.operationFinished();
		
		if(snmpMetrics.getOperationResult() == "NOT_SET"){
			snmpMetrics.setOperationResult("SUCCESS");
		}
		// Prepare the wrapping result object and return it
		SnmpResult operationResult = new SnmpResult(ipAddress, snmpResult, snmpMetrics);
		log("Finished SNMP");
		return operationResult;
	}
	
	public void runSnmp() throws Exception {
		// Create TransportMapping and Listen
		DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
    	transport.listen();
    	
    	// Create Target Address object
    	CommunityTarget comtarget = getTarget();
        
        // Create the PDU object
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(config.getSmnpOid())));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));
        
        // Create Snmp object for sending data to Agent
        Snmp snmp = new Snmp(transport);
        
        log("Sending Request to Agent...");
        ResponseEvent response = snmp.get(pdu, comtarget);
        processResponse(response);
        snmp.close();
	}
	
	private void processResponse(ResponseEvent response) {
		String msg = "";
		if (response != null) {
			log("Got Response from Agent");
			PDU responsePDU = response.getResponse();
			
			if (responsePDU != null) {
				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				String errorStatusText = responsePDU.getErrorStatusText();
				
				if (errorStatus == PDU.noError) {
					msg = "" + responsePDU.getVariableBindings();
				} else {
					snmpMetrics.setOperationResult("FAILED");
					msg = "Error: Request Failed";
					log("Error: Request Failed");
					log("Error Status = " + errorStatus);
					log("Error Index = " + errorIndex);
					log("Error Status Text = " + errorStatusText);
				}
			} else {
				msg = "Error: Response PDU is null";
				snmpMetrics.setOperationResult("FAILED");
			}
		} else {
			msg = "Error: Agent Timeout... ";
			snmpMetrics.setOperationResult("TIMEOUT");
		}
		log(msg);
		snmpResult = msg;
	}
	
	public void doSnmpwalk() throws IOException  {
		DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
		Snmp snmp = new Snmp(transport);
		transport.listen();

		// setting up target
		CommunityTarget target = getTarget();
		target.setCommunity(new OctetString(config.getSnmpCommunity()));
		

		OID oid = null;
		try{
			oid = new OID(config.getSmnpOid());
		}
		catch(RuntimeException ex){
			log("OID is not specified correctly.");
			snmpMetrics.setOperationResult("FAILED");
		}

		log("Sending SNMP msg...");
		List<TreeEvent> events = safeGetSnmpSubTree(snmp, target, oid);
		log(events.toString());
		if(events == null || events.size() == 0){
			log("No result returned.");
			snmpMetrics.setOperationResult("FAILED");
		}
		
		// Get snmpwalk result.
		for (TreeEvent event : events) {
			if(event != null){
				if (event.isError()) {
					log("oid [" + oid + "] " + event.getErrorMessage());
					snmpMetrics.setOperationResult("FAILED");
				}		
				VariableBinding[] varBindings = event.getVariableBindings();
				if(varBindings == null || varBindings.length == 0){
					log("No result returned."); 
					snmpMetrics.setOperationResult("FAILED");
				} else {
					for (VariableBinding varBinding : varBindings) {
						log(varBinding.getVariable().toString());
						snmpResult += varBinding.getVariable().toString();
					}
				}
			}
		}
		snmp.close();
	}
	
	private static synchronized List<TreeEvent> safeGetSnmpSubTree(Snmp snmp, CommunityTarget target, OID oid){
		TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		return treeUtils.getSubtree(target, oid);
	}
	
	private CommunityTarget getTarget() {
		Address targetAddress;
        InetAddress addr;
        try {
			addr = InetAddress.getByName(ipAddress);
			targetAddress = new UdpAddress(addr, Integer.parseInt(this.port));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			targetAddress = GenericAddress.parse("udp:"+ this.ipAddress + "/" + this.port);
		}
        
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(config.getSnmpCommunity()));
		target.setAddress(targetAddress);
		target.setRetries(config.getSnmpRetries());
		target.setTimeout(config.getSnmpTimeout());
		target.setVersion(config.getSnmpVersion());
		return target;
	}
	
	private static void log(String msg) {
		System.out.println("[SNMP] " + msg);
	}
}