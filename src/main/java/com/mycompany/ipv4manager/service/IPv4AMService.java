package com.mycompany.ipv4manager.service;

import java.util.List;

import com.mycompany.ipv4manager.model.IPv4Block;
import com.mycompany.ipv4manager.model.IPv4Node;

/**
 * 
 * @author mathi
 * Service Interface for Controller
 *
 */
public interface IPv4AMService {

	IPv4Block createIPBlock (String block);
	List<IPv4Node> getAllIPs ();
	List<IPv4Node> acquireIP (String ip);
	List<IPv4Node> releaseIP (String ip);
}

