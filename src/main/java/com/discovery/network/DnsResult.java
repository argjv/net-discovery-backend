package com.discovery.network;

public class DnsResult implements ProtocolResult {
	private String ip;
	private String hostname;
	private Metrics metrics;
	
	public DnsResult(String ip, String hostname, Metrics metrics) {
		this.ip = ip;
		this.hostname = hostname;
		this.metrics = metrics;
	}

	@Override
	public String getIp() {
		return ip;
	}
	@Override
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	@Override
	public Metrics getMetrics() {
		return metrics;
	}
	@Override
	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}
}
