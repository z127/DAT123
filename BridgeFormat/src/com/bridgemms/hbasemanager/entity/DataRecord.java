package com.bridgemms.hbasemanager.entity;

public class DataRecord {
	/**
	 * HBase记录的key值
	 */
	private String key;
	/**
	 * HBase记录的value值
	 */
	private byte[] data;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	
}
