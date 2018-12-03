package com.lotterental.generalrental.item;

import android.graphics.drawable.Drawable;

/**
 * Created by Jonathan on 2017-07-07.
 */

public class BarcodeListItem {
    private String barcode ;
    private String num ;

    public void setBarcode(String b) {
        barcode = b ;
    }
    public void setNum(String n) {
        num = n ;
    }

    public String getBarcode() {
        return this.barcode ;
    }
    public String getNum() {
        return this.num ;
    }
}


