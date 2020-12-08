package altacool6.ota.variety.maus;

import altacool6.ota.core.OtaCtrlServer;
import altacool6.ota.core.OtaStorageServer;
import altacool6.ota.core.lOtaFileInfo;

class CtrlServer extends OtaCtrlServer{
    private lOtaFileInfo mFileInfo;
    private OtaStorageServer mStorageServer; 

    public CtrlServer(lOtaFileInfo fileInfo){
        mFileInfo = fileInfo;
    }
    protected Result connect(){
        return OtaCtrlServer.Result.SUCCESS;
    }

    protected void disconnect(){
    }

    protected Result requestCheckingFile(){
        mStorageServer = new StorageServer();
        
        return OtaCtrlServer.Result.NO_NEED_DOWNLOAD;
    }

    protected OtaStorageServer getOtaStorageServer(){
        return mStorageServer;
    }
}
