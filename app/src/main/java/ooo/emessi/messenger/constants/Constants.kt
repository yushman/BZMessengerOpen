package ooo.emessi.messenger.constants

import android.os.Build

object Constants {
 
    val DEVICE_ID = "35" +
            Build.BOARD.length%10 + Build.BRAND.length%10 +
            Build.DEVICE.length%10 +
            Build.DISPLAY.length%10 + Build.HOST.length%10 +
            Build.ID.length%10 + Build.MANUFACTURER.length%10 +
            Build.MODEL.length%10 + Build.PRODUCT.length%10 +
            Build.TAGS.length%10 + Build.TYPE.length%10 +
            Build.USER.length%10

}
