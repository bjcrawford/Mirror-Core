package com.wckd_dev.mirror.core;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DialogManager {
	
	public static final String TAG = "wckd";

	public static boolean buildQustomDialog(final MirrorActivity mirrorActivity, int id, QustomDialogBuilder builder) {
		boolean result = true;
		switch(id) {   
	    
	    /* Frames */
	    	case MirrorActivity.FRAME_STYLE_DIALOG:
	
	        	LayoutInflater inflater = (LayoutInflater) mirrorActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
	        	View layout = inflater.inflate(R.layout.framedialog,(ViewGroup) mirrorActivity.findViewById(R.id.layout_root));  
	        	
	            GridView gridview = (GridView)layout.findViewById(R.id.gridview);     
	            gridview.setAdapter(new ImageAdapter(mirrorActivity, mirrorActivity.frameMgr));     
	              
	            gridview.setOnItemClickListener(new OnItemClickListener() {  // On gridview item click
	                public void onItemClick(AdapterView<?> parent, View v,int position, long id) {     
	                	mirrorActivity.dialog.dismiss(); // Close frame style dialog
	                	mirrorActivity.setFrame(position); // Set frame 
	                }     
	            });  
	          
	            ImageView close = (ImageView) layout.findViewById(R.id.custom_close);  
	            close.setOnClickListener(new View.OnClickListener() {  // On close button click
	            	public void onClick(View v){  
	            		mirrorActivity.dialog.dismiss();  // Close frame style dialog
	            	}  
	            });  
	            
	            Button framesButton = (Button) layout.findViewById(R.id.add_ons_button);
	            framesButton.setOnClickListener(new View.OnClickListener() {
	            	public void onClick(View v) {
	            		
	    				Uri uriUrl = Uri.parse(MirrorActivity.frameLink);
	    		        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
	    		        mirrorActivity.startActivity(launchBrowser); // On click, Send user to wckd_dev apps
	    		        mirrorActivity.finish();
	                }
	            });
	            builder.setView(layout);  // Set builder to custom view
	            break;
	
	    /* Zoom */
	    	case MirrorActivity.ZOOM_DIALOG:
	    		
	    		if(mirrorActivity.isZoomSupported) {
	
	    	        LinearLayout zoomLinear = new LinearLayout(mirrorActivity);
	        		zoomLinear.setOrientation(1);
	        		SeekBar zoomSeek = new SeekBar(mirrorActivity);
	        		mirrorActivity.initZoomSeekBar(zoomSeek);
	        		zoomLinear.addView(zoomSeek);
	        		
	        		builder
	        		.setTitle(mirrorActivity.getString(R.string.dialog_zoom_title))
	        		.setView(zoomLinear)
	        		.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int whichButton) {
	        				
	        			}
	        		});
	    		}
	    		else
	        		Toast.makeText(mirrorActivity.getApplicationContext(), R.string.toast_no_zoom, Toast.LENGTH_SHORT).show();
	    		break;
	
	    /* Exposure */
	    	case MirrorActivity.EXPOSURE_DIALOG:
	    		
	    		if(mirrorActivity.isExposureSupported) {
	    			
	    			LinearLayout expLinear = new LinearLayout(mirrorActivity);
	        		expLinear.setOrientation(1);
	        		SeekBar expSeek = new SeekBar(mirrorActivity);
	        		mirrorActivity.initExpSeekBar(expSeek);
	        		expLinear.addView(expSeek);
	        		
	        		builder
	        		.setTitle(mirrorActivity.getString(R.string.dialog_exposure_title))
	        		.setView(expLinear)
	        		.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int whichButton) {
	        				
	        			}
	        		});
	    		}
	    		else
		    		Toast.makeText(mirrorActivity.getApplicationContext(), R.string.toast_no_exposure, Toast.LENGTH_SHORT).show();
	    		break;
	
	    /* Theme Change */
	    	case MirrorActivity.THEME_DIALOG:
	    		
	    		builder
	    		.setMessage(R.string.dialog_theme_text)	
	    		.setTitle(mirrorActivity.getString(R.string.dialog_theme_title))
	    		.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				Editor editor = mirrorActivity.appSettings.edit();
	    				editor.putString(MirrorActivity.APP_PREFERENCES_THEME, Integer.toString(mirrorActivity.themePref));
	    				editor.commit();
	    				mirrorActivity.recreate();
	    			}
	    		})
	    		.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				// On click, no action. Dialog closed
	    			}
	    		});
	    		builder.setMessageGravity(Gravity.CENTER);
		
	    		break;
	
	    //  TODO - Custom dialog needed with links to web site
	    /* App Info */
	    	case MirrorActivity.APP_INFO_DIALOG:
	        	

	    		if(mirrorActivity.version.compareTo("free") != 0) {
		    		builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int whichButton) {
		    				// No action
		    			}
		    		});
	    		}
	    		else{
		    		builder.setPositiveButton(R.string.button_upgrade_paid, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int whichButton) {
		    				Uri uriUrl = Uri.parse(MirrorActivity.upgradePaidLink);
		    		        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		    		        mirrorActivity.startActivity(launchBrowser); 
		    		        mirrorActivity.finish();
		    			}
		    		});
	    		}
	    		
	    		builder
	    		.setMessage(mirrorActivity.dialogAppInfoText)	
	    		.setTitle(mirrorActivity.getString(R.string.dialog_app_info_title))
	    		.setNeutralButton(R.string.button_frames, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				Uri uriUrl = Uri.parse(MirrorActivity.frameLink);
	    		        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
	    		        mirrorActivity.startActivity(launchBrowser);
	    		        mirrorActivity.finish();
	    			}
	    		})
	    		.setNegativeButton(R.string.button_rate, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				Uri uriUrl = Uri.parse(MirrorActivity.rateLink);
	    		        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
	    		        mirrorActivity.startActivity(launchBrowser);
	    		        mirrorActivity.finish();
	    			}
	    		});
	    		builder.setMessageGravity(Gravity.CENTER);
	    		
	    		break;
	
	    // TODO - Help should link to website page
	    /* Help */
	    	case MirrorActivity.HELP_DIALOG:
	        	
	    		builder
	    		.setMessage(R.string.dialog_help_text)	
	    		.setTitle(mirrorActivity.getString(R.string.dialog_help_title))
	    		.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				// On click, no action. Dialog closed
	    			}
	    		})
	    		.setNeutralButton(R.string.button_bug, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				mirrorActivity.sendBugReport("Mirror - Bug Report");
					}
	    		});
	    		if(mirrorActivity.version.compareTo("free") == 0) {
	    			builder.setNegativeButton(R.string.button_upgrade_paid, new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int whichButton) {
	    					Uri uriUrl = Uri.parse(MirrorActivity.upgradePaidLink);
	    					Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
	    					mirrorActivity.startActivity(launchBrowser); // On click, Send user to Mirror (paid)
	    					mirrorActivity.finish();
	    				}
	    			});
	    		}
	    		break;
	    		
	    /* No Front Camera Found */
	    	case MirrorActivity.FRONT_CAMERA_NOT_FOUND:
	        	
	    		builder
	    		.setMessage(R.string.dialog_no_front_camera_text)	
	    		.setTitle(mirrorActivity.getString(R.string.dialog_no_front_camera_title))
	    		.setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				mirrorActivity.finish();
	    			}
	    		})
	    		.setNeutralButton(R.string.button_try, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				
	    				
	    			}
	    		})
	    		.setNegativeButton(R.string.button_bug, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				mirrorActivity.sendBugReport("Mirror - No Front Camera Found");
	    			}
	    		});
	    		break;
	
	    /* No Camera Found */
	    	case MirrorActivity.CAMERA_NOT_FOUND: 
	        	
	    		builder
	    		.setMessage(R.string.dialog_no_camera_text)	
	    		.setTitle(mirrorActivity.getString(R.string.dialog_no_camera_title))
	    		.setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				mirrorActivity.finish();
	    			}
	    		})
	    		.setNeutralButton(R.string.button_try, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				
	    				
	    			}
	    		})
	    		.setNegativeButton(R.string.button_bug, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				mirrorActivity.sendBugReport("Mirror - No Camera Found");
	    			}
	    		});
	    		break;
	
	    /* Upgrade */
	    	case MirrorActivity.UPGRADE:
	    		
	    		builder
	    		.setMessage(R.string.dialog_upgrade_text)	
	    		.setTitle(mirrorActivity.getString(R.string.dialog_upgrade_title))
	    		.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				// On click, no action. Dialog closed
	    			}
	    		})
	    		.setNegativeButton(R.string.button_upgrade_paid, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				Uri uriUrl = Uri.parse(MirrorActivity.upgradePaidLink);
	    		        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
	    		        mirrorActivity.startActivity(launchBrowser); // On click, Send user to wckd_dev apps
	    		        mirrorActivity.finish();
	    			}
	    		});
	    		builder.setMessageGravity(Gravity.CENTER);
	    		break;
	    		
	    /* Rate */
	    	case MirrorActivity.RATE:
	    		
	    		builder
	    		.setMessage(R.string.dialog_rate_text)	
	    		.setTitle(mirrorActivity.getString(R.string.dialog_rate_title))
	    		.setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				mirrorActivity.finish();
	    			}
	    		})
	    		.setNeutralButton(R.string.button_upgrade_paid, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				Uri uriUrl = Uri.parse(MirrorActivity.upgradePaidLink);
	    		        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
	    		        mirrorActivity.startActivity(launchBrowser); // On click, Send user to wckd_dev apps
	    		        mirrorActivity.finish();
	    			}
	    		})
	    		.setNegativeButton(R.string.button_rate, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				mirrorActivity.hasRatedPref = 1;
	    				Uri uriUrl = Uri.parse(MirrorActivity.rateLink);
	    		        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
	    		        mirrorActivity.startActivity(launchBrowser); // On click, Send user to rate mirror
	    		        mirrorActivity.finish();
	    			}
	    		});
	    		builder.setMessageGravity(Gravity.CENTER);
	    		break;
	    	
	    	default:     
	    		mirrorActivity.dialog = null;     
	    }		
    		
	   mirrorActivity.dialog = builder.show();
			
       if(id == MirrorActivity.EXPOSURE_DIALOG && mirrorActivity.isExposureSupported) {
        	Window window = mirrorActivity.dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

    		wlp.gravity = Gravity.BOTTOM;
    		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
    		wlp.alpha = 0.85f;
    		window.setAttributes(wlp);
        }
        else if(id == MirrorActivity.ZOOM_DIALOG && mirrorActivity.isZoomSupported) {
            Window window = mirrorActivity.dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

    		wlp.gravity = Gravity.BOTTOM;
    		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
    		wlp.alpha = 0.85f;
    		window.setAttributes(wlp);
        }
        
        return result;
	}
	
	public static boolean buildDialog(final MirrorActivity mirrorActivity, int id) {
		boolean result = true;
		
		mirrorActivity.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		switch(id) {   
		
	    /* WelcomeOne */
	    	case MirrorActivity.WELCOME_ONE_DIALOG:

	    		mirrorActivity.dialog.setContentView(R.layout.welcome_one);
	    		break;
	    		
	    /* WelcomeTwo */
	    	case MirrorActivity.WELCOME_TWO_DIALOG:

	    		mirrorActivity.dialog.setContentView(R.layout.welcome_two);
	    		break;
	    		
	    /* Pause */
	    	case MirrorActivity.PAUSE_DIALOG:

	    		mirrorActivity.dialog.setContentView(R.layout.pause);
	    		break;
	    		
	    /* Snapshot */
	    	case MirrorActivity.SNAPSHOT_DIALOG:

	    		mirrorActivity.dialog.setContentView(R.layout.snapshot);
	    		break;
	    		
	    /* Photostrip */
	    	case MirrorActivity.PHOTOSTRIP_DIALOG:

	    		mirrorActivity.dialog.setContentView(R.layout.photostrip);
	    		break;
	    	
	    	default:  

	    		mirrorActivity.dialog.setContentView(R.layout.generic);       
	    }	
		
		mirrorActivity.dialog.show();
     	Window window = mirrorActivity.dialog.getWindow();
     	window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();

 		wlp.gravity = Gravity.CENTER;
 		wlp.dimAmount = 0.5f;
 		window.setAttributes(wlp);
        
        return result;
	}
	
}
