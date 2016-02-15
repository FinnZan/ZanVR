package finnzan.zanvr;

import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import finnzan.util.CommonTools;

/**
 * Created by finnb on 2/15/2016.
 */
public class Observer {
    public static float[] mEyeVect = {0,0,-1};
    private static float[] mPosition = {0, 0, 0};

    private static float[] mHeadRotation = {0, 0, 0};
    private static float[] mBodyRotation = {0, 0, 0};

    private static float[] mMovement = {0, 0, 0};
    private static float mSpin = 0;

    private static float mEyeHeight = 100;
    private static float mStepSize = 10;

    public void takeTimeEvent(){
        mPosition[1] += mMovement[1] * Global.SCENE_SCALE;;

        // Gravity pull
        if(mPosition[1] <= mEyeHeight) {
            mPosition[1] = mEyeHeight;
        }else{
            mPosition[1] -= 1 * Global.SCENE_SCALE;;
        }
        mMovement[1] /=2;

        mPosition[0] += -mMovement[2] * mEyeVect[0] * Global.SCENE_SCALE * mStepSize;
        mPosition[2] += -mMovement[2] * mEyeVect[2] * Global.SCENE_SCALE * mStepSize;

        mBodyRotation[1] += mSpin/8;
    }

    public void takeGyro(float[] rotation) {

        mHeadRotation[1] = rotation[0];
        mHeadRotation[0] = rotation[2];
    }

    public void takeKeyEvent(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BUTTON_X) {
            mMovement[1] = 20;
        }
    }

    public void takeMotionEvent(MotionEvent event){

        mMovement[2] = event.getAxisValue(MotionEvent.AXIS_Y);
        if (Math.abs(mMovement[2]) < 0.01) {
            mMovement[2] = 0;
        }

        mSpin = event.getAxisValue(MotionEvent.AXIS_X)/5;
        if (Math.abs(mSpin) < 0.01) {
            mSpin = 0;
        }
    }

    public float[] getEyeVect(){
        mEyeVect[0] = (float)Math.sin(mHeadRotation[1] + mBodyRotation[1]);
        mEyeVect[1] = (float)Math.sin(mHeadRotation[0] - Math.PI/2);
        mEyeVect[2] = (float)-Math.cos(mHeadRotation[1] + mBodyRotation[1]);

        return mEyeVect;
    }

    public float[] getPosition(){
        return mPosition;
    }
}