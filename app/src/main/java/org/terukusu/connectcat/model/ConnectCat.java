package org.terukusu.connectcat.model;

import android.util.Log;
import android.util.Xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

/**
 * Created by teru on 2017/04/28.
 */

public class ConnectCat {

    /** 接続タイムアウトです。単位はミリ秒です。 */
    private static final int CONNECT_TIMEOUT_MILLIS = 5000;
    /** リードタイムアウトのです。単位はミリ秒です。 */
    private static final int READ_TIMEOUT_MILLIS = 2000;
    /** 受信バッファーサイズです。 */
    private static final int RECEIVE_BUFFER_SIZE = 8192;
    /** シングルトンインスタンスです。 */
    private static ConnectCat instance;
    /** デフォルトキャラクターセット */
    private Charset defaultCharset = Charset.defaultCharset();
    /** サーバーへの接続です。 */
    private volatile  Socket socket;

    private volatile  boolean isClosed = false;

    /**
     * コンストラクタです。
     */
    protected ConnectCat() {
    }

    /**
     * シングルトンインスタンスを取得します。
     *
     * @return シングルトンインスタンスです。
     */
    public static ConnectCat getInstance() {
        synchronized (ConnectCat.class) {
            if (instance == null) {
                instance = new ConnectCat();
            }

            return instance;
        }
    }

    /**
     * 指定されたホスト、ポートでソケットを作成して接続します。
     *
     * @param host ホスト
     * @param port ポート
     * @throws IOException I/Oで問題が発生した場合にスローします
     */
    public void connect(String host, int port) throws IOException {
        InetSocketAddress addr = new InetSocketAddress(host, port);
        connect(addr);
    }

    /**
     * 指定されたアドレスに対してソケットを作成して接続します。
     *
     * @param addr ソケットアドレス
     * @throws IOException I/Oで問題が発生した場合にスローします
     */
    public void connect(SocketAddress addr) throws IOException {
        socket = new Socket();
        socket.setSoTimeout(READ_TIMEOUT_MILLIS);
        socket.connect(addr, CONNECT_TIMEOUT_MILLIS);

        synchronized (this) {
            this.isClosed = false;
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public boolean isClosed() {

        synchronized (this) {
            if (this.isClosed) {
                return true;
            }
        }

        return (socket == null || socket.isClosed());
    }

    /**
     * データを受信します。
     * データを読み出すまでブロックします。
     *
     * @throws IOException
     */
    public int read(byte[] buff) throws SocketTimeoutException, IOException {
        InputStream is = socket.getInputStream();

        int len = -1;
        try {
            len = is.read(buff);
        } catch (SocketException e) {
            synchronized (this) {
                if (! this.isClosed) {
                    throw e;
                }
            }
        }

        if (len == -1) {
            synchronized (this) {
                this.isClosed = true;
            }
        }

        return len;
    }

    public void disconnect() throws IOException {
        synchronized (this) {
            this.isClosed = true;
        }

        if (socket != null) {
            socket.close();
        }
    }

    public void send(byte[] data) throws IOException {
        send(new ByteArrayInputStream(data));
    }

    public void send(String str) throws IOException {
        send(str, Charset.defaultCharset());
    }

    public void send(String str, Charset charset) throws IOException {
        send(new ByteArrayInputStream(str.getBytes(charset)));
    }

    public void send(InputStream is) throws IOException {
        byte[] buff = new byte[8192];

        OutputStream os = socket.getOutputStream();

        for(int len = -1; (len = is.read(buff)) >= 0; ) {
            os.write(buff, 0, len);
        }
        os.flush();
    }

    public void setDefaultCharset(Charset defaultCharset) {
        this.defaultCharset = defaultCharset;
    }
}
