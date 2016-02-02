package finnzan.zanvr.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DellDebugView extends Activity {
    private Context mContext = this;
    private ListView lvEvents = null;
    private LayoutInflater mInflater = null;
    private boolean mIsActive = true;

    private LogEvent mEvents[];

    private int[] mThreadColors = {
            Color.rgb(200, 100, 200),
            Color.rgb(200, 200, 200),
            Color.rgb(200, 255, 255),
            Color.rgb(200, 100, 100),
            Color.rgb(255, 250, 200),
            Color.rgb(250, 100, 200),
            Color.rgb(100, 250, 200),
            Color.rgb(200, 100, 255),
            Color.rgb(255, 255, 200)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        ListView lvEvents = new ListView(mContext);
        relativeLayout.addView(lvEvents);
        setContentView(relativeLayout, lParams);

        lvEvents.setAdapter(mLogEventListAdapter);

        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread(new Runnable(){
            @Override
            public void run() {
                while(mIsActive){
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mEvents = CommonTools.getLogEvents().toArray(new LogEvent[CommonTools.getLogEvents().size()]);
                                    mLogEventListAdapter.notifyDataSetChanged();
                                }catch (Exception ex){

                                }
                            }
                        });
                        Thread.sleep(1000);
                    }catch (Exception ex){

                    }
                }
            }
        }).start();
    }

    @Override
    public void onStop() {
        mIsActive = false;
        super.onStop();
        CommonTools.Log("onStop");
    }

    private BaseAdapter mLogEventListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return CommonTools.getLogEvents().size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(mContext);
            }

            try {
                LogEvent event = mEvents[position];
                if(event.ThreadID == 1) {
                    ((TextView) convertView).setBackgroundColor(Color.WHITE);
                }else {
                    ((TextView) convertView).setBackgroundColor(mThreadColors[(int) event.ThreadID % mThreadColors.length]);
                }
                String str = String.format("%04d", event.ThreadID) + " " + event.Time + " [" + event.Source + "]     " + event.Event;
                ((TextView) convertView).setText(str);
            } catch (Exception ex) {

            }
            return convertView;
        }
    };
}
