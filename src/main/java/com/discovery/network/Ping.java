package com.discovery.network;

import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingUtil;

import com.discovery.network.snmp;

import java.util.ArrayList; 
import java.util.List;
import java.util.concurrent.Callable;

public class Ping implements Callable<DiscoveryResult>{
	// Configuration variables
	private long INITIAL_TIMEOUT = 1000;
	private long RETRY_TIMEOUT = 3000;
	private String SNMP_PORT = "161"; // 8001 161 162
	private int TTL = 128; // 256 128 64 0
	
	private String ip;
	private int netmask;
	private List<IcmpPingResponse> pingResult;
	private Metrics pingMetrics;
	private List<String> ipsToPing;
	
	public Ping(List<String> ipsToPing) {
		// Metrics
		this.pingMetrics = new Metrics("PingMetrics");
		
		this.ipsToPing = ipsToPing;
		this.pingResult = new ArrayList<IcmpPingResponse>();
	}
	
	public Ping(String ip, int netmask) {
		// Metrics
		this.pingMetrics = new Metrics("PingMetrics");
		
		// Class attributes
		this.ip = ip;
		this.netmask = netmask;
		this.pingResult = new ArrayList<IcmpPingResponse>();
	}
	
	@Override
	public DiscoveryResult call() throws Exception {
		// Execute the ping
		pingMetrics.operationStarted();
//		pingSubnet(ip, netmask);
		pingIps(this.ipsToPing);
		pingMetrics.operationFinished();
		
		// Prepare the wrapping result object and return it
		DiscoveryResult operationResult = new DiscoveryResult();
		operationResult.setPingMetrics(pingMetrics);
		operationResult.setPingResults(pingResult);
		return operationResult;
	}
	
	
	private void pingIps(List<String> ipsToPing) {
		for(String ip : ipsToPing) {
			IcmpPingResponse response = pingIp(ip, INITIAL_TIMEOUT);
			System.out.println(ip + ": " + IcmpPingUtil.formatResponse (response));
			
//			pingResult.add(response);
			if(response.getSuccessFlag()) {
				pingResult.add(response);
				snmp snmpProtocol = new snmp(ip, SNMP_PORT);
				try {
					snmpProtocol.runSnmp();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// If times out, try one more time.
			if(response.getTimeoutFlag()) {
				IcmpPingResponse retryResponse = pingIp(ip, RETRY_TIMEOUT);
				System.out.println(ip + ": " + "**Retrying: " + IcmpPingUtil.formatResponse (retryResponse));
			}
			
		}
	}
	
	private IcmpPingResponse pingIp(String ip, long timeout) {
		IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest ();
		request.setHost (ip);
		request.setTimeout(timeout);
		request.setTtl(TTL);
		return IcmpPingUtil.executePingRequest (request);
	}

	public String getIp() {
		return ip;
	}

	public int getNetmask() {
		return netmask;
	}

	public List<IcmpPingResponse> getResults() {
		return pingResult;
	}

	public Metrics getPingMetrics() {
		return pingMetrics;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setNetmask(int netmask) {
		this.netmask = netmask;
	}

	public void setResults(List<IcmpPingResponse> results) {
		this.pingResult = results;
	}

	public void setPingMetrics(Metrics pingMetrics) {
		this.pingMetrics = pingMetrics;
	}

	public List<IcmpPingResponse> getPingResult() {
		return pingResult;
	}

	public List<String> getIpsToPing() {
		return ipsToPing;
	}

	public void setPingResult(List<IcmpPingResponse> pingResult) {
		this.pingResult = pingResult;
	}

	public void setIpsToPing(List<String> ipsToPing) {
		this.ipsToPing = ipsToPing;
	}
}
