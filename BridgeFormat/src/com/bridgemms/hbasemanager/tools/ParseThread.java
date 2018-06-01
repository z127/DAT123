package com.bridgemms.hbasemanager.tools;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bridgemms.hbasemanager.channelpra.ChannelPraHandler;
import com.bridgemms.hbasemanager.entity.ChannelDataItem;
import com.bridgemms.hbasemanager.entity.DayDataItem;
import com.bridgemms.hbasemanager.entity.FileQueue;
import com.bridgemms.hbasemanager.entity.HistDataItem;
import com.bridgemms.hbasemanager.entity.HourDataItem;
import com.bridgemms.hbasemanager.entity.MinDataItem;
import com.bridgemms.hbasemanager.entity.SecDataItem;
import com.justone.health.core.data.DataQueue;
import com.justone.health.core.data.dataitem.DataItem;
import com.justone.health.core.file.FileData;
//import com.justone.health.core.file.FileData;
import com.justone.health.core.file.FileNameParser;

public class ParseThread extends Thread
{
	private static Log log = LogFactory.getLog(Format.class);
	String channelid;
	public Map<String, FileQueue> fmap = new HashMap<String, FileQueue>();
	BufferedWriter statisdos = null;
	BufferedWriter histdos = null;
	FileData fileData = null;
	DataQueue dataQueue = null;
	DataItem[] allElementDatas = null;
	String statispath;
	String histpath;
	int id;// Thread id
	int allThreadNumber;// Thread number
	FileNameParser fpr = null;
	double frequency = 0.0;
	double formerTs = 0.0f;
	private HistDataItem histdata;
	private SecDataItem sdi;
	private MinDataItem mdi;
	private HourDataItem hdi;
	private DayDataItem ddi;
	private int bufferSize;
	private int timeInterval;

	ParseThread(String statispath, String histpath,
			Map<String, FileQueue> fmap, int id, int allThreadNumber,
			int bufferSize, int timeInterval)
	{
		this.fmap = fmap;
		this.statispath = statispath + id;
		this.histpath = histpath + id;
		this.id = id;
		this.allThreadNumber = allThreadNumber;
		this.bufferSize = bufferSize;
		this.timeInterval = timeInterval;
	}

