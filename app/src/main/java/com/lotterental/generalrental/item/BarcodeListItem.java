package com.lotterental.generalrental.item;

import android.graphics.drawable.Drawable;

/**
 * Created by Jonathan on 2017-07-07.
 */

public class BarcodeListItem {
    private String barcode ;
    private String num ;

    public BarcodeListItem(String num, String barcode) {
        this.barcode = barcode;
        this.num = num;
    }

    public String getBarcode() {
        return this.barcode ;
    }
    public String getNum() {
        return this.num ;
    }
}


