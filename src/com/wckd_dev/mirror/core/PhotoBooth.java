package com.wckd_dev.mirror.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.view.View;
import android.widget.Toast;

public class PhotoBooth {
	
	// private final String TAG = "wckd";
	
	private boolean isOneFinished = false;
	private boolean isTwoFinished = false;
	private boolean isThreeFinished = false;
	private boolean isFourFinished = false;
	private boolean isStripFinished = false;
	
	private Bitmap frame;
	private Bitmap photoOne, photoTwo, photoThree, photoFour;
	private Bitmap photoStrip;
	private Canvas photoCanvas;
	private Context context;
	private Drawable photoDrawable;
	private MirrorView mirror;
	private Random rand;
	private Resources resources;
	private View frameView;
	
	public PhotoBooth(MirrorView image, View frame, Resources res, Context con) {
		rand = new Random();
		context = con;
		mirror = image;
		resources = res;
		frameView = frame;
	}
	
	public void takePhotoOne() {
		
		Bitmap rawPhotoOne = Bitmap.createBitmap(mirror.grabMirrorImage(Snapshot.ImageSize.SMALL));
		Toast.makeText(context, R.string.toast_photo_one, Toast.LENGTH_SHORT).show();
		
		frameView.setDrawingCacheEnabled(true);
		frameView.buildDrawingCache(true);
		frame = Bitmap.createBitmap(frameView.getDrawingCache());
		frameView.buildDrawingCache(false);
		photoOne = rawPhotoOne.copy(rawPhotoOne.getConfig(), true);
		photoCanvas = new Canvas(photoOne);
		photoDrawable = new BitmapDrawable(resources, frame);
		photoDrawable.setBounds(0, 0, photoOne.getWidth(), photoOne.getHeight());
		photoDrawable.draw(photoCanvas);
		photoCanvas = null;
		frameView = null;
		frame = null;
		
		// Log.d(TAG, "Photo One Complete");
		isOneFinished = true;
	}
	
	public void takePhotoTwo() {
		
		Bitmap rawPhotoTwo = Bitmap.createBitmap(mirror.grabMirrorImage(Snapshot.ImageSize.SMALL));
		Toast.makeText(context, R.string.toast_photo_two, Toast.LENGTH_SHORT).show();
		
		while(!isOneFinished) {
			// Wait for one to finish
		}
		
		photoTwo = rawPhotoTwo.copy(rawPhotoTwo.getConfig(), true);
		photoCanvas = new Canvas(photoTwo);
		photoDrawable.draw(photoCanvas);
		photoCanvas = null;
		
		// Log.d(TAG, "Photo Two Complete");
		isTwoFinished = true;
		
	}
	
	public void takePhotoThree() {
		
		Bitmap rawPhotoThree = Bitmap.createBitmap(mirror.grabMirrorImage(Snapshot.ImageSize.SMALL));
		Toast.makeText(context, R.string.toast_photo_three, Toast.LENGTH_SHORT).show();
		
		while(!isTwoFinished) {
			// Wait for two to finish
		}
		
		photoThree = rawPhotoThree.copy(rawPhotoThree.getConfig(), true);
		photoCanvas = new Canvas(photoThree);
		photoDrawable.draw(photoCanvas);
		photoCanvas = null;
		
		// Log.d(TAG, "Photo Three Complete");
		isThreeFinished = true;
		
	}

	public void takePhotoFour() {
		
		Bitmap rawPhotoFour = Bitmap.createBitmap(mirror.grabMirrorImage(Snapshot.ImageSize.SMALL));
		Toast.makeText(context, R.string.toast_photo_four, Toast.LENGTH_SHORT).show();
		
		while(!isThreeFinished) {
			// Wait for two to finish
		}
		
		photoFour = rawPhotoFour.copy(rawPhotoFour.getConfig(), true);
		photoCanvas = new Canvas(photoFour);
		photoDrawable.draw(photoCanvas);
		photoCanvas = null;
		photoDrawable = null;
		mirror = null;
		
		// Log.d(TAG, "Photo Four Complete");
		isFourFinished = true;
	
	}
	
	public void createPhotoStrip() {
		
		while(!isFourFinished) {
			// Wait for four to finish
		}
		
		int x1 = (rand.nextInt(10) + 16);
        int y1 = (rand.nextInt(10) + 16);
        int x2 = (40 - x1);
        int y2 = (40 - y1);
        int fh = (photoOne.getHeight()) / 2;
        int fw = (photoOne.getWidth()) / 2;
        int th = (4 * (fh + y1 + y2));
        int tw = (fw + x1 + x2);
        
        photoStrip = Bitmap.createBitmap(tw, th, Bitmap.Config.ARGB_8888);
        photoCanvas = new Canvas(photoStrip);
        photoCanvas.drawColor(0xffffffff);
        
        photoDrawable = new BitmapDrawable(resources, photoOne);
        photoDrawable.setBounds(x1, y1, fw + x1, fh + y1);
        photoDrawable.draw(photoCanvas);
        
        photoDrawable = new BitmapDrawable(resources, photoTwo);
        photoDrawable.setBounds(x1, (2 * y1) + (1 * fh) + (1 * y2), fw + x1, (2 * y1) + (2 * fh) + (1 * y2));
        photoDrawable.draw(photoCanvas);
        
        photoDrawable = new BitmapDrawable(resources, photoThree);
        photoDrawable.setBounds(x1, (3 * y1) + (2 * fh) + (2 * y2), fw + x1, (3 * y1) + (3 * fh) + (2 * y2));
        photoDrawable.draw(photoCanvas);
        
        photoDrawable = new BitmapDrawable(resources, photoFour);
        photoDrawable.setBounds(x1, (4 * y1) + (3 * fh) + (3 * y2), fw + x1, (4 * y1) + (4 * fh) + (3 * y2));
        photoDrawable.draw(photoCanvas);
        
        // Log.d(TAG, "Photo Strip Complete");
        isStripFinished = true;
	}
	
	public ContentValues savePhotoStrip() throws Exception {
		
		while(!isStripFinished) {
			// Wait for strip to finish
		}
		
		
		String dirPath = Environment.getExternalStorageDirectory().toString() + File.separator + "DCIM" + File.separator + resources.getString(R.string.folder_name);
		DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
        String fileName = "IMG_" + sdf.format(new Date()) + ".jpg";
        File captureFile = new File(dirPath);
        long currentTime = System.currentTimeMillis();
        
        if(!captureFile.exists())
            if(!captureFile.mkdirs())
                throw new Exception("Could not create folder: " + captureFile.getAbsolutePath());
            
        String filePath = captureFile.getAbsolutePath() + File.separator + fileName;
        FileOutputStream fos = null;
        ContentValues values = null;
        try {
            fos = new FileOutputStream(filePath);
            photoStrip.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            
            long size = new File(filePath).length();

            values = new ContentValues(8);
            values.put(Images.Media.TITLE, fileName);
            values.put(Images.Media.DISPLAY_NAME, fileName);
            values.put(Images.Media.DATE_TAKEN, currentTime);
            values.put(Images.Media.DESCRIPTION, "Taken using Front Camera Mirror by wckd Dev.");
            values.put(Images.Media.MIME_TYPE, "image/jpeg");
            values.put(Images.Media.ORIENTATION, 0);
            values.put(Images.Media.DATA, filePath);
            values.put(Images.Media.SIZE, size);
            
            // Log.d(TAG, "Photo Strip Saved");
    		Toast.makeText(context, R.string.toast_strip_complete, Toast.LENGTH_SHORT).show();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } 
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        return values;
	}
	

}
