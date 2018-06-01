import ch.ethz.ssh2.*;
import java.io.*;

public class SCP8 {
	public static void main(String[] args) {
		
		try {
			
			Connection conn = new Connection("10.141.208.49");
			
			conn.connect();

			boolean isAuthenticated = conn.authenticateWithPassword("hadoop", "hadoop");

			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");
			
			SCPClient SCPclient = new SCPClient(conn);
			
			SCPClient curSCP = null;
			
			int nodeId = 0;
			
			File pathDir = new File("E:\\map");
			File[] listByYear = pathDir.listFiles();		
			
			for(int i = 0; i < listByYear.length; i++) {
				
				String yearName = listByYear[i].getName();
				if(yearName.charAt(3) == '7') {
				
					File[] listByChannel = listByYear[i].listFiles();
				
					for(int j = 0; j < listByChannel.length; j++) {
					
						String channelName = listByChannel[j].getName();
					
						if(channelName.charAt(0) == '8' && channelName.charAt(1) != 'A' && channelName.charAt(1) != 'B' && channelName.charAt(1) != 'C' && channelName.charAt(1) != 'D' && channelName.charAt(1) != 'E' && channelName.charAt(1) != 'F') {
						
							System.out.println(channelName + "#");
						
							curSCP = SCPclient;
							nodeId = 1;
				
							File path = new File(listByChannel[j].getAbsolutePath());
					
							BufferedReader fbr = new BufferedReader(new FileReader(path));

							String SCPpath = "/workspace/input/" + channelName;
					
							System.out.println("copy data to: " + "hadoop" + nodeId + " " + SCPpath);
							
							String pathString = fbr.readLine(); 
					
							while(pathString != null) {
						
								System.out.println("h" + nodeId + " " + "copy: " + pathString);
						
								curSCP.put(pathString, SCPpath);
								pathString = fbr.readLine();
							}
					
							fbr.close();
						}
						
					}
				}
			}
			
			conn.close();

			System.out.println("End");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
