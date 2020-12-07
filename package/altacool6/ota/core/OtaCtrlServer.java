package altacool6.ota.core;

abstract class OtaCtrlServer extends Thread{
    private lOtaFileInfo mFileInfo;
    private lEventListener mListener;

    public void startAsyncCheck(lOtaFileInfo fileInfo, lEventListener listener){
        mFileInfo = fileInfo;
        mListener = listener;
        start();
    }

    abstract Result connect();
    abstract void disconnect();
    abstract Result requestCheckingFile();
    abstract OtaStorageServer getOtaStorageServer();

    public void run() {
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

    abstract class Response {
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

    interface lEventListener {
        void onEventByCtrlServer(Response response);
    }

    enum Result{SUCCESS, CONNECTION_FAILURE, NOT_SUPPORTED_FILE, NO_NEED_DOWNLOAD, NEED_DOWNLOAD};
}


