 package com.discovery.network;

import org.apache.commons.net.util.*;
import org.icmp4j.IcmpPingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;

import java.math.RoundingMode;

@Service
public class DiscoveryManager{
	private Configuration config;
	private Metrics discoveryManagerMetrics;
	
	@Autowired
	private MetricsRepository metricsRepository;
	
	@Autowired
	private DiscoveryResultWebRepository discoveryResultWebRepository;
	
	@Autowired
	private ConfigurationRepository configurationRepository;

	public DiscoveryManager() {
		this.discoveryManagerMetrics = new Metrics("discovery");
		this.config = Configuration.getInstance();
	}
	
	@Cacheable(cacheNames="discovery", sync=true, condition="!#forceDiscovery")
	public List<DiscoveryResultWeb> runDiscovery(String ip, int netmask, boolean forceDiscovery){
		configurationRepository.save(this.config);
		this.discoveryManagerMetrics.operationStarted();

		//Get ExecutorService from Executors utility class, thread pool size is 10
        ExecutorService executor = Executors.newFixedThreadPool(config.getThreadPoolSize());
        List<DiscoveryResult> pResult = new ArrayList<DiscoveryResult>();
		
		log("Threads: " + config.getThreadCount());
		
		// Execute Ping.
		List<Future<DiscoveryResult>> pingDiscoveryResultList = new ArrayList<Future<DiscoveryResult>>();
		List<List<String>> partitions = getPartitionedIPs(ip, netmask);
		for (List<String> ipList: partitions) {
			log("Partition size: " + ipList.size());
			Callable<DiscoveryResult> callable = new PingExecutor(ipList);
            Future<DiscoveryResult> future = executor.submit(callable);
            pingDiscoveryResultList.add(future);
		}
		List<DiscoveryResultWeb> webResult = new ArrayList<DiscoveryResultWeb>();
		int aux = 1;
		for(Future<DiscoveryResult> fut : pingDiscoveryResultList){
            try {
            	log("Reading future object " + aux);
            	DiscoveryResult pingResult = fut.get();
            	for(IcmpPingResponse pr : pingResult.getPingResults()) {
            		DiscoveryResultWeb pWebResult = new DiscoveryResultWeb();
                	pWebResult.setIp(pr.getHost());
                	pWebResult.setRtt(pr.getRtt());
                	pWebResult.setDuration(pr.getDuration());
                	webResult.add(pWebResult);
            	}
            	
            	pResult.add(pingResult);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            aux++;
        }
		
		// Execute SNMP and DNS.
		List<Future<SnmpResult>> snmpDiscoveryResultList = new ArrayList<Future<SnmpResult>>();
		List<Future<DnsResult>> dnsDiscoveryResultList = new ArrayList<Future<DnsResult>>();
		for (DiscoveryResult pingResults : pResult) {
			metricsRepository.save(pingResults.getPingMetrics());
			for(IcmpPingResponse pingResult : pingResults.getPingResults()) {
				if(pingResult.getHost() != null) {
					log("Active host: " + pingResult.getHost());
					try {
						Callable<SnmpResult> snmpCallable = new SnmpExecutor(pingResult.getHost(), config.getSmnpPort());
			            Future<SnmpResult> snmpFuture = executor.submit(snmpCallable);
			            snmpDiscoveryResultList.add(snmpFuture);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						Callable<DnsResult> dnsCallable = new DnsExecutor(pingResult.getHost());
			            Future<DnsResult> dnsFuture = executor.submit(dnsCallable);
			            dnsDiscoveryResultList.add(dnsFuture);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		aux = 1;
		for(Future<SnmpResult> fut : snmpDiscoveryResultList){
            try {
            	log("Reading SNMP future object " + aux);
            	SnmpResult snmpResult = fut.get();
            	metricsRepository.save(snmpResult.getMetrics());
            	if(snmpResult.getOs() != "") {
            		for(DiscoveryResultWeb dwr :  webResult){
            			if(dwr.getIp() == snmpResult.getIp()){
            				dwr.setOs(snmpResult.getOs());
            				break;
            			}
            		}
            	}
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            aux++;
        }
		aux = 1;
		for(Future<DnsResult> fut : dnsDiscoveryResultList){
            try {
            	log("Reading DNS future object " + aux);
            	DnsResult dnsResult = fut.get();
            	metricsRepository.save(dnsResult.getMetrics());
            	if(dnsResult.getHostname() != "") {
            		for(DiscoveryResultWeb dwr :  webResult){
            			discoveryResultWebRepository.save(dwr);
            			if(dwr.getIp() == dnsResult.getIp()){
            				dwr.setHostname(dnsResult.getHostname());
            				break;
            			}
            		}
            	}
            	
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            aux++;
        }
		
		

		executor.shutdown();
		this.discoveryManagerMetrics.operationFinished();
		metricsRepository.save(this.discoveryManagerMetrics);
		log("Finished discovery");
		return webResult;
	}
	
	private List<List<String>> getPartitionedIPs(String ip, int netmask) {
		// Get all the IP addresses for the given IP and Netmask.
		SubnetUtils utils = new SubnetUtils(ip + "/" + netmask);
		utils.setInclusiveHostCount(true);
		String[] allIps = utils.getInfo().getAllAddresses();
		
		int partitionSize = IntMath.divide(allIps.length, config.getThreadCount(), RoundingMode.UP);
		return Lists.partition(Arrays.asList(allIps), partitionSize);	
	}
	
	private static void log(String msg) {
		System.out.println("[DM] " + msg);
	}
}
