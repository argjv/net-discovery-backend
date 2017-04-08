package com.discovery.network;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class snmp {
	private String ipAddress;
	private String port;
	private String  oidValue;
	private int snmpVersion;
	private String community;
	
	public snmp(String ip, String port) {
		this.ipAddress = ip;  // 127.0.0.1
		this.port = port; // 8001
		this.oidValue = ".1.3.6.1.2.1.1.1.0";  // ends with 0 for scalar object
		this.snmpVersion = SnmpConstants.version1;
		this.community = "public";
	}
	
	public void runSnmp() throws Exception {
		// Create TransportMapping and Listen
		TransportMapping transport = new DefaultUdpTransportMapping();
    	transport.listen();
    	
    	// Create Target Address object
    	CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(this.community));
        comtarget.setVersion(this.snmpVersion);
        comtarget.setAddress(new UdpAddress(this.ipAddress + "/" + this.port));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);
        
        // Create the PDU object
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oidValue)));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));
        
        // Create Snmp object for sending data to Agent
        Snmp snmp = new Snmp(transport);
        
        System.out.println("Sending Request to Agent...");
        ResponseEvent response = snmp.get(pdu, comtarget);
        processResponse(response);
        snmp.close();
	}
	
	private void processResponse(ResponseEvent response) {
		if (response != null) {
			System.out.println("Got Response from Agent");
			PDU responsePDU = response.getResponse();
			
			if (responsePDU != null) {
				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				String errorStatusText = responsePDU.getErrorStatusText();
				
				if (errorStatus == PDU.noError) {
					System.out.println("Snmp Get Response = " + responsePDU.getVariableBindings());
				} else {
					System.out.println("Error: Request Failed");
					System.out.println("Error Status = " + errorStatus);
					System.out.println("Error Index = " + errorIndex);
					System.out.println("Error Status Text = " + errorStatusText);
				}
			} else {
				System.out.println("Error: Response PDU is null");
			}
		} else {
			System.out.println("Error: Agent Timeout... ");
		}	
	}
}