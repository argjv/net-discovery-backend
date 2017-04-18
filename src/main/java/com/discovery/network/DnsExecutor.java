package com.discovery.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

public class DnsExecutor implements Callable<DnsResult>{
	private String ipAddress;
	private Metrics dnsMetrics;
	
	public DnsExecutor(String ip) {
		this.ipAddress = ip;
		this.dnsMetrics = new Metrics("dns");
	}

	@Override
	public DnsResult call() throws Exception {
		// Execute the snmp
		dnsMetrics.operationStarted();
		log("Initializing DNS lookup...");
		String dnsResult = safeHostNameLookup(this.ipAddress);
		dnsMetrics.operationFinished();
		
		if(dnsResult == ipAddress || dnsResult == ""){
			dnsMetrics.setOperationResult("FAILED");
		} else {
			dnsMetrics.setOperationResult("SUCCESS");
		}
		// Prepare the wrapping result object and return it
		DnsResult operationResult = new DnsResult(ipAddress, dnsResult, dnsMetrics);
		log("Finished DNS lookup");
		return operationResult;
	}
	
	private static synchronized String safeHostNameLookup(String ipAddress){
		String hostname = "";
		InetAddress addr;
		try {
			addr = InetAddress.getByName(ipAddress);
			hostname = addr.getHostName();
			log("Host Address: "+ addr.getHostAddress());
	        log("Host Name: "+ hostname);
	        log("CanonicalHostName: "+ addr.getCanonicalHostName());
	        log("Address: "+ addr.getAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hostname;
	}
	
	private static void log(String msg) {
		System.out.println("[DNS] " + msg);
	}
}
