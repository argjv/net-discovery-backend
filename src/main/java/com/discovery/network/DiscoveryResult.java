package com.discovery.network;

import java.util.List;

import org.icmp4j.IcmpPingResponse;

public class DiscoveryResult {
	private Metrics pingMetrics;
	private List<IcmpPingResponse> pingResults;

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

}
