/*
 * Copyright (c)2022 KawanSoft S.A.S. All rights reserved.
 * 
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file in the project's root directory.
 *
 * Change Date: 2026-09-01
 *
 * On the date above, in accordance with the Business Source License, use
 * of this software will be governed by version 2.0 of the Apache License.
 */
package org.kawanfw.test.parms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kawanfw.sql.util.FrameworkSystemUtil;

/**
 * @author Nicolas de Pomereu
 *
 */
public class ProxyLoader {

    private static final String NEOTUNNEL_TXT = "i:\\neotunnel.txt";

    /** Proxy to use with HttpUrlConnection */
    private Proxy proxy = null;

    /** For authenticated proxy */
    private PasswordAuthentication passwordAuthentication = null;

    public Proxy getProxy() throws IOException, URISyntaxException {
	if (FrameworkSystemUtil.isAndroid()) {
	    return null;
	}

	System.setProperty("java.net.useSystemProxies", "true");
	List<Proxy> proxies = ProxySelector.getDefault()
		.select(new URI("http://www.google.com/"));

	if (proxies != null && proxies.size() >= 1) {
	    System.out.println("Loading proxy file info...");

	    if (proxies.get(0).type().equals(Proxy.Type.DIRECT)) {
		return null;
	    }

	    File file = new File(NEOTUNNEL_TXT);
	    if (file.exists()) {
		String proxyValues = FileUtils.readFileToString(file,
			Charset.defaultCharset());
		String username = StringUtils.substringBefore(proxyValues, " ");
		String password = StringUtils.substringAfter(proxyValues, " ");

		username = username.trim();
		password = password.trim();

		proxy = new Proxy(Proxy.Type.HTTP,
			new InetSocketAddress("localhost", 8080));

		passwordAuthentication = new PasswordAuthentication(username,
			password.toCharArray());

		System.out.println("USING PROXY WITH AUTHENTICATION: " + proxy
			+ " / " + username + " " + password);
	    } else {
		throw new FileNotFoundException(
			"proxy values not found. No file " + file);
	    }
	}

	return proxy;
    }

    /**
     * @return the passwordAuthentication
     */
    public PasswordAuthentication getPasswordAuthentication() {
	return passwordAuthentication;
    }
}
