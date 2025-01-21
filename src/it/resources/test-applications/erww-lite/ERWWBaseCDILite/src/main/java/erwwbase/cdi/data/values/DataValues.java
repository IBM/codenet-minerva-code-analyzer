package erwwbase.cdi.data.values;

public class DataValues implements java.io.Serializable {
	
	private static final long serialVersionUID = 1441301208389636626L;
	
	public static String[] STATES = {"AK", "AL", "AR", "AZ", "CA", "CO", 
		"CT", "DC", "DE", "FL", "GA", "HI", "IA", "ID", "IL", "IN", "KS", "KY", 
		"LA", "MA", "MD", "ME", "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE", 
		"NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "RI", "SC", "SD", 
		"TN", "TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY"};
	
	public static String[] CITIES = {"Juneau", "Montgomery", "Little Rock", "Phoenix", "Sacramento", "Denver", 
			"Hartford", "Washington", "Dover", "Tallahassee", "Atlanta", "Honolulu", "Des Moines", "Boise", "Springfield", "Indianapolis", "Topeka", "Frankfort", 
			"Baton Rouge", "Boston", "Annapolis", "Augusta", "Lansing", "Saint Paul", "Jefferson City", "Jackson", "Helena", "Raleigh", "Bismarck", "Lincoln", 
			"Concord", "Trenton", "Santa Fe", "Carson City", "Albany", "Columbus", "Oklahoma City", "Salem", "Harrisburg", "Providence", "Columbia", "Pierre", 
			"Nashville", "Austin", "Salt Lake City", "Richmond", "Montpelier", "Olympia", "Madison", "Charleston", "Cheyenne"};

	public static String[] ORDER_TRACKING_ACTIVITIES = {"No Order Tracking Information", "Arrived at Delivery Location",
			"Left Delivery Location","Delivered"};
}
