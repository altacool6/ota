import altacool6.ota.core.OtaClient;
import altacool6.ota.core.OtaRequest;
import altacool6.ota.variety.maus.Request;

public class test{
    public static void main(String args[]){
        System.out.println("Hello world!");

        OtaClient a = new OtaClient(10);

        a.start();

        OtaRequest request = new Request("/data/data/fw/bin", "12.1.3");

        a.addRequest(request);
    }
}
