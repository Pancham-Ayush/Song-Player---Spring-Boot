package com.example.Music_Player.ClientServiceManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;

@Service
public class LoadBalancer {

//    No Need Spring - Eureka will autoconfigure , needed when ribbon used(Deprecated)
    @Autowired
    LoadBalancerClient loadBalancerClient;
    
    String S3_SERVICE_URL(){
        return loadBalancerClient.choose("S3-Service").toString();
    }
}
