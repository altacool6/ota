/* Ota Control Server is abstract class.
   User should extends this class 
   and implements 4 abstract methods(connect/disconnect/requestCheckingFile/getOtaStorageServer).
   The 4 methods called internally by OTA Client. */

package altacool6.ota.core;

import java.util.LinkedList;
import java.util.Queue;

public abstract class Server extends Thread{
    protected abstract Response _Connect();
    protected abstract void     _Disconnect();
    protected abstract Response _ProcessRequest(ServerRequest serverRequest);
    ////////////////////////////////////////////////////////////////////////////////

    private boolean bInit = false;
    private lConsumedMonitor  consumedMonitor = null;
    private lResponseReceiver responseReceiver = null;

    private Queue<ServerRequest> requestQ;

    protected final void _Init(lConsumedMonitor consumedMonitor, lResponseReceiver responseReceiver) {
        if (!bInit) {
            requestQ = new LinkedList<ServerRequest>();

            if (requestQ != null) {
                this.consumedMonitor  = consumedMonitor;
                this.responseReceiver = responseReceiver;
                bInit = true;
            }
        }
    }

    protected final void _AddRequest(ServerRequest request) {
        requestQ.offer(request);

        Thread.State state = getState();

        if (state == Thread.State.NEW)
            start();
    }

    public final void run() {
        Log.I(Log.FLAG_CLIENT, "OtaClient thread is started.");
        long lastTime = 0, curTime= 0; 

        while (true){
            ServerRequest  serverRequest = null;
            Request        sourceRequest = null;
            Response       response = null;
            lContentsInfo contentsInfo = null;

            synchronized(requestQ) {
                if (requestQ.isEmpty()){
                    long idleTime = System.nanoTime() - lastTime;

                    if ((idleTime/1000000000) > 5) break;

                    continue;
                }

                lastTime = System.nanoTime(); 

                serverRequest = requestQ.poll();
                //sourceRequest = serverRequest.getSourceRequest();
                //contentsInfo  = sourceRequest.getContentsInfo();

                response =_ProcessRequest(serverRequest);
                responseReceiver.onReceiveResponse(response);
            }            
        }

        consumedMonitor.consumed(this);
    }

        
    public static class ServerRequest{
        private final long cTime;
        private Request sourceRequest;
        
        public ServerRequest(Request sourceRequest){
            cTime = System.nanoTime(); 
            this.sourceRequest = sourceRequest;
        }

        public Request getSourceRequest(){
            return sourceRequest;
        }
    }
    
    public interface lResponseReceiver {
        void onReceiveResponse(Response response);
    }

    public interface lConsumedMonitor{
        void consumed(Server ctrlServer);
    }
}


