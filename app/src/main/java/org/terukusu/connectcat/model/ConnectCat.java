package org.terukusu.connectcat.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by teru on 2017/04/28.
 */

public class ConnectCat {

    /** 接続タイムアウトです。単位はミリ秒です。 */
    private static final int CONNECT_TIMEOUT_MILLIS = 50000;

    /** リードタイムアウトのです。単位はミリ秒です。 */
    private static final int READ_TIMEOUT_MILLIS = 50000;

    /** 受信バッファーサイズです。 */
    private static final int RECEIVE_BUFFER_SIZE = 8192;

    /** シングルトンインスタンスです。 */
    private static ConnectCat instance;

    /** サーバーへの接続です。 */
    private Socket socket;

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
     * コンストラクタです。
     */
    protected ConnectCat() {
    }

    /**
     * 指定されたホスト、ポートでソケットを作成して接続します。
     *
     * @param host ホスト
     * @param port ポート
     * @throws IOException I/Oで問題が発生した場合にスローします
     */
    public synchronized  void connect(String host, int port) throws IOException {
        InetSocketAddress addr = new InetSocketAddress(host, port);
        socket = new Socket();
        socket.setSoTimeout(READ_TIMEOUT_MILLIS);
        socket.connect(addr, CONNECT_TIMEOUT_MILLIS);
    }

    public synchronized boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public synchronized boolean isClosed() {
        return socket != null && socket.isConnected();
    }

    public synchronized  void receive(OutputStream os) throws IOException {
        InputStream is = socket.getInputStream();
        int len;
        byte[] buff = new byte[8192];
        while ((len = is.read(buff)) >= 0) {
            os.write(buff);
        }
        os.flush();
    }

    public synchronized void send(byte[] data) throws IOException {
        OutputStream os = socket.getOutputStream();
        os.write(data);
        os.flush();
    }

    public synchronized void disconnect() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }
}
