package com.wbem.checker.modules.executors;

import javax.cim.CIMInstance;
import javax.cim.CIMProperty;
import javax.cim.UnsignedInteger64;

import com.wbem.checker.api.connection.WBEMConnection;
import com.wbem.checker.api.data.Request;
import com.wbem.checker.api.data.Response;
import com.wbem.checker.api.exceptions.InvalidParametersException;
import com.wbem.checker.api.exceptions.SourceIsNotFoundException;

public class FSVolumeRequestExecutor extends AbstractLoadPercentageExecutor {
    private static final String REQUEST_TYPE = "disk_usage";
    private static final String MOUNT_POINT = "-mount_point";
    private static final String PURPOSE = "get loading of file system";
    
    public static final String path = "CIM_FileSystem";
    public static final String freeSpaceProperty = "AvailableSpace";//UINT_64
    public static final String totalSystemSizeProperty = "FileSystemSize";//UINT_64
    public static final String mountPointProperty = "ElementName";//String
    
    
    private String targetMountPoint = null;
    
    @Override
    public void validateRequest(Request request) throws InvalidParametersException {
        super.validateRequest(request);
        targetMountPoint = request.getParameterValue(MOUNT_POINT);
    }
    
    @Override
    public String[] getRequiredParameters() {
        return new String[] { AbstractLoadPercentageExecutor.warningParam, 
                              AbstractLoadPercentageExecutor.critParam,
                              MOUNT_POINT};
    }
    
    @Override
    public Response execute(WBEMConnection connection) throws Exception {
        CIMInstance[] fileSystems = connection.getAllInstances(path);
        if ( fileSystems.length == 0 ) {
            throw new SourceIsNotFoundException(path, null, PURPOSE);
        }
        CIMInstance targetFS = null;
        for ( CIMInstance fs : fileSystems ) {
            CIMProperty elementName = fs.getProperty(mountPointProperty);
            if ( elementName != null && targetMountPoint.equals(elementName.getValue())) {
                targetFS = fs;
                break;
            }
        }
        if ( targetFS == null ) {
            throw new SourceIsNotFoundException(path, null, "get loading of file system");
        }
        CIMProperty free = targetFS.getProperty(freeSpaceProperty);
        CIMProperty total = targetFS.getProperty(totalSystemSizeProperty);
        return createResponse(computeLoadPercentage(total, free));
    }

    @Override
    public String getRequestType() {
        return REQUEST_TYPE;
    }

    @Override
    public boolean isAuthentificateNeeded() {
        return true;
    }

    @Override
    protected String getMessage() {
        return "Loading of file system mounted at '" + targetMountPoint + "' is ";
    }
    
    private int computeLoadPercentage(CIMProperty total, CIMProperty free) throws Exception {
        if (total == null || free == null ){
            throw new SourceIsNotFoundException(path, total == null ? totalSystemSizeProperty : freeSpaceProperty, PURPOSE);
        }
        UnsignedInteger64 totalValue = (UnsignedInteger64) total.getValue();
        if (totalValue.intValue() == 0) {
            return 0;
        }
        UnsignedInteger64 freeValue = (UnsignedInteger64) free.getValue();
        return 100 - (int) Math.round(freeValue.doubleValue() / totalValue.doubleValue() * 100);
    }
}
