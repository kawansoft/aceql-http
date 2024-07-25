package org.kawanfw.test.util;

import java.util.UUID;

public class UUIDGenerator {

    public static void main(String[] args) {
	UUID uuid = UUID.randomUUID();
	String uuidStr = uuid.toString();
	System.out.println("UUID: " + uuidStr);
    }

}
