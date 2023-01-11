package com.mycompany.ipv4manager.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mycompany.ipv4manager.util.NetUtil;

/**
 * 
 * @author mathi
 * Model to reflect IPv4Block
 *
 */

@Entity
@Table (name="ipv4block")
public class IPv4Block {
	/*
	 * JsonIgnore is used to avoid values in response
	 */
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer blockid;

	/*
	 * Validates are not necessary .. but a fail safe
	 * xxx.xxx.xxx.xxx/xx (max of 18 chars)
	 */
	@NotNull
	@Length(max = 18, message = NetUtil.CIDR_FMT_WARN)
	private String block;

	private Date createDate;

	@Length(max = 64)
	private String createdBy;

	private Date modifiedDate;

	@OneToMany(mappedBy = "ipv4Block", cascade = CascadeType.ALL)
	private List<IPv4Node> iplist;

	public IPv4Block() {
	}

	public IPv4Block(String block, String createdBy) {
		super();
		this.block = block;
		this.createdBy = createdBy;
	}

	public List<IPv4Node> getIplist() {
		return iplist;
	}

	public void setIplist(List<IPv4Node> iplist) {
		this.iplist = iplist;
	}

	public Integer getBlockid() {
		return blockid;
	}

	public void setBlockid(Integer blockid) {
		this.blockid = blockid;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
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

	@Override
	public String toString() {
		return "IPv4Block [blockid=" + blockid + ", block=" + block + ", createDate=" + createDate + ", createdBy="
				+ createdBy + ", modifiedDate=" + modifiedDate + "]";

	}

	private void fillin() {
		if (null == this.createDate) {
			this.setCreateDate(new Date(System.currentTimeMillis()));
		}
		this.setModifiedDate(new Date(System.currentTimeMillis()));
	}

	@PrePersist
	public void initDates() {
		fillin();
	}

}
