package com.game.server.loader;
/**
 * 内部服务器配置
 * @author JiangBangMing
 *
 * 2018年4月8日 下午2:21:23
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

import com.game.server.config.InnerServerConfig;
import com.game.structs.XMLConfigTagName;
/**
 * 内部连接配置
 * @author JiangBangMing
 *
 * 2018年4月8日 下午2:33:05
 */
public class InnerServerXMLoader {
	private static final Logger LOGGER=LoggerFactory.getLogger(InnerServerXMLoader.class);
	public InnerServerConfig load(String file) {
		try {
			DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream in=new FileInputStream(file);
			Document doc=builder.parse(in);
			NodeList list=doc.getElementsByTagName(XMLConfigTagName.SERVER);
			if(list.getLength()==0) {
				return null;
			}
			InnerServerConfig config=new InnerServerConfig();
			Node node=list.item(0);
			NodeList childs=node.getChildNodes();
			for(int i=0;i<childs.getLength();i++) {
				Node item=childs.item(i);
				if(XMLConfigTagName.SERVER_CONNECT_IP.equals(item.getNodeName())) {
					config.setConnectIp(item.getTextContent().trim());
				}
				if(XMLConfigTagName.SERVER_CONNECT_PORT.equals(item.getNodeName())) {
					config.setConnectPort(Integer.parseInt(item.getTextContent().trim()));
				}
				if(XMLConfigTagName.SERVER_PORT.equals(item.getNodeName())) {
					config.setPort(Integer.parseInt(item.getTextContent().trim()));
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
