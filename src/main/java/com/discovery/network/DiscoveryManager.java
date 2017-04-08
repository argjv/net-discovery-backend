 package com.discovery.network;

import org.apache.commons.net.util.*;
 
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

public class DiscoveryManager{
	private int DEFAULT_THREAD_COUNT = 100;
	private int DEFAULT_THREAD_POOL_COUNT = 50;
	private String ip;
	private Metrics discoveryManagerMetrics;

	public DiscoveryManager() {
		this.discoveryManagerMetrics = new Metrics("DiscoveryManagerMetrics");
	}
	
	public List<DiscoveryResult> runDiscovery(String ip, int netmask){
		this.discoveryManagerMetrics.operationStarted();
		//Get ExecutorService from Executors utility class, thread pool size is 10
        ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_COUNT);
        
        List<DiscoveryResult> result = new ArrayList<DiscoveryResult>();
		List<Future<DiscoveryResult>> discoveryResultList = new ArrayList<Future<DiscoveryResult>>();
		
		// Get all the IP addresses for the given IP and Netmask.
		SubnetUtils utils = new SubnetUtils(ip + "/" + netmask);
		utils.setInclusiveHostCount(true);
		String[] allIps = utils.getInfo().getAllAddresses();
		
		int partitionSize = IntMath.divide(allIps.length, DEFAULT_THREAD_COUNT, RoundingMode.UP);
		List<List<String>> partitions = Lists.partition(Arrays.asList(allIps), partitionSize);
		System.out.println("Threads: " + DEFAULT_THREAD_COUNT);
		
		for (List<String> ipList: partitions) {
			System.out.println("Partition size: " + ipList.size());
			Callable<DiscoveryResult> callable = new Ping(ipList);
            Future<DiscoveryResult> future = executor.submit(callable);
            discoveryResultList.add(future);
		}
		
		int aux = 1;
		for(Future<DiscoveryResult> fut : discoveryResultList){
            try {
            	System.out.println("Reading future object " + aux);
            	result.add(fut.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            aux++;
        }
		
		executor.shutdown();
		DiscoveryResult dr = new DiscoveryResult();
		this.discoveryManagerMetrics.operationFinished();
		dr.setPingMetrics(this.discoveryManagerMetrics);
		return result;
	}

}
