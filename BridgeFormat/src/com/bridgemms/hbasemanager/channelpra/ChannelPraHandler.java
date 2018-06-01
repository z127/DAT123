package com.bridgemms.hbasemanager.channelpra;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import com.bridgemms.hbasemanager.entity.ChannelDataItem;

public class ChannelPraHandler {
	
	private static Map<String,ChannelDataItem> chadils = new TreeMap<String,ChannelDataItem>();
	private static Connection conn = null;
	private static Map<String,List<ChannelDataItem>> treeStracture = new TreeMap<String,List<ChannelDataItem>>();
	static{
		conn = getConn();
		chadils = getAllPra();
	}
	
	public static Connection getConn()
	{
		
		Connection conn = null;
		//Map<String,String> dbpra = GetDBSettings.dbset;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Mysql start!");
			//conn = DriverManager.getConnection("jdbc:mysql://218.193.132.35:3306/ChannelPra", "hadoop", "hadoop");
			//conn = DriverManager.getConnection("jdbc:mysql://10.131.239.176:3306/ChannelPra", "hadoop", "hadoop");
			//conn = DriverManager.getConnection("jdbc:mysql://192.168.99.24:3306/ChannelPra", "root", "rootadmin");
			conn = DriverManager.getConnection("jdbc:mysql://10.141.208.45:3306/bridge", "root", "hadoop");
			System.out.println("Mysql end!");			
		} catch (Exception e) {
			e.printStackTrace();
		}                        
		return conn;
	}
	
	public final static ChannelDataItem getRoot()
	{
		ChannelDataItem cdi = new ChannelDataItem();
		cdi.setCode("root");
		cdi.setName("root");
		return cdi;
	}
	
	public final static List<ChannelDataItem> getChilds(String channelno)
	{
		if(conn == null) {
			getConn();
			getAllPra();
		}
		return treeStracture.get(channelno);
	}
	
	/**
	 * ���ͨ����Ż�ȡ��ͨ�����в���
	 * @param channelno
	 * @return ChannelDataItem
	 */
	public final static ChannelDataItem getItem(String channelno)
	{
		return chadils.get(channelno);
	}
	/**
	 * ��MySQL�ж�������ͨ����ȫ��������Map<String,ChannelDataItem>��ʽ�������ڴ���
	 * @return Map<String,DataItem>
	 */
	private static Map<String, ChannelDataItem> getAllPra()
	{
		Statement st = null;
		ResultSet rs = null;
		final String sql = "select * from channel_pra where code not like 'CTDQ-%' and code not like 'DLLSD-%' order by name";
		Map<String, ChannelDataItem> ls = new TreeMap<String,ChannelDataItem>();
		List<ChannelDataItem> temp = null;
		ChannelDataItem cdi = null;		
		try {			
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next())
			{
				cdi = new ChannelDataItem();
//				System.out.println(rs.getString("code"));
				cdi.setId(rs.getInt("id"));
				cdi.setCode(rs.getString("code"));
				cdi.setName(rs.getString("name"));
				cdi.setType(rs.getString("type"));
				cdi.setTimeperrow(rs.getInt("time_per_row"));
				cdi.setFrequence(rs.getDouble("frequence"));
				cdi.setLocation(rs.getString("location"));
				cdi.setParent(rs.getString("parent"));
				cdi.setNodetype(rs.getShort("nodetype"));
				if(cdi.getNodetype() == 1) ls.put(cdi.getCode(), cdi);
				if((temp = treeStracture.get(cdi.getParent()))!=null) temp.add(cdi);
				else {
					temp = new ArrayList<ChannelDataItem>();
					temp.add(cdi);
					treeStracture.put(cdi.getParent(),temp);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return ls;
	}
	
	public static void scan(String s)
	{
		List<ChannelDataItem> ls = ChannelPraHandler.getChilds(s);
		if(ls!=null)
		for(ChannelDataItem c:ls)
		{
			if(c.getName().contains("-"))
			System.out.println(c.getName());
			scan(c.getCode());
		}

	}
	
//	public static void main(String[] args) {
//		ChannelDataItem cdi = ChannelPraHandler.getItem("SGP1101-DY");
//		System.out.println(cdi.getCode());
//		scan("root");
//		for(ChannelDataItem di:getChilds("root"))
//		{
//			System.out.println(di.getCode());
//		};
//	}
}
