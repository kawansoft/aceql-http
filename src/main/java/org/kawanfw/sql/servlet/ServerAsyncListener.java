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
package org.kawanfw.sql.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.RejectedExecutionException;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kawanfw.sql.servlet.sql.LoggerUtil;

public class ServerAsyncListener implements AsyncListener {

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
	// Do nothing.
    }

    @Override
    public void onComplete(AsyncEvent event) throws IOException {
	// Do nothing.
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
	AsyncDebug.debug("ASYNC ERROR:" + event.getThrowable());

	if (AsyncDebug.DEBUG) {
	    event.getThrowable().printStackTrace(System.out);
	}

	HttpServletRequest request = (HttpServletRequest)event.getSuppliedRequest();
	HttpServletResponse response = (HttpServletResponse)event.getSuppliedResponse();

	OutputStream out = null;
	try {
	    out = response.getOutputStream();
	} catch (IllegalStateException e) {
	    // Do nothing. Case it was already opened in main servlet...
	} catch (IOException ioexception) {
	    AsyncDebug.debug("1 Internal IOException: " + ioexception.toString());
	    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    return;
	}

	if (out == null) {
	    System.out.println(AsyncDebug.getNowFormatted() + " " +  "2 Internal: out is null!");
	    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    LoggerUtil.log(request, event.getThrowable());
	    return;
	}

	// Rejected response means server is timeout
	if ( event.getThrowable() instanceof RejectedExecutionException) {
	    AsyncDebug.debug("set reponse status to SC_SERVICE_UNAVAILABLE");
	    response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
	    String message = "Server is too busy and not available for now. Please try later.";
	    out.write(("<font face=\"arial\"><h3>" + message + "</h3>").getBytes());
	    LoggerUtil.log(request, event.getThrowable());
	}
	else {
	    AsyncDebug.debug("Set reponse status to SC_INTERNAL_SERVER_ERROR");
	    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    String message = "Server is on error and not available. Please try later.";
	    out.write(("<font face=\"arial\"><h3>" + message + "</h3>").getBytes());
	    LoggerUtil.log(request, event.getThrowable());

	}
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
	HttpServletRequest request = (HttpServletRequest)event.getSuppliedRequest();
	LoggerUtil.log(request, event.getThrowable());

	HttpServletResponse response = (HttpServletResponse)event.getSuppliedResponse();
	AsyncDebug.debug("Set reponse status to SC_GATEWAY_TIMEOUT");
	response.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
    }



}
