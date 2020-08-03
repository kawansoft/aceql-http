/**
 *
 */
package org.kawanfw.sql.api.server.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;

/**
 * Simple HttpClient to use for the AceQL AWS (Authentication Web Service)
 *
 * @author Nicolas de Pomereu
 * @since 5.0
 */
public class SimpleHttpClient {

    /** If true, calls will be traced */
    public static boolean TRACE_ON = false;

    private Proxy proxy = null;
    private int connectTimeout = 0;
    private int readTimeout = 0;

    private int httpStatusCode = HttpURLConnection.HTTP_OK;
    private String httpStatusMessage = null;

    /**
     * Default constructor.
     *
     * @param connectTimeout connect timeout in milliseconds
     * @param readTimeout    read timeout in milliseconds
     */
    public SimpleHttpClient(int connectTimeout, int readTimeout) {
	this.connectTimeout = connectTimeout;
	this.readTimeout = readTimeout;
    }

    /**
     * Allows to call a remote URL in POST mode and pass parameters.
     *
     * @param url           the URL to call
     * @param parametersMap the parameters, empty if none. (Cannot be null).
     * @return the value returned by the call
     *
     * @throws IOException                  if an IOException occurs
     * @throws ProtocolException            if a ProtocolException occurs
     * @throws SocketTimeoutException       if a if a ProtocolException occurs
     *                                      occurs
     * @throws UnsupportedEncodingException if a if a ProtocolException occurs
     *                                      occurs
     */
    public String callWithPost(URL url, Map<String, String> parametersMap)
	    throws IOException, ProtocolException, SocketTimeoutException, UnsupportedEncodingException {

	Objects.requireNonNull(url, "url cannot be null!");
	Objects.requireNonNull(parametersMap, "parametersMap cannot be null!");

	String result = null;
	try (InputStream in = callWithPostReturnStream(url, parametersMap);) {
	    if (in != null) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(in, out);

		result = out.toString("UTF-8");
		trace("result :" + result + ":");
	    }
	}
	return result;
    }

    /**
     * Allows to call a remote URL in GET mode and pass parameters. Result is put in
     * String.
     *
     * @param url           the URL to call
     * @param parametersMap the parameters, empty if none. (Cannot be null).
     * @return the value returned by the call
     *
     * @throws MalformedURLException        if a MalformedURLException occurs
     * @throws IOException                  if an IOException occurs
     * @throws ProtocolException            if a ProtocolException occurs
     * @throws SocketTimeoutException       if a if a ProtocolException occurs
     *                                      occurs
     * @throws UnsupportedEncodingException if a if a ProtocolException occurs
     *                                      occurs
     */
    public String callWithGet(final String url, Map<String, String> parametersMap)
	    throws MalformedURLException, IOException, ProtocolException, UnsupportedEncodingException {

	String responseBody = null;

	String urlNew = url;
	if (parametersMap != null && !parametersMap.isEmpty()) {
	    urlNew += "?" + getPostDataString(parametersMap);
	}

	try (InputStream in = callWithGetReturnStream(urlNew)) {
	    if (in == null)
		return null;

	    ByteArrayOutputStream out = new ByteArrayOutputStream();

	    IOUtils.copy(in, out);

	    responseBody = out.toString("UTF-8");
	    if (responseBody != null) {
		responseBody = responseBody.trim();
	    }

	    trace("----------------------------------------");
	    trace(responseBody);
	    trace("----------------------------------------");

	    return responseBody;
	}
    }

    /**
     * Gets and InputStream from execution.
     *
     * @param url the URL to call
     * @return the result of the call
     *
     * @throws MalformedURLException if a MalformedURLException occurs
     * @throws IOException           if an IOException occurs
     * @throws ProtocolException     if a ProtocolException occurs
     */
    private InputStream callWithGetReturnStream(String url)
	    throws MalformedURLException, IOException, ProtocolException {
	URL theUrl = new URL(url);
	HttpURLConnection conn = null;

	if (this.proxy == null) {
	    conn = (HttpURLConnection) theUrl.openConnection();
	} else {
	    conn = (HttpURLConnection) theUrl.openConnection(proxy);
	}

	conn.setRequestProperty("Accept-Charset", "UTF-8");
	conn.setConnectTimeout(connectTimeout);
	conn.setReadTimeout(readTimeout);
	conn.setRequestMethod("GET");
	conn.setDoOutput(true);

	trace();
	trace("Executing request " + url);

	httpStatusCode = conn.getResponseCode();
	httpStatusMessage = conn.getResponseMessage();

	InputStream in = null;
	// if (httpStatusCode == HttpURLConnection.HTTP_OK || httpStatusCode ==
	// HttpURLConnection.HTTP_MOVED_TEMP) {
	if (httpStatusCode == HttpURLConnection.HTTP_OK) {
	    in = conn.getInputStream();
	} else {
	    in = conn.getErrorStream();
	}

	return in;
    }

    private InputStream callWithPostReturnStream(URL theUrl, Map<String, String> parameters)
	    throws IOException, ProtocolException, SocketTimeoutException, UnsupportedEncodingException {
	HttpURLConnection conn = null;

	if (this.proxy == null) {
	    conn = (HttpURLConnection) theUrl.openConnection();
	} else {
	    conn = (HttpURLConnection) theUrl.openConnection(proxy);
	}

	conn.setRequestProperty("Accept-Charset", "UTF-8");
	conn.setConnectTimeout(connectTimeout);
	conn.setReadTimeout(readTimeout);
	conn.setRequestMethod("POST");
	conn.setDoOutput(true);

	try (OutputStream connOut = conn.getOutputStream();) {
	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connOut, "UTF-8"));
	    writer.write(getPostDataString(parameters));

	    // writer.flush();
	    writer.close();
	}

	trace();
	trace("Executing request: " + theUrl.toString());

	if (parameters.containsKey("sql")) {
	    trace("sql..............: " + parameters.get("sql"));
	}

	trace("parameters.......: " + parameters);

	// Analyze the error after request execution
	httpStatusCode = conn.getResponseCode();
	httpStatusMessage = conn.getResponseMessage();

	InputStream in = null;
	if (httpStatusCode == HttpURLConnection.HTTP_OK) {
	    in = conn.getInputStream();
	} else {
	    in = conn.getErrorStream();
	}

	return in;
    }

    /**
     * Formats and URL encode the the post data for POST.
     *
     * @param requestParams the parameter names and values
     * @return the formated and URL encoded string for the POST.
     * @throws UnsupportedEncodingException if an UnsupportedEncodingException
     *                                      occurs
     */
    public static String getPostDataString(Map<String, String> requestParams) throws UnsupportedEncodingException {
	StringBuilder result = new StringBuilder();
	boolean first = true;

	for (Map.Entry<String, String> entry : requestParams.entrySet()) {

	    // trace(entry.getKey() + "/" + entry.getValue());

	    if (first)
		first = false;
	    else
		result.append("&");

	    if (entry.getValue() != null) {
		result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
		result.append("=");
		result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	    }
	}

	return result.toString();
    }

    /**
     * Gets the connect timeout.
     *
     * @return the connect timeout.
     */
    public int getConnectTimeout() {
	return connectTimeout;
    }

    /**
     * Get the read timeout.
     *
     * @return the read timeout.
     */
    public int getReadTimeout() {
	return readTimeout;
    }

    /**
     * Gets the HTTP status code
     *
     * @return the HTTP status code
     */
    public int getHttpStatusCode() {
	return httpStatusCode;
    }

    /**
     * Gets the HTTP status message
     *
     * @return the HTTP status message
     */

    public String getHttpStatusMessage() {
	return httpStatusMessage;
    }

    private void trace() {
	if (TRACE_ON) {
	    System.out.println();
	}
    }

    private void trace(String s) {
	if (TRACE_ON) {
	    System.out.println(s);
	}
    }

}
