package com.ontimize.util.gd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;

public class GDUtilities {

    private static final Logger logger = LoggerFactory.getLogger(GDUtilities.class);

    protected static int STEP_SIZE = 512;

    public static boolean DEBUG = true;

    public static boolean DEBUG_DETAILS = false;

    private static String DESKTOP_URL = null;

    public static String FORMAT = "xml";

    static {
        GDUtilities.DESKTOP_URL = System.getProperty("google.desktop.url");
        if (GDUtilities.DESKTOP_URL != null) {
            GDUtilities.DESKTOP_URL = GDUtilities.DESKTOP_URL + "{0}";
            if (GDUtilities.FORMAT != null) {
                GDUtilities.DESKTOP_URL = GDUtilities.DESKTOP_URL + "&format=" + GDUtilities.FORMAT;
            }
        }
    }

    public static class Result implements Serializable {

        public static final String FILE = "file";

        public static final String EMAIL = "email";

        private String cat = null;

        private String title = null;

        private String url = null;

        private long time = -1;

        private String snippet = null;

        private int id = -1;

        public Result(String cat, int id, String title, String url, long time, String snippet) {
            this.cat = cat;
            this.title = title;
            this.url = url;
            this.snippet = snippet;
            this.time = time;
            this.id = id;
        }

        public String getCategory() {
            return this.cat;
        }

        public String getTitle() {
            return this.title;
        }

        public String getURL() {
            return this.url;
        }

        public String getSnippet() {
            return this.snippet;
        }

        public int getId() {
            return this.id;
        }

        public long getTime() {
            return this.time;
        }

        public void setURL(String url) {
            this.url = url;
        }

        public void setTime(long time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return this.getCategory() + " " + this.getId() + " " + this.getTitle() + " " + this.getURL() + " "
                    + this.getTime();
        }

    }

    public static void setFormat(String format) {
        GDUtilities.FORMAT = format;
        GDUtilities.DESKTOP_URL = null;
    }

    public static String getDesktopURL() throws Exception {
        if (GDUtilities.DESKTOP_URL != null) {
            return GDUtilities.DESKTOP_URL;
        }
        RegistryKey registrykey = Registry.getTopLevelKey("HKEY_CURRENT_USER");
        RegistryKey registrykey1 = Registry.openSubkey(registrykey, "Software\\Google\\Google Desktop\\API", 0);
        if (registrykey1 == null) {
            throw new Exception("Registry key not found: HKEY_CURRENT_USER\\Software\\Google\\Google Desktop\\API");
        }
        GDUtilities.DESKTOP_URL = registrykey1.getStringValue("search_url") + "{0}";
        if (GDUtilities.FORMAT != null) {
            GDUtilities.DESKTOP_URL = GDUtilities.DESKTOP_URL + "&format=" + GDUtilities.FORMAT;
        }
        return GDUtilities.DESKTOP_URL;
    }

    public static String getURL(String queryString, String start, String num, String flags) throws Exception {
        StringBuilder StringBuilder = new StringBuilder();
        StringBuilder StringBuilder1 = new StringBuilder(queryString);

        StringBuilder.append(MessageFormat.format(GDUtilities.getDesktopURL(),
                new String[] { URLEncoder.encode(StringBuilder1.toString()) }));
        if (num != null) {
            StringBuilder.append("&num=");
            StringBuilder.append(num);
        }
        if (start != null) {
            StringBuilder.append("&start=");
            StringBuilder.append(start);
        }
        if (flags != null) {
            StringBuilder.append("&flags=");
            StringBuilder.append(flags);
        }
        if (GDUtilities.DEBUG) {
            GDUtilities.logger.debug("URL: " + StringBuilder);
        }
        return StringBuilder.toString();
    }

    public static InputStream getResultInputStream(String queryString, String start, String num, String flags)
            throws Exception {
        String s = GDUtilities.getURL(queryString, start, num, flags);
        URL url = new URL(s);
        return url.openStream();
    }

    public static Result[] search(String query, int start, int items) throws Exception {
        return GDUtilities.search(query, start, items, null, null, null);
    }

    public static Result[] search(String query, int start, int items, File rootDir, String category, String name)
            throws Exception {
        InputStream in = GDUtilities.getResultInputStream(query, start >= 0 ? "" + start : null,
                items > 0 ? "" + items : null, null);
        return GDUtilities.parseXML(in, rootDir, category, name);
    }

    public static Result[] parseXML(InputStream in, File rootDir, String category, String name) {
        try {
            long t = System.currentTimeMillis();
            DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = df.newDocumentBuilder();
            Document doc = db.parse(in);
            Element e = doc.getDocumentElement();
            // DEBUG
            if (GDUtilities.DEBUG_DETAILS) {
                String s = GDUtilities.dom2String(doc);
                GDUtilities.logger.debug("Results: " + s);
            }
            List lList = new ArrayList();
            NodeList nodeList = e.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node n = nodeList.item(i);
                if ((n.getNodeType() == Node.ELEMENT_NODE) && n.getNodeName().equalsIgnoreCase("result")) {
                    // Children
                    String categ = null;
                    String id = null;
                    String title = null;
                    String url = null;
                    String time = null;
                    String snippet = null;
                    NodeList l2 = n.getChildNodes();
                    for (int j = 0; j < l2.getLength(); j++) {
                        Node n2 = l2.item(j);
                        if (n2.getNodeName().equalsIgnoreCase("category")) {
                            categ = n2.getFirstChild().getNodeValue();
                        } else if (n2.getNodeName().equalsIgnoreCase("id")
                                || n2.getNodeName().equalsIgnoreCase("doc_id")) {
                            id = n2.getFirstChild().getNodeValue();
                        } else if (n2.getNodeName().equalsIgnoreCase("title")) {
                            title = n2.getFirstChild().getNodeValue();
                        } else if (n2.getNodeName().equalsIgnoreCase("url")) {
                            url = n2.getFirstChild().getNodeValue();
                        } else if (n2.getNodeName().equalsIgnoreCase("time")) {
                            time = n2.getFirstChild().getNodeValue();
                        } else if (n2.getNodeName().equalsIgnoreCase("snippet")) {
                            snippet = n2.getFirstChild().getNodeValue();
                        }
                    }
                    Result res = new Result(categ, Integer.parseInt(id), title, url, Long.parseLong(time), snippet);
                    boolean match = GDUtilities.matchs(res, rootDir, category, name);
                    if (match) {
                        lList.add(res);
                    }
                }
            }
            if (GDUtilities.DEBUG) {
                GDUtilities.logger.debug("ParseXML: t = " + (System.currentTimeMillis() - t));
            }
            return (Result[]) lList.toArray(new Result[] {});
        } catch (Exception ex) {
            GDUtilities.logger.error(null, ex);
            return null;
        }
    }

    public static String dom2String(Document d) {
        StringWriter w = null;
        try {
            w = new StringWriter();
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(d);
            StreamResult result = new StreamResult(w);
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "ISO-8859-1");
            transformer.transform(source, result);
            w.flush();
            StringBuffer sb = w.getBuffer();
            return sb.toString();
        } catch (Exception ex) {
            GDUtilities.logger.error("XMLServerUtilities: " + ex.getMessage(), ex);
            return null;
        } finally {
            try {
                w.close();
            } catch (IOException ex) {
                GDUtilities.logger.trace(null, ex);
            }
        }
    }

    /**
     * Applies a filter to res.
     * @param root File. Directory in which the results must be stored. The results returned will be
     *        only of type 'file'. If null no filter is applied to location of the result.
     * @param category String. Type of the result. If null no filter is applied to the type of the
     *        result.
     * @param name String. String that must be contained in the name of the resource (case-insensitive).
     *        If null no filter is applied.
     * @param res Result[]
     * @return Result[]
     */
    public static Result[] filter(File rootDir, String category, String name, Result[] res) {
        long t = System.currentTimeMillis();
        if ((rootDir != null) && !rootDir.isDirectory()) {
            throw new IllegalArgumentException(rootDir + " is not a directory");
        }
        if (res == null) {
            return null;
        }
        List list = new ArrayList();
        for (int i = 0; i < res.length; i++) {
            boolean match = GDUtilities.matchs(res[i], rootDir, category, name);
            if (match) {
                list.add(res[i]);
            }
        }
        if (GDUtilities.DEBUG) {
            GDUtilities.logger.debug("filter: t = " + (System.currentTimeMillis() - t));
        }
        return (Result[]) list.toArray(new Result[] {});
    }

    /**
     * Determines if res matchs the filter defined by rootDir, category and name.
     * @param res Result Result to evaluate
     * @param root File. Directory in which the results must be stored. The results returned will be
     *        only of type 'file'. If null no filter is applied to location of the result.
     * @param category String. Type of the result. If null no filter is applied to the type of the
     *        result.
     * @param name String. String that must be contained in the name of the resource (case-insensitive).
     *        If null no filter is applied.
     * @return boolean true if res match the conditions
     */
    public static boolean matchs(Result res, File rootDir, String category, String name) {
        long t = System.currentTimeMillis();
        if (res == null) {
            throw new IllegalArgumentException("Result can´t be null");
        }
        boolean match = true;
        if (rootDir != null) {
            if (Result.FILE.equalsIgnoreCase(res.getCategory())) {
                File fResult = new File(res.getURL());
                if (!fResult.toString().toLowerCase().startsWith(rootDir.toString().toLowerCase())) {
                    match = false;
                }
            } else {
                match = false;
            }
        }
        if (category != null) {
            if (!category.equalsIgnoreCase(res.getCategory())) {
                match = false;
            }
        }
        if (name != null) {
            if (res.getTitle().toLowerCase().indexOf(name.toLowerCase()) < 0) {
                match = false;
            }
        }
        return match;
    }

    public static Result[] searchInDirectory(String query, File rootDir) throws Exception {
        long t = System.currentTimeMillis();
        // STEP_SIZE en STEP_SIZE
        int items = GDUtilities.STEP_SIZE;
        int start = 0;

        if (query == null) {
            throw new IllegalArgumentException("query parameter can´t be null");
        }
        int lastResults = GDUtilities.STEP_SIZE;
        List list = new ArrayList();
        while (lastResults == GDUtilities.STEP_SIZE) {
            Result[] res = GDUtilities.search(query, start, items);
            if (res == null) {
                return null;
            }
            Result[] resFiltered = GDUtilities.filter(rootDir, null, null, res);
            if (resFiltered == null) {
                return null;
            }
            lastResults = res.length;
            start = start + res.length;
            list.addAll(Arrays.asList(resFiltered));
        }
        if (GDUtilities.DEBUG) {
            GDUtilities.logger.debug("searchInDirectory: t = " + (System.currentTimeMillis() - t));
        }
        return (Result[]) list.toArray(new Result[] {});
    }

}
