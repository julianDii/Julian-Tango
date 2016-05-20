package com.example.juliandobrot.artest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.google.atap.tango.ux.TangoUx;
import com.google.atap.tango.ux.TangoUxLayout;
import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoCameraIntrinsics;
import com.google.atap.tangoservice.TangoCameraPreview;
import com.google.atap.tangoservice.TangoConfig;
import com.google.atap.tangoservice.TangoCoordinateFramePair;
import com.google.atap.tangoservice.TangoErrorException;
import com.google.atap.tangoservice.TangoEvent;
import com.google.atap.tangoservice.TangoOutOfDateException;
import com.google.atap.tangoservice.TangoPoseData;
import com.google.atap.tangoservice.TangoXyzIjData;
import org.rajawali3d.surface.RajawaliSurfaceView;
import java.util.ArrayList;

public class MainActivity extends Activity {


    private RoomerRender roomerRender;

    private static final ArrayList<TangoCoordinateFramePair> FRAME_PAIRS =
            new ArrayList<TangoCoordinateFramePair>();

    {
        FRAME_PAIRS.add(new TangoCoordinateFramePair(
                TangoPoseData.COORDINATE_FRAME_START_OF_SERVICE,
                TangoPoseData.COORDINATE_FRAME_DEVICE));
    }

    private static final String TAG = "JavaVideoOverlay";
    private TangoCameraPreview tangoCameraPreview;
    private Tango mTango;
    private TangoUx mTangoUx;
    private boolean mIsConnected = false;
    private TangoPoseData mpose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roomerRender = setupGLViewAndRenderer();

        tangoCameraPreview = new TangoCameraPreview(this);

       // setContentView(tangoCameraPreview);
        mTangoUx = setupUxAndLayout();
    }

    private TangoUx setupUxAndLayout() {

        TangoUxLayout uxLayout = (TangoUxLayout) findViewById(R.id.layout_tango);
        TangoUx tangoUx = new TangoUx(this);
        tangoUx.setLayout(uxLayout);
        return tangoUx;
    }

    // Camera Preview
    private void startCameraPreview() {
        // Connect to color camera
        tangoCameraPreview.connectToTangoCamera(mTango,
                TangoCameraIntrinsics.TANGO_CAMERA_COLOR);
        // Use default configuration for Tango Service.
        TangoConfig config = mTango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT);

        mTango.connect(config);
        mIsConnected = true;


        mTango.connectListener(FRAME_PAIRS, new Tango.OnTangoUpdateListener() {

            @Override
            public void onPoseAvailable(TangoPoseData pose) {

                if (mTangoUx != null) {
                    mTangoUx.updatePoseStatus(pose.statusCode);

                }

                // Update our copy of the latest pose
                // Synchronize against concurrent use in the render loop.
                synchronized (this) {
                    mpose = pose;
                }

            }

            @Override
            public void onXyzIjAvailable(TangoXyzIjData tangoXyzIjData) {

            }

            @Override
            public void onFrameAvailable(int i) {

                if (i == TangoCameraIntrinsics.TANGO_CAMERA_COLOR) {
                    tangoCameraPreview.onFrameAvailable();


                }

            }

            @Override
            public void onTangoEvent(TangoEvent tangoEvent) {


                if (mTangoUx != null) {
                    mTangoUx.updateTangoEvent(tangoEvent);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTangoUx.start(new TangoUx.StartParams());

        if (!mIsConnected) {
            // Initialize Tango Service as a normal Android Service, since we call
            // mTango.disconnect() in onPause, this will unbind Tango Service, so
            // everytime when onResume get called, we should create a new Tango object.
            mTango = new Tango(MainActivity.this, new Runnable() {

                // Pass in a Runnable to be called from UI thread when Tango is ready,
                // this Runnable will be running on a new thread.
                // When Tango is ready, we can call Tango functions safely here only
                // when there is no UI thread changes involved.


                @Override

                public void run() {
                    try {

                        startCameraPreview();

                    } catch (TangoOutOfDateException e) {
                        Log.e(TAG, getString(R.string.exception_tango_out_of_date), e);
                    } catch (TangoErrorException e) {
                        Log.e(TAG, getString(R.string.exception_tango_error), e);
                    }    }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mIsConnected) {
            mTangoUx.stop();
            mTango.disconnect();
            mIsConnected = false;
        }
    }

    private RoomerRender setupGLViewAndRenderer() {
        RoomerRender renderer = new RoomerRender(this);

        RajawaliSurfaceView glView = (RajawaliSurfaceView) findViewById(R.id.renderView);

        glView.setEGLContextClientVersion(2);
        glView.setSurfaceRenderer(renderer);
        return renderer;
    }

}
