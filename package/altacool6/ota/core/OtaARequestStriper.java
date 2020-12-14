package altacool6.ota.core;

import java.util.LinkedList;

public class OtaARequestStriper {
    
    private LinkedList<OtaStorageServer> connectedServers;

    protected OtaARequestStriper(){
        connectedServers = new LinkedList<OtaStorageServer>();
    }

    protected synchronized boolean _Stripe(OtaStorageServer.Request otaARequest, 
                                           OtaStorageServer.lEventListener eventListener){
        boolean bConnected = false;
        
        OtaStorageServer          serverInReq  = otaARequest.getOtaStorageServer();
        OtaStorageServer          server = null;
        OtaStorageServer.Response response = null;

        int idx = connectedServers.indexOf(serverInReq);
        
        if (idx == -1) {
            // Storage Server is not connected yet!
            // Do Connect & Add ctrlServer at connectedServers list.
            server = serverInReq;

            response = server._connect();
            eventListener.onEventByStorageServer(response);

            if (response.getResult() != OtaStorageServer.Response.CONNECTION_SUCCESS)
                return bConnected;

            connectedServers.add(server);
            server._Init((a)->__FinalizeServer(a), eventListener);
        }
        else {
            server = connectedServers.get(idx);
        }

        bConnected = true;

        server._AddRequest(otaARequest);

        return bConnected;
    }

    private synchronized void __FinalizeServer(OtaStorageServer server){
        int idx = connectedServers.indexOf(server);

        //assert (idx > 0) : "The CtrlServer what is trying disconnect should be in connectedServers list.";

        connectedServers.remove(idx);

        server._disconnect();
    }
}