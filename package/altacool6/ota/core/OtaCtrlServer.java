/* Ota Control Server is abstract class.
   User should extends this class 
   and implements 4 abstract methods(connect/disconnect/requestCheckingFile/getOtaStorageServer).
   The 4 methods called internally by OTA Client. */

package altacool6.ota.core;

public abstract class OtaCtrlServer extends Thread{
    private lOtaFileInfo mFileInfo;
    private lEventListener mListener;

    protected abstract Result           connect();
    protected abstract void             disconnect();
    protected abstract Result           requestCheckingFile();
    protected abstract OtaStorageServer getOtaStorageServer();

    protected final void startAsyncCheck(lOtaFileInfo fileInfo, lEventListener listener){
        mFileInfo = fileInfo;
        mListener = listener;
        start();
    }

    public final void run() {
        Result ret;

        ret = connect();
        if (ret != Result.SUCCESS) {
            //mListener.onEventByCtrlServer();
        }

        ret = requestCheckingFile();
        if (ret != Result.SUCCESS &&
                ret != Result.NO_NEED_DOWNLOAD &&
                ret != Result.NEED_DOWNLOAD) {
            //mListener.onEventByCtrlServer();
        }

        disconnect();

        if (ret != Result.NEED_DOWNLOAD) {
            //mListener.onEventByCtrlServer();
        }
    }

    public abstract class Response {
        private OtaRequest mRequest;

        private int fileSize;
        private int fileName;
        private String comment;

        public OtaRequest getOtaRequest(){
            return mRequest;
        }
        public void setOtaRequest(OtaRequest request){
            mRequest = request;
        }

        public abstract OtaStorageServer getOtaStorageServer();
    }

    public interface lEventListener {
        void onEventByCtrlServer(Response response);
    }

    public enum Result{SUCCESS, CONNECTION_FAILURE, NOT_SUPPORTED_FILE, NO_NEED_DOWNLOAD, NEED_DOWNLOAD};
}


