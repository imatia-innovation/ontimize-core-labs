package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.ClassInfo;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.File.ImageMediaMetadata;
import com.google.api.services.drive.model.File.IndexableText;
import com.google.api.services.drive.model.File.Labels;
import com.google.api.services.drive.model.File.Thumbnail;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.Property;
import com.google.api.services.drive.model.User;

public class GoogleFile implements Serializable {

	private static final long	serialVersionUID	= 1L;
	// TODO ver como serializarlo
	private transient File		file;

	public GoogleFile(File file2) {
		super();
		this.file = file2;
	}

	public GoogleFile() {
		this(new File());
	}

	public File getFile() {
		return this.file;
	}

	/**
	 * @return
	 * @see com.google.api.client.json.GenericJson#getFactory()
	 */
	public final JsonFactory getFactory() {
		return this.file.getFactory();
	}

	/**
	 * @param factory
	 * @see com.google.api.client.json.GenericJson#setFactory(com.google.api.client.json.JsonFactory)
	 */
	public final void setFactory(JsonFactory factory) {
		this.file.setFactory(factory);
	}

	/**
	 * @return
	 * @see com.google.api.client.json.GenericJson#toString()
	 */
	@Override
	public String toString() {
		return this.file.toString();
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#size()
	 */
	public int size() {
		return this.file.size();
	}

	/**
	 * @return
	 * @throws IOException
	 * @see com.google.api.client.json.GenericJson#toPrettyString()
	 */
	public String toPrettyString() throws IOException {
		return this.file.toPrettyString();
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#isEmpty()
	 */
	public boolean isEmpty() {
		return this.file.isEmpty();
	}

	/**
	 * @param name
	 * @return
	 * @see com.google.api.client.util.GenericData#get(java.lang.Object)
	 */
	public final Object get(Object name) {
		return this.file.get(name);
	}

	/**
	 * @param value
	 * @return
	 * @see java.util.AbstractMap#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return this.file.containsValue(value);
	}

	/**
	 * @param fieldName
	 * @param value
	 * @return
	 * @see com.google.api.client.util.GenericData#put(java.lang.String, java.lang.Object)
	 */
	public final Object put(String fieldName, Object value) {
		return this.file.put(fieldName, value);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.AbstractMap#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return this.file.containsKey(key);
	}

	/**
	 * @param map
	 * @see com.google.api.client.util.GenericData#putAll(java.util.Map)
	 */
	public final void putAll(Map<? extends String, ?> map) {
		this.file.putAll(map);
	}

	/**
	 * @param name
	 * @return
	 * @see com.google.api.client.util.GenericData#remove(java.lang.Object)
	 */
	public final Object remove(Object name) {
		return this.file.remove(name);
	}

	/**
	 * @return
	 * @see com.google.api.client.util.GenericData#entrySet()
	 */
	public Set<Entry<String, Object>> entrySet() {
		return this.file.entrySet();
	}

	/**
	 * @return
	 * @see com.google.api.client.util.GenericData#getUnknownKeys()
	 */
	public final Map<String, Object> getUnknownKeys() {
		return this.file.getUnknownKeys();
	}

	/**
	 * @param unknownFields
	 * @see com.google.api.client.util.GenericData#setUnknownKeys(java.util.Map)
	 */
	public final void setUnknownKeys(Map<String, Object> unknownFields) {
		this.file.setUnknownKeys(unknownFields);
	}

	/**
	 * @return
	 * @see com.google.api.client.util.GenericData#getClassInfo()
	 */
	public final ClassInfo getClassInfo() {
		return this.file.getClassInfo();
	}

	/**
	 *
	 * @see java.util.AbstractMap#clear()
	 */
	public void clear() {
		this.file.clear();
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#keySet()
	 */
	public Set<String> keySet() {
		return this.file.keySet();
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#values()
	 */
	public Collection<Object> values() {
		return this.file.values();
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getAlternateLink()
	 */
	public String getAlternateLink() {
		return this.file.getAlternateLink();
	}

	/**
	 * @param alternateLink
	 * @return
	 * @see com.google.api.services.drive.model.File#setAlternateLink(java.lang.String)
	 */
	public GoogleFile setAlternateLink(String alternateLink) {
		this.file.setAlternateLink(alternateLink);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getAppDataContents()
	 */
	public Boolean getAppDataContents() {
		return this.file.getAppDataContents();
	}

	/**
	 * @param appDataContents
	 * @return
	 * @see com.google.api.services.drive.model.File#setAppDataContents(java.lang.Boolean)
	 */
	public GoogleFile setAppDataContents(Boolean appDataContents) {
		this.file.setAppDataContents(appDataContents);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getCopyable()
	 */
	public Boolean getCopyable() {
		return this.file.getCopyable();
	}

	/**
	 * @param copyable
	 * @return
	 * @see com.google.api.services.drive.model.File#setCopyable(java.lang.Boolean)
	 */
	public GoogleFile setCopyable(Boolean copyable) {
		this.file.setCopyable(copyable);
		return this;
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.AbstractMap#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		return this.file.equals(o);
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getCreatedDate()
	 */
	public DateTime getCreatedDate() {
		return this.file.getCreatedDate();
	}

	/**
	 * @param createdDate
	 * @return
	 * @see com.google.api.services.drive.model.File#setCreatedDate(com.google.api.client.util.DateTime)
	 */
	public GoogleFile setCreatedDate(DateTime createdDate) {
		this.file.setCreatedDate(createdDate);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getDefaultOpenWithLink()
	 */
	public String getDefaultOpenWithLink() {
		return this.file.getDefaultOpenWithLink();
	}

	/**
	 * @param defaultOpenWithLink
	 * @return
	 * @see com.google.api.services.drive.model.File#setDefaultOpenWithLink(java.lang.String)
	 */
	public GoogleFile setDefaultOpenWithLink(String defaultOpenWithLink) {
		this.file.setDefaultOpenWithLink(defaultOpenWithLink);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getDescription()
	 */
	public String getDescription() {
		return this.file.getDescription();
	}

	/**
	 * @param description
	 * @return
	 * @see com.google.api.services.drive.model.File#setDescription(java.lang.String)
	 */
	public GoogleFile setDescription(String description) {
		this.file.setDescription(description);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getDownloadUrl()
	 */
	public String getDownloadUrl() {
		return this.file.getDownloadUrl();
	}

	/**
	 * @param downloadUrl
	 * @return
	 * @see com.google.api.services.drive.model.File#setDownloadUrl(java.lang.String)
	 */
	public GoogleFile setDownloadUrl(String downloadUrl) {
		this.file.setDownloadUrl(downloadUrl);
		return this;
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.file.hashCode();
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getEditable()
	 */
	public Boolean getEditable() {
		return this.file.getEditable();
	}

	/**
	 * @param editable
	 * @return
	 * @see com.google.api.services.drive.model.File#setEditable(java.lang.Boolean)
	 */
	public GoogleFile setEditable(Boolean editable) {
		this.file.setEditable(editable);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getEmbedLink()
	 */
	public String getEmbedLink() {
		return this.file.getEmbedLink();
	}

	/**
	 * @param embedLink
	 * @return
	 * @see com.google.api.services.drive.model.File#setEmbedLink(java.lang.String)
	 */
	public GoogleFile setEmbedLink(String embedLink) {
		this.file.setEmbedLink(embedLink);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getEtag()
	 */
	public String getEtag() {
		return this.file.getEtag();
	}

	/**
	 * @param etag
	 * @return
	 * @see com.google.api.services.drive.model.File#setEtag(java.lang.String)
	 */
	public GoogleFile setEtag(String etag) {
		this.file.setEtag(etag);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getExplicitlyTrashed()
	 */
	public Boolean getExplicitlyTrashed() {
		return this.file.getExplicitlyTrashed();
	}

	/**
	 * @param explicitlyTrashed
	 * @return
	 * @see com.google.api.services.drive.model.File#setExplicitlyTrashed(java.lang.Boolean)
	 */
	public GoogleFile setExplicitlyTrashed(Boolean explicitlyTrashed) {
		this.file.setExplicitlyTrashed(explicitlyTrashed);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getExportLinks()
	 */
	public Map<String, String> getExportLinks() {
		return this.file.getExportLinks();
	}

	/**
	 * @param exportLinks
	 * @return
	 * @see com.google.api.services.drive.model.File#setExportLinks(java.util.Map)
	 */
	public GoogleFile setExportLinks(Map<String, String> exportLinks) {
		this.file.setExportLinks(exportLinks);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getFileExtension()
	 */
	public String getFileExtension() {
		return this.file.getFileExtension();
	}

	/**
	 * @param fileExtension
	 * @return
	 * @see com.google.api.services.drive.model.File#setFileExtension(java.lang.String)
	 */
	public GoogleFile setFileExtension(String fileExtension) {
		this.file.setFileExtension(fileExtension);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getFileSize()
	 */
	public Long getFileSize() {
		return this.file.getFileSize();
	}

	/**
	 * @param fileSize
	 * @return
	 * @see com.google.api.services.drive.model.File#setFileSize(java.lang.Long)
	 */
	public GoogleFile setFileSize(Long fileSize) {
		this.file.setFileSize(fileSize);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getHeadRevisionId()
	 */
	public String getHeadRevisionId() {
		return this.file.getHeadRevisionId();
	}

	/**
	 * @param headRevisionId
	 * @return
	 * @see com.google.api.services.drive.model.File#setHeadRevisionId(java.lang.String)
	 */
	public GoogleFile setHeadRevisionId(String headRevisionId) {
		this.file.setHeadRevisionId(headRevisionId);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getIconLink()
	 */
	public String getIconLink() {
		return this.file.getIconLink();
	}

	/**
	 * @param iconLink
	 * @return
	 * @see com.google.api.services.drive.model.File#setIconLink(java.lang.String)
	 */
	public GoogleFile setIconLink(String iconLink) {
		this.file.setIconLink(iconLink);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getId()
	 */
	public String getId() {
		return this.file.getId();
	}

	/**
	 * @param id
	 * @return
	 * @see com.google.api.services.drive.model.File#setId(java.lang.String)
	 */
	public GoogleFile setId(String id) {
		this.file.setId(id);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getImageMediaMetadata()
	 */
	public ImageMediaMetadata getImageMediaMetadata() {
		return this.file.getImageMediaMetadata();
	}

	/**
	 * @param imageMediaMetadata
	 * @return
	 * @see com.google.api.services.drive.model.File#setImageMediaMetadata(com.google.api.services.drive.model.File.ImageMediaMetadata)
	 */
	public GoogleFile setImageMediaMetadata(ImageMediaMetadata imageMediaMetadata) {
		this.file.setImageMediaMetadata(imageMediaMetadata);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getIndexableText()
	 */
	public IndexableText getIndexableText() {
		return this.file.getIndexableText();
	}

	/**
	 * @param indexableText
	 * @return
	 * @see com.google.api.services.drive.model.File#setIndexableText(com.google.api.services.drive.model.File.IndexableText)
	 */
	public GoogleFile setIndexableText(IndexableText indexableText) {
		this.file.setIndexableText(indexableText);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getKind()
	 */
	public String getKind() {
		return this.file.getKind();
	}

	/**
	 * @param kind
	 * @return
	 * @see com.google.api.services.drive.model.File#setKind(java.lang.String)
	 */
	public GoogleFile setKind(String kind) {
		this.file.setKind(kind);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getLabels()
	 */
	public Labels getLabels() {
		return this.file.getLabels();
	}

	/**
	 * @param labels
	 * @return
	 * @see com.google.api.services.drive.model.File#setLabels(com.google.api.services.drive.model.File.Labels)
	 */
	public GoogleFile setLabels(Labels labels) {
		this.file.setLabels(labels);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getLastModifyingUser()
	 */
	public User getLastModifyingUser() {
		return this.file.getLastModifyingUser();
	}

	/**
	 * @param lastModifyingUser
	 * @return
	 * @see com.google.api.services.drive.model.File#setLastModifyingUser(com.google.api.services.drive.model.User)
	 */
	public GoogleFile setLastModifyingUser(User lastModifyingUser) {
		this.file.setLastModifyingUser(lastModifyingUser);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getLastModifyingUserName()
	 */
	public String getLastModifyingUserName() {
		return this.file.getLastModifyingUserName();
	}

	/**
	 * @param lastModifyingUserName
	 * @return
	 * @see com.google.api.services.drive.model.File#setLastModifyingUserName(java.lang.String)
	 */
	public GoogleFile setLastModifyingUserName(String lastModifyingUserName) {
		this.file.setLastModifyingUserName(lastModifyingUserName);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getLastViewedByMeDate()
	 */
	public DateTime getLastViewedByMeDate() {
		return this.file.getLastViewedByMeDate();
	}

	/**
	 * @param lastViewedByMeDate
	 * @return
	 * @see com.google.api.services.drive.model.File#setLastViewedByMeDate(com.google.api.client.util.DateTime)
	 */
	public GoogleFile setLastViewedByMeDate(DateTime lastViewedByMeDate) {
		this.file.setLastViewedByMeDate(lastViewedByMeDate);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getMarkedViewedByMeDate()
	 */
	public DateTime getMarkedViewedByMeDate() {
		return this.file.getMarkedViewedByMeDate();
	}

	/**
	 * @param markedViewedByMeDate
	 * @return
	 * @see com.google.api.services.drive.model.File#setMarkedViewedByMeDate(com.google.api.client.util.DateTime)
	 */
	public GoogleFile setMarkedViewedByMeDate(DateTime markedViewedByMeDate) {
		this.file.setMarkedViewedByMeDate(markedViewedByMeDate);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getMd5Checksum()
	 */
	public String getMd5Checksum() {
		return this.file.getMd5Checksum();
	}

	/**
	 * @param md5Checksum
	 * @return
	 * @see com.google.api.services.drive.model.File#setMd5Checksum(java.lang.String)
	 */
	public GoogleFile setMd5Checksum(String md5Checksum) {
		this.file.setMd5Checksum(md5Checksum);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getMimeType()
	 */
	public String getMimeType() {
		return this.file.getMimeType();
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 * @see java.util.Map#getOrDefault(java.lang.Object, java.lang.Object)
	 */
	public Object getOrDefault(Object key, Object defaultValue) {
		return this.file.getOrDefault(key, defaultValue);
	}

	/**
	 * @param mimeType
	 * @return
	 * @see com.google.api.services.drive.model.File#setMimeType(java.lang.String)
	 */
	public GoogleFile setMimeType(String mimeType) {
		this.file.setMimeType(mimeType);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getModifiedByMeDate()
	 */
	public DateTime getModifiedByMeDate() {
		return this.file.getModifiedByMeDate();
	}

	/**
	 * @param modifiedByMeDate
	 * @return
	 * @see com.google.api.services.drive.model.File#setModifiedByMeDate(com.google.api.client.util.DateTime)
	 */
	public GoogleFile setModifiedByMeDate(DateTime modifiedByMeDate) {
		this.file.setModifiedByMeDate(modifiedByMeDate);
		return this;
	}

	/**
	 * @param action
	 * @see java.util.Map#forEach(java.util.function.BiConsumer)
	 */
	public void forEach(BiConsumer<? super String, ? super Object> action) {
		this.file.forEach(action);
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getModifiedDate()
	 */
	public DateTime getModifiedDate() {
		return this.file.getModifiedDate();
	}

	/**
	 * @param modifiedDate
	 * @return
	 * @see com.google.api.services.drive.model.File#setModifiedDate(com.google.api.client.util.DateTime)
	 */
	public GoogleFile setModifiedDate(DateTime modifiedDate) {
		this.file.setModifiedDate(modifiedDate);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getOpenWithLinks()
	 */
	public Map<String, String> getOpenWithLinks() {
		return this.file.getOpenWithLinks();
	}

	/**
	 * @param openWithLinks
	 * @return
	 * @see com.google.api.services.drive.model.File#setOpenWithLinks(java.util.Map)
	 */
	public GoogleFile setOpenWithLinks(Map<String, String> openWithLinks) {
		this.file.setOpenWithLinks(openWithLinks);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getOriginalFilename()
	 */
	public String getOriginalFilename() {
		return this.file.getOriginalFilename();
	}

	/**
	 * @param function
	 * @see java.util.Map#replaceAll(java.util.function.BiFunction)
	 */
	public void replaceAll(BiFunction<? super String, ? super Object, ? extends Object> function) {
		this.file.replaceAll(function);
	}

	/**
	 * @param originalFilename
	 * @return
	 * @see com.google.api.services.drive.model.File#setOriginalFilename(java.lang.String)
	 */
	public GoogleFile setOriginalFilename(String originalFilename) {
		this.file.setOriginalFilename(originalFilename);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getOwnerNames()
	 */
	public List<String> getOwnerNames() {
		return this.file.getOwnerNames();
	}

	/**
	 * @param ownerNames
	 * @return
	 * @see com.google.api.services.drive.model.File#setOwnerNames(java.util.List)
	 */
	public GoogleFile setOwnerNames(List<String> ownerNames) {
		this.file.setOwnerNames(ownerNames);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getOwners()
	 */
	public List<User> getOwners() {
		return this.file.getOwners();
	}

	/**
	 * @param owners
	 * @return
	 * @see com.google.api.services.drive.model.File#setOwners(java.util.List)
	 */
	public GoogleFile setOwners(List<User> owners) {
		this.file.setOwners(owners);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getParents()
	 */
	public List<ParentReference> getParents() {
		return this.file.getParents();
	}

	/**
	 * @param parents
	 * @return
	 * @see com.google.api.services.drive.model.File#setParents(java.util.List)
	 */
	public GoogleFile setParents(List<ParentReference> parents) {
		this.file.setParents(parents);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getPermissions()
	 */
	public List<Permission> getPermissions() {
		return this.file.getPermissions();
	}

	/**
	 * @param permissions
	 * @return
	 * @see com.google.api.services.drive.model.File#setPermissions(java.util.List)
	 */
	public GoogleFile setPermissions(List<Permission> permissions) {
		this.file.setPermissions(permissions);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getProperties()
	 */
	public List<Property> getProperties() {
		return this.file.getProperties();
	}

	/**
	 * @param properties
	 * @return
	 * @see com.google.api.services.drive.model.File#setProperties(java.util.List)
	 */
	public GoogleFile setProperties(List<Property> properties) {
		this.file.setProperties(properties);
		return this;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.Map#putIfAbsent(java.lang.Object, java.lang.Object)
	 */
	public Object putIfAbsent(String key, Object value) {
		return this.file.putIfAbsent(key, value);
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getQuotaBytesUsed()
	 */
	public Long getQuotaBytesUsed() {
		return this.file.getQuotaBytesUsed();
	}

	/**
	 * @param quotaBytesUsed
	 * @return
	 * @see com.google.api.services.drive.model.File#setQuotaBytesUsed(java.lang.Long)
	 */
	public GoogleFile setQuotaBytesUsed(Long quotaBytesUsed) {
		this.file.setQuotaBytesUsed(quotaBytesUsed);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getSelfLink()
	 */
	public String getSelfLink() {
		return this.file.getSelfLink();
	}

	/**
	 * @param selfLink
	 * @return
	 * @see com.google.api.services.drive.model.File#setSelfLink(java.lang.String)
	 */
	public GoogleFile setSelfLink(String selfLink) {
		this.file.setSelfLink(selfLink);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getShared()
	 */
	public Boolean getShared() {
		return this.file.getShared();
	}

	/**
	 * @param shared
	 * @return
	 * @see com.google.api.services.drive.model.File#setShared(java.lang.Boolean)
	 */
	public GoogleFile setShared(Boolean shared) {
		this.file.setShared(shared);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getSharedWithMeDate()
	 */
	public DateTime getSharedWithMeDate() {
		return this.file.getSharedWithMeDate();
	}

	/**
	 * @param sharedWithMeDate
	 * @return
	 * @see com.google.api.services.drive.model.File#setSharedWithMeDate(com.google.api.client.util.DateTime)
	 */
	public GoogleFile setSharedWithMeDate(DateTime sharedWithMeDate) {
		this.file.setSharedWithMeDate(sharedWithMeDate);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getSharingUser()
	 */
	public User getSharingUser() {
		return this.file.getSharingUser();
	}

	/**
	 * @param sharingUser
	 * @return
	 * @see com.google.api.services.drive.model.File#setSharingUser(com.google.api.services.drive.model.User)
	 */
	public GoogleFile setSharingUser(User sharingUser) {
		this.file.setSharingUser(sharingUser);
		return this;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.Map#remove(java.lang.Object, java.lang.Object)
	 */
	public boolean remove(Object key, Object value) {
		return this.file.remove(key, value);
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getThumbnail()
	 */
	public Thumbnail getThumbnail() {
		return this.file.getThumbnail();
	}

	/**
	 * @param thumbnail
	 * @return
	 * @see com.google.api.services.drive.model.File#setThumbnail(com.google.api.services.drive.model.File.Thumbnail)
	 */
	public GoogleFile setThumbnail(Thumbnail thumbnail) {
		this.file.setThumbnail(thumbnail);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getThumbnailLink()
	 */
	public String getThumbnailLink() {
		return this.file.getThumbnailLink();
	}

	/**
	 * @param thumbnailLink
	 * @return
	 * @see com.google.api.services.drive.model.File#setThumbnailLink(java.lang.String)
	 */
	public GoogleFile setThumbnailLink(String thumbnailLink) {
		this.file.setThumbnailLink(thumbnailLink);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getTitle()
	 */
	public String getTitle() {
		return this.file.getTitle();
	}

	/**
	 * @param title
	 * @return
	 * @see com.google.api.services.drive.model.File#setTitle(java.lang.String)
	 */
	public GoogleFile setTitle(String title) {
		this.file.setTitle(title);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getUserPermission()
	 */
	public Permission getUserPermission() {
		return this.file.getUserPermission();
	}

	/**
	 * @param userPermission
	 * @return
	 * @see com.google.api.services.drive.model.File#setUserPermission(com.google.api.services.drive.model.Permission)
	 */
	public GoogleFile setUserPermission(Permission userPermission) {
		this.file.setUserPermission(userPermission);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getVersion()
	 */
	public Long getVersion() {
		return this.file.getVersion();
	}

	/**
	 * @param key
	 * @param oldValue
	 * @param newValue
	 * @return
	 * @see java.util.Map#replace(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public boolean replace(String key, Object oldValue, Object newValue) {
		return this.file.replace(key, oldValue, newValue);
	}

	/**
	 * @param version
	 * @return
	 * @see com.google.api.services.drive.model.File#setVersion(java.lang.Long)
	 */
	public GoogleFile setVersion(Long version) {
		this.file.setVersion(version);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getWebContentLink()
	 */
	public String getWebContentLink() {
		return this.file.getWebContentLink();
	}

	/**
	 * @param webContentLink
	 * @return
	 * @see com.google.api.services.drive.model.File#setWebContentLink(java.lang.String)
	 */
	public GoogleFile setWebContentLink(String webContentLink) {
		this.file.setWebContentLink(webContentLink);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getWebViewLink()
	 */
	public String getWebViewLink() {
		return this.file.getWebViewLink();
	}

	/**
	 * @param webViewLink
	 * @return
	 * @see com.google.api.services.drive.model.File#setWebViewLink(java.lang.String)
	 */
	public GoogleFile setWebViewLink(String webViewLink) {
		this.file.setWebViewLink(webViewLink);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#getWritersCanShare()
	 */
	public Boolean getWritersCanShare() {
		return this.file.getWritersCanShare();
	}

	/**
	 * @param writersCanShare
	 * @return
	 * @see com.google.api.services.drive.model.File#setWritersCanShare(java.lang.Boolean)
	 */
	public GoogleFile setWritersCanShare(Boolean writersCanShare) {
		this.file.setWritersCanShare(writersCanShare);
		return this;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.Map#replace(java.lang.Object, java.lang.Object)
	 */
	public Object replace(String key, Object value) {
		return this.file.replace(key, value);
	}

	/**
	 * @param fieldName
	 * @param value
	 * @return
	 * @see com.google.api.services.drive.model.File#set(java.lang.String, java.lang.Object)
	 */
	public GoogleFile set(String fieldName, Object value) {
		this.file.set(fieldName, value);
		return this;
	}

	/**
	 * @return
	 * @see com.google.api.services.drive.model.File#clone()
	 */
	@Override
	public GoogleFile clone() {
		return new GoogleFile(this.file.clone());
	}

	/**
	 * @param key
	 * @param mappingFunction
	 * @return
	 * @see java.util.Map#computeIfAbsent(java.lang.Object, java.util.function.Function)
	 */
	public Object computeIfAbsent(String key, Function<? super String, ? extends Object> mappingFunction) {
		return this.file.computeIfAbsent(key, mappingFunction);
	}

	/**
	 * @param key
	 * @param remappingFunction
	 * @return
	 * @see java.util.Map#computeIfPresent(java.lang.Object, java.util.function.BiFunction)
	 */
	public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) {
		return this.file.computeIfPresent(key, remappingFunction);
	}

	/**
	 * @param key
	 * @param remappingFunction
	 * @return
	 * @see java.util.Map#compute(java.lang.Object, java.util.function.BiFunction)
	 */
	public Object compute(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) {
		return this.file.compute(key, remappingFunction);
	}

	/**
	 * @param key
	 * @param value
	 * @param remappingFunction
	 * @return
	 * @see java.util.Map#merge(java.lang.Object, java.lang.Object, java.util.function.BiFunction)
	 */
	public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
		return this.file.merge(key, value, remappingFunction);
	}

	public static List<GoogleFile> toGoogleFile(List<File> items) {
		List<GoogleFile> res = new ArrayList<>();
		for (File file : items) {
			res.add(new GoogleFile(file));
		}
		return res;
	}

	public static GoogleFile toGoogleFile(File file) {
		return new GoogleFile(file);
	}

}
