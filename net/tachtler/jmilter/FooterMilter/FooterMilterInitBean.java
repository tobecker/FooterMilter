/**
 * Copyright (c) 2018 Klaus Tachtler. All Rights Reserved.
 * Klaus Tachtler. <klaus@tachtler.net>
 * http://www.tachtler.net
 */
package net.tachtler.jmilter.FooterMilter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*******************************************************************************
 * Bean for JMilter.
 * 
 * @author Klaus Tachtler. <klaus@tachtler.net>
 * 
 *         Homepage : http://www.tachtler.net
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License..
 * 
 *         Copyright (c) 2018 by Klaus Tachtler.
 ******************************************************************************/
public class FooterMilterInitBean {

	private static Logger log = LogManager.getLogger();

	/**
	 * Returns the IPv4-Address.
	 */
	private InetAddress inetAddress;

	/**
	 * Returns the Port.
	 */
	private int port;

	/**
	 * Returns if TcpLogging is enabled.
	 */
	private Boolean tcpLoggingEnabled;

	/**
	 * Return the log level for the jmilter.
	 */
	private String tcpLogLevel;

	/**
	 * Return HashMap with mail from as key and text footer as value pair.
	 */
	private HashMap<String, String> mapText = new HashMap<String, String>();

	/**
	 * Return HashMap with mail from as key and html footer as value pair.
	 */
	private HashMap<String, String> mapHtml = new HashMap<String, String>();

	/**
	 * Constructor.
	 */
	public FooterMilterInitBean(InetAddress inetAddress, int port, Boolean tcpLoggingEnabled, String tcpLogLevel,
			HashMap<String, String> mapText, HashMap<String, String> mapHtml) {
		super();
		this.inetAddress = inetAddress;
		this.port = port;
		this.tcpLoggingEnabled = tcpLoggingEnabled;
		this.tcpLogLevel = tcpLogLevel;
		this.mapText.clear();
		this.mapHtml.clear();
	}

	/**
	 * Initialize all variables to default or unseeded values.
	 * 
	 * @throws FooterMilterException
	 */
	public final void init() throws FooterMilterException {
		try {
			this.inetAddress = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException eUnknownHostException) {
			log.error(
					"***** Program stop, because FooterMilter could not be initialized! ***** (For more details, see error messages and caused by below).");
			log.error("UnknownHostException                    : " + eUnknownHostException);
			log.error(ExceptionUtils.getStackTrace(eUnknownHostException));
			throw new FooterMilterException(
					"***** Program stop, because FooterMilter could not be initialized! ***** (For more details, see error messages and caused by below).",
					eUnknownHostException);
		}

		this.port = 10099;
		this.tcpLoggingEnabled = false;
		this.tcpLogLevel = "INFO";
	}

	/**
	 * @return the inetAddress
	 */
	public InetAddress getInetAddress() {
		return inetAddress;
	}

	/**
	 * @param inetAddress
	 *            the inetAddress to set
	 */
	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the tcpLoggingEnabled
	 */
	public Boolean getTcpLoggingEnabled() {
		return tcpLoggingEnabled;
	}

	/**
	 * @param tcpLoggingEnabled
	 *            the tcpLoggingEnabled to set
	 */
	public void setTcpLoggingEnabled(Boolean tcpLoggingEnabled) {
		this.tcpLoggingEnabled = tcpLoggingEnabled;
	}

	/**
	 * @return the tcpLogLevel
	 */
	public String getTcpLogLevel() {
		return tcpLogLevel;
	}

	/**
	 * @param tcpLogLevel
	 *            the tcpLogLevel to set
	 */
	public void setTcpLogLevel(String tcpLogLevel) {
		this.tcpLogLevel = tcpLogLevel;
	}

	/**
	 * @return the mapText
	 */
	public HashMap<String, String> getMapText() {
		return mapText;
	}

	/**
	 * @param mapText
	 *            the mapText to set
	 */
	public void setMapText(HashMap<String, String> mapText) {
		this.mapText = mapText;
	}

	/**
	 * @return the mapHtml
	 */
	public HashMap<String, String> getMapHtml() {
		return mapHtml;
	}

	/**
	 * @param mapHtml
	 *            the mapHtml to set
	 */
	public void setMapHtml(HashMap<String, String> mapHtml) {
		this.mapHtml = mapHtml;
	}

}