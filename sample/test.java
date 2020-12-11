import altacool6.ota.core.OtaClient;
import altacool6.ota.core.OtaRequest;

import altacool6.ota.variety.maus.Request;
import altacool6.ota.variety.maus.CtrlServer;

public class test{
    public static void main(String args[]){
        System.out.println("Hello world!");

        OtaClient a = new OtaClient(10);

        a.start();

        boolean bNeedConfirm = true;
        OtaRequest request = new Request("PATH", "ID", "VALUE", bNeedConfirm, 
                                         new CtrlServer(),
                                         result->System.out.println("User response : "+ result));

        a.addRequest(request);
    }
}
