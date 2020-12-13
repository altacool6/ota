package altacool6.ota.core;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;

public class OtaClient extends Thread
                    implements OtaCtrlServer.lEventListener,
                               OtaStorageServer.lEventListener {
    // class member
    private static Map<Integer, Integer> s_mapResp2ReqResult;
    private static int                   s_handle4PendingResp;
    static {
        s_handle4PendingResp = 0;
        s_mapResp2ReqResult = new HashMap<Integer, Integer>();
        s_mapResp2ReqResult.put(OtaCtrlServer.Response.CONNECTION_SUCCESS, OtaRequest.CONNECTION_SUCCESS);
        s_mapResp2ReqResult.put(OtaCtrlServer.Response.CONNECTION_FAILURE, OtaRequest.CONNECTION_FAILURE);
        s_mapResp2ReqResult.put(OtaCtrlServer.Response.NOT_SUPPORTED_FILE, OtaRequest.NOT_SUPPORTED_FILE);
        s_mapResp2ReqResult.put(OtaCtrlServer.Response.NO_NEED_DOWNLOAD,   OtaRequest.NO_NEED_DOWNLOAD);
        s_mapResp2ReqResult.put(OtaCtrlServer.Response.NEED_DOWNLOAD,      OtaRequest.NEED_DOWNLOAD);
    }

    // instance member
    private Queue<OtaRequest>                    requestQ;

    private OtaCtrlServerManager              ctrlServerMgr;

    private Map<Integer, OtaCtrlServer.Response> pendingResponse4Confirm;

    private Queue<OtaCtrlServer.Response>        pendingResponse4Download;

    private Queue<OtaStorageServer.Response > mResponseByStorage;
    
    

    private int MAX_RUNNING_REQUEST;

    public OtaClient(int maxRunningCnt){
        super();
        OtaLog.I(OtaLog.FLAG_CLIENT, "Create OtaClient instance.");

        MAX_RUNNING_REQUEST = maxRunningCnt;
        
        requestQ     = new LinkedList<OtaRequest>();

        ctrlServerMgr = new OtaCtrlServerManager();

        pendingResponse4Confirm   = new HashMap<Integer, OtaCtrlServer.Response>();
        pendingResponse4Download    = new LinkedList<OtaCtrlServer.Response>();
        
        mResponseByStorage = new LinkedList<OtaStorageServer.Response>();
        
    }

    public void addRequest(OtaRequest request){
        OtaLog.I(OtaLog.FLAG_CLIENT, "OtaClient get an request.");
        synchronized(requestQ){
            requestQ.offer(request);
        }
    }

    public boolean confirmDownload(int handle){
        OtaCtrlServer.Response response = pendingResponse4Confirm.get(handle);
        if (response == null)
            return false;
        
        synchronized(pendingResponse4Download) {
            pendingResponse4Download.offer(response);
        }
        return true;
    }

    @Override
    public void onEventByCtrlServer(OtaCtrlServer.Response response) {
        OtaLog.I(OtaLog.FLAG_CLIENT, "OtaClient get an response via Control Server.");

        int        result   = response.getResult();
        OtaRequest           request  = response.getOtaRequest();
        OtaRequest.lCallback callback = request.getCallback();

        Integer requestResult = s_mapResp2ReqResult.get(result);
        if (requestResult != null)
            callback.notify(requestResult);
        else 
            OtaLog.E(OtaLog.FLAG_CLIENT, "s_mapResp2ReqResult is wrong. key("+result+")");
    
        if (result == OtaCtrlServer.Response.NEED_DOWNLOAD){
            if (!request.isNeedUserConfirmForDownload()){       // no need user confirm
                synchronized(pendingResponse4Download) {
                    pendingResponse4Download.offer(response);
                }
            }
            else {                                              // need user confirm
                synchronized(pendingResponse4Confirm) {
                    pendingResponse4Confirm.put(s_handle4PendingResp++, response);
                }
            }
        }
    }

    @Override
    public void onEventByStorageServer(OtaStorageServer.Response response) {
        OtaLog.I(OtaLog.FLAG_CLIENT, "OtaClient get an response via Storage Server.");
        synchronized(mResponseByStorage) {
            mResponseByStorage.offer(response);
        }
    }
    public void run() {
        OtaLog.I(OtaLog.FLAG_CLIENT, "OtaClient thread is started.");

        while (true){
            OtaRequest request = null;

            synchronized(requestQ) {
                if (!requestQ.isEmpty())
                    request = requestQ.poll();
            }

            if (request != null) {
                boolean ret = ctrlServerMgr._AllocateRequestToServer(request, this);
            }

/*
            OtaCtrlServer.Response response0 = null;
            synchronized(pendingResponse4Download) {
                if (!pendingResponse4Download.isEmpty())
                    response0 = pendingResponse4Download.poll();
            }

            if (response0 != null) {
                OtaStorageServer server = response0.getOtaStorageServer();
                server.startAsyncDownload(this);
            }

            OtaStorageServer.Response response1 = null;

            synchronized(mResponseByStorage) {
                if (!mResponseByStorage.isEmpty())
                    response1 = mResponseByStorage.poll();
            }

            if (response1 != null) {

            }
*/
            try {
                OtaLog.D(OtaLog.FLAG_CLIENT, "OtaClient thread is working now");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
