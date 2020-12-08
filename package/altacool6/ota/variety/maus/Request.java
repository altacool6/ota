package altacool6.ota.variety.maus;

import altacool6.ota.core.OtaRequest;
import altacool6.ota.core.OtaCtrlServer;
import altacool6.ota.core.lOtaFileInfo;

public class Request extends OtaRequest implements lOtaFileInfo {
    private String mValue;
    private OtaCtrlServer mCtrlServer; 

    public Request(String path, String value){
        setDownloadPath(path);
        mValue = value;

        mCtrlServer = new CtrlServer(this);
    }


    public OtaCtrlServer getOtaCtrlServer(){
        return mCtrlServer;
    }

    public lOtaFileInfo getOtaFileInfo(){
        return this;
    }

    public String getId(){
        return "차종"+"향지"+"플렛폼"+getDownloadPath();
    }

    public String getValue(){
        return mValue;
    }

}
