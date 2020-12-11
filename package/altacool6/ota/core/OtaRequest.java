package altacool6.ota.core;

public abstract class OtaRequest {
    public static final int CONNECTION_SUCCESS = 0;
    public static final int CONNECTION_FAILURE = 1;
    public static final int NOT_SUPPORTED_FILE = 2;
    public static final int NO_NEED_DOWNLOAD = 3;
    public static final int NEED_DOWNLOAD = 4;

    private String        downloadPath;
    private boolean       bNeedConfirmForDownload;
    private lCallback     callback;
    private lFileInfo     fileInfo;
    private OtaCtrlServer ctrlServer; 

    protected final String        getDownloadPath()                                   { return downloadPath;      }
    public    final void          setDownloadPath(String path)                        { this.downloadPath = path; }

    protected final boolean       isNeedUserConfirmForDownload()                      { return bNeedConfirmForDownload;         }
    public    final void          setNeedUserConfirmForDownload(boolean bNeedConfirm) { bNeedConfirmForDownload = bNeedConfirm; }

    protected final lCallback     getCallback()                                       { return callback;          }
    public    final void          setCallback(lCallback callback)                     { this.callback = callback; }

    public    final OtaCtrlServer getOtaCtrlServer()                                  { return ctrlServer;   }
    public    final void          setOtaCtrlServer(OtaCtrlServer server)              { ctrlServer = server; }

    protected final lFileInfo     getOtaFileInfo()                                    { return fileInfo; }
    public    final void          setOtaFileInfo(lFileInfo info)                      { fileInfo = info; }

    public interface lCallback {
        void notify(int result);
    }

    public interface lFileInfo {
        String getId();
        String getValue();
    }
}
