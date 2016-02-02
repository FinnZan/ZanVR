package finnzan.zanvr.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by finnb on 9/10/2015.
 */
public class LogEvent
{
    public LogEvent(String source, long id, String event){
        Source = source;
        ThreadID= id;
        Event = event;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        Time= sdf.format(new Date());
    }
    public String Source;
    public long ThreadID;
    public String Event;
    public String Time;

    /*
    public CallStackItem[] CallStack
    {
        get;
        set;
    }*/
}