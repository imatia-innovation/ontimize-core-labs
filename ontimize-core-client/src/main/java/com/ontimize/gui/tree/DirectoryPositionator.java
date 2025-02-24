package com.ontimize.gui.tree;

import java.awt.Dimension;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class DirectoryPositionator implements JOrgTreeNodePositionator {

	protected boolean calculated = false;

	protected List levels = null;

	protected Dimension preferredSize = new Dimension(0, 0);

	protected int ORIENTATION_UP_RIGHT = JOrgTree.ORIENTATION_UP_DOWN;

	protected int ORIENTATION_UP_LEFT = JOrgTree.ORIENTATION_RIGHT_LEFT;

	protected int ORIENTATION_DOWN_RIGHT = JOrgTree.ORIENTATION_DOWN_UP;

	protected int ORIENTATION_DOWN_LEFT = JOrgTree.ORIENTATION_LEFT_RIGHT;

	public DirectoryPositionator() {
	}

	@Override
	public Dimension getPreferredSize() {
		if (this.calculated) {
			return this.preferredSize;
		} else {
			return new Dimension(0, 0);
		}
	}

	@Override
	public void calculateNodePositions(final JOrgTree tree) {
		this.calculated = false;
		this.levels = new Vector();
		final int orientation = tree.getOrientation();
		final int hMargin = tree.getHMargin();
		final int vMargin = tree.getVMargin();
		final int siblingSeparation = tree.getSiblingSeparation();
		final int levelSeparation = tree.getLevelSeparation();
		final int rendererHeight = tree.getRendererHeight();
		final int rendererWidth = tree.getRendererWidth();

		final JOrgTreeNode root = (JOrgTreeNode) tree.getInnerModel().getRoot();
		final Enumeration enumNodes = root.preorderEnumeration();
		final List vNodes = new Vector();
		while (enumNodes.hasMoreElements()) {
			final JOrgTreeNode jtNode = (JOrgTreeNode) enumNodes.nextElement();
			vNodes.add(jtNode);
		}

		int xMax = 0;
		int yMax = 0;

		for (int i = 0; i < vNodes.size(); i++) {
			final JOrgTreeNode jtNode = (JOrgTreeNode) vNodes.get(i);

			final int nivel = jtNode.getLevel();
			jtNode.setX(hMargin + (nivel * (siblingSeparation + rendererWidth)));

			jtNode.setY(vMargin + (i * (levelSeparation + rendererHeight)));

			xMax = Math.max(xMax, jtNode.getX());
			yMax = Math.max(yMax, jtNode.getY());

		}

		this.preferredSize.width = xMax + rendererWidth + hMargin;
		this.preferredSize.height = yMax + rendererHeight + vMargin;

		if (orientation == this.ORIENTATION_UP_LEFT) {
			for (int i = 0; i < vNodes.size(); i++) {
				final JOrgTreeNode jtNode = (JOrgTreeNode) vNodes.get(i);
				jtNode.setX(this.preferredSize.width - jtNode.getX() - rendererWidth);
			}
		} else if (orientation == this.ORIENTATION_DOWN_RIGHT) {
			for (int i = 0; i < vNodes.size(); i++) {
				final JOrgTreeNode jtNode = (JOrgTreeNode) vNodes.get(i);
				jtNode.setY(this.preferredSize.height - jtNode.getY() - rendererHeight);
			}
		} else if (orientation == this.ORIENTATION_DOWN_LEFT) {
			for (int i = 0; i < vNodes.size(); i++) {
				final JOrgTreeNode jtNode = (JOrgTreeNode) vNodes.get(i);
				jtNode.setX(this.preferredSize.width - jtNode.getX() - rendererWidth);
				jtNode.setY(this.preferredSize.height - jtNode.getY() - rendererHeight);
			}
		}

		this.calculated = true;
	}

	@Override
	public boolean calculated() {
		return this.calculated;
	}

}
