package com.mycompany.ipv4manager.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.util.SubnetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mycompany.ipv4manager.model.IPv4Node;

@Component
public class NetUtil {

	public static final Logger log = LoggerFactory.getLogger(NetUtil.class);
	public static final String HOST_MASK = "255.255.255.255";
	public static final String CIDR_FMT_WARN = "must meet CDIR format : https://en.wikipedia.org/wiki/Classless_Inter-Domain_Routing";

	public static List<String> findIPRangeList(String cidr) {

		SubnetUtils util = new SubnetUtils(cidr);

		util.setInclusiveHostCount(true);

		return Arrays.asList(util.getInfo().getAllAddresses());
	}

	public static IPv4Node createByIPString(String ip, String user) {

		int[] octs = CreateNodewithIPOctOnly(ip);

		return (null != octs) ? new IPv4Node(octs[0], octs[1], octs[2], octs[3], user) : null;
	}

	private static int[] CreateNodewithIPOctOnly(String ip) {
		SubnetUtils util = new SubnetUtils(ip, HOST_MASK); // Should not get Exception here as it is already given by
															// SubnetUtils only
		util.setInclusiveHostCount(true);
		int[] octs = toArray(util.getInfo().asInteger(ip)); //
		return octs;
	}

	/*
	 * Convert a packed integer address into a 4-element array
	 */
	public static int[] toArray(int val) {
		int ret[] = new int[4];
		for (int j = 3; j >= 0; --j) {
			ret[j] |= ((val >>> 8 * (3 - j)) & (0xff));
		}
		return ret;
	}

	public static String getSubNet(String cidr) {
		SubnetUtils util = new SubnetUtils(cidr);
		util.setInclusiveHostCount(true);
		return util.getInfo().getNetmask();

	}

	/**
	 *  To set transient variable setNetMask
	 * @param node
	 */
	public static void setSubNet(IPv4Node node) {

		if (node.getIpv4Block() != null) {

			SubnetUtils util = new SubnetUtils(node.getIpv4Block().getBlock());
			util.setInclusiveHostCount(true);
			node.setNetMask(util.getInfo().getNetmask());
		}

	}
	
	/**
	 *  This method creates an example IPV4Node for search purposes.
	 *  All except octed values are set Null.
	 * @param ip
	 * @return IPv4Node
	 */

	public static IPv4Node createByIPStringExample(String ip) {
		IPv4Node node = null;

		int[] octs = CreateNodewithIPOctOnly(ip);

		if (null != octs && octs.length == 4) {
			node = new IPv4Node(octs[0], octs[1], octs[2], octs[3]);
			node.setStatus(null);
		}

		return node;

	}

}
