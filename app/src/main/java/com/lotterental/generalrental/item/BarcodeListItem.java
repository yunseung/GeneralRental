package com.lotterental.generalrental.item;

import android.graphics.drawable.Drawable;

/**
 * Created by Jonathan on 2017-07-07.
 */

public class BarcodeListItem {
    private String barcode ;

    public BarcodeListItem(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return this.barcode ;
    }
}


