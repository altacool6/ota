package altacool6.ota.variety.maus;

import altacool6.ota.core.Server;
import altacool6.ota.core.Request;
import altacool6.ota.core.Response;
import altacool6.ota.core.lContentsInfo;

import static altacool6.ota.core.Response.CONNECTION_SUCCESS;
import static altacool6.ota.core.Response.CONNECTION_FAILURE;
import static altacool6.ota.core.Response.CONTENTS_IS_WRONG;
import static altacool6.ota.core.Response.CONTENTS_IS_LATEST;
import static altacool6.ota.core.Response.CONTENTS_NEED_DOWNLOAD;
import static altacool6.ota.core.Response.DISCONNECTED;

public class MausCtrlServer extends Server{

    protected Response _Connect(){
        return new Response(CONNECTION_SUCCESS, null, null);
    }

    protected void _Disconnect(){
        return;
    }

    protected Response _ProcessRequest(Server.ServerRequest serverRequest){
        int result = CONTENTS_IS_WRONG;
        //result = CONTENTS_IS_LATEST;
        //result = NEED_DOWNLOAD;

        //Response response = new Response(result, null, new StorageServer());
        
        return null;
    }
}