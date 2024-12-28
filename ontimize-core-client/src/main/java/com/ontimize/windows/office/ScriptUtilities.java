package com.ontimize.windows.office;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that allows to execute command files in Windows systems.
 *
 * @author Imatia Innovation
 */
public class ScriptUtilities {

	private static final Logger logger = LoggerFactory.getLogger(ScriptUtilities.class);

	public static boolean DEBUG = true;

	public static final String SCRIPT_COMMAND = "cscript ";

	public static final String SCRIPT_COMMAND_W = "wscript ";

	public static final int CSCRIPT = 0;

	public static final int WSCRIPT = 1;

	public static ExecutionResult executeScript(final String archive, final List pa) throws Exception {
		return ScriptUtilities.executeScript(archive, pa, ScriptUtilities.CSCRIPT);
	}

	public static ExecutionResult executeScript(final String archive, final List parameters, final int with) throws Exception {
		final StringBuilder param = new StringBuilder();
		for (int i = 0; i < parameters.size(); i++) {
			final Object p = parameters.get(i);
			if (p != null) {
				final String ps = p.toString();
				if (ps.indexOf(" ") >= 0) {
					param.append(" " + "\"" + ps + "\"");
				} else {
					param.append(" " + ps);
				}
			}
		}
		return ScriptUtilities.executeScript(archive, param.toString(), with);
	}

	public static ExecutionResult executeScript(final String archive, final String parameters) throws Exception {
		return ScriptUtilities.executeScript(archive, parameters, ScriptUtilities.CSCRIPT);
	}

	public static ExecutionResult executeScript(final String archive, final String parameters, final int with) throws Exception {
		InputStream in = null;
		BufferedInputStream bIn = null;
		try {
			String command = ScriptUtilities.SCRIPT_COMMAND;
			if (with == ScriptUtilities.WSCRIPT) {
				command = ScriptUtilities.SCRIPT_COMMAND_W;
			}
			final String sCommand = command + archive + parameters + "  //Nologo ";
			if (ScriptUtilities.DEBUG) {
				ScriptUtilities.logger.debug(sCommand);
			}
			final Process pProccess = Runtime.getRuntime().exec(sCommand);
			in = pProccess.getInputStream();
			bIn = new BufferedInputStream(in);
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			int result = 0;
			while (true) {
				try {
					int iValue = -1;
					while ((iValue = bIn.read()) != -1) {
						out.write(iValue);
					}
					result = pProccess.exitValue();
					while ((iValue = bIn.read()) != -1) {
						out.write(iValue);
					}
					break;
				} catch (final Exception e) {
					ScriptUtilities.logger.trace(null, e);
				}
				try {
					Thread.sleep(10);
				} catch (final Exception e) {
					ScriptUtilities.logger.trace(null, e);
				}

			}

			out.flush();
			out.close();
			final byte[] bytes = out.toByteArray();
			final String sOut = new String(bytes);
			if (ScriptUtilities.DEBUG) {
				ScriptUtilities.logger.debug(sOut.toString());
			}
			return new ExecutionResult(result, out.toByteArray());

		} catch (final Exception e) {
			ScriptUtilities.logger.debug("Exception executing script " + archive, e);
			throw e;
		} finally {
			if (bIn != null) {
				bIn.close();
			}
		}
	}

	public List listAccessDBReports(final String bdArchive) throws Exception {
		ScriptUtilities.logger.debug("Executing request for access report list");
		final URL url = this.getClass().getResource("scripts/listaccessreports.vbs");
		final File f = new File(url.getFile());
		final ExecutionResult res = ScriptUtilities.executeScript(f.toString(), bdArchive);
		if (res.getResult() != 0) {
			return null;
		}
		final String s = res.getOuput();
		// From the beginning to the end;
		final int iLast = s.lastIndexOf(";");
		final List vReports = new Vector();
		if ((s.length() > 0) && (iLast >= 0)) {
			final String ss = s.substring(0, iLast);
			final StringTokenizer st = new StringTokenizer(ss, ";");
			while (st.hasMoreTokens()) {
				vReports.add(st.nextToken());
			}
		}
		return vReports;
	}

