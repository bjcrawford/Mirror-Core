package com.wckd_dev.mirror.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter { 
	
    private LayoutInflater inflater; 
    private FrameManager fm;
    
    
    public ImageAdapter(Context c, FrameManager fm) { 
    	inflater = LayoutInflater.from(c);
    	this.fm = fm;
    }     
    
    public int getCount() {     
        return fm.getCount();     
    }     
    
    public Object getItem(int position) {     
        return null;     
    }  
    
    public long getItemId(int position) {     
        return 0;     
    }    
    
    class ViewHolder {  
        TextView title;  
        ImageView icon;  
    }
    
    // create a new ImageView for each item referenced     
    public View getView(int position, View convertView, ViewGroup parent) {     
    	ViewHolder holder;  
        if (convertView == null) {  // if it's not recycled,     
        	convertView = inflater.inflate(R.layout.framecontent, null);  
        	convertView.setLayoutParams(new GridView.LayoutParams(250, 400));  
        	holder = new ViewHolder();  
            holder.title = (TextView) convertView.findViewById(R.id.categoryText);  
            holder.icon = (ImageView )convertView.findViewById(R.id.categoryimage);  
            convertView.setTag(holder);  
        } 
        else {  
            holder = (ViewHolder) convertView.getTag();  
        }  

        holder.icon.setAdjustViewBounds(true);  
        holder.icon.setScaleType(ImageView.ScaleType.CENTER_CROP);     
        holder.icon.setPadding(8, 8, 8, 8);  
        holder.title.setText(fm.getTitleContent(position)); 
        holder.icon.setImageDrawable(fm.getThumbResId(position));
        
        return convertView;     
    }
}
