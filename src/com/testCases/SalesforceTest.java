//package com.testCases;
//
//import org.junit.Test;
//
//import com.sforce.soap.enterprise.Connector;
//import com.sforce.soap.enterprise.EnterpriseConnection;
//import com.sforce.ws.ConnectionException;
//import com.sforce.ws.ConnectorConfig;
//
//import sfautoscript.Application;
//
//public class SalesforceTest {
//	static final String username = "ankuradmin.sharma@databricks.com.bse2";
//	static final String password = "vashistha@1233X4NNZenTOK3B4r8IL3xDYdyU";
//	static EnterpriseConnection connection;
//	
//	private static Application app = new Application();
//	
//	@Test
//	public void testingSf() {
//		ConnectorConfig config = new ConnectorConfig();
//		config.setUsername(username);
//		config.setPassword(password);
//		try {
//			connection = Connector.newConnection(config);
//			System.out.println("connection between java & sf cretating by Ankur Sharma");
//			System.out.println("Authantication Endpoint:" + config.getAuthEndpoint());
//			System.out.println("Service Endpoint:" + config.getServiceEndpoint());
//			System.out.println("Username of the User:" + config.getUsername());
//			System.out.println("Session Id:" + config.getSessionId());
//			System.out.println("Connection established successfully");
//			app.fetchOpportunities(); // method to fetch opportunities from salesforce
//
//		} catch (ConnectionException e1) {
//			e1.printStackTrace();
//		}
//
//		try {
//			connection.logout();
//			System.out.println("Connection Logout Successfully");
//		} catch (ConnectionException ce) {
//			ce.printStackTrace();
//		}
//	}
//	
//}
