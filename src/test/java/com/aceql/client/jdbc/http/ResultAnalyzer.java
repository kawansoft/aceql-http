/*
 * This file is part of AceQL Client SDK.
 * AceQL Client SDK: Remote JDBC access over HTTP with AceQL HTTP.                                 
 * Copyright (C) 2017,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                                
 *                                                                               
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.aceql.client.jdbc.http;

import java.io.StringReader;
import java.net.HttpURLConnection;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;

/**
 * 
 * Analyses the JSON result sent by server
 * @author Nicolas de Pomereu
 *
 */
class ResultAnalyzer {

    private String jsonResult = null;
    private int httpStatusCode;
    
    /** We try to find status. If error parsing, invalidJsonStream = true*/
    private boolean invalidJsonStream = false;
    
    /** Exception when parsing the JSON stream. Futur usage */
    private Exception parseException = null;
    private String httpStatusMessage;
    
    
    /**
     * Constructor
     * @param jsonResult
     * @param httpStatusCode
     * @param httpStatusMessage
     */
    public ResultAnalyzer(String jsonResult, int httpStatusCode, String httpStatusMessage) {
	
	if (jsonResult != null) {
	    jsonResult = jsonResult.trim();
	}

	this.jsonResult = jsonResult;
	this.httpStatusCode = httpStatusCode;
	this.httpStatusMessage = httpStatusMessage;
    }


    /**
     * Says if status is OK
     * @return true if status is OK
     */
    public boolean isStatusOk() {
	
	if (jsonResult == null || jsonResult.isEmpty()) {
	    return false;
	}
	
	try {
	    JsonReader reader = Json.createReader(new StringReader(jsonResult));
	    JsonStructure jsonst = reader.read();

	    JsonObject object = (JsonObject) jsonst;
	    JsonString status = (JsonString) object.get("status");

	    if (status != null && status.getString().equals("OK")) {
		return true;
	    } else {
		return false;
	    }
	} catch (Exception e) {
	    this.parseException = e;
	    invalidJsonStream = true;
	    return false;
	}

    }

    /**
     * Returns the result for key name "result"
     * @param name
     * @return the value
     */
    public String getResult() {
	return getValue("result");
    }
    
    /**
     * Returns the value for a name
     * @param name
     * @return the value
     */
    public String getValue(String name) {
	if (name == null) {
	    throw new NullPointerException("name is null!");
	}
	
	if (isInvalidJsonStream()) {
	    return null;
	}

	try {
	    JsonReader reader = Json.createReader(new StringReader(jsonResult));
	    JsonStructure jsonst = reader.read();

	    JsonObject object = (JsonObject) jsonst;
	    JsonString value = (JsonString) object.get(name);
	    
	    if (value == null) {
	        return null;
	    }
	    
	    return value.getString();
	} catch (Exception e) {
	    this.parseException = e;
	    return null;
	}
    }
    
    /**
     * Says if the JSON Stream is invalid
     * @return rue if JSOn stream is invalid
     */
    private boolean isInvalidJsonStream() {
	if (jsonResult == null || jsonResult.isEmpty()) {
	    return true;
	}
	
	if (invalidJsonStream) {
	    return true;
	}
	
	return false;
    }


    /**
     * Returns the int value for a name
     * @param name
     * @return the value
     */
    public int getIntvalue(String name) {
	if (name == null) {
	    throw new NullPointerException("name is null!");
	}
	
	if (isInvalidJsonStream()) {
	    return -1;
	}

	try {
	    JsonReader reader = Json.createReader(new StringReader(jsonResult));
	    JsonStructure jsonst = reader.read();

	    JsonObject object = (JsonObject) jsonst;
	    JsonNumber value = (JsonNumber) object.get(name);
	    
	    if (value == null) {
	        return -1;
	    }
	    
	    return value.intValue();
	} catch (Exception e) {
	    this.parseException = e;
	    return -1;
	}
    }
    
//    /**
//     * Returns the long value for a name
//     * @param name
//     * @return the value
//     */
//    public long getLongvalue(String name) {
//	if (name == null) {
//	    throw new NullPointerException("name is null!");
//	}
//	
//	JsonReader reader = Json.createReader(new StringReader(jsonResult));
//	JsonStructure jsonst = reader.read();
//
//	JsonObject object = (JsonObject) jsonst;
//	JsonNumber value = (JsonNumber) object.get(name);
//	
//	if (value == null) {
//	    return -1;
//	}
//	
//	return value.longValue();
//    }
    
    
    /**
     * Returns the error_type in case of failure
     * @return the error_type in case of failure, -1 if no error
     */
    public int getErrorType() {
	
	if (isInvalidJsonStream()) {
	    return 0;
	}
	
	try {
	    JsonReader reader = Json.createReader(new StringReader(jsonResult));
	    JsonStructure jsonst = reader.read();

	    JsonObject object = (JsonObject) jsonst;
	    JsonString status = (JsonString) object.get("status");

	    if (status == null) {
	        return -1;
	    }
	    
	    JsonNumber errorType = (JsonNumber) object.get("error_type");
	    
	    if (errorType == null) {
	        return -1;
	    }
	    else {
	        return errorType.intValue();
	    }
	} catch (Exception e) {
	    this.parseException = e;
	    return -1;
	}

	
    }
    
    /**
     * Returns the error_message in case of failure
     * @return the error_message in case of failure, null if no error
     */
    public String getErrorMessage() {
		
	if (isInvalidJsonStream()) {
	    
	    String errorMessage = "Unknown error.";
	    if (httpStatusCode != HttpURLConnection.HTTP_OK) {
		errorMessage = "HTTP FAILURE " + httpStatusCode + " (" + httpStatusMessage + ")";
	    }
	    
	    return errorMessage;
	}
	
	try {
	    JsonReader reader = Json.createReader(new StringReader(jsonResult));
	    JsonStructure jsonst = reader.read();

	    JsonObject object = (JsonObject) jsonst;
	    JsonString status = (JsonString) object.get("status");

	    if (status == null) {
	        return null;
	    }
	    
	    JsonString errorMessage = (JsonString) object.get("error_message");
	    if (errorMessage == null) {
	        return null;
	    }
	    else {
	        return errorMessage.getString();
	    }
	} catch (Exception e) {
	    this.parseException = e;
	    return null;
	}
	
    }
    
    /**
     * Returns the stack_trace in case of failure
     * @return the stack_trace in case of failure, null if no stack_trace
     */
    public String getStackTrace() {
	
	if (isInvalidJsonStream()) {
	    return null;
	}
	
	try {
	    JsonReader reader = Json.createReader(new StringReader(jsonResult));
	    JsonStructure jsonst = reader.read();

	    JsonObject object = (JsonObject) jsonst;
	    JsonString status = (JsonString) object.get("status");

	    if (status == null) {
	        return null;
	    }
	    
	    JsonString stackTrace = (JsonString) object.get("stack_trace");
	    if (stackTrace == null) {
	        return null;
	    }
	    else {
	        return stackTrace.getString();
	    }
	} catch (Exception e) {
	    this.parseException = e;
	    return null;
	}
	
    }

//    
    
    @Override
    public String toString() {
	return "ResultAnalyzer [jsonResult=" + jsonResult + "]";
    }


    /**
     * Returns the Exception raised when parsing JSON stream
     * @return the Exception raised when parsing JSON stream
     */
    public Exception getParseException() {
        return parseException;
    }
    
    

}
