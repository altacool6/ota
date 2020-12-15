package altacool6.ota.variety.maus;

import altacool6.ota.core.Server;
import altacool6.ota.core.OtaStorageServer;
import altacool6.ota.core.OtaRequest;

import static altacool6.ota.core.OtaRequest.lContentsInfo;
import static altacool6.ota.core.Response;
import static altacool6.ota.core.Response.CONNECTION_SUCCESS;
import static altacool6.ota.core.Response.CONNECTION_SUCCESS;
import static altacool6.ota.core.Response.CONNECTION_FAILURE;
import static altacool6.ota.core.Response.NOT_SUPPORTED_FILE;
import static altacool6.ota.core.Response.NO_NEED_DOWNLOAD;
import static altacool6.ota.core.Response.NEED_DOWNLOAD;
import static altacool6.ota.core.Response.DISCONNECTED;

public class CtrlServer extends Server{

    protected Response _connect(){
        return new Response(CONNECTION_SUCCESS, null, null);
    }

    protected void _disconnect(){
        return;
    }

    protected Response _processRequest(lContentsInfo fileInfo){
        int result = NOT_SUPPORTED_FILE;
        //result = NO_NEED_DOWNLOAD;
        //result = NEED_DOWNLOAD;

        Response response = new Response(result, null, new StorageServer());
        
        return response;
    }
}