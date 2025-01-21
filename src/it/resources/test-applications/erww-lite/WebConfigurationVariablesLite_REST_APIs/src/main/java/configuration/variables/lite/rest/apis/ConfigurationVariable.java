package configuration.variables.lite.rest.apis;


import com.ibm.json.java.JSONObject;

public class ConfigurationVariable {
	private String name;
	private String value;
	
	public ConfigurationVariable(JSONObject jsonConfigurationVariable){
		setName((String)jsonConfigurationVariable.get(("name")));
		setValue((String)jsonConfigurationVariable.get("value"));
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public JSONObject getJsonObject(){
		JSONObject jsonConfigurationVariable = new JSONObject();
		if(name!=null)
			jsonConfigurationVariable.put("name", name);
		if(value!=null)
			jsonConfigurationVariable.put("value", value);
		
		return jsonConfigurationVariable;
	}
	
}
