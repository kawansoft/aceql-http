/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP
 * Copyright (C) 2021,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.
 *
 * AceQL HTTP is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * AceQL HTTP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301  USA
 *
 * Any modifications to this file must keep this entire header
 * intact.
 */
package org.kawanfw.sql.api.server.firewall.trigger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sound.sampled.LineUnavailableException;

import org.kawanfw.sql.api.server.SqlEvent;
import org.kawanfw.sql.api.server.firewall.SqlFirewallManager;
import org.kawanfw.sql.servlet.util.BeepUtil;

/**
 * Simply beeps on terminal if an attack is detected by a {@code SqlFirewallManager}.
 * Uses a <a href="https://gist.github.com/jbzdak/61398b8ad795d22724dd">GitHub Gist</a> created by Jacek Bzdak.
 * @author Nicolas de Pomereu
 * @since 11.0
 */
public class BeeperSqlFirewallTrigger implements SqlFirewallTrigger {

    /**
     * Beeps on terminal if an attack is detected by a {@code SqlFirewallManager}
     */
    @Override
    public void runIfStatementRefused(SqlEvent sqlEvent, SqlFirewallManager sqlFirewallManager, Connection connection)
	    throws IOException, SQLException {
	try {
	    BeepUtil.beep(750, 2000);
	} catch (InterruptedException | LineUnavailableException e) {
	    e.printStackTrace();
	}
    }

}
