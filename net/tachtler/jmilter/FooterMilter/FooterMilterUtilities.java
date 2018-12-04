/**
 * Copyright (c) 2018 Klaus Tachtler. All Rights Reserved.
 * Klaus Tachtler. <klaus@tachtler.net>
 * http://www.tachtler.net
 */
package net.tachtler.jmilter.FooterMilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.james.mime4j.codec.EncoderUtil;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.SingleBody;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.io.InputStreams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

/*******************************************************************************
 * JMilter Handler for handling connections from an MTA to add a footer.
 * 
 * JMilter is an Open Source implementation of the Sendmail milter protocol, for
 * implementing milters in Java that can interface with the Sendmail or Postfix
 * MTA.
 * 
 * Java implementation of the Sendmail Milter protocol based on the project of
 * org.nightcode.jmilter from dmitry@nightcode.org.
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
public class FooterMilterUtilities {

	private static Logger log = LogManager.getLogger();

	private static StringBuffer stringBuffer = new StringBuffer();

	private static String entityTextBody = null;

	/**
	 * Constructor.
	 */
	public FooterMilterUtilities() {
		super();
	}

	/**
	 * Return a String including the content from the given entity with added
	 * footer. The given entity should be from "Content-Type" - "text/plain".
	 * 
	 * If the given entity has the "Content-Disposition" - "attachment" do NOT
	 * append any footer String!
	 * 
	 * If the given entity has the "Content-Transfer-Encoding" - "quoted-printable"
	 * convert the given footer String to a "Quoted Printable" String, before
	 * appending the footer String!
	 * 
	 * @param entity
	 * @param footer
	 * @return String
	 */
	public static String getTextContentWithFooter(Entity entity, String footer) {
		stringBuffer.delete(0, stringBuffer.length());

		stringBuffer.append(FooterMilterUtilities.getTextBody(entity));

		log.debug("*entity.getDispositionType()            : " + entity.getDispositionType());
		log.debug("*entity.getContentTransferEncoding()    : " + entity.getContentTransferEncoding());

		if (null != entity.getDispositionType()) {
			if (!entity.getDispositionType().equalsIgnoreCase("attachment")) {
				if (null != entity.getContentTransferEncoding()) {
					if (entity.getContentTransferEncoding().equalsIgnoreCase("quoted-printable")) {
						stringBuffer.append(FooterMilterUtilities.createQuotedPrintable(footer));
					} else {
						stringBuffer.append(footer);
					}
				} else {
					stringBuffer.append(footer);
				}
			}
		} else {
			if (null != entity.getContentTransferEncoding()) {
				if (entity.getContentTransferEncoding().equalsIgnoreCase("quoted-printable")) {
					stringBuffer.append(FooterMilterUtilities.createQuotedPrintable(footer));
				} else {
					stringBuffer.append(footer);
				}
			} else {
				stringBuffer.append(footer);
			}
		}

		stringBuffer.append(System.lineSeparator());

		log.debug("Content-Type: text/plain                : " + stringBuffer.toString());

		return stringBuffer.toString();
	}

	/**
	 * Return a String including the content from the given entity with added
	 * footer. The given entity should be from "Content-Type" - "text/html".
	 * 
	 * Determine if the given "Content-Type" - "text/html" is correct formatted
	 * HTML-Code. If so, add the footer before the closing HTML-Body tag (</body>).
	 * If NOT, add the footer at the end of the given entity body.
	 * 
	 * If the given entity has the "Content-Disposition" - "attachment" do NOT
	 * append any footer String!
	 * 
	 * If the given entity has the "Content-Transfer-Encoding" - "quoted-printable"
	 * convert the given footer String to a "Quoted Printable" String, before
	 * appending the footer String!
	 * 
	 * @param entity
	 * @param footer
	 * @return String
	 */
	public static String getHtmlContentWithFooter(Entity entity, String footer) {
		stringBuffer.delete(0, stringBuffer.length());
		entityTextBody = null;

		log.debug("*entity.getDispositionType()            : " + entity.getDispositionType());
		log.debug("*entity.getContentTransferEncoding()    : " + entity.getContentTransferEncoding());

		/*
		 * Check if a well formed HTML content will be found. If it's true, customize
		 * the well formed HTML content. If it's false, add the HTML content at the end
		 * of the multipart part.
		 */
		entityTextBody = FooterMilterUtilities.getTextBody(entity);

		if (entityTextBody.indexOf("</body>") != -1) {
			String[] splitString = entityTextBody.split("</body>");
			stringBuffer.append(splitString[0].toString());

			if (null != entity.getDispositionType()) {
				if (!entity.getDispositionType().equalsIgnoreCase("attachment")) {
					if (null != entity.getContentTransferEncoding()) {
						if (entity.getContentTransferEncoding().equalsIgnoreCase("quoted-printable")) {
							stringBuffer.append(FooterMilterUtilities.createQuotedPrintable(footer));
						} else {
							stringBuffer.append(footer);
						}
					} else {
						stringBuffer.append(footer);
					}
				}
			} else {
				if (null != entity.getContentTransferEncoding()) {
					if (entity.getContentTransferEncoding().equalsIgnoreCase("quoted-printable")) {
						stringBuffer.append(FooterMilterUtilities.createQuotedPrintable(footer));
					} else {
						stringBuffer.append(footer);
					}
				} else {
					stringBuffer.append(footer);
				}
			}

			stringBuffer.append("</body>");
			stringBuffer.append(splitString[1].toString());

		} else {
			stringBuffer.append(entityTextBody);

			if (null != entity.getDispositionType()) {
				if (!entity.getDispositionType().equalsIgnoreCase("attachment")) {
					if (null != entity.getContentTransferEncoding()) {
						if (entity.getContentTransferEncoding().equalsIgnoreCase("quoted-printable")) {
							stringBuffer.append(FooterMilterUtilities.createQuotedPrintable(footer));
						} else {
							stringBuffer.append(footer);
						}
					} else {
						stringBuffer.append(footer);
					}
				}
			} else {
				if (null != entity.getContentTransferEncoding()) {
					if (entity.getContentTransferEncoding().equalsIgnoreCase("quoted-printable")) {
						stringBuffer.append(FooterMilterUtilities.createQuotedPrintable(footer));
					} else {
						stringBuffer.append(footer);
					}
				} else {
					stringBuffer.append(footer);
				}
			}
		}

		log.debug("Content-Type: text/html                 : " + stringBuffer.toString());

		return stringBuffer.toString();
	}

	/**
	 * Return a String including the content from the given entity WITHOUT added
	 * footer. The given entity could be from ANY "Content-Type", but should NOT be
	 * from "Content-Type" - "text/plain" or "text/html".
	 * 
	 * @param entity
	 * @return String
	 */
	public static String getBinaryContent(Entity entity) {
		stringBuffer.delete(0, stringBuffer.length());

		stringBuffer.append(FooterMilterUtilities.getBinaryBody(entity));
		stringBuffer.append(System.lineSeparator());

		log.debug("Content-Type: \"binary-content\"          : " + stringBuffer.toString());

		return stringBuffer.toString();
	}

	/**
	 * Return the bodyString String from given (TextBody) entity.
	 * 
	 * @param entity
	 * @return String
	 */
	public static String getTextBody(Entity entity) {
		TextBody textBody = (TextBody) entity.getBody();
		return getBody(entity, textBody);
	}

	/**
	 * Return the bodyString String from given (BinaryBody) entity.
	 * 
	 * @param part
	 * @return String
	 */
	public static String getBinaryBody(Entity entity) {
		BinaryBody binaryBody = (BinaryBody) entity.getBody();
		return getBody(entity, binaryBody);
	}

	/**
	 * Return the bodyString String from given entity and body. Depending on the
	 * "Content-Transfer-Encoding" create the right body encoding.
	 * 
	 * If the "Content-Transfer-Encoding" is "base64" or "quoted-printable", not
	 * only a simple text body was given. The bodyString String must be encoded as
	 * "base64" or "quoted-printable", while using the "Base64" or the "EncoderUtil"
	 * from MIME4J.
	 * 
	 * @param entity
	 * @param body
	 * @return String
	 */
	public static String getBody(Entity entity, Body body) {
		String bodyString = null;
		try {
			InputStream inputStream = ((SingleBody) body).getInputStream();
			byte[] bytes = IOUtils.toByteArray(inputStream);

			if (entity.getContentTransferEncoding().equalsIgnoreCase("base64")) {
				ByteBuf byteBuf = Unpooled.buffer(bytes.length);
				byteBuf.writeBytes(bytes);
				bodyString = Base64.encode(byteBuf, true).toString(StandardCharsets.UTF_8);
			} else if (entity.getContentTransferEncoding().equalsIgnoreCase("quoted-printable")) {
				InputStream inputStreamQuotedPrintable = InputStreams.create(bytes);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				EncoderUtil.encodeQBinary(inputStreamQuotedPrintable, byteArrayOutputStream);
				bodyString = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
			} else {
				bodyString = new String(bytes);
			}
		} catch (IOException eIOException) {
			log.error(
					"***** Program stop, because FooterMilter detects a runtime error! ***** (For more details, see error messages and caused by below).");
			log.error("IOException                             : " + eIOException);
			log.error(ExceptionUtils.getStackTrace(eIOException));
		}

		log.debug("bodyString    <- (Start at next line) -> : " + System.lineSeparator() + bodyString);

		return bodyString;
	}

	/**
	 * Create a "Quoted Printable" from a String, using the MIME4J EncoderUtil and
	 * return a coded String.
	 * 
	 * @param string
	 * @return String
	 */
	public static String createQuotedPrintable(String string) {
		InputStream inputStreamQuotedPrintable = InputStreams.create(string, StandardCharsets.UTF_8);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			EncoderUtil.encodeQBinary(inputStreamQuotedPrintable, byteArrayOutputStream);
		} catch (IOException eIOException) {
			log.error(
					"***** Program stop, because FooterMilter detects a runtime error! ***** (For more details, see error messages and caused by below).");
			log.error("IOException                             : " + eIOException);
			log.error(ExceptionUtils.getStackTrace(eIOException));
		}

		return new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
	}

}