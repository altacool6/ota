package altacool6.ota.core;


import java.util.LinkedList;

public class OtaCtrlServerManager {
    private LinkedList<OtaCtrlServer> connectedServers;

    protected OtaCtrlServerManager(){
        connectedServers = new LinkedList<OtaCtrlServer>();
    }

    protected synchronized boolean _Connect(OtaRequest otaRequest, OtaCtrlServer.lEventListener eventListener){
        boolean bConnected = false;
        
        OtaCtrlServer          ctrlServerInReq  = otaRequest.getOtaCtrlServer();
        OtaCtrlServer          ctrlServer = null;
        OtaCtrlServer.Response response = null;

        int idx = connectedServers.indexOf(ctrlServerInReq);
        
        if (idx == -1) {
            // Control Server is not connected yet!
            // Do Connect & Add ctrlServer at connectedServers list.
            ctrlServer = connectedServers.get(idx);

            response = ctrlServer._connect();

            if (response.getResult() != OtaCtrlServer.Response.CONNECTION_SUCCESS)
                return bConnected;

            connectedServers.add(ctrlServer);
            ctrlServer._Init((a)->__Disconnect(a), eventListener);
        }

        bConnected = true;

        OtaCtrlServer.Request ctrlRequest = new OtaCtrlServer.Request(otaRequest);
        ctrlServer._AddRequest(ctrlRequest);

        return bConnected;
    }

    private synchronized void __Disconnect(OtaCtrlServer ctrlServer){
        int idx = connectedServers.indexOf(ctrlServer);

        //assert (idx > 0) : "The CtrlServer what is trying disconnect should be in connectedServers list.";

        connectedServers.remove(idx);

        ctrlServer._disconnect();
    }
}