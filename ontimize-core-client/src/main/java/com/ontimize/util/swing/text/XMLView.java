package com.ontimize.util.swing.text;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

public class XMLView extends PlainView {

    private static HashMap patternColors;

    private static String TAG_PATTERN = "(</?[A-Za-z\\-_]*)\\s?>?";

    private static String TAG_END_PATTERN = "(/>)";

    private static String TAG_ATTRIBUTE_PATTERN = "\\s(\\w*)\\=";

    private static String TAG_ATTRIBUTE_VALUE = "[a-z\\-]*\\=(\"[^\"]*\")";

    private static String TAG_COMMENT = "(<\\!--[\\w ]*-->)";

    private static String TAG_CDATA = "(<\\!\\[CDATA\\[.*\\]\\]>)";

    static {
        XMLView.patternColors = new LinkedHashMap();
        XMLView.patternColors.put(Pattern.compile(XMLView.TAG_PATTERN), new Color(63, 127, 127));
        XMLView.patternColors.put(Pattern.compile(XMLView.TAG_CDATA), Color.GRAY);
        XMLView.patternColors.put(Pattern.compile(XMLView.TAG_ATTRIBUTE_PATTERN), new Color(127, 0, 127));
        XMLView.patternColors.put(Pattern.compile(XMLView.TAG_END_PATTERN), new Color(63, 127, 127));
        XMLView.patternColors.put(Pattern.compile(XMLView.TAG_ATTRIBUTE_VALUE), new Color(42, 0, 255));
        XMLView.patternColors.put(Pattern.compile(XMLView.TAG_COMMENT), Color.BLUE);
    }

    public XMLView(Element elem) {
        super(elem);
    }

    @Override
    protected int drawUnselectedText(Graphics graphics, int x, int y, int p0, int p1) throws BadLocationException {

        Document doc = this.getDocument();
        String text = doc.getText(p0, p1 - p0);

        Segment segment = this.getLineBuffer();

        // Integer,Integer
        SortedMap startMap = new TreeMap();
        // Integer,Color
        SortedMap colorMap = new TreeMap();

        Iterator patterColorKeys = XMLView.patternColors.keySet().iterator();
        while (patterColorKeys.hasNext()) {
            Pattern currentKey = (Pattern) patterColorKeys.next();
            Matcher matcher = currentKey.matcher(text);

            while (matcher.find()) {
                startMap.put(Integer.valueOf(matcher.start(1)), Integer.valueOf(matcher.end()));
                colorMap.put(Integer.valueOf(matcher.start(1)), XMLView.patternColors.get(currentKey));
            }
        }

        int i = 0;

        // Colour the parts
        Iterator startIterator = startMap.keySet().iterator();
        while (startIterator.hasNext()) {
            Object currentKey = startIterator.next();
            Object currentValue = startMap.get(currentKey);

            int start = ((Integer) currentKey).intValue();
            int end = ((Integer) currentValue).intValue();

            if (i < start) {
                graphics.setColor(Color.black);
                doc.getText(p0 + i, start - i, segment);
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
            }

            graphics.setColor((Color) colorMap.get(currentKey));
            i = end;
            doc.getText(p0 + start, i - start, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
        }

        // Paint possible remaining text black
        if (i < text.length()) {
            graphics.setColor(Color.black);
            doc.getText(p0 + i, text.length() - i, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
        }

        return x;
    }

}
