import ch.ethz.ssh2.*;
import java.io.*;
import java.util.*;

public class MKDir8 {
	
	public static void main(String[] args) {
		
		try {
			Connection conn = new Connection("10.141.208.49");
			
			conn.connect();
			
			boolean isAuthenticated = conn.authenticateWithPassword("hadoop", "hadoop");

			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");
			
			SFTPv3Client client = new SFTPv3Client(conn);

			HashSet<String> channels = new HashSet<String>(); 
			
			System.out.println("Scan PathFiles...");
			
			File pathDir = new File("E:\\map");
			File[] listByYear = pathDir.listFiles();
			
			for(int i = 0; i < listByYear.length; i++) {
				String yearName = listByYear[i].getName();
				
				if(yearName.charAt(3) == '7') {
				
					File[] listByChannel = listByYear[i].listFiles();
					for(int j = 0; j < listByChannel.length; j++) {
						
						String channelName = listByChannel[j].getName();
						
						//if(channelName.charAt(0) == '8') {
							
							System.out.println(yearName + ": " + channelName);
							
							channels.add(channelName);	
						//}	
					}
				}
			}
			
			System.out.println("mkdir...");
			
			for(String s: channels) {
				
				System.out.println(s + "#");
				
				String MKDirpath = "/workspace/input/" + s;
				
				System.out.println(MKDirpath);
				
				client.mkdir(MKDirpath, 0777);
			}
			
			conn.close();

			System.out.println("End");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
