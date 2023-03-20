package com.mycompany.ipv4manager.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.ipv4manager.model.IPv4Block;
import com.mycompany.ipv4manager.model.IPv4Node;
import com.mycompany.ipv4manager.service.IPv4AMService;
import com.mycompany.ipv4manager.util.NetUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author mathi Main RestController for IPv4 Manager
 *
 */

@Api(tags = {
		"ipv4 Manager" }, description = "The RestController used for managing ipv4 Blocks.", produces = "JSON representation of IPv4's status information")
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Validated
public class IPv4AMController {

	public static final Logger log = LoggerFactory.getLogger(IPv4AMController.class);
	public static final String IP_REGEX = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
	private static final String CIDR_REGEX = IP_REGEX + "/(\\d{1,3})";

	// Inject Service/Business Bean
	@Autowired
	IPv4AMService ipv4Service;


	@ApiOperation(value = "Create Block of IPs", notes = "Input: CIDR format IP Block, Output: Details of provisioned block if successful")
	@PutMapping(path = "/createBlock")
	public Resource<IPv4Block> createIPBlock(
			@RequestBody @Valid @NotNull @Pattern(regexp = CIDR_REGEX, message = NetUtil.CIDR_FMT_WARN) String block) {

		log.debug("Create IP Block Call - Received {}", block);
		IPv4Block ipv4Block = ipv4Service.createIPBlock(block);
		Resource<IPv4Block> resource = new Resource<IPv4Block>(ipv4Block);
		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).listAddresses());
		resource.add(linkTo.withRel("to-get-ips-click-here"));
		// HATEOAS
		return resource;

	}

	@ApiOperation(value = "GetAllIPs with Status", notes = "Input: none, Output: All IPs with their status")
	@GetMapping(path = "/getAllIPs")
	public List<IPv4Node> listAddresses() {

		log.debug("List IP Block Call");
		List<IPv4Node> list = ipv4Service.getAllIPs();
		return list;
	}

	@ApiOperation(value = "Acquire an IP", notes = "Input: IPv4 format, Output: If successful, the status of IP")
	@PutMapping(path = "/acquireIP")
	public List<IPv4Node> acquireIP(
			@RequestBody @NotNull @Valid @Pattern(regexp = IP_REGEX, message = "must meet IPv4 Format") String ip) {

		log.debug("Acquire IP Block Call");
		List<IPv4Node> list = ipv4Service.acquireIP(ip);
		return list;
	}

	@ApiOperation(value = "Release an IP", notes = "Input: IPv4 format, Output: If successful, the status of IP")
	@PutMapping(path = "/releaseIP")
	public List<IPv4Node> releaseIP(
			@RequestBody @NotNull @Valid @Pattern(regexp = IP_REGEX, message = "must meet IPv4 Format") String ip) {

		log.debug("Relase IP Block Call");
		List<IPv4Node> list = ipv4Service.releaseIP(ip);
		return list;
	}

}