	public boolean printAccessDBReport(String bdArchive, String report, String conditions) throws Exception {
		if (conditions == null) {
			conditions = "";
		}
		final URL url = this.getClass().getResource("scripts/printaccessreport.vbs");
		final File f = new File(url.getFile());
		if (bdArchive.indexOf(" ") > 0) {
			bdArchive = "\"" + bdArchive + "\"";
		}
		if (report.indexOf(" ") > 0) {
			report = "\"" + report + "\"";
		}
		final ExecutionResult res = ScriptUtilities.executeScript(f.toString(), bdArchive + " " + report);
		ScriptUtilities.logger.debug(res.getOuput());
		if (res.getResult() != 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Prints and fills a word document replacing <code>Hashtable</code> keys by correspondent
	 * descriptions. Max. length for description is 255 characters.
	 * @param archive The name of file
	 * @param valuesDescriptions <code>Hashtable</code> with texts and descriptions
	 * @return true Whether document has been filled and printed correctly
	 * @throws Exception When a <code>Exception</code> occurs
	 */
	public static boolean fillAndPrintWordDocument(String archive, final Map valuesDescriptions) throws Exception {

		final File f = ScriptUtilities
				.createTemporalFileForScript("com/ontimize/windows/office/scripts/fillandprintworddocument.vbs");
		final List parameters = new Vector();
		if (archive.indexOf(" ") > 0) {
			archive = "\"" + archive + "\"";
		}
		parameters.add(archive);
		final Enumeration enumKeys = Collections.enumeration(valuesDescriptions.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			parameters.add(oKey);
			parameters.add(valuesDescriptions.get(oKey));
		}

		final ExecutionResult res = ScriptUtilities.executeScript(f.toString(), parameters, ScriptUtilities.WSCRIPT);
		ScriptUtilities.logger.debug(res.getOuput());
		if (res.getResult() != 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean report2Snapshot(String bdArchive, String report, String dest, String conditions) throws Exception {
		if (conditions == null) {
			conditions = "";
		}
		final URL url = this.getClass().getResource("scripts/report2snapshot.vbs");
		final File f = new File(url.getFile());
		if (bdArchive.indexOf(" ") >= 0) {
			bdArchive = "\"" + bdArchive + "\"";
		}
		if (report.indexOf(" ") >= 0) {
			report = "\"" + report + "\"";
		}
		if (dest.indexOf(" ") >= 0) {
			dest = "\"" + dest + "\"";
		}
		if (conditions.indexOf(" ") >= 0) {
			conditions = "\"" + conditions + "\"";
		}
		final ExecutionResult res = ScriptUtilities.executeScript(f.toString(),
				bdArchive + " " + report + " " + dest + " " + conditions);
		ScriptUtilities.logger.debug(res.getOuput());
		if (res.getResult() != 0) {
			return false;
		} else {
			return true;
		}
	}

	public static void main(final String[] args) throws Exception {
		ScriptUtilities.logger.debug(args[0]);
		final String sAux = new String("á");
		final byte[] bytes = sAux.getBytes();
		ScriptUtilities.logger.debug("Byte number: " + bytes.length);

		ScriptUtilities.logger.debug(sAux);
		for (int i = 0; i < 1300; i++) {
			try {
				final String enc = "Cp" + Integer.toString(i);
				final String s = new String(bytes, enc);
				ScriptUtilities.logger.debug(s + " Encoding: " + enc);
			} catch (final Exception e) {
				ScriptUtilities.logger.trace(null, e);
			}
		}
	}

	public static File createTemporalFileForScript(final String script) throws Exception {
		final URL urlScript = new ExecutionResult(0, "").getClass().getClassLoader().getResource(script);
		if (urlScript == null) {
			throw new Exception("Script : " + script + " not found");
		}
		InputStream in = null;

		BufferedInputStream bIn = null;
		FileOutputStream fOut = null;
		BufferedOutputStream bOut = null;
		try {
			final File temp = File.createTempFile("scripts", ".vbs");
			fOut = new FileOutputStream(temp);

			in = urlScript.openStream();
			bIn = new BufferedInputStream(in);
			bOut = new BufferedOutputStream(fOut);
			int b = -1;
			while ((b = bIn.read()) != -1) {
				bOut.write(b);
			}
			bOut.flush();
			temp.deleteOnExit();
			return temp;
		} finally {
			if (bOut != null) {
				bOut.close();
			}
			if (fOut != null) {
				fOut.close();
			}
			if (in != null) {
				in.close();
			}
			if (bIn != null) {
				bIn.close();
			}
		}

	}

}
