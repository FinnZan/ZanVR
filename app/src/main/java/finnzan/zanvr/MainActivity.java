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
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import finnzan.zanvr.util.CommonTools;

public class MainActivity extends Activity {
    private TextView tvOut;
    private GLSurfaceView mRightEyeView;
    private GLSurfaceView mLeftEyeView;

    private SceneRenderer mRenderR;
    private SceneRenderer mRenderL;

    private float mEyeSpacing = 10;

    private Timer mTimer = new Timer();

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOut = (TextView)this.findViewById(R.id.tvOut);

        mRenderR = new SceneRenderer(this, mEyeSpacing);
        mRightEyeView = (GLSurfaceView)this.findViewById(R.id.mRightEyeView);
        //glsScene.setEGLContextClientVersion(2);
        mRightEyeView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mRightEyeView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mRightEyeView.setRenderer(this.mRenderR);

        mRenderL = new SceneRenderer(this, -mEyeSpacing);
        mLeftEyeView = (GLSurfaceView)this.findViewById(R.id.mLeftEyeView);
        //glsScene.setEGLContextClientVersion(2);
        mLeftEyeView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mLeftEyeView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mLeftEyeView.setRenderer(this.mRenderL);

        timerHandler.postDelayed(timerRunnable, 0);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(mSensorListener, mSensor, 100);
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] mRotationMatrix = new float[16];

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                String str = "";
                for(float v : event.values){
                    str += v + ",";
                }
                SensorManager.getRotationMatrixFromVector(mRotationMatrix, event.values);

                float[] actual_orientation = new float[3];
                SensorManager.getOrientation(mRotationMatrix, actual_orientation);

                tvOut.setText(actual_orientation[0] + ", " + actual_orientation[1] + ", " + actual_orientation[2]);
                //Global.ROTATE_Y = (float)(actual_orientation[0]/Math.PI * 180);
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
        private float mAngle = 0;

        @Override
        public void run() {
            mAngle += 0.0025;

            Global.TRANSLATE_Y = 20;

            Global.TRANSLATE_X = 200 * (float)Math.sin(mAngle);
            Global.TRANSLATE_Z = 200 * (float)Math.cos(mAngle);

            timerHandler.postDelayed(this, 16);
        }
    };


    public void onStart() {
        super.onStart();
        CommonTools.Log("onStart");
        hideSystemUI();

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
