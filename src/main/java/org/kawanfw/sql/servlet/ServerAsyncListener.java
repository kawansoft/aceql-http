/*
 * This file is part of AceQL HTTP.
 * AceQL HTTP: SQL Over HTTP                                     
 * Copyright (C) 2020,  KawanSoft SAS
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

    public ServerAsyncListener() {

    }


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
