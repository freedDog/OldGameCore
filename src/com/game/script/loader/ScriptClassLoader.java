package com.game.script.loader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptClassLoader extends ClassLoader{
	private static Logger logger=LoggerFactory.getLogger(ScriptLoader.class);
	public ScriptClassLoader(ClassLoader classLoader) {
		super(classLoader);
	}
	public Class<?> loadScriptClass(String name){
		try {
			byte[] bs=this.loadByteCode(name);
			return super.defineClass(name, bs, 0,bs.length);
		}catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	private byte[] loadByteCode(String name) throws IOException {
		int iRead=0;
		String classFileName="scriptbin/"+name.replace('.', '/')+".class";
		FileInputStream in=null;
		ByteArrayOutputStream buffer=null;
		try {
			in=new FileInputStream(classFileName);
			buffer=new ByteArrayOutputStream();
			while((iRead=in.read())!=-1) {
				buffer.write(iRead);
			}
			return buffer.toByteArray();
		}finally {
			try {
				if(in!=null) {
					in.close();
				}
			}catch (Exception e) {
				logger.error(e.getMessage());
			}
			try {
				if(buffer!=null) {
					buffer.close();
				}
			}catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
}
