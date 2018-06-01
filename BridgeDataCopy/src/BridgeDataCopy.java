import java.io.*;
import java.util.*;

public class BridgeDataCopy {
	
	static FileInputStream fis;
	static FileOutputStream fos;

	public static void main(String[] args) {
		
		final HashSet<String> channelList = new HashSet<String>();
//		File file = new File("list09out.txt");
//		String s = new String();
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(file));
//			try {
//				while((s = br.readLine()) != null) {
//					channelList.add(s);
//				}				
//				br.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		
		long start = System.currentTimeMillis();
		
		File src = new File("H:/bakdata");
		//File src = new File("/bridge/bakdata");
		//File src = new File("/bridge/donghai_data");
		//File src = new File("/bridge/bak");
		File dst = new File("D:/workspace/input");
		//2007
		for(File year : src.listFiles(new FileFilter() {
			
            @Override
			public boolean accept(File name) {
                String s = name.getName();
                if(s.startsWith("2007") && name.isDirectory()){
                    return true;
                }         
                return false;
            	//return true;
            }
			
		})) {
			//System.out.print(year.getName());
			//AB ..
			for(File type : year.listFiles(new FileFilter() {
			
	            @Override
				public boolean accept(File name) {
//	                String s = name.getName();
//	                if(s.charAt(0) >= 'A' && s.charAt(0) <= 'Z'){
//	                    return true;
//	                }         
//	                return false;
	            	String s = name.getName();
	            	if(s.startsWith("AD") && name.isDirectory()){
	                    return true;
	                }   
	            	return false;
	            }
			
			})) {
				System.out.println(type.getName());
				//5AB001 ..
				for(File number : type.listFiles(new FileFilter() {
					
		            @Override
					public boolean accept(File name) {
//		                String s = name.getName();
//		                if((s.charAt(0) > '5' && s.charAt(0) <= '9') || (s.charAt(0) >= '0' && s.charAt(0) < '5')) {
//		                    return true;
//		                }         
//		                return false;
		            	String s = name.getName();
		            	if(s.equals("5AD001")&& name.isDirectory()){
		            		return true;
		            	}
		            	return false;
		            }
					
				})) {
					//System.out.print(number.getName());
					//5AB001-DX ..
					for(File channel : number.listFiles(new FileFilter() {
						
			            @Override
						public boolean accept(File name) {
			                String s = name.getName();
//			                if((s.charAt(0) > '5' && s.charAt(0) <= '9') || (s.charAt(0) >= '0' && s.charAt(0) < '5')){
//			                    return true;
//			                }         
//			                return false;
//			                if(channelList.contains(s)) {
//			                	return true;
//			                }
			                return true;
			            }
						
					})) {
						//System.out.print(channel);
						File dir = new File(dst.getAbsolutePath() + "/" + channel.getName());
						if(!dir.exists()) {
							dir.mkdirs();
						}
						//200701 ..
						System.out.println("channel"+channel.getName());
						for(File month : channel.listFiles(new FileFilter() {
							
				            @Override
							public boolean accept(File name) {
				                String s = name.getName();
				                if(!s.startsWith(".")){
				                    return true;
				                }         
				                return false;
				            }
							
						})) {
							//System.out.print(month);
							//data
							for(File srcData : month.listFiles(new FileFilter() {
								
					            @Override
								public boolean accept(File name) {
					                String s = name.getName();
					                if(!s.startsWith(".")){
					                    return true;
					                }         
					                return false;
					            }
								
							})) {
								
								File dstData = new File(dst.getAbsolutePath() + "/" + channel.getName() + "/" + srcData.getName());
								System.out.println("dstData"+dstData.getAbsolutePath());
								try {
									copyFile(srcData, dstData);
								} catch (IOException e) {
									e.printStackTrace();
								}
								
								System.out.println(dstData.getName());
							}
						}
					}
				}
			}
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("total copy time: " + (end - start) / 1000 + "s");
		
	}
	
	public static void copyFile(File src, File dst) throws IOException {
		
		try {
			fis = new FileInputStream(src);
			fos = new FileOutputStream(dst);
			byte[] temp = new byte[1024];
			int c;
			while((c = fis.read(temp)) > 0) {
				fos.write(temp, 0, c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fis.close();
			fos.close();
		}
		
	}
	
}
