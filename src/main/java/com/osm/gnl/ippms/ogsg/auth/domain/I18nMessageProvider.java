/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.auth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

public abstract class I18nMessageProvider {

	private MessageSourceAccessor messages;

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}

	/**
	 * Convenience method for getting a i18n key's value.
	 * 
	 * @param msgKey
	 *            the i18n key to lookup
	 * @return the message for the key
	 */
	protected String getText(String msgKey) {
		return messages.getMessage(msgKey);
	}

	/**
	 * Convenient method for getting a i18n key's value with a single string
	 * argument.
	 * 
	 * @param msgKey
	 *            the i18n key to lookup
	 * @param arg
	 *            arguments to substitute into key's value
	 * @return the message for the key
	 */
	protected String getText(String msgKey, String arg) {
		return getText(msgKey, new Object[] { arg });
	}

	/**
	 * Convenience method for getting a i18n key's value with arguments.
	 * 
	 * @param msgKey
	 *            the i18n key to lookup
	 * @param args
	 *            arguments to substitute into key's value
	 * @return the message for the key
	 */
	protected String getText(String msgKey, Object[] args) {
		return messages.getMessage(msgKey, args);
	}

}
