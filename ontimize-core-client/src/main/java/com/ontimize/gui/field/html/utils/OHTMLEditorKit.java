package com.ontimize.gui.field.html.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLWriter;
import javax.swing.text.html.MinimalHTMLWriter;
import javax.swing.text.html.ObjectView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.html.HTMLUtils;
import com.ontimize.gui.field.html.actions.DecoratedTextAction;
import com.ontimize.gui.field.html.actions.EnterKeyAction;
import com.ontimize.gui.field.html.actions.HTMLTextEditAction;
import com.ontimize.gui.field.html.actions.RemoveAction;
import com.ontimize.gui.field.html.actions.TabAction;

/**
 * An HTML editor kit which can properly draw borderless tables and allows for resizing of tables
 * and images.
 *
 * @author Imatia S.L.
 *
 */
public class OHTMLEditorKit extends HTMLEditorKit {

	private static final Logger logger = LoggerFactory.getLogger(OHTMLEditorKit.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected List monitoredViews = new ArrayList();

	protected MouseInputAdapter resizeHandler = new ResizeHandler();

	protected Map editorToActionsMap = new HashMap();

	protected KeyStroke tabBackwardKS = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK);

	protected ViewFactory wysFactory = new OHTMLFactory();

	public OHTMLEditorKit() {
		super();
	}

	@Override
	public ViewFactory getViewFactory() {
		return this.wysFactory;
	}

	@Override
	public Document createDefaultDocument() {
		final HTMLDocument doc = (HTMLDocument) super.createDefaultDocument();

		// Unless the following property is set, the HTML parser will throw a
		// ChangedCharSetException every time a char set tag is encountered.
		doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);

