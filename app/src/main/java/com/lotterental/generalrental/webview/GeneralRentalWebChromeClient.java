package com.lotterental.generalrental.webview;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

import com.lotterental.LLog;

/**
 * Created by macpro on 2018. 6. 15..
 */

public final class GeneralRentalWebChromeClient extends WebChromeClient {
    private final String TAG = GeneralRentalWebChromeClient.class.getSimpleName();

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
