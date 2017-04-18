package com.discovery.network;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.snmp4j.mp.SnmpConstants;

@Entity
public class Configuration {
	private static Configuration instance = null;
	
	@Id
	private long timestamp;
	
	// Ping default values.
	private long pingInitialTimeout = 1000;
	private long pingRetryTimeout = 0;
	private boolean pingIncludeFailures = false;
	private int pingTtl = 128; // 256 128 64 0
	
	// Snmp default values.
	/**
	* OID - .1.3.6.1.2.1.1.1.0 => SysDec
	* OID - .1.3.6.1.2.1.1.5.0 => SysName
	*/
	private String smnpOid = ".1.3.6.1.2.1.1.5.0";  // ends with 0 for scalar object
	private int snmpVersion = SnmpConstants.version2c;
	private String snmpCommunity = "public";
	private String smnpPort = "161"; // 8001 161 162
	private int snmpRetries = 1;
	private int snmpTimeout = 1500;
	
	// General config.
	private int threadCount = 50;
	private int threadPoolSize = 100;
	
	protected Configuration(){
		timestamp = System.currentTimeMillis();
	}
	
	public static Configuration getInstance() {
		if(instance == null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	// Called by every set method to mark the time of the last update.
	protected synchronized void updateTimestamp() {
		timestamp = System.currentTimeMillis();
	}
	
	public synchronized long getTimestamp() {
		return timestamp;
	}

	public synchronized void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		updateTimestamp();
	}

	public synchronized long getPingInitialTimeout() {
		return pingInitialTimeout;
	}

	public synchronized void setPingInitialTimeout(long pingInitialTimeout) {
		this.pingInitialTimeout = pingInitialTimeout;
		updateTimestamp();
	}

	public synchronized long getPingRetryTimeout() {
		return pingRetryTimeout;
	}

	public synchronized void setPingRetryTimeout(long pingRetryTimeout) {
		this.pingRetryTimeout = pingRetryTimeout;
		updateTimestamp();
	}

	public synchronized boolean isPingIncludeFailures() {
		return pingIncludeFailures;
	}

	public synchronized void setPingIncludeFailures(boolean pingIncludeFailures) {
		this.pingIncludeFailures = pingIncludeFailures;
		updateTimestamp();
	}

	public synchronized int getPingTtl() {
		return pingTtl;
	}

	public synchronized void setPingTtl(int pingTtl) {
		this.pingTtl = pingTtl;
		updateTimestamp();
	}

	public synchronized String getSmnpOid() {
		return smnpOid;
	}

	public synchronized void setSmnpOid(String smnpOid) {
		this.smnpOid = smnpOid;
		updateTimestamp();
	}

	public synchronized int getSnmpVersion() {
		return snmpVersion;
	}

	public synchronized void setSnmpVersion(int snmpVersion) {
		this.snmpVersion = snmpVersion;
		updateTimestamp();
	}

	public synchronized String getSnmpCommunity() {
		return snmpCommunity;
	}

	public synchronized void setSnmpCommunity(String snmpCommunity) {
		this.snmpCommunity = snmpCommunity;
		updateTimestamp();
	}

	public synchronized String getSmnpPort() {
		return smnpPort;
	}

	public synchronized void setSmnpPort(String smnpPort) {
		this.smnpPort = smnpPort;
		updateTimestamp();
	}

	public synchronized int getThreadCount() {
		return threadCount;
	}

	public synchronized void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
		updateTimestamp();
	}

	public synchronized int getThreadPoolSize() {
		return threadPoolSize;
	}

	public synchronized void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
		updateTimestamp();
	}

	public synchronized int getSnmpRetries() {
		return snmpRetries;
	}

	public synchronized void setSnmpRetries(int snmpRetries) {
		this.snmpRetries = snmpRetries;
	}

	public synchronized int getSnmpTimeout() {
		return snmpTimeout;
	}

	public synchronized void setSnmpTimeout(int snmpTimeout) {
		this.snmpTimeout = snmpTimeout;
	}
}
