package com.bridgemms.hbasemanager.entity;

public class DataRecord {
	/**
	 * HBase��¼��keyֵ
	 */
	private String key;
	/**
	 * HBase��¼��valueֵ
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
