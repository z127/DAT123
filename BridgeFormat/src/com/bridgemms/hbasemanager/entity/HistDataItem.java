package com.bridgemms.hbasemanager.entity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;

import com.justone.health.core.data.dataitem.DataItem;

public class HistDataItem
{

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
	private static Log log = LogFactory.getLog(HistDataItem.class);
	public byte[] histData;
	public List<byte[]> histDataList = new ArrayList<byte[]>();
	public long stime;
	public long etime;
	private int histcounter = 0;
	
	//changed for new encode method.
	private final int itemSize = 12;// long+float
	//private final int itemSize = ?;
	
	//added for new encode method.
	//private long lastTime;
	//private int delta = 0;
	//private int dod = 0;

	public HistDataItem(long t0, int num, long interval)
	{
		histData = new byte[num * itemSize];
		this.getStEt(t0, interval);
	}

	public HistDataItem(long t0, long interval)
	{
		histData = new byte[3000 * itemSize];
		this.getStEt(t0, interval);
	}

	private void getStEt(long t0, long interval)
	{
		this.stime = t0 - t0 % interval;
		this.etime = this.stime + interval;
	}

	public boolean isOver(long t0)
	{
		if (t0 >= stime && t0 < etime)
			return false;
		else
			return true;
	}

	public void write(DataItem di)
	{
		byte[] temp = new byte[12];
		System.arraycopy(Bytes.toBytes(di.getT0() - this.stime), 0, temp, 0, 8);
		System.arraycopy(Bytes.toBytes(di.getValue()), 0, temp, 8, 4);
		this.histcounter++;
		histDataList.add(temp);

	}

	public void flushToHistFile(BufferedWriter histdos, String channelid)
			throws IOException
	{
		// TODO Auto-generated method stub
		// log.info("flush to hist file : "+channelid+"_"+sdf.format(this.stime));

		// histdos.write(channelid + "_" + this.stime);
		// histdos.write("#");
		// histdos.write(Base64.encodeBytes(
		// Bytes.head(this.histData, this.histcounter * this.itemSize),
		// Base64.DONT_BREAK_LINES));
		// histdos.write("\r\n");
		// this.histcounter = 0;

		byte[] histDataTemp = new byte[histDataList.size() * 12];
		for (int i = 0; i < histDataList.size(); i++)
		{
			System.arraycopy(histDataList.get(i), 0, histDataTemp,
					i * itemSize, 12);
		}
		histdos.write(channelid + "_" + this.stime);
		histdos.write("#");
		histdos.write(Base64.encodeBytes(
				Bytes.head(histDataTemp, this.histcounter * this.itemSize),
				Base64.DONT_BREAK_LINES));
		histdos.write("\r\n");
		this.histcounter = 0;

	}
}
