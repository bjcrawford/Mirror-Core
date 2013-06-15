package com.wckd_dev.mirror.core;

import java.util.ArrayList;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class FrameManager {

	private PackageManager packageMgr;
	private ArrayList<Integer> thumbResId;
	private ArrayList<Integer> frameResId;
	private ArrayList<String>  titleContent;
	private ArrayList<Resources> resParent;
	
	public FrameManager(PackageManager packageMgr) {
		this.packageMgr = packageMgr;
		thumbResId  = new ArrayList<Integer>();
		frameResId = new ArrayList<Integer>();
		titleContent = new ArrayList<String>();
		resParent = new ArrayList<Resources>();
	}
	
	public int getCount() {     
        return thumbResId.size();     
    }  
	
	public Drawable getThumbResId(int position) {
		Drawable result = null;
		if(position <= thumbResId.size() - 1)
			result = resParent.get(position).getDrawable(thumbResId.get(position));
		return result;
	}
	
	public Drawable getFrameResId(int position) {
		Drawable result = null;
		if(position <= frameResId.size() - 1)
			result = resParent.get(position).getDrawable(frameResId.get(position));
		return result;
	}
	
	public String getTitleContent(int position) {
		String result = "Out Of Range";
		if(position <= titleContent.size() - 1)
			result = titleContent.get(position);
		return result;
	}
	
	public void addFrame(Integer tResId, Integer fResId, String tContent, Resources rParent) {

		thumbResId.add(tResId);
		frameResId.add(fResId);
		titleContent.add(tContent);
		resParent.add(rParent);
	}

	protected boolean hasPack(String packageName) {
    	boolean result;
		try {
    	    packageMgr.getApplicationInfo(packageName, 0 );
    	    result = true;
    	} 
    	catch(PackageManager.NameNotFoundException e) { 
    		result = false;
    	}
		return result;
	}
	
	public boolean hasCFP1() {
		String packageName = "com.wckd_dev.mirrorCFP1";
    	boolean result = hasPack(packageName);
    	if(result) {
    		try {
				initCFP1(packageName);
			} 
    		catch (NameNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	return result;
    }
    
	protected void initCFP1(String packageName) throws NameNotFoundException {
		
	    ApplicationInfo info = packageMgr.getApplicationInfo(packageName, 0 );
		Resources res = packageMgr.getResourcesForApplication(info);

    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame_thumb4", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame4", null, null),
    			           res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP1:string/frame_chrome", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame_thumb5", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame5", null, null),
    			           res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP1:string/frame_fire_chrome", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame_thumb6", null, null),
		                   res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame6", null, null),
                           res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP1:string/frame_wood", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame_thumb7", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame7", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP1:string/frame_shiny_gold", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame_thumb8", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame8", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP1:string/frame_antique", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame_thumb9", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame9", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP1:string/frame_fire", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame_thumb10", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame10", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP1:string/frame_purple", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame_thumb11", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorCFP1:drawable/frame11", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP1:string/frame_layered_green", null, null)),
    					   res);
    }
    
	public boolean hasCFP2() {
		String packageName = "com.wckd_dev.mirrorCFP2";
    	boolean result = hasPack(packageName);
    	if(result) {
    		try {
				initCFP2(packageName);
    		} 
    		catch (NameNotFoundException e) {
    			e.printStackTrace();
    		}
    	}
    	return result;
    }
    
	protected void initCFP2(String packageName) throws NameNotFoundException {
		
	    ApplicationInfo info = packageMgr.getApplicationInfo(packageName, 0 );
		Resources res = packageMgr.getResourcesForApplication(info);
    	
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame_thumb12", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame12", null, null),
    			           res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP2:string/frame_ember", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame_thumb13", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame13", null, null),
		                   res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP2:string/frame_rainbow", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame_thumb14", null, null),
		                   res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame14", null, null),
                           res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP2:string/frame_green_laser", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame_thumb15", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame15", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP2:string/frame_cyan_laser", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame_thumb16", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame16", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP2:string/frame_purple_glow", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame_thumb17", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorCFP2:drawable/frame17", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorCFP2:string/frame_red_glow", null, null)),
    			           res);
    }
    
	public boolean hasFFP1() {
		String packageName = "com.wckd_dev.mirrorFFP1";
    	boolean result = hasPack(packageName);
    	if(result) {
    		try {
				initFFP1(packageName);
    		} 
    		catch (NameNotFoundException e) {
    			e.printStackTrace();
    		}
    	}
    	return result;
    }
    
	protected void initFFP1(String packageName) throws NameNotFoundException {
		
	    ApplicationInfo info = packageMgr.getApplicationInfo(packageName, 0 );
		Resources res = packageMgr.getResourcesForApplication(info);
    	
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame_thumb18", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame18", null, null),
    			           res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP1:string/frame_white_flowers", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame_thumb19", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame19", null, null),
		                   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP1:string/frame_white_purple_flowers", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame_thumb20", null, null),
		                   res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame20", null, null),
                           res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP1:string/frame_orange_flower", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame_thumb21", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame21", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP1:string/frame_pink_flower", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame_thumb22", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame22", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP1:string/frame_purple_flowers", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame_thumb23", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame23", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP1:string/frame_red_flower", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame_thumb24", null, null),
    					   res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame24", null, null),
    					   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP1:string/frame_orange_yellow_flowers", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame_thumb25", null, null),
    					   res.getIdentifier("com.wckd_dev.mirrorFFP1:drawable/frame25", null, null),
    					   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP1:string/frame_pink_green_flowers", null, null)),
    					   res);
    }
    
	public boolean hasFFP2() {
		String packageName = "com.wckd_dev.mirrorFFP2";
    	boolean result = hasPack(packageName);
    	if(result) {
    		try {
				initFFP2(packageName);
			} 
			catch (NameNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	return result;
    }
    
	protected void initFFP2(String packageName) throws NameNotFoundException {
		
	    ApplicationInfo info = packageMgr.getApplicationInfo(packageName, 0 );
		Resources res = packageMgr.getResourcesForApplication(info);
    	
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame_thumb26", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame26", null, null),
    			           res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP2:string/frame26", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame_thumb27", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame27", null, null),
		                   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP2:string/frame27", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame_thumb28", null, null),
		                   res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame28", null, null),
                           res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP2:string/frame28", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame_thumb29", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame29", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP2:string/frame29", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame_thumb30", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame30", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP2:string/frame30", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame_thumb31", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame31", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP2:string/frame31", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame_thumb32", null, null),
    					   res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame32", null, null),
    					   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP2:string/frame32", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame_thumb33", null, null),
    					   res.getIdentifier("com.wckd_dev.mirrorFFP2:drawable/frame33", null, null),
    					   res.getString(res.getIdentifier("com.wckd_dev.mirrorFFP2:string/frame33", null, null)),
    					   res);
    }
    
	public boolean hasNFP1() {
		String packageName = "com.wckd_dev.mirrorNFP1";
    	boolean result = hasPack(packageName);
    	if(result) {
    		try {
				initNFP1(packageName);
			} 
			catch (NameNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	return result;
    }
    
	protected void initNFP1(String packageName) throws NameNotFoundException {
		
	    ApplicationInfo info = packageMgr.getApplicationInfo(packageName, 0 );
		Resources res = packageMgr.getResourcesForApplication(info);
    	
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame_thumb34", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame34", null, null),
    			           res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP1:string/frame34", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame_thumb35", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame35", null, null),
		                   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP1:string/frame35", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame_thumb36", null, null),
		                   res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame36", null, null),
                           res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP1:string/frame36", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame_thumb37", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame37", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP1:string/frame37", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame_thumb38", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame38", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP1:string/frame38", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame_thumb39", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame39", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP1:string/frame39", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame_thumb40", null, null),
    					   res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame40", null, null),
    					   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP1:string/frame40", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame_thumb41", null, null),
    					   res.getIdentifier("com.wckd_dev.mirrorNFP1:drawable/frame41", null, null),
    					   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP1:string/frame41", null, null)),
    					   res);
    }
    
	public boolean hasNFP2() {
		String packageName = "com.wckd_dev.mirrorNFP2";
    	boolean result = hasPack(packageName);
    	if(result) {
    		try {
				initNFP2(packageName);
			} 
			catch (NameNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	return result;
    }
    
	protected void initNFP2(String packageName) throws NameNotFoundException {
		
	    ApplicationInfo info = packageMgr.getApplicationInfo(packageName, 0 );
		Resources res = packageMgr.getResourcesForApplication(info);
    	
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame_thumb42", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame42", null, null),
    			           res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP2:string/frame42", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame_thumb43", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame43", null, null),
		                   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP2:string/frame43", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame_thumb44", null, null),
		                   res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame44", null, null),
                           res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP2:string/frame44", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame_thumb45", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame45", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP2:string/frame45", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame_thumb46", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame46", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP2:string/frame46", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame_thumb47", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame47", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP2:string/frame47", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame_thumb48", null, null),
    					   res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame48", null, null),
    					   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP2:string/frame48", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame_thumb49", null, null),
    					   res.getIdentifier("com.wckd_dev.mirrorNFP2:drawable/frame49", null, null),
    					   res.getString(res.getIdentifier("com.wckd_dev.mirrorNFP2:string/frame49", null, null)),
    					   res);
    }
    
	public boolean hasVFP() {
		String packageName = "com.wckd_dev.mirrorVFP";
    	boolean result = hasPack(packageName);
    	if(result) {
    		try {
				initVFP(packageName);
			} 
			catch (NameNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	return result;
    }
    
	protected void initVFP(String packageName) throws NameNotFoundException {
		
	    ApplicationInfo info = packageMgr.getApplicationInfo(packageName, 0 );
		Resources res = packageMgr.getResourcesForApplication(info);
    	
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame_thumb50", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame50", null, null),
    			           res.getString(res.getIdentifier("com.wckd_dev.mirrorVFP:string/frame50", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame_thumb51", null, null),
    			           res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame51", null, null),
		                   res.getString(res.getIdentifier("com.wckd_dev.mirrorVFP:string/frame51", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame_thumb52", null, null),
		                   res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame52", null, null),
                           res.getString(res.getIdentifier("com.wckd_dev.mirrorVFP:string/frame52", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame_thumb53", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame53", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorVFP:string/frame53", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame_thumb54", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame54", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorVFP:string/frame54", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame_thumb55", null, null),
		           		   res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame55", null, null),
		           		   res.getString(res.getIdentifier("com.wckd_dev.mirrorVFP:string/frame55", null, null)),
    			           res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame_thumb56", null, null),
    					   res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame56", null, null),
    					   res.getString(res.getIdentifier("com.wckd_dev.mirrorVFP:string/frame56", null, null)),
    					   res);
    	addFrame(res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame_thumb57", null, null),
    					   res.getIdentifier("com.wckd_dev.mirrorVFP:drawable/frame57", null, null),
    					   res.getString(res.getIdentifier("com.wckd_dev.mirrorVFP:string/frame57", null, null)),
    					   res);
    }

}
