package cloudSvc;

import java.io.*;
import java.net.*;

public class Client {

	Socket requestSocket;
	ObjectOutputStream os;
 	InputStream is;
	Client(){}
	/**
	 * @param args
	 */
	private long run(String msg){		
		
		try{
			long st = System.currentTimeMillis();
			
			//1. creating a socket to connect to the server
			requestSocket = new Socket("10.245.125.7", 9999);
//			System.out.println("Connected to localhost in port 9999");
			//2. get Input and Output streams
			os = new ObjectOutputStream(requestSocket.getOutputStream());
			os.flush();
			
			is = requestSocket.getInputStream();
			
			//3: Communicating with the server
			sendMessage(msg);
			receiveFile();
			
			long et = System.currentTimeMillis();
//		    System.out.println("CloudSvcClient> Total time used = "+(end-start));
//			System.out.println((end-start));
			return (et-st);
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				is.close();
				os.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
		//FileOutputStream fos = new FileOutputStream(testDir + "MT-RBACPEPRequest.xml");
		//BufferedOutputStream bos = new BufferedOutputStream(fos);
		return 0;
		
		//byte[] buffer = new byte[1024*1024];
		
		//sendMessage("Connection successful");
		//4. The two parts communicate via the input and output streams
		
//				int bytesRead = is.read(buffer, 0, buffer.length);
//				System.out.println("client>Request File Length = " + bytesRead
//						+ " Bytes");
//				bos.write(buffer, 0, bytesRead);
//				bos.close();
//				System.out.println("server>" + "PEPRequest.xml stored.");
	}
	void sendMessage(String msg)
	{
		try{
			os.writeObject(msg);
			os.flush();
//			System.out.println("client> " + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	void receiveFile(){
		
		
	    int bytesRead = 0, byteCount = 0;
	    
	    // receive file
	    byte [] mybytearray  = new byte [1024*1024*2];
	    
//	    System.out.println("client> Receiving file ...");
	    
	    try {
	    FileOutputStream fos = new FileOutputStream("file.txt");
	    BufferedInputStream bif = new BufferedInputStream(is, 1024);
	    
	    while( -1 != (bytesRead = bif.read(mybytearray, 0, 1024))){
	    	byteCount += 1024;
	    	fos.write(mybytearray,0,bytesRead);
	    }
	  
	    
//	    System.out.println("Bytes written = "+byteCount);

	    bif.close();
	    fos.close();
	    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public static void main(String[] args) {

		Client c = new Client();
		long t_pep = 0, t_file=0;
		int n = 100;
		double r_pep = 0.0, r_file = 0.0;
		 
		for(int i = 0; i < n; i++){
			t_pep += c.run("PEP");
			t_pep += c.run("file");
		}
		
		for(int i = 0; i < n; i++){
			t_file += c.run("file");
			t_file += c.run("file");
		}
		
		r_pep = t_pep / n;
		System.out.println("Average r_pep = " + r_pep + " ms");
		
		r_file = t_file / n;
		System.out.println("Average r_file = " + r_file + " ms");
		System.out.println("Average ratio = " + (r_pep-r_file)/r_file);		
	}

}
