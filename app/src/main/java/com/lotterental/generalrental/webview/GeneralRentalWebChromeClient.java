package com.lotterental.generalrental.webview;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

import com.lotterental.LLog;

/**
 * GeneralRentalWebChromeClient.
 *
 * WebView Console log 찍어주는 클래스.
 *
 * 2019-02-13
 *
 * yunseung kim.
 * yun_87k@naver.com
 */

public final class GeneralRentalWebChromeClient extends WebChromeClient {

    public GeneralRentalWebChromeClient() {
        super();
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        ConsoleMessage.MessageLevel messageLevel = consoleMessage.messageLevel();

        if ("ERROR".equals(messageLevel.toString()) && consoleMessage.message() != null) {
                return super.onConsoleMessage(consoleMessage);
        }

        String[] sourceIds = consoleMessage.sourceId().split("/");
        String sourceId = sourceIds[sourceIds.length - 1];
        String message = sourceId + " : " + consoleMessage.lineNumber() + "  " + consoleMessage.message();

        if (consoleMessage.message().indexOf("TypeError") > -1 ||
                consoleMessage.message().indexOf("ReferenceError") > -1) {
            LLog.e(message);
            return true;
        }

        return super.onConsoleMessage(consoleMessage);
    }
}
