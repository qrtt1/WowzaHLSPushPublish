package org.qrtt1.wse.pushpub.storage;

import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;

public class SimpleStorageFactory {

    private static WMSLogger logger = WMSLoggerFactory.getLogger(SimpleStorageFactory.class);

    @SuppressWarnings("unchecked")
    public static ISimpleStorage createByClass(String className) {

        if (className == null) {
            return new NullSimpleStorage();
        }

        try {
            Class<? extends ISimpleStorage> clazz = (Class<? extends ISimpleStorage>) Class.forName(className);
            return clazz.newInstance();
        } catch (Exception e) {
            logger.error("Cannot create storage for class: " + className + " ", e);
        }

        return new NullSimpleStorage();
    }

}
