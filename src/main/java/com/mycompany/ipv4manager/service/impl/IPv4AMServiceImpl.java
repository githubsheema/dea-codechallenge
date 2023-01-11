package com.mycompany.ipv4manager.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.mycompany.ipv4manager.exception.IPv4ConflictException;
import com.mycompany.ipv4manager.exception.IPv4NotFoundException;
import com.mycompany.ipv4manager.model.IPv4Block;
import com.mycompany.ipv4manager.model.IPv4Node;
import com.mycompany.ipv4manager.model.IPv4NodeStatus;
import com.mycompany.ipv4manager.repository.IPv4AMBlockRepository;
import com.mycompany.ipv4manager.repository.IPv4AMNodRepository;
import com.mycompany.ipv4manager.service.IPv4AMService;
import com.mycompany.ipv4manager.util.NetUtil;

@Service
public class IPv4AMServiceImpl implements IPv4AMService {

	public static final Logger log = LoggerFactory.getLogger(IPv4AMServiceImpl.class);

	// Inject DAO for Block
	@Autowired
	IPv4AMBlockRepository ipv4BlockRepository;

	// Inject DAO for IPv4 Node
	@Autowired
	IPv4AMNodRepository ipv4NodeRepository;

	/**
	 *  createIPBlock takes "xxx.xxx.xxx.xxx/xx" as CIDR format block address
	 *  returns list of IP addresses associated with that network if allocation is successsfull
	 *  Otherwise returns responses with generic fault response
	 *  
	 *  First an exact match is made with DB. If found returns conflict error
	 *  Then attempts to allocate IP but each ip is again verified with DB. If IP already
	 *  presents (must be due to overlapping Subnet) returns conflict error
	 *  
	 *  If all is well a response is sent back with a link to pull all IPs.
	 *  
	 *  
	 */
	@Override
	public IPv4Block createIPBlock(String block) {
		IPv4Block ipv4Block = null;
		log.debug("createIPBlock Block Call");
		checkBlockAlreadyExists(block); // matches exact block
		List<String> IPs = NetUtil.findIPRangeList(block);
		if (null != IPs && IPs.size() != 0) {

			ipv4Block = new IPv4Block(block, "tester");

			List<IPv4Node> nodes = new ArrayList<IPv4Node>(IPs.size());
			for (int i = 0; i < IPs.size(); i++) {
				String ip = IPs.get(i);
				checkIfExist(ip, block); // matches other subnet that might overlap
				nodes.add(NetUtil.createByIPString(ip, "tester"));
				nodes.get(i).setIpAddress(ip);
				nodes.get(i).setNetMask(NetUtil.getSubNet(block));
				nodes.get(i).setIpv4Block(ipv4Block);
			}
			ipv4Block.setIplist(nodes);
			ipv4BlockRepository.save(ipv4Block);
		}
		return ipv4Block;
	}

	/**
	 * 
	 * A simple query to IPV4Node table is made and returns values 
	 * 
	 */
	@Override
	public List<IPv4Node> getAllIPs() {
		log.debug("getAllByBlock Block Call");
		List<IPv4Node> results = ipv4NodeRepository.findAll();
		for (IPv4Node node : results) {
			NetUtil.setSubNet(node);
		}
		checkforExceptions(results, "all-blocks");
		return results;
	}
	
	/**
	 *  After all validations (format), it will check for existance in DB
	 *  if not present returns not found
	 *  if present and status is already available returns 'what are you doing'
	 *  otherwise updates status to ACQUIRED and returns the IPv4Node with status
	 *  along with subnet mask.
	 */

	@Override
	public List<IPv4Node> acquireIP(String ip) {
		log.debug("acquireIP  Block Call");

		List<IPv4Node> results = updateStatus(ip, IPv4NodeStatus.ACQUIRED);
		checkforExceptions(results, ip);
		return results;
	}
	
	/**
	 *  After all validations (format), it will check for existence in DB
	 *  if not present returns not found
	 *  if present and status is already available returns 'what are you doing'
	 *  otherwise updates status to AVAILABLE and returns the IPv4Node with status
	 *  along with subnet mask.
	 */

	@Override
	public List<IPv4Node> releaseIP(String ip) {
		log.debug("releaseIP Block Call");
		List<IPv4Node> results = updateStatus(ip, IPv4NodeStatus.AVAILABLE);
		checkforExceptions(results, ip);
		return results;
	}
	
	/**
	 * 
	 *  Below are some convenient reusable private methods.
	 */
	
	private void checkBlockAlreadyExists(String block) {
		List<IPv4Block> result = ipv4BlockRepository.searchBlock(block);
		if (null != result && result.size() > 0) {
			throw new IPv4ConflictException("[" + block + "] - exists alreaady!");
		}
	}

	private void checkIfExist(String ip, String block) {
		Example<IPv4Node> example = Example.of(NetUtil.createByIPStringExample(ip));
		List<IPv4Node> results = ipv4NodeRepository.findAll(example);
		if (null != results && results.size() > 0) {
			throw new IPv4ConflictException("[" + ip + "] - already exists - check [" + block + "]!");
		}
	}

	private List<IPv4Node> updateStatus(String ip, IPv4NodeStatus status) {
		Example<IPv4Node> example = Example.of(NetUtil.createByIPStringExample(ip));
		List<IPv4Node> results = ipv4NodeRepository.findAll(example);
		if (null != results && results.size() > 0) {
			for (IPv4Node node : results) {
				NetUtil.setSubNet(node);
			} // if more than there is some problem...!
			IPv4Node node = results.get(0);
			if (node.getStatus() == status) {
				throw new IPv4ConflictException("[" + ip + "] - has [" + status + "] status alreaady!");
			}
			node.setStatus(status);
			node.setModifiedDate(new Date(System.currentTimeMillis()));
			ipv4NodeRepository.save(node);
		}
		return results;

	}

	private void checkforExceptions(List<IPv4Node> list, String value) {
		if (null == list || list.size() <= 0) {
			throw new IPv4NotFoundException("[" + value + "] - not found with Manager. Pleae check the input value.");
		}
	}

}
