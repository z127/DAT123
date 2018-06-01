package com.bridgemms.hbasemanager.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileQueue {

	/**
	 * @param args
	 */
	String Channelid;
	double frequency;
	List<File> fls;
	public FileQueue(String id,double f)
	{
		this.Channelid = id;
		this.frequency = f;
		this.fls = new ArrayList<File>();
	}
	public String getChannelid() {
		return Channelid;
	}
	public void setChannelid(String channelid) {
		Channelid = channelid;
	}
	public double getFrequency() {
		return frequency;
	}
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	public List<File> getFls() {
		return fls;
	}
	public void setFls(List<File> fls) {
		this.fls = fls;
	}
	
	public void addFile(File f)
	{
		this.fls.add(f);
	}
}
