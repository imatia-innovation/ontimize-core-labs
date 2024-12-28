package com.ontimize.util.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.UIResource;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.images.ImageManager;

public class JCollapsibleGroupPanel extends JPanel {

	protected long cycleStart = 0;

	protected int deployTime = 200;

	protected Timer timer = null;

	protected boolean animated = true;

	protected CollapseAction target = null;

	protected JPanel fillerComponent = null;

	protected MouseListener headerMouseListener;

	protected boolean initStateCollapsed = true;

	/**
	 * Attribute to set if just one CollapsibleGroup can be opened or it can be opened more than one. By
	 * default it is allowed to deploy more than one Collpasible Group.
	 */
	protected boolean onlyonedeployed = false;

	public JCollapsibleGroupPanel() {
		this(new Hashtable());
	}

	public JCollapsibleGroupPanel(final Map parameters) {

		final String sStartShowed = (String) parameters.get("startshowed");
		if (sStartShowed != null) {
			if (sStartShowed.equalsIgnoreCase("yes") || sStartShowed.equalsIgnoreCase("true")) {
				this.initStateCollapsed = false;
			} else if (sStartShowed.equalsIgnoreCase("no") || sStartShowed.equalsIgnoreCase("false")) {
				this.initStateCollapsed = true;
			} else {
				this.initStateCollapsed = true;
			}
		}

		final Object anim = parameters.get("anim");
		if (anim == null) {
			this.animated = true;
		} else {
			if (anim.toString().equalsIgnoreCase("no")) {
				this.animated = false;
			} else {
				this.animated = true;
			}
		}

		this.setLayout(new GridBagLayout());
		this.fillerComponent = this.createFillerComponent();

		super.add(this.fillerComponent,
				new GridBagConstraints(0, 0, 1, 1, 1, this.initStateCollapsed ? 1 : 0, GridBagConstraints.NORTH,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.target = new CollapseAction();

		if (this.animated) {
			this.timer = new Timer(35, this.target);
			this.timer.setInitialDelay(0);
		}

		this.headerMouseListener = new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				final Object source = e.getSource();
				if (source instanceof JCollapsibleGroupHeader) {
					((JCollapsibleGroupHeader) source).setClose(!((JCollapsibleGroupHeader) source).isClose());
					final Container parent = SwingUtilities.getAncestorOfClass(JCollapsibleGroup.class, (Component) source);
					if (parent != null) {
						final JCollapsibleGroup group = (JCollapsibleGroup) parent;
						JCollapsibleGroupPanel.this.target.addMenuGroup(group);
					}
				}
			}
		};
	}

	protected JPanel createFillerComponent() {
		final JPanel fillerComponent = new JPanel();
		fillerComponent.setOpaque(true);
		return fillerComponent;
	}

	@Override
	public void revalidate() {
		if (this.getLayout() instanceof GridBagLayout) {
			final Component[] componentList = this.getComponents();
			boolean expand = true;
			for (int i = 0; i < componentList.length; i++) {
				if (!componentList[i].equals(this.fillerComponent)) {
					final GridBagConstraints constraints = ((GridBagLayout) this.getLayout())
							.getConstraints(componentList[i]);
					if (Double.compare(constraints.weighty, 1.0) == 0) {
						expand = false;
						break;
					}
				}
			}
			final GridBagConstraints constraints = ((GridBagLayout) this.getLayout()).getConstraints(this.fillerComponent);
			if (expand) {
				if (this.onlyonedeployed) {
					boolean allCollapsed = true;
					for (int i = 0; i < componentList.length; i++) {
						final Object current = componentList[i];
						if (current instanceof JCollapsibleGroup) {
							if (!((JCollapsibleGroup) current).isCollapsed()) {
								allCollapsed = false;
								break;
							}
						}
					}
					if (allCollapsed) {
						constraints.weighty = 1.0;
					}
				} else {
					constraints.weighty = 1.0;
				}
			} else {
				constraints.weighty = 0.0;
			}
			((GridBagLayout) this.getLayout()).setConstraints(this.fillerComponent, constraints);
		}
		super.revalidate();
	}

	protected class CollapseAction implements ActionListener {

		protected List list = new ArrayList();

		protected boolean start = false;