		return doc;
	}

	class CustomHTMLWriter extends HTMLWriter {

		public CustomHTMLWriter(final Writer w, final HTMLDocument doc, final int pos, final int len) {
			super(w, doc, pos, len);
			this.setLineLength(Integer.MAX_VALUE);
		}

	}

	@Override
	public void write(final Writer out, final Document doc, final int pos, final int len) throws IOException, BadLocationException {
		if (doc instanceof HTMLDocument) {
			final CustomHTMLWriter w = new CustomHTMLWriter(out, (HTMLDocument) doc, pos, len);
			w.write();
		} else if (doc instanceof StyledDocument) {
			final MinimalHTMLWriter w = new MinimalHTMLWriter(out, (StyledDocument) doc, pos, len);
			w.write();
		} else {
			super.write(out, doc, pos, len);
		}
	}

	@Override
	public void install(final JEditorPane ed) {
		super.install(ed);
		if (this.editorToActionsMap.containsKey(ed)) {
			return; // already installed
		}

		ed.addMouseListener(this.resizeHandler);
		ed.addMouseMotionListener(this.resizeHandler);

		// install wysiwyg actions into the ActionMap for the editor being
		// installed
		final Map actions = new HashMap();
		final InputMap inputMap = ed.getInputMap(JComponent.WHEN_FOCUSED);
		final ActionMap actionMap = ed.getActionMap();

		Action delegate = actionMap.get("insert-break");
		Action action = new EnterKeyAction(delegate);
		actions.put("insert-break", action);
		actionMap.put("insert-break", action);

		delegate = actionMap.get("delete-previous");
		action = new RemoveAction(RemoveAction.BACKSPACE, delegate);
		actions.put("delete-previous", action);
		actionMap.put("delete-previous", action);

		delegate = actionMap.get("delete-next");
		action = new RemoveAction(RemoveAction.DELETE, delegate);
		actions.put("delete-next", action);
		actionMap.put("delete-next", action);

		delegate = actionMap.get("insert-tab");
		action = new TabAction(TabAction.FORWARD, delegate);
		actions.put("insert-tab", action);
		actionMap.put("insert-tab", action);

		delegate = actionMap.get("paste-from-clipboard");
		final HTMLTextEditAction hteAction = new com.ontimize.gui.field.html.actions.PasteAction();
		hteAction.putContextValue(HTMLTextEditAction.EDITOR, ed);
		actions.put("paste-from-clipboard", delegate);
		actionMap.put("paste-from-clipboard", hteAction);

		inputMap.put(this.tabBackwardKS, "tab-backward");// install tab
		// backwards
		// keystroke
		action = new TabAction(TabAction.BACKWARD, delegate);
		actions.put("tab-backward", action);
		actionMap.put("tab-backward", action);

		this.editorToActionsMap.put(ed, actions);
	}

	@Override
	public void deinstall(final JEditorPane ed) {
		super.deinstall(ed);
		if (!this.editorToActionsMap.containsKey(ed)) {
			return; // not installed installed
		}
		ed.removeMouseListener(this.resizeHandler);
		ed.removeMouseMotionListener(this.resizeHandler);

		// restore actions to their original state
		final ActionMap actionMap = ed.getActionMap();
		final Map actions = (Map) this.editorToActionsMap.get(ed);

		Action curAct = actionMap.get("insert-break");
		if (curAct == actions.get("insert-break")) {
			actionMap.put("insert-break", ((DecoratedTextAction) curAct).getDelegate());
		}

		curAct = actionMap.get("delete-previous");
		if (curAct == actions.get("delete-previous")) {
			actionMap.put("delete-previous", ((DecoratedTextAction) curAct).getDelegate());
		}

		curAct = actionMap.get("delete-next");
		if (curAct == actions.get("delete-next")) {
			actionMap.put("delete-next", ((DecoratedTextAction) curAct).getDelegate());
		}

		curAct = actionMap.get("insert-tab");
		if (curAct == actions.get("insert-tab")) {
			actionMap.put("insert-tab", ((DecoratedTextAction) curAct).getDelegate());
		}

		curAct = actionMap.get("paste-from-clipboard");
		if (curAct instanceof com.ontimize.gui.field.html.actions.PasteAction) {
			actionMap.put("paste-from-clipboard", (Action) actions.get("paste-from-clipboard"));
		}

		curAct = actionMap.get("tab-backward");
		if (curAct == actions.get("insert-tab")) {
			actionMap.remove("tab-backward");
			// inputMap.remove(tabBackwardKS);//remove backwards keystroke
		}

		this.editorToActionsMap.remove(ed);
	}

	/**
	 * Factory to build views of the html elements. This simply extends the behavior of the default html
	 * factory to draw borderless tables, etc
	 */
	public class OHTMLFactory extends HTMLFactory {

		@Override
		public View create(final Element elem) {
			final Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
			if (o instanceof HTML.Tag) {
				final HTML.Tag kind = (HTML.Tag) o;
				if (kind == HTML.Tag.TABLE) {
					final ResizableView v = new ResizableView(new BorderlessTableView(super.create(elem)));
					OHTMLEditorKit.this.monitoredViews.add(v);
					return v;
				} else if (kind == HTML.Tag.IMG) {
					final ResizableView v = new ResizableView(new OImageView(elem));
					OHTMLEditorKit.this.monitoredViews.add(v);
					return v;
				} else if (kind == HTML.Tag.COMMENT) {
					return new UnknownElementView(elem);
				} else if (kind == HTML.Tag.OBJECT) {
					final ObjectView ov = new ObjectView(elem) {

						// make a nicer looking representation for <object>.
						// The default is a crappy red JLabel with "??" as the
						// text
						@Override
						protected Component createComponent() {
							final Component comp = super.createComponent();
							if (comp instanceof JLabel) {
								final JLabel l = (JLabel) comp;
								if (l.getText().equals("??") && l.getForeground().equals(Color.red)) {
									l.setText(null);
									l.setBackground(Color.YELLOW);
									l.setOpaque(true);
									l.setBorder(BorderFactory.createRaisedBevelBorder());
									l.setToolTipText("<object></object>");
								}
							}
							return comp;
						}
					};
					return ov;
				} else if ((kind instanceof HTML.UnknownTag) || (kind == HTML.Tag.TITLE) || (kind == HTML.Tag.META)
						|| (kind == HTML.Tag.LINK) || (kind == HTML.Tag.STYLE) || (kind == HTML.Tag.SCRIPT)
						|| (kind == HTML.Tag.AREA) || (kind == HTML.Tag.MAP) || (kind == HTML.Tag.PARAM)
						|| (kind == HTML.Tag.APPLET)) {
					return new UnknownElementView(elem);
				}
			}

			return super.create(elem);
		}

	}

	/**
	 * Handle the resizing of images and tables
	 *
	 */
	protected class ResizeHandler extends MouseInputAdapter {

		boolean dragStarted;

		// need down flag for Java 1.4 - mouseMoved gets called during a drag
		boolean mouseDown;

		int dragDir = -1;

		@Override
		public void mousePressed(final MouseEvent e) {
			this.mouseDown = true;
			boolean selected = false;
			// iterate thru the list backwards to select
			// most recently added views so nested tables get selected properly
			for (int i = OHTMLEditorKit.this.monitoredViews.size() - 1; i >= 0; i--) {
				final ResizableView v = (ResizableView) OHTMLEditorKit.this.monitoredViews.get(i);
				final Rectangle r = v.getBounds();
				if ((r != null) && r.contains(e.getPoint()) && !selected) {
					v.setSelectionEnabled(true);
					this.dragDir = v.getHandleForPoint(e.getPoint());
					this.setCursorForDir(this.dragDir, e.getComponent());
					selected = true;
				} else {
					v.setSelectionEnabled(false);
				}
			}

			e.getComponent().repaint();
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			if (!this.mouseDown) {
				final ResizableView v = this.getSelectedView();
				if (v == null) {
					return;
				}

				final Component c = e.getComponent();
				this.setCursorForDir(v.getHandleForPoint(e.getPoint()), c);
			}
		}

		@Override
		public void mouseDragged(final MouseEvent e) {
			this.dragStarted = this.dragDir != -1;
			final ResizableView v = this.getSelectedView();
			if ((v == null) || !this.dragStarted) {
				return;
			}

			final Rectangle r = v.getSelectionBounds();

			if (this.dragDir == ResizableView.SE) {
				r.width = e.getX() - r.x;
				r.height = e.getY() - r.y;
			} else if (this.dragDir == ResizableView.NE) {
				r.width = e.getX() - r.x;
				r.height = (r.y + r.height) - e.getY();
				r.y = e.getY();
			} else if (this.dragDir == ResizableView.SW) {
				r.width = (r.x + r.width) - e.getX();
				r.height = e.getY() - r.y;
				r.x = e.getX();
			} else if (this.dragDir == ResizableView.NW) {
				r.width = (r.x + r.width) - e.getX();
				r.height = (r.y + r.height) - e.getY();
				r.x = e.getX();
				r.y = e.getY();
			} else if (this.dragDir == ResizableView.N) {
				r.height = (r.y + r.height) - e.getY();
				r.y = e.getY();
			} else if (this.dragDir == ResizableView.S) {
				r.height = e.getY() - r.y;
			} else if (this.dragDir == ResizableView.E) {
				r.width = e.getX() - r.x;
			} else if (this.dragDir == ResizableView.W) {
				r.width = (r.x + r.width) - e.getX();
				r.x = e.getX();
			}

			e.getComponent().repaint();
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			this.mouseDown = false;
			final ResizableView v = this.getSelectedView();
			if ((v != null) && this.dragStarted) {
				final Element elem = v.getElement();
				final SimpleAttributeSet sas = new SimpleAttributeSet(elem.getAttributes());
				final Integer w = new Integer(v.getSelectionBounds().width);
				final Integer h = new Integer(v.getSelectionBounds().height);

				if (elem.getName().equals("table"))// resize the table
				{
					// currently jeditorpane only supports the width attrib for
					// tables
					sas.addAttribute(HTML.Attribute.WIDTH, w);
					String html = HTMLUtils.getElementHTML(elem, false);
					html = HTMLUtils.createTag(HTML.Tag.TABLE, sas, html);
					this.replace(elem, html);
				} else if (elem.getName().equals("img"))// resize the img
				{
					sas.addAttribute(HTML.Attribute.WIDTH, w);
					sas.addAttribute(HTML.Attribute.HEIGHT, h);
					String html = "<img";
					for (final Enumeration ee = sas.getAttributeNames(); ee.hasMoreElements();) {
						final Object name = ee.nextElement();
						if (!(name.toString().equals("name") || name.toString().equals("a"))) {
							final Object val = sas.getAttribute(name);
							html += " " + name + "=\"" + val + "\"";
						}
					}
					html += ">";

					if (sas.isDefined(HTML.Tag.A)) {
						html = "<a " + sas.getAttribute(HTML.Tag.A) + ">" + html + "</a>";
					}
					this.replace(elem, html);
				}

				// remove views not appearing in the doc
				this.updateMonitoredViews((HTMLDocument) v.getDocument());
			}

			this.dragStarted = false;
		}

		protected void setCursorForDir(final int d, final Component c) {
			if (d == ResizableView.NW) {
				c.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			} else if (d == ResizableView.SW) {
				c.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			} else if (d == ResizableView.NE) {
				c.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
			} else if (d == ResizableView.SE) {
				c.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			} else if (d == ResizableView.N) {
				c.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			} else if (d == ResizableView.S) {
				c.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
			} else if (d == ResizableView.E) {
				c.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			} else if (d == ResizableView.W) {
				c.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			} else if (c.getCursor().getType() != Cursor.DEFAULT_CURSOR) {
				c.setCursor(Cursor.getDefaultCursor());
			}
		}

		/**
		 * Updates the list of monitored ResizeableViews. If they don't exist in the document, they're
		 * removed from the list.
		 * @param doc
		 */
		protected void updateMonitoredViews(final HTMLDocument doc) {
			for (final Iterator it = OHTMLEditorKit.this.monitoredViews.iterator(); it.hasNext();) {
				final View v = (View) it.next();
				final Element vElem = v.getElement();
				if (vElem.getName().equals("img")) {
					final Element el = doc.getCharacterElement(vElem.getStartOffset());
					if (el != vElem) {
						it.remove();
					}
				} else if (vElem.getName().equals("table")) {
					Element el = doc.getParagraphElement(vElem.getStartOffset());
					// get the parent and check if its the same element
					el = HTMLUtils.getParent(el, HTML.Tag.TABLE);
					// FIXME if the element is a nested table in the first cell
					// of the parent table, the parent view is removed
					if (el != vElem) {
						it.remove();
					}
				}
			}
		}

		/**
		 * Get the currently selected view. Only one view at a time can be selected
		 * @return
		 */
		protected ResizableView getSelectedView() {
			for (final Iterator it = OHTMLEditorKit.this.monitoredViews.iterator(); it.hasNext();) {
				final ResizableView v = (ResizableView) it.next();
				if (v.isSelectionEnabled()) {
					return v;
				}
			}

			return null;
		}

		/**
		 * Replaced the element with the specified html.
		 * @param elem
		 * @param html
		 */
		protected void replace(final Element elem, final String html) {
			final HTMLDocument document = (HTMLDocument) elem.getDocument();
			CompoundUndoManager.beginCompoundEdit(document);
			try {
				document.setOuterHTML(elem, html);
			} catch (final Exception ex) {
				OHTMLEditorKit.logger.error(null, ex);
			}
			CompoundUndoManager.endCompoundEdit(document);
		}

	}

	/**
	 * View which can draw resize handles around its delegate
	 *
	 * @author Imatia S.L.
	 */
	protected class ResizableView extends DelegateView {

		public static final int NW = 0;

		public static final int NE = 1;

		public static final int SW = 2;

		public static final int SE = 3;

		public static final int N = 4;

		public static final int S = 5;

		public static final int E = 6;

		public static final int W = 7;

		protected Rectangle curBounds;

		protected Rectangle selBounds;

		public ResizableView(final View delegate) {
			super(delegate);
		}

		@Override
		public void paint(final Graphics g, final Shape allocation) {
			this.curBounds = new Rectangle(allocation.getBounds());
			this.delegate.paint(g, allocation);
			this.drawSelectionHandles(g);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see javax.swing.text.View#insertUpdate(javax.swing.event.DocumentEvent, java.awt.Shape,
		 * javax.swing.text.ViewFactory)
		 */
		@Override
		public void insertUpdate(final DocumentEvent e, final Shape a, final ViewFactory f) {
			this.setSelectionEnabled(false);
			super.insertUpdate(e, a, f);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see javax.swing.text.View#changedUpdate(javax.swing.event.DocumentEvent, java.awt.Shape,
		 * javax.swing.text.ViewFactory)
		 */
		@Override
		public void changedUpdate(final DocumentEvent e, final Shape a, final ViewFactory f) {
			this.setSelectionEnabled(false);
			super.changedUpdate(e, a, f);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see javax.swing.text.View#removeUpdate(javax.swing.event.DocumentEvent, java.awt.Shape,
		 * javax.swing.text.ViewFactory)
		 */
		@Override
		public void removeUpdate(final DocumentEvent e, final Shape a, final ViewFactory f) {
			this.setSelectionEnabled(false);
			super.removeUpdate(e, a, f);
		}

		/**
		 * Gets the current bounds of the view
		 * @return
		 */
		public Rectangle getBounds() {
			return this.curBounds;
		}

		/**
		 * Gets the Rectangle from which the selection handles are drawn
		 * @return
		 */
		public Rectangle getSelectionBounds() {
			return this.selBounds;
		}

		/**
		 * Draw the selection if true
		 * @param b
		 */
		public void setSelectionEnabled(final boolean b) {
			if (b && (this.curBounds != null)) {
				this.selBounds = new Rectangle(this.curBounds);
			} else {
				this.selBounds = null;
			}
		}

		public boolean isSelectionEnabled() {
			return this.selBounds != null;
		}

		/**
		 * Gets the selection handle at the specified point.
		 * @param p
		 * @return one of NW, SW, NE, SE, N, S, E, W or -1 if a handle is not at the point
		 */
		public int getHandleForPoint(final Point p) {
			if (this.isSelectionEnabled()) {
				final Rectangle r[] = this.computeHandles(this.selBounds);
				for (int i = 0; i < r.length; i++) {
					if (r[i].contains(p)) {
						return i;
					}
				}
			}

			return -1;
		}

		protected void drawSelectionHandles(final Graphics g) {
			if (!this.isSelectionEnabled()) {
				return;
			}

			final Color cached = g.getColor();
			g.setColor(Color.DARK_GRAY);
			g.drawRect(this.selBounds.x, this.selBounds.y, this.selBounds.width, this.selBounds.height);

			final Rectangle h[] = this.computeHandles(this.selBounds);
			for (int i = 0; i < h.length; i++) {
				g.fillRect(h[i].x, h[i].y, h[i].width, h[i].height);
			}
			g.setColor(cached);
		}

		protected Rectangle[] computeHandles(final Rectangle sel) {
			final Rectangle r[] = new Rectangle[8];
			final int sq = 8;
			r[ResizableView.NW] = new Rectangle(sel.x, sel.y, sq, sq);
			r[ResizableView.NE] = new Rectangle((sel.x + sel.width) - sq, sel.y, sq, sq);
			r[ResizableView.SW] = new Rectangle(sel.x, (sel.y + sel.height) - sq, sq, sq);
			r[ResizableView.SE] = new Rectangle((sel.x + sel.width) - sq, (sel.y + sel.height) - sq, sq, sq);

			final int midX = (sel.x + (sel.width / 2)) - (sq / 2);
			final int midY = (sel.y + (sel.height / 2)) - (sq / 2);
			r[ResizableView.N] = new Rectangle(midX, sel.y, sq, sq);
			r[ResizableView.S] = new Rectangle(midX, (sel.y + sel.height) - sq, sq, sq);
			r[ResizableView.E] = new Rectangle((sel.x + sel.width) - sq, midY, sq, sq);
			r[ResizableView.W] = new Rectangle(sel.x, midY, sq, sq);

			return r;
		}

	}

	/**
	 * Delegate view which draws borderless tables.
	 *
	 * This class is a delegate view because javax.swing.text.html.TableView is not public...
	 *
	 * @author Imatia S.L.
	 *
	 */
	protected class BorderlessTableView extends DelegateView {

		public BorderlessTableView(final View delegate) {
			super(delegate);
		}

		@Override
		public void paint(final Graphics g, final Shape allocation) {
			// if the table element has no border,
			// then draw the table outline with a dotted line
			if (this.shouldDrawDottedBorder()) {
				// draw the table background color if set
				// we need to do this for Java 1.5, otherwise
				// the bgcolor doesnt get painted for some reason
				final Color bgColor = this.getTableBgcolor();
				if (bgColor != null) {
					final Color cachedColor = g.getColor();
					g.setColor(bgColor);
					final Rectangle tr = allocation.getBounds();
					g.fillRect(tr.x, tr.y, tr.width, tr.height);
					g.setColor(cachedColor);
				}

				this.delegate.paint(g, allocation);

				// set up the graphics object to draw dotted lines
				final Graphics2D g2 = (Graphics2D) g;
				final Stroke cachedStroke = g2.getStroke();
				final float dash[] = { 3.0f };
				final BasicStroke stroke = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0.0f);
				g2.setStroke(stroke);
				g2.setColor(Color.DARK_GRAY);

				final int rows = this.getViewCount();
				for (int r = 0; r < rows; r++) {
					final Shape rowShape = this.getChildAllocation(r, allocation);
					final View rowView = this.getView(r);
					final int cells = rowView.getViewCount();
					// draw each cell with a dotted border
					for (int c = 0; c < cells; c++) {
						final Shape cellShape = rowView.getChildAllocation(c, rowShape);
						final Rectangle cr = cellShape.getBounds();
						g2.drawRect(cr.x, cr.y, cr.width, cr.height);
					}
				}

				g2.setStroke(cachedStroke);
			} else {
				this.delegate.paint(g, allocation);
			}
		}

		protected Color getTableBgcolor() {
			final AttributeSet atr = this.getElement().getAttributes();
			final Object o = atr.getAttribute(HTML.Attribute.BGCOLOR);
			if (o != null) {
				final Color c = HTMLUtils.stringToColor(o.toString());
				return c;
			}

			return null;
		}

		protected boolean shouldDrawDottedBorder() {
			final AttributeSet atr = this.getElement().getAttributes();
			final boolean isBorderAttr = this.hasBorderAttr(atr);
			return !isBorderAttr || (isBorderAttr && atr.getAttribute(HTML.Attribute.BORDER).toString().equals("0"));
		}

		protected boolean hasBorderAttr(final AttributeSet atr) {
			for (final Enumeration e = atr.getAttributeNames(); e.hasMoreElements();) {
				if (e.nextElement().toString().equals("border")) {
					return true;
				}
			}

			return false;
		}

	}

	/**
	 * A view for {@link Element}s that are uneditable.
	 * <p>
	 * The default view for such {@link Element}s are big ugly blocky JTextFields, which doesn't really
	 * work well in practice and tends to confuse users. This view replaces the default and simply draws
	 * the elements as blocks that indicates the presence of an uneditable {@link Element}.
	 * <p>
	 *
	 * @author Imatia S.L.
	 *
	 */
	protected class UnknownElementView extends ComponentView {

		public UnknownElementView(final Element e) {
			super(e);
		}

		@Override
		protected Component createComponent() {
			final JLabel p = new JLabel();
			if (this.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.COMMENT) {
				p.setText("<!-- -->");
				final AttributeSet as = this.getElement().getAttributes();
				if (as != null) {
					final Object comment = as.getAttribute(HTML.Attribute.COMMENT);
					if (comment instanceof String) {
						p.setToolTipText((String) comment);
					}
				}
			} else {
				String text = this.getElement().getName();
				if ((text == null) || text.equals("")) {
					text = "??";
				}
				if (this.isEndTag()) {
					text = "</" + text + ">";
				} else {
					text = "<" + text + ">";
				}
				p.setText(text);
			}

			p.setBorder(BorderFactory.createRaisedBevelBorder());
			p.setBackground(Color.YELLOW);
			p.setForeground(Color.BLUE);
			p.setOpaque(true);

			return p;
		}

		boolean isEndTag() {
			final AttributeSet as = this.getElement().getAttributes();
			if (as != null) {
				final Object end = as.getAttribute(HTML.Attribute.ENDTAG);
				if ((end != null) && (end instanceof String) && ((String) end).equals("true")) {
					return true;
				}
			}
			return false;
		}

	}

}
