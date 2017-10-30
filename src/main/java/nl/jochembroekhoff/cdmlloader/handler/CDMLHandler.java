package nl.jochembroekhoff.cdmlloader.handler;

import lombok.RequiredArgsConstructor;
import nl.jochembroekhoff.cdmlloader.meta.ApplicationMeta;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class CDMLHandler extends DefaultHandler {

    final Logger LOGGER;
    final Field[] fields;
    final Map<String, Field> fieldRemapping;
    final Runnable loadStart;
    final Consumer<Boolean> loadComplete;

    boolean inApplication = false;
    boolean inLayouts = false;

    ApplicationMeta applicationMeta;

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        LOGGER.info("Start Element: qname={}, lcname={}", qName, localName);

        if (qName.equals("application")) {
            inApplication = true;

            applicationMeta = new ApplicationMeta(attributes.getValue("main"));
        }
        if (!inApplication)
            return;

        if (qName.equals("layouts")) {
            inLayouts = true;
        }
        if (!inLayouts)
            return;

    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        LOGGER.info("End Element: qname={}, lcname={}", qName, localName);

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        LOGGER.info("Characters: {}", new String(ch, start, length));

    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();

        if (loadStart != null)
            loadStart.run();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();

        if (loadComplete != null)
            loadComplete.accept(true);
    }
}
