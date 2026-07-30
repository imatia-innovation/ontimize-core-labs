package com.ontimize.jee.common.services.dms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class DMSCategory.
 */
public class DMSCategory implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id category. */
    private Serializable idCategory;

    /** The id document. */
    private Serializable idDocument;

    /** The parent. */
    private DMSCategory parent;

    /** The name. */
    private String name;

    /** The other info. */
    private Map<? extends Serializable, ? extends Serializable> otherInfo;

    /** The children. */
    private List<DMSCategory> children;

    /**
     * Instantiates a new DMS category.
     */
    public DMSCategory() {
        super();
        this.otherInfo = new HashMap<>();
        this.children = new ArrayList<>();
    }

    /**
     * Instantiates a new DMS category.
     * @param idDocument the id document
     * @param idCategory the id category
     * @param name the name
     * @param otherInfo the other info
     * @param parent the parent
     */
    public DMSCategory(Serializable idDocument, Serializable idCategory, String name,
            Map<? extends Serializable, ? extends Serializable> otherInfo, DMSCategory parent) {
        super();
        this.idCategory = idCategory;
        this.name = name;
        this.otherInfo = otherInfo == null ? new HashMap<Serializable, Serializable>() : otherInfo;
        this.children = new ArrayList<>();
        this.idDocument = idDocument;
        this.parent = parent;
    }

    /**
     * Sets the id category.
     * @param idCategory the new id category
     */
    public void setIdCategory(Serializable idCategory) {
        this.idCategory = idCategory;
    }

    /**
     * Sets the name.
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the other info.
     * @param otherInfo the other info
     */
    public void setOtherInfo(Map<? extends Serializable, ? extends Serializable> otherInfo) {
        this.otherInfo = otherInfo == null ? new HashMap<Serializable, Serializable>() : otherInfo;
    }

    /**
     * Gets the id category.
     * @return the id category
     */
    public Serializable getIdCategory() {
        return this.idCategory;
    }

    /**
     * Gets the name.
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the other info.
     * @return the other info
     */
    public Map<? extends Serializable, ? extends Serializable> getOtherInfo() {
        return this.otherInfo;
    }

    /**
     * Sets the children.
     * @param children the new children
     */
    public void setChildren(List<DMSCategory> children) {
        this.children = children == null ? new ArrayList<DMSCategory>() : children;
    }

    /**
     * Gets the children.
     * @return the children
     */
    public List<DMSCategory> getChildren() {
        return this.children;
    }

    /**
     * Adds the children.
     * @param child the child
     */
    public void addChildren(DMSCategory child) {
        this.getChildren().add(child);
    }

    /**
     * Removes the children.
     * @param child the child
     */
    public void removeChildren(DMSCategory child) {
        this.getChildren().remove(child);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.idCategory).append(":").append(this.name);
        return sb.toString();
    }

    /**
     * Gets the id document.
     * @return the id document
     */
    public Serializable getIdDocument() {
        return this.idDocument;
    }

    /**
     * Sets the id document.
     * @param idDocument the new id document
     */
    public void setIdDocument(Serializable idDocument) {
        this.idDocument = idDocument;
    }

    /**
     * Gets the parent.
     * @return the parent
     */
    public DMSCategory getParent() {
        return this.parent;
    }

}
