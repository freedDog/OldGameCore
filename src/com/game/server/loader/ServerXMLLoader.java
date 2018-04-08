package com.game.server.loader;
/**
 * 服务器配置加载
 * @author JiangBangMing
 *
 * 2018年4月8日 下午2:44:29
 */

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.game.server.config.ServerConfig;
import com.game.structs.XMLConfigTagName;
import com.game.utils.DateUtile;

public class ServerXMLLoader {
	private static final Logger LOGGER=LoggerFactory.getLogger(ServerXMLLoader.class);
	
	public List<ServerConfig> load(String file) {
		try {
			DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream in=new FileInputStream(file);
			Document doc=builder.parse(in);
			NodeList list=doc.getElementsByTagName(XMLConfigTagName.SERVER);
			if(list.getLength()==0) {
				return null;
			}
			List<ServerConfig> config=new ArrayList<>();
			for(int i=0;i<list.getLength();i++) {
				Node node=list.item(i);
				NodeList childs=node.getChildNodes();
				ServerConfig serverConfig=new ServerConfig();
				for(int j=0;j<childs.getLength();j++) {
					Node item=childs.item(j);
					String nodeName=item.getNodeName();
					String nodeText=item.getTextContent().trim();
					switch (nodeName) {
					case XMLConfigTagName.SERVER_NAME:
						serverConfig.setName(nodeText);
						break;
					case XMLConfigTagName.SERVER_ID:
						serverConfig.setId(Integer.parseInt(nodeText));
						break;
					case XMLConfigTagName.SERVER_WEB:
						serverConfig.setWeb(nodeText);
						break;
					case XMLConfigTagName.SERVER_WEB_ID:
						serverConfig.setWebId(Integer.parseInt(nodeText));
						break;
					case XMLConfigTagName.SERVER_GROUP_ID:
						serverConfig.setGroupId(Integer.parseInt(nodeText));
						break;
					case XMLConfigTagName.SERVER_URL:
						serverConfig.setUrl(nodeText);
						break;
					case XMLConfigTagName.SERVER_OPEN:
						serverConfig.setOpenDate(DateUtile.stringToDate(nodeText));
						break;
					case XMLConfigTagName.SERVER_TYPE:
						serverConfig.setType(Integer.parseInt(nodeText));
						break;
					case XMLConfigTagName.SERVER_DB:
						serverConfig.setDbFile(nodeText);
						break;
					case XMLConfigTagName.SERVER_DBBACKUP:
						serverConfig.setDbBackupFile(nodeText);
						break;
					case XMLConfigTagName.SERVER_LIST:
						break;
					}
				}
				config.add(serverConfig);
			}
			in.close();
			return config;
		}catch(Exception e) {
			LOGGER.error("load "+file+" Error:"+e.getMessage());
		}
		return null;
	}
}
