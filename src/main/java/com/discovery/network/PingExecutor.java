package com.discovery.network;

import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingUtil;

import java.util.ArrayList; 
import java.util.List;
import java.util.concurrent.Callable;

public class PingExecutor implements Callable<DiscoveryResult>{
	private List<String> ipsToPing;
	private Metrics pingMetrics;
	private List<IcmpPingResponse> pingResult;
	private Configuration config;
	
	public PingExecutor(List<String> ipsToPing) {
		this.pingMetrics = new Metrics("ping");
		this.ipsToPing = ipsToPing;
		this.pingResult = new ArrayList<IcmpPingResponse>();
		this.config = Configuration.getInstance();
	}
	
	@Override
	public DiscoveryResult call() throws Exception { 
		// Execute the ping
		pingMetrics.operationStarted();
		System.out.println("[PING] Initializing...");
		pingIps(this.ipsToPing);
		pingMetrics.operationFinished();
		
		if(pingResult.isEmpty()){
			pingMetrics.setOperationResult("FAILED");
		} else {
			pingMetrics.setOperationResult("SUCCESS");
		}
		
		// Wrap the result and metrics.
		DiscoveryResult operationResult = new DiscoveryResult();
		operationResult.setPingMetrics(pingMetrics);
		operationResult.setPingResults(pingResult);
		System.out.println("[PING] Finished");
		return operationResult;
	}
	
	private synchronized void pingIps(List<String> ipsToPing) {
		for(String ip : ipsToPing) {
			IcmpPingResponse response = pingIp(ip, config.getPingInitialTimeout(), config);
			
			// Only add successful results.
			if(response.getSuccessFlag() || config.isPingIncludeFailures()) {
				pingResult.add(response);
			} else if (response.getTimeoutFlag() && config.getPingRetryTimeout() > 0) {
				// If times out, try one more time.
				IcmpPingResponse retryResponse = pingIp(ip, config.getPingRetryTimeout(), config);
				if(response.getSuccessFlag()) {
					pingResult.add(retryResponse);
				}
			}
		}
	}
	
	private static synchronized IcmpPingResponse pingIp(String ip, long timeout, Configuration config) {
		System.out.println("[PING] Pinging " + ip + " ...");
		IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest();
		request.setHost(ip);
		request.setTimeout(timeout);
		request.setTtl(config.getPingTtl());
		IcmpPingResponse pingResponse = IcmpPingUtil.executePingRequest(request);
		System.out.println("[PING] Response " + ip + ": " + IcmpPingUtil.formatResponse (pingResponse));
		return pingResponse;
	}

	public synchronized List<IcmpPingResponse> getPingResult() {
		return pingResult;
	}

	public synchronized Metrics getPingMetrics() {
		return pingMetrics;
	}

	public synchronized List<String> getIpsToPing() {
		return ipsToPing;
	}

	public synchronized void setPingResult(List<IcmpPingResponse> pingResult) {
		this.pingResult = pingResult;
	}

	public synchronized void setPingMetrics(Metrics pingMetrics) {
		this.pingMetrics = pingMetrics;
	}

	public synchronized void setIpsToPing(List<String> ipsToPing) {
		this.ipsToPing = ipsToPing;
	}

}
