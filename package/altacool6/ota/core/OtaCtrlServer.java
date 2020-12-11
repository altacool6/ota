/* Ota Control Server is abstract class.
   User should extends this class 
   and implements 4 abstract methods(connect/disconnect/requestCheckingFile/getOtaStorageServer).
   The 4 methods called internally by OTA Client. */

package altacool6.ota.core;

import java.util.LinkedList;
import java.util.Queue;

public abstract class OtaCtrlServer extends Thread{
    protected abstract Response _connect();
    protected abstract void     _disconnect();
    protected abstract Response _requestCheckingFile(OtaRequest.lFileInfo fileInfo);
    ////////////////////////////////////////////////////////////////////////////////

    private boolean bInit = false;
    private lConsumedMonitor consumedMonitor = null;
    private lEventListener   eventListener = null;

    private Queue<Request> requestQ;

    protected final void _Init(lConsumedMonitor consumedMonitor, lEventListener eventListener) {
        if (!bInit) {
            requestQ = new LinkedList<Request>();

            if (requestQ != null) {
                this.consumedMonitor = consumedMonitor;
                this.eventListener   = eventListener;
                bInit = true;
            }
        }
    }

    protected final void _AddRequest(Request request) {
        requestQ.offer(request);

        Thread.State state = getState();

        if (state == Thread.State.NEW)
            start();
    }

    public final void run() {
        OtaLog.I(OtaLog.FLAG_CLIENT, "OtaClient thread is started.");
        long lastTime = 0, curTime= 0; 

        while (true){
            OtaRequest userRequest = null;
            Request  request = null;
            Response response = null;
            OtaRequest.lFileInfo fileInfo = null;

            synchronized(requestQ) {
                if (requestQ.isEmpty()){
                    long idleTime = System.nanoTime() - lastTime;

                    if ((idleTime/1000000000) > 5) break;

                    continue;
                }

                lastTime = System.nanoTime(); 

                request     = requestQ.poll();
                userRequest = request.getUserRequest();

                fileInfo = userRequest.getOtaFileInfo();

                response =_requestCheckingFile(fileInfo);

                eventListener.onEventByCtrlServer(response);
            }            
        }

        consumedMonitor.consumed(this);
    }

        
    public static class Request{
        private OtaRequest generatedReq;
        
        public Request(OtaRequest generatedReq){
            this.generatedReq = generatedReq;
        }

        public OtaRequest getUserRequest(){
            return generatedReq;
        }
    }
    public static class Response {
        public static final int CONNECTION_SUCCESS = 0;
        public static final int CONNECTION_FAILURE = 1;
        public static final int NOT_SUPPORTED_FILE = 2;
        public static final int NO_NEED_DOWNLOAD   = 3;
        public static final int NEED_DOWNLOAD      = 4;
        public static final int DISCONNECTED       = 5;

        private int        result;
        private OtaRequest request;
        private OtaStorageServer storageServer;

        private int        fileSize;
        private int        fileName;
        private String     comment;

        public Response(int ret, OtaRequest request, OtaStorageServer server){
            this.result = ret;
            this.request = request;
            this.storageServer = server;
        }

        protected final int              getResult()           { return result; }
        protected final OtaRequest       getOtaRequest()       { return request; }
        protected final OtaStorageServer getOtaStorageServer() { return storageServer; };
    }

    public interface lEventListener {
        void onEventByCtrlServer(Response response);
    }

    public interface lConsumedMonitor{
        void consumed(OtaCtrlServer ctrlServer);
    }
}


