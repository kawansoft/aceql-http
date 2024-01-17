package org.kawanfw.sql.util;

import java.io.IOException;

public class BadClass {
    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
	badMethod1();
    }

    public static boolean badMethod1() {

	int value = 42;
	
	if (value == 42) {
            return true;
        }
	else {
	    return false;
	}
    }
}
