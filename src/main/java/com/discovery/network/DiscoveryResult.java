package com.discovery.network;

import java.util.List;

import org.icmp4j.IcmpPingResponse;

public class DiscoveryResult {
	private String ip;
	private Metrics pingMetrics;
	private List<IcmpPingResponse> pingResults;
	private Metrics discoveryMetrics;

	public synchronized Metrics getDiscoveryMetrics() {
		return discoveryMetrics;
	}
	public synchronized void setDiscoveryMetrics(Metrics discoveryMetrics) {
		this.discoveryMetrics = discoveryMetrics;
	} 
	public synchronized Metrics getPingMetrics() {
		return pingMetrics;
	}
	public synchronized List<IcmpPingResponse> getPingResults() {
		return pingResults;
	}
	public synchronized void setPingMetrics(Metrics pingMetrics) {
		this.pingMetrics = pingMetrics;
	}
	public synchronized void setPingResults(List<IcmpPingResponse> pingResults) {
		this.pingResults = pingResults;
	}
	public synchronized String getIp() {
		return ip;
	}
	public synchronized void setIp(String ip) {
		this.ip = ip;
	}

}
