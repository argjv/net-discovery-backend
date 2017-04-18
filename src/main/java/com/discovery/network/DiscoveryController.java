package com.discovery.network;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscoveryController {
	private final String DEFAULT_IP = "127.0.0.1";
	private final String DEFAULT_NETWORK_MASK = "0";
	private final String DEFAULT_TIMESTAMP = "0";
	private final String FORCE_DISCOVERY = "false";
	
	@Autowired
    private DiscoveryManager dm;
	
	@Autowired
	private ChartBuilder cb;

	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping("/discover")
    public List<DiscoveryResultWeb> discoverNetwork(
    		@RequestParam(value="ip", defaultValue=DEFAULT_IP) String ip,
    		@RequestParam(value="nm", defaultValue=DEFAULT_NETWORK_MASK) int networkmask,
    		@RequestParam(value="ts", defaultValue=DEFAULT_TIMESTAMP) long clientTimestamp,
    		@RequestParam(value="force", defaultValue=FORCE_DISCOVERY) boolean forceDiscovery) {
    	return dm.runDiscovery(ip, networkmask, forceDiscovery);
    }
	
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping("/metrics")
    public List<Serie> getMetrics(
    		@RequestParam(value="chart", defaultValue=DEFAULT_IP) String chartName,
    		@RequestParam(value="force", defaultValue=FORCE_DISCOVERY) boolean forceDiscovery) {
        return cb.getChartSeries(chartName, true);
    }
}
