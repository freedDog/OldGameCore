package com.game.script.loader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarLoader {
	private static Logger logger=LoggerFactory.getLogger(JarLoader.class);
	private JarClassLoader loader;
	public boolean loadJar(String name) {
		try {
			File file=new File(name);
			this.loader=new JarClassLoader(new URL[] {file.toURI().toURL()});
		}catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
	public boolean excuteJar(String name) {
		try {
			JarFile file=new JarFile(name);
			Manifest manifest=file.getManifest();
			String value=manifest.getMainAttributes().getValue("Main-Class");
			Class<?> clazz=this.loader.loadClass(value);
			
			Method method=clazz.getMethod("main",new Class[] {java.lang.String.class});
			method.invoke(null,new Object[] {new String[0]});
			file.close();
		}catch(Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
	public JarClassLoader getLoader() {
		return loader;
	}
	
}