		public void addMenuGroup(final JCollapsibleGroup group) {

			group.changeDeploy();
			if (!this.list.contains(group)) {
				this.list.add(group);
			}

			final Object parent = SwingUtilities.getAncestorOfClass(JCollapsibleGroupPanel.class, group);
			if (parent instanceof JCollapsibleGroupPanel) {
				final JCollapsibleGroupPanel groupPanel = (JCollapsibleGroupPanel) parent;
				if (groupPanel.isOnlyonedeployed()) {
					final JCollapsibleGroup[] groups = groupPanel.getGroupPanels();
					for (int i = 0; i < groups.length; i++) {
						if (!groups[i].isCollapsed() && !groups[i].attr.equals(group.attr)) {
							groups[i].changeDeploy();
							if (this.list.contains(groups[i])) {
								return;
							}
							this.list.add(groups[i]);
						}
					}
				}
			}

			if (!this.start) {
				this.start = true;
				if (JCollapsibleGroupPanel.this.timer != null) {
					JCollapsibleGroupPanel.this.timer.start();
				}
			}
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			for (int i = 0; i < this.list.size(); i++) {
				final JCollapsibleGroup current = (JCollapsibleGroup) this.list.get(i);
				current.processGridBagConstraints();
				final GridBagConstraints constraints = current.getGridBagConstraints();
				((GridBagLayout) JCollapsibleGroupPanel.this.getLayout()).setConstraints(current, constraints);
				if ((Double.compare(constraints.weighty, 0.0) == 0)
						|| (Double.compare(constraints.weighty, 1.0) == 0)) {
					this.list.remove(current);
				}
			}

			JCollapsibleGroupPanel.this.revalidate();

			if (this.list.isEmpty()) {
				if (JCollapsibleGroupPanel.this.timer != null) {
					JCollapsibleGroupPanel.this.timer.stop();
				}
				this.start = false;
			}
		}

	}

	public boolean isOnlyonedeployed() {
		return this.onlyonedeployed;
	}

	public void setOnlyonedeployed(final boolean onlyonedeployed) {
		this.onlyonedeployed = onlyonedeployed;
	}

	public boolean isAnimated() {
		return this.animated;
	}

	public void setAnimated(final boolean animated) {
		this.animated = animated;
		if (animated) {
			if (this.timer == null) {
				this.timer = new Timer(35, this.target);
				this.timer.setInitialDelay(0);
			}
		} else {
			this.timer = null;
		}
	}

