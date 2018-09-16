package sfautoscript;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.Opportunity;
import com.sforce.soap.enterprise.sobject.OpportunityLineItem;
import com.sforce.soap.enterprise.sobject.Product2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.*;

import com.sforce.soap.enterprise.Connector;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class Application {

	static String val[] = new String[] { "360053813927", "7803506843", "1408088026", "792107403", "1397097646",
			"48810436", "48406213", "55533143", "7056294526", "6301141826", "3448150763", "1900419383", "34532744",
			"1409114903", "1245053666", "1434909646", "1433695366", "1321333986", "1409542686", "35517370",
			"1408360343", "7802855963", "1445070086", "32474350", "299077753", "6641059623", "1435854266", "32322820",
			"30366070", "1440184326", "3959330786", "6086307746", "7072918123", "2657723006", "1408261066",
			"1444985063", "7782229566", "1445069286", "48176546", "6645787203", "6951640966", "7072918103", "40555684",
			"2687594583", "1445198243", "35089994" };

	static Set<String> ZendeskIds = new HashSet<String>();
	static {
		for (String id : val) {
			ZendeskIds.add(id);
		}
	}
	static String revenue;
	static String subs;
	static String zendesk_id;
	static String support;
	static String start_Date;
	static String end_Date;
	static String account_owner;
	static String field_Engineer;
	static String cse_Name;
	static String support_Hours;
	static String accont_Status;
	static final String username = "ankuradmin.sharma@databricks.com.bse2";
	static final String password = "vashistha@1233X4NNZenTOK3B4r8IL3xDYdyU";
	static EnterpriseConnection connection;

	// long MID = 250000000;
	// long ENTERPRISE = 250000000;
	public static void main(String[] args) {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(username);
		config.setPassword(password);
		try {
			connection = Connector.newConnection(config);
			System.out.println("connection between java & sf cretating by Ankur Sharma");
			System.out.println("Authantication Endpoint:" + config.getAuthEndpoint());
			System.out.println("Service Endpoint:" + config.getServiceEndpoint());
			System.out.println("Username of the User:" + config.getUsername());
			System.out.println("Session Id:" + config.getSessionId());
			System.out.println("Connection established successfully");
			fetchOpportunities(); // method to fetch opportunities from salesforce

		} catch (ConnectionException e1) {
			e1.printStackTrace();
		}

		try {
			connection.logout();
			System.out.println("Connection Logout Successfully");
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}

	}

	private static List<Opportunity> getPAYGOPID(Map<String, Opportunity> opportunityMap) {
		List<Opportunity> list = new ArrayList<>();
		for (String key : opportunityMap.keySet()) {
			if (opportunityMap.get(key).getType().equals("PAYG")) {
				list.add(opportunityMap.get(key));
			}
		}
		return list;
	}

	private static void fetchOpportunities() throws ConnectionException {
		QueryResult queryresults = connection
				.query("SELECT Id, AccountId, Account.Name, Account.Zendesk__Zendesk_Organization_Id__c,"
						+ " Account.AnnualRevenue, Start_Date__c, End_Date__c,Owner.Name, DB_Users__r.Name, CSE__C,  Type, "
						+ "CloseDate FROM Opportunity WHERE stageName = 'Closed Won' "
						+ "AND ((Type = 'PAYG' AND CloseDate = THIS_MONTH) or "
						+ "(CloseDate <= TODAY AND End_Date__c >= TODAY AND Start_Date__c < TODAY and Type != 'PAYG'))");

		System.out.println(queryresults.getSize());

		Map<String, Opportunity> opportunityMap = new HashMap<>();

		if (queryresults.getSize() > 0) {
			for (int i = 0; i < queryresults.getSize(); i++) {
				Opportunity o = (Opportunity) queryresults.getRecords()[i];

				opportunityMap.put(o.getId(), o);
				// System.out.println("OIUD "+o.getAccountId());

			}

		}
		List<Opportunity> list = getPAYGOPID(opportunityMap);
		// System.out.println("list " + list.size());
		List<OpportunityLineItem> extraItem = new ArrayList<>();
		QueryResult line_item_query = connection
				.query("SELECT id, Opportunityid,Product2.Name, TotalPrice FROM OpportunityLineItem");
		if (list.size() > 0) {
			QueryResult line_item_query_payg = connection.query(
					"SELECT id, Opportunityid,Product2.Name, TotalPrice FROM OpportunityLineItem where Opportunity.Type='PAYG'");
			Set<String> val = new HashSet<>();
			if (line_item_query_payg.getSize() > 0) {
				for (int j = 0; j < line_item_query_payg.getSize(); j++) {
					val.add(((OpportunityLineItem) line_item_query_payg.getRecords()[j]).getOpportunityId());
				}
			}

			for (Opportunity opportunity : list) {
				if (val.contains(opportunity.getId())) {
					continue;
				}
				OpportunityLineItem itm = new OpportunityLineItem();
				Product2 product2 = new Product2();
				product2.setName("");
				itm.setId("");
				itm.setTotalPrice(0.0);
				itm.setProduct2(product2);
				itm.setOpportunityId(opportunity.getId());
				// System.out.println("va "+opportunity.getAccountId());
				extraItem.add(itm);
			}

		}
		// System.out.println("extraItem " + extraItem.size());

		// System.out.println("Total Record" + line_item_query.getSize());
		System.out.println("----------------------------------------------------------------------------------");

		Map<String, OpportunityLineItem> lineItemMapSupport = new HashMap();

		Map<String, OpportunityLineItem> lineItemMapSubscription = new HashMap();

		if (line_item_query.getSize() > 0) {
			for (int j = 0, k = 0; j < line_item_query.getSize() || k < extraItem.size();) {
				OpportunityLineItem opline = null;
				if (j < line_item_query.getSize()) {
					opline = (OpportunityLineItem) line_item_query.getRecords()[j];
					j++;
				} else if (k < extraItem.size()) {
					opline = (OpportunityLineItem) extraItem.get(k);
					k++;
				}

				if (opline == null || opline.getProduct2().getName() == null
						|| opportunityMap.get(opline.getOpportunityId()) == null) {
					continue;
				}

				// System.out.println("->> "
				// +opportunityMap.get(opline.getOpportunityId()).getAccountId());

				if (opportunityMap.get(opline.getOpportunityId()).getType().equals("PAYG")
						|| opline.getProduct2().getName().contains("support")
						|| opline.getProduct2().getName().contains("Support")
						|| opline.getProduct2().getName().contains("Committed")
						|| opline.getProduct2().getName().contains("Enterprise")) {

				} else {
					continue;
				}

				if (opportunityMap.get(opline.getOpportunityId()) == null) {
					continue;

				}

				// -----------------------------------support--------------------------------------

				if (lineItemMapSupport.keySet()
						.contains((opportunityMap.get(opline.getOpportunityId()).getAccountId()))) {
					if (opportunityMap.get(opline.getOpportunityId()).getType().equals("PAYG")
							|| opline.getProduct2().getName().equals("Support/Production (Monthly)")
							|| opline.getProduct2().getName().equals("Support/Enhanced (Monthly)")
							|| opline.getProduct2().getName().equals("Support/Business (Monthly)")
							|| opline.getProduct2().getName().equals("Basic Support (Deprecated)")) {
						if (wait(opline.getProduct2().getName()) > wait(
								lineItemMapSupport.get(opportunityMap.get(opline.getOpportunityId()).getAccountId())
										.getProduct2().getName())) {
							lineItemMapSupport.put(opportunityMap.get(opline.getOpportunityId()).getAccountId(),
									opline);
						}
					}
				} else {
					if (opportunityMap.get(opline.getOpportunityId()).getType().equals("PAYG")
							|| opline.getProduct2().getName().equals("Support/Production (Monthly)")
							|| opline.getProduct2().getName().equals("Support/Enhanced (Monthly)")
							|| opline.getProduct2().getName().equals("Support/Business (Monthly)")
							|| opline.getProduct2().getName().equals("Basic Support (Deprecated)")) {
						lineItemMapSupport.put(opportunityMap.get(opline.getOpportunityId()).getAccountId(), opline);
					}
				}
				// -----------------------------------subscription--------------------------------------

				if (lineItemMapSubscription.keySet()
						.contains((opportunityMap.get(opline.getOpportunityId()).getAccountId()))) {
					if (opportunityMap.get(opline.getOpportunityId()).getType().equals("PAYG")
							|| opline.getProduct2().getName().equals("Enterprise Platform Fee")
							|| opline.getProduct2().getName().equals("Committed Blended Monthly DBUs")
							|| opline.getProduct2().getName().equals("Committed Blended Annual DBUs")
							|| opline.getProduct2().getName().equals("Committed Monthly DBUs")) {

						if (wait2(opline.getProduct2().getName()) > wait2(lineItemMapSubscription
								.get(opportunityMap.get(opline.getOpportunityId()).getAccountId()).getProduct2()
								.getName()))

						{
							lineItemMapSubscription.put(opportunityMap.get(opline.getOpportunityId()).getAccountId(),
									opline);

						}
					}
				}

				else {
					if (opportunityMap.get(opline.getOpportunityId()).getType().equals("PAYG")
							|| opline.getProduct2().getName().equals("Enterprise Platform Fee")
							|| opline.getProduct2().getName().equals("Committed Blended Monthly DBUs")
							|| opline.getProduct2().getName().equals("Committed Blended Annual DBUs")
							|| opline.getProduct2().getName().equals("Committed Monthly DBUs")) {
						lineItemMapSubscription.put(opportunityMap.get(opline.getOpportunityId()).getAccountId(),
								opline);

					}
				}

			}
		}

//		System.out.println("opportunitys Supports " + lineItemMapSupport.size());
//		System.out.println("opportunitys Subscriptions" + lineItemMapSubscription.size());
		String M = "";

		Map<String, Long> valueForSupport = new HashMap<String, Long>();
		Map<String, Long> valueForSubs = new HashMap<String, Long>();

		for (String key : lineItemMapSupport.keySet()) {
			OpportunityLineItem opline = lineItemMapSupport.get(key);
			OpportunityLineItem subscripton = lineItemMapSubscription.get(key);

			Opportunity o = opportunityMap.get(opline.getOpportunityId());
			M += getVal(opline, subscripton, o);
			lineItemMapSubscription.remove(key);
		}

		for (String key : lineItemMapSubscription.keySet()) {

			OpportunityLineItem opline = lineItemMapSupport.get(key);
			OpportunityLineItem subscripton = lineItemMapSubscription.get(key);
			Opportunity o = opportunityMap.get(subscripton.getOpportunityId());
			M += getVal(opline, subscripton, o);
		}

		WriteToFile file = new WriteToFile();
		file.write(M);

	}

	public static int wait(String name) {
		if (name.equals("Support/Production (Monthly)") || name.equals("Production Support")) {
			return 16;

		}

		if (name.equals("Support/Enhanced (Monthly)") || name.equals("Enhanced Support")) {
			return 8;

		}

		if (name.equals("Support/Business (Monthly)")) {
			return 4;

		}

		if (name.equals("Basic Support (Deprecated)") || name.equals("Basic Support)")) {
			return 2;

		}
		return 0;

	}

	public static int wait2(String name) {
		if (name.equals("Enterprise Platform Fee")) {
			return 18;

		}

		if (name.equals("Committed Blended Monthly DBUs")) {
			return 14;

		}

		if (name.equals("Committed Blended Annual DBUs")) {
			return 12;

		}

		if (name.equals("Committed Monthly DBUs")) {
			return 11;

		}
		return 0;

	}

	public static String getVal(OpportunityLineItem opline, OpportunityLineItem subscripton, Opportunity o) {

		// System.out.println(o.getAccount().getZendesk_Org_Id__c());
		support="";
		accont_Status = "account_status_no_support";
		if (o == null)
			return "";

		if (opline != null) {
			if (opline.getProduct2().getName() != null) {
				support = opline.getProduct2().getName();
				accont_Status = "account_status_active";
			}
		}

		if (subscripton != null) {
			if (subscripton.getProduct2() != null) {
				subs = subscripton.getProduct2().getName();
			}
		} else {
			subs = "";
		}

		if (o.getAccount() != null) {
			if (o.getAccount().getZendesk__Zendesk_Organization_Id__c() != null) {
				zendesk_id = o.getAccount().getZendesk__Zendesk_Organization_Id__c();
			}
		} else {
			zendesk_id = "";
		}

		if (o.getAccount().getAnnualRevenue() != null) {
			if ((o.getAccount().getAnnualRevenue() >= 2500000000l)
					|| ((o.getAccount().getAnnualRevenue()) < 1000000000)) {
				revenue = "market_segment_mid_market";

			} else if ((o.getAccount().getAnnualRevenue() == 1000000000l)
					|| (o.getAccount().getAnnualRevenue() > 1000000000l)) {
				revenue = "market_segment_enterprise";
			} else {
				revenue = "market_segment_commercial";
			}
		}

		else {
			revenue = "null";
		}

		if (o.getOwner() != null || o.getOwner().getName() != null) {
			account_owner = o.getOwner().getName();
		} else {
			account_owner = "";
		}

		if (o.getDB_Users__r() == null || o.getDB_Users__r().getName() == null) {
			field_Engineer = "";
		} else {
			field_Engineer = o.getDB_Users__r().getName();
		}

		if (o.getCSE__c() != null) {
			cse_Name = o.getCSE__c();
		} else {
			cse_Name = "";
		}

		if (support != null && !support.trim().equals("")) {

			//System.out.println(support+" "+o.getAccount().getName());
			
			if ((support.equals("Basic Support (Deprecated)") && opline.getTotalPrice() == 0.00)
					|| (support.equals("Basic Support") && opline.getTotalPrice() == 0.00)) {

				support = "support_tier_basic_no_support";
				support_Hours = "0";
			}

			else if ((support.equals("Basic Support (Deprecated)") && (opline.getTotalPrice() > 0.00))
					|| ((support.equals("Basic Support")) && (opline.getTotalPrice() > 0.00))
					|| support.equals("Support/Business (Monthly)")) {

				support = "support_tier_business";
				support_Hours = "2";

			} else if (support.equals("Support/Production (Monthly)") || (support.equals("Production Support"))) {
				support = "support_tier_production";
				support_Hours = "8";
			} else if (support.equals("Support/Enhanced (Monthly)") || support.equals("Enhanced Support")) {
				support_Hours = "4";
				if (ZendeskIds.contains(zendesk_id)) {
					support = "support_tier_enhanced_slack";
				} else {
					support = "support_tier_enhanced_no_slack";
				}
			}
		} else {
			support_Hours = "";
		}
		
		if(subs.contains("Committed Monthly DBUs"))
		{
			subs="subscription_professional";
		}
		else if(subs.contains("Enterprise Platform Fee"))
		{
			subs="subscription_enterprise";
		}
		// System.out.println(ZendeskIds.contains(zendesk_id)&&
		// support.contains("Support/Enhanced (Monthly)"));

		String val = opline != null
				? ("Total Price:" + opline.getTotalPrice() + "Annual Revenue:" + revenue + "Support Hours:"
						+ support_Hours + "Account Status:" + accont_Status)
				: "";

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
		String startDate = o.getStart_Date__c() != null ? format.format(o.getStart_Date__c().getTime()) : "";
		String endDate = o.getEnd_Date__c() != null ? format.format(o.getEnd_Date__c().getTime()) : "";

		System.out.println("Account Id:" + o.getAccountId() + " " + "Account Name:" + o.getAccount().getName() + " "
				+ "Zendesk Id:" + zendesk_id + " " + "Start_Date:" + startDate + " " + "End_Date:" + endDate + " "
				+ subs + "," + support + "," + "Owener name:" + account_owner + " " + "field engineer:" + field_Engineer
				+ " " + "CSE NAme:" + cse_Name + " " + "CloseDate:" + o.getCloseDate().getTime() + " "
				+ "Customer Success Tier:" + val);

		return o.getAccount().getName() + "," + o.getAccountId() + "," + startDate + "," + endDate + "," + subs + ","
				+ support + "," + account_owner + "," + field_Engineer + "," + cse_Name + "," + revenue + ","
				+ support_Hours + "," + accont_Status + "," + "\n";

	}

}
