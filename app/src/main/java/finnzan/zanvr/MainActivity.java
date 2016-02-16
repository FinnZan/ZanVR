package finnzan.zanvr;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import finnzan.util.CommonTools;

public class MainActivity extends Activity {
    private TextView tvOut;
    private Button btMode;
    private GLSurfaceView mSurfaceView;
    private TextView tvInfoL;
    private TextView tvInfoR;

    private SceneRenderer mRender;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonTools.Log("onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOut = (TextView)this.findViewById(R.id.tvOut);

        btMode = (Button)this.findViewById(R.id.btMode);
        btMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.IS_VR_MODE = !Global.IS_VR_MODE;
            }
        });

        tvInfoL = (TextView)this.findViewById(R.id.tvInfoL);
        tvInfoR = (TextView)this.findViewById(R.id.tvInfoR);

        mRender = new SceneRenderer(this);
        mSurfaceView = (GLSurfaceView)this.findViewById(R.id.mSurfaceView);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceView.setRenderer(this.mRender);

        timerHandler.postDelayed(timerRunnable, 0);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        if(mSensor == null) {
            CommonTools.Log("TYPE_GAME_ROTATION_VECTOR not available.");
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }

        mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {

            }
        });

        SetScreenText("Ready");
    }

    public void onStart() {
        CommonTools.Log("onStart");
        super.onStart();
    }



    @Override
    public void onResume(){
        CommonTools.Log("onResume");
        super.onResume();
        hideSystemUI();
    }

    // time function
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Global.Observer.takeTimeEvent();
            float pos[] = Global.Observer.getPosition();
            float eye[] = Global.Observer.getEyeVect();

            tvOut.setText("Position [" + (int)pos[0] + "] [" + pos[1] + "] [" + pos[2] + "]\n" +
                          "Eye Vector [" + String.format("%.2f", eye[0]) + "] [" + String.format("%.2f", eye[1]) + "] [" + String.format("%.2f", eye[2]) + "]");

            timerHandler.postDelayed(this, 16);
        }
    };

    // region Input Handling ===============
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] mRotationMatrix = new float[16];

            if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR || event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);

                float[] actual_orientation = new float[3];
                SensorManager.getOrientation(mRotationMatrix, actual_orientation);
                Global.Observer.takeGyro(actual_orientation);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent e){
        CommonTools.Log("onTouchEvent");
        hideSystemUI();
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            CommonTools.Log("Key [" + keyCode + "]");
            if (event.getRepeatCount() == 0) {
                Global.Observer.takeKeyEvent(keyCode);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        try {
            if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
                //CommonTools.Log(event.getAction() + "");
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    final int historySize = event.getHistorySize();

                    for (int i = 0; i < historySize; i++) {
                        //CommonTools.Log(event.getHistoricalAxisValue(MotionEvent.AXIS_X, i) + ", " + event.getAxisValue(MotionEvent.AXIS_Y, i));
                    }

                    Global.Observer.takeMotionEvent(event);
                    return true;
                }
            }
        }catch (Exception ex){
            CommonTools.HandleException(ex);
        }
        return super.onGenericMotionEvent(event);
    }

    // endregion

    // region UI Runtines =================

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void SetScreenText(String text){
        tvInfoL.setText(text);
        tvInfoR.setText(text);
    }

    // endregion
}
