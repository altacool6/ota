package altacool6.ota.core;

abstract class OtaRequest {
    private String mDownloadPath;

    public String getDownloadPath() { return mDownloadPath; }
    public void setDownloadPath(String path) { this.mDownloadPath = path; }

    public abstract OtaCtrlServer getOtaCtrlServer();
    public abstract lOtaFileInfo getOtaFileInfo();
}
