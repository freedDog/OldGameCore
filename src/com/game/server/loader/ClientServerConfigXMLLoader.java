package com.game.server.loader;
/**
 * 客服端服务加载配置
 * @author JiangBangMing
 *
 * 2018年4月8日 下午1:57:10
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

import com.game.server.config.ClientServerConfig;
import com.game.server.config.ServerInfo;
import com.game.structs.XMLConfigTagName;

public class ClientServerConfigXMLLoader {
	private final static Logger LOGGER=LoggerFactory.getLogger(ClientServerConfigXMLLoader.class);
	
	public ClientServerConfig load(String file) {
		try {
			DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream in=new FileInputStream(file);
			Document doc=builder.parse(in);
			NodeList list=doc.getElementsByTagName(XMLConfigTagName.SERVER);
			ClientServerConfig config=new ClientServerConfig();
			if(list.getLength()>0) {
				Node node=list.item(0);
				NodeList childs=node.getChildNodes();
				for(int i=0;i<childs.getLength();i++) {
					if(XMLConfigTagName.CONNECT_SERVERS.equals(childs.item(i).getNodeName())) {
						NodeList servers=childs.item(i).getChildNodes();
						for(int j=0;j<servers.getLength();j++) {
							Node item=servers.item(j);
							ServerInfo info=new ServerInfo();
							NodeList attrs=item.getChildNodes();
							for(int k=0;k<attrs.getLength();k++) {
								Node attr=attrs.item(k);
								if(XMLConfigTagName.SERVER_ID.equals(attr.getNodeName())) {
									info.setId(Integer.parseInt(attr.getTextContent().trim()));
								}
								if(XMLConfigTagName.SERVER_IP.equals(attr.getNodeName())) {
									info.setIp(attr.getTextContent().trim());
								}
								if(XMLConfigTagName.SERVER_PORT.equals(attr.getNodeName())) {
									info.setPort(Integer.parseInt(attr.getTextContent().trim()));
								}
								if(XMLConfigTagName.SERVER_TYPE.equals(attr.getNodeName())) {
									info.setType(Integer.parseInt(attr.getTextContent().trim()));
								}
							}
							config.getConnectServers().add(info);
						}
					}
				}
			}
			in.close();
			return config;
		}catch(Exception e) {
			LOGGER.error("load "+file+" Error"+e.getMessage());
		}
		return null;
	}
}
