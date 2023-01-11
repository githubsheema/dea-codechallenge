package com.mycompany.ipv4manager.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;

import com.mycompany.ipv4manager.exception.IPv4ConflictException;
import com.mycompany.ipv4manager.model.IPv4Block;
import com.mycompany.ipv4manager.model.IPv4Node;
import com.mycompany.ipv4manager.model.IPv4NodeStatus;
import com.mycompany.ipv4manager.repository.IPv4AMBlockRepository;
import com.mycompany.ipv4manager.repository.IPv4AMNodRepository;
import com.mycompany.ipv4manager.service.impl.IPv4AMServiceImpl;
import com.mycompany.ipv4manager.util.NetUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IPv4AMServiceTest {

	@MockBean
	private IPv4AMBlockRepository ipv4BlockRepository;

	@MockBean
	private IPv4AMNodRepository ipv4NodeRepository;

	
	@Autowired
	@InjectMocks
	private IPv4AMServiceImpl ipv4Service;

	
	
	@Test
	public void testCreateIPBlock() throws Exception {
		
		Mockito.when(ipv4NodeRepository.findAll(Mockito.<Example<IPv4Node>>any()))
				.thenReturn(null);
		
		Mockito.when(ipv4BlockRepository.searchBlock(Mockito.any(String.class)))
		.thenReturn(null);
		
		IPv4Block result = ipv4Service.createIPBlock("1.1.1.1/24");
		
		assertNotNull(result);
		
		assertEquals("1.1.1.1/24", result.getBlock());
	}

	@Test
	public void testGetAllIPs() throws Exception {
		
		Mockito.when(ipv4NodeRepository.findAll())
				.thenReturn(sampleListIPs(5, IPv4NodeStatus.ACQUIRED));
		
		List<IPv4Node> result = ipv4Service.getAllIPs();
		
		assertNotNull(result);
		
		assertEquals(5, result.size());
		assertEquals(IPv4NodeStatus.ACQUIRED, result.get(0).getStatus());
		
	}
	
	@Test
	public void testAcquireIP() throws Exception {
		
		Mockito.when(ipv4NodeRepository.findAll(Mockito.<Example<IPv4Node>>any()))
		.thenReturn(sampleListIPs(1, IPv4NodeStatus.AVAILABLE));
		
		List<IPv4Node> result = ipv4Service.acquireIP("1.1.1.1");
		
		assertNotNull(result);
		
		assertEquals(1, result.size());
		assertEquals(IPv4NodeStatus.ACQUIRED, result.get(0).getStatus());
		
	}
	
	@Test
	public void testReleaseIP() throws Exception {
		
		Mockito.when(ipv4NodeRepository.findAll(Mockito.<Example<IPv4Node>>any()))
		.thenReturn(sampleListIPs(1, IPv4NodeStatus.ACQUIRED));
		
		List<IPv4Node> result = ipv4Service.releaseIP("1.1.1.1");
		
		assertNotNull(result);
		
		assertEquals(1, result.size());
		assertEquals(IPv4NodeStatus.AVAILABLE, result.get(0).getStatus());
		
	}

	@Test(expected = IPv4ConflictException.class)
	public void testAcquireIP_Exception() throws Exception {
		Mockito.when(ipv4NodeRepository.findAll(Mockito.<Example<IPv4Node>>any()))
		.thenReturn(sampleListIPs(1, IPv4NodeStatus.ACQUIRED));
		
		List<IPv4Node> result = ipv4Service.acquireIP("1.1.1.1");
	}
	
	@Test(expected = IPv4ConflictException.class)
	public void testReleaseIP_Exception() throws Exception {
		Mockito.when(ipv4NodeRepository.findAll(Mockito.<Example<IPv4Node>>any()))
		.thenReturn(sampleListIPs(1, IPv4NodeStatus.AVAILABLE));
		
		List<IPv4Node> result = ipv4Service.releaseIP("1.1.1.1");
	}
	
	
	private List<IPv4Node> sampleListIPs(int i, IPv4NodeStatus status) {
		List<IPv4Node> nodes = new ArrayList<IPv4Node>();

		for (int j = 1; j <= i; j ++) {
			nodes.add(createSampleNode("1.1.1." + Integer.toString(j), status));
		}
		return nodes;
	}

	private IPv4Node createSampleNode(String IP, IPv4NodeStatus status) {

		IPv4Node node = new IPv4Node();
		node.setIpAddress(IP);
		node.setStatus(status);
		return node;
	}
	
	private IPv4Block createMockIPBlock(String block) {
		NetUtil util = new NetUtil();

		IPv4Block blkobj = new IPv4Block(block, "user");
		IPv4Node node1 = util.createByIPString("1.1.1.1", "user");
		node1.setIpv4Block(blkobj);

		return blkobj;
	}

}