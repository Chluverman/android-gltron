package inc.flide.touchboard.logging;

import android.util.Log;

public class Logger implements LoggingConstants
{
	public static String className;
	private static final String tag = LoggingConstants.Project_Name;

	public static void setTag(String nameOfClass)
	{
		className = nameOfClass;
	}

	public static void Debug(String message)
	{
		 if(LoggingConstants.DEBUG == true)
			 Log.d(tag, className + " : " + message);
	}

	public static void Debug(String message, Throwable tr)
	{
		 if(LoggingConstants.DEBUG == true)
			 Log.d(tag, className + " : " + message, tr);
	}

	public static void Error(String message)
	{
		 if(LoggingConstants.ERROR == true)
			 Log.e(tag, className + " : " + message);
	}

	public static void Error(String message, Throwable tr)
	{
		 if(LoggingConstants.ERROR == true)
			 Log.e(tag, className + " : " + message, tr);
	}

	public static void Info(String message)
	{
		 if(LoggingConstants.INFO == true)
			 Log.i(tag, className + " : " + message);
	}

	public static void Info(String message, Throwable tr)
	{
		 if(LoggingConstants.INFO == true)
			 Log.i(tag, className + " : " + message, tr);
	}
	
	public static void Verbose(String message)
	{
		 if(LoggingConstants.VERBOSE == true)
			 Log.v(tag, className + " : " + message);
	}

	public static void Verbose(String message, Throwable tr)
	{
		 if(LoggingConstants.VERBOSE == true)
			 Log.v(tag, className + " : " + message, tr);
	}

	public static void Warn(String message)
	{
		 if(LoggingConstants.WARN == true)
			 Log.w(tag, className + " : " + message);
	}

	public static void Warn(Throwable tr)
	{
		 if(LoggingConstants.WARN == true)
			 Log.w(tag, tr);
	}

	public static void Warn(String message, Throwable tr)
	{
		 if(LoggingConstants.WARN == true)
			 Log.w(tag, className + " : " + message, tr);
	}

}
