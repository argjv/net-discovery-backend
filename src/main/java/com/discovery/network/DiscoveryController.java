package com.discovery.network;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscoveryController {
	private final String DEFAULT_IP = "127.0.0.1";
	private final String DEFAULT_NETWORK_MASK = "0";

	@RequestMapping("/discover")
    public List<DiscoveryResult> discoverNetwork(
    		@RequestParam(value="ip", defaultValue=DEFAULT_IP) String ip,
    		@RequestParam(value="nm", defaultValue=DEFAULT_NETWORK_MASK) int networkmask) {
    	// This time stamp should be provided by the client when it started the operation.
    	long operationStartedTimestamp = System.currentTimeMillis();
    	DiscoveryManager dm = new DiscoveryManager();
    	return dm.runDiscovery(ip, networkmask);
    }
	
	@RequestMapping("/ping")
    public Ping pingIp(
    		@RequestParam(value="ip", defaultValue="127.0.0.1") String ip,
    		@RequestParam(value="nm", defaultValue="0") int networkmask) {
    	// This timestamp should be provided by the client when it started the operation.
    	long operationStartedTimestamp = System.currentTimeMillis();
        return new Ping(ip, networkmask);
    }
}
