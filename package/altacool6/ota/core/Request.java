package altacool6.ota.core;

public class Request {
    //public static final int CONNECTION_SUCCESS = 0;
    //public static final int CONNECTION_FAILURE = 1;
    //public static final int NOT_SUPPORTED_FILE = 2;
    //public static final int NO_NEED_DOWNLOAD = 3;
    //public static final int NEED_DOWNLOAD = 4;

    private lContentsInfo contentsInfo;
    private String        downloadPath;
    private boolean       bNeedConfirm;
    private Server        server; 
    private lCallback     callback;

    public Request(lContentsInfo info, String path, boolean bNeedConfirm, Server server, lCallback callback){
        this.contentsInfo = info;
        this.downloadPath = path;
        this.bNeedConfirm = bNeedConfirm;
        this.server       = server;
        this.callback     = callback;
    }

    protected final String        getDownloadPath(String path)   { return downloadPath; }
    protected final boolean       isNeedUserConfirm()            { return bNeedConfirm; }
    protected final lCallback     getCallback()                  { return callback;     }
    protected final Server        getServer()                    { return server;       }
    protected final lContentsInfo getContentsInfo()              { return contentsInfo; }
}
