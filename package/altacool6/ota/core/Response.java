package altacool6.ota.core;

public class Response {
    public static final int CONNECTION_SUCCESS      = 0;
    public static final int CONNECTION_FAILURE      = 1;
    public static final int CONTENTS_IS_WRONG       = 2;
    public static final int CONTENTS_IS_LATEST      = 3;
    public static final int CONTENTS_NEED_DOWNLOAD  = 4;
    public static final int CONTENT_DOWNLOAD_START  = 5;
    public static final int CONTENT_DOWNLOADING     = 6;
    public static final int CONTENT_DOWNLOADED      = 7;
    public static final int DISCONNECTED            = 8;


    private final int         result;
    private final Request     sourceRequest;
    private final Request     redirectRequest;

    private int         fileSize;
    private int         fileName;
    private String      comment;
 
    public Response(int ret, Request sourceRequest, Request redirectRequest){
        this.result          = ret;
        this.sourceRequest   = sourceRequest;
        this.redirectRequest = redirectRequest;
    }

    protected final int     getResult()          { return result;         }
    protected final Request getSourceRequest()   { return sourceRequest;  }
    protected final Request getRedirectRequest() { return redirectRequest;}
}