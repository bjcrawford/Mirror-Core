/*/ Mirror Core
 *  wckd Development 
 *  Brett Crawford 
 */

package com.wckd_dev.mirror.core;

import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MirrorActivity extends Activity implements OnTouchListener {

	private final String TAG = "wckd";
	
	/* Intent variables */
	
	protected final static String MIRROR_MESSAGE = "com.wckd_dev.mirror.core.MESSAGE";
	private                String intentMessage;
	private                String store;
	protected              String version;
	protected              String dialogAppInfoText;
	
	/* Constants for Calling Dialogs */
	
    protected static final int WELCOME_DIALOG         =  1;
    protected static final int SNAPSHOT_DIALOG        =  2;
    protected static final int SNAPSHOT_SIZE          =  3;
    protected static final int PHOTOSTRIP_DIALOG      =  4;
    protected static final int FRAME_STYLE_DIALOG     =  5;
    protected static final int ZOOM_DIALOG            =  6;
    protected static final int EXPOSURE_DIALOG        =  7;
    protected static final int THEME_DIALOG           =  8;
    protected static final int APP_INFO_DIALOG        =  9;
    protected static final int HELP_DIALOG            = 10;
    protected static final int FRONT_CAMERA_NOT_FOUND = 11;
    protected static final int CAMERA_NOT_FOUND       = 12;
    protected static final int UPGRADE                = 13;
    protected static final int RATE                   = 14;
    protected static final int PAUSE_DIALOG           = 15;
    protected static final int WELCOME_ONE_DIALOG     = 16;
    protected static final int WELCOME_TWO_DIALOG     = 17;
    
    
    /** Change between 1 and 2 to display one time messages to users after upgrades */
    private final int INFO_DIALOGS = 1; // v2.5 set to 1 for release
	
    /* Preferences Strings*/
    
	protected static final String APP_PREFERENCES                    = "AppPrefs";
	protected static final String APP_PREFERENCES_ORIENTATION        = "Orientation";
	protected static final String APP_PREFERENCES_REVERSE            = "Reverse";
	protected static final String APP_PREFERENCES_FLIP               = "Flip";
    protected static final String APP_PREFERENCES_FRAME              = "Frame";
    protected static final String APP_PREFERENCES_FRAME_CHANGED      = "FrameChanged";
    protected static final String APP_PREFERENCES_INITIAL_LOAD_ONE   = "InitialLoadOne";
    protected static final String APP_PREFERENCES_INITIAL_LOAD_TWO   = "InitialLoadTwo";
    protected static final String APP_PREFERENCES_INITIAL_PAUSE      = "InitialPause";
    protected static final String APP_PREFERENCES_INITIAL_SNAPSHOT   = "InitialSnapshot";
    protected static final String APP_PREFERENCES_INITIAL_PHOTOBOOTH = "InitialPhotoBooth";
    protected static final String APP_PREFERENCES_THEME              = "Theme";
    protected static final String APP_PREFERENCES_EXPOSURE           = "Exposure";
    protected static final String APP_PREFERENCES_WHITE_BALANCE      = "WhiteBalance";
    protected static final String APP_PREFERENCES_ZOOM               = "Zoom";
    protected static final String APP_PREFERENCES_HAS_RATED          = "HasRated";
    protected static final String APP_PREFERENCES_USE_COUNT          = "UseCount";
    protected static final String APP_PREFERENCES_SNAPSHOT_SIZE      = "SnapshotSize";
    protected static final String APP_PREFERENCES_BRIGHTNESS 	     = "Brightness";
    protected static final String APP_PREFERENCES_BRIGHTNESS_MODE    = "BrightnessMode";
    protected static final String APP_PREFERENCES_BRIGHTNESS_LEVEL 	 = "BrightnessLevel";
    
    
    
    /* Preferences */
    
    private   int    reversePref;
    private   int    flipPref;
	private   int    orientationPref;
    private   int    frameModePref;
	private   int    framePacksPref;
 	private   int    initialLoadOnePref;
 	private   int    initialLoadTwoPref;
	private   int    initialPausePref;
	private   int    initialSnapshotPref;
	private   int    initialPhotoBoothPref;
	protected int    themePref;
	private   String themeColor;
	private   int    exposurePref;
	private   int    whiteBalancePref;
    private   float  zoomPrefF;
	protected int    hasRatedPref;
	private   int    useCountPref;
	private   int    snapshotSizePref;
	private   int    brightnessPref;
	private   int    brightnessModePref;
	private   int    brightnessLevelPref;
	
	/* Camera */
	
    private int numberOfCameras;
    private int currentCameraId;
    private int backCameraId;
    private int frontCameraId;
    private int photoBoothCount = 0; // Paid Only
	
	/* Flags */

	private   boolean   isPaused                  = false;
	private   boolean   isPreviewStopped          = false;
	private   boolean   isBackCameraFound         = false;
    private   boolean   isFrontCameraFound        = false;
    private   boolean   isUsingFrontCamera        = false;
    protected boolean   isZoomSupported           = false;
    protected boolean   isExposureSupported       = false;
    private   boolean   isWhiteBalanceSupported   = false;
    private   boolean   isFullscreen              = true;
    private   boolean   isMirrorMode              = true;
    private   boolean   isPortrait;
    private   boolean   isPauseButtonVisible      = false;
    private   boolean   isSnapshotButtonVisible   = false;
    private   boolean   isPhotoBoothButtonVisible = false;
    private   boolean   isBrightness              = false;
    
    /* Objects */
    
    private   Camera                camera = null;
    private   MirrorView            mirrorView;
    protected SharedPreferences     appSettings;
    protected Dialog                dialog;
    private   MenuItem              menuItemWhiteBalance;
    private   MenuItem              menuItemMirrorModeOn;
    private   MenuItem              menuItemMirrorModeOff;
    private   Animation             slideUp, slideDown;
    private   Animation             rightSlideIn, rightSlideOut;
    private   Animation             leftSlideIn, leftSlideOut;
    private   ImageButton           pause, takeSnapshot, takePhotoBooth;
    protected FrameManager          frameMgr;
    private   Snapshot              snapshot;   // Paid Only
    protected Snapshot.ImageSize    imageSize = Snapshot.ImageSize.LARGE;  // Paid Only
    private   PhotoBooth            booth;      // Paid Only
    
    /* Touch */
    
    private Matrix            matrix;
    private Matrix            savedMatrix;
    
    private static final int NONE = 0;
    private static final int PRESS = 1;
    private static final int DRAG = 2;
    private static final int ZOOM = 3;
    
    private int touchState = NONE;
    
    private PointF start     = new PointF();
    private PointF mid       = new PointF();
    private float  startDist = 1f;
    private float  lastDist  = 0f;
    private float  minDist   = 10f;
    private float  zoomRate  = 0f;
    
    private long      pressTime;
    private long      lastPressTime = 0;
    private boolean   hasDoubleTapped = false;
    
    private static final long DOUBLE_TAP_LENGTH = 250;
    
    /* Brightness */
    
    private ContentResolver conRes;
    
    
    /* Store Links */
    
    protected static String rateLink;
    protected static String upgradePaidLink;
    protected static String frameLink;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initIntentInfo();
        initPrefs();
        initTheme();
        initWindowFeatures();
        initContentView();
        initMirrorView();
        initFrameManager();
        initCamera();
        initPauseButton();
        initSnapshotButton();
        initPhotoBoothButton();
        initOnTouchListener();
        initBrightnessSettings();
	    showWelcomeOneDialog();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCamera();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        unloadCamera();
        restoreBrightnessSettings(false);
        
        // Save preferences
        Editor editor = appSettings.edit();
        editor.putString(APP_PREFERENCES_REVERSE, Integer.toString(reversePref));
        editor.putString(APP_PREFERENCES_FLIP, Integer.toString(flipPref));
        editor.putString(APP_PREFERENCES_ORIENTATION, Integer.toString(orientationPref));
		editor.putString(APP_PREFERENCES_FRAME, Integer.toString(frameModePref));
		editor.putString(APP_PREFERENCES_FRAME_CHANGED, Integer.toString(framePacksPref));
		editor.putString(APP_PREFERENCES_EXPOSURE, Integer.toString(exposurePref));
		editor.putString(APP_PREFERENCES_WHITE_BALANCE, Integer.toString(whiteBalancePref));
		editor.putString(APP_PREFERENCES_ZOOM, Integer.toString(Math.round(zoomPrefF)));
		editor.putString(APP_PREFERENCES_HAS_RATED, Integer.toString(hasRatedPref));
		editor.putString(APP_PREFERENCES_USE_COUNT, Integer.toString(useCountPref));
		editor.putString(APP_PREFERENCES_SNAPSHOT_SIZE, Integer.toString(snapshotSizePref));
		editor.putString(APP_PREFERENCES_BRIGHTNESS, Integer.toString(brightnessPref));
		editor.putString(APP_PREFERENCES_BRIGHTNESS_MODE, Integer.toString(brightnessModePref));
		editor.putString(APP_PREFERENCES_BRIGHTNESS_LEVEL, Integer.toString(brightnessLevelPref));
		editor.commit();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    @Override
	public void onBackPressed() {
    	if(version.compareTo("paid") != 0 && hasRatedPref == 0 && useCountPref > 6) 
    		displayDialog(RATE);
    	else {
    		useCountPref++;
    		MirrorActivity.this.finish();
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	
    	if(reversePref == 0) {
    		mirrorView.mirrorMode(false);
    		isMirrorMode = false;
    	}
    	
    	if(flipPref == 0) {
    		(menu.findItem(R.id.menu_options_flip_mode_off)).setChecked(true);
    	}
    	else {
    		(menu.findItem(R.id.menu_options_flip_mode_on)).setChecked(true);
    		mirrorView.flipMode(true);
    	}
    	
    	if(Math.round(zoomPrefF) > 0)
        	setZoom(Math.round(zoomPrefF)); // Set the zoom preference
    	
    	if(brightnessPref == 0) {
    		(menu.findItem(R.id.menu_options_brightness_off)).setChecked(true);
    	}
    	else {
    		(menu.findItem(R.id.menu_options_brightness_on)).setChecked(true);
    	}
        
        if(exposurePref != -999) { // If exposure pref is valid
        	try {
        		setExposure(exposurePref); // Set the exposure preference
        	}
        	catch(RuntimeException e) {
        		// No action taken. Exposure settings will not be used.
        	}
        }

        menuItemWhiteBalance = menu.findItem(R.id.menu_options_white_balance);
        if(whiteBalancePref != 0)
        	setWhiteBalance(whiteBalancePref); // Set the white balance preference
        
        if(numberOfCameras > 1) {
        	(menu.findItem(R.id.menu_options_switch_camera)).setEnabled(true);
        	(menu.findItem(R.id.menu_options_switch_camera)).setVisible(true);
        }

    	if(isPortrait) {
	    	(menu.findItem(R.id.menu_options_screen_rotation_portrait)).setChecked(true);
        }
        else {
	    	(menu.findItem(R.id.menu_options_screen_rotation_landscape)).setChecked(true);
        }
    	

    	if(version.compareTo("free") != 0) {

        	if(snapshotSizePref == 2) {
    	    	(menu.findItem(R.id.menu_options_snapshot_size_small)).setChecked(true);
        	}
        	else if(snapshotSizePref == 1) {
    	    	(menu.findItem(R.id.menu_options_snapshot_size_medium)).setChecked(true);
        	}
        	else {
    	    	(menu.findItem(R.id.menu_options_snapshot_size_large)).setChecked(true);
        	}
    	}
    	else {

        	(menu.findItem(R.id.menu_options_snapshot_size)).setEnabled(false);
        	(menu.findItem(R.id.menu_options_snapshot_size)).setVisible(false);
    	}
    	
    	
    	
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	
    	menuItemWhiteBalance = menu.findItem(R.id.menu_options_white_balance);
    	menuItemMirrorModeOn = menu.findItem(R.id.menu_options_mirror_mode_on);
    	menuItemMirrorModeOff = menu.findItem(R.id.menu_options_mirror_mode_off);
    	//menuItemFlipModeOn = menu.findItem(R.id.menu_options_flip_mode_on);
    	//menuItemFlipModeOff = menu.findItem(R.id.menu_options_flip_mode_off);
    	
    	if(reversePref == 0) {
    		menuItemMirrorModeOff.setChecked(true);
    	}
    	//if(flipPref == 0) {
    	//	menuItemFlipModeOff.setChecked(true);
    	//}
    	
    	if(snapshotSizePref == 2) {
	    	(menu.findItem(R.id.menu_options_snapshot_size_small)).setChecked(true);
    	}
    	else if(snapshotSizePref == 1) {
	    	(menu.findItem(R.id.menu_options_snapshot_size_medium)).setChecked(true);
    	}
    	else {
	    	(menu.findItem(R.id.menu_options_snapshot_size_large)).setChecked(true);
    	}
    	
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	boolean result = false;
    	int id = item.getItemId();
        
        /* Snapshot */
	    if(id == R.id.menu_snapshot_snapshot) {
	        	if(isSnapshotButtonVisible) { // Hide snapshot button
	        		takeSnapshot.startAnimation(rightSlideOut);
	                takeSnapshot.setVisibility(View.INVISIBLE);
	        		isSnapshotButtonVisible = false;
	        	}
	        	else { // Show snapshot button
	        		if(isPhotoBoothButtonVisible) {
	        			takePhotoBooth.startAnimation(leftSlideOut);
		                takePhotoBooth.setVisibility(View.INVISIBLE);
		        		isPhotoBoothButtonVisible = false;
		        		booth = null; // Paid Only
		        		photoBoothCount = 0; // Paid Only
	        		}
	        		if(isPauseButtonVisible) {
		        		pause.startAnimation(slideDown);
		                pause.setVisibility(View.INVISIBLE);
		        		isPauseButtonVisible = false;
		        	}
	        		takeSnapshot.startAnimation(rightSlideIn);
	                takeSnapshot.setVisibility(View.VISIBLE);
	        		isSnapshotButtonVisible = true;
	        		showSnapshotDialog();
	        	}
	        	result = true;
	    }	
	    /* Photobooth */
	    else if(id == R.id.menu_snapshot_photobooth) {

	        	showPhotoBoothDialog();
	        	
	        	if(isPhotoBoothButtonVisible) {
	        		takePhotoBooth.startAnimation(leftSlideOut);
	                takePhotoBooth.setVisibility(View.INVISIBLE);
	        		isPhotoBoothButtonVisible = false;
	        		booth = null; // Paid Only
	        		photoBoothCount = 0; // Paid Only
	        	}
	        	else {
	        		if(isSnapshotButtonVisible) {
		        		takeSnapshot.startAnimation(rightSlideOut);
		                takeSnapshot.setVisibility(View.INVISIBLE);
		        		isSnapshotButtonVisible = false;
		        	}
	        		if(isPauseButtonVisible) {
		        		pause.startAnimation(slideDown);
		                pause.setVisibility(View.INVISIBLE);
		        		isPauseButtonVisible = false;
		        	}
	        		takePhotoBooth.startAnimation(leftSlideIn);
	                takePhotoBooth.setVisibility(View.VISIBLE);
	        		isPhotoBoothButtonVisible = true;
	        	}
	        	result = true;
	    }
        /* Pause */
	    else if(id == R.id.menu_pause) {
	        	
	        	showPauseDialog();
	        	
	        	if(isPauseButtonVisible) {
	        		pause.startAnimation(slideDown);
	                pause.setVisibility(View.INVISIBLE);
	        		isPauseButtonVisible = false;
	        	}
	        	else {
	        		if(isPhotoBoothButtonVisible) {
	        			takePhotoBooth.startAnimation(leftSlideOut);
		                takePhotoBooth.setVisibility(View.INVISIBLE);
		        		isPhotoBoothButtonVisible = false;
		        		booth = null; // Paid Only
		        		photoBoothCount = 0; // Paid Only
	        		}
	        		if(isSnapshotButtonVisible) {
	        			takeSnapshot.startAnimation(rightSlideOut);
	        			takeSnapshot.setVisibility(View.INVISIBLE);
		        		isSnapshotButtonVisible = false;
	        		}
	        		pause.startAnimation(slideUp);
	                pause.setVisibility(View.VISIBLE);
	        		isPauseButtonVisible = true;
	        	}
	        	result = true;
	        	
	    }
	    /* Frames */
	    else if(id == R.id.menu_frame) {
        	displayDialog(FRAME_STYLE_DIALOG);
        	result = true;
	    }
        /* Zoom */
        else if(id == R.id.menu_options_zoom) {
        	displayDialog(ZOOM_DIALOG);
            result = true;
        }
        /* Brightness On */
        else if(id == R.id.menu_options_brightness_on) {
        	if(!isBrightness) {
        		isBrightness = true;
        		brightnessPref = 1;
        		item.setChecked(true);
        		initBrightnessSettings();
        	}
        	result = true;
        }
        /* Brightness Off */
        else if(id == R.id.menu_options_brightness_off) {
        	if(isBrightness) {
        		isBrightness = false;
        		brightnessPref = 0;
        		item.setChecked(true);
        		restoreBrightnessSettings(true);
        	}
        	result = true;
        }
        /* Exposure */
        else if(id == R.id.menu_options_exposure) {
        	displayDialog(EXPOSURE_DIALOG);
            result = true;
        }
        /* White Balance Auto */
        else if(id == R.id.menu_options_white_balance_auto) {
        	if(isWhiteBalanceSupported)
        		setWhiteBalance(0);
        	else
        		Toast.makeText(getApplicationContext(), R.string.toast_no_white_balance, Toast.LENGTH_SHORT).show();
        	result = true;
        }
        /* White Balance Daylight */
        else if(id == R.id.menu_options_white_balance_daylight) { 
        	if(isWhiteBalanceSupported)
        		setWhiteBalance(1);
        	else
        		Toast.makeText(getApplicationContext(), R.string.toast_no_white_balance, Toast.LENGTH_SHORT).show();
        	result = true;
        }
        /* White Balance Incandescent */
        else if(id == R.id.menu_options_white_balance_incandescent) {
        	if(isWhiteBalanceSupported)
        		setWhiteBalance(2);
        	else
        		Toast.makeText(getApplicationContext(), R.string.toast_no_white_balance, Toast.LENGTH_SHORT).show();
        	result = true;
        }
        /* White Balance Fluorescent */
        else if(id == R.id.menu_options_white_balance_fluorescent) {
        	if(isWhiteBalanceSupported)
        		setWhiteBalance(3);
        	else
        		Toast.makeText(getApplicationContext(), R.string.toast_no_white_balance, Toast.LENGTH_SHORT).show();
        	result = true;
        }
        /* Mirror Mode On */
        else if(id == R.id.menu_options_mirror_mode_on) {
        	if(!isMirrorMode) {
        		mirrorView.mirrorMode(true);
        		isMirrorMode = true;
        		reversePref = 1;
        		item.setChecked(true);
        	}
        	result = true;
        }
        /* Mirror Mode Off */
        else if(id == R.id.menu_options_mirror_mode_off) {
        	if(isMirrorMode) {
        		mirrorView.mirrorMode(false);
        		isMirrorMode = false;
        		reversePref = 0;
        		item.setChecked(true);
        	}
        	result = true;
        }
	    /* Flip Mode On */
        else if(id == R.id.menu_options_flip_mode_on) {
        	flipPref = 1;
        	item.setChecked(true);
            mirrorView.flipMode(true);
        }
	    /* Flip Mode Off */
        else if(id == R.id.menu_options_flip_mode_off) {
        	flipPref = 0;
        	item.setChecked(true);
            mirrorView.flipMode(false);
        }
        /* Switch Camera */
        else if(id == R. id.menu_options_switch_camera) {
    		unloadCamera();
        	if(isUsingFrontCamera) {
        		currentCameraId = backCameraId;
        		isUsingFrontCamera = false;
        	}
        	else {
        		currentCameraId = frontCameraId;
        		isUsingFrontCamera = true;
        	}
        	loadCamera();
        	result = true;
        }	
        /* Screen Rotation Portrait */
        else if(id == R.id.menu_options_screen_rotation_portrait) {
        	if(!isPortrait) {
    			isMirrorMode = true;
    			reversePref = 1;
                mirrorView.mirrorMode(true);
    			menuItemMirrorModeOn.setChecked(true);
    			
    			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	        	mirrorView.isPortrait = isPortrait = true;
                camera.setDisplayOrientation(90);
                setFrame(0);
                orientationPref = 0;
    			item.setChecked(true);
    		}
    		result = true;
        }	
        /* Screen Rotation Landscape */
        else if(id == R.id.menu_options_screen_rotation_landscape) {
        	if(isPortrait) {
    			isMirrorMode = true;
    			reversePref = 1;
                mirrorView.mirrorMode(true);
    			menuItemMirrorModeOn.setChecked(true);
    			
        		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	        	mirrorView.isPortrait = isPortrait = false;
                camera.setDisplayOrientation(0);
                setFrame(0);
                orientationPref = 1;
    			item.setChecked(true);
    		}
            result = true;
        }	

        /* Dark Theme */
        else if(id == R.id.menu_options_theme_dark) {
        	themePref = 1;
        	displayDialog(THEME_DIALOG);
        	result = true;
        }
        /* Light Theme */
        else if(id == R.id.menu_options_theme_light) {
        	themePref = 2;
        	displayDialog(THEME_DIALOG);
        	result = true;
        }
        /*  RedTheme */
        else if(id == R.id.menu_options_theme_red) {
        	if(version.compareTo("free") != 0) {
        		themePref = 3;
        		displayDialog(THEME_DIALOG);
        	}
        	else 
        		displayDialog(UPGRADE);
        	result = true;
        }
        /* Orange Theme */
        else if(id == R.id.menu_options_theme_orange) {
        	if(version.compareTo("free") != 0) {
        		themePref = 4;
        		displayDialog(THEME_DIALOG);
        	}
        	else 
        		displayDialog(UPGRADE);
        	result = true;
        }
        /* Green Theme */
        else if(id == R.id.menu_options_theme_green) {
        	if(version.compareTo("free") != 0) {
        		themePref = 5;
        		displayDialog(THEME_DIALOG);
        	}
        	else 
        		displayDialog(UPGRADE);
        	result = true;
        }
        /* Purple Theme */
        else if(id == R.id.menu_options_theme_purple) {
        	if(version.compareTo("free") != 0) {
        		themePref = 6;
        		displayDialog(THEME_DIALOG);
        	}
        	else 
        		displayDialog(UPGRADE);
        	result = true;
        }
        /* Blue Theme */
        else if(id == R.id.menu_options_theme_blue) {
        	if(version.compareTo("free") != 0) {
        		themePref = 7;
        		displayDialog(THEME_DIALOG);
        	}
        	else 
        		displayDialog(UPGRADE);
        	result = true;
        }
	    /* Snapshot Size Large */
        else if(id == R.id.menu_options_snapshot_size_large) {
        	snapshotSizePref = 0;
	    	item.setChecked(true);
        }
	    /* Snapshot Size Medium */
        else if(id == R.id.menu_options_snapshot_size_medium) {
        	snapshotSizePref = 1;
	    	item.setChecked(true);
        }
	    /* Snapshot Size Small */
        else if(id == R.id.menu_options_snapshot_size_small) {
        	snapshotSizePref = 2;
	    	item.setChecked(true);
        }
        /* App Info */
        else if(id == R.id.menu_options_app_info) {
        	displayDialog(APP_INFO_DIALOG);
        	result = true;
        }
        /* Help */
        else if(id == R.id.menu_options_help) {
        	displayDialog(HELP_DIALOG);
        	result = true;
        }
        /* Exit */
        else if(id == R.id.menu_exit) {
        	if(version.compareTo("paid") != 0 && hasRatedPref == 0 && useCountPref > 6) 
    			displayDialog(RATE);
    		else {
    			useCountPref++;
    			MirrorActivity.this.finish();
    		}
	    }
        else 
        	result = super.onOptionsItemSelected(item);
	    return result;
    }
    
    private void initIntentInfo() {

        // Get information from the intent
        intentMessage = getIntent().getStringExtra(MIRROR_MESSAGE);
        Scanner message = new Scanner(intentMessage);
        store = message.next();
        version = message.next();
        rateLink = message.next();
        upgradePaidLink = message.next();
        frameLink = message.next();
        message.useDelimiter("&");
        dialogAppInfoText = message.next();
    }
    
    private void initPrefs() {
        appSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        reversePref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_REVERSE, "1"));
        flipPref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_FLIP, "0"));
        orientationPref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_ORIENTATION, "0"));
        frameModePref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_FRAME, "0"));
        framePacksPref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_FRAME_CHANGED, "0"));
        initialLoadOnePref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_INITIAL_LOAD_ONE, "0"));
        initialLoadTwoPref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_INITIAL_LOAD_TWO, "0"));
        initialPausePref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_INITIAL_PAUSE, "0"));
        initialSnapshotPref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_INITIAL_SNAPSHOT, "0"));
        initialPhotoBoothPref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_INITIAL_PHOTOBOOTH, "0"));
        themePref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_THEME, "2"));
        exposurePref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_EXPOSURE, "-999"));
        whiteBalancePref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_WHITE_BALANCE, "0"));
        zoomPrefF = Integer.parseInt(appSettings.getString(APP_PREFERENCES_ZOOM, "0"));
        hasRatedPref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_HAS_RATED, "0"));
        useCountPref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_USE_COUNT, "0"));
        snapshotSizePref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_SNAPSHOT_SIZE, "0"));
        brightnessPref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_BRIGHTNESS, "0"));
        brightnessModePref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_BRIGHTNESS_MODE, "1"));
        brightnessLevelPref = Integer.parseInt(appSettings.getString(APP_PREFERENCES_BRIGHTNESS_LEVEL, "255"));
    }
    
    private void initTheme() {
        switch(themePref) {
	        case 1:
	        	setTheme(R.style.HoloDark);
	        	themeColor = "#33B5E5";
	        	break;
	        case 2:
	        	setTheme(R.style.HoloLight);
	        	themeColor = "#0099CC";
	        	break;
	        case 3:
	        	setTheme(R.style.HoloRed);
	        	themeColor = "#CC0000";
	        	break;
	        case 4:
	        	setTheme(R.style.HoloOrange);
	        	themeColor = "#FF8800";
	        	break;
	        case 5:
	        	setTheme(R.style.HoloGreen);
	        	themeColor = "#669900";
	        	break;
	        case 6:
	        	setTheme(R.style.HoloPurple);
	        	themeColor = "#9933CC";
	        	break;
	        case 7:
	        	setTheme(R.style.HoloBlue);
	        	themeColor = "#0099CC";
	        	break;
        }
    }
    
	private void initWindowFeatures() {
		if(orientationPref == 0) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			isPortrait = true;
			
		}
		else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			isPortrait = false;
		}
		if(brightnessPref == 0) {
			isBrightness = false;
		}
		else {
			isBrightness = true;
		}
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }
    
    private void initContentView() {
        setContentView(R.layout.main);
    }
    
    private void initMirrorView() {

	    mirrorView = (MirrorView) findViewById(R.id.mirror_view_view);
	    mirrorView.isFullscreen = true; // App starts in fullscreen mode
	    if(isPortrait) {
	    	mirrorView.isPortrait = true;
	    }
	    else {
	    	mirrorView.isPortrait = false;
	    }
    }
    
    private void initFrameManager() {
    	
    	int numberOfPacks = 0;
    	frameMgr = new FrameManager(getPackageManager());
    	Resources res = getResources();
    	
    	if(version.compareTo("free") == 0) {
    	
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror:drawable/frame_thumb0", null, null),
	    					  res.getIdentifier("com.wckd_dev.mirror:drawable/frame0", null, null),
	    					  res.getString(res.getIdentifier("com.wckd_dev.mirror:string/frame_no_frame", null, null)),
	    					  res);
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror:drawable/frame_thumb1_0", null, null),
	    					  res.getIdentifier("com.wckd_dev.mirror:drawable/frame1_0", null, null),
	    					  res.getString(res.getIdentifier("com.wckd_dev.mirror:string/frame_low_light_thin", null, null)),
	    					  res);
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror:drawable/frame_thumb1_1", null, null),
					          res.getIdentifier("com.wckd_dev.mirror:drawable/frame1_1", null, null),
					          res.getString(res.getIdentifier("com.wckd_dev.mirror:string/frame_low_light_medium", null, null)),
					          res);
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror:drawable/frame_thumb1_2", null, null),
					          res.getIdentifier("com.wckd_dev.mirror:drawable/frame1_2", null, null),
					          res.getString(res.getIdentifier("com.wckd_dev.mirror:string/frame_low_light_thick", null, null)),
					          res);
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror:drawable/frame_thumb2", null, null),
	    					  res.getIdentifier("com.wckd_dev.mirror:drawable/frame2", null, null),
	    					  res.getString(res.getIdentifier("com.wckd_dev.mirror:string/frame_soft_gold", null, null)),
	    					  res);
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror:drawable/frame_thumb3", null, null),
	    					  res.getIdentifier("com.wckd_dev.mirror:drawable/frame3", null, null),
	    					  res.getString(res.getIdentifier("com.wckd_dev.mirror:string/frame_brushed", null, null)),
	    					  res);
    	}
    	else if(version.compareTo("paid") == 0) {
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame_thumb0", null, null),
						      res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame0", null, null),
						      res.getString(res.getIdentifier("com.wckd_dev.mirror_paid:string/frame_no_frame", null, null)),
						      res);
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame_thumb1_0", null, null),
					  		  res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame1_0", null, null),
					          res.getString(res.getIdentifier("com.wckd_dev.mirror_paid:string/frame_low_light_thin", null, null)),
					          res);
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame_thumb1_1", null, null),
			                  res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame1_1", null, null),
			                  res.getString(res.getIdentifier("com.wckd_dev.mirror_paid:string/frame_low_light_medium", null, null)),
			                  res);
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame_thumb1_2", null, null),
			                  res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame1_2", null, null),
			                  res.getString(res.getIdentifier("com.wckd_dev.mirror_paid:string/frame_low_light_thick", null, null)),
			                  res);
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame_thumb2", null, null),
					          res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame2", null, null),
					          res.getString(res.getIdentifier("com.wckd_dev.mirror_paid:string/frame_soft_gold", null, null)),
					          res);
	    	frameMgr.addFrame(res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame_thumb3", null, null),
					          res.getIdentifier("com.wckd_dev.mirror_paid:drawable/frame3", null, null),
					          res.getString(res.getIdentifier("com.wckd_dev.mirror_paid:string/frame_brushed", null, null)),
					          res);
    		
    	}

    	if(frameMgr.hasCFP1())
    		numberOfPacks++;
    	if(frameMgr.hasCFP2())
    		numberOfPacks++;
    	
    	if(frameMgr.hasFFP1())
    		numberOfPacks++;
    	if(frameMgr.hasFFP2())
    		numberOfPacks++;
    	
    	if(frameMgr.hasNFP1())
    		numberOfPacks++;
    	if(frameMgr.hasNFP2())
    		numberOfPacks++;

    	if(frameMgr.hasVFP())
    		numberOfPacks++;
    	
    	if(numberOfPacks == framePacksPref) // No new frame packs
    		setFrame(frameModePref); // Restore saved preference
    	else { // Packs have been installed/removed
    		setFrame(0); // Default frame used
    		framePacksPref = numberOfPacks; // Record number of packs installed
    	}
    }
    	
    private void initCamera() {
    
    	// Try to find the ID of the front camera
        numberOfCameras = Camera.getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        for(int i = 0; i < numberOfCameras && !isFrontCameraFound; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if(cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                backCameraId = i;
                isBackCameraFound = true;
            }
            else if(cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                frontCameraId = i;
                isFrontCameraFound = true;
            }
        }
        
        if(isFrontCameraFound) { // If front camera is found, use front camera
        	currentCameraId = frontCameraId;
        	isUsingFrontCamera = true;
        }
        else if(!isFrontCameraFound && isBackCameraFound) { // If no front camera and back is found, use back camera
        	currentCameraId = backCameraId;
        	isUsingFrontCamera = false;
        	displayDialog(FRONT_CAMERA_NOT_FOUND);
        }
        else if(!isBackCameraFound && !isFrontCameraFound) {// No cameras found
        	displayDialog(CAMERA_NOT_FOUND);
        }
    }
    
    private void initPauseButton() {
    	// Prepare pause button
        pause = (ImageButton) findViewById(R.id.pause_button);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        pause.startAnimation(slideDown);
        pause.setVisibility(View.INVISIBLE);
        pause.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		
        		if(isPaused)
        			unpausePreview();
        		else
        			pausePreview();
        	}
        });
    }
    
    private void initSnapshotButton() {
    	// Prepare snapshot button
        takeSnapshot = (ImageButton) findViewById(R.id.snapshot_button);
        rightSlideIn = AnimationUtils.loadAnimation(this, R.anim.right_slide_in);
        rightSlideOut = AnimationUtils.loadAnimation(this, R.anim.right_slide_out);
        takeSnapshot.startAnimation(rightSlideOut);
        takeSnapshot.setVisibility(View.INVISIBLE);
        takeSnapshot.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		
        		if(version.compareTo("free") == 0) {
            		displayDialog(UPGRADE);
        		}
        		else {
        		
	        		snapshot = new Snapshot(mirrorView, findViewById(R.id.frame_overlay), getApplicationContext());
	        		
	        		if(snapshotSizePref == 2) {
	        			imageSize = Snapshot.ImageSize.SMALL;
	        		}
	        		else if(snapshotSizePref == 1) {
	        			imageSize = Snapshot.ImageSize.MEDIUM;
	        		}
	        		else {
	        			imageSize = Snapshot.ImageSize.LARGE;
	        		}
	        		
	        		snapshot.takePhoto(imageSize);
	        		try {
	    	    		ContentValues values = snapshot.savePhoto();
	    	    		MirrorActivity.this.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
	    	    	}
	    	    	catch(Exception e) {
	    	    		e.printStackTrace();
	    	    	}
	        		snapshot = null;
        		}
        		
        	}
        });
    }
    
    private void initPhotoBoothButton() {
    	// Prepare photobooth button
        takePhotoBooth = (ImageButton) findViewById(R.id.photobooth_button);
        leftSlideIn = AnimationUtils.loadAnimation(this, R.anim.left_slide_in);
        leftSlideOut = AnimationUtils.loadAnimation(this, R.anim.left_slide_out);
        takePhotoBooth.startAnimation(leftSlideOut);
        takePhotoBooth.setVisibility(View.INVISIBLE);
        takePhotoBooth.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {

        		if(version.compareTo("free") == 0) {
        			displayDialog(UPGRADE);
        		}
        		else {
        			try {
	        			photoBoothCount++;
	        			switch(photoBoothCount) {
	        				case 1:
	        					booth = new PhotoBooth(mirrorView, findViewById(R.id.frame_overlay), getResources(), getApplicationContext());
	                	    	booth.takePhotoOne();
	                	    	break;
	        				case 2:
	        					booth.takePhotoTwo();
	        					break;
	        				case 3:
	        					booth.takePhotoThree();
	        					break;
	        				case 4:
	        					booth.takePhotoFour();
	        					booth.createPhotoStrip();
	                	    	
	                	    	try {
	                	    		ContentValues values = booth.savePhotoStrip();
	                	    		MirrorActivity.this.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
	                	    	}
	                	    	catch(Exception e) {
	                	    		e.printStackTrace();
	                	    		throw e;
	                	    	}
	                	    	booth = null;
	                	    	photoBoothCount = 0;
	        					break;
	        				default:	
	        			}
	    			} 
	    			catch (Exception e) {
	    				e.printStackTrace();
	    			}
        		}
        	}
        });
    }
    
    private void initOnTouchListener() {
	    // Initialize onTouchListener
	    ImageView view = (ImageView) findViewById(R.id.invis_button);
	    view.setOnTouchListener(this);
	    
	    matrix = new Matrix();
	    savedMatrix = new Matrix();
    }
    
    private void initBrightnessSettings() {
		conRes = getContentResolver();
    	if(isBrightness) {
        	
        	try {
        		brightnessModePref = Settings.System.getInt(conRes, Settings.System.SCREEN_BRIGHTNESS_MODE);
        		if(brightnessModePref == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
        			Settings.System.putInt(conRes, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        		}
        		else {
        			brightnessLevelPref = Settings.System.getInt(conRes, Settings.System.SCREEN_BRIGHTNESS);
        		}
        		Settings.System.putInt(conRes, Settings.System.SCREEN_BRIGHTNESS, 255);
        	}
        	catch(SettingNotFoundException e) {
        		Log.e(TAG, "Cannot access system brightness settings.");
        		e.printStackTrace();
        	}
    	}
    }
    
    private void restoreBrightnessSettings(boolean force) {
    	if(isBrightness || force) {
    		if(brightnessModePref == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
        		Settings.System.putInt(conRes, Settings.System.SCREEN_BRIGHTNESS_MODE, brightnessModePref);
        	}
        	else {
        		Settings.System.putInt(conRes, Settings.System.SCREEN_BRIGHTNESS, brightnessLevelPref);
        	}
    	}
    }
    
    protected void initExpSeekBar(SeekBar expSeek) {
    	
    	switch(themePref) {
	        case 1:
	        	setTheme(R.style.HoloDark);
	        	expSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_dark));
	        	expSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_dark));
	        	break;
	        case 2:
	        	setTheme(R.style.HoloLight);
	        	expSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_light));
	        	expSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_light));
	        	break;
	        case 3:
	        	setTheme(R.style.HoloRed);
	        	expSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_red));
	        	expSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_red));
	        	break;
	        case 4:
	        	setTheme(R.style.HoloOrange);
	        	expSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_orange));
	        	expSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_orange));
	        	break;
	        case 5:
	        	setTheme(R.style.HoloGreen);
	        	expSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_green));
	        	expSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_green));
	        	break;
	        case 6:
	        	setTheme(R.style.HoloPurple);
	        	expSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_purple));
	        	expSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_purple));
	        	break;
	        case 7:
	        	setTheme(R.style.HoloBlue);
	        	expSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_blue));
	        	expSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_blue));
	        	break;
    	}
    	
    	int[] range = mirrorView.getExposureRange();
		expSeek.setMax(range[1] - range[0]);
		expSeek.setProgress(exposurePref == -999 ? (range[1] - range[0]) / 2 : exposurePref);
		expSeek.setPadding(50, 20, 50, 20);
		expSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				try {
					setExposure(seekBar.getProgress());
				}
				catch(RuntimeException e) {
					
				}
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            	try {
					setExposure(seekBar.getProgress());
				}
				catch(RuntimeException e) {
		    		Toast.makeText(getApplicationContext(), R.string.toast_no_exposure, Toast.LENGTH_SHORT).show();
				}
            }
		});
		
		
    }
    
    protected void initZoomSeekBar(SeekBar zoomSeek) {
    	
    	switch(themePref) {
	        case 1:
	        	setTheme(R.style.HoloDark);
	        	zoomSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_dark));
	        	zoomSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_dark));
	        	break;
	        case 2:
	        	setTheme(R.style.HoloLight);
	        	zoomSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_light));
	        	zoomSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_light));
	        	break;
	        case 3:
	        	setTheme(R.style.HoloRed);
	        	zoomSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_red));
	        	zoomSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_red));
	        	break;
	        case 4:
	        	setTheme(R.style.HoloOrange);
	        	zoomSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_orange));
	        	zoomSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_orange));
	        	break;
	        case 5:
	        	setTheme(R.style.HoloGreen);
	        	zoomSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_green));
	        	zoomSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_green));
	        	break;
	        case 6:
	        	setTheme(R.style.HoloPurple);
	        	zoomSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_purple));
	        	zoomSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_purple));
	        	break;
	        case 7:
	        	setTheme(R.style.HoloBlue);
	        	zoomSeek.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress_blue));
	        	zoomSeek.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb_blue));
	        	break;
    	}
    	
    	zoomSeek.setMax(mirrorView.getZoomMax());
		zoomSeek.setProgress(Math.round(zoomPrefF) == -1 ? 0 : Math.round(zoomPrefF));
		zoomSeek.setPadding(50, 20, 50, 20);
		zoomSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				zoomPrefF = seekBar.getProgress();
				setZoom(Math.round(zoomPrefF));
            }
			
            public void onStartTrackingTouch(SeekBar seekBar) {
                
            }
            
            public void onStopTrackingTouch(SeekBar seekBar) {
            	zoomPrefF = seekBar.getProgress();
				setZoom(Math.round(zoomPrefF));
            }
		});
    }
    
    // TODO - These show<action>Dialog methods could be condensed into one method
    // or they could be removed in favor of something else
    
    private void showWelcomeOneDialog() {
    	// If first load, first click, display instruction dialog
    	if(initialLoadOnePref != INFO_DIALOGS) {
    		initialLoadOnePref = INFO_DIALOGS; // Set app preferences initial load to false
    		Editor editor = appSettings.edit();
    		editor.putString(APP_PREFERENCES_INITIAL_LOAD_ONE, Integer.toString(initialLoadOnePref));
    		editor.commit();
    		displayCustomDialog(WELCOME_ONE_DIALOG);
    	}
    }
    
    private void showWelcomeTwoDialog() {
    	// If first load, second click, display instruction dialog
    	if(initialLoadOnePref == INFO_DIALOGS && initialLoadTwoPref != INFO_DIALOGS) {
    		initialLoadTwoPref = INFO_DIALOGS; // Set app preferences initial load to false
    		Editor editor = appSettings.edit();
    		editor.putString(APP_PREFERENCES_INITIAL_LOAD_TWO, Integer.toString(initialLoadTwoPref));
    		editor.commit();
    		displayCustomDialog(WELCOME_TWO_DIALOG);
    	}
    }
    
    private void showSnapshotDialog() {
    	// If first button press, display instruction dialog
    	if(initialSnapshotPref != INFO_DIALOGS) {
    		initialSnapshotPref = INFO_DIALOGS; // Set app preferences initial load to false
    		Editor editor = appSettings.edit();
    		editor.putString(APP_PREFERENCES_INITIAL_SNAPSHOT, Integer.toString(initialSnapshotPref));
    		editor.commit();
    		displayCustomDialog(SNAPSHOT_DIALOG);	
    	}
    }
    
    private void showPhotoBoothDialog() {
    	// If first button press, display instruction dialog
    	if(initialPhotoBoothPref != INFO_DIALOGS) {
    		initialPhotoBoothPref = INFO_DIALOGS; // Set app preferences initial load to false
    		Editor editor = appSettings.edit();
    		editor.putString(APP_PREFERENCES_INITIAL_PHOTOBOOTH, Integer.toString(initialPhotoBoothPref));
    		editor.commit();
    		displayCustomDialog(PHOTOSTRIP_DIALOG);	
    	}
    }
    
    private void showPauseDialog() {
    	// If first button press, display instruction dialog
    	if(initialPausePref != INFO_DIALOGS) {
    		initialPausePref = INFO_DIALOGS; // Set app preferences initial load to false
    		Editor editor = appSettings.edit();
    		editor.putString(APP_PREFERENCES_INITIAL_PAUSE, Integer.toString(initialPausePref));
    		editor.commit();
    		displayCustomDialog(PAUSE_DIALOG);	
    	}
    }
    
    private void loadCamera() {
    	if(camera == null) {
        	
		    try {
		        camera = Camera.open(currentCameraId);
			    if(isPortrait)
			    	camera.setDisplayOrientation(90);
			    else
			    	camera.setDisplayOrientation(0);
		    	mirrorView.setCamera(camera); //
		    	Camera.Parameters parameters = camera.getParameters();
		    	isZoomSupported = parameters.isZoomSupported();
		    	if(parameters.getMinExposureCompensation() != 0 || parameters.getMaxExposureCompensation() != 0 ) 
		    		isExposureSupported = true;
		    	if(parameters.getWhiteBalance() != null)
		    		isWhiteBalanceSupported = true;
		    } 
	        catch(RuntimeException e) {
	        		displayDialog(CAMERA_NOT_FOUND);
	        		e.printStackTrace();
	        }
		    
		    if(isPreviewStopped) {
		    	mirrorView.startPreview();
		    	isPreviewStopped = false;
		    }
        }
    }
    
    private void unloadCamera() {
    	if (camera != null) {
            
            try { 
            	mirrorView.setCamera(null);
            }
            catch(RuntimeException e) {
	        	Toast.makeText(this, "Error Unloading Camera", Toast.LENGTH_SHORT).show();
	        }
            
            camera.stopPreview();
            isPreviewStopped = true;
            camera.release();
            camera = null;
        }
    }
    
    private void pausePreview() {
    	mirrorView.stopPreview();
		isPaused = true;
		if(themePref == 2 || themePref == 4 || themePref == 5 || themePref == 7) 
			pause.setImageResource(R.drawable.button_play_holo_light);
		else
			pause.setImageResource(R.drawable.button_play_holo_dark);
    }
    
    private void unpausePreview() {
    	mirrorView.startPreview();
		isPaused = false;
		if(themePref == 2 || themePref == 4 || themePref == 5 || themePref == 7) 
			pause.setImageResource(R.drawable.button_pause_holo_light);
		else
			pause.setImageResource(R.drawable.button_pause_holo_dark);
    }
    
     private void displayDialog(int id) {     
    	QustomDialogBuilder builder = new QustomDialogBuilder(this);
    	builder
    	  .setTitleColor(themeColor)
    	  .setDividerColor(themeColor);
        DialogManager.buildQustomDialog(this, id, builder);
    }
     
     private void displayCustomDialog(int id) {
     	dialog = new Dialog(this);
     	DialogManager.buildDialog(this, id);
     }
     
    protected void sendBugReport(String subject) {
    	Intent emailIntent = new Intent(android.content.Intent.ACTION_VIEW); 
    	emailIntent.setData(Uri.parse("mailto:wckd.dev@gmail.com"));
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);  
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "" +
				getResources().getString(R.string.bug_report) +
				"\n\nStore: " + store +
				"\nVersion: " + version +
				"\nBrand: " + android.os.Build.BRAND +
				"\nManufacturer: " + android.os.Build.MANUFACTURER +
				"\nModel: " + android.os.Build.MODEL + 
				"\nDevice: " + android.os.Build.DEVICE +
				"\nCameras: " + numberOfCameras +
				"\nBack ID: " + backCameraId +
				"\nFront ID: " + frontCameraId +
				"\nCurrent ID: " + currentCameraId +
				"\n" + mirrorView.getDisplayInfo());   
		startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.bug_report_send))); 
		MirrorActivity.this.finish();
    }

    protected void setFrame(int position) {
    	
    	ImageView frame = (ImageView) findViewById(R.id.frame_overlay);
    	frame.setImageDrawable(frameMgr.getFrameResId(position));
    	frameModePref = position;
    }
    
    private void setZoom(int level){ 
    	
	    if(isZoomSupported) {
	    	mirrorView.zoom(level);
		}
		else {
    		Toast.makeText(getApplicationContext(), R.string.toast_no_zoom, Toast.LENGTH_SHORT).show();
		}
    }
    
    private void setExposure(int level) throws RuntimeException { 
    	
    	try {
			mirrorView.exposure(level);
	    	exposurePref = level;
    	}
    	catch(RuntimeException e) {
    		exposurePref = -999;
    		throw e;
    	}
    }
    
    private void setWhiteBalance(int level) { 
    	
    	try {
	    	mirrorView.whiteBalance(level);
	    	whiteBalancePref = level;
	    	
	    	// TODO - Why won't this work? App crashes during menu load after icon change
	    	// Requesting resource failed because it is too complex
	    	
	    	/* switch(level) {
	    	
			    case 0:
					menuItemWB.setIcon(R.attr.wbAutoIcon); // drawable.menu_options_white_balance_auto_holo_dark);
					break;

				case 1:
					menuItemWB.setIcon(R.attr.wbDaylightIcon); // drawable.menu_options_white_balance_daylight_holo_dark);
					break;

				case 2:
					menuItemWB.setIcon(R.attr.wbIncanIcon); // drawable.menu_options_white_balance_incandescent_holo_dark);
					break;

				case 3:
					menuItemWB.setIcon(R.attr.wbFluorIcon); // drawable.menu_options_white_balance_fluorescent_holo_dark);
					break;
	    	} */
	    	
	    	switch(level) {
			    case 0:
					if(themePref == 2 || themePref == 4 || themePref == 5 || themePref == 7) 
			        	menuItemWhiteBalance.setIcon(R.drawable.menu_options_white_balance_auto_holo_light);
					else
				        menuItemWhiteBalance.setIcon(R.drawable.menu_options_white_balance_auto_holo_dark);
					break;
				case 1:
					if(themePref == 2 || themePref == 4 || themePref == 5 || themePref == 7)  
			        	menuItemWhiteBalance.setIcon(R.drawable.menu_options_white_balance_daylight_holo_light);
					else
				        menuItemWhiteBalance.setIcon(R.drawable.menu_options_white_balance_daylight_holo_dark);
					break;
				case 2:
					if(themePref == 2 || themePref == 4 || themePref == 5 || themePref == 7)  
			        	menuItemWhiteBalance.setIcon(R.drawable.menu_options_white_balance_incandescent_holo_light);
					else
				        menuItemWhiteBalance.setIcon(R.drawable.menu_options_white_balance_incandescent_holo_dark);
					break;
				case 3:
					if(themePref == 2 || themePref == 4 || themePref == 5 || themePref == 7)  
			        	menuItemWhiteBalance.setIcon(R.drawable.menu_options_white_balance_fluorescent_holo_light);
					else
				        menuItemWhiteBalance.setIcon(R.drawable.menu_options_white_balance_fluorescent_holo_dark);
					break;
	    	}
	    	
    	}
		catch(RuntimeException e) {
			Toast.makeText(getApplicationContext(), R.string.toast_no_white_balance, Toast.LENGTH_SHORT).show();
			whiteBalancePref = 0;
		}
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
    	
    	switch(event.getAction() & MotionEvent.ACTION_MASK){
    	
    		case MotionEvent.ACTION_DOWN:
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
    			touchState = PRESS;
    			break;
    			
    		case MotionEvent.ACTION_POINTER_DOWN:
    			if(!isPaused) {
	    			startDist = spacing(event);
	    	        if (startDist > minDist) {
	    	           savedMatrix.set(matrix);
	    	           midPoint(mid, event);
	    	           touchState = ZOOM;
	    	        }
    			}
    			break;
    			
    		case MotionEvent.ACTION_UP:
    			if(touchState == PRESS) {
	    			float moveDist = spacing(event, start);
	    			if(moveDist < minDist) {
	    				
	    				pressTime = System.currentTimeMillis();
	    				
	    				if(pressTime - lastPressTime < DOUBLE_TAP_LENGTH) {
	    					if(isPaused)
	    	        			unpausePreview();
	    	        		else
	    	        			pausePreview();
	    					hasDoubleTapped = true;
	    				}
	    				else {
	    					hasDoubleTapped = false;
	    		            Handler myHandler = new Handler() {
	    		                 public void handleMessage(Message emptyMessage) {
	    		                      if (!hasDoubleTapped) 
	    		                            fullScreenClick();
	    		                 }
	    		            };
	    		            Message emptyMessage = new Message();
	    		            myHandler.sendMessageDelayed(emptyMessage,DOUBLE_TAP_LENGTH);

	    				}
	    				
	    				lastPressTime = pressTime;
	    			}
    			}
    			break;
    			
    		case MotionEvent.ACTION_POINTER_UP:
    			if(touchState == ZOOM) {
        			touchState = NONE;
    			}
    			break;
    			
    		case MotionEvent.ACTION_MOVE:
    			if ( (touchState == PRESS && spacing(event, start) > minDist) || touchState == DRAG) {
    	            matrix.set(savedMatrix);
    	            matrix.postTranslate(event.getX() - start.x,
    	                  event.getY() - start.y);
    	            touchState = DRAG;
    	         }
    	         else if (touchState == ZOOM) {
    	            float nowDist = spacing(event);
    	            if (nowDist > minDist) {
    	               matrix.set(savedMatrix);
    	               float scale = nowDist / startDist;
    	               matrix.postScale(scale, scale, mid.x, mid.y);
    	               
    	               if(nowDist > lastDist) 
    	            	   zoomIn();
    	               else 
    	            	   zoomOut();
    	            }
    	            lastDist = nowDist;
    	         }
    			 break;
    	}
    	
    	return true;
    }
    
    /** Determine the space between a finger and a saved point */
    @SuppressLint("FloatMath")
	private float spacing(MotionEvent event, PointF point) {
        float x = event.getX() - point.x;
        float y = event.getY() - point.y;
        return FloatMath.sqrt(x * x + y * y);
     }
    
    /** Determine the space between the first two fingers */
    @SuppressLint("FloatMath")
	private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
     }

     /** Calculate the mid point of the first two fingers */
     private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
     }
     
     private void fullScreenClick() {

	    showWelcomeTwoDialog();
	        
 		// Exit fullscreen mode
 		if(isFullscreen) {

 			// Show navigation and notification bar
 			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE); 
 			getActionBar().show(); // Show action bar
 			isFullscreen = false;
 			mirrorView.isFullscreen = false;
 		}
 		// Enter fullscreen mode
 		else {
 			// Hide notification bar and set navigation bar to low profile
 			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
 			getActionBar().hide(); // Hide action bar
 			isFullscreen = true;
 			mirrorView.isFullscreen = true;
 		}
     }
     
     private void zoomIn() {
     	if(zoomRate == 0f) 
     		zoomRate = mirrorView.getZoomMax() * 0.02f;
     	if(zoomPrefF < (mirrorView.getZoomMax() - zoomRate)) {
     		zoomPrefF += zoomRate;
     		setZoom(Math.round(zoomPrefF));
     	}
     }
     
     private void zoomOut() {
     	if(zoomRate == 0f) 
     		zoomRate = mirrorView.getZoomMax() * 0.02f;
     	if(zoomPrefF > (0 + zoomRate)) {
     		zoomPrefF -= zoomRate;
     		setZoom(Math.round(zoomPrefF));
     	}
     }
    
    
}
