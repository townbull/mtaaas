package mtrbac.PDPServer;
import java.io.*;
import java.net.*;
import java.util.Date;

import com.sun.xacml.PDP;
import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;

public class BasicTestThread extends Thread {
	//private Socket socket = null;
	private ServerSocket PDPSvrSocket;
	private Socket connection = null;
	private OutputStream os;
	private InputStream is;
	private PDP pdp;
	
	private String name;
	private String testPrefix;
    private boolean errorExpected;
    private boolean experimental;
	
	int errorCount = 0;
    boolean failurePointReached = false;
	
	public BasicTestThread(Socket socket, PDP pdp){
		super("BasicTestThread");
		this.connection = socket;
		this.pdp = pdp;
		
		/*
		this.name = name;
		this.testPrefix = testPrefix;
        this.errorExpected = errorExpected;
        this.experimental = experimental;
        */
	}
	
	public void run(){
		long t0 = 0, t1 = 0;

		try{
//			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
			//3. get Input and Output streams
			os = connection.getOutputStream();
			os.flush();
			is = connection.getInputStream();
			
			
			//FileOutputStream fos = new FileOutputStream(testDir + "MT-RBACPEPRequest.xml");
			//BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			//byte[] buffer = new byte[1024*1024];
			
			//sendMessage("Connection successful");
			//4. The two parts communicate via the input and output streams
			/*
					int bytesRead = is.read(buffer, 0, buffer.length);
					System.out.println("client>Request File Length = " + bytesRead
							+ " Bytes");
					bos.write(buffer, 0, bytesRead);
					bos.close();
					System.out.println("server>" + "PEPRequest.xml stored.");
			*/
			
			ByteArrayInputStream bais = new ByteArrayInputStream(readBytes(is));
			RequestCtx request = RequestCtx.getInstance(bais);
			//System.out.println("client>" + request.toString());
			
			
			
			//evaluate the request
			//System.out.println("======== Beginning Evaluation ==========");
            t0 = new Date().getTime();
            
            // actually do the evaluation
            ResponseCtx response = pdp.evaluate(request);
            
            t1 = new Date().getTime();
           // System.out.println("======== Ending Evaluation ==========");            
           // System.out.println("Time Used = "+ (t1-t0) + " ms.");
            System.out.println(t1-t0);
            
            response.encode(os);
			//os.flush();
            connection.shutdownOutput();  //send EOS
			//System.out.println("server>" + response.toString());
           
            
           /* 
            FileOutputStream fos = new FileOutputStream(testPrefix + name + "PDPResponse.xml");
            response.encode(fos);
            fos.close();
            
            //response = ResponseCtx.getInstance(new FileInputStream(testPrefix 
            //+ name + "PDPResponse.xml"));
            
            // if we're supposed to fail, we should have done so by now
            if (errorExpected) {
                System.out.println("failed");
                errorCount++;
            } else {
                failurePointReached = true;

                // load the reponse that we expectd to get
                ResponseCtx expectedResponse =
                    ResponseCtx.getInstance(new FileInputStream(testPrefix + name +
                                                    "Response.xml"));

                // see if the actual result matches the expected result
                boolean equiv = TestUtil.areEquivalent(response,
                                                       expectedResponse);
                
                if (equiv) {
                    System.out.println("passed");
                } else {
                    System.out.println("failed:");
                    response.encode(System.out);
                    errorCount++;
                }
            }*/
		}
		catch(IOException e){
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				is.close();
				os.close();
				connection.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
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
}
