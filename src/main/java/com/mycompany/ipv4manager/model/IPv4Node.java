package com.mycompany.ipv4manager.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mycompany.ipv4manager.util.NetUtil;

/**
 * 
 * @author mathi
 * Model to reflect IPv4Node
 *
 */
@Entity
@Table (name="ipv4node")
@JsonPropertyOrder({ "ipAddress", "netMask", "status", "createdBy", "createdDate", "modifiedDate" })
public class IPv4Node {
	/*
	 * JsonIgnore is used to avoid values in response
	 */
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer ipid;
	
	/*
	 * Validates are not necessary .. but a fail safe
	 * IP is stored as 4 int pockets (each octed in decimal)
	 */

	@NotNull
	@Min(0)
	@Max(255)
	@JsonIgnore
	private Integer oct1;

	@NotNull
	@Min(0)
	@Max(255)
	@JsonIgnore
	private Integer oct2;

	@NotNull
	@Min(0)
	@Max(255)
	@JsonIgnore
	private Integer oct3;

	@NotNull
	@Min(0)
	@Max(255)
	@JsonIgnore
	private Integer oct4;

	
	/*
	 * Status is stored as int type
	 */
	@NotNull
	@Column(columnDefinition = "int default 0")
	@Enumerated(value = EnumType.ORDINAL)
	private IPv4NodeStatus status = IPv4NodeStatus.AVAILABLE;

	private Date createDate;

	@Length(max = 64)
	private String createdBy;

	private Date modifiedDate;
	
	/*
	 * Related to IPv4Block that identifies subnetmask and network
	 */

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "blockid")
	@JsonIgnore
	private IPv4Block ipv4Block;

	
	/*
	 * Transients are stored for convinient purposes
	 * Easy to render response with readable values
	 */
	
	@Transient
	private String ipAddress;

	@Transient
	private String netMask;

	/*
	 * We want to be able to fill in transients
	 * after db call is maded (fetched)
	 */
	@PostLoad
	public void initTransientsPost() {
		fillin();
	}

	private void fillin() {
		ipAddress = Integer.toString(oct1) + "." + Integer.toString(oct2) + "." + Integer.toString(oct3) + "."
				+ Integer.toString(oct4);

		if (null != getIpv4Block()) {
			netMask = NetUtil.getSubNet(getIpv4Block().getBlock());
		}
	}

	/*
	 * We want to populate create/modified
	 * date (could have been done in DB trigger also)
	 */
	@PrePersist
	public void initTransientsPre() {
		fillin();
		if (null == this.createDate) {
			this.setCreateDate(new Date(System.currentTimeMillis()));
		}
		this.setModifiedDate(new Date(System.currentTimeMillis()));

	}

	/*
	 * various constructors used for convinient purposes
	 * Spring uses the default constructor for injection.
	 */
	public IPv4Node() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IPv4Node(Integer ipid, Integer oct1, Integer oct2, Integer oct3, Integer oct4, Date createDate,
			String createdBy, Date modifiedDate) {
		super();
		this.ipid = ipid;
		this.oct1 = oct1;
		this.oct2 = oct2;
		this.oct3 = oct3;
		this.oct4 = oct4;
		this.createDate = createDate;
		this.createdBy = createdBy;
		this.modifiedDate = modifiedDate;
	}

	public IPv4Node(int i, int j, int k, int l, String user) {
		this.oct1 = i;
		this.oct2 = j;
		this.oct3 = k;
		this.oct4 = l;
		this.createdBy = user;
	}
/*
 * special constructor for example query
 */
	public IPv4Node(int i, int j, int k, int l) {
		this.oct1 = i;
		this.oct2 = j;
		this.oct3 = k;
		this.oct4 = l;
	}

	
	public IPv4NodeStatus getStatus() {
		return status;
	}

	public void setStatus(IPv4NodeStatus status) {
		this.status = status;
	}

	public IPv4Block getIpv4Block() {
		return ipv4Block;
	}

	public void setIpv4Block(IPv4Block ipv4Block) {
		this.ipv4Block = ipv4Block;
	}

	public Integer getIpid() {
		return ipid;
	}

	public void setIpid(Integer id) {
		this.ipid = id;
	}

	public Integer getOct1() {
		return oct1;
	}

	public void setOct1(Integer oct1) {
		this.oct1 = oct1;
	}

	public Integer getOct2() {
		return oct2;
	}

	public void setOct2(Integer oct2) {
		this.oct2 = oct2;
	}

	public Integer getOct3() {
		return oct3;
	}

	public void setOct3(Integer oct3) {
		this.oct3 = oct3;
	}

	public Integer getOct4() {
		return oct4;
	}

	public void setOct4(Integer oct4) {
		this.oct4 = oct4;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getNetMask() {
		return netMask;
	}

	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}

	@Override
	public String toString() {
		return "IPv4Node [ipid=" + ipid + ", oct1=" + oct1 + ", oct2=" + oct2 + ", oct3=" + oct3 + ", oct4=" + oct4
				+ ", status=" + status + ", createDate=" + createDate + ", createdBy=" + createdBy + ", modifiedDate="
				+ modifiedDate + ", ipv4Block=" + ipv4Block + ", ipAddress=" + ipAddress + ", netMask=" + netMask + "]";
	}

}
