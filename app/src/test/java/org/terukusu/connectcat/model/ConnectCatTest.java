package org.terukusu.connectcat.model;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
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

    @Test
    public void testReceive() throws Exception {
        ConnectCat instance = ConnectCat.getInstance();
        Assert.assertNotNull("instance should not be null.", instance);

        instance.connect(host, port);
        Assert.assertTrue("should be connected.", instance.isConnected());

        instance.send("GET / HTTP/1.0\n\n".getBytes(Charset.forName("UTF-8")));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        instance.receive(baos);

        Assert.assertTrue("data size should not be  0", baos.size() > 0);

        instance.disconnect();
        Assert.assertTrue("should be closed.", instance.isClosed());
    }


}
