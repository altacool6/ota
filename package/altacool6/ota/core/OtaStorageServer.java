/* Ota Storage Server is abstract class.
   User should extends this class 
   and implements 3 abstract methods(connect/disconnect/runAsyncDownload).
   The 3 methods called internally by OTA Client. */

package altacool6.ota.core;

import java.util.LinkedList;
import java.util.Queue;

public abstract class OtaStorageServer extends Thread{
    
    protected abstract Response _connect();          // This method be called internally by OTA Client. can not call directly by user.
    protected abstract void     _disconnect();       // This method be called internally by OTA Client. can not call directly by user.
    protected abstract Response _runAsyncDownload(OtaRequest.lFileInfo fileInfo); // This method be called internally by OTA Client. can not call directly by user.
    ///////////////////////////////////////////////////////////////////////////////////////////////

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
        OtaLog.I(OtaLog.FLAG_CLIENT, "OtaStorage thread is started.");
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

                response =_runAsyncDownload(fileInfo);

                eventListener.onEventByStorageServer(response);
            }            
        }

        consumedMonitor.consumed(this);
    }

    public static class Request{
        private OtaRequest             generatedReq;
        private OtaCtrlServer.Response generatedResp;
        private OtaStorageServer       storageServer; 
        
        public Request(OtaCtrlServer.Response generatedResp){
            this.generatedReq  = generatedResp.getOtaRequest();
            this.generatedResp = generatedResp;
            this.storageServer = generatedResp.getOtaStorageServer();
        }

        public OtaRequest getUserRequest(){
            return generatedReq;
        }

        public final OtaStorageServer getOtaStorageServer()                        { return storageServer;   }
        public final void             setOtaStorageServer(OtaStorageServer server) { storageServer = server; }
    }

    public static class Response { //아직 정해지지 않음.
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
        void onEventByStorageServer(Response response);
    }

    public interface lConsumedMonitor{
        void consumed(OtaStorageServer ctrlServer);
    }
}
