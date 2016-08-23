package org.qrtt1.wse.pushpub.metadata;

import org.apache.commons.lang.StringUtils;

import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;

public class MetadataFactory {

    private static WMSLogger logger = WMSLoggerFactory.getLogger(MetadataFactory.class);

    @SuppressWarnings("unchecked")
    public static IMetadata createByClass(String className) {
        if (StringUtils.isEmpty(className)) {
            return new DefaultMetadata();
        }

        try {
            Class<? extends IMetadata> clazz = (Class<? extends IMetadata>) Class.forName(className);
            return clazz.newInstance();
        } catch (Exception e) {
            logger.error("Cannot create metadata for class: " + className + " ", e);
        }

        return new DefaultMetadata();
    }

}
