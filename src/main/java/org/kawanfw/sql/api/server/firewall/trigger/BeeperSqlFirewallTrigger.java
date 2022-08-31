/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2027-08-31
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
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
 * A trigger that simply beeps on the terminal if an attack is detected by a
 * {@code SqlFirewallManager}. <br>
 * Uses a slightly modified
 * <a href="https://gist.github.com/jbzdak/61398b8ad795d22724dd">GitHub Gist</a>
 * created by Jacek Bzdak.
 * 
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
	    BeepUtil.beep(750, 1000);
	} catch (InterruptedException | LineUnavailableException e) {
	    e.printStackTrace();
	}
    }

}
