package org.ender.updater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdaterConfig {
    private static final String ITEM = "item";
    
    public String mem, res, server, jar;
    public File dir;

    private final ItemFactory itemFactory = new ItemFactory(this);
    public final List<Item> items = new ArrayList<Item>();

    public UpdaterConfig() {
	InputStream stream = UpdaterConfig.class.getResourceAsStream("/config.xml");

    dir = new File(".");

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder;
	try {
	    builder = factory.newDocumentBuilder();
	    Document doc = builder.parse(stream);
	    stream.close();
	    
	    NamedNodeMap attrs = doc.getDocumentElement().getAttributes();
	    Node node = attrs.getNamedItem("mem");
	    mem = (node != null)?node.getNodeValue():"";
	    
	    node = attrs.getNamedItem("res");
	    res = (node != null)?node.getNodeValue():"";
	    
	    node = attrs.getNamedItem("server");
	    server = (node != null)?node.getNodeValue():"";
	    
	    node = attrs.getNamedItem("jar");
	    jar = (node != null)?node.getNodeValue():"";

	    NodeList groupNodes = doc.getElementsByTagName(ITEM);
	    for (int i = 0; i < groupNodes.getLength(); i++) {
		Item itm = parseItem(groupNodes.item(i));
		if (itm != null)
		    items.add(itm);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private Item parseItem(Node node) throws IOException {
		if (node.getNodeType() != Node.ELEMENT_NODE)
			return null;
		return itemFactory.create((Element)node);
    }
}
