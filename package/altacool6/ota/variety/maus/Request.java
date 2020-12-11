package altacool6.ota.variety.maus;

import altacool6.ota.core.OtaRequest;
import altacool6.ota.core.OtaCtrlServer;

public class Request extends OtaRequest implements OtaRequest.lFileInfo {
    private String mId;
    private String mValue;
    

    public Request(String path, String id, String value, boolean needConfirm, 
                   CtrlServer server, OtaRequest.lCallback callback){
        setDownloadPath(path);
        setNeedUserConfirmForDownload(needConfirm);
        setCallback(callback);
        setOtaCtrlServer(server);
        mId = id;
        mValue = value;
    }


    public lFileInfo getFileInfo(){
        return this;
    }

    public String getId(){
        return "차종"+"향지"+"플렛폼"+getDownloadPath();
    }

    public String getValue(){
        return mValue;
    }

}
