package altacool6.ota.variety.maus;

import altacool6.ota.core.Request;
import altacool6.ota.core.Server;
import altacool6.ota.core.lCallback;
import altacool6.ota.core.lContentsInfo;

public class MausRequest extends Request implements lContentsInfo {
    private final String id;
    private final String value;
    

    public MausRequest(String path, String id, String value,
                       boolean needConfirm, 
                       Server server, lCallback callback){
        super(path, needConfirm, server, callback);

        this.id    = id;
        this.value = value;
        setContentsInfo(this);
    }

    public final String getId(){
        return id;
    }

    public final String getValue(){
        return value;
    }

}
