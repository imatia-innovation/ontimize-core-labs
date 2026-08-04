package com.ontimize.util.rtf;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.util.rtf.element.RTFElement;

public class RTFParserExtended {

    private static final Logger logger = LoggerFactory.getLogger(RTFParserExtended.class);

    private final char[] digits = { '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    RTFElement root = new RTFElement();

    Reader in;

    int depth = 0;

    public RTFParserExtended(Reader in) {
        this.in = in;
    }

    public RTFElement parse() throws IOException {
        this.parse(this.root);
        return this.root.getChild(0);
    }

    protected void parse(RTFElement parent) throws IOException {
        int i = this.in.read();
        StringBuilder str = new StringBuilder();
        while (i != -1) {
            char c = (char) i;
            if (c == '\\') {
                if (!str.toString().equals("\\")) {
                    this.createNode(parent, str.toString());
                    str.delete(0, str.length());
                } else {
                    this.createNode(parent, str.toString());
                    str.delete(0, str.length());
                    c = ' ';
                }
            }
            if (c == ';') {
                if (str.length() == 0) {
                    i = this.in.read();
                    continue;
                }
                if (str.charAt(0) == '\\') {
                    this.createNode(parent, str.toString());
                    str.delete(0, str.length());
                }
                str.append(c);
            } else if (c == ' ') {
                if (str.length() == 0) {
                    str.append(c);
                    i = this.in.read();
                    continue;
                }
                if (str.charAt(0) == '\\') {
                    this.createNode(parent, str.toString());
                    str.delete(0, str.length());
                }

                str.append(c);
            } else if ((c == '\n') || (c == '\r')) {
                this.createNode(parent, str.toString());
                str.delete(0, str.length());
            } else if (c == '{') {
                if ((str.length() == 1) && (str.charAt(0) == '\\')) {
                    str.setCharAt(0, ' ');
                    str.append(c);
                    i = this.in.read();
                    continue;
                }

                this.createNode(parent, str.toString());
                str.delete(0, str.length());
                RTFElement node = new RTFElement(parent);
                node.setAttribute("name", "*<group>*");
                this.depth += 1;
                this.parse(node);
            } else {
                if (c == '}') {
                    if ((str.length() == 1) && (str.charAt(0) == '\\')) {
                        str.setCharAt(0, ' ');
                        str.append(c);
                        i = this.in.read();
                        continue;
                    }

                    this.depth -= 1;
                    this.createNode(parent, str.toString());
                    str.delete(0, str.length());
                    return;
                }

                str.append(c);
            }
            i = this.in.read();
        }
    }

    protected void createNode(RTFElement parent, String text) {
        if (text.length() == 0) {
            return;
        }
        RTFElement node = new RTFElement(parent);
        if (text.charAt(0) == '\\') {
            int index = this.lookupNumber(text);
            int length = this.cval(text);
            if (text.length() == 1) {
                node.setAttribute("name", "");
                node.setAttribute("content", text);
            } else if (text.charAt(1) == '\'') {
                String character = text.substring(2, 4);

                node.setAttribute("length", Integer.valueOf(2));
                node.setAttribute("name", text.substring(0, 4));

                if (text.length() > 4) {
                    node = new RTFElement(parent);
                    node.setAttribute("content", text.substring(4));
                }

            } else if (length != -1) {
                node.setAttribute("length", Integer.valueOf(length));
                node.setAttribute("name", text.substring(0, index));
            } else {
                node.setAttribute("name", text);
            }
        } else {
            node.setAttribute("name", "");
            node.setAttribute("content", text);
        }
    }

    private boolean isdigit(char c) {
        for (int j = 0; j < this.digits.length; j++) {
            if (c == this.digits[j]) {
                return true;
            }
        }
        return false;
    }

    private int lookupNumber(String s) {
        if (s.length() == 0) {
            return -1;
        }
        for (int j = s.length() - 1; j >= 0; j--) {
            if (!this.isdigit(s.charAt(j))) {
                return j + 1;
            }
        }
        return -1;
    }

    private int cval(String tok) {
        int idx = this.lookupNumber(tok);
        if ((idx < 0) || (tok.length() == idx)) {
            return -1;
        }
        try {
            return Integer.parseInt(tok.substring(idx));
        } catch (Exception e) {
            RTFParserExtended.logger.trace(null, e);
        }
        return -1;
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Test parser");
        frame.setDefaultCloseOperation(3);
        RTFParserExtended parser;
        if (args.length > 0) {
            parser = new RTFParserExtended(new FileReader(args[0]));
        } else {
            parser = new RTFParserExtended(new FileReader("C:/test.rtf"));
        }
        parser.parse();
        DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("ROOT");
        RTFParserExtended.createTree(treeRoot, parser.root);
        JTree tree = new JTree(treeRoot);
        JScrollPane scroll = new JScrollPane(tree);
        frame.getContentPane().add(scroll);
        frame.setSize(500, 700);
        frame.show();
    }

    private static void createTree(DefaultMutableTreeNode dest, RTFElement source) {
        for (int i = 0; i < source.getChildCount(); i++) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(source.getChild(i));
            dest.add(node);
            RTFParserExtended.createTree(node, source.getChild(i));
        }
    }

}
