package org.terukusu.connectcat.model;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by teru on 2017/04/28.
 */

public class ConnectCatTest {

    private static final String host = "www.google.com";
    private static final int port = 80;

    @Test
    public void testGetInstance() throws  Exception {
        ConnectCat instance = ConnectCat.getInstance();
        Assert.assertNotNull("instance should not be null.", instance);
    }

    @Test
    public void testConnect() throws  Exception {
        ConnectCat instance = ConnectCat.getInstance();
        Assert.assertNotNull("instance should not be null.", instance);

        instance.connect(host, port);
        Assert.assertTrue("should be connected.", instance.isConnected());
    }

    @Test
    public void testConnectSocketAddress() throws  Exception {
        InetSocketAddress addr = new InetSocketAddress(host, port);

        ConnectCat instance = ConnectCat.getInstance();
        Assert.assertNotNull("instance should not be null.", instance);

        instance.connect(addr);
        Assert.assertTrue("should be connected.", instance.isConnected());
    }

    @Test
    public void testDisconnect() throws  Exception {
        ConnectCat instance = ConnectCat.getInstance();
        Assert.assertNotNull("instance should not be null.", instance);

        instance.connect(host, port);
        Assert.assertTrue("should be connected.", instance.isConnected());

        instance.disconnect();
        Assert.assertTrue("should be closed.", instance.isClosed());
    }

    @Test
    public void testSend() throws Exception {
        ConnectCat instance = ConnectCat.getInstance();
        Assert.assertNotNull("instance should not be null.", instance);

        instance.connect(host, port);
        Assert.assertTrue("should be connected.", instance.isConnected());

        instance.disconnect();
        Assert.assertTrue("should be closed.", instance.isClosed());
    }
}
