package altacool6.ota.core;

public class Log{
    public interface ILogout {
        void printf(String msg);
    }; 

    private static ILogout mExternalOutway = null;
    
    public static final int ERROR = 0;
    public static final int WARN  = 1;
    public static final int INFO  = 2;
    public static final int DEBUG = 3;
    
    private static int LOG_LEVEL = INFO;

    public static final int FLAG_CLIENT         = 0x01;
    public static final int FLAG_CTRL_SERVER    = 0x02;
    public static final int FLAG_STORAGE_SERVER = 0x04;

    private static int MASK = 0xFFFFFFFF;

    static{
        String level = System.getProperty("ota.log.level");
        if (level == null){
            LOG_LEVEL = INFO;
        }
        else{
            if      (level.equalsIgnoreCase("ERROR")){ LOG_LEVEL = ERROR;}
            else if (level.equalsIgnoreCase("WARN")) { LOG_LEVEL = WARN; }
            else if (level.equalsIgnoreCase("INFO")) { LOG_LEVEL = INFO; }
            else if (level.equalsIgnoreCase("DEBUG")){ LOG_LEVEL = DEBUG;}
            else                                     { LOG_LEVEL = DEBUG;}
        } 
    }
    public static void setExternalOutway(ILogout way){
        mExternalOutway = way;
    }

    public static int SetMask(int flags){
        MASK = flags;
        return MASK;
    }

    private static void _print(int flag, String msg){
        if ((MASK & flag) != 0 ){
            if (mExternalOutway == null)
                System.out.println(msg);
            else 
                mExternalOutway.printf(msg);
        }
    }

    public static void E(int flag, String msg){
        if (ERROR <= LOG_LEVEL)
            _print(flag, msg);
    }
    public static void W(int flag, String msg){
        if (WARN <= LOG_LEVEL)
            _print(flag, msg);
    }
    public static void I(int flag, String msg){
        if (INFO <= LOG_LEVEL)
            _print(flag, msg);
    }
    public static void D(int flag, String msg){
        if (DEBUG <= LOG_LEVEL)
            _print(flag, msg);
    }
}