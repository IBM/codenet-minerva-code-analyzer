package configuration.variables.lite.rest.apis;

import java.io.IOException;

import jakarta.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import java.util.regex.Pattern;

import irwwbase.IRWWBase;
import irwwbase.POJOQualifier;

/**
 * Servlet implementation class ConfigurationVariablesServlet
 */

@WebServlet(name="ConfigurationVariablesServlet", value = "/configurationVariable/*")
public class ConfigurationVariablesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String className = (ConfigurationVariablesServlet.class).getName();
	
    private Pattern allConfigurationVariablesPattern = Pattern.compile("/WebConfigurationVariablesLite_REST_APIs/configurationVariable");
    private Pattern oneConfigurationVariablePattern = Pattern.compile("/WebConfigurationVariablesLite_REST_APIs/configurationVariable/.*");
	
	@Inject @POJOQualifier IRWWBase irwwbase;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfigurationVariablesServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		String uri = request.getRequestURI();
		String configurationVariable = null;
		irwwbase.debugOut("<<< " + className + ": uri = " + uri);
		irwwbase.debugOut("<<< " + className + ": oneConfigurationVariablePattern.matcher(uri) = " + oneConfigurationVariablePattern.matcher(uri));
		irwwbase.debugOut("<<< " + className + ": oneConfigurationVariablePattern.matcher(uri).matches() = " + oneConfigurationVariablePattern.matcher(uri).matches());
		
		if(oneConfigurationVariablePattern.matcher(uri).matches()){	
			irwwbase.debugOut("<<< " + className + ": oneConfigurationVariablePattern path ");
		   	if (uri != null){    	
		    	if(uri.contains("mqttHost")){
		    		configurationVariable = this.getMqttHost(response);
		    	}		    	
		    	if(uri.contains("mqttPort")){
		    		configurationVariable = this.getMqttPort(response);
		    	}		    	
		    	if(uri.contains("rtcommTopicPath")){
		    		configurationVariable = this.getRtcommTopicPath(response);
		    	} 		    	
				if(configurationVariable != null){
					try {
						response.setStatus(200);
						response.setContentType("text/plain");
						response.setContentLength(configurationVariable.length());
						response.getWriter().write(configurationVariable);
					} catch (Throwable e) {
						System.out.println("<<< " + className + ": Exception in doGet method where exception = " + e);
						response.setStatus(404);
						response.setContentLength(0);
						response.setContentType("text/plain");
					}
				} else {
					response.setStatus(404);
					response.setContentLength(0);
					response.setContentType("text/plain");
				}
	    	}
			return;
		}
		
		if(allConfigurationVariablesPattern.matcher(uri).matches()){
			irwwbase.debugOut("<<< " + className + ": allConfigurationVariablesPattern path ");
			String mqttHostValue = null;
			String mqttPortValue = null;
			String rtcommTopicPathValue = null;
			JSONObject jsonConfigurationVariable = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			
		   	if (uri != null){		   		
				response.setContentType("application/json");
				try {				
					mqttHostValue = this.getMqttHost(response);
					mqttPortValue = this.getMqttPort(response);
					rtcommTopicPathValue = this.getRtcommTopicPath(response);			
				
					if(mqttHostValue!=null){
						jsonConfigurationVariable.put("mqttHost", mqttHostValue);
					}
					if(mqttPortValue!=null){
						jsonConfigurationVariable.put("mqttPort", mqttPortValue);
					}
					if(rtcommTopicPathValue!=null){
						jsonConfigurationVariable.put("rtcommTopicPath", rtcommTopicPathValue);
					}
					jsonArray.add(jsonConfigurationVariable);
				} catch (Throwable e) {
					System.out.println("<<< " + className + ": Exception in doGet method where exception = " + e);
				}
				try {
					String json = jsonArray.serialize();
					response.setContentLength(json.length());
					response.getWriter().write(json);
				} catch (Throwable e) {
					System.out.println("<<< " + className + ": Exception in doGet method where exception = " + e);
					response.setStatus(404);
					response.setContentLength(0);
					response.setContentType("text/plain");
				}
		   	}
			return;
		}		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  	
		doGet(request, response);
	}
    
	public String getMqttHost(HttpServletResponse response) throws ServletException, IOException {		
		String mqttHostValue = (String) this.performJNDILookup("mqttHost", response);	
		irwwbase.debugOut("<<< " + className + ": mqttHostValue = "  + mqttHostValue);
		return mqttHostValue;
	}	
	
	public String getMqttPort(HttpServletResponse response) throws ServletException, IOException {
		Integer mqttPortValue = (Integer) this.performJNDILookup("mqttPort", response);
		irwwbase.debugOut("<<< " + className + ": mqttPortValue = "  + mqttPortValue);
		return Integer.toString(mqttPortValue);
	}
	
	public String getRtcommTopicPath(HttpServletResponse response) throws ServletException, IOException {
		String rtcommTopicPathValue = (String) this.performJNDILookup("rtcommTopicPath", response);	
		irwwbase.debugOut("<<< " + className + ": rtcommTopicPathValue = "  + rtcommTopicPathValue);
		return rtcommTopicPathValue;
	}
	
	public Object performJNDILookup(String jndiName, HttpServletResponse response) throws ServletException, IOException {
		Object jndiNameValue = null;
		
		try {
			irwwbase.debugOut("<<< " + className + ": Entering performJNDILookup >>");		
			jndiNameValue = new InitialContext().lookup(jndiName);
		} catch (NamingException e) {
            System.out.println("<<< " + className + ": Exception in performJNDILookup(): " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("<<< " + className + ": Exception in performJNDILookup(): " + e.getMessage());
			e.printStackTrace();
			throw new ServletException("<<< " + className + ": Exception in performJNDILookup()", e);
		}
		
		return jndiNameValue;
	}	

}
