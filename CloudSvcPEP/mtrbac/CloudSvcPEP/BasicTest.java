
/*
 * @(#)BasicTest.java
 *
 * Copyright 2004 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistribution of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *   2. Redistribution in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use in
 * the design, construction, operation or maintenance of any nuclear facility.
 */

package mtrbac.CloudSvcPEP;

import com.sun.xacml.PDP;

import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import java.io.*;
import java.net.*;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mtrbac.CloudSvcPEP.BasicTest;
import mtrbac.CloudSvcPEP.Test;
import mtrbac.CloudSvcPEP.TestPolicyFinderModule;
import mtrbac.CloudSvcPEP.TestUtil;
import mtrbac.CloudSvcPEP.BasicTestThread;


/**
 * A simple implementation of a single conformance test case.
 *
 * @author Seth Proctor
 */
public class BasicTest implements Test
{
	ServerSocket CloudSvcSocket;
	Socket connection = null;
	OutputStream os;
	InputStream is;
 	//String message;

    // the PDP and module that manage this test's policies
    private PDP pdp;
    private TestPolicyFinderModule module;

    // the policies and references used by this test
    private Set policies;
    private Map policyRefs;
    private Map policySetRefs;

    // meta-data associated with this test
    private String name;
    private boolean errorExpected;
    private boolean experimental;

    /**
     * Constructor that accepts all values associatd with a test.
     *
     * @param pdp the pdp that manages this test's evaluations
     * @param module the module that manages this test's policies
     * @param policies the files containing the policies used by this test,
     *                 or null if we only use the default policy for this test
     * @param policyRefs the policy references used by this test
     * @param policySetRefs the policy set references used by this test
     * @param name the name of this test
     * @param errorExpected true if en error is expected from a normal run
     * @param experimental true if this is an experimental test
     */
    public BasicTest(PDP pdp, TestPolicyFinderModule module, Set policies,
                     Map policyRefs, Map policySetRefs, String name,
                     boolean errorExpected, boolean experimental) {
        this.pdp = pdp;
        this.module = module;
        this.policies = policies;
        this.policyRefs = policyRefs;
        this.policySetRefs = policySetRefs;
        this.name = name;
        this.errorExpected = errorExpected;
        this.experimental = experimental;
    }

    /**
     * Creates an instance of a test from its XML representation.
     *
     * @param root the root of the XML-encoded data for this test
     * @param pdp the <code>PDP</code> used by this test
     * @param module the module used for this test's policies
     */
    public static BasicTest getInstance(Node root, PDP pdp,
                                        TestPolicyFinderModule module) {
        NamedNodeMap map = root.getAttributes();
        
        // the name is required...
        String name = map.getNamedItem("name").getNodeValue();

        // ...but the other two aren't
        boolean errorExpected = isAttrTrue(map, "errorExpected");
        boolean experimental = isAttrTrue(map, "experimental");

        // see if there's any content
        Set policies = null;
        Map policyRefs = null;
        Map policySetRefs = null;
        if (root.hasChildNodes()) {
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String childName = child.getNodeName();
                
                if (childName.equals("policy")) {
                    if (policies == null)
                        policies = new HashSet();
                    policies.add(child.getFirstChild().getNodeValue());
                } else if (childName.equals("policyReference")) {
                    if (policyRefs == null)
                        policyRefs = new HashMap();
                    policyRefs.put(child.getAttributes().getNamedItem("ref").
                                   getNodeValue(),
                                   child.getFirstChild().getNodeValue());
                } else if (childName.equals("policySetReference")) {
                    if (policySetRefs == null)
                        policySetRefs = new HashMap();
                    policySetRefs.put(child.getAttributes().
                                      getNamedItem("ref").getNodeValue(),
                                      child.getFirstChild().getNodeValue());
                }
            }
        }

