package com.github.jerryxia.healthcheck;

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }


    public void testApp() {
        InstanceNode node = new InstanceNode();
        node.setServerName("aaa.bbb.com");
        node.setNodes(new ArrayList<ServerNode>());

        ServerNode serverNode = new ServerNode();
        serverNode.setIp("127.0.0.1");
        serverNode.setPort(8082);
        serverNode.setPath("/");
        serverNode.setQueryParamName("t");
        node.getNodes().add(serverNode);

        CheckTask task = new CheckTask(node);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
 
//        task.callShell("ipconfig");
//        try {
//            FileUtils.writeStringToFile(new File("D:/1.txt"), "adsf\n");
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
