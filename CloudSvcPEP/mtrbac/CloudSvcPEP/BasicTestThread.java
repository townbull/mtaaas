package mtrbac.CloudSvcPEP;
import java.io.*;
import java.net.*;
import java.util.Date;

import com.sun.xacml.PDP;
import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;

public class BasicTestThread extends Thread {
	//private Socket socket = null;
	private Socket csSocket;
	private Socket requestSocket = null;
	private OutputStream os,cos;
	private InputStream is,cis;
	private RequestCtx request;
	private ResponseCtx expectedResponse;
	
	private String prefix;
    private boolean errorExpected;
    private boolean experimental;
	
	int errorCount = 0;
    boolean failurePointReached = false;
	
	public BasicTestThread(Socket csSocket, 
			RequestCtx request, 
			ResponseCtx expectedResponse,
			String prefix){
		super("BasicTestThread");
		this.csSocket = csSocket;
		this.request = request;
		this.expectedResponse = expectedResponse;
		this.prefix = prefix;
	}
	
	public void run(){
//		long t0 = 0, t1 = 0;

//    	t0 = new Date().getTime();
    	
//    	for(int i = 0; i < 2; i++)
//    	{
    		
    		
    	try{
    		cos = csSocket.getOutputStream();
			cos.flush();
			cis = csSocket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(cis);
				
			String message = (String)ois.readObject();
			System.out.println("CloudSvc Client> "+ message);
			
			if(message.equals("file")){
				sendFile("file.txt");
			}
			else 
			{
				long start = System.currentTimeMillis();
				
				//1. creating a socket to connect to the server
				requestSocket = new Socket("10.245.122.12", 22222);
				System.out.println("Connected to PDP Server in port 22222");
				//2. get Input and Output streams
				os = requestSocket.getOutputStream();
				os.flush();
				is = requestSocket.getInputStream();
				
				
				
				System.out.println("======== Beginning Evaluation ==========");
				//SEND the request xml to PDPServer  
				request.encode(os);
				requestSocket.shutdownOutput();  //send EOS
				//os.close();
				System.out.println("client>" + request.toString());
				/*		
				FileOutputStream fos = new FileOutputStream(testPrefix + name + "PDPResponse.xml");
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				
				byte[] buffer = new byte[1024];
				
				int bytesRead = is.read(buffer, 0, buffer.length);
				System.out.println("server> Response XML Length = " + bytesRead
						+ " Bytes");
				bos.write(buffer, 0, bytesRead);
				bos.close();
				System.out.println("server>" + "PDPResponse.xml stored.");
				
				byte[] buffer = new byte[1024];
				int bytesRead = is.read(buffer);
				System.out.println("server> Response XML Length = " + bytesRead
						+ " Bytes");
				*/
				
				//is.wait();
				//if(is.available() > 0)
				ByteArrayInputStream bais = new ByteArrayInputStream(readBytes(is));
				//System.out.println("server> Available bytes = " + is.available());
				ResponseCtx response = ResponseCtx.getInstance(bais);
				System.out.println("server>" + response.toString());
				
				/*
				FileOutputStream fos = new FileOutputStream(testPrefix + name + "PDPResponse.xml");
	            response.encode(fos);
	            fos.close();
	            */
				
				
				System.out.println("======== Ending Evaluation ==========");
				
				long end = System.currentTimeMillis();
				System.out.println("PEP time used = "+(end-start));
				
				// if we're supposed to fail, we should have done so by now
				if (errorExpected) {
				    System.out.println("failed");
				    errorCount++;
				} else {
				    failurePointReached = true;
	
				    // see if the actual result matches the expected result
				    boolean equiv = TestUtil.areEquivalent(response,
				                                           expectedResponse);
				    
				    if (equiv) {
				        System.out.println("passed");
				        
				        //send the file to client					
						sendFile("file.txt");			        
				        
				    } else {
				        System.out.println("failed:");
				        response.encode(System.out);
	//			        errorCount++;
				    }
				}

				is.close();
				os.close();
				requestSocket.close();
			}
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			//4: Closing connection
			try{
				cis.close();
				cos.close();
				csSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
		
//    }
//    	t1 = new Date().getTime();
//        System.out.println("Time Used = "+ (t1-t0) + " ms.");
        
	}
	public static byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024*128;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
          byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
	
	private void sendFile(String fileName){
	      try {
	    	  File myFile = new File (prefix + fileName);
		      byte [] mybytearray  = new byte [(int)myFile.length()];
		      FileInputStream fis = new FileInputStream(myFile);
		      BufferedInputStream bis = new BufferedInputStream(fis);
	      
		      bis.read(mybytearray,0,mybytearray.length);
		      
		      System.out.println("File length = "+mybytearray.length);	
		      
		      System.out.println("Cloud Service> Sending file...");
		      cos.write(mybytearray,0,mybytearray.length);
		      cos.flush();
		      bis.close();
		      fis.close();
	      } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