        return new BasicTest(pdp, module, policies, policyRefs, policySetRefs,
                             name, errorExpected, experimental);
    }

    /**
     * Private helper that reads a attribute to see if it's set, and if so
     * if its value is "true".
     */
    private static boolean isAttrTrue(NamedNodeMap map, String attrName) {
        Node attrNode = map.getNamedItem(attrName);

        if (attrNode == null)
            return false;

        return attrNode.getNodeValue().equals("true");
    }

    public String getName() {
        return name;
    }

    public boolean isErrorExpected() {
        return errorExpected;
    }

    public boolean isExperimental() {
        return experimental;
    }

    public int run(String testPrefix) {
        System.out.print("test " + name + ": ");
        //System.out.println("FilePrefix = " + testPrefix + name);
        int errorCount = 0;
        boolean failurePointReached = false;

        // FIXME: we should get more specific with the exceptions, so we can
        // make sure that an error happened _for the right reason_

//        long t0 = 0, t1 = 0;
       
        try {
        	
        	// load the request for this test
        	RequestCtx request =
				    RequestCtx.getInstance(new FileInputStream(testPrefix + name + "Request.xml"));
        	
        	// load the reponse that we expected to get
		    ResponseCtx expectedResponse =
		        ResponseCtx.getInstance(new FileInputStream(testPrefix + name + "Response.xml"));
        	
        	 try{
	    			//1. creating a server socket
        		 	CloudSvcSocket = new ServerSocket(9999, 10);
	    			//2. Wait for connection
	    			System.out.println("Cloud Service> Waiting for connection ...");
	    			
	    			
	    			while(true){
	    				new BasicTestThread(CloudSvcSocket.accept(), request, expectedResponse, testPrefix).start();
	    				System.out.println("Cloud Service> connection accepted ...");
	    			}
	    			
	    			/*
	    			connection = PDPSvrSocket.accept();
	    			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
	    			//3. get Input and Output streams
	    			os = connection.getOutputStream();
	    			os.flush();
	    			is = connection.getInputStream();
	    			
	    			
	    			//FileOutputStream fos = new FileOutputStream(testDir + "MT-RBACPEPRequest.xml");
	    			//BufferedOutputStream bos = new BufferedOutputStream(fos);
	    			
	    			//byte[] buffer = new byte[1024*1024];
	    			
	    			//sendMessage("Connection successful");
	    			//4. The two parts communicate via the input and output streams
	    			
//	    					int bytesRead = is.read(buffer, 0, buffer.length);
//	    					System.out.println("client>Request File Length = " + bytesRead
//	    							+ " Bytes");
//	    					bos.write(buffer, 0, bytesRead);
//	    					bos.close();
//	    					System.out.println("server>" + "PEPRequest.xml stored.");
	    			
					
	    			ByteArrayInputStream bais = new ByteArrayInputStream(readBytes(is));
					RequestCtx request = RequestCtx.getInstance(bais);
					System.out.println("client>" + request.toString());
					
					//evaluate the request
					System.out.println("======== Beginning Evaluation ==========");
		            
		            
		            // actually do the evaluation
		            ResponseCtx response = pdp.evaluate(request);
	
		            System.out.println("======== Ending Evaluation ==========");
		            
		            
		            response.encode(os);
					//os.flush();
		            connection.shutdownOutput();  //send EOS
					System.out.println("server>" + response.toString());
		            
		           
//		            FileOutputStream fos = new FileOutputStream(testPrefix + name + "PDPResponse.xml");
//		            response.encode(fos);
//		            fos.close();
		           
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
		                    ResponseCtx.
		                    getInstance(new FileInputStream(testPrefix + name +
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
		            }
					
	    		}
	    		catch(IOException ioException){
	    			ioException.printStackTrace();
	    		*/
	    		}
	    		finally{
	    			//4: Closing connection
	    			try{
	    				CloudSvcSocket.close();
	    			}
	    			catch(IOException ioException){
	    				ioException.printStackTrace();
	    			}
	    		}

        } catch (Exception e) {
            // any errors happen as exceptions, and may be successes if we're
            // supposed to fail and we haven't reached the failure point yet
            if (! failurePointReached) {
                if (errorExpected) {
                    System.out.println("passed");
                } else {
                    System.out.println("EXCEPTION: " + e.getMessage());
                    errorCount++;
                }
            } else {
                System.out.println("UNEXPECTED EXCEPTION: " + e.getMessage());
                errorCount++;
            }
        }
        
//        errorCount = (int)(t1-t0);
        
        // return the number of errors that occured
        return errorCount;
    }
    
    public static byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
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
