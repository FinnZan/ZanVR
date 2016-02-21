package finnzan.zanvr;

import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import finnzan.util.CommonTools;

/**
 * Created by finnb on 2/15/2016.
 */
public class Observer {
    public float[] mEyeVect = {0,0,-1};
    public float[] mFrontVect = {0,0,-1};
    private float[] mPosition = {0, 0, 1000};

    private float[] mHeadRotation = {0, 0, 0};
    private float[] mBodyRotation = {0, 0, 0};

    private float mHeadYZero = 0;

    private float[] mMovement = {0, 0, 0};
    private float mSpin = 0;

    private float mEyeHeight = 100;
    private float mStepSize = 10;

    public boolean IsWalkMode = false;

    public void takeTimeEvent(){
        mPosition[1] += mMovement[1];

        // Gravity pull
        if(mPosition[1] <= mEyeHeight) {
            mPosition[1] = mEyeHeight;
        }else{
            mPosition[1] -= 1;
        }
        mMovement[1] /=2;

        mBodyRotation[1] += mSpin/8;
        updateFrontVect();
        updateEyeVect();

        if(IsWalkMode) {
            mPosition[0] += -mMovement[2] * mEyeVect[0] * mStepSize;
            mPosition[2] += -mMovement[2] * mEyeVect[2] * mStepSize;
        }else{
            mPosition[0] += -mMovement[2] * mFrontVect[0] * mStepSize;
            mPosition[2] += -mMovement[2] * mFrontVect[2] * mStepSize;
        }
    }

    public void takeGyro(float[] rotation) {

        mHeadRotation[1] = rotation[0];
        mHeadRotation[0] = rotation[2];

        updateEyeVect();
    }

    public void takeKeyEvent(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_BUTTON_X) {
            mMovement[1] = 20;
        }

        //re-center
        if (keyCode == KeyEvent.KEYCODE_BUTTON_A) {
            mHeadYZero = mHeadRotation[1];
        }

        if (keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
            IsWalkMode = !IsWalkMode;
            if(IsWalkMode){
                mEyeHeight = 150;
            }else{
                mEyeHeight = 100;
            }
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

    private void updateFrontVect(){
        mFrontVect[0] = (float)Math.sin(mBodyRotation[1]);
        mFrontVect[1] = (float)Math.sin(0);
        mFrontVect[2] = (float)-Math.cos(mBodyRotation[1]);
    }

    private void updateEyeVect(){
        mEyeVect[0] = (float)Math.sin(mHeadRotation[1] - mHeadYZero + mBodyRotation[1]);
        mEyeVect[1] = (float)Math.sin(mHeadRotation[0] - Math.PI/2);
        mEyeVect[2] = (float)-Math.cos(mHeadRotation[1] - mHeadYZero + mBodyRotation[1]);
    }

    public float[] getFrontVect(){
        return mFrontVect;
    }

    public float[] getEyeVect(){
        return mEyeVect;
    }

    public float[] getPosition(){
        return mPosition;
    }

    public float[] getBodyRotation(){
        return mBodyRotation;
    }
}
