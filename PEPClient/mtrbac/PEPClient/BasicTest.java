
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

package mtrbac.PEPClient;

import com.sun.xacml.PDP;

import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;


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

import mtrbac.PEPClient.BasicTest;
import mtrbac.PEPClient.Test;
import mtrbac.PEPClient.TestPolicyFinderModule;
import mtrbac.PEPClient.TestUtil;


/**
 * A simple implementation of a single conformance test case.
 *
 * @author Seth Proctor
 */
public class BasicTest implements Test
{
	Socket requestSocket;
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
        	
//        	t0 = new Date().getTime();
        	
//        	for(int i = 0; i < 2; i++)
//        	{
        		
        		
        	try{
    			//1. creating a socket to connect to the server
    			requestSocket = new Socket("10.245.123.61", 55555);
    			System.out.println("Connected to PDP Server in port 55555");
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
//        }
//        	t1 = new Date().getTime();
//            System.out.println("Time Used = "+ (t1-t0) + " ms.");
            
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
