package com.discovery.network;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ChartBuilder {
	private static Iterable<Metrics> metricsIterator = null;

	@Autowired
	private MetricsRepository metricsRepository;
	
	@Cacheable(cacheNames="discovery", sync=true, condition="!#forceDiscovery")
	public List<Serie> getChartSeries(String chartName, boolean forceDiscovery) {
		switch (chartName) {
		case "chart1":
			return getChart1();
		case "chart2":
			return getChart2();
		case "chart3":
			return getChart3();
		default:
			return getChart1();
		}
	}
	
	private List<Serie> getChart1() {
		System.out.println("Fetching chart 1 metrics...");
		long[] dataS1 = new long[3];
		long[] dataS2 = new long[3];
		long[] dataS3 = new long[3];
		for (Metrics metrics : getInstanceFrom(metricsRepository)) {
			switch (metrics.getName()) {
			case "ping":
				dataS1[0] += 1;
				break;
			case "snmp":
				dataS2[1] += 1;
				break;
			case "dns":
				dataS3[2] += 1;
				break;
			default:
				break;
			}
		}
    	Serie s1 = new Serie("ICMP", dataS1);
    	Serie s2 = new Serie("SNMP", dataS2);
    	Serie s3 = new Serie("DNS", dataS3);

    	List<Serie> resultWeb = new ArrayList<Serie>();
    	resultWeb.add(s1);
    	resultWeb.add(s2);
    	resultWeb.add(s3);
    	System.out.println("Finished metrics 1 lookup");
    	return resultWeb;
	}
	
	private List<Serie> getChart2() {
		System.out.println("Fetching chart 2 metrics...");
		long[] dataS1 = new long[3];
		long[] dataS2 = new long[3];
		long[] dataS3 = new long[3];
		for (Metrics metrics : getInstanceFrom(metricsRepository)) {
			if (metrics.getOperationResult() != null) {
				switch (metrics.getName()) {
				case "ping":
					switch (metrics.getOperationResult()) {
					case "FAILED":
						dataS1[0] += 1;
						break;
					case "TIMEOUT":
						dataS2[0] += 1;
						break;
					case "SUCCESS":
						dataS3[0] += 1;
						break;
					default:
						break;
					}
					break;
				case "snmp":
					switch (metrics.getOperationResult()) {
					case "FAILED":
						dataS1[1] += 1;
						break;
					case "TIMEOUT":
						dataS2[1] += 1;
						break;
					case "SUCCESS":
						dataS3[1] += 1;
						break;
					default:
						break;
					}
					break;
				case "dns":
					switch (metrics.getOperationResult()) {
					case "FAILED":
						dataS1[2] += 1;
						break;
					case "TIMEOUT":
						dataS2[2] += 1;
						break;
					case "SUCCESS":
						dataS3[2] += 1;
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
			}
		}
    	Serie s1 = new Serie("FAILED", dataS1);
    	Serie s2 = new Serie("TIMEOUT", dataS2);
    	Serie s3 = new Serie("SUCCESS", dataS3);

    	List<Serie> resultWeb = new ArrayList<Serie>();
    	resultWeb.add(s1);
    	resultWeb.add(s2);
    	resultWeb.add(s3);
    	System.out.println("Finished metrics 2 lookup");
    	return resultWeb;
	}
	
	private List<Serie> getChart3() {
		System.out.println("Fetching chart 3 metrics...");
		int daysToCount = 10;
		long now = System.currentTimeMillis();
		long untilDate = now - 1000 * 60 * 60 * 24 * daysToCount;

		long[] dataS1 = new long[daysToCount];
		int[] iPingOperations = new int[daysToCount];
		int[] iPingTotal = new int[daysToCount];
		long[] dataS2 = new long[daysToCount];
		int[] iSnmpOperations = new int[daysToCount];
		int[] iSnmpTotal = new int[daysToCount];
		long[] dataS3 = new long[daysToCount];
		int[] iDnsOperations = new int[daysToCount];
		int[] iDnsTotal = new int[daysToCount];
		long[] dataS4 = new long[daysToCount];
		int[] iDiscoveryOperations = new int[daysToCount];
		int[] iDiscoveryTotal = new int[daysToCount];

		for (Metrics metrics : getInstanceFrom(metricsRepository)) {
			if (metrics.getOperationFinishedTimestamp() >= untilDate) {
				int day = (int) (now - metrics.getOperationFinishedTimestamp())/ (24 * 60 * 60 * 1000);
				switch (metrics.getName()) {
				case "ping":
					iPingOperations[day]++;
					iPingTotal[day] += metrics.getTotalTime();
					break;
				case "PingMetrics":
					iPingOperations[day]++;
					iPingTotal[day] += metrics.getTotalTime();
					break;
				case "snmp":
					iSnmpOperations[day]++;
					iSnmpTotal[day] += metrics.getTotalTime();
					break;
				case "SnmpMetrics":
					iSnmpOperations[day]++;
					iSnmpTotal[day] += metrics.getTotalTime();
					break;
				case "dns":
					iDnsOperations[day]++;
					iDnsTotal[day] += metrics.getTotalTime();
					break;
				case "discovery":
					iDiscoveryOperations[day]++;
					iDiscoveryTotal[day] += metrics.getTotalTime();
					break;
				case "DiscoveryManagerMetrics":
					iDiscoveryOperations[day]++;
					iDiscoveryTotal[day] += metrics.getTotalTime();
					break;
				default:
					break;
				}
			}
		}
		
		for (int day = 0; day < daysToCount; day++) {
			if(iPingOperations[day] > 0) {
				dataS1[day] = (iPingTotal[day] / iPingOperations[day])/1000;
			} else {
				dataS1[day] = 0;
			}
			if(iSnmpOperations[day] > 0) {
				dataS2[day] = (iSnmpTotal[day] / iSnmpOperations[day])/1000;
			} else {
				dataS2[day] = 0;
			}
			if(iDnsOperations[day] > 0) {
				dataS3[day] = (iDnsTotal[day] / iDnsOperations[day])/1000;
			} else {
				dataS3[day] = 0;
			}
			if(iDiscoveryOperations[day] > 0) {
				dataS4[day] = (iDiscoveryTotal[day] / iDiscoveryOperations[day])/1000;
			} else {
				dataS4[day] = 0;
			}
		}
		
		Serie s1 = new Serie("ICMP", dataS1);
    	Serie s2 = new Serie("SNMP", dataS2);
    	Serie s3 = new Serie("DNS", dataS3);
    	Serie s4 = new Serie("Discovery", dataS4);

    	List<Serie> resultWeb = new ArrayList<Serie>();
    	resultWeb.add(s1);
    	resultWeb.add(s2);
    	resultWeb.add(s3);
    	resultWeb.add(s4);
    	System.out.println("Finished metrics 3 lookup");
    	return resultWeb;
	}
	
	private static Iterable<Metrics> getInstanceFrom(MetricsRepository metricsRepository) {
		if(metricsIterator == null) {
			metricsIterator = metricsRepository.findAll();;
		}
		int dbSize = (int) metricsRepository.count();
		System.out.println("Numer of records: " + dbSize);
		return metricsIterator;
	}
}
