package com.wckd_dev.mirror.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class Snapshot {
	
	// private final String TAG = "wckd";
	
	public enum ImageSize {
		SMALL, MEDIUM, LARGE
	}
	
	private boolean isPhotoFinished = false;
	
	private Bitmap frame;
	private Bitmap photo;
	private Canvas photoCanvas;
	private Context context;
	private Drawable photoDrawable;
	private MirrorView mirror;
	private Resources resources;
	private View frameView;
	
	public Snapshot(MirrorView image, View frame, Context con) {
		context = con;
		mirror = image;
		resources = context.getResources();
		frameView = frame;
	}
	
	public void takePhoto(ImageSize size) {
		
		Bitmap rawPhoto = Bitmap.createBitmap(mirror.grabMirrorImage(size));
		Toast.makeText(context, R.string.toast_photo_taken, Toast.LENGTH_SHORT).show();
		
		frameView.setDrawingCacheEnabled(true);
		frameView.buildDrawingCache(true);
		frame = Bitmap.createBitmap(frameView.getDrawingCache());
		frameView.buildDrawingCache(false);
		photo = rawPhoto.copy(rawPhoto.getConfig(), true);
		photoCanvas = new Canvas(photo);
		photoDrawable = new BitmapDrawable(resources, frame);
		photoDrawable.setBounds(0, 0, photo.getWidth(), photo.getHeight());
		photoDrawable.draw(photoCanvas);
		photoCanvas = null;
		frameView = null;
		frame = null;
		
		// Log.d(TAG, "Photo Complete");
		isPhotoFinished = true;
		
	}
	
public ContentValues savePhoto() throws Exception {
		
		while(!isPhotoFinished) {
			// Wait for strip to finish
		}
		String dirPath = Environment.getExternalStorageDirectory().toString() + File.separator + "DCIM" + File.separator + resources.getString(R.string.folder_name);
        DateFormat sdf = SimpleDateFormat.getDateTimeInstance(); // new SimpleDateFormat("yyyyMMdd_HHmmss");
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
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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
            
            Toast.makeText(context, R.string.toast_photo_saved, Toast.LENGTH_SHORT).show();
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
