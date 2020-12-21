package altacool6.ota.core;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;

public class Agent extends Thread
                    implements Server.lResponseReceiver{
    // class member
    //private static Map<Integer, Integer> s_mapResp2ReqResult;
    private static int                   s_handle4PendingResp;
    static {
        s_handle4PendingResp = 0;
        //s_mapResp2ReqResult = new HashMap<Integer, Integer>();
        //s_mapResp2ReqResult.put(Response.CONNECTION_SUCCESS,      Request.CONNECTION_SUCCESS);
        //s_mapResp2ReqResult.put(Response.CONNECTION_FAILURE,      Request.CONNECTION_FAILURE);
        //s_mapResp2ReqResult.put(Response.NOT_SUPPORTED_FILE,      Request.NOT_SUPPORTED_FILE);
        //s_mapResp2ReqResult.put(Response.CONTENTS_IS_LATEST,      Request.CONTENTS_IS_LATEST);
        //s_mapResp2ReqResult.put(Response.CONTENTS_NEED_DOWNLOAD,  Request.CONTENTS_NEED_DOWNLOAD);
    }

    // instance member
    private Queue<Request>         requestQ;              // user request queueing
    private RequestLoader          requestLoader;
    private Map<Integer, Response> wait4Confirm; 
    

    private int MAX_RUNNING_REQUEST;

    public Agent(int maxRunningCnt){
        super();
        Log.I(Log.FLAG_CLIENT, "Create Agent instance.");

        MAX_RUNNING_REQUEST = maxRunningCnt;
        
        requestQ      = new LinkedList<Request>();
        requestLoader = new RequestLoader();
        wait4Confirm  = new HashMap<Integer, Response>();
    }

    // addRequest is ota user's level api.
    public void addRequest(Request request){
        Log.I(Log.FLAG_CLIENT, "Agent get an request.");
        synchronized(requestQ){
            requestQ.offer(request);
        }
    }

    // confirmDownload is ota user's level api.
    public boolean confirmDownload(int handle){
        Response response = wait4Confirm.remove(handle);
        if (response == null){
            Log.E(Log.FLAG_CLIENT, "ERROR Wrong handle("+handle+")  valuefor confirming");
            return false;
        }

        Request request = response.getRedirectRequest();
        addRequest(request);
        return true;
    }

    @Override
    public void onReceiveResponse(Response response) {
        Log.I(Log.FLAG_CLIENT, "onReceiveResponse");

        int        result           = response.getResult();
        Request    sourceRequest    = response.getSourceRequest();
        Request    redirectRequest  = response.getRedirectRequest();
        
        lCallback callback = sourceRequest.getCallback();
        
        if (result != Response.CONTENTS_NEED_DOWNLOAD) {
            callback.notify(result, null);
        }
        else {
            if (sourceRequest.isNeedUserConfirm()) {
                synchronized(wait4Confirm) {
                    wait4Confirm.put(s_handle4PendingResp++, response);
                }
                callback.notify(result, null);
            }
            else {
                if (redirectRequest != null)
                    addRequest(redirectRequest);
                else {
                    sourceRequest.setNeedUserConfirm();
                    addRequest(sourceRequest);
                }
            }
        }
    }

    public void run() {
        Log.I(Log.FLAG_CLIENT, "Agent thread is started.");

        while (true){
            Request request = null;
            boolean ret = false;

            synchronized(requestQ) {
                if (!requestQ.isEmpty())
                    request = requestQ.poll();
            }

            if (request != null)
                ret = requestLoader._Load(request, this);

            try {
                Log.D(Log.FLAG_CLIENT, "Agent thread is working now");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
