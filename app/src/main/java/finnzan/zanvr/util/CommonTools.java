package finnzan.zanvr.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CommonTools {

	public static List<LogEvent> mLogEvents = new ArrayList<LogEvent>();
	public static List<LogEvent> getLogEvents(){
		return mLogEvents;
	}

	public static void Log(String log){
		String source = "";
		StackTraceElement[] ss = Thread.currentThread().getStackTrace();
		StackTraceElement s = ss[3];
		String claasname = s.getClassName();
		try{
			claasname = claasname.substring(claasname.lastIndexOf(".") + 1);
		}catch (Exception ex){}
		source = claasname + "." + s.getMethodName();

		mLogEvents.add(0, new LogEvent(source, Thread.currentThread().getId(), log));
		Log.d("Radium", "[" + android.os.Process.myPid() + "] [" + Thread.currentThread().getId() + "] [" + source + "] " + log); // + " - [" + s.getFileName() + "(" + s.getLineNumber() + ")]");
	}

	public static Bitmap GetProfilePicture(Context context){
		try {
			String[] projections = new String[]{
					ContactsContract.Profile._ID,
					ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
					ContactsContract.Profile.LOOKUP_KEY,
					ContactsContract.Profile.PHOTO_THUMBNAIL_URI,
					ContactsContract.Profile.IS_USER_PROFILE
			};

			// Retrieves the profile from the Contacts Provider
			Cursor c = context.getContentResolver().query(
					ContactsContract.Profile.CONTENT_URI,
					projections,
					null,
					null,
					null);

			Uri photoUri = null;
			InputStream in = null;

			if (c != null && c.getCount() > 0) {
				int count = c.getCount();
				c.moveToFirst();
				do {
					int is_user_profile = c.getInt(c.getColumnIndex(ContactsContract.Profile.IS_USER_PROFILE));
					CommonTools.Log("[" + is_user_profile + "]" + c.getString(c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME_PRIMARY)) + ", " + c.getString(c.getColumnIndex(ContactsContract.Profile.PHOTO_THUMBNAIL_URI)));
					if (count == 1 || (count > 1 && is_user_profile == 1)) {
						photoUri = Uri.parse(c.getString(c.getColumnIndex(ContactsContract.Profile.PHOTO_THUMBNAIL_URI)));
					}
				} while (c.moveToNext());
				c.close();
			}

			if (photoUri != null) {
				CommonTools.Log("Profile picture URI [" + photoUri.toString() + "]");
				try {
					in = context.getContentResolver().openInputStream(photoUri);
				} catch (Exception ex) {
					CommonTools.HandleException(ex);
				}
			}

			return in == null ? null : BitmapFactory.decodeStream(in);
		}catch (Exception ex){
			CommonTools.HandleException(ex);
			return null;
		}
	}

	public static String GetMIME(String file){
		String filenameArray[] = file.split("\\.");
		String extension = filenameArray[filenameArray.length - 1];
		if(extension.equals("JPG"))  {
			return "image/jpeg";
		}else {
			String ret = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
			if (ret != null) {
				return ret;
			} else {
				return "unknown";
			}
		}
	}

	public static String URLEncode(String str){
		try {
			return URLEncoder.encode(str, "UTF8");
		}catch (Exception ex){
			CommonTools.HandleException(ex);
			return str;
		}
	}

	public static String URLDecode(String str){
		try {
			return URLDecoder.decode(str, "UTF8");
		}catch (Exception ex){
			CommonTools.HandleException(ex);
			return str;
		}
	}

	public static boolean OpenInDefaultApplication(Context context, String path){
		try {
			String mime = GetMIME(path);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(path)), mime);
			if (intent.resolveActivity(context.getPackageManager()) != null) {
				context.startActivity(intent);
			} else {
				CommonTools.Log("Unsupported format.");
				context.startActivity(Intent.createChooser(intent, "Open File").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
			return true;
		}catch (Exception ex){
			CommonTools.HandleException(ex);
			return false;
		}
	}

	public static void HandleException(Exception ex){
		Log.d("Radium", "Exception [" + ex.toString() + "]");

		/*
		Time t = new Time();
		t.setToNow();
		
		String strLog = "";				
		
		strLog += "\n\n== Exception ==" + "(" + t.minute + ":" + t.second + ")\n";
		StackTraceElement[] stack =  ex.getStackTrace();
		for(int i= stack.length-1; i >=0; i-- ){
			strLog += stack[i].getMethodName() + " " + stack[i].getFileName() + " " + stack[i].getLineNumber() + "\n";			
		}
		strLog += "[" + ex.toString() + "]\n";
		strLog += "=================";
		Log.d("Radium", strLog);

		mLogEvents.add(0, new LogEvent("", Thread.currentThread().getId(), strLog));*/
	}
}
