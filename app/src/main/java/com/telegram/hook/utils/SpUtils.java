package com.telegram.hook.utils;

import android.content.SharedPreferences;
import android.util.Log;

import com.telegram.hook.App;

import java.util.Random;


/**SharedPreferences 的工具类*/
public class SpUtils {
	private static SharedPreferences sp;

	private static void getInstance() {
		sp = App.getContext().getSharedPreferences("config", 0);
	}

	public static void put(String key, Object value) {
		if (sp == null) {
			getInstance();
		}
		if(value==null)return;
		if (value instanceof String) {
			sp.edit().putString(key, (String) value).commit();
		} else if (value instanceof Integer) {
			sp.edit().putInt(key, (Integer) value).commit();
		} else if (value instanceof Boolean) {
			sp.edit().putBoolean(key, (Boolean) value).commit();
		} else if (value instanceof Float) {
			sp.edit().putFloat(key, (Float) value).commit();
		} else if (value instanceof Long) {
			sp.edit().putLong(key, (Long) value).commit();
		} else {
			sp.edit().putString(key, value.toString()).commit();
		}
	}

	public static Object get(String key, Object defaultObject) {
		if (sp == null) {
			getInstance();
		}
		if (defaultObject instanceof String) {
			return sp.getString(key, (String) defaultObject);
		} else if (defaultObject instanceof Integer) {
			return sp.getInt(key, (Integer) defaultObject);
		} else if (defaultObject instanceof Boolean) {
			return sp.getBoolean(key, (Boolean) defaultObject);
		} else if (defaultObject instanceof Float) {
			return sp.getFloat(key, (Float) defaultObject);
		} else if (defaultObject instanceof Long) {
			return sp.getLong(key, (Long) defaultObject);
		}
		
		return null;
	}
	
	public static String getUserPhone(){
		if (sp == null) {
			getInstance();
		}
		return sp.getString("userPhoneNum","");
	}

	public static void setUserPhone(String userPhone){
		if (sp == null) {
			getInstance();
		}
		sp.edit().putString("userPhoneNum",userPhone).apply();
	}
	public static int getPosition(){
		if (sp == null) {
			getInstance();
		}
		return sp.getInt("position",0);
	}

	public static void setPosition(int position){
		if (sp == null) {
			getInstance();
		}
		sp.edit().putInt("position",position).apply();
	}
	public  static void positionIncrease(){
		if (sp == null) {
			getInstance();
		}
         int currentPostion=getPosition()+1;
         setPosition(currentPostion);
	}
	/**
	 * 0 筛号码
	 * 1 发消息
	 * */
	public static void setMode(int mode){
		if (sp == null) {
			getInstance();
		}
		sp.edit().putInt("mode",mode).apply();
	}
	public static int getMode(){
		if (sp == null) {
			getInstance();
		}
		return sp.getInt("mode",0);
	}
	public static String getPhoneArea(){
		if (sp == null) {
			getInstance();
		}
		return sp.getString("phoneArea","");
	}

	public static void setPhoneArea(String pickArea){
		if (sp == null) {
			getInstance();
		}
		sp.edit().putString("phoneArea",pickArea).apply();
	}
	public static String getMsgPhoneList(){
		if (sp == null) {
			getInstance();
		}
		return sp.getString("msgPhoneList","");
	}

	public static void setMsgPhoneList(String msgPhoneList){
		if (sp == null) {
			getInstance();
		}
		sp.edit().putString("msgPhoneList",msgPhoneList).apply();
	}
	/**
	 * 重置启动时间
	 * 如果是0 就正常启动
	 * 否则 启动时间必须大于当前时间
	 * */
	public static void reSetLaunTime(){
		if (sp == null) {
			getInstance();
		}

		sp.edit().putLong("lanchTime",0).apply();
	}
	/**
	 * 随机生成一个启动时间
	 * */
	public static void setRandomLaunTime(){
		if (sp == null) {
			getInstance();
		}
		int min = new Random().nextInt(10) +25;
		Long lacntime = min * 60 * 1000L+System.currentTimeMillis();
		MyLog.writeException("lanchTime min="+min+" later");
		sp.edit().putLong("lanchTime",lacntime).apply();
	}
	public static long getRandomLaunTime(){
		if (sp == null) {
			getInstance();
		}
		return sp.getLong("lanchTime",0);
	}
}
