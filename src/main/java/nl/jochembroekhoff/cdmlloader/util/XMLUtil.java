package nl.jochembroekhoff.cdmlloader.util;

import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.function.Consumer;

public class XMLUtil {
    public static void iterateChildren(NodeList nodeList, short filter, Consumer<Node> iter) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (filter > 0) {
                if (node.getNodeType() == filter)
                    iter.accept(node);
            } else
                iter.accept(node);
        }
    }

    public static void iterateChildren(Node node, short filter, Consumer<Node> iter) {
        iterateChildren(node.getChildNodes(), filter, iter);
    }

    public static void debugElement(Logger logger, Element elem) {
        logger.info("ELEM:: lname={}, nname={}, nsuri={}, baseuri={}", elem.getLocalName(), elem.getNodeName(), elem.getNamespaceURI(), elem.getBaseURI());
    }
}
