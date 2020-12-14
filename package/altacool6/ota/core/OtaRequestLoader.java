package altacool6.ota.core;

import java.util.LinkedList;

public class OtaRequestLoader {
    private LinkedList<OtaCtrlServer> connectedServers;

    protected OtaRequestLoader(){
        connectedServers = new LinkedList<OtaCtrlServer>();
    }

    protected synchronized boolean _Stripe(OtaRequest otaRequest, 
                                           OtaCtrlServer.lEventListener eventListener){
        boolean bConnected = false;
        
        OtaCtrlServer            serverInReq  = null;
        OtaCtrlServer            server = null;
        OtaCtrlServer.Response   response = null;

        serverInReq  = otaRequest.getOtaCtrlServer();

        int idx = connectedServers.indexOf(serverInReq);
        
        if (idx == -1) {
            // Control Server is not connected yet!
            // Do Connect & Add ctrlServer at connectedServers list.
            server = serverInReq;

            response = server._connect();
            eventListener.onEventByCtrlServer(response);

            if (response.getResult() != OtaCtrlServer.Response.CONNECTION_SUCCESS)
                return bConnected;

            connectedServers.add(server);
            server._Init((a)->__FinalizeServer(a), eventListener);
        }
        else {
            server = connectedServers.get(idx);
        }

        bConnected = true;

        OtaCtrlServer.Request ctrlRequest = new OtaCtrlServer.Request(otaRequest);
        server._AddRequest(ctrlRequest);

        return bConnected;
    }

    private synchronized void __FinalizeServer(OtaCtrlServer server){
        int idx = connectedServers.indexOf(server);

        //assert (idx > 0) : "The CtrlServer what is trying disconnect should be in connectedServers list.";

        connectedServers.remove(idx);

        server._disconnect();
    }
}