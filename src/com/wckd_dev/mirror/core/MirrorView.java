package com.wckd_dev.mirror.core;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
 

public class MirrorView extends ViewGroup implements TextureView.SurfaceTextureListener {

	// private final String TAG = "wckd";
	
	/* Camera */
	
    private Camera camera;
    private Camera.Parameters cameraParameters;
    
    /* Texture Preview*/ 
    
    private TextureView textureView;
    
    /* Dimensions */
    
    /** A list of the camera's supported preview sizes, may be ascending or descending depending on device */
    private List<Size> supportedPreviewSizes;
    
    /** previewSize dimensions represent the highest supported resolution for the camera's preview size */
    private Size previewSize;
    
    /** screenSize dimensions represent the actual dimensions of the device's screen, for full screen measurements */
    private Size screenSize;
    
    /** layoutSize dimensions represent the layout size, after adjustment, for the image on screen */
    private Size layoutSize;
    
    /** cropSize dimensions represent the overhang of the preview on the device screen, used to center preview */
    private Size cropSize;
    
    /* Flags */
    
    public boolean isFullscreen;
    public boolean isPortrait;
    
    /* Scale Values */
    
    private float xScaleValue = 1.0f;
    private float yScaleValue = 1.0f;
    
    /* Objects */
    
    private WindowManager wm;

    public MirrorView(Context context) {
        super(context);

        textureView = new TextureView(context);
        textureView.setSurfaceTextureListener(this);
        addView(textureView);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
    
    public MirrorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        textureView = new TextureView(context);
        textureView.setSurfaceTextureListener(this);
        addView(textureView);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
    
    public MirrorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        textureView = new TextureView(context);
        textureView.setSurfaceTextureListener(this);
        addView(textureView);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	if (changed && getChildCount() > 0 && camera != null) {
    		int left, top, right, bottom;
			initSizes();
    		final View child0 = getChildAt(0);
    		
    		if(isFullscreen == true) {
    			left = 0 + (cropSize.width / 2);
  				top = 0 + (cropSize.height / 2);
  				right = layoutSize.width + (cropSize.width / 2);
  				bottom = layoutSize.height + (cropSize.height / 2);
    		}
    		else {
    			left = 0 + (cropSize.width / 2);
  				top = 0 + (cropSize.height / 2) - 108;
  				right = layoutSize.width + (cropSize.width / 2);
  				bottom = layoutSize.height + (cropSize.height / 2) - 108;
    		} 
    		
    		child0.layout(left, top, right, bottom);
    		
    	}
    }

    public void setCamera(Camera theCamera) throws RuntimeException {
        
        camera = theCamera;
        if (camera != null) {
        	initSizes();
            cameraParameters = camera.getParameters();
            if(isPortrait)
            	cameraParameters.setPreviewSize(previewSize.height, previewSize.width);
            else
            	cameraParameters.setPreviewSize(previewSize.width, previewSize.height);
            try {
            	camera.setParameters(cameraParameters);
            }
            catch(RuntimeException e) {
            	throw e;
            }
        }
    }
    
    private void initSizes() { 
    	initDeviceDimensions();
    	// Log.d(TAG, "screenSize - w: " + screenSize.width + " h: " + screenSize.height);
        initPreviewDimensions();
    	// Log.d(TAG, "previewSize - w: " + previewSize.width + " h: " + previewSize.height);
        initLayoutDimensions();
    	// Log.d(TAG, "layoutSize - w: " + layoutSize.width + " h: " + layoutSize.height);
        initCropDimensions();
    	// Log.d(TAG, "cropSize - w: " + cropSize.width + " h: " + cropSize.height);
    }
    
    public void initDeviceDimensions() {
        
        Point devSize = new Point();
        Display display = wm.getDefaultDisplay();
        display.getSize(devSize);
        
        try {
        	screenSize = camera.new Size(devSize.x, devSize.y);
        }
        catch(NullPointerException e) {
        	
        }
    }
    
    public void initPreviewDimensions() {
    	cameraParameters = camera.getParameters();
        
        int prevWidth = 0, prevHeight = 0, largestRes = 0;
        
        // Find and list the supported preview sizes
        supportedPreviewSizes = cameraParameters.getSupportedPreviewSizes();
        for (Size size : supportedPreviewSizes) {
          
          // Find the highest resolution to use for the camera preview
          if((size.width * size.height) > largestRes) {
        	  largestRes = size.width * size.height;
        	  prevWidth = size.height;
        	  prevHeight = size.width;
          }
        }
        if(isPortrait)
        	previewSize = camera.new Size(prevWidth, prevHeight);
        else
        	previewSize = camera.new Size(prevHeight, prevWidth);
    }
    
    private void initLayoutDimensions() {
    	
    	/* ADJUSTMENT 1 - Attempt to adjust using widths */
        float multiplier = (float) screenSize.width / (float) previewSize.width;
        int adjHeight = (int) (previewSize.height * multiplier);
        	
        // If the adjusted preview height will fill the screen, use the adjusted preview height
        if(adjHeight >= screenSize.height) {
        	layoutSize = camera.new Size(screenSize.width , adjHeight);
        }
        	
        /* ADJUSTMENT 2 - If the adjusted preview height is to small to fill the screen, adjust preview based on height */
        else {
        	multiplier = (float) screenSize.height / (float) previewSize.height;
            int adjWidth = (int) (previewSize.width * multiplier);
            
            if(adjWidth >= screenSize.width) {
                layoutSize = camera.new Size(adjWidth, screenSize.height);
            }
            
        /* ADJUSTMENT 3 - If both adj 1 and adj 2 fail (unpossible?), the unadjusted preview size is used */
            else {
                layoutSize = camera.new Size(previewSize.width, previewSize.height);
            }
        }
    }
    
    private void initCropDimensions() {
        cropSize = camera.new Size(screenSize.width - layoutSize.width, screenSize.height - layoutSize.height);
    }
    
    protected Bitmap grabMirrorImage(Snapshot.ImageSize size) { 
    	
    	int x, y, width, height;
    	Bitmap result;
    	// Log.d(TAG, "textureView - w: " + textureView.getWidth() + " h: " + textureView.getHeight());
    	
		x = 0 - (cropSize.width / 2);
		y = 0 - (cropSize.height / 2);
		width = screenSize.width;
		height = screenSize.height;
		
		// Log.d(TAG, "bitmap - x: " + x + " y: " + y + " w: " + width + " b: " + height);
    	result = Bitmap.createBitmap(textureView.getBitmap(), x, y, width, height);
		switch(size) {
			case SMALL:
				result = Bitmap.createScaledBitmap(result, (int) (width * 0.5f), (int) (height * 0.5f), false);
				break;
			case MEDIUM:
				result = Bitmap.createScaledBitmap(result, (int) (width * 0.75f), (int) (height * 0.75f), false);
				break;
			case LARGE:
				break;
		}
    	
    	return result;
    }
    
    public void zoom(int value) {
    	
    	if (camera != null) {
            cameraParameters = camera.getParameters();
            
            cameraParameters.setZoom(value);
            try {
            	camera.setParameters(cameraParameters);
            }
            catch(RuntimeException e) {
            } 
        }
    }
    
    public int getZoomMax() {
    	cameraParameters = camera.getParameters();
    	return cameraParameters.getMaxZoom();
    }
    
    public void exposure(int value) throws RuntimeException {
    	
        if (camera != null) {
            if(value != -999) {	
	            cameraParameters = camera.getParameters();
	            
	            int[] rangeLimits = getExposureRange();
	            int range = rangeLimits[1] - rangeLimits[0];
	            
	            int adjValue = value - (range / 2);
	            cameraParameters.setExposureCompensation(adjValue);
	            try {
	            	camera.setParameters(cameraParameters);
	            }
	            catch(RuntimeException e) {
	            	throw e;
	            }
            }
        }
    }
    
    public int[] getExposureRange() {
    	int[] range = new int[2];
    	cameraParameters = camera.getParameters();
    	range[0] = cameraParameters.getMinExposureCompensation();
    	range[1] = cameraParameters.getMaxExposureCompensation();
    	return range;
    }
    
    public void whiteBalance(int value) throws RuntimeException {
    	
        if (camera != null) {
            	
            cameraParameters = camera.getParameters();
            switch(value) {
            	case 0:
            		cameraParameters.setWhiteBalance("auto");
            		break;
            	case 1:
            		cameraParameters.setWhiteBalance("daylight");
            		break;
            	case 2:
            		cameraParameters.setWhiteBalance("incandescent");
            		break;
            	case 3:
            		cameraParameters.setWhiteBalance("fluorescent");
            		break;
            }
            try {
            	camera.setParameters(cameraParameters);
            }
            catch(RuntimeException e) {
            	throw e;
            }
        }
    }
    
    public void mirrorMode(boolean mode) {
    	if(mode) {
    		Matrix flip = new Matrix();
    		xScaleValue = 1.0f;
    		flip.setScale(xScaleValue, yScaleValue, (float) (screenSize.width/2), (float) (screenSize.height/2));
	        textureView.setTransform(flip);
    	}
    	else {
	    	Matrix flip = new Matrix();
	    	xScaleValue = -1.0f;
	    	flip.setScale(xScaleValue, yScaleValue, (float) (screenSize.width/2), (float) (screenSize.height/2));
		    textureView.setTransform(flip);
    	}
    }
    
    public void flipMode(boolean mode) {
    	if(mode) {
    		Matrix flip = new Matrix();
    		yScaleValue = -1.0f;
    		flip.setScale(xScaleValue, yScaleValue, (float) (screenSize.width/2), (float) (screenSize.height/2 - cropSize.height));
	        textureView.setTransform(flip);
    	}
    	else {
	    	Matrix flip = new Matrix();
	    	yScaleValue = 1.0f;
	    	flip.setScale(xScaleValue, yScaleValue, (float) (screenSize.width/2), (float) (screenSize.height/2 - cropSize.height));
		    textureView.setTransform(flip);
    	}
    }
    
    public String getDisplayInfo() {
    	String info;
    	if(camera != null) {
	    	info =  "Screen Size: " + screenSize.width + " x " + screenSize.height +
	    		      "\nPreview Size: " + previewSize.width + " x " + previewSize.height +
	    		      "\nLayout Size: " + layoutSize.width + " x " + layoutSize.height +
	    		      "\nCrop Size: " + cropSize.width + " x " + cropSize.height;
    	}
    	else
    		info = "No Camera Found";
    	return info;
    }
    
    public int getScreenHeight() { return screenSize.height; }
    
    public int getScreenWidth() { return screenSize.width; }
    
    public void startPreview() {
        try {
            if (camera != null) {

                camera.setPreviewTexture(textureView.getSurfaceTexture());
                camera.startPreview();
            }
        } 
        catch (IOException exception) {
        }
    }
    
    public void stopPreview() {
        if (camera != null) {

           camera.stopPreview();
        }
    }
    	
    @Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            if (camera != null) {
            	
                camera.setPreviewTexture(surface);
                camera.startPreview();
            }
        }
        catch (IOException e) {
        }
	}

    @Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    	if (camera != null) {
        	camera.stopPreview();
            camera.release();
        }
        return true;
	}
    
	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
		
	}
	
	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
	}

}
