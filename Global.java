/**
 * Copyright &copy; 2012-2013 <a href="https://www.sunwiseinfo.com/wing_sv">wing_sv</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sunwiseinfo.wing_sv.common.config;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.common.collect.Maps;
import com.sunwiseinfo.wing_sv.common.utils.PropertiesLoader;
import com.sunwiseinfo.wing_sv.common.utils.SpringContextHolder;
import com.sunwiseinfo.wing_sv.modules.sys.dao.SecretConfigDao;

/**
 * 全局配置类
 * @author wing_sv
 * @version 2013-03-23
 */

public class Global {
	
	
	/**
	 * 保存全局属性值
	 */
	private static Map<String, String> map = Maps.newHashMap();
	
	/**
	 * 属性文件加载对象
	 */
	private static PropertiesLoader propertiesLoader = new PropertiesLoader("application.properties");
	
	/**
	 * 获取配置
	 */
	public static String getConfig(String key) {
		String value = map.get(key);
		if (value == null){
			value = propertiesLoader.getProperty(key);
			map.put(key, value);
		}
		return value;
	}

	/////////////////////////////////////////////////////////
	
	/**
	 * 获取管理端根路径
	 */
	public static String getAdminPath() {
		return getConfig("adminPath");
	}
	
	/**
	 * 获取前端根路径
	 */
	public static String getFrontPath() {
		return getConfig("frontPath");
	}
	
	/**
	 * 获取URL后缀
	 */
	public static String getUrlSuffix() {
		return getConfig("urlSuffix");
	}
	
	/**
	 * 是否是演示模式，演示模式下不能修改用户、角色、密码、菜单、授权
	 */
	public static Boolean isDemoMode() {
		String dm = getConfig("demoMode");
		return "true".equals(dm) || "1".equals(dm);
	}
	/**
	 * 上传路径
	 * @return
	 */
	public static String uploadPath(){
		
		return getConfig("uploadPath");
	}
	/**
	 * 限制上传文件大小
	 * @return
	 */
	public static String uploadSize(){
		
		return getConfig("uploadSize");
	}
	
	public static boolean getSecret(){
		
		if(map.get("secret")!=null){
			if(map.get("secret").equals("1")){
				return true;
			}else{
				return false;
			}
			
		}else{
			String sql="select value from t_sec_config where type='noCopySwitch'";
			SecretConfigDao dao=SpringContextHolder.getBean("secretConfigDao");
			Session session=dao.getSession();
			Query query=session.getSessionFactory().openSession().createSQLQuery(sql);
			List list=query.list();
			if(list !=null && list.size()>0){
				if(((String) list.get(0)).equals("1")){
					map.put("secret", (String) list.get(0));
					return true;
				}else{
					map.put("secret", (String) list.get(0));
					return false;
				}
			}
			//默认属性
			map.put("secret", "0");
			return false;
		}
		
	}
	
	public static void setSecreat(String flag){
		map.put("secret", flag);
	}
	
	/**
	 * 创建文件名称，避免出现文件覆盖
	 * 格式是（文件名+数字后缀）
	 * @param name文件路径名称
	 * @return
	 */
	public static String getFileName(String name){
			String newName=new String(name);
			int count=2;
			File file = new File(newName);
			new File(file.getParent()).mkdirs();
			while(file.exists()){
				newName=name.substring(0,name.lastIndexOf('.'))+"("+count+")."+name.substring(name.lastIndexOf('.')+1);
				count++;
				file=new File(newName);
			}
			return newName;
	}
}
