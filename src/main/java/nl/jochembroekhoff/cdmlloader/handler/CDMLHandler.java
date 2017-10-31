package nl.jochembroekhoff.cdmlloader.handler;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Icon;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.RadioGroup;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ResourceLocation;
import nl.jochembroekhoff.cdmlloader.exception.FieldNotFoundException;
import nl.jochembroekhoff.cdmlloader.meta.ApplicationMeta;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class CDMLHandler extends DefaultHandler {

    final Logger LOGGER;
    final String modId;
    final Application app;
    final Field[] fields;
    final Map<String, Field> fieldRemapping;
    final Runnable loadStart;
    final Consumer<Boolean> loadComplete;

    boolean inApplication = false;

    boolean inLayouts = false;

    boolean inLayout = false;
    String layoutName = "";
    Layout layout = null;
    Map<String, Layout> layoutMap = new HashMap<>();

    boolean inlayoutElements = false;

    ApplicationMeta applicationMeta;

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        try {
            if (qName.equals("application")) {
                inApplication = true;

                applicationMeta = new ApplicationMeta(attributes.getValue("main"));

                LOGGER.info("Main layoutId: {}", applicationMeta.getMainLayoutId());
            }
            if (!inApplication)
                return;

            if (qName.equals("layouts")) {
                inLayouts = true;
            }
            if (!inLayouts)
                return;

            /*
            Create layout
             */

            if (qName.equals("layout")) {
                inLayout = true;

                layoutName = attributes.getValue("name");
                String layoutTitle = attributes.getValue("title");
                String layoutWidth = attributes.getValue("width");
                String layoutHeight = attributes.getValue("height");
                String layoutLeft = attributes.getValue("left");
                String layoutTop = attributes.getValue("top");
                boolean hasWH = layoutWidth != null && layoutHeight != null;
                boolean hasLT = layoutLeft != null && layoutTop != null;

                if (hasWH && hasLT) {
                    /*x, y, w, h*/
                    layout = new Layout(
                            Integer.parseInt(layoutLeft),
                            Integer.parseInt(layoutTop),
                            Integer.parseInt(layoutWidth),
                            Integer.parseInt(layoutHeight)
                    );
                } else if (hasWH) {
                    /*w, h*/
                    layout = new Layout(
                            Integer.parseInt(layoutWidth),
                            Integer.parseInt(layoutHeight)
                    );
                } else {
                    /*-*/
                    layout = new Layout();
                }

                if (layoutTitle != null)
                    layout.setTitle(layoutTitle);

                layoutMap.put(layoutName, layout);

                LOGGER.info("--> Created Layout: {}", layoutName);

                setToId(layoutName, layout);
            }
            if (!inLayout)
                return;

            /*
            Process layout contents
             */
            if (qName.equals("elements")) {
                inlayoutElements = true;
            }

            if (inlayoutElements) {
                /*
                Process layout elements
                 */

                Component component = null;

                String id = attributes.getValue("id");
                String left = attributes.getValue("left");
                String top = attributes.getValue("top");
                String width = attributes.getValue("width");
                String height = attributes.getValue("height");
                boolean hasId = id != null;
                boolean hasLT = left != null && top != null;
                boolean hasWH = width != null && height != null;

                switch (qName) {
                    case "Button":
                        if (!hasLT) break;

                        RadioGroup rg = new RadioGroup();

                        /*x, y, txt(null)*/
                        Button btn = new Button(Integer.parseInt(left), Integer.parseInt(top), attributes.getValue("text"));

                        if (hasWH)
                            btn.setSize(Integer.parseInt(width), Integer.parseInt(height));

                        String icon = attributes.getValue("icon");
                        if (icon != null)
                            btn.setIcon(Icon.valueOf(icon));

                        //TODO: Test how this works
                        String iconResource = attributes.getValue("iconResource");
                        String iconU = attributes.getValue("iconU");
                        String iconV = attributes.getValue("iconV");
                        String iconWidth = attributes.getValue("iconWidth");
                        String iconHeight = attributes.getValue("iconHeight");
                        if (iconResource != null && iconU != null && iconV != null && iconWidth != null && iconHeight != null) {
                            ResourceLocation res;
                            if (!iconResource.contains(":"))
                                res = new ResourceLocation(modId, iconResource);
                            else
                                res = new ResourceLocation(iconResource);
                            btn.setIcon(res, Integer.parseInt(iconU), Integer.parseInt(iconV), Integer.parseInt(iconWidth), Integer.parseInt(iconHeight));
                        }

                        component = btn;
                        break;
                    case "Label":
                        if (!hasLT) break;

                        /*txt(null), x, y*/
                        Label lbl = new Label(attributes.getValue("text"), Integer.parseInt(left), Integer.parseInt(top));

                        component = lbl;
                        break;
                    case "listeners":
                        //TODO: Process sub listener + action
                        break;
                }

                if (hasId)
                    setToId(id, component);
                if (component != null)
                    layout.addComponent(component);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        switch (qName) {
            case "layouts":
                inLayouts = false;
                if (!layoutMap.containsKey(applicationMeta.getMainLayoutId())) {
                    LOGGER.error("==> Could not find main layout: #{}", applicationMeta.getMainLayoutId());
                    break;
                }
                try {
                    Method m = Application.class.getDeclaredMethod("setCurrentLayout", Layout.class);
                    m.setAccessible(true);
                    m.invoke(app, layoutMap.get(applicationMeta.getMainLayoutId()));

                    LOGGER.info("--> Set Main Layout: {}", applicationMeta.getMainLayoutId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "elements":
                inlayoutElements = false;
                break;
        }

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

    private void setToId(String id, Component component) throws IllegalAccessException, FieldNotFoundException {
        if (!fieldRemapping.containsKey(id))
            throw new FieldNotFoundException();
        fieldRemapping.get(id).set(app, component);
    }
}
