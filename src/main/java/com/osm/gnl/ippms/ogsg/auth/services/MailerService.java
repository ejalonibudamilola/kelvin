/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.auth.services;

import com.osm.gnl.ippms.ogsg.auth.domain.Mailer;



public interface MailerService {

    boolean sendMailWithAttachments(Mailer mailer);

    boolean sendMailForPasswordReset(Mailer mailer);
}