	public JCollapsibleGroup[] getGroupPanels() {
		final List list = new ArrayList();
		final Component[] components = this.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JCollapsibleGroup) {
				list.add(components[i]);
			}
		}
		return (JCollapsibleGroup[]) list.toArray(new JCollapsibleGroup[list.size()]);
	}

	public void addGroupPanel(final JCollapsibleGroup grPanel) {
		if (this.getLayout() instanceof GridBagLayout) {
			grPanel.addHeaderMouserListener(this.headerMouseListener);
			if (this.initStateCollapsed) {
				if (!grPanel.isCollapsed()) {
					grPanel.setCollapsed(false);
				} else {
					grPanel.setCollapsed(true);
				}
			} else {
				grPanel.setCollapsed(true);
			}
			final int count = this.getComponentCount();
			final GridBagConstraints constraints = new GridBagConstraints(0, count - 1, 1, 1, 1,
					this.initStateCollapsed ? 0 : 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
							new Insets(0, 0, 0, 0), 0, 0);
			grPanel.setGridBagConstraints(constraints);
			super.add(grPanel, constraints);
			final GridBagConstraints fillconstraints = ((GridBagLayout) this.getLayout())
					.getConstraints(this.fillerComponent);
			fillconstraints.gridy = count;
			((GridBagLayout) this.getLayout()).setConstraints(this.fillerComponent, fillconstraints);
		}
	}

	public void removeGroupPanel(final JCollapsibleGroup grPanel) {
		if (this.getLayout() instanceof GridBagLayout) {
			grPanel.removeHeaderMouserListener(this.headerMouseListener);
			final int count = this.getComponentCount();
			super.remove(grPanel);
			final GridBagConstraints fillconstraints = ((GridBagLayout) this.getLayout())
					.getConstraints(this.fillerComponent);
			fillconstraints.gridy = count - 1;
			((GridBagLayout) this.getLayout()).setConstraints(this.fillerComponent, fillconstraints);
		}
	}

	public Component add(final Component component, final String title) {
		if (!(component instanceof UIResource)) {
			final JCollapsibleGroup group = new JCollapsibleGroup(component, title);
			this.addGroupPanel(group);
		}
		return component;
	}

	@Override
	public Component add(final Component component) {
		if (this.getLayout() instanceof GridBagLayout) {
			final int count = this.getComponentCount();
			final GridBagConstraints constraints = new GridBagConstraints(0, count - 1, 1, 1, 1,
					this.initStateCollapsed ? 0 : 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
							new Insets(0, 0, 0, 0), 0, 0);
			super.add(component, constraints);
			final GridBagConstraints fillconstraints = ((GridBagLayout) this.getLayout())
					.getConstraints(this.fillerComponent);
			fillconstraints.gridy = count;
			((GridBagLayout) this.getLayout()).setConstraints(this.fillerComponent, fillconstraints);
		}
		return component;
	}

	@Override
	public void add(final Component comp, final Object constraints) {
		if (this.getLayout() instanceof GridBagLayout) {
			final int count = this.getComponentCount();
			((GridBagConstraints) constraints).gridy = count - 1;
			((GridBagConstraints) constraints).weighty = 0.0;
			super.add(comp, constraints);
			final GridBagConstraints fillconstraints = ((GridBagLayout) this.getLayout())
					.getConstraints(this.fillerComponent);
			fillconstraints.gridy = count;
			((GridBagLayout) this.getLayout()).setConstraints(this.fillerComponent, fillconstraints);
		}
	}

	public static class JCollapsibleGroup extends JPanel {

		/**
		 * This variable indicates if the Group is collapsed. By default, the group is collapsed, that is,
		 * is closed.
		 */
		protected boolean collapsed = true;

		protected JCollapsibleGroupHeader header = null;

		protected JScrollPane scroll = null;

		protected JPanel body = null;

		protected String attr;

		protected String title;

		protected GridBagConstraints constraints = null;

		protected JPanel filler = new JPanel();

		public JCollapsibleGroup(final Component comp, final String name) {
			this.setLayout(new BorderLayout());
			this.title = name;
			this.attr = name;
			this.body = new JPanel();
			this.header = this.createCollapsibleGroupHeader(this.title);

			this.body.setLayout(new GridBagLayout());
			this.filler.setLayout(new GridBagLayout());
			this.filler.setOpaque(false);
			this.body.add(this.filler, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			if (comp != null) {
				this.add(comp);
			}

			this.scroll = new JScrollPane(this.body);
			this.scroll.setBorder(null);
			super.add(this.header, BorderLayout.NORTH);
			super.add(this.scroll, BorderLayout.CENTER);
		}

		public JCollapsibleGroup() {
			this((Component) null);
		}

		public JCollapsibleGroup(final Component comp) {
			this(comp, "");
		}

		public JCollapsibleGroup(final Map parameters) {
			this.setLayout(new BorderLayout());
			final Object oAttr = parameters.get("attr");
			if (oAttr != null) {
				this.attr = (String) oAttr;
			}

			final Object oTitle = parameters.get("title");
			if (oTitle != null) {
				this.title = (String) oTitle;
			} else {

				if (oAttr != null) {
					this.title = (String) oAttr;
				} else {
					this.title = "CollapsibleGroup";
				}
			}

			this.header = this.createCollapsibleGroupHeader(this.title);
			this.body = new JPanel();
			this.body.setLayout(new GridBagLayout());
			this.filler.setLayout(new GridBagLayout());
			this.filler.setOpaque(false);
			this.body.add(this.filler, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			this.scroll = new JScrollPane(this.body);
			this.scroll.setBorder(null);

			super.add(this.header, BorderLayout.NORTH);
			super.add(this.scroll, BorderLayout.CENTER);
		}

		public JCollapsibleGroupHeader createCollapsibleGroupHeader(final String title) {
			final JCollapsibleGroupHeader header = new JCollapsibleGroupHeader(title);
			header.setClose(true);
			return header;
		}

		@Override
		public Component add(final Component c) {
			if (this.body.getLayout() instanceof GridBagLayout) {
				final int count = this.body.getComponentCount();
				final GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
				constraints.gridy = count - 1;
				constraints.weighty = 0.0;
				this.body.add(c, constraints);

				final GridBagConstraints fillconstraints = ((GridBagLayout) this.filler.getLayout())
						.getConstraints(this.filler);
				fillconstraints.gridx = 0;
				fillconstraints.gridy = count;
				fillconstraints.weightx = 1.0;
				fillconstraints.weighty = 1.0;
				fillconstraints.anchor = GridBagConstraints.NORTH;
				fillconstraints.fill = GridBagConstraints.BOTH;
				((GridBagLayout) this.body.getLayout()).setConstraints(this.filler, fillconstraints);
			} else {
				super.add(c);
			}
			return c;
		}

		@Override
		public void add(final Component c, Object constraints) {
			if (c instanceof FormComponent) {
				if (this.body.getLayout() instanceof GridBagLayout) {
					final int count = this.body.getComponentCount();
					if (constraints == null) {
						constraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
								GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
					}
					((GridBagConstraints) constraints).gridy = count - 1;
					((GridBagConstraints) constraints).weighty = 0.0;
					this.body.add(c, constraints);

					final GridBagConstraints fillconstraints = ((GridBagLayout) this.filler.getLayout())
							.getConstraints(this.filler);
					fillconstraints.gridx = 0;
					fillconstraints.gridy = count;
					fillconstraints.weightx = 1.0;
					fillconstraints.weighty = 1.0;
					fillconstraints.anchor = GridBagConstraints.NORTH;
					fillconstraints.fill = GridBagConstraints.BOTH;
					((GridBagLayout) this.body.getLayout()).setConstraints(this.filler, fillconstraints);
				}
			}
		}

		public String getTitle() {
			return this.title;
		}

		public JCollapsibleGroupHeader getCollapsibleGroupHeader() {
			return this.header;
		}

		public void setHeader(final JCollapsibleGroupHeader header) {
			this.header = header;
		}

		public void addHeaderMouserListener(final MouseListener listener) {
			this.header.addMouseListener(listener);
		}

		public void removeHeaderMouserListener(final MouseListener listener) {
			this.header.removeMouseListener(listener);
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension headerDimension = this.header.getPreferredSize();
			if (this.collapsed) {
				return headerDimension;
			} else {
				Dimension bodyDimension = null;
				if (this.scroll.getViewport().getView() != null) {
					bodyDimension = this.scroll.getViewport().getView().getPreferredSize();
				}
				if (bodyDimension != null) {
					headerDimension.height = headerDimension.height + bodyDimension.height;
				}
				return headerDimension;
			}
		}

		public void setCollapsed(final boolean value) {
			final boolean oldValue = this.collapsed;
			this.collapsed = value;
			if (this.body != null) {
				if (this.collapsed) {
					this.scroll.setVisible(false);
				} else {
					this.scroll.setVisible(true);
				}
			}
			this.firePropertyChange("collapsed", oldValue, this.collapsed);

		}

		public boolean isCollapsed() {
			return this.collapsed;
		}

		public void changeDeploy() {
			this.setCollapsed(!this.isCollapsed());
		}

		public void processGridBagConstraints() {
			if (this.collapsed) {
				this.constraints.weighty = this.constraints.weighty - 0.1;
				if (this.constraints.weighty < 0.0) {
					this.constraints.weighty = 0.0;
				}
			} else {
				this.constraints.weighty = this.constraints.weighty + 0.1;
				if (this.constraints.weighty > 1.0) {
					this.constraints.weighty = 1.0;
				}
			}
		}

		public void setGridBagConstraints(final GridBagConstraints gbConstraints) {
			this.constraints = gbConstraints;
		}

		public GridBagConstraints getGridBagConstraints() {
			return this.constraints;
		}

		@Override
		public void setOpaque(final boolean isOpaque) {
			super.setOpaque(isOpaque);
			if ((this.body != null) && (this.scroll != null)) {
				this.body.setOpaque(isOpaque);
				this.scroll.setOpaque(isOpaque);
			}
		}

		@Override
		public void setBackground(final Color bg) {
			super.setBackground(bg);
			if ((this.body != null) && (this.scroll != null)) {
				this.body.setBackground(bg);
				this.scroll.setBackground(bg);
			}
		}

	}

	public static class JCollapsibleGroupHeader extends JLabel {

		protected ImageIcon openIcon = ImageManager
				.getIcon("com/ontimize/designer/gui/images/component/folder_open.png");

		protected ImageIcon closeIcon = ImageManager
				.getIcon("com/ontimize/designer/gui/images/component/folder_close.png");

		protected boolean close = true;

		public JCollapsibleGroupHeader(final String name) {
			super(name);
			this.setBorder(new EtchedBorder());
		}

		public void setOpenIcon(final ImageIcon open) {
			this.openIcon = open;
		}

		public void setCloseIcon(final ImageIcon close) {
			this.closeIcon = close;
			this.setIcon(close);
		}

		public boolean isClose() {
			return this.close;
		}

		public String getTitle() {
			return super.getText();
		}

		public void setTitle(final String title) {
			this.setText(title);
		}

		public ImageIcon getOpenIcon() {
			return this.openIcon;
		}

		public ImageIcon getCloseIcon() {
			return this.closeIcon;
		}

		public void setClose(final boolean close) {
			this.close = close;
			if (!close) {
				this.setIcon(this.openIcon);
			} else {
				this.setIcon(this.closeIcon);
			}
			this.revalidate();
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension d = super.getPreferredSize();
			if (this.openIcon != null) {
				d.height = this.openIcon.getIconHeight() + 8;
			} else {
				d.height = 20;
			}
			return d;

		}

	}

}
