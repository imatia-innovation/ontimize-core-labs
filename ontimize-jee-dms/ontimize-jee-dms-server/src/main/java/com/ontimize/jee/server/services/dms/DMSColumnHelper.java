package com.ontimize.jee.server.services.dms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.jee.common.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.tools.BasicExpressionTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.server.dao.common.INameConvention;


/**
 * This class will manage all behaviour around DMS columns naming. <br>
 * From client side will be used allways configured values in DMSNaming. <br>
 * To dao side will by applied this columns transformation. <br>
 * By default same values. <br>
 * <br>
 * Moreove this allows to have column extensions, indicating extends columns for each dao (document,
 * file, version, ....)
 */
public class DMSColumnHelper{

    private Map<String, String> columnsMapping;

    private INameConvention nameConvention;

    public DMSColumnHelper(INameConvention nameConvention) {
        this.nameConvention = nameConvention;
        initColumnsMapping();
    }


    protected void initColumnsMapping() {

        if (this.columnsMapping == null) {
            this.columnsMapping = new HashMap<>();
        }

        this.addColumnMapping(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT, this.nameConvention.convertName(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT));
        this.addColumnMapping(DMSNaming.DOCUMENT_UPDATE_DATE, this.nameConvention.convertName(DMSNaming.DOCUMENT_UPDATE_DATE));
        this.addColumnMapping(DMSNaming.DOCUMENT_UPDATE_BY, this.nameConvention.convertName(DMSNaming.DOCUMENT_UPDATE_BY));
        this.addColumnMapping(DMSNaming.DOCUMENT_DOCUMENT_NAME, this.nameConvention.convertName(DMSNaming.DOCUMENT_DOCUMENT_NAME));
        this.addColumnMapping(DMSNaming.DOCUMENT_OWNER_ID, this.nameConvention.convertName(DMSNaming.DOCUMENT_OWNER_ID));
        this.addColumnMapping(DMSNaming.DOCUMENT_DOCUMENT_DESCRIPTION, this.nameConvention.convertName(DMSNaming.DOCUMENT_DOCUMENT_DESCRIPTION));
        this.addColumnMapping(DMSNaming.DOCUMENT_DOCUMENT_KEYWORDS, this.nameConvention.convertName(DMSNaming.DOCUMENT_DOCUMENT_KEYWORDS));

        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE,
                this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE));
        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_NAME, this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_NAME));
        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_TYPE, this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_TYPE));

        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION,
                this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION));
        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH, this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH));
        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_VERSION, this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_VERSION));
        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION,
                this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION));
        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE, this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE));
        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE, this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE));
        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL, this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL));
        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_DATE,
                this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_DATE));
        this.addColumnMapping(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_USER,
                this.nameConvention.convertName(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_USER));

        this.addColumnMapping(DMSNaming.RELATED_DOCUMENT_ID_DMS_RELATED_DOCUMENT,
                this.nameConvention.convertName(DMSNaming.RELATED_DOCUMENT_ID_DMS_RELATED_DOCUMENT));
        this.addColumnMapping(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER,
                this.nameConvention.convertName(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER));
        this.addColumnMapping(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_CHILD,
                this.nameConvention.convertName(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_CHILD));

        this.addColumnMapping(DMSNaming.DOCUMENT_PROPERTY_ID_DMS_DOCUMENT_PROPERTY,
                this.nameConvention.convertName(DMSNaming.DOCUMENT_PROPERTY_ID_DMS_DOCUMENT_PROPERTY));
        this.addColumnMapping(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY,
                this.nameConvention.convertName(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY));
        this.addColumnMapping(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE,
                this.nameConvention.convertName(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE));

        this.addColumnMapping(DMSNaming.CATEGORY_ID_CATEGORY, this.nameConvention.convertName(DMSNaming.CATEGORY_ID_CATEGORY));
        this.addColumnMapping(DMSNaming.CATEGORY_CATEGORY_NAME, this.nameConvention.convertName(DMSNaming.CATEGORY_CATEGORY_NAME));
        this.addColumnMapping(DMSNaming.CATEGORY_ID_CATEGORY_PARENT, this.nameConvention.convertName(DMSNaming.CATEGORY_ID_CATEGORY_PARENT));
    }

    public Map<String, String> getColumnsMapping() {
        return this.columnsMapping;
    }

    public void setColumnsMapping(Map<String, String> columnsMapping) {
        this.columnsMapping = columnsMapping;
    }

    public String getColumnEquivalence(String column) {
        return this.columnsMapping.containsKey(column) ? this.columnsMapping.get(column) : column;
    }

    public String getColumnSource(String column) {
        if (!this.columnsMapping.containsValue(column)) {
            return column;
        }
        for (Entry<String, String> entry : this.columnsMapping.entrySet()) {
            if (ObjectTools.safeIsEquals(entry.getValue(), column)) {
                return entry.getKey();
            }
        }
        return column;
    }

    public void addColumnMapping(String column, String daoColumn) {
        this.addColumnMapping(column, daoColumn, false);
    }

    public void addColumnMapping(String column, String daoColumn, boolean ignoreIfExists) {
        MapTools.safePut(this.columnsMapping, column, daoColumn, ignoreIfExists);
    }

    ////// DOCUMENT methods
    ////// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<String> getDocumentColumns() {
        return EntityResultTools.attributes(//
                this.getColumnEquivalence(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_UPDATE_DATE), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_UPDATE_BY), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_DOCUMENT_NAME), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_OWNER_ID), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_DOCUMENT_DESCRIPTION), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_DOCUMENT_KEYWORDS)//
        );
    }

    public String getDocumentIdColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
    }

    public String getDocumentNameColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_DOCUMENT_NAME);
    }

    public String getDocumentOwnerColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_OWNER_ID);
    }

    public String getDocumentUpdateDateColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_UPDATE_DATE);
    }

    public String getDocumentUpdateByColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_UPDATE_BY);
    }

    public String getDocumentDescriptionColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_DOCUMENT_DESCRIPTION);
    }

    public String getDocumentKeywordsColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_DOCUMENT_KEYWORDS);
    }

    ////// FILE methods
    ////// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<String> getFileColumns() {
        return EntityResultTools.attributes(//
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_NAME), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_TYPE), //
                // Foreign columns
                this.getColumnEquivalence(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT), //
                this.getColumnEquivalence(DMSNaming.CATEGORY_ID_CATEGORY)//
        );
    }

    public String getFileIdColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
    }

    public String getFileNameColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_NAME);
    }

    ////// VERSION methods
    ////// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<String> getVersionColumns() {
        return EntityResultTools.attributes(//
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_VERSION), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_DATE), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_USER), //
                // Foreign columns
                this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE)//
        );
    }

    public String getVersionIdColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
    }

    public String getVersionVersionColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_VERSION);
    }

    public String getVersionActiveColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_IS_ACTIVE);
    }

    public String getVersionSizeColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE);
    }

    public String getVersionDescriptionColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_DESCRIPTION);
    }

    public String getVersionPathColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_PATH);
    }

    public String getVersionThumbnailColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_THUMBNAIL);
    }

    public String getVersionAddedDateColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_DATE);
    }

    public String getVersionAddedUserColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_FILE_VERSION_FILE_ADDED_USER);
    }

    ////// CATEGORY methods
    ////// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<String> getCategoryColumns() {
        return EntityResultTools.attributes(//
                this.getColumnEquivalence(DMSNaming.CATEGORY_ID_CATEGORY), //
                this.getColumnEquivalence(DMSNaming.CATEGORY_CATEGORY_NAME), //
                this.getColumnEquivalence(DMSNaming.CATEGORY_ID_CATEGORY_PARENT), //
                // Foreign columns
                this.getColumnEquivalence(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT)//
        );
    }

    public String getCategoryIdColumn() {
        return this.getColumnEquivalence(DMSNaming.CATEGORY_ID_CATEGORY);
    }

    public String getCategoryNameColumn() {
        return this.getColumnEquivalence(DMSNaming.CATEGORY_CATEGORY_NAME);
    }

    public String getCategoryParentColumn() {
        return this.getColumnEquivalence(DMSNaming.CATEGORY_ID_CATEGORY_PARENT);
    }

    ////// PROPERTY methods
    ////// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<String> getPropertyColumns() {
        return EntityResultTools.attributes(//
                this.getColumnEquivalence(DMSNaming.DOCUMENT_PROPERTY_ID_DMS_DOCUMENT_PROPERTY), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY), //
                this.getColumnEquivalence(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE), //
                // Foreign columns
                this.getColumnEquivalence(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT)//
        );
    }

    public String getPropertyIdColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_PROPERTY_ID_DMS_DOCUMENT_PROPERTY);
    }

    public String getPropertyKeyColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_KEY);
    }

    public String getPropertyValueColumn() {
        return this.getColumnEquivalence(DMSNaming.DOCUMENT_PROPERTY_DOCUMENT_PROPERTY_VALUE);
    }

    ////// DOCUMENT RELATIONS methods
    ////// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<String> getDocumentRelatedColumns() {
        return EntityResultTools.attributes(//
                this.getColumnEquivalence(DMSNaming.RELATED_DOCUMENT_ID_DMS_RELATED_DOCUMENT), //
                this.getColumnEquivalence(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER), //
                this.getColumnEquivalence(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_CHILD));
    }

    public String getDocumentRelatedIdColumn() {
        return this.getColumnEquivalence(DMSNaming.RELATED_DOCUMENT_ID_DMS_RELATED_DOCUMENT);
    }

    public String getDocumentRelatedMasterColumn() {
        return this.getColumnEquivalence(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_MASTER);
    }

    public String getDocumentRelatedChildColumn() {
        return this.getColumnEquivalence(DMSNaming.RELATED_DOCUMENT_ID_DMS_DOCUMENT_CHILD);
    }

    public List<?> translate(List<?> attributes) {
        List<Object> newAttributes = new ArrayList<>();
        if (attributes != null) {
            for (Object key : attributes) {
                newAttributes.add(this.getColumnEquivalence((String) key));
            }
        }
        return newAttributes;
    }

    public Map<?, ?> translate(Map<?, ?> criteria) {
        Map<Object, Object> newCriteria = new HashMap<>();
        if (criteria != null) {
            BasicExpression expr = (BasicExpression) criteria.get(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY);
            for (Entry<String, String> entry : this.getColumnsMapping().entrySet()) {
                if (criteria.containsKey(entry.getKey())) {
                    newCriteria.put(entry.getValue(), criteria.get(entry.getKey()));
                } else if (criteria.containsKey(entry.getValue())) {
                    newCriteria.put(entry.getValue(), criteria.get(entry.getValue()));
                }
                if (expr != null) {
                    BasicExpressionTools.renameField(expr, entry.getKey(), entry.getValue());
                    newCriteria.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, expr);
                }
            }
        }
        return newCriteria;
    }

    public EntityResult translateResult(EntityResult query) {
        if (query != null) {
            List<Object> keys = new ArrayList<>(query.keySet());
            for (Object key : keys) {
                EntityResultTools.renameColumn(query, (String) key, this.getColumnSource((String) key));
            }
        }
        return query;
    }

}
