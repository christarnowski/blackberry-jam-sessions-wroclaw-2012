package pl.itraff.camera;

import java.io.IOException;

import pl.itraff.camera.camera.CameraController;
import pl.itraff.camera.camera.CameraZoomChangedListener;
import pl.itraff.camera.camera.PhotoFrameView;
import pl.itraff.camera.utils.CameraConstants;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.tooploox.blackberryjam.R;

/**
 * activity to take photo
 * 
 * @author qba
 * 
 */
@SuppressWarnings("deprecation")
public class TakePhoto extends Activity {

    private final String TAG = "CAMERA";

    // private GoogleAnalyticsTracker tracker;

    public static final String activityId = "TakePhoto";

    /**
     * camera controller object
     */
    private CameraController cameraController;
    /**
     * surface view holder
     */
    private SurfaceHolder holder;
    /**
     * surface view on which we display preview
     */
    private SurfaceView previewView;
    /**
     * tells if photo is being taken right now
     */
    private boolean takingPhoto = false;

    /**
     * view that draws rectangular frame on preview
     */
    private PhotoFrameView frameView;

    private int categoryType;

    /**
     * zoom scroll bar
     */
    private SeekBar zoomer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);
        categoryType = getIntent().getIntExtra(CameraConstants.CATEGORY_TYPE, 0);
        frameView = (PhotoFrameView) findViewById(R.id.bookCoverFrame);
        try {
            initCamera(categoryType);
        } catch (IOException e) {
            Log.e("onCreate()", "error in initCamera()");
        }
        // if (CameraConstants.ANALYTICS_ON) {
        // tracker = GoogleAnalyticsTracker.getInstance();
        // tracker.startNewSession(CameraConstants.ANALYTICS_API_KEY, 30, this);
        // }

    }

    /**
     * initialize camera
     * 
     * @throws IOException
     */
    private void initCamera(int categoryType) throws IOException {
        CameraController.init(TakePhoto.this, categoryType);
        cameraController = CameraController.getController();
        cameraController.setTaskId(categoryType);
        findViewById(R.id.cameraLayout).setOnKeyListener(mKeyListener);
        findViewById(R.id.shootPhoto).setOnClickListener(mShootPhotoListener);

        previewView = (SurfaceView) findViewById(R.id.preview);
        holder = previewView.getHolder();
        holder.addCallback(holderCallback);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        takingPhoto = false;
        cameraController.setContext(this);
        cameraController.startPreview();
        // cameraController.setCallback();
        // if (CameraConstants.ANALYTICS_ON) {
        // tracker.trackPageView("/" + this.getLocalClassName());
        // }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraController.stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stopping the tracker
        // if (CameraConstants.ANALYTICS_ON) {
        // tracker.stopSession();
        // }
    }

    /**
     * callback executed when surface view is changed
     */
    private SurfaceHolder.Callback holderCallback = new SurfaceHolder.Callback() {

        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            cameraController.stopPreview();
            cameraController.releaseCamera();
        }

        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                CameraController.getController().cameraOpen(surfaceHolder);

                int maxZoom = cameraController.getMaxZoom();
                zoomer = (SeekBar) findViewById(R.id.zoomSlider);
                if (cameraController.isZoomSupported() && maxZoom > 1) {
                    zoomer.setMax(maxZoom);
                    zoomer.setOnSeekBarChangeListener(sbListener);
                } else {
                    zoomer.setVisibility(View.INVISIBLE);
                }

                cameraController.setZoomChangeListener(pinchZoomListener);

                CameraController.getController().startPreview();
            } catch (Exception e) {

                Log.e("Error in surfaceCreated:", "camera controller problem");

            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            cameraController.startPreview();
        }
    };

    /**
     * callback executed when picture button is taken using camera key
     */
    private View.OnKeyListener mKeyListener = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_CAMERA) {
                if (event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                    CameraController.getController().autoFocus();
                }
                return true;
            }
            return false;
        }
    };

    /**
     * callback executed when zoom value is changed using scroll bar
     */
    private OnSeekBarChangeListener sbListener = new OnSeekBarChangeListener() {

        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                CameraController.getController().zoom(progress);
            }
        }
    };

    /**
     * callback executed when zoom value is changed using user gesture
     */
    private CameraZoomChangedListener pinchZoomListener = new CameraZoomChangedListener() {

        public void zoomChanged(int value) {
            zoomer.setProgress(value);
        }

    };

    private OnClickListener mShootPhotoListener = new OnClickListener() {
        public void onClick(View v) {
            if (!takingPhoto) {
                // if (CameraConstants.ANALYTICS_ON) {
                // tracker.trackEvent("clicks", "picture taken",
                // "using camera key", 0);
                // }
                ((Button) v).setEnabled(false);
                takingPhoto = true;
                CameraController.isTakingPhoto = true;
                cameraController.autoFocus();
            }
        }
    };

    public void redrawFrame(int color, long delayInMilis) {
        frameView.redraw(color, delayInMilis);
    }

    public void finishFromCamera(Intent intent) {
        if (intent != null) {
            Log.v(TAG, "data != null");
            Bundle bundle = intent.getBundleExtra("data");
            if (bundle != null) {
                Log.v(TAG, "bundle != null");

                String napis = "AA";
                napis = bundle.getString("pictureName");
                // Log.v(TAG, napis);

                byte[] pictureData = bundle.getByteArray("pictureData");
                Bitmap image = null;
                if (pictureData != null) {
                    Log.v(TAG, "pictureData != null");
                    try {
                        image = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (image != null) {
                        Log.v(TAG, "image != null");

                    }
                } else {
                    Log.v(TAG, "picture data == null");
                }
                // ThumbObject thumb = bundle.getSerializable("thumbnailData");
            }
        }

        setResult(Activity.RESULT_OK, intent);
        Log.v("CAMERA", "result take photo ok finish");
        finish();

    }
}
