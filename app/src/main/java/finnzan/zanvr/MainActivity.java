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

        SetScreenText("Ready");
    }

    public void onStart() {
        super.onStart();
        CommonTools.Log("onStart");
        hideSystemUI();
    }

    public void SetScreenText(String text){
        tvInfoL.setText(text);
        tvInfoR.setText(text);
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] mRotationMatrix = new float[16];

            if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR || event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                String str = "";
                for (float v : event.values) {
                    str += v + ",";
                }
                SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);

                float[] actual_orientation = new float[3];
                SensorManager.getOrientation(mRotationMatrix, actual_orientation);

                tvOut.setText(Global.TRANSLATE_X + "\n"
                        + Global.TRANSLATE_Z + "\n"
                        + (int)(Global.ROTATE_Y/Math.PI * 180));

                Global.ROTATE_Y = actual_orientation[0];
                Global.ROTATE_X = actual_orientation[2];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            Global.TRANSLATE_Y += Global.UPWARD_MOVEMENT;

            // Gravity pull
            if(Global.TRANSLATE_Y <= Global.EYE_HEIGHT) {
                Global.TRANSLATE_Y = Global.EYE_HEIGHT;
            }else{
                Global.TRANSLATE_Y -= 2;
            }
            Global.UPWARD_MOVEMENT /=2;

            Global.TRANSLATE_X += -(Global.FORWARD_MOVEMENT * (float)Math.sin(Global.ROTATE_Y));
            Global.TRANSLATE_Z += -(-Global.FORWARD_MOVEMENT * (float)Math.cos(Global.ROTATE_Y));

            Global.TRANSLATE_X += (Global.SIDEWAY_MOVEMENT * (float)Math.sin(Global.ROTATE_Y + 90));
            Global.TRANSLATE_Z += (-Global.SIDEWAY_MOVEMENT * (float)Math.cos(Global.ROTATE_Y + 90));

            timerHandler.postDelayed(this, 16);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            CommonTools.Log("Key [" + keyCode + "]");
            if (event.getRepeatCount() == 0) {
                if (keyCode == KeyEvent.KEYCODE_BUTTON_X) {
                    Global.UPWARD_MOVEMENT = 20;
                }
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

                    //CommonTools.Log(event.getAxisValue(MotionEvent.AXIS_X) + ", " + event.getAxisValue(MotionEvent.AXIS_Y));
                    Global.FORWARD_MOVEMENT = event.getAxisValue(MotionEvent.AXIS_Y);
                    if (Math.abs(Global.FORWARD_MOVEMENT) < 0.01) {
                        Global.FORWARD_MOVEMENT = 0;
                    }

                    Global.SIDEWAY_MOVEMENT = event.getAxisValue(MotionEvent.AXIS_X);
                    if (Math.abs(Global.SIDEWAY_MOVEMENT) < 0.01) {
                        Global.SIDEWAY_MOVEMENT = 0;
                    }
                    return true;
                }
            }
        }catch (Exception ex){
            CommonTools.HandleException(ex);
        }
        return super.onGenericMotionEvent(event);
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.

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
}
