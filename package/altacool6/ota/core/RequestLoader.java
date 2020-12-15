package altacool6.ota.core;

import java.util.LinkedList;

public class RequestLoader {
    private LinkedList<Server> connectedServers;

    protected RequestLoader(){
        connectedServers = new LinkedList<Server>();
    }

    protected synchronized boolean _Load(Request request, 
                                         Server.lResponseReceiver cb){
        boolean    bConnected  = false;
        Server     serverInReq = null;
        Server     server      = null;
        Response   response    = null;

        Log.I(Log.FLAG_CLIENT, "Create OtaClient instance.");

        server = request.getServer();

        int idx = connectedServers.indexOf(serverInReq);
        
        if (idx == -1) {
            // Control Server is not connected yet!
            // Do Connect & Add ctrlServer at connectedServers list.
            server = serverInReq;

            response = server._connect();
            cb.onReceiveResponse(response);

            if (response.getResult() != Response.CONNECTION_SUCCESS)
                return bConnected;

            connectedServers.add(server);
            server._Init((a)->__FinalizeServer(a), cb);
        }
        else {
            server = connectedServers.get(idx);
        }

        bConnected = true;

        Server.ServerRequest serverRequest = new Server.ServerRequest(request);
        server._AddRequest(serverRequest);

        return bConnected;
    }

    private synchronized void __FinalizeServer(Server server){
        int idx = connectedServers.indexOf(server);

        //assert (idx > 0) : "The CtrlServer what is trying disconnect should be in connectedServers list.";

        connectedServers.remove(idx);

        server._disconnect();
    }
}