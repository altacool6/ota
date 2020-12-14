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
    private Queue<OtaRequest>                    requestQ;              // user request queueing
    private OtaRequestLoader                    requestStriper;

    private Map<Integer, OtaCtrlServer.Response> pendingResponse4Confirm;

    //private Queue<OtaCtrlServer.Response>        pendingResponse4Download;

    private Queue<OtaStorageServer.Request>        aRequestQ;            // download request queueing

    private OtaARequestStriper                   aRequestStriper;

    private Queue<OtaStorageServer.Response> mResponseByStorage;
    
    

    private int MAX_RUNNING_REQUEST;

    public OtaClient(int maxRunningCnt){
        super();
        OtaLog.I(OtaLog.FLAG_CLIENT, "Create OtaClient instance.");

        MAX_RUNNING_REQUEST = maxRunningCnt;
        
        requestQ     = new LinkedList<OtaRequest>();
        requestStriper = new OtaRequestLoader();

        pendingResponse4Confirm  = new HashMap<Integer, OtaCtrlServer.Response>();
        //pendingResponse4Download = new LinkedList<OtaCtrlServer.Response>

        aRequestQ = new LinkedList<OtaStorageServer.Request>();
        aRequestStriper = new OtaARequestStriper();


        
        mResponseByStorage = new LinkedList<OtaStorageServer.Response>();
        
    }

    // addRequest is ota user's level api.
    public void addRequest(OtaRequest request){
        OtaLog.I(OtaLog.FLAG_CLIENT, "OtaClient get an request.");
        synchronized(requestQ){
            requestQ.offer(request);
        }
    }

    // confirmDownload is ota user's level api.
    public boolean confirmDownload(int handle){
        OtaCtrlServer.Response response = pendingResponse4Confirm.remove(handle);
        if (response == null){
            OtaLog.E(OtaLog.FLAG_CLIENT, "ERROR Wrong handle("+handle+")  valuefor confirming");
            return false;
        }

        OtaStorageServer.Request aRequest = new OtaStorageServer.Request(response);
        synchronized(aRequestQ) {
            aRequestQ.offer(aRequest);
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
                OtaStorageServer.Request aRequest = new OtaStorageServer.Request(response);
                synchronized(aRequestQ) {
                    aRequestQ.offer(aRequest);
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
            
            {   //step 1
                OtaRequest request = null;
                boolean ret = false;

                synchronized(requestQ) {
                    if (!requestQ.isEmpty())
                        request = requestQ.poll();
                }

                if (request != null)
                    ret = requestStriper._Stripe(request, this);
            }

            {   //step 2
                OtaStorageServer.Request aRequest = null;
                boolean ret = false;

                synchronized(aRequestQ) {
                    if (!aRequestQ.isEmpty())
                        aRequest = aRequestQ.poll();
                }
                if (aRequest != null)
                    ret = aRequestStriper._Stripe(aRequest, this);
            }

            try {
                OtaLog.D(OtaLog.FLAG_CLIENT, "OtaClient thread is working now");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
