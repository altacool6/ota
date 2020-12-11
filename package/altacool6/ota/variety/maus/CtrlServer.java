package altacool6.ota.variety.maus;

import altacool6.ota.core.OtaCtrlServer;
import altacool6.ota.core.OtaStorageServer;
import altacool6.ota.core.OtaRequest;

import static altacool6.ota.core.OtaRequest.lFileInfo;
import static altacool6.ota.core.OtaCtrlServer.Response;
import static altacool6.ota.core.OtaCtrlServer.Response.CONNECTION_SUCCESS;
import static altacool6.ota.core.OtaCtrlServer.Response.CONNECTION_SUCCESS;
import static altacool6.ota.core.OtaCtrlServer.Response.CONNECTION_FAILURE;
import static altacool6.ota.core.OtaCtrlServer.Response.NOT_SUPPORTED_FILE;
import static altacool6.ota.core.OtaCtrlServer.Response.NO_NEED_DOWNLOAD;
import static altacool6.ota.core.OtaCtrlServer.Response.NEED_DOWNLOAD;
import static altacool6.ota.core.OtaCtrlServer.Response.DISCONNECTED;

public class CtrlServer extends OtaCtrlServer{

    protected Response _connect(){
        return new Response(CONNECTION_SUCCESS, null, null);
    }

    protected void _disconnect(){
        return;
    }

    protected Response _requestCheckingFile(lFileInfo fileInfo){
        int result = NOT_SUPPORTED_FILE;
        //result = NO_NEED_DOWNLOAD;
        //result = NEED_DOWNLOAD;

        Response response = new Response(result, null, new StorageServer());
        
        return response;
    }
}