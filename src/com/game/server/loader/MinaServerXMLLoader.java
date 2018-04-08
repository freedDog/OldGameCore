package com.game.server.loader;
/**
 * mina 服务器配置
 * @author JiangBangMing
 *
 * 2018年4月8日 下午2:34:19
 */

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.game.server.config.MinaServerConfig;
import com.game.structs.XMLConfigTagName;
/**
 * 加载mina 配置
 * @author JiangBangMing
 *
 * 2018年4月8日 下午2:43:32
 */
public class MinaServerXMLLoader {
	private static final Logger LOGGER=LoggerFactory.getLogger(MinaServerXMLLoader.class);
	
	public MinaServerConfig load(String file) {
		try {
			DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream in=new FileInputStream(file);
			Document doc=builder.parse(in);
			NodeList list=doc.getElementsByTagName(XMLConfigTagName.SERVER);
			if(list.getLength()==0) {
				return null;
			}
			Node node=list.item(0);
			NodeList childs=node.getChildNodes();
			MinaServerConfig config=new MinaServerConfig();
			for(int i=0;i<childs.getLength();i++) {
				Node item=childs.item(i);
				if(XMLConfigTagName.SERVER_PORT.equals(item.getNodeName())) {
					config.setMina_port(Integer.parseInt(item.getTextContent().trim()));
				}
				if(XMLConfigTagName.SERVER_SSL_PORT.equals(item.getNodeName())) {
					config.setMina_ssl_port(Integer.parseInt(item.getTextContent().trim()));
				}
			}
			in.close();
			return config;
		
		}catch(Exception e) {
			LOGGER.error("load"+file+" Error:"+e.getMessage());
		}
		return null;
	}
}
