/**
 * Project: FDE030
 * Author:	T Murray
 * Date:	09/01/2015
 * 
 * Copyright AMT-Sybex
 */
package com.amtsybex.fieldreach.extract.jms;

import javax.jms.Destination;
import javax.jms.JMSException;

import org.springframework.jms.core.JmsTemplate;

public interface FeaJmsController {

	/**
	 * Takes the message supplied and dispatches it as a TextMessage JMS message
	 * to the queue configured.
	 * 
	 * @param frInstance
	 *            Fieldreach instance the message supplied was extracted from.
	 * 
	 * @param returnId
	 *            The returnId of the extracted script result.
	 *            
	 * @param message
	 *            Message to be dispatched to the queue configured.
	 * 
	 * @throws JMSException
	 */
	public void dispatch(String frInstance, int returnId, String message) throws JMSException;

	/**
	 * Set the JMSTemplate object that used be used for dispatching JMS
	 * messages.
	 * 
	 * @param jmsTemplate
	 *            JmsTemplate object that has been configured with an
	 *            appropriate broker JMS connection factory.
	 */
	public void setJmsTemplate(JmsTemplate jmsTemplate);

	/**
	 * Set the destination queue that the configured JMSTemplate will dispatch
	 * JMS messages to.
	 * 
	 * @param destination
	 */
	public void setDestination(Destination destination);
	
	/**
	 * Indicate if AIB headers should be present in the JMS message produced.
	 * 
	 * @param headers
	 */
	public void setAibHeaders(boolean headers);
	
}
