package com.lotterental.generalrental.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lotterental.common.Common;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityTestBinding;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestActivity extends AppCompatActivity {
    private ActivityTestBinding mBinding = null;

    private Socket mSocket = null;

    private String mCmdCdoe = null;
    private String mAssetNo = null;
    private String mSerialNo = null;
    private String mModelNm = null;

    private String ip = null;
    private int port = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test);


        mBinding.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                ip = mBinding.eip.getText().toString();
                try {
                    port = Integer.parseInt(mBinding.eport.getText().toString());
                } catch (NumberFormatException e) {
                    port = 5000;
                }

                threadTest(10, ip, port);

            }
        });


        mBinding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {

            }
        });

        mBinding.scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this, ScanActivity.class));
            }
        });

        mBinding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    public static void threadTest(int threadCount, String host, int port) {

        for (int inx = 0; inx < threadCount; inx++) {

            ConnectThread thread = new ConnectThread(host, port);

            System.out.println("===================================");

            System.out.println("Thread " + inx + " '" + thread.getName() + "' start!");

            thread.start();

            System.out.println("===================================");

        }
    }

}

class ConnectThread extends Thread {


    private String host;

    private int port;


    public ConnectThread(String host, int port) {

        this.host = host;

        this.port = port;

    }


    @Override

    public void run() {

        Socket socket = null;

        DataInputStream dis = null;


        try {

            socket = new Socket(host, port);

            socket.setSoTimeout(1000 * 15); // 타임아웃 15초로 설정


            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());


            try (ByteArrayOutputStream bos1 = new ByteArrayOutputStream()) {
                try (ByteArrayOutputStream bos2 = new ByteArrayOutputStream()) {
                    bos2.write("13 ".getBytes("UTF-8"));

                    bos2.write("60090477".getBytes("UTF-8"));
                    bos2.write(0x11);
                    bos2.write("MY52090792".getBytes("UTF-8"));
                    bos2.write(0x11);
                    bos2.write("N9020A".getBytes("UTF-8"));
                    bos2.write(0x11);
                    bos2.write("\r\n".getBytes("UTF-8"));

                    bos2.write(0x13);
                    bos2.write(0x04);

                    byte[] data = bos2.toByteArray();

                    bos1.write(intToByteArray(data.length + 25));
                    bos1.write("ninebytes".getBytes("UTF-8"));
                    bos1.write("twelvebytess".getBytes("UTF-8"));
                    bos1.write(data);
                } catch (IOException e) {
                    Common.printException(e);
                }

                dos.write(bos1.toByteArray());
                dos.flush();


            } catch (IOException e) {
                Common.printException(e);
            }


            // 소켓의 입력스트림을 얻는다.

            dis = new DataInputStream(socket.getInputStream()); // 기본형 단위로 처리하는 보조스트림


            // 소켓으로 부터 받은 데이터를 출력한다.

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int bufferSize = 1024;

            byte[] buffer = new byte[bufferSize];

            int read;


            while (true) {

                read = dis.read(buffer, 0, bufferSize);

                bos.write(buffer, 0, read);

                if (read < bufferSize)

                    break;

            }

            System.out.println("=========================================");

            System.out.println("서버로부터 받은 메시지" + this.getName() + ": " + new String(bos.toByteArray(), "UTF-8"));

            System.out.println("=========================================");

        } catch (UnknownHostException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (dis != null && socket != null) {

                    System.out.println("연결을 종료합니다.");

                    // 스트림과 소켓을 닫는다.

                    dis.close();

                    socket.close();

                }

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

    }


    private byte[] intToByteArray ( final int i ) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(i);
        dos.flush();
        return bos.toByteArray();
    }

}
