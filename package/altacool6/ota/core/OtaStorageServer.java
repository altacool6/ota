/* Ota Storage Server is abstract class.
   User should extends this class 
   and implements 3 abstract methods(connect/disconnect/runAsyncDownload).
   The 3 methods called internally by OTA Client. */

package altacool6.ota.core;

public abstract class OtaStorageServer extends Thread{
    private lEventListener mlistener;

    protected abstract void connect();          // This method be called internally by OTA Client. can not call directly by user.
    protected abstract void disconnect();       // This method be called internally by OTA Client. can not call directly by user.
    protected abstract void runAsyncDownload(); // This method be called internally by OTA Client. can not call directly by user.

    protected final void startAsyncDownload(lEventListener listener){
        mlistener = listener;
        start();
    }

    public final void run() {
        connect();
        runAsyncDownload();
        disconnect();
    }

    public abstract class Response { //아직 정해지지 않음.
        public int a;
        public abstract int result();
    }

    public interface lEventListener {
        void onEventByStorageServer(Response response);
    }
}
