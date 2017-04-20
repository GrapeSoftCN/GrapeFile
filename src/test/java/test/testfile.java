package test;

import httpServer.booter;

public class testfile {
public static void main(String[] args) {
	booter booter = new booter();
	try {
		System.out.println("Grapefile!");
		System.setProperty("AppName", "Grapefile");
		booter.start(1002);
	} catch (Exception e) {
		// TODO: handle exception
	}
}
}
