package com.bridgemms.hbasemanager.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class CoproDataItem implements Writable{
	
	long timestamp = 0l;
	float value = 0;
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		this.timestamp = in.readLong();
		this.value = in.readFloat();
		
	}
	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeLong(this.timestamp);
		out.writeFloat(this.value);
	}
	
}
