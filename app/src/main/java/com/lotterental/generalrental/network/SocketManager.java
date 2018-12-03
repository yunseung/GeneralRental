package com.lotterental.generalrental.network;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

public class SocketManager {
    private String IP;
    private int Port;
    private SocketChannel m_hSocketChannel;
    private Selector m_hSelector;
    private readDataThread m_readData;
    private sendDataThread m_sendData;
    private Handler m_handler;

    public SocketManager(String ip, int port, Handler h) {
        this.IP = ip;
        this.Port = port;
        this.m_handler = h;
        // thread objects의 작업 할당 및 초기화
        m_readData = new readDataThread();
        m_readData.start();
    }

    // m_createSocket thread 안에서 실행
    private void setSocket(String ip, int port) throws IOException {
        // selector 생성
        m_hSelector = Selector.open();
        // 채널 생성
        m_hSocketChannel = SocketChannel.open(new InetSocketAddress(ip, port));
        // 논블로킹 모드 설정
        m_hSocketChannel.configureBlocking(false);
        // 소켓 채널을 selector에 등록
        m_hSocketChannel.register(m_hSelector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    public void sendData(String data) {
        m_sendData = new sendDataThread(m_hSocketChannel, data);
        m_sendData.start();
    }

    private void read(SelectionKey key) throws Exception {
        // SelectionKey로부터 소켓채널을 얻어온다.
        SocketChannel sc = (SocketChannel) key.channel();
        // ByteBuffer를 생성한다.
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int read = 0;
        // 요청한 클라이언트의 소켓채널로부터 데이터를 읽어들인다.
        read = sc.read(buffer);
        buffer.flip();
        String data = new String();
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        data = decoder.decode(buffer).toString();
        // 메시지 얻어오기
        Message msg = m_handler.obtainMessage();
        // 메시지 ID 설정
        msg.what = 1;
        // 메시지 정보 설정3 (Object 형식)
        msg.obj = data;
        m_handler.sendMessage(msg);
        // 버퍼 메모리를 해제한다.
        clearBuffer(buffer);
    }

    private void clearBuffer(ByteBuffer buffer) {
        if (buffer != null) {
            buffer.clear();
            buffer = null;
        }
    }

    /*********** inner thread classes **************/
    public class sendDataThread extends Thread {
        private SocketChannel sdt_hSocketChannel;
        private String data;

        public sendDataThread(SocketChannel sc, String d) {
            sdt_hSocketChannel = sc;
            data = d;
        }

        public void run() {
            try {
                // 데이터 전송.
                sdt_hSocketChannel.write(ByteBuffer.wrap(data.getBytes()));
            } catch (Exception e1) {
            }
        }
    }

    public class readDataThread extends Thread {
        public readDataThread() {
        }

        public void run() {
            try {
                setSocket(IP, Port);
            } catch (IOException e) {
                e.printStackTrace();
            } // 소켓 생성 완료를 메인UI스레드에 알림.
            m_handler.obtainMessage();
            m_handler.sendEmptyMessage(0);
            // 데이터 읽기 시작.
            try {
                while (true) {
                    // 셀렉터의 select() 메소드로 준비된 이벤트가 있는지 확인한다
                    m_hSelector.select();
                    // 셀렉터의 SelectoedSet에 저장된 준비된 이벤트들(SelectionKey)을 하나씩 처리한다.
                    Iterator it = m_hSelector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = (SelectionKey) it.next();
                        if (key.isReadable()) {
                            // 이미 연결된 클라이언트가 메시지를 보낸경우...
                            try {
                                read(key);
                            } catch (Exception e) {
                            }
                        }
                        // 이미 처리한 이벤트므로 반드시 삭제해준다.
                        it.remove();
                    }
                }
            } catch (Exception e) {
            }
        }
    }
}
