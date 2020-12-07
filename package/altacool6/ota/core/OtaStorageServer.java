package altacool6.ota.core;

abstract class OtaStorageServer extends Thread{
    private lEventListener mlistener;

    public void startAsyncDownload(lEventListener listener){
        mlistener = listener;
        start();
    }

    abstract void connect();
    abstract void disconnect();

    public void run() {
        connect();
        while(true){
            break;
        }
        disconnect();
    }

    abstract class Response { //아직 정해지지 않음.
        public int a;
        public abstract int result();
    }

    interface lEventListener {
        void onEventByStorageServer(Response response);
    }
}
