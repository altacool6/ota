import altacool6.ota.core.Agent;
import altacool6.ota.core.Request;

import altacool6.ota.variety.maus.MausRequest;
import altacool6.ota.variety.maus.MausCtrlServer;

public class test{
    public static void main(String args[]){
        System.out.println("Hello world!");

        Agent a = new Agent(10);

        a.start();

        boolean bNeedConfirm = true;
        Request request = new MausRequest("PATH", "ID", "VALUE", bNeedConfirm, 
                                         new MausCtrlServer(),
                                          (result,tag)->System.out.println("User response : "+ result));

        a.addRequest(request);
    }
}
