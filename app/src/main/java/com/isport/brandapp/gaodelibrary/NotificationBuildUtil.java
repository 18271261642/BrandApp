package com.isport.brandapp.gaodelibrary;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;


/**
 * 巡河消息通知，后台service设置为前台服务
 * Created by ly on 2018/5/8.
 */
public class NotificationBuildUtil {
	private static NotificationManager manager;
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static Notification showNotification(Context context, String timeStr, String disStr, Class<?> startClass, int resIdIcon ) {



		Notification.Builder builder = new Notification.Builder(context.getApplicationContext()); //获取一个Notification构造器
		//Intent nfIntent = new Intent(context, InDoorSportActivity.class);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//修改安卓8.1以上系统报错
			NotificationChannel notificationChannel = new NotificationChannel("notification_id", "bonlala", NotificationManager.IMPORTANCE_MIN);
			notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
			notificationChannel.setShowBadge(false);//是否显示角标
			notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
			manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.createNotificationChannel(notificationChannel);
			builder.setChannelId("notification_id");
		}
		builder.setAutoCancel(true);//用户点击就自动消失
		builder // 设置 PendingIntent
				.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), com.today.step.lib.R.drawable.ic_t_launcher)) // 设置下拉列表中的图标(大图标)
				.setContentTitle("") // 设置下拉列表里的标题
				.setSmallIcon(com.today.step.lib.R.drawable.ic_t_launcher) // 设置状态栏内的小图标
				.setContentText("") // 设置上下文内容
				.setWhen(System.currentTimeMillis()); // 设置该通知发生的时间*/
		Notification notification = builder.build(); // 获取构建好的Notification
		notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
		// notiManager.notify(110 /* ID of notification */, notification);  //这就是那个
		// 参数一：唯一的通知标识；参数二：通知消息。

		/*manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent inten = new Intent(context, startClass);
		PendingIntent pd = PendingIntent.getActivity(context, 0, inten, PendingIntent.FLAG_UPDATE_CURRENT);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout);// 获取remoteViews（参数一：包名；参数二：布局资源）

		remoteViews.setImageViewResource(R.id.logoIV,resIdIcon);

		if("00:00:00".equals(timeStr)) {
			remoteViews.setViewVisibility(R.id.timeLy, View.GONE);
			remoteViews.setViewVisibility(R.id.distanceLy, View.GONE);
			remoteViews.setViewVisibility(R.id.isgoingTv,View.VISIBLE);
			remoteViews.setTextViewText(R.id.isgoingTv,getAppName(context)+"正在运行");
		}else{
			remoteViews.setViewVisibility(R.id.timeLy, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.distanceLy, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.isgoingTv,View.GONE);
			remoteViews.setTextViewText(R.id.timeTv,timeStr);
			remoteViews.setTextViewText(R.id.disTv,disStr);
		}
		*//*
		 * SKD中API Level高于11低于16
		 *//*
		int sdk_int = Build.VERSION.SDK_INT;
		*//*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			resIdIcon = R.mipmap.logo_bozhou;
		}else{
			resIdIcon = R.mipmap.logo_bozhou;
		}*//*
		if ( sdk_int >= Build.VERSION_CODES.HONEYCOMB
				&& sdk_int < Build.VERSION_CODES.JELLY_BEAN) {
			Notification.Builder builder1 = new Notification.Builder(context).setContent(remoteViews);

			builder1.setSmallIcon(resIdIcon); // 设置图标
			builder1.setWhen(System.currentTimeMillis()); // 发送时间
			builder1.setDefaults(Notification.FLAG_FOREGROUND_SERVICE); // 设置前台
			builder1.setAutoCancel(false);// 打开程序后图标消失
			builder1.setContentIntent(pd);
			Notification notification = builder1.getNotification();
			notification.flags |= Notification.FLAG_NO_CLEAR;
//			manager.notify(R.string.app_name, notification); //
			return notification;
		}
		*//*
		 * SKD中API Level高于16
		 *//*
		else if (sdk_int >= Build.VERSION_CODES.JELLY_BEAN && sdk_int < Build.VERSION_CODES.O ) {
			// 新建状态栏通知
			Notification.Builder baseNF1 = new Notification.Builder(context).setContent(remoteViews);
			baseNF1.setSmallIcon(resIdIcon);
			baseNF1.setAutoCancel(true);
			baseNF1.setAutoCancel(false);
			baseNF1.setDefaults(Notification.FLAG_FOREGROUND_SERVICE);
			baseNF1.setWhen(System.currentTimeMillis());
			baseNF1.setContentIntent(pd);
			Notification notification = baseNF1.build();// 获取一个Notification
			notification.flags |= Notification.FLAG_NO_CLEAR;
			return notification;
		}else if(sdk_int >= Build.VERSION_CODES.O){

			NotificationChannel channel = new NotificationChannel("notification_id",
					"Channel2", NotificationManager.IMPORTANCE_DEFAULT);
			channel.enableLights(false);
			channel.setLightColor(Color.GREEN);
			channel.setShowBadge(false);
//			 channel.setSound(null,null);
			channel.setVibrationPattern(null);
			channel.enableVibration(false);
			manager.createNotificationChannel(channel);
			Notification.Builder builderAndroidO = new Notification.Builder(context,"notification_id").setContent(remoteViews);
			builderAndroidO
					.setSmallIcon(resIdIcon)
					.setAutoCancel(false)
					.setWhen(System.currentTimeMillis())
					.setSound(null)
					.setVibrate(null)
					.setOnlyAlertOnce(true)
					.setContentIntent(pd);
			builderAndroidO.setDefaults(Notification.FLAG_FOREGROUND_SERVICE); // 设置前台
			Notification notification = builderAndroidO.build();// 获取一个Notification
			notification.defaults = 0;//
			notification.flags |= Notification.FLAG_NO_CLEAR;*/
			return notification;

	}

	public static void clearNotification(){
		if(manager != null){
			manager.cancelAll();
		}
	}

	/**
	 * 获取应用程序名称
	 */
	public static synchronized String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
