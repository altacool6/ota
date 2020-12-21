package altacool6.ota.core;

public class Request {
    private       lContentsInfo contentsInfo;
    private final String        downloadPath;
    private       boolean       bNeedConfirm;
    private final Server        server; 
    private final lCallback     callback;

    public Request(String path, boolean bNeedConfirm, 
                   Server server, lCallback callback){
        this.downloadPath = path;
        this.bNeedConfirm = bNeedConfirm;
        this.server       = server;
        this.callback     = callback;
    }

    protected final String        getDownloadPath(String path)   { return downloadPath; }
    protected final boolean       isNeedUserConfirm()            { return bNeedConfirm; }
    protected final void          setNeedUserConfirm()           { bNeedConfirm = true; }
    protected final lCallback     getCallback()                  { return callback;     }
    protected final Server        getServer()                    { return server;       }

    protected final lContentsInfo getContentsInfo()                  { return contentsInfo; }
    public    final void          setContentsInfo(lContentsInfo info){ contentsInfo = info; }
}
