package org.kawanfw.test.api.server.auth;

import java.io.IOException;
import java.util.Map;

import org.kawanfw.sql.api.server.auth.headers.RequestHeadersAuthenticator;

public class MyRequestHeadersAuthenticator implements RequestHeadersAuthenticator {

    @Override
    public boolean validate(Map<String, String> headers) throws IOException {
        // Print all the request headers (name, value) on stdout
        for (Map.Entry<String, String> mapElement  : headers.entrySet()) {
            String key = mapElement.getKey();
            String value = mapElement.getValue();

            System.out.println(key + " : " + value);
        }

        // This true says that we have accepted all values.
        return true;
    }

}
