/**
 * Author:  T Murray
 * Date:    07/03/2013
 * Project: FDE020
 * 
 * Copyright AMT-Sybex 2013
 */
package com.amtsybex.fieldreach.services.jms;

import javax.jms.JMSException;
import javax.jms.Queue;

import org.springframework.jms.core.JmsTemplate;

public interface FieldreachTransactionJMSController 
{
	
	/**
	 * Takes the message supplied and dispatches it to the queue configured for
	 * WORKSTATUS transactions.
	 * 
	 * @param message
	 * WORKSTATUS transaction XML to be dispatched  to the queue configured for
	 * WORKSTATUS transactions.
	 * 
	 * @throws JMSException
	 */
	public void dispatchWorkStatusTransaction(String message) throws JMSException;
	
	
	/**
	 * Set the JMSTemplate object that used be used for dispatching Fieldreach
	 * Transaction messages.
	 * 
	 * @param jmsTemplate
	 * JmsTemplate object that has been configured with an appropriate broker
	 * JMS connection factory.
	 */
	public void setJmsTemplate(JmsTemplate jmsTemplate);

	
	/**
	 * Set the destination queue for WORKSTATUS transaction messages.
	 * 
	 * @param queue
	 */
	public void setWorkStatusQueue(Queue queue);
}
