/**
 * Author:  T Murray
 * Date:    07/03/2013
 * Project: FDE020
 * 
 * Copyright AMT-Sybex 2013
 */
package com.amtsybex.fieldreach.services.jms.impl;

import javax.jms.JMSException;
import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.amtsybex.fieldreach.services.jms.FieldreachTransactionJMSController;
import com.amtsybex.fieldreach.utils.impl.Common;


public class FieldreachTransactionJMSControllerImpl implements FieldreachTransactionJMSController
{

	private JmsTemplate _jmsTemplate;
	private Queue _workStatusQueue;
	
	static Logger _log = LoggerFactory.getLogger(
			FieldreachTransactionJMSControllerImpl.class.getName());
	
	/*-------------------------------------------
	 - Public Methods
	 --------------------------------------------*/
	
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
	public void dispatchWorkStatusTransaction(String message) throws JMSException 
	{
		_log.debug(">>> dispatchWorkStatusTransaction message=XXX");
		
		if (_log.isDebugEnabled())
			_log.debug("Dispatching message to Queue[" + Common.CRLFEscapeString(_workStatusQueue.toString()) + 
				Common.CRLFEscapeString("] Message[" + message + "]"));
		
		_jmsTemplate.convertAndSend(_workStatusQueue, message);
		
		_log.debug("<<< dispatchWorkStatusTransaction");
	}
	
	
	/**
	 * Set the JMSTemplate object that used be used for dispatching Fieldreach
	 * Transaction messages.
	 * 
	 * @param jmsTemplate
	 * JmsTemplate object that has been configured with an appropriate broker
	 * JMS connection factory.
	 */
	public void setJmsTemplate(JmsTemplate jmsTemplate)
	{
		_log.debug(">>> setJmsTemplate jmsTemplate=XXX");
		
		this._jmsTemplate = jmsTemplate;
		
		_log.debug("<<< setJmsTemplate");
	}

	
	/**
	 * Set the destination queue for WORKSTATUS transaction messages.
	 * 
	 * @param queue
	 */
	public void setWorkStatusQueue(Queue queue)
	{
		_log.debug(">>> setWorkStatusQueue queue=XXX");
		
		this._workStatusQueue = queue;
		
		_log.debug("<<< setWorkStatusQueue");
	}
	
}


