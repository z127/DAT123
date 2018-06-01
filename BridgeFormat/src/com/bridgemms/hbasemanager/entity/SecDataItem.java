package com.bridgemms.hbasemanager.entity;

import java.io.BufferedWriter;
import java.io.IOException;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;

import com.justone.health.core.data.dataitem.DataItem;

public class SecDataItem extends StatisDataItem
{

	// Log log = LogFactory.getLog(SecDataItem.class);
	private final static long SEC_INTERVAL_ITEM = 1000;
	private final static long SEC_INTERVAL = 60 * SEC_INTERVAL_ITEM;

	private long itemStime;
	private long itemEtime;
	private int itemcounter;
	private float sum;

	public SecDataItem(long t0, int num)
	{
		super(t0, num, SEC_INTERVAL, SEC_INTERVAL_ITEM);
	}

	public SecDataItem(long t0)
	{
		super(t0, 60, SEC_INTERVAL, SEC_INTERVAL_ITEM);
	}

	public void flushToStatisFile(BufferedWriter dos, String channelid)
			throws IOException
	{
		// TODO Auto-generated method stub
		// log.info("flush to statis file : "+channelid+ "_"
		// +"Sec"+"_"+sdf.format(this.stime));
		byte[] MaxDataTemp = new byte[Max.size() * 12];
		for (int i = 0; i < Max.size(); i++)
		{
			System.arraycopy(Max.get(i), 0, MaxDataTemp, i * 12, 12);
		}
		byte[] MinDataTemp = new byte[Min.size() * 12];
		for (int i = 0; i < Min.size(); i++)
		{
			System.arraycopy(Min.get(i), 0, MinDataTemp, i * 12, 12);
		}
		byte[] AvgDataTemp = new byte[Avg.size() * 12];
		for (int i = 0; i < Avg.size(); i++)
		{
			System.arraycopy(Avg.get(i), 0, AvgDataTemp, i * 12, 12);
		}
		dos.write(channelid + "_" + "Sec" + "_" + this.stime);
		dos.write("#");
		dos.write(Base64.encodeBytes(
				Bytes.head(MaxDataTemp, this.maxCounter * this.ITEM_SIZE),
				Base64.DONT_BREAK_LINES));
		dos.write("#");
		dos.write(Base64.encodeBytes(
				Bytes.head(MinDataTemp, this.minCounter * this.ITEM_SIZE),
				Base64.DONT_BREAK_LINES));
		dos.write("#");
		dos.write(Base64.encodeBytes(
				Bytes.head(AvgDataTemp, this.avgCounter * this.ITEM_SIZE),
				Base64.DONT_BREAK_LINES));
		dos.write("\r\n");
	}

	private void getItemStEt(DataItem di)
	{
		this.itemStime = di.getT0() - (di.getT0() % SEC_INTERVAL_ITEM);
		this.itemEtime = this.itemStime + SEC_INTERVAL_ITEM;
	}

	private boolean isItemOver(long t0)
	{
		if (t0 >= this.itemStime && t0 < this.itemEtime)
			return false;
		else
			return true;
	}

	public void setStatis(DataItem di)
	{
		if (this.itemStime == 0l)
		{
			this.getItemStEt(di);
		}
		if (isItemOver(di.getT0()))
		{
			this.avg = this.sum / this.itemcounter;
			this.write(this.itemStime);
			this.itemStime = 0l;
			this.itemEtime = 0l;
			this.itemcounter = 0;
			this.max = Float.MIN_VALUE;
			this.min = Float.MAX_VALUE;
			this.sum = 0.0f;
		}
		if (di.getValue() > this.max)
			this.max = di.getValue();
		if (di.getValue() < this.min)
			this.min = di.getValue();
		this.sum += di.getValue();
		this.itemcounter++;

	}
}
