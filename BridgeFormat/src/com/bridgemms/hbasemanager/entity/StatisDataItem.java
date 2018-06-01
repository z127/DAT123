package com.bridgemms.hbasemanager.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.util.Bytes;

public class StatisDataItem
{

	protected static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
	// Log log = LogFactory.getLog(StatisDataItem.class);
	protected List<byte[]> Min;
	protected List<byte[]> Max;
	protected List<byte[]> Avg;
	protected float min = Float.MAX_VALUE;
	protected float max = Float.MIN_VALUE;
	protected float avg = 0;
	protected long stime;
	protected long etime;
	protected int minCounter;
	protected int maxCounter;
	protected int avgCounter;
	protected final int ITEM_SIZE = 12;// long+float

	public StatisDataItem(long t0, int num, long interval, long itemInterval)
	{
		Min = new ArrayList<byte[]>();
		Max = new ArrayList<byte[]>();
		Avg = new ArrayList<byte[]>();
		// Avg = new byte[num*ITEM_SIZE];
		this.getStEt(t0, interval, itemInterval);
	}

	public StatisDataItem(long t0, long interval, long itemInterval)
	{
		Min = new ArrayList<byte[]>();
		Max = new ArrayList<byte[]>();
		Avg = new ArrayList<byte[]>();
		// Avg = new byte[60*ITEM_SIZE];
		this.getStEt(t0, interval, itemInterval);
	}

	private void getStEt(long t0, long interval, long itemInterval)
	{
		this.stime = t0 - (t0 % itemInterval);
		this.etime = this.stime + interval;
	}

	public boolean isOver(long t0)
	{
		if (t0 >= stime && t0 < etime)
			return false;
		else
			return true;
	}

	protected void write(long itemStime)
	{
		// TODO Auto-generated method stub
		this.writeMax(itemStime);
		this.writeMin(itemStime);
		this.writeAvg(itemStime);
	}

	protected void writeMax(long itemStime)
	{
		// log.debug("stime,etime = "+sdf.format(this.stime)+","+sdf.format(this.etime)+" , timestamp = "+sdf.format(itemStime)+" , byte length = "+
		// Max.length+" , item size = "+this.ITEM_SIZE+" , value counter = "+this.maxCounter);
		// if(maxCounter < Max.length/this.ITEM_SIZE)
		// {
		// System.arraycopy(Bytes.toBytes(itemStime-this.stime), 0, this.Max,
		// this.maxCounter*this.ITEM_SIZE, 8);
		// System.arraycopy(Bytes.toBytes(this.max), 0, this.Max,
		// this.maxCounter*this.ITEM_SIZE+8, 4);
		// this.maxCounter++;
		// }
		// else
		// {
		// //log.error("Array out of bound!  "+"stime,etime = "+sdf.format(this.stime)+","+sdf.format(this.etime)+" , timestamp = "+sdf.format(itemStime)+" , byte length = "+
		// //Max.length+" , item size = "+this.ITEM_SIZE+" , value counter = "+this.maxCounter);
		// System.exit(1);
		// System.out.println(maxCounter+":"+Max.length/this.ITEM_SIZE);
		// }
		byte[] temp = new byte[12];
		System.arraycopy(Bytes.toBytes(itemStime - this.stime), 0, temp, 0, 8);
		System.arraycopy(Bytes.toBytes(this.max), 0, temp, 8, 4);
		this.maxCounter++;
		Max.add(temp);
	}

	protected void writeMin(long itemStime)
	{
		// log.debug("stime,etime = "+sdf.format(this.stime)+","+sdf.format(this.etime)+" , timestamp = "+sdf.format(itemStime)+" , byte length = "+
		// Min.length+" , item size = "+this.ITEM_SIZE+" , value counter = "+this.minCounter);
		// if(minCounter < Min.length/this.ITEM_SIZE)
		// {
		// System.arraycopy(Bytes.toBytes(itemStime-this.stime), 0, this.Min,
		// this.minCounter*this.ITEM_SIZE, 8);
		// System.arraycopy(Bytes.toBytes(this.min), 0, this.Min,
		// this.minCounter*this.ITEM_SIZE+8, 4);
		// this.minCounter++;
		// }
		// else
		// {
		// //log.error("Array out of bound!  "+"stime,etime = "+sdf.format(this.stime)+","+sdf.format(this.etime)+" , timestamp = "+sdf.format(itemStime)+" , byte length = "+
		// //Max.length+" , item size = "+this.ITEM_SIZE+" , value counter = "+this.maxCounter);
		// System.exit(1);
		// }
		byte[] temp = new byte[12];
		System.arraycopy(Bytes.toBytes(itemStime - this.stime), 0, temp, 0, 8);
		System.arraycopy(Bytes.toBytes(this.min), 0, temp, 8, 4);
		this.minCounter++;
		Min.add(temp);
	}

	protected void writeAvg(long itemStime)
	{
		// log.debug("stime,etime = "+sdf.format(this.stime)+","+sdf.format(this.etime)+" , timestamp = "+sdf.format(itemStime)+" , byte length = "+
		// Avg.length+" , item size = "+this.ITEM_SIZE+" , value counter = "+this.avgCounter);
		// if(avgCounter < Avg.length/this.ITEM_SIZE)
		// {
		// System.arraycopy(Bytes.toBytes(itemStime-this.stime), 0, this.Avg,
		// this.avgCounter*this.ITEM_SIZE, 8);
		// System.arraycopy(Bytes.toBytes(this.avg), 0, this.Avg,
		// this.avgCounter*this.ITEM_SIZE+8, 4);
		// this.avgCounter++;
		// }
		// else
		// {
		// //log.error("Array out of bound!  "+"stime,etime = "+sdf.format(this.stime)+","+sdf.format(this.etime)+" , timestamp = "+sdf.format(itemStime)+" , byte length = "+
		// //Max.length+" , item size = "+this.ITEM_SIZE+" , value counter = "+this.maxCounter);
		// System.exit(1);
		// }
		byte[] temp = new byte[12];
		System.arraycopy(Bytes.toBytes(itemStime - this.stime), 0, temp,
				0, 8);
		System.arraycopy(Bytes.toBytes(this.avg), 0, temp, 8,
				4);
		this.avgCounter++;
		Avg.add(temp);
	}
}
