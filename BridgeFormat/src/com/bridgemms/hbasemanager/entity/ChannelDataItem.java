package com.bridgemms.hbasemanager.entity;

public class ChannelDataItem {
	private int id = 0;
	private String code = null;
	private String name = null;
	private String type = null;
	private int timeperrow = 0;
	private double frequence = 0.0;
	private String location = null;
	private String parent = null;
	private short nodetype = 0;
	/**
	 * ���ͨ��id
	 * @return ͨ��id
	 */
	public int getId() {
		return id;
	}
	/**
	 * ����ͨ��id
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getTimeperrow() {
		return timeperrow;
	}
	public void setTimeperrow(int timeperrow) {
		this.timeperrow = timeperrow;
	}
	public double getFrequence() {
		return frequence;
	}
	public void setFrequence(double frequence) {
		this.frequence = frequence;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public short getNodetype() {
		return nodetype;
	}
	public void setNodetype(short nodetype) {
		this.nodetype = nodetype;
	}
	
}
