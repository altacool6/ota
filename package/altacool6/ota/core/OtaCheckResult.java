package altacool6.ota.core;

public abstract class OtaCheckResult {
    public enum E_CHECK_RESULT { NEED_DOWNLOAD, NO_NEED_DOWNLOAD};

    private E_CHECK_RESULT result = E_CHECK_RESULT.NO_NEED_DOWNLOAD;

    public void setCheckResult(E_CHECK_RESULT result) {
        this.result = result;
    }

    public E_CHECK_RESULT getCheckResult(){
        return result;
    }

    public abstract OtaStorageServer getOtaStorageServer();
}
