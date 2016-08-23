package org.qrtt1.wse.pushpub.storage.m3u8;

import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;

public class M3U8StorageFactory {

    private static WMSLogger logger = WMSLoggerFactory.getLogger(M3U8StorageFactory.class);

    @SuppressWarnings("unchecked")
    public static IM3U8ModelStorage createByClass(String className) {

        try {
            Class<? extends IM3U8ModelStorage> clazz = (Class<? extends IM3U8ModelStorage>) Class.forName(className);
            return clazz.newInstance();
        } catch (Exception e) {
            logger.error("Cannot create storage for class: " + className + " ", e);
        }

        return new NullM3U8Storage();
    }

}
