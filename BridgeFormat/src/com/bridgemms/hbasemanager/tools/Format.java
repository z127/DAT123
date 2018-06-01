package com.bridgemms.hbasemanager.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import com.justone.health.core.file.FileNameParser;

public class Format
{
	private static Log log = LogFactory.getLog(Format.class);
	private HistDataItem histdata;
	private SecDataItem sdi;
	private MinDataItem mdi;
	private HourDataItem hdi;
	private DayDataItem ddi;

	/*
	 * configuration information
	 */
	static int threadNumber;
	static int timeInterval;
	static int bufferSize;
	static String inputPath;
	static String histPath;
	static String statisPath;
	public static long fileSize = 0;

	public static Long outputSize = new Long(0);

	/**
	 * @param args
	 */
	public void getFilels(Map<String, FileQueue> fmap, String path)
	{
		System.out.println(path);
		File dir = new File(path);
		String channelid = null;
		double frequency;
		FileQueue fls = null;
		FileNameParser fpr = null;
		if (dir.isDirectory())
		{
			for (File f : dir.listFiles())
			{
				this.getFilels(fmap, f.getAbsolutePath());
			}
		} else
		{
			System.out.println(dir.getName()+":dir.getName");
			fpr = new FileNameParser(dir.getName());
			channelid = fpr.getChannel();
			frequency = 1 / getDtNum(fpr.getDtString());
			if ((fls = fmap.get(channelid)) == null)
				fls = new FileQueue(channelid, frequency);
			fls.addFile(dir);
			fmap.put(channelid, fls);
			fileSize += dir.length();
		}
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

	public int getTimeperrow(String channelid)
	{
		ChannelDataItem cdi = ChannelPraHandler.getItem(channelid);
		return cdi.getTimeperrow();
	}

	public double getFrequency(String channelid)
	{
		ChannelDataItem cdi = ChannelPraHandler.getItem(channelid);
		return cdi.getFrequence();
	}

	public void InsData(String inpath, String statispath, String histpath)
			throws Exception
	{

		Map<String, FileQueue> fmap = new HashMap<String, FileQueue>();

		long getFile = System.currentTimeMillis();
		System.out.println("Get Files ....");
		this.getFilels(fmap, inpath);
		System.out.println("Get Files Finished...."
				+ (System.currentTimeMillis() - getFile));
		long sortFile = System.currentTimeMillis();
		System.out.println("Sort Files ...."+fmap.size());
		for (String s : fmap.keySet())
		{
			Collections.sort(fmap.get(s).getFls(), new Comparator<File>()
			{
				@Override
				public int compare(File arg0, File arg1)
				{
					return arg0.getName().compareTo(arg1.getName());
				}
			});
		}
		System.out.println("Sort Files Finished...."
				+ (System.currentTimeMillis() - sortFile));

		List<Thread> thread = new ArrayList<Thread>();
		for (int i = 0; i < threadNumber; i++)
		{

			Thread t = new ParseThread(statispath, histpath, fmap, i,
					threadNumber, bufferSize, timeInterval);
			thread.add(t);
		}
		for (int i = 0; i < thread.size(); i++)
		{
			thread.get(i).start();
		}
		for (int i = 0; i < thread.size(); i++)
		{
			if (thread.get(i).isAlive())
			{
				thread.get(i).join();
			} else
			{
				System.out.println("Thread" + i + "is overed");
			}
		}

	}

	public static void main(String[] args) throws Exception
	{

		long startTime = System.currentTimeMillis();
		System.out.println("start" + startTime);
		new Format().parseProperties();
		new Format().InsData(inputPath, statisPath, histPath);
		System.out.println("end");
		System.out.println("JarFile\t" + "BridgeFormat.jar");
		System.out.println("inputFile\t" + inputPath);
		System.out.println("statispath\t" + statisPath);
		System.out.println("inputSize\t" + fileSize);
		System.out.println("outputSize\t" + outputSize);
		System.out.println("ThreadNumber\t" + threadNumber);
		System.out.println(System.currentTimeMillis() - startTime);
	}

	public void parseProperties() throws Exception
	{
		Properties properties = new Properties();
		InputStream inputStream = new FileInputStream("config.properties");
		properties.load(inputStream);

		try
		{
			if (null == properties.getProperty("threadNumber"))
			{
				throw new Exception("threadNumber is null");
			}
			if (null == properties.getProperty("timeInterval"))
			{
				throw new Exception("timeInterval is null");
			}
			if (null == properties.getProperty("bufferSize"))
			{
				throw new Exception("bufferSize is null");
			}
			if (null == properties.getProperty("inputPath"))
			{
				throw new Exception("inputPath is null");
			}
			if (null == properties.getProperty("statisPath"))
			{
				throw new Exception("statisPath is null");
			}
			if (null == properties.getProperty("histPath"))
			{
				throw new Exception("histPath is null");
			}
			threadNumber = Integer.parseInt(properties
					.getProperty("threadNumber"));
			timeInterval = Integer.parseInt(properties
					.getProperty("timeInterval"));
			bufferSize = Integer.parseInt(properties.getProperty("bufferSize"));
			inputPath = properties.getProperty("inputPath");
			histPath = properties.getProperty("histPath");
			statisPath = properties.getProperty("statisPath");
		} catch (Exception e)
		{
			e.printStackTrace();
			inputStream.close();
			
			System.exit(-1);
		}
		inputStream.close();
	}

}
