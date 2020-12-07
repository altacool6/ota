package altacool6.ota.core;

import java.util.LinkedList;
import java.util.Queue;

public class OtaClient extends Thread
                    implements OtaCtrlServer.lEventListener,
                               OtaStorageServer.lEventListener {
    private Queue<OtaRequest> mRequestQ;
    private Queue<OtaCtrlServer.Response> mResponseByCtrl;
    private Queue<OtaStorageServer.Response> mResponseByStorage;

    private int MAX_RUNNING_REQUEST;

    public OtaClient(int maxRunningCnt){
        super();
        MAX_RUNNING_REQUEST = maxRunningCnt;
        mRequestQ = new LinkedList<OtaRequest>();
        mResponseByCtrl = new LinkedList<OtaCtrlServer.Response>();
        mResponseByStorage = new LinkedList<OtaStorageServer.Response>();
    }

    public void addRequest(OtaRequest request){
        synchronized(mRequestQ){
            mRequestQ.offer(request);
        }
    }

    @Override
    public void onEventByCtrlServer(OtaCtrlServer.Response response) {
        synchronized(mResponseByCtrl) {
            mResponseByCtrl.offer(response);
        }
    }

    @Override
    public void onEventByStorageServer(OtaStorageServer.Response response) {
        synchronized(mResponseByStorage) {
            mResponseByStorage.offer(response);
        }
    }
    public void run() {
        while (true){
            OtaRequest request = null;

            synchronized(mRequestQ) {
                if (!mRequestQ.isEmpty())
                    request = mRequestQ.poll();
            }

            if (request != null) {
                OtaCtrlServer server = request.getOtaCtrlServer();
                lOtaFileInfo fileInfo = request.getOtaFileInfo();

                server.startAsyncCheck(fileInfo, this);
            }

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


            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
