//
// Copyright (C) 2011, 2012 Mocean Mobile. All Rights Reserved. 
//
package com.MASTAdView.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

public class DeviceFeatures
{
	private Context context;
	
	
	private Boolean cacheSmsSupport 		= null;
	private Boolean cachePhoneSupport 		= null;
	//private Boolean cacheEmailSupport 		= null;
	private Boolean cacheCalendarSupport 	= null;
	private Boolean cachePictureSupport 	= null;
	private Boolean cacheVideoSupport 		= null;
	
	
	public DeviceFeatures(Context context)
	{
		this.context = context;
	}
	
	
	public boolean isSupported(MraidInterface.FEATURES feature)
	{
		switch(feature)
		{
		case SMS:
			return smsSupport();
		case PHONE:
			return phoneSupport();
		//case EMAIL:
			//return emailSupport();
		case CALENDAR:
			return calendarSupport();
		case STORE_PICTURE:
			return storePictureSupport();
		case INLINE_VIDEO:
			return inlineVideoSupport();
		}

		return false;
	}
	
	
	// Allow app developer to override system if desired
	public void setSupported(MraidInterface.FEATURES feature, Boolean value)
	{
		switch(feature)
		{
		case SMS:
			cacheSmsSupport = value;
		case PHONE:
			cachePhoneSupport = value;
		//case EMAIL:
			//return emailSupport();
		case CALENDAR:
			cacheCalendarSupport = value;
		case STORE_PICTURE:
			cachePictureSupport = value;
		case INLINE_VIDEO:
			cacheVideoSupport = value;
		}
	}

	
	public boolean smsSupport()
	{
		if (cacheSmsSupport == null)
		{
			cacheSmsSupport = context.checkCallingOrSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
			System.out.println("setSmsSupport: " + cacheSmsSupport);
		}
		
		return cacheSmsSupport;
	}
	
	
	public boolean phoneSupport()
	{
		if (cachePhoneSupport == null)
		{
			cachePhoneSupport = context.checkCallingOrSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
			System.out.println("setPhoneSupport: " + cachePhoneSupport);
		}
		
		return cachePhoneSupport;
	}
	
	
	/*
	public boolean emailSupport()
	{
		if (cacheEmailSupport == null)
		{
			cacheEmailSupport = context.checkCallingOrSelfPermission(android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
			System.out.println("setEmailSupport: " + cacheEmailSupport);
		}
		
		return cacheEmailSupport;
	}
	*/
	
	
	public boolean calendarSupport()
	{
		if (cacheCalendarSupport == null)
		{
			cacheCalendarSupport =
					((context.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) &&
					 (context.checkCallingOrSelfPermission(android.Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED));
			System.out.println("setCalendarSupport: " + cacheCalendarSupport);
		}
		
		return cacheCalendarSupport;
	}
	
	
	public String createCalendarInteractive(String description, String location,  String summary, String start, String end)
	{
		try
		{
			Intent intent = new Intent(Intent.ACTION_EDIT); 
			intent.setType("vnd.android.cursor.item/event"); 
			intent.putExtra("dtstart", parseDateString(start)); // Events.DTSTART from API 14
			intent.putExtra("dtend", parseDateString(end));		// Events.DTEND from API 14   
			intent.putExtra("title", summary);					// Events.TITLE from API 14
			intent.putExtra("eventLocation", location);			// Events.EVENT_LOCATION from API 14
			intent.putExtra("descriptoin", description);		// Events.DESCRIPTION from API 14
			
			context.startActivity(intent);
		}
		catch (Exception ex)
		{
			String error = "Error creating calendar: " + ex.getMessage();
			System.out.println(error);
			return error;
		}
			
		return null;
	}
	
	
	private SimpleDateFormat dateFormat = null;
	
	
	// Parse date strings
	private Long parseDateString(String input)
	{
		try
		{
			if (dateFormat == null)
			{
				// java.text.DateFormat df = DateFormat.getLongDateFormat(context);
				
				// spec format sample: '2011-03-24T09:00-08:00'
				dateFormat = new SimpleDateFormat("yyyy-MM-FF'T'HH:mmZ");
			}
			Date myDate = dateFormat.parse(input);
			return myDate.getTime();
		}
		catch(Exception ex)
		{
			System.out.println("Error parsing date: " + input + ", exception: " + ex.getMessage());
		}
		
		return System.currentTimeMillis();
	}
	
	
	/*
	public String createCalendarEvent(String description, String location,  String summary, String start, String end)
	{
		if (calendarSupport())
		{
			try
			{
				String[] projection = new String[] { "_id", "name" };

				Uri calendars = Uri.parse("content://calendar/calendars");
	            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
	            {
					calendars = Uri.parse("content://com.android.calendar/calendars");
	            }
				     
				Cursor managedCursor = ((Activity)context).managedQuery(calendars, projection, "selected=1", null, null);
				String calId; 
	
				if (managedCursor!= null && managedCursor.moveToFirst())
				{
					int idColumn = managedCursor.getColumnIndex("_id");
				    calId = managedCursor.getString(idColumn);
				}
				else
				{
					String error = "Create calendar event failed, could not find local calendar.";
					System.out.println(error);
					return error;
				}
				
				ContentValues event = new ContentValues();
				event.put(Events.CALENDAR_ID, calId);
				event.put(Events.TITLE, summary);
				event.put(Events.DESCRIPTION, description);
				event.put(Events.EVENT_LOCATION, location);
				
				Long ldate = parseDateString(start);
				event.put(Events.DTSTART, ldate);
				ldate = parseDateString(end);
				event.put(Events.DTEND, ldate);
				
				
				Uri eventsUri = Uri.parse("content://calendar/events");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
	            {
					eventsUri = Uri.parse("content://com.android.calendar/events");
	            }
	            
			    context.getContentResolver().insert(eventsUri, event);
			}
			catch (Exception e)
			{
				String error = "Excetion creating calendar event: " + e.getMessage();
				System.out.println(error);
				return error;
			}
		}
		else
		{
			String error = "Calendar not available";
			System.out.println(error);
			return error;
		}
		
		return null;
	}
	*/
	
	
	public boolean storePictureSupport()
	{
		if (cachePictureSupport == null)
		{
			cachePictureSupport = true; // XXX
			System.out.println("setPictureSupport: " + cachePictureSupport);
		}

		return cachePictureSupport;
	}
	
	
	public boolean inlineVideoSupport()
	{
		if (cacheVideoSupport == null)
		{
			// Honeycomb and later OS versions can playback inline with hardware support
			final int honeycomb = 11; // Build.VERSION_CODES.HONEYCOMB if using API 3.0 or above
			if ((Build.VERSION.SDK_INT >= honeycomb) && (context instanceof Activity))
			{
				// inline can work, must turn on hardware acceleration for web views, but can only
				// do this if our context is an activity which allows access to the window properties
				
				
				
				// XXX true when ready, currently this causes hw acceleration to be enabled in the web view, which cases some problems
				cacheVideoSupport = false;
				
				
				
			}
			else
			{
				cacheVideoSupport = false; // no inline video?
			}
			System.out.println("setVideoSupport: " + cacheVideoSupport);
		}

		return cacheVideoSupport;
	}
	
	
	public String playVideo(String videoUrl)
	{
		// Launch the play video intent and let android use the preferred player for this device/user
		Uri uri = Uri.parse(videoUrl);
		Intent i = new Intent(Intent.ACTION_VIEW, uri);
		if (i.resolveActivity(context.getPackageManager()) == null)
		{
			String error = "No video playback handler found, skipping..."; // XXX string
			System.out.println(error);
			return error;
		}
		
		context.startActivity(i);
		return null;
	}
}