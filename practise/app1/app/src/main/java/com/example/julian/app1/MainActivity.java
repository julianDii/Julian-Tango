package com.example.julian.app1;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tango.ux.TangoUxLayout;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;

import java.util.ArrayList;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The Tango Service component.
     */
    private Tango mTango;

    /**
     * The Tango Configuration component.
     */
    private TangoConfig mConfig;

    /**
     * The Tango UX component.
     */
    public TangoUx mTangoUx;

    /**
     * Teh UX Layout component.
     */
    private TangoUxLayout mTangoUxLayout;

    /**
     * The params
     */
    private TangoUx.StartParams params;

    public static final TangoCoordinateFramePair SOS_T_DEVICE_FRAME_PAIR =
            new TangoCoordinateFramePair(
                    TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                    TangoPoseData.COORDINATE_FRAME_DEVICE);
    public static final TangoCoordinateFramePair DEVICE_T_PREVIOUS_FRAME_PAIR =
            new TangoCoordinateFramePair(
                    TangoPoseData.COORDINATE_FRAME_PREVIOUS_DEVICE_POSE,
                    TangoPoseData.COORDINATE_FRAME_DEVICE);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Instantiate Tango service
        mTango = new Tango(this);
       // instantiate Tango UX Framework
        mTangoUx = new TangoUx(this);

        ArrayList<TangoCoordinateFramePair> framePairs = new ArrayList<>();
        framePairs.add(SOS_T_DEVICE_FRAME_PAIR);
        framePairs.add(DEVICE_T_PREVIOUS_FRAME_PAIR);
        mTango.connectListener(framePairs,listener);

        setContentView(R.layout.activity_main);

        // set up config
        tangoConfig();

        // set up Tango UX
        tangoUXLayout();







        mTango.connect(mConfig);



    }

    /**
     * This method sets up the Tango UX Framework.
     */
    private void tangoUXLayout() {



        mTangoUxLayout = (TangoUxLayout) findViewById(R.id.layout_tango);

        mTangoUx.setLayout(mTangoUxLayout);

        params = new TangoUx.StartParams();
        params.showConnectionScreen = true;

        mTangoUx.start(params);


        // defines how the use have to hold the device
        mTangoUx.setHoldPosture(TangoUx.TYPE_HOLD_POSTURE_FORWARD);

    }

    /**
     * This method creates a new tango configuration and enable the MotionTracking API
     */
    private void tangoConfig() {

        mConfig = new TangoConfig();
        mConfig = mTango.getConfig(TangoConfig.CONFIG_TYPE_CURRENT);
        mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_MOTIONTRACKING, true);
        mConfig.putBoolean(TangoConfig.KEY_BOOLEAN_DEPTH,true);

    }

    Tango.OnTangoUpdateListener listener = new
            Tango.OnTangoUpdateListener() {


                /**
                 *
                 * This callback is triggered every time a new pose estimate for the Tango device
                 * is available from the service for each coordinate frame pair that has been
                 * registered with a call to connectListener.
                 *
                 * @param pose
                 */
                @Override

                public void onPoseAvailable(final TangoPoseData pose) {
                    if (mTangoUx != null) {
                        mTangoUx.updatePoseStatus(pose.statusCode);
                    }
                }

                /**
                 * This callback is triggered every time a new point cloud is available from the
                 * depth sensor in the Tango service.
                 * @param xyzIj
                 */

                @Override
                public void onXyzIjAvailable(TangoXyzIjData xyzIj) {
                    if (mTangoUx != null) {
                        mTangoUx.updateXyzCount(xyzIj.xyzCount);
                    }
                }

                /**
                 * This callback is triggered every time an event occurs in the Tango system.
                 * @param event
                 */
                @Override
                public void onTangoEvent(TangoEvent event) {
                    if (mTangoUx != null) {
                        mTangoUx.updateTangoEvent(event);
                    }
                }

                /**
                 * This callback is triggered every time a new image is available from the RGB or
                 * Fisheye cameras in the Tango service.
                 * @param cameraId
                 */
                @Override
                public void onFrameAvailable(int cameraId) {
                    System.out.println("hello world");
                }
            };


}
