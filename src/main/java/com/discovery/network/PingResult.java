package com.discovery.network;

public class PingResult implements ProtocolResult {
	private String ip;
	private long rtt;
	private long duration;
	private Metrics metrics;
	
	public PingResult(String ip, long rtt, long duration, Metrics metrics) {
		this.ip = ip;
		this.rtt = rtt;
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
	@Override
	public Metrics getMetrics() {
		return metrics;
	}
	@Override
	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}

	public long getRtt() {
		return rtt;
	}

	public void setRtt(long rtt) {
		this.rtt = rtt;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
}
