package com.game.script.loader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.game.script.IScript;
import com.game.utils.CharsetUtil;

public class ScriptLoader {
	private static Logger logger=LoggerFactory.getLogger(ScriptLoader.class);
	private String outPath="scriptbin";
	private String sourcePath="scripts";
	private String jarPath="jars";
	private ConcurrentHashMap<Integer, IScript> scripts=new ConcurrentHashMap<>();
	private URLClassLoader appClassLoader;
	private URLClassLoader parentClassLoader;
	private String classpath;
	public ScriptLoader() {
		this(null,null,null);
	}
	public ScriptLoader(String sourcePath,String jarPath,String outPath) {
		if(sourcePath!=null&&!sourcePath.isEmpty()) {
			this.sourcePath=sourcePath;
		}
		if(jarPath!=null&&!jarPath.isEmpty()) {
			this.jarPath=jarPath;
		}
		if(outPath!=null&&!outPath.isEmpty()) {
			this.outPath=outPath;
		}
		this.appClassLoader=(URLClassLoader) getClass().getClassLoader();
		this.parentClassLoader=this.appClassLoader;
		this.buildClassPath();
	}
	public int loadJar(String name) {
		JarLoader loader=new JarLoader();
		if(loader.loadJar(this.jarPath+"/"+name)) {
			this.parentClassLoader=loader.getLoader();
			buildClassPath();
			HashMap<String, String> files=this.loadAllJavaScript();
			if(files!=null&&files.size()>0) {
				if(this.load(files,true)) {
					if(loader.excuteJar(this.jarPath+"/"+name)) {
						return 0;
					}else {
						return 3;
					}
				}else {
					return 2;
				}
			}else {
				return 2;
			}
		}
		return 1;
	}
	public boolean load(boolean redefine) {
		File file=new File(this.outPath);
		if(!file.exists()) {
			file.mkdirs();
		}
		HashMap<String, String> files=this.loadAllJavaScript();
		if(files!=null&&files.size()>0) {
			return load(files, redefine);
		}
		return false;
	}
	public boolean load(int id) {
		IScript script=this.scripts.get(id);
		if(null==script) {
			return false;
		}
		return this.load(script.getClass().getName());
	}
	public boolean load(String javaName) {
		try {
			HashMap<String, String> files=this.loadJavaScript(javaName);
			return this.load(files, true);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		return false;
	}
	public void addScript(IScript script) {
		this.scripts.put(script.getId(), script);
	}
	public IScript getScript(int id) {
		return this.scripts.get(id);
	}
	private boolean load(HashMap<String, String> files,boolean redefine) {
		File file=new File(this.outPath);
		if(!file.exists()) {
			file.mkdirs();
		}
		try {
			HashMap<String,String> loads=new HashMap<>();
			Iterator<Entry<String, String>> iterator=files.entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, String> entry=iterator.next();
				String name=entry.getKey();
				String content=entry.getValue();
				loads.put(name,content);
			}
			Iterator<Entry<String, String>> fileIter=loads.entrySet().iterator();
			while(fileIter.hasNext()) {
				Map.Entry<String,String> entry=fileIter.next();
				String name=entry.getKey();
				logger.debug("start load "+name);
			}
			HashMap<String,Class<?>> classes=this.javaCodeToObject(loads.entrySet(), redefine);
			Iterator<Entry<String, Class<?>>> iter=classes.entrySet().iterator();
			while(iter.hasNext()) {
				Map.Entry<String,Class<?>> entry=iter.next();
				String name=entry.getKey();
				Class<?> clazz=entry.getValue();
				IScript script=(IScript)clazz.newInstance();
				this.addScript(script);
				logger.debug("load success"+name);
			}
			return true;
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		return false;
	}
	private HashMap<String, Class<?>> javaCodeToObject(Set<Map.Entry<String, String>> fileSet,boolean redefine) throws Exception{
		HashMap<String,Boolean> reloads=new HashMap<>();
		HashSet<String> successes=new HashSet<>();
		List<JavaFileObject> jfiles=new ArrayList<>();
		HashMap<String,Class<?>> classs=new HashMap<>();
		Iterator<Entry<String, String>> fileIter=fileSet.iterator();
		while(fileIter.hasNext()) {
			Map.Entry<String, String> entry=fileIter.next();
			String name=entry.getKey();
			String code=entry.getValue();
			boolean reload=false;
			try {
				Class<?> c=Class.forName(name,false,this.parentClassLoader);
				if(c!=null) {
					reload=true;
					if(!redefine) {
						classs.put(name, c);
					}
				}
			}catch(Exception e) {
				reloads.put(name, Boolean.valueOf(reload));
				JavaFileObject jfile=new JavaSourceFromString(name, code);
				jfiles.add(jfile);
			}
		}
		if(jfiles.isEmpty()) {
			return classs;
		}
		StandardJavaFileManager fileManager=null;
		try {
			JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
			DiagnosticCollector<Object> diagnosticCollector=new DiagnosticCollector<>();
			fileManager =compiler.getStandardFileManager(diagnosticCollector, null, null);
			ScriptClassLoader loader=new ScriptClassLoader(this.parentClassLoader);
			List<String> options=new ArrayList<>();
			options.add("-encoding");
			options.add(CharsetUtil.UTF_8);
			options.add("-classpath");
			options.add(this.classpath);
			options.add("-d");
			options.add(this.outPath);
			JavaCompiler.CompilationTask task=compiler.getTask(null, fileManager, diagnosticCollector, options,null, jfiles);
			
			boolean success=task.call().booleanValue();
			String name;
			if(success) {
				Iterator<Entry<String, String>> fileIter2=fileSet.iterator();
				while(fileIter2.hasNext()) {
					Map.Entry<String, String> entry=fileIter2.next();
					name=entry.getKey();
					logger.info("loading "+name);
					boolean reload=reloads.get(name).booleanValue();
					List<String> innerClasses=this.getInnerClasses(name);
					if(reload) {
						for(String className:innerClasses) {
							try {
								Class<?> c=Class.forName(className,false,this.parentClassLoader);
								if(c!=null) {
									loader.loadScriptClass(className);
								}
							}catch (Exception e) {
								logger.error(e.getMessage());
							}
							classs.put(name,loader.loadScriptClass(name));
						}
					}else {
						for(String className:innerClasses) {
							loader.loadScriptClass(className);
						}
						classs.put(name, loader.loadScriptClass(name));
					}
					successes.add(name);
				}
				return classs;
			}
			String error="";
			for(Diagnostic<?> dia:diagnosticCollector.getDiagnostics()) {
				error=error+this.compilePrint(dia);
			}
			logger.error(error);
		}finally {
			if(fileManager!=null) {
				fileManager.close();
			}
			Iterator<Entry<String, String>> fileIter3=fileSet.iterator();
			while(fileIter3.hasNext()) {
				Map.Entry<String, String> entry=fileIter3.next();
				String name=entry.getKey();
				boolean reload=reloads.get(name).booleanValue();
				logger.info("loader "+name+(successes.contains(name)?"success":"failure"));
			}
		}
		return null;
	}
	private void buildClassPath() {
		this.classpath=null;
		StringBuilder sb=new StringBuilder();
		for(URL url:this.appClassLoader.getURLs()) {
			String p=url.getFile();
			sb.append(p).append(File.pathSeparator);
		}
		if(this.parentClassLoader!=null&&this.parentClassLoader!=this.appClassLoader) {
			for(URL url:this.parentClassLoader.getURLs()) {
				String p=url.getFile();
				sb.append(p).append(File.pathSeparator);
			}
		}
		this.classpath=sb.toString();
	}
	private HashMap<String, String> loadAllJavaScript(){
		HashMap<String, String> scripts=new HashMap<>();
		File dir=new File(this.sourcePath);
		if(!dir.exists()){
			return scripts;
		}
		List<File> files=new ArrayList<>();
		FileFilter filter=new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.isDirectory()
						||pathname.getName().endsWith(".java")) {
					return true;
				}
				return false;
			}
		};
		files.addAll(Arrays.asList(dir.listFiles(filter)));
		while(files.size()>0) {
			File file=files.remove(0);
			if(file.isDirectory()) {
				files.addAll(Arrays.asList(file.listFiles(filter)));
			}else {
				String name=file.getPath();
				try {
					scripts.put(this.transferFilenameToJavaname(name), this.loadJavaScriptContent(name));
				}catch(Exception e) {
					logger.error(e.getMessage());
					return null;
				}
			}
		}
		return scripts;
	}
	private HashMap<String, String> loadJavaScript(String name) throws Exception{
		HashMap<String, String> script=new HashMap<>();
		String fileName=this.sourcePath+"/"+this.transferFilenameToJavaname(name)+".java";
		script.put(name, this.loadJavaScriptContent(fileName));
		return script;
	}
	private String loadJavaScriptContent(String fileName) throws Exception {
		InputStream in=null;
		try {
			in=new FileInputStream(fileName.replace('\\','/'));
			IoBuffer buf=IoBuffer.allocate(10240);
			buf.setAutoExpand(true);
			buf.setAutoShrink(true);
			byte[] bytes=new byte[1024];
			int length=0;
			while((length=in.read(bytes))==-1) {
				buf.put(bytes,0,length);
			}
			buf.flip();
			byte[] allBytes=new byte[buf.remaining()];
			buf.get(allBytes);
			return new String(allBytes,CharsetUtil.UTF_8);
		}finally {
			in.close();
		}
	}
	private String transferFilenameToJavaname(String fileName) {
		return fileName.replace('\\', '/').replace(this.sourcePath+"/", "")
				.replace(".java", "").replace('/','.');
	}
	private class JavaSourceFromString extends SimpleJavaFileObject{
		private String code;
		public JavaSourceFromString(String name, String code) {
			super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.code=code;
		}
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return this.code;
		}
	}
	private List<String> getInnerClasses(String className){
		List<String> classs=new ArrayList<>();
		String fileName=this.transferFilenameToJavaname(className);
		String packageName=className.substring(0, className.lastIndexOf("0"));
		String path=this.outPath+"/"+fileName.substring(0, fileName.lastIndexOf("/"));
		final String javaName=fileName.substring(fileName.lastIndexOf("/")+1);
		File dir=new File(path);
		if(dir.exists()) {
			FileFilter filter=new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if(!pathname.isDirectory()&&(pathname.getName().startsWith(javaName+"$"))) {
						return true;
					}
					return false;
				}
			};
			File[] files=dir.listFiles(filter);
			for(File file:files) {
				classs.add(packageName+"."+file.getName().replace(".class",""));
			}
		}
		return classs;
	}
	private String compilePrint(Diagnostic<?> diagnostic) {
		StringBuffer res=new StringBuffer();
		res.append("Code:["+diagnostic.getCode()+"]\n");
		res.append("Kind:["+diagnostic.getKind()+"]\n");
		res.append("Position:["+diagnostic.getPosition()+"]\n");
		res.append("Start Position:["+diagnostic.getStartPosition()+"]\n");
		res.append("End Position:["+diagnostic.getEndPosition()+"]\n");
		res.append("Source:["+diagnostic.getSource()+"]\n");
		res.append("Message:["+diagnostic.getMessage(null)+"]\n");
		res.append("LineNumber:["+diagnostic.getLineNumber()+"]\n");
		res.append("ColumnNumber:["+diagnostic.getColumnNumber()+"]\n");
		return res.toString();
	}
}
