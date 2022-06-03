/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	09/01/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.jms.impl;

import java.util.Date;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.amtsybex.fieldreach.extract.jms.FeaJmsController;

/**
 * Class to facilitate the dispatch of JMS messages.
 */
public class FeaJmsControllerImpl implements FeaJmsController {

	private JmsTemplate jmsTemplate;
	private Destination destination;
	private boolean aibHeaders;

	static Logger log = LoggerFactory.getLogger(FeaJmsControllerImpl.class.getName());

	/*-------------------------------------------
	 - Interface Implementation
	 --------------------------------------------*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.jms.FeaJmsController#dispatch(java.lang
	 * .String, int, java.lang.String)
	 */
	@Override
	public void dispatch(String frInstance, int returnId, String message)
			throws JMSException {

		if (log.isDebugEnabled())
			log.debug(">>> dispatch message=XXX");

		log.info("Dispatching message to Queue["+ this.destination.toString() + "]");

		MessageCreator msg = this.createMessageCreator(frInstance, returnId,
				message, this.aibHeaders);

		try {
			
			jmsTemplate.send(this.destination, msg);
		
		} catch (Exception e) {
			
			log.error("Dispatch failed!");
			
			throw new JMSException(e.getMessage());
		}

		log.info("Dispatch success!");
		
		if (log.isDebugEnabled())
			log.debug("<<< dispatch");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.jms.FeaJmsController#setJmsTemplate(org
	 * .springframework.jms.core.JmsTemplate)
	 */
	@Override
	public void setJmsTemplate(JmsTemplate jmsTemplate) {

		if (log.isDebugEnabled())
			log.debug(">>> setJmsTemplate jmsTemplate=XXX");

		this.jmsTemplate = jmsTemplate;

		if (log.isDebugEnabled())
			log.debug("<<< setJmsTemplate");
	}

	/*
	 * (non-Javadoc)
	 * @see com.amtsybex.fieldreach.extract.jms.FeaJmsController#setDestination(javax.jms.Destination)
	 */
	@Override
	public void setDestination(Destination destination) {

		if (log.isDebugEnabled())
			log.debug(">>> setDestination destination=XXX");

		this.destination = destination;

		if (log.isDebugEnabled())
			log.debug("<<< setDestination");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.amtsybex.fieldreach.extract.jms.FeaJmsController#setAibHeaders(boolean
	 * )
	 */
	@Override
	public void setAibHeaders(boolean headers) {

		if (log.isDebugEnabled())
			log.debug(">>> setAibHeaders headers=" + headers);

		this.aibHeaders = headers;

		if (log.isDebugEnabled())
			log.debug("<<< setAibHeaders");
	}

	/*-------------------------------------------
	 - Private Methods
	 --------------------------------------------*/

	/**
	 * Build JMS message using the information supplied and build a
	 * MessageCreator object for use by a JMSTemplate.
	 * 
	 * @param frInstance
	 *            Fieldreach instance the message supplied was extracted from.
	 * 
	 * @param returnId
	 *            The returnId of the extracted script result.
	 * 
	 * @param message
	 *            Message body of the JMS message.
	 * 
	 * @param aibHeaders
	 *            Flag to indicate if AIB Headers should be added to the JMS
	 *            message.
	 * 
	 * @return MessageCreator object containing a JMS message containing the
	 *         supplied information.
	 * 
	 * @throws JMSException
	 *             Error occurs creating the JMS message contained by the
	 *             MessageCreator.
	 */
	private MessageCreator createMessageCreator(final String frInstance,
			final int returnId, final String message, final boolean aibHeaders)
			throws JMSException {

		if (log.isDebugEnabled())
			log.debug(">>> createMessageCreator");

		MessageCreator creator = new MessageCreator() {

			public Message createMessage(Session session) throws JMSException {

				TextMessage msg = session.createTextMessage(message);

				if (aibHeaders) {

					msg.setStringProperty("DTS$ContentType", "RAW");
					msg.setStringProperty("DTS$ContentLocation", "MEMORY");
					msg.setLongProperty("DTS$PickupTimestamp",
							new Date().getTime());
					msg.setStringProperty("DTS$SourceExternalName", "FEA");
					msg.setStringProperty("DTS$FileName", "FEA_" + returnId
							+ ".xml");
					msg.setLongProperty("DTS$FileSize",
							message.getBytes().length);
				}

				msg.setStringProperty("filename", "FEA_" + returnId + ".xml");
				
				if (frInstance != null && !frInstance.trim().equals(""))
					msg.setStringProperty("frinstance", frInstance);

				return msg;
			}
		};

		if (log.isDebugEnabled())
			log.debug("<<< createMessageCreator");

		return creator;
	}

}
