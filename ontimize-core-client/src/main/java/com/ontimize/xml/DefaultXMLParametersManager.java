package com.ontimize.xml;

import java.awt.Color;
import java.awt.Paint;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BorderManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.util.ParseUtils;

public abstract class DefaultXMLParametersManager {

	private static final Logger logger = LoggerFactory.getLogger(DefaultXMLParametersManager.class);

	public static class ParameterValue {

		protected String parameter = null;

		protected String value = null;

		public ParameterValue(final String parameter, final String value) {
			this.parameter = parameter;
			this.value = value;
		}

		public String getParameter() {
			return this.parameter;
		}

		public String getValue() {
			return this.value;
		}

	}

	protected static Map parameters = new Hashtable();

	public static void add(final String className, final String parameter, final String value) {
		if (DefaultXMLParametersManager.parameters.containsKey(className)) {
			final List list = (List) DefaultXMLParametersManager.parameters.get(className);
			list.add(new ParameterValue(parameter, value));
		} else {
			final List list = new ArrayList();
			list.add(new ParameterValue(parameter, value));
			DefaultXMLParametersManager.parameters.put(className, list);
		}
	}

	public static void setXMLDefaultParameterFile(final String filePath) {
		final List fileNames = ApplicationManager.getTokensAt(filePath, ";");

		for (int i = 0; i < fileNames.size(); i++) {

			try {
				final URL url = DefaultXMLParametersManager.class.getClassLoader().getResource((String) fileNames.get(i));
				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder;
				dBuilder = factory.newDocumentBuilder();
				final Document doc = dBuilder.parse(url.openStream());

				NodeList nodeList = null;

				// Parse the SystemProperties node to set the system properties
				// This must be the first node to read because the properties
				// must
				// be set before create any data field
				nodeList = doc.getElementsByTagName("SystemProperties");
				if ((nodeList != null) && (nodeList.getLength() == 1)) {
					final Node rootNode = nodeList.item(0);
					DefaultXMLParametersManager.parseSystemProperties(rootNode);
				}

				// Parse the Colors node to register the color in the
				// ColorConstants
				// using the method ColorConstants.addUserColor(colorName,
				// color)
				nodeList = doc.getElementsByTagName("Colors");
				if ((nodeList != null) && (nodeList.getLength() == 1)) {
					final Node rootNode = nodeList.item(0);
					DefaultXMLParametersManager.parseColors(rootNode);
				}

				// Parse the Paints node to register the paints in the
				// ColorConstants using the method
				// ColorConstants.addUserPaint(paintName, (Paint)
				// paintObject);
				nodeList = doc.getElementsByTagName("Paints");
				if ((nodeList != null) && (nodeList.getLength() == 1)) {
					final Node rootNode = nodeList.item(0);
					DefaultXMLParametersManager.parsePaints(rootNode);
				}

				// Parse the Borders node to register the borders in the
				// BorderManager
				// using BorderManager.putBorder(borderName, (Border)
				// borderObject);
				nodeList = doc.getElementsByTagName("Borders");
				if ((nodeList != null) && (nodeList.getLength() == 1)) {
					final Node rootNode = nodeList.item(0);
					DefaultXMLParametersManager.parseBorders(rootNode);
				}

				// Parse the StaticValues node to set the static values and call
				// the
				// static set methods with the specified values
				nodeList = doc.getElementsByTagName("StaticValues");
				if ((nodeList != null) && (nodeList.getLength() == 1)) {
					final Node rootNode = nodeList.item(0);
					DefaultXMLParametersManager.parseStaticValues(rootNode);
				}

				// Parse the UIManager parameters
				nodeList = doc.getElementsByTagName("UIManagerParameters");
				if ((nodeList != null) && (nodeList.getLength() == 1)) {
					final Node rootNode = nodeList.item(0);
					DefaultXMLParametersManager.parseUIManagerParameters(rootNode);
				}

				// Parse the XMLConfigurationParametes node to set the default
				// parameters to use in the components construction
				nodeList = doc.getElementsByTagName("XMLConfigurationParameters");
				if ((nodeList != null) && (nodeList.getLength() == 1)) {
					final Node rootNode = nodeList.item(0);
					DefaultXMLParametersManager.parseXMLParameters(rootNode);
				}

			} catch (final Exception e) {
				DefaultXMLParametersManager.logger.error("{}", e.getMessage(), e);
			}
		}
	}

	protected static void parseColors(final Node rootNode) {
		// <Colors>
		// <Color name="HeaderColor" value="0;51;153" />
		// </Colors>
		final NodeList childNodes = rootNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			try {
				final Node item = childNodes.item(i);
				if ("Color".equalsIgnoreCase(item.getNodeName())) {
					final String colorName = item.getAttributes().getNamedItem("name").getNodeValue();
					final String colorValue = item.getAttributes().getNamedItem("value").getNodeValue();
					final Color c = ParseUtils.getColor(colorValue, null);
					if ((c != null) && (colorName != null)) {
						ColorConstants.addUserColor(colorName, c);
					}
				}
			} catch (final Exception e) {
				DefaultXMLParametersManager.logger.error("{}", e.getMessage(), e);
			}
		}
	}

	protected static void parsePaints(final Node rootNode) {
		// <Paints>
		// <Paint name="linearGradientPaint"
		// class="java.awt.LinearGradientPaint">
		// <Parameter class="java.awt.geom.Point2D">
		// <Value class="java.awt.Point">
		// <Parameter class="int">
		// <Value>0</Value>
		// </Parameter>
		// <Parameter class="int">
		// <Value>0</Value>
		// </Parameter>
		// </Value>
		// </Parameter>
		// <Parameter class="java.awt.geom.Point2D">
		// <Value class="java.awt.Point">
		// <Parameter class="int">
		// <Value>0</Value>
		// </Parameter>
		// <Parameter class="int">
		// <Value>26</Value>
		// </Parameter>
		// </Value>
		// </Parameter>
		// <Parameter class="[F">
		// <Value>0.0f;0.4999f;0.5f;1f</Value>
		// </Parameter>
		// <Parameter class="[Ljava.awt.Color;">
		// <Value>#f2f2f2;#ebebeb;#dddddd;#cfcfcf</Value>
		// </Parameter>
		// </Paint>
		// </Paints>
		final NodeList childNodes = rootNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			try {
				final Node item = childNodes.item(i);
				if ("Paint".equalsIgnoreCase(item.getNodeName())) {
					final String paintName = item.getAttributes().getNamedItem("name").getNodeValue();
					try {
						final Object paintObject = DefaultXMLParametersManager.createObject(item);
						ColorConstants.addUserPaint(paintName, (Paint) paintObject);
					} catch (final Exception ex) {
						DefaultXMLParametersManager.logger.error("Error creating {} paint: {}", paintName,
								ex.getMessage(), ex);
					}
				}
			} catch (final Exception e) {
				DefaultXMLParametersManager.logger.error("{}", e.getMessage(), e);
			}
		}
	}

	protected static void parseUIManagerParameters(final Node rootNode) {
		final NodeList childNodes = rootNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			try {
				final Node item = childNodes.item(i);
				if ("UIManagerParameter".equalsIgnoreCase(item.getNodeName())) {
					final String keyName = item.getAttributes().getNamedItem("name").getNodeValue();
					final Object object = DefaultXMLParametersManager.createObject(item);
					UIManager.put(keyName, object);
				}
			} catch (final Exception e) {
				DefaultXMLParametersManager.logger.error("{}", e.getMessage(), e);
			}
		}
	}

	protected static void parseBorders(final Node rootNode) {
		// <Borders>
		// <Border name="empty" class="javax.swing.border.EmptyBorder">
		// <Parameter class="int">
		// <Value>0</Value>
		// </Parameter>
		// <Parameter class="int">
		// <Value>0</Value>
		// </Parameter>
		// <Parameter class="int">
		// <Value>0</Value>
		// </Parameter>
		// <Parameter class="int">
		// <Value>0</Value>
		// </Parameter>
		// </Border>
		// </Borders>
		final NodeList childNodes = rootNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			try {
				final Node item = childNodes.item(i);
				if ("Border".equalsIgnoreCase(item.getNodeName())) {
					final String borderName = item.getAttributes().getNamedItem("name").getNodeValue();
					final Object borderObject = DefaultXMLParametersManager.createObject(item);
					BorderManager.putBorder(borderName, (Border) borderObject);
				}
			} catch (final Exception e) {
				DefaultXMLParametersManager.logger.error("{}", e.getMessage(), e);
			}
		}
	}

	protected static Object createObject(final Node rootNode) throws Exception {
		final String objectClassName = rootNode.getAttributes().getNamedItem("class").getNodeValue();
		final NodeList childParameters = rootNode.getChildNodes();
		final List parameterTypes = new ArrayList();
		final List parameterValues = new ArrayList();

		// Read all the parameters to know the classes and the values
		// The classes are needed to know the constructor to use
		for (int k = 0; k < childParameters.getLength(); k++) {
			if ("Parameter".equalsIgnoreCase(childParameters.item(k).getNodeName())) {
				final String parameterClassName = childParameters.item(k)
						.getAttributes()
						.getNamedItem("class")
						.getNodeValue();
				final Class parameterClass = DefaultXMLParametersManager.getClassFromString(parameterClassName);
				Object parameterValue = null;
				final NodeList childNodes = childParameters.item(k).getChildNodes();
				for (int m = 0; m < childNodes.getLength(); m++) {
					if ("Value".equalsIgnoreCase(childNodes.item(m).getNodeName())) {
						final NodeList valueChildNodes = childNodes.item(m).getChildNodes();
						if ((valueChildNodes.getLength() == 1)
								&& (valueChildNodes.item(0).getNodeType() == Node.TEXT_NODE)) {
							parameterValue = DefaultXMLParametersManager
									.getObjectFromClass(valueChildNodes.item(0).getNodeValue(), parameterClass);
						} else {
							// When the parameter is a new class to create a new
							// instance with its own parameters
							parameterValue = DefaultXMLParametersManager.createObject(childNodes.item(m));
						}
					}
				}
				parameterTypes.add(parameterClass);
				parameterValues.add(parameterValue);
			}
		}

		final Class objectClass = DefaultXMLParametersManager.getClassFromString(objectClassName);
		final Class[] types = new Class[parameterTypes.size()];
		final Object[] values = new Object[parameterValues.size()];
		for (int i = 0; i < parameterTypes.size(); i++) {
			types[i] = (Class) parameterTypes.get(i);
			values[i] = parameterValues.get(i);
		}

		final Constructor constructor = objectClass.getConstructor(types);
		final Object newInstance = constructor.newInstance(values);
		return newInstance;
	}

	/**
	 * Get the Class for the specified String parameter. Is not exactly the same as
	 * Class.forName(String) because this method compare first the className with the primitive types
	 * @param className Name of the class, for example: <br>
	 *        (int, float, double ..., java.awt.Color, [I, [F, [Ljava.awt.Color;
	 * @return
	 * @throws Exception
	 */
	protected static Class getClassFromString(final String className) throws Exception {
		if (className.equalsIgnoreCase("int")) {
			return int.class;
		} else if (className.equalsIgnoreCase("float")) {
			return float.class;
		} else if (className.equalsIgnoreCase("double")) {
			return double.class;
		} else if (className.equalsIgnoreCase("boolean")) {
			return boolean.class;
		} else if (className.equalsIgnoreCase("byte")) {
			return byte.class;
		} else if (className.equalsIgnoreCase("long")) {
			return long.class;
		} else if (className.equalsIgnoreCase("short")) {
			return short.class;
		} else if (className.equalsIgnoreCase("char")) {
			return char.class;
		}

		if (className.equalsIgnoreCase("color")) {
			return Color.class;
		} else if (className.equalsIgnoreCase("icon")) {
			return ImageIcon.class;
		}
		// [F is float[]
		// [I is int[]
		// [Ljava.awt.Color; is Color[]
		return Class.forName(className);
	}

	protected static void parseStaticValues(final Node rootNode) {
		// <StaticValues>
		// <Class
		// name="com.ontimize.gui.preferences.BasicApplicationPreferences">
		// <StaticValue variable="remoteUserPreferences" value="true" />
		// </Class>
		// <Class name="com.ontimize.gui.Form">
		// <StaticSetMethod method="setDefaultTitlePaint"
		// value="linearGradientPaint"
		// datatype="java.awt.Paint" />
		// </Class>
		// </StaticValues>
		final NodeList childNodes = rootNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			try {
				final Node item = childNodes.item(i);
				if ("Class".equalsIgnoreCase(item.getNodeName())) {
					final String className = item.getAttributes().getNamedItem("name").getNodeValue();
					final NodeList classParamtersList = item.getChildNodes();
					for (int k = 0; k < classParamtersList.getLength(); k++) {
						final Node parameterItem = classParamtersList.item(k);
						if ("StaticValue".equalsIgnoreCase(parameterItem.getNodeName())) {
							final String variableName = parameterItem.getAttributes().getNamedItem("variable").getNodeValue();
							final String sValue = parameterItem.getAttributes().getNamedItem("value").getNodeValue();
							DefaultXMLParametersManager.setStaticValue(className, variableName, sValue);
						} else if ("StaticSetMethod".equalsIgnoreCase(parameterItem.getNodeName())) {
							final String methodName = parameterItem.getAttributes().getNamedItem("method").getNodeValue();
							final String sValue = parameterItem.getAttributes().getNamedItem("value").getNodeValue();
							String dataType = null;
							if (parameterItem.getAttributes().getNamedItem("datatype") != null) {
								dataType = parameterItem.getAttributes().getNamedItem("datatype").getNodeValue();
							}
							DefaultXMLParametersManager.executeStaticSetMethod(className, methodName, sValue, dataType);
						}
					}
				}
			} catch (final Exception e) {
				DefaultXMLParametersManager.logger.error("{}", e.getMessage(), e);
			}
		}
	}

	protected static void parseSystemProperties(final Node rootNode) {
		final NodeList childNodes = rootNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			try {
				final Node item = childNodes.item(i);
				if ("SystemProperty".equalsIgnoreCase(item.getNodeName())) {
					final String propertyName = item.getAttributes().getNamedItem("name").getNodeValue();
					final String propertyValue = item.getAttributes().getNamedItem("value").getNodeValue();
					System.setProperty(propertyName, propertyValue);
				}
			} catch (final Exception e) {
				DefaultXMLParametersManager.logger.error("{}", e.getMessage(), e);
			}
		}
	}

	protected static void executeStaticSetMethod(final String className, final String methodName, final String value, final String dataType)
			throws Exception {
		final Class classObject = DefaultXMLParametersManager.getClassFromString(className);

		final List listValueName = ApplicationManager.getTokensAt(value, ";");
		final List listDataType = ApplicationManager.getTokensAt(dataType, ";");
		Class[] arrayReflectionClasses = null;
		Object[] arrayReflectionValues = null;
		if ((listDataType != null) && (listValueName != null) && (listDataType.size() == listValueName.size())) {
			arrayReflectionClasses = new Class[listValueName.size()];
			arrayReflectionValues = new Object[listValueName.size()];
			for (int i = 0; i < listDataType.size(); i++) {
				final Class classI = DefaultXMLParametersManager.getClassFromString(listDataType.get(i).toString());
				final Object objectI = DefaultXMLParametersManager.getObjectFromClass(listValueName.get(i).toString(),
						classI);
				arrayReflectionValues[i] = objectI;
				arrayReflectionClasses[i] = classI;
			}
			final Method method = classObject.getMethod(methodName, arrayReflectionClasses);
			if (method != null) {
				method.invoke(classObject, arrayReflectionValues);
			}
		}
	}

	protected static void setStaticValue(final String className, final String variableName, final String value) throws Exception {
		final Class classObject = DefaultXMLParametersManager.getClassFromString(className);
		final Field field = classObject.getField(variableName);
		if (field != null) {
			final Object oValue = DefaultXMLParametersManager.getObjectFromClass(value, field.getType());
			field.set(classObject, oValue);
		}
	}

	protected static Object getObjectFromClass(final String value, final Class classObject) {
		final String dataType = classObject.getName();
		if (value != null) {
			if (classObject.isArray()) {
				final List v = ApplicationManager.getTokensAt(value, ";");
				final Object newInstance = Array.newInstance(classObject.getComponentType(), v.size());
				for (int i = 0; i < v.size(); i++) {
					Array.set(newInstance, i, DefaultXMLParametersManager.getObjectFromClass((String) v.get(i),
							classObject.getComponentType()));
				}
				return newInstance;
			} else {
				return DefaultXMLParametersManager.getObjectFromString(value, dataType);
			}
		}

		return value;
	}

	/**
	 * Get an object of types: float, double, boolean, int, byte, long, short or char. For example
	 * Float.valueOf(value)<br>
	 * If dataType is color, paint or image then use {@link ParseUtils} class to get the object
	 * @param value
	 * @param dataType
	 * @return
	 */
	protected static Object getObjectFromString(final String value, String dataType) {
		if (value != null) {
			dataType = dataType.substring(dataType.lastIndexOf(".") + 1);
			if ((value != null) && (dataType != null)) {
				if (dataType.toLowerCase().indexOf("float") >= 0) {
					return Float.valueOf(value);
				} else if (dataType.toLowerCase().indexOf("double") >= 0) {
					return Double.valueOf(value);
				} else if (dataType.toLowerCase().indexOf("boolean") >= 0) {
					return Boolean.valueOf(value);
				} else if (dataType.toLowerCase().indexOf("color") >= 0) {
					return ParseUtils.getColor(value, null);
				} else if (dataType.toLowerCase().indexOf("paint") >= 0) {
					return ParseUtils.getPaint(value, null);
				} else if (dataType.toLowerCase().indexOf("image") >= 0) {
					return ParseUtils.getImage(value, null);
				} else if (dataType.toLowerCase().indexOf("font") >= 0) {
					return ParseUtils.getFont(value, null);
				} else if (dataType.toLowerCase().indexOf("byte") >= 0) {
					return Byte.valueOf(value);
				} else if (dataType.toLowerCase().indexOf("long") >= 0) {
					return Long.valueOf(value);
				} else if (dataType.toLowerCase().indexOf("short") >= 0) {
					return Short.valueOf(value);
				} else if (dataType.toLowerCase().indexOf("char") >= 0) {
					return value;
				} else if (dataType.toLowerCase().indexOf("int") >= 0) {
					return Integer.valueOf(value);
				}
			}
		}
		return value;
	}

	protected static void parseXMLParameters(final Node rootNode) {
		final NodeList childNodes = rootNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			try {
				final Node item = childNodes.item(i);
				if ("Class".equalsIgnoreCase(item.getNodeName())) {
					final String className = item.getAttributes().getNamedItem("name").getNodeValue();
					final List classNames = ApplicationManager.getTokensAt(className, ";");

					final NodeList classParamtersList = item.getChildNodes();
					for (int k = 0; k < classParamtersList.getLength(); k++) {
						final Node parameterItem = classParamtersList.item(k);
						if ("Parameter".equalsIgnoreCase(parameterItem.getNodeName())) {
							final String parameterName = parameterItem.getAttributes().getNamedItem("key").getNodeValue();
							final String parameterValue = parameterItem.getAttributes().getNamedItem("value").getNodeValue();
							for (int n = 0; n < classNames.size(); n++) {
								DefaultXMLParametersManager.add((String) classNames.get(n), parameterName,
										parameterValue);
							}
						}
					}
				}
			} catch (final Exception e) {
				DefaultXMLParametersManager.logger.error("{}", e.getMessage(), e);
			}
		}
	}

	public static void clearXMLDefaultParameters() {
		DefaultXMLParametersManager.parameters.clear();
	}

	/**
	 * Get a list with all the default xml configuration parameters for the class specified
	 * @param className
	 * @return
	 */
	public static ParameterValue[] get(final String className) {
		if (DefaultXMLParametersManager.parameters.containsKey(className)) {
			return (ParameterValue[]) ((ArrayList) DefaultXMLParametersManager.parameters.get(className))
					.toArray(new ParameterValue[] {});
		} else {
			return null;
		}
	}

	public static Map getParameters(final String className) {
		final Map hParams = new Hashtable();
		final ParameterValue[] parameterValues = DefaultXMLParametersManager.get(className);
		if (parameterValues != null) {
			for (int i = 0; i < parameterValues.length; i++) {
				hParams.put(parameterValues[i].getParameter(), parameterValues[i].getValue());
			}
		}
		return hParams;
	}

	public static ParameterValue[] getStartsWith(final String tag) {
		// keys
		final ArrayList list = new ArrayList();
		final Iterator keys = DefaultXMLParametersManager.parameters.keySet().iterator();
		while (keys.hasNext()) {
			final Object keyO = keys.next();
			final String key = keyO.toString();
			if (tag.startsWith(key) && !tag.equals(key)) {
				list.addAll(Arrays.asList(DefaultXMLParametersManager.get(key)));
			}
		}
		final ParameterValue[] params = DefaultXMLParametersManager.get(tag);
		if (params != null) {
			list.addAll(Arrays.asList(DefaultXMLParametersManager.get(tag)));
		}
		return (ParameterValue[]) list.toArray(new ParameterValue[] {});
	}

}
