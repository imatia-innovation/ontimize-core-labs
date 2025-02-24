package com.ontimize.gui.tree;

import java.awt.Dimension;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompactPositionator implements JOrgTreeNodePositionator {

	private static final Logger logger = LoggerFactory.getLogger(CompactPositionator.class);

	protected boolean calculated = false;

	protected Dimension preferredSize = new Dimension(0, 0);

	protected static boolean DEBUG = false;

	public CompactPositionator() {
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
		final int orientation = tree.getOrientation();
		final int hMargin = tree.getHMargin();
		final int vMargin = tree.getVMargin();
		final int siblingSeparation = tree.getSiblingSeparation();
		final int levelSeparation = tree.getLevelSeparation();
		final int rendererHeight = tree.getRendererHeight();
		final int rendererWidth = tree.getRendererWidth();

		final JOrgTreeNode root = (JOrgTreeNode) tree.getInnerModel().getRoot();
		final Enumeration enumNodes = root.preorderEnumeration();
		List vLevels = new Vector();
		final List vLeafNodes = new Vector();
		final List vNodes = new Vector();
		vLevels = new Vector();

		while (enumNodes.hasMoreElements()) {
			final JOrgTreeNode jtNode = (JOrgTreeNode) enumNodes.nextElement();
			jtNode.setX(0);
			jtNode.setY(0);
			final int nivel = jtNode.getLevel();
			if (nivel == vLevels.size()) {
				vLevels.add(new Vector());
			}
			((List) vLevels.get(nivel)).add(jtNode);

			if (jtNode.isLeaf()) {
				vLeafNodes.add(jtNode);
			}

			vNodes.add(jtNode);
		}

		if ((orientation == JOrgTree.ORIENTATION_UP_DOWN) || (orientation == JOrgTree.ORIENTATION_DOWN_UP)) {

			this.preferredSize.height = ((2 * vMargin) + (vLevels.size() * (rendererHeight + levelSeparation)))
					- levelSeparation;
			this.preferredSize.width = ((2 * hMargin) + (vLeafNodes.size() * (rendererWidth + siblingSeparation)))
					- siblingSeparation;

			// Put leaf nodes from left to right
			final int currentLevel = vLevels.size() - 1;
			for (int i = 0; i < vLeafNodes.size(); i++) {
				final JOrgTreeNode jtNode = (JOrgTreeNode) vLeafNodes.get(i);
				jtNode.setY(vMargin + (jtNode.getLevel() * (rendererHeight + levelSeparation)));
				jtNode.setX(hMargin + (i * (rendererWidth + siblingSeparation)));
			}

			// Put the not leaf nodes from curren level - 1 to 0
			for (int i = currentLevel - 1; i >= 0; i--) {
				final List vLevelI = (List) vLevels.get(i);
				for (int j = 0; j < vLevelI.size(); j++) {
					final JOrgTreeNode jtNode = (JOrgTreeNode) vLevelI.get(j);
					if (!jtNode.isLeaf()) {

						jtNode.setY(vMargin + (i * (rendererHeight + levelSeparation)));

						final int childrenCount = jtNode.getChildCount();
						if (childrenCount == 1) {
							jtNode.setX(((JOrgTreeNode) jtNode.getChildAt(0)).getX());
						} else {
							final int xChildMin = ((JOrgTreeNode) jtNode.getChildAt(0)).getX();
							final int xChildMax = ((JOrgTreeNode) jtNode.getChildAt(childrenCount - 1)).getX()
									+ rendererWidth;
							jtNode.setX(((xChildMin + xChildMax) - rendererWidth) / 2);
						}
					}
				}
			}

			this.compactTreeVertically(root, vLevels, rendererWidth, siblingSeparation);

			// Center the tree horizontally
			this.centerTreeVertically(root, hMargin);

			// Recalculate preferedSize.width
			this.recalculatePreferredWidth(root, hMargin, rendererWidth);

			if (orientation == JOrgTree.ORIENTATION_DOWN_UP) {
				for (int i = 0; i < vNodes.size(); i++) {
					final JOrgTreeNode jtNode = (JOrgTreeNode) vNodes.get(i);
					jtNode.setY(this.preferredSize.height - jtNode.getY() - rendererHeight);
				}
			}
		} else if ((orientation == JOrgTree.ORIENTATION_LEFT_RIGHT)
				|| (orientation == JOrgTree.ORIENTATION_RIGHT_LEFT)) {

			this.preferredSize.width = ((2 * hMargin) + (vLevels.size() * (rendererWidth + levelSeparation)))
					- levelSeparation;
			this.preferredSize.height = ((2 * vMargin) + (vLeafNodes.size() * (rendererHeight + siblingSeparation)))
					- siblingSeparation;

			// Put leaf nodes from top to bottom.
			final int currentLevel = vLevels.size() - 1;
			for (int i = 0; i < vLeafNodes.size(); i++) {
				final JOrgTreeNode jtNode = (JOrgTreeNode) vLeafNodes.get(i);
				jtNode.setX(hMargin + (jtNode.getLevel() * (rendererWidth + levelSeparation)));
				jtNode.setY(vMargin + (i * (rendererHeight + siblingSeparation)));
			}

			// Put the not leaf nodes from current level - 1 to 0
			for (int i = currentLevel - 1; i >= 0; i--) {
				final List vLevelI = (List) vLevels.get(i);
				for (int j = 0; j < vLevelI.size(); j++) {
					final JOrgTreeNode jtNode = (JOrgTreeNode) vLevelI.get(j);
					if (!jtNode.isLeaf()) {

						jtNode.setX(hMargin + (i * (rendererWidth + levelSeparation)));

						final int childrenCount = jtNode.getChildCount();
						if (childrenCount == 1) {
							jtNode.setY(((JOrgTreeNode) jtNode.getChildAt(0)).getY());
						} else {
							final int yChildMin = ((JOrgTreeNode) jtNode.getChildAt(0)).getY();
							final int yChildMax = ((JOrgTreeNode) jtNode.getChildAt(childrenCount - 1)).getY()
									+ rendererHeight;
							jtNode.setY(((yChildMin + yChildMax) - rendererHeight) / 2);
						}
					}
				}
			}

			this.compactTreeHorizontally(root, vLevels, rendererHeight, siblingSeparation);

			// Center the tree horizontally
			this.centerTreeHorizontally(root, vMargin);

			// Recalculate preferedSize.width
			this.recalculatePreferredHeight(root, vMargin, rendererHeight);

			if (orientation == JOrgTree.ORIENTATION_RIGHT_LEFT) {
				for (int i = 0; i < vNodes.size(); i++) {
					final JOrgTreeNode node = (JOrgTreeNode) vNodes.get(i);
					node.setX(this.preferredSize.width - node.getX() - rendererWidth);
				}
			}
		}

		this.calculated = true;
	}

	public void centerTreeVertically(final JOrgTreeNode root, final int hMargin) {
		Enumeration enumNode = root.preorderEnumeration();
		int xMin = hMargin;
		while (enumNode.hasMoreElements()) {
			final JOrgTreeNode node = (JOrgTreeNode) enumNode.nextElement();
			xMin = Math.min(xMin, node.getX());
		}

		if (xMin < hMargin) {
			final int offset = hMargin - xMin;
			enumNode = root.preorderEnumeration();
			while (enumNode.hasMoreElements()) {
				final JOrgTreeNode node = (JOrgTreeNode) enumNode.nextElement();
				node.setX(node.getX() + offset);
			}
		}
	}

	public void centerTreeHorizontally(final JOrgTreeNode root, final int vMargin) {
		Enumeration enumNode = root.preorderEnumeration();
		int yMin = vMargin;
		while (enumNode.hasMoreElements()) {
			final JOrgTreeNode jtNode = (JOrgTreeNode) enumNode.nextElement();
			yMin = Math.min(yMin, jtNode.getY());
		}

		if (yMin < vMargin) {
			final int offset = vMargin - yMin;
			enumNode = root.preorderEnumeration();
			while (enumNode.hasMoreElements()) {
				final JOrgTreeNode node = (JOrgTreeNode) enumNode.nextElement();
				node.setY(node.getY() + offset);
			}
		}
	}

	protected void recalculatePreferredWidth(final JOrgTreeNode root, final int hMargin, final int rendererWidth) {
		final Enumeration enumNode = root.preorderEnumeration();
		int xMax = 0;
		while (enumNode.hasMoreElements()) {
			final JOrgTreeNode node = (JOrgTreeNode) enumNode.nextElement();
			xMax = Math.max(xMax, node.getX());
		}
		this.preferredSize.width = xMax + rendererWidth + hMargin;
	}

	protected void recalculatePreferredHeight(final JOrgTreeNode root, final int vMargin, final int rendererHeight) {
		final Enumeration enumNode = root.preorderEnumeration();
		int yMax = 0;
		while (enumNode.hasMoreElements()) {
			final JOrgTreeNode jtNode = (JOrgTreeNode) enumNode.nextElement();
			yMax = Math.max(yMax, jtNode.getY());
		}
		this.preferredSize.height = yMax + rendererHeight + vMargin;
	}

	public void compactTreeVertically(final JOrgTreeNode root, final List levels, final int rendererWidth, final int siblingSeparation) {
		final int childrenCount = root.getChildCount();

		// Compact the subtrees
		for (int i = 0; i < childrenCount; i++) {
			this.compactTreeVertically((JOrgTreeNode) root.getChildAt(i), levels, rendererWidth, siblingSeparation);
		}

		// Try to move subtrees to the left except the first one
		// Start with the second on the left
		for (int i = 1; i < childrenCount; i++) {
			final JOrgTreeNode childNode = (JOrgTreeNode) root.getChildAt(i);
			this.compactSubTreeLeft(childNode, levels, rendererWidth, siblingSeparation);
		}

		// Recalculate the root node position
		if (childrenCount == 1) {
			root.setX(((JOrgTreeNode) root.getChildAt(0)).getX());
		} else if (childrenCount > 1) {
			final int xChildMin = ((JOrgTreeNode) root.getChildAt(0)).getX();
			final int xChildMax = ((JOrgTreeNode) root.getChildAt(childrenCount - 1)).getX() + rendererWidth;
			root.setX(((xChildMin + xChildMax) - rendererWidth) / 2);
		}

	}

	public void compactTreeHorizontally(final JOrgTreeNode root, final List levels, final int rendererHeight, final int siblingSeparation) {
		final int iChildCount = root.getChildCount();

		// Compact subtrees
		for (int i = 0; i < iChildCount; i++) {
			this.compactTreeHorizontally((JOrgTreeNode) root.getChildAt(i), levels, rendererHeight, siblingSeparation);
		}

		// Try to move subtrees to the left except the first one
		// Start with the second on the top
		for (int i = 1; i < iChildCount; i++) {
			final JOrgTreeNode childNode = (JOrgTreeNode) root.getChildAt(i);
			this.compactSubTreeUp(childNode, levels, rendererHeight, siblingSeparation);
		}

		// Recalculate the root node position
		if (iChildCount == 1) {
			root.setY(((JOrgTreeNode) root.getChildAt(0)).getY());
		} else if (iChildCount > 1) {
			final int yChildMin = ((JOrgTreeNode) root.getChildAt(0)).getY();
			final int yChildMax = ((JOrgTreeNode) root.getChildAt(iChildCount - 1)).getY() + rendererHeight;
			root.setY(((yChildMin + yChildMax) - rendererHeight) / 2);
		}

	}

	public void compactSubTreeLeft(final JOrgTreeNode root, final List levels, final int rendererWidth, final int siblingSeparation) {
		final List vLeftNodes = this.getLeftNodes(root);
		boolean bRightNode = false;
		int horizontalMinimumDistance = 0;

		if (CompactPositionator.DEBUG) {
			CompactPositionator.logger.debug("COMPACTING SUBTREE TO THE LEFT: " + root);
		}

		for (int i = 0; i < vLeftNodes.size(); i++) {
			final JOrgTreeNode jtNode = (JOrgTreeNode) vLeftNodes.get(i);
			final int nivel = jtNode.getLevel();
			final List vLevelNodes = (List) levels.get(nivel);

			final int iIndex = vLevelNodes.indexOf(jtNode);
			if (iIndex > 0) {
				final JOrgTreeNode jtLeftNode = (JOrgTreeNode) vLevelNodes.get(iIndex - 1);
				final int horizontalDistance = jtNode.getX() - (jtLeftNode.getX() + rendererWidth);
				if (!bRightNode) {
					bRightNode = true;
					horizontalMinimumDistance = horizontalDistance;
				} else {
					horizontalMinimumDistance = Math.min(horizontalMinimumDistance, horizontalDistance);
				}
			}
		}

		if (CompactPositionator.DEBUG) {
			CompactPositionator.logger.debug(" horizontal minimum distance: " + horizontalMinimumDistance
					+ " siblingSeparation: " + siblingSeparation);
		}

		// If bRightNode is true then move to the left until
		// horizontalMinimumDistance=siblingSeparation
		if (bRightNode && (horizontalMinimumDistance > siblingSeparation)) {
			final int offset = -(horizontalMinimumDistance - siblingSeparation);

			final Enumeration enumNodes = root.preorderEnumeration();
			while (enumNodes.hasMoreElements()) {
				final JOrgTreeNode jtNode = (JOrgTreeNode) enumNodes.nextElement();
				jtNode.setX(jtNode.getX() + offset);
				if (CompactPositionator.DEBUG) {
					CompactPositionator.logger.debug("  offset applied: " + offset);
				}
			}
		}
	}

	public void compactSubTreeUp(final JOrgTreeNode root, final List levels, final int rendererHeight, final int siblingSeparation) {
		// Left is bottom and right is top
		// For left to right orientation
		final List vTopNodes = this.getLeftNodes(root);
		boolean bTopNode = false;
		int minimumVerticalDistance = 0;

		if (CompactPositionator.DEBUG) {
			CompactPositionator.logger.debug("COMPACTING SUBTREE UPWARDS: " + root);
		}

		for (int i = 0; i < vTopNodes.size(); i++) {
			final JOrgTreeNode jtNode = (JOrgTreeNode) vTopNodes.get(i);
			final int nivel = jtNode.getLevel();
			final List vLevelNodes = (List) levels.get(nivel);

			final int index = vLevelNodes.indexOf(jtNode);
			if (index > 0) {
				final JOrgTreeNode topNode = (JOrgTreeNode) vLevelNodes.get(index - 1);
				final int verticalDistance = jtNode.getY() - (topNode.getY() + rendererHeight);
				if (!bTopNode) {
					bTopNode = true;
					minimumVerticalDistance = verticalDistance;
				} else {
					minimumVerticalDistance = Math.min(minimumVerticalDistance, verticalDistance);
				}
			}
		}

		// Move up until minimumVerticalDistance=siblingSeparation
		// only if bTopNode is true
		if (bTopNode && (minimumVerticalDistance > siblingSeparation)) {
			final int offset = -(minimumVerticalDistance - siblingSeparation);

			final Enumeration enumNodes = root.preorderEnumeration();
			while (enumNodes.hasMoreElements()) {
				final JOrgTreeNode jtNode = (JOrgTreeNode) enumNodes.nextElement();
				jtNode.setY(jtNode.getY() + offset);
				if (CompactPositionator.DEBUG) {
					CompactPositionator.logger.debug(" offset applied: " + offset);
				}
			}
		}
	}

	public List getLeftNodes(final JOrgTreeNode root) {
		final List v = new Vector();
		final int rootLevel = root.getLevel();

		final List vLevels = new Vector();
		final Enumeration enumNodes = root.preorderEnumeration();
		while (enumNodes.hasMoreElements()) {
			final JOrgTreeNode jtNode = (JOrgTreeNode) enumNodes.nextElement();
			final int level = jtNode.getLevel() - rootLevel;
			if (level == vLevels.size()) {
				vLevels.add(new Vector());
			}
			((List) vLevels.get(level)).add(jtNode);
		}

		if (CompactPositionator.DEBUG) {
			CompactPositionator.logger
			.debug(this.getClass().getName() + "getLeftNodes: node: " + root + " levels: " + vLevels);
		}

		for (int i = 0; i < vLevels.size(); i++) {
			final List nivelI = (List) vLevels.get(i);
			v.add(nivelI.get(0));
		}
		if (CompactPositionator.DEBUG) {
			CompactPositionator.logger
			.debug(this.getClass().getName() + "getLeftNodes: node: " + root + " left nodes: " + v);
		}
		return v;
	}

	public List getRightNodes(final JOrgTreeNode root) {
		final List v = new Vector();
		final int rootLevel = root.getLevel();

		final List vLevels = new Vector();
		final Enumeration enumNodes = root.preorderEnumeration();
		while (enumNodes.hasMoreElements()) {
			final JOrgTreeNode jtNode = (JOrgTreeNode) enumNodes.nextElement();
			final int level = jtNode.getLevel() - rootLevel;
			if (level == vLevels.size()) {
				vLevels.add(new Vector());
			}
			((List) vLevels.get(level)).add(jtNode);
		}

		for (int i = 0; i < vLevels.size(); i++) {
			final List vLevelI = (List) vLevels.get(i);
			v.add(vLevelI.get(vLevelI.size() - 1));
		}

		return v;
	}

	@Override
	public boolean calculated() {
		return this.calculated;
	}

}
