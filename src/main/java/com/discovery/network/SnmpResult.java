package com.discovery.network;

public class SnmpResult implements ProtocolResult {
	private String ip;
	private String os;
	private Metrics metrics;
	
	public SnmpResult(String ip, String os, Metrics metrics) {
		this.ip = ip;
		this.os = os;
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
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
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
