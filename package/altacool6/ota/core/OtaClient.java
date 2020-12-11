package altacool6.ota.core;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;

public class OtaClient extends Thread
                    implements OtaCtrlServer.lEventListener,
                               OtaStorageServer.lEventListener {
    private Queue<OtaRequest>                mRequestQ;
    private Queue<OtaCtrlServer.Response>    mResponseByCtrl;
    private Queue<OtaStorageServer.Response> mResponseByStorage;
    private OtaCtrlServerManager             ctrlServerMgr;

    private static int watingId = 0;
    private Map<Integer, OtaCtrlServer.Response> mWaitingResponse;

    private int MAX_RUNNING_REQUEST;

    public OtaClient(int maxRunningCnt){
        super();
        OtaLog.I(OtaLog.FLAG_CLIENT, "Create OtaClient instance.");

        MAX_RUNNING_REQUEST = maxRunningCnt;
        mRequestQ          = new LinkedList<OtaRequest>();
        mResponseByCtrl    = new LinkedList<OtaCtrlServer.Response>();
        mResponseByStorage = new LinkedList<OtaStorageServer.Response>();
        mWaitingResponse   = new HashMap<Integer, OtaCtrlServer.Response>();
        
        ctrlServerMgr = new OtaCtrlServerManager();
    }

    public void addRequest(OtaRequest request){
        OtaLog.I(OtaLog.FLAG_CLIENT, "OtaClient get an request.");
        synchronized(mRequestQ){
            mRequestQ.offer(request);
        }
    }

    @Override
    public void onEventByCtrlServer(OtaCtrlServer.Response response) {
        OtaLog.I(OtaLog.FLAG_CLIENT, "OtaClient get an response via Control Server.");

        int        result   = response.getResult();
        OtaRequest           request  = response.getOtaRequest();
        OtaRequest.lCallback callback = request.getCallback();


        switch(result){
        case OtaCtrlServer.Response.CONNECTION_SUCCESS:
            callback.notify(OtaRequest.CONNECTION_SUCCESS);
            break;

        case OtaCtrlServer.Response.CONNECTION_FAILURE:
            callback.notify(OtaRequest.CONNECTION_FAILURE);
            break;

        case OtaCtrlServer.Response.NOT_SUPPORTED_FILE:
            callback.notify(OtaRequest.NOT_SUPPORTED_FILE);
            break;

        case OtaCtrlServer.Response.NO_NEED_DOWNLOAD:
            callback.notify(OtaRequest.NO_NEED_DOWNLOAD);
            break;

        case OtaCtrlServer.Response.NEED_DOWNLOAD:
            callback.notify(OtaRequest.NEED_DOWNLOAD);

            if (!request.isNeedUserConfirmForDownload()){       // no need user confirm
                synchronized(mResponseByCtrl) {
                    mResponseByCtrl.offer(response);
                }
            }
            else {                                              // need user confirm
                synchronized(mWaitingResponse) {
                    mWaitingResponse.put(watingId++, response);
                }
            }
            break;
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

            synchronized(mRequestQ) {
                if (!mRequestQ.isEmpty())
                    request = mRequestQ.poll();
            }

            if (request != null) {
                boolean ret = ctrlServerMgr._Connect(request, this);
                if (!ret)
                    request.getCallback().notify(OtaRequest.CONNECTION_FAILURE);
            }
/*
            OtaCtrlServer.Response response0 = null;
            synchronized(mResponseByCtrl) {
                if (!mResponseByCtrl.isEmpty())
                    response0 = mResponseByCtrl.poll();
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