	@Override
	public void run()
	{
		File fileResult = new File(histpath);
		try
		{
			statisdos = new BufferedWriter(
					new FileWriter(new File(statispath)), bufferSize);
			histdos = new BufferedWriter(new FileWriter(new File(histpath)),
					bufferSize);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if (statisdos == null || histdos == null)
		{
			return;
		}

		try
		{

			FileWriter fw = new FileWriter("./myresult" + id);

			int size = fmap.size();
			int averageSize = 0;

			if (size % allThreadNumber != 0)
			{
				averageSize = (allThreadNumber - size % allThreadNumber + size)
						/ allThreadNumber;
			} else
			{
				averageSize = size / allThreadNumber;
			}
			Map<String, FileQueue> fmapCurrent = new HashMap<String, FileQueue>();
			fmapCurrent.putAll(fmap);

			BufferedWriter bw = new BufferedWriter(fw);
			int i = 0;
			long filesize = 0;
			long count = 1;
			for (String sss : fmapCurrent.keySet())
			{
				if (i >= (id + 1) * averageSize || i >= size)
				{
					break;
				}
				if (i < id * averageSize)
				{
					i++;
					continue;
				}
				channelid = sss;
				int timeperrow = this.getTimeperrow(channelid); // 获得每行存储值
				long startTime = System.currentTimeMillis();
				formerTs = 0.0f;
				for (File f : fmapCurrent.get(sss).getFls())
				{
					filesize += f.length();
					if (filesize > count * 1073741824)
					{
						count++;
						bw.write(count + ":" + System.currentTimeMillis()
								+ "#####" + filesize);
						bw.newLine();
						bw.flush();
						System.out.println("Threadid============>" + id
								+ "---->fileSize------->" + filesize + ":"
								+ (System.currentTimeMillis() - startTime)
								/ 1000.0);
						startTime = System.currentTimeMillis();
					}
					fpr = new FileNameParser(f.getName());
					frequency = 1 / getDtNum(fpr.getDtString());
					fileData = new FileData(f);
					dataQueue = new DataQueue();
					fileData.readFile();
					fileData.putIntoQueue(dataQueue);// zzm changed

					allElementDatas = dataQueue.getAllElementDatas();

					int h = 0;
					for (DataItem di : allElementDatas)
					{

						if (formerTs >= di.getT0())
						{
							continue;
						} else
						{
							formerTs = di.getT0();
						}

						this.insHist(histdos, di, channelid, frequency,
								timeperrow);
						this.inStatis(statisdos, di, channelid, frequency);

					}
				}
				i++;
				histdata.flushToHistFile(histdos, channelid);
				histdata = null;
			}

			bw.close();
			synchronized (Format.outputSize)
			{
				Format.outputSize += fileResult.length();
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			try
			{
				statisdos.flush();
				histdos.flush();
				statisdos.close();
				histdos.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int getTimeperrow(String channelid)
	{
		// System.out.println(channelid);
		ChannelDataItem cdi = ChannelPraHandler.getItem(channelid);
		return cdi.getTimeperrow();
	}

	private double getDtNum(String tmp)
	{
		// TODO Auto-generated method stub
		if (tmp.matches("^[0-9]+D$"))
		{ // 天为单位
			return Double.parseDouble(tmp.replace("D", "")) * 24 * 3600;
		} else if (tmp.matches("^[0-9]+H$"))
		{ // 小时为单位
			return Double.parseDouble(tmp.replace("H", "")) * 3600;
		} else if (tmp.matches("^[0-9]+M$"))
		{ // 分钟为单位
			return Double.parseDouble(tmp.replace("M", "")) * 60;
		} else if (tmp.matches("^[0-9]+S$"))
		{ // 秒为单位
			return Double.parseDouble(tmp.replace("S", ""));
		} else if (tmp.matches("^[0-9]+MS$"))
		{ // 毫秒为单位
			return Double.parseDouble(tmp.replace("MS", "")) * 0.001;
		} else
		{
			log.fatal("Wrong Frequency format " + tmp);
			System.exit(1);
			return 0.0;
		}
	}

	// private void insHist(BufferedWriter histdos, DataItem di, String
	// channelid,
	// double frequency, int timeperrow) throws IOException
	private void insHist(BufferedWriter histdos, DataItem di, String channelid,
			double frequency, long timeperrow) throws IOException
	{
		// TODO Auto-generated method stub
		// System.out.println(timeperrow+"  "+frequency);

		// if (this.histdata == null)
		// this.histdata = new HistDataItem(di.getT0(),
		// (int) (timeperrow * frequency), (long) timeperrow * 1000);
		// if (this.histdata.isOver(di.getT0()))
		// {
		// this.histdata.flushToHistFile(histdos, channelid);
		// this.histdata = null;
		// this.insHist(histdos, di, channelid, frequency, timeperrow);
		// } else
		// this.histdata.write(di);
		if (histdata == null)
		{

			//if ((int) (timeInterval * frequency) > 0)
			if(frequency >= 1.0)
			{
				histdata = new HistDataItem(di.getT0(),
						(int) (timeInterval * frequency),
						(long) 60 * 1000);
			//} else if ((int) (timeInterval * frequency) == 0
			//		&& (int) (30 * 24 * timeInterval * frequency) > 0)
			} else if (frequency >= 0.01666666)
			{
				histdata = new HistDataItem(di.getT0(),
						(int) (60 * timeInterval * frequency), (long) 60 * 60 * 1000);
			} else
			{
				histdata = new HistDataItem(di.getT0(), 1,
						(long) (1 / frequency * 1000));
			}

		}
		if (histdata.isOver(di.getT0()))
		{

			histdata.flushToHistFile(histdos, channelid);
			histdata = null;
			insHist(histdos, di, channelid, frequency,
					(long) timeInterval * 1000);
		} else
			histdata.write(di);
	}

	private void inStatis(BufferedWriter statisdos, DataItem di,
			String channelid, double frequency) throws IOException
	{

		if (sdi == null)
			sdi = new SecDataItem(di.getT0());
		if (mdi == null)
			mdi = new MinDataItem(di.getT0());
		if (hdi == null)
			hdi = new HourDataItem(di.getT0());
		if (ddi == null)
			ddi = new DayDataItem(di.getT0());
		if (this.sdi.isOver(di.getT0()))
		{
			this.sdi.flushToStatisFile(statisdos, channelid);
			this.sdi = new SecDataItem(di.getT0());
		}
		if (this.mdi.isOver(di.getT0()))
		{
			this.mdi.flushToStatisFile(statisdos, channelid);
			this.mdi = new MinDataItem(di.getT0());
		}
		if (this.hdi.isOver(di.getT0()))
		{
			this.hdi.flushToStatisFile(statisdos, channelid);
			this.hdi = new HourDataItem(di.getT0());
		}
		if (this.ddi.isOver(di.getT0()))
		{
			this.ddi.flushToStatisFile(statisdos, channelid);
			this.ddi = new DayDataItem(di.getT0());
		}
		this.sdi.setStatis(di);
		this.mdi.setStatis(di);
		this.hdi.setStatis(di);
		this.ddi.setStatis(di);
	}

}
