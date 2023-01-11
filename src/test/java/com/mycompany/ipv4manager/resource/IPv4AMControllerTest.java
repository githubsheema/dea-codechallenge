package com.mycompany.ipv4manager.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.mycompany.ipv4manager.model.IPv4Block;
import com.mycompany.ipv4manager.model.IPv4Node;
import com.mycompany.ipv4manager.model.IPv4NodeStatus;
import com.mycompany.ipv4manager.repository.IPv4AMBlockRepository;
import com.mycompany.ipv4manager.service.IPv4AMService;
import com.mycompany.ipv4manager.util.NetUtil;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@WebMvcTest(value = IPv4AMController.class)
public class IPv4AMControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IPv4AMService ipv4Service;

	@MockBean
	private IPv4AMBlockRepository repo;

	private IPv4Block createMockIPBlock(String block) {
		NetUtil util = new NetUtil();

		IPv4Block blkobj = new IPv4Block(block, "user");
		IPv4Node node1 = util.createByIPString("1.1.1.1", "user");
		node1.setIpv4Block(blkobj);

		return blkobj;
	}

	@Test
	public void testCreateIPBlock() throws Exception {

		String expected = "{\"block\":\"1.1.1.1/24\",\"createDate\":null,\"createdBy\":\"user\",\"modifiedDate\":null,\"iplist\":null,\"_links\":{\"to-get-ips-click-here\":{\"href\":\"http://localhost/getAllIPs\"}}}";
		Mockito.when(ipv4Service.createIPBlock(Mockito.anyString())).thenReturn(createMockIPBlock("1.1.1.1/24"));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/createBlock").accept(MediaType.APPLICATION_JSON)
				.content("1.1.1.1/24").contentType(MediaType.TEXT_PLAIN);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		assertNotNull(result);
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), true);
	}

	@Test
	public void test404() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/wronguri").accept(MediaType.APPLICATION_JSON)
				.content("1.1.1.1/24").contentType(MediaType.TEXT_PLAIN);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		assertNotNull(result);
		assertThat(result.getResponse().getStatus(), is(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	public void test406() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/createBlock").accept(MediaType.APPLICATION_JSON)
				.content("1.1.1.1/24").contentType(MediaType.TEXT_PLAIN);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		assertNotNull(result);
		assertThat(result.getResponse().getStatus(), is(HttpStatus.METHOD_NOT_ALLOWED.value()));
	}

	@Test
	public final void testListAddresses() throws Exception {

		String expected = "[{\"ipAddress\":\"1.1.1.1\",\"netMask\":null,\"status\":\"AVAILABLE\",\"createdBy\":null,\"modifiedDate\":null,\"createDate\":null},{\"ipAddress\":\"1.1.1.2\",\"netMask\":null,\"status\":\"AVAILABLE\",\"createdBy\":null,\"modifiedDate\":null,\"createDate\":null}]";
		Mockito.when(ipv4Service.getAllIPs()).thenReturn(sampleListIPs(2, IPv4NodeStatus.AVAILABLE));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/getAllIPs").accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.TEXT_PLAIN);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		assertNotNull(result);
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), true);

	}

	@Test
	public final void testAcquireIP() throws Exception {
		String expected = "[{\"ipAddress\":\"1.1.1.1\",\"netMask\":null,\"status\":\"ACQUIRED\",\"createdBy\":null,\"modifiedDate\":null,\"createDate\":null}]";
		Mockito.when(ipv4Service.acquireIP(Mockito.anyString()))
				.thenReturn(sampleListIPs(1, IPv4NodeStatus.ACQUIRED));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/acquireIP").accept(MediaType.APPLICATION_JSON)
				.content("1.1.1.1").contentType(MediaType.TEXT_PLAIN);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		assertNotNull(result);
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), true);
	}

	@Test
	public final void testReleaseIP() throws Exception {
		String expected = "[{\"ipAddress\":\"1.1.1.1\",\"netMask\":null,\"status\":\"AVAILABLE\",\"createdBy\":null,\"modifiedDate\":null,\"createDate\":null}]";
		Mockito.when(ipv4Service.releaseIP(Mockito.anyString()))
				.thenReturn(sampleListIPs(1, IPv4NodeStatus.AVAILABLE));

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/releaseIP").accept(MediaType.APPLICATION_JSON)
				.content("1.1.1.1").contentType(MediaType.TEXT_PLAIN);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		assertNotNull(result);
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), true);
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
}