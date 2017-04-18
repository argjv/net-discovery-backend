package com.discovery.network;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DiscoveryResultWeb {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private long timestamp;
	private String ip;
	private String hostname;
	private String os;
	private long rtt;
	private long duration;
	
	public DiscoveryResultWeb() {
		this.timestamp = System.currentTimeMillis();
		this.os = "";
		this.hostname = "";
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
