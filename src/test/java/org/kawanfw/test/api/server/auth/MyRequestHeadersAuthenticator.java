package org.kawanfw.test.api.server.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kawanfw.sql.api.server.auth.headers.RequestHeadersAuthenticator;

public class MyRequestHeadersAuthenticator implements RequestHeadersAuthenticator {

    @Override
    public boolean validate(Map<String, String> headers) throws IOException {
        // Print all the request headers (name, value) on stdout
	
	Set<String> keysSet = headers.keySet();
	List<String> keysList = new ArrayList<String>();
	keysList.addAll(keysSet);
	
        System.out.println();
	for (String key : keysList) {
            System.out.println(key + ": " + headers.get(key));
	}
	
        // This true says that we have accepted all values.
        return true;
    }

}
