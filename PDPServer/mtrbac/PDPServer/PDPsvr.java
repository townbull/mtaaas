
/*
 * @(#)TestDriver.java
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

package mtrbac.PDPServer;

import com.sun.xacml.ConfigurationStore;
import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;

import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.finder.PolicyFinder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import mtrbac.PDPServer.BasicGroupTest;
import mtrbac.PDPServer.BasicTest;
import mtrbac.PDPServer.Test;
import mtrbac.PDPServer.PDPsvr;
import mtrbac.PDPServer.TestPolicyFinderModule;


/**
 * Simple driver class that sets up and runs the tests.
 *
 * @author Seth Proctor
 */
public class PDPsvr
{
    // the pdp we use to do all evaluations
    private PDP pdp;

    // the module we use to manage all policy management
    private TestPolicyFinderModule policyModule;
    
    // the tests themselves
    private ArrayList tests;
    private BasicTest test;

    /**
     * Constructor that sets up the tests for running.
     *
     * @param testFile the XML file defining which tests to run
     *
     * @throws Exception if any error occurred during setup
     */
    public PDPsvr(String testFile) throws Exception {
        policyModule = new TestPolicyFinderModule();
        tests = new ArrayList();

        //TODO: Customize policyModule used for the tests here
        
        
        configurePDP();
        loadPolicy(testFile);
    }

    /**
     * Private helper that configures the pdp and the factories based on the
     * settings in the run-time configuration file.
     */
    private void configurePDP() throws Exception {
        // load the configuration
        ConfigurationStore cs = new ConfigurationStore();

        // use the default factories from the configuration
        cs.useDefaultFactories();

        // get the PDP configuration's policy finder modules...
        PDPConfig config = cs.getDefaultPDPConfig();
        PolicyFinder finder = config.getPolicyFinder();
        Set policyModules = finder.getModules();
        
        
        //System.out.println("policyModules = "+policyModules.toString());

        
        // ...and add the module used by the tests
        policyModules.add(policyModule);
        finder.setModules(policyModules);

        // finally, setup the PDP
        pdp = new PDP(config);
    }

    /**
     * Private helper that loads the tree of test cases
     */
    private void loadPolicy(String testFile) throws Exception {
        // load the test file
        Node root = getRootNode(testFile);
        
        //System.out.println("root name = " + root.getNodeName());
        
        test = BasicTest.getInstance(root, pdp, policyModule);   
        //System.out.println("test = " + test.toString());
    }

    /**
     * Private helper that parses the file and sets up the DOM tree.
     */
    private Node getRootNode(String configFile) throws Exception {
        DocumentBuilderFactory dbFactory =
            DocumentBuilderFactory.newInstance();

        dbFactory.setIgnoringComments(true);
        dbFactory.setNamespaceAware(false);
        dbFactory.setValidating(false);

        DocumentBuilder db = dbFactory.newDocumentBuilder();
        Document doc = db.parse(new FileInputStream(configFile));
        Element root = doc.getDocumentElement();

        if (! root.getTagName().equals("test"))
            throw new Exception("unknown document type: " + root.getTagName());

        return root;
    }

    /**
     * Runs the tests, in order, using the given location of the test data.
     *
     * @param prefix the root directory of all the conformance test cases
     */
    public void runTest(String prefix) {
        
        int errorCount = 0;

//        System.out.println("STARTING TEST at " + new Date());
//
//        System.out.println("test = " + test.toString());
//        errorCount += test.run(prefix);
          test.run(prefix);

//
//        System.out.println("FINISHED TEST at " + new Date());
//        System.out.println("Total Failed: " + errorCount);
    }
    
    public static void main(String args[]) throws Exception
	{
		PDPsvr server = new PDPsvr(args[0]);
		String testDir = "./";

        if (args.length != 1)
            testDir = args[1] + "/";

		while(true){
			server.runTest(testDir);
		}
	}
}
