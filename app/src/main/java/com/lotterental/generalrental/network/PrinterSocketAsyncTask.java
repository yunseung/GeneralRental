package com.lotterental.generalrental.network;

import android.os.AsyncTask;
import android.webkit.WebView;

import com.lotterental.LLog;
import com.lotterental.common.Common;
import com.lotterental.generalrental.webview.JavascriptSender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PrinterSocketAsyncTask extends AsyncTask<JSONObject, Void, Boolean> {
    private WebView mWebView = null;
    private String mCallbackName = null;

    public PrinterSocketAsyncTask(WebView webView, String callback) {
        mWebView = webView;
        mCallbackName = callback;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(JSONObject... jsonObjects) {
        String host;
        int port;
        String type;
        String pdaModelNr;
        String deviceId;
        String cmdCode;
        JSONArray list;

        String result;

        try {
            host = jsonObjects[0].getString("PRINT_IP");
            port = jsonObjects[0].getInt("PRINT_PORT");
            cmdCode = jsonObjects[0].getString("CMD_CODE");
            deviceId = jsonObjects[0].getString("DEVICE_ID");
            list = jsonObjects[0].getJSONArray("list");
            if (deviceId.length() < 12) {
                for (int i = deviceId.length(); i < 12; i++) {
                    deviceId = new StringBuffer(deviceId).append("0").toString();
                }
            }
        } catch (JSONException e) {
            Common.printException(e);
            return false;
        }

        Socket socket = null;

        DataInputStream dis = null;


        try {

            socket = new Socket(host, port);

            socket.setSoTimeout(1000 * 15); // 타임아웃 15초로 설정


            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());


            try (ByteArrayOutputStream bos1 = new ByteArrayOutputStream()) {
                try (ByteArrayOutputStream bos2 = new ByteArrayOutputStream()) {
                    bos2.write(cmdCode.getBytes("UTF-8"));

                    // TODO IP 설정 callback, sharedPreferences 저장..????
                    for (int i = 0; i < list.length(); i++) {
                        bos2.write(list.getJSONObject(i).getString("ASSETS_NO").getBytes("UTF-8"));
                        bos2.write(0x11);
                        bos2.write(list.getJSONObject(i).getString("SERIAL_NO").getBytes("UTF-8"));
                        bos2.write(0x11);
                        bos2.write(list.getJSONObject(i).getString("MODEL_NM").getBytes("UTF-8"));
                        bos2.write(0x11);
                        bos2.write("\r\n".getBytes("UTF-8"));
                    }
                    bos2.write(0x13);
                    bos2.write(0x04);

                    byte[] data = bos2.toByteArray();

                    bos1.write(intToByteArray(data.length + 25));
                    bos1.write("androidpk".getBytes("UTF-8"));
                    bos1.write(deviceId.getBytes("UTF-8"));
                    bos1.write(data);
                } catch (IOException | JSONException e) {
                    Common.printException(e);
                }

                dos.write(bos1.toByteArray());
                dos.flush();

            } catch (IOException e) {
                Common.printException(e);
                return false;
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

            result = new String(bos.toByteArray(), "UTF-8").toLowerCase();

            LLog.d("=========================================");

            LLog.d("서버로부터 받은 메시지" + new String(bos.toByteArray(), "UTF-8"));

            LLog.d("=========================================");

            if (result.contains("ok")) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            Common.printException(e);
            return false;
        } finally {
            try {
                if (dis != null && socket != null) {
                    LLog.d("연결을 종료합니다.");
                    // 스트림과 소켓을 닫는다.
                    dis.close();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (mWebView != null && mCallbackName != null) {
            try {
                JSONObject param = new JSONObject();
                param.put(mCallbackName, result ? "SUCC" : "FAIL");
                JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallbackName, param);
            } catch (JSONException e) {
                Common.printException(e);
            }
        }
    }


    private byte[] intToByteArray(final int i) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(i);
        dos.flush();
        return bos.toByteArray();
    }
}
