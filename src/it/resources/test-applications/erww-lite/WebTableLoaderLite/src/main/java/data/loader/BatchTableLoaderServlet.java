package data.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.BatchRuntime;
import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;
import jakarta.batch.runtime.JobInstance;
import jakarta.batch.runtime.Metric;
import jakarta.batch.runtime.StepExecution;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BatchTableLoaderServlet
 */
@WebServlet("/joboperator")
public class BatchTableLoaderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected final static Logger logger = Logger.getLogger(BatchTableLoaderServlet.class.getName());

    /**
     * For writing responses.
     */
    private TextWriter responseWriter ;

    /**
     * The batch JobOperator
     */
    private JobOperator jobOperator;

    /**
     * Logging helper.
     */
    protected static void log(String method, Object msg) {
        System.out.println("BatchTableLoaderServlet: " + method + ": " + String.valueOf(msg));
    }
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BatchTableLoaderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		log("doGet", "URL: " + request.getRequestURL() + "?" + request.getQueryString());
		
		setResponseWriter(request.getHeader("Accept"));

		String action = request.getParameter("action");
		
		if ("start".equalsIgnoreCase(action)) {
            start(request, response);
		} else if ("status".equalsIgnoreCase(action)) {
            status(request, response);
        } else if ("stop".equalsIgnoreCase(action)) {
            stop(request, response);
        } else if ("abandon".equalsIgnoreCase(action)) {
            abandon(request, response);
        } else if ("getJobExecution".equalsIgnoreCase(action)) {
            getJobExecution(request, response);
        } else if ("getJobExecutions".equalsIgnoreCase(action)) {
            getJobExecutions(request, response);
        } else if ("getJobInstance".equalsIgnoreCase(action)) {
            getJobInstance(request, response);
        } else if ("getJobInstanceCount".equalsIgnoreCase(action)) {
            getJobInstanceCount(request, response);
        } else if ("getJobInstances".equalsIgnoreCase(action)) {
            getJobInstances(request, response);
        } else if ("getJobNames".equalsIgnoreCase(action)) {
            getJobNames(request, response);
        } else if ("getParameters".equalsIgnoreCase(action)) {
            getParameters(request, response);
        } else if ("getRunningExecutions".equalsIgnoreCase(action)) {
            getRunningExecutions(request, response);
        } else if ("getStepExecutions".equalsIgnoreCase(action)) {
            getStepExecutions(request, response);
        } else if ("restart".equalsIgnoreCase(action)) {
            restart(request, response);
        } else if ("help".equalsIgnoreCase(action) || StringUtils.isEmpty(action)) {
            help(request, response);
        } else {
            throw new IOException("action not recognized: " + action );
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	/**
     * @return the response writer.
     */
    protected TextWriter setResponseWriter(String acceptHeader) {
        if (StringUtils.isEmpty(acceptHeader)) {
            return (responseWriter = new TextWriter());
        } else if ( acceptHeader.contains("text/html") ) {
            return (responseWriter = new HtmlWriter());
        } else {
            return (responseWriter = new TextWriter());
        }
    }
    
    /**
     * @return the response writer.
     */
    protected TextWriter getResponseWriter() {
        return (responseWriter != null) ? responseWriter : (responseWriter = new TextWriter());
    }
    
    /**
     * @return the batch JobOperator
     */
    protected JobOperator getJobOperator() {
        return (jobOperator != null) ? jobOperator : (jobOperator = BatchRuntime.getJobOperator());
    }
    
    /**
     * @return the value for the given query parm as a long.
     *
     * @throws IllegalArgumentException if parm is not specified
     */
    protected long getLongParm(HttpServletRequest request, String parmName) throws IOException {
        return Long.parseLong( getRequiredParm(request, parmName) );
    }

    /**
     * @param queryParmName The parm name
     *
     * @return the parm value
     *
     * @throws IllegalArgumentException if parm is not specified
     */
    protected String getRequiredParm(HttpServletRequest request, String queryParmName) throws IOException {
        String queryParmValue = request.getParameter(queryParmName);
        if ( StringUtils.isEmpty(queryParmValue) ) {
            throw new IllegalArgumentException("ERROR: " + queryParmName + " is a required parameter" );
        }
        return queryParmValue;
    }
    
    /**
     * Parse job parameters from the request's query parms.
     *
     * JobParameters are specified like so:
     * /joboperatorservlet?action=start&jobXMLName=sleepy-batchlet&jobParameters={name}={value}&jobParameters={name}={value}&...
     *
     * @param queryParmName The name of the query parm containing job params ("jobParameters" or "restartParameters")
     *
     * @return the given query parms as a Properties object, or null if no parms were specified.
     */
    protected Properties getJobParameters(HttpServletRequest request, String queryParmName) throws IOException {
        String[] jobParameters = request.getParameterValues(queryParmName);
        if (jobParameters == null ) {
            return null;
        }

        Properties retMe = new Properties();

        for (String jobParameter : jobParameters) {
            log("getJobParameters", "jobParameter: " + jobParameter);
            String[] keyValue = jobParameter.split("=");
            retMe.setProperty(keyValue[0], (keyValue.length >= 2) ? keyValue[1] : null);
        }

        log("getJobParameters", "retMe: " + retMe);
        return retMe;
    }
    
    /**
     * @response help text - a list of actions understood by the servlet along with examples.
     */
    protected void help(HttpServletRequest request, HttpServletResponse response) throws IOException {

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "help:" )
                           .println( "joboperator?action=help" )
                           .println( "joboperator?action=start&jobXMLName={jobXMLName}&jobParameters={name=value}&jobParameters={name=value}..." )
                           .println( "joboperator?action=restart&executionId={execId}&restartParameters={name=value}&restartParameters={name=value}..." )
                           .println( "joboperator?action=stop&executionId={execId}" )
                           .println( "joboperator?action=abandon&executionId={execId}" )
                           .println( "joboperator?action=status&executionId={execId}" )
                           .println( "joboperator?action=getJobExecution&executionId={execId}" )
                           .println( "joboperator?action=getJobExecutions&instanceId={instanceId}" )
                           .println( "joboperator?action=getJobInstance&executionId={execId}" )
                           .println( "joboperator?action=getJobInstanceCount&jobName={jobName}" )
                           .println( "joboperator?action=getJobInstances&jobName={jobName}&start={start}&count={count}" )
                           .println( "joboperator?action=getJobNames" )
                           .println( "joboperator?action=getParameters&executionId={execId}" )
                           .println( "joboperator?action=getRunningExecutions&jobName={jobName}" )
                           .println( "joboperator?action=getStepExecutions&executionId={execId}" )
                           .endResponse();
    }

    
    /**
     * Start the job.
     *
     * @response the JobInstance + JobExecution record of the newly started job.
     */
    protected void start(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String jobXMLName = getRequiredParm(request, "jobXMLName");
        Properties jobParameters = getJobParameters(request, "jobParameters");

        JobOperator jobOperator = getJobOperator();
        long execId = jobOperator.start(jobXMLName, jobParameters);

        JobInstance jobInstance = jobOperator.getJobInstance(execId);
        JobExecution jobExecution = jobOperator.getJobExecution(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "start(jobXMLName=" + jobXMLName + ", jobParameters=" + jobParameters + "): Job started!" )
                           .printJobInstance( jobInstance )
                           .printJobExecution( jobExecution )
                           .endResponse();
    }
    
    /**
     * Get status for the job identified by the 'executionId' query parm.
     * 
     * @response instance + execs
     */
    protected void status(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");

        JobOperator jobOperator = getJobOperator();
        JobInstance jobInstance = jobOperator.getJobInstance(execId);
        List<JobExecution> jobExecutions = jobOperator.getJobExecutions(jobInstance);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "status(executionId=" + execId + "): ")
                           .printJobInstance( jobInstance )
                           .printJobExecutions( jobExecutions )
                           .endResponse();
    }

    /**
     * Stop the job identified by the 'executionId' query parm.
     *
     * @response the job status (instance + execs)
     */
    protected void stop(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");

        getJobOperator().stop(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "stop(executionId=" + execId + "): Stop request submitted!");

        status(request, response);
    }

    /**
     * Abandon the job identified by the 'executionId' query parm.
     *
     * @response the job status (instance + execs)
     */
    protected void abandon(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");
        getJobOperator().abandon(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "abandon(executionId=" + execId + "): Abandon request submitted!");

        status(request, response);
    }

    /**
     * Restart the job identified by the 'executionId' query parm.
     *
     * @response the job status (instance + execs)
     */
    protected void restart(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");
        Properties restartParameters =  getJobParameters(request, "restartParameters");

        long newExecId = getJobOperator().restart(execId,restartParameters);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "restart(executionId=" + execId + ", restartParameters=" + restartParameters + "): Job restarted!");

        status(request, response);
    }

    /**
     * @response the jobexecution record
     */
    protected void getJobExecution(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");
        JobExecution jobExecution = getJobOperator().getJobExecution(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println("getJobExecution(executionId=" + execId + "): ")
                           .printJobExecution( jobExecution )
                           .endResponse();
    }

    /**
     * @response the jobexecution records
     */
    protected void getJobExecutions(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final long instanceId = getLongParm(request, "instanceId") ;

        List<JobExecution> jobExecutions = getJobOperator().getJobExecutions( new JobInstance() {
            public long getInstanceId() { return instanceId; }
            public String getJobName() { return "dummy-for-call-to-getJobExecutions"; }
        });

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "getJobExecutions(instanceId=" + instanceId + "): ")
                           .printJobExecutions(jobExecutions)
                           .endResponse();
    }

    /**
     * @response the count
     */
    protected void getJobInstanceCount(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String jobName = getRequiredParm(request, "jobName") ;

        int count = getJobOperator().getJobInstanceCount( jobName );

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "getJobInstanceCount(jobName=" + jobName+ "): " + count)
                           .endResponse();
    }

    /**
     * @response the job instance record
     */
    protected void getJobInstance(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long execId = getLongParm(request, "executionId");

        JobInstance jobInstance = getJobOperator().getJobInstance(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "getJobInstance(executionId=" + execId + "): " )
                           .printJobInstance( jobInstance )
                           .endResponse();
    }

    /**
     * @response a list of jobinstance records
     */
    protected void getJobInstances(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String jobName = getRequiredParm(request, "jobName") ;
        int start = (int) getLongParm(request, "start");
        int count = (int) getLongParm(request, "count");

        List<JobInstance> jobInstances = getJobOperator().getJobInstances( jobName, start, count );

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "getJobInstances(jobName=" + jobName + ", start=" + start + ", count=" + count + "): " )
                           .printJobInstances( jobInstances ) 
                           .endResponse();
    }

    /**
     * @response a list of job names
     */
    protected void getJobNames(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Set<String> jobNames = getJobOperator().getJobNames();

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "getJobNames(): ")
                           .printJobNames( jobNames )
                           .endResponse();
    }

    /**
     * @response list of properties
     */
    protected void getParameters(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");

        Properties props = getJobOperator().getParameters(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "getParameters(executionId=" + execId + "): ")
                           .printProps( props )
                           .endResponse();
    }

    /**
     * @response a list of jobexecution records
     */
    protected void getRunningExecutions(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String jobName = getRequiredParm(request, "jobName") ;
        List<Long> execIds = getJobOperator().getRunningExecutions(jobName);

        List<JobExecution> jobExecutions = new ArrayList<JobExecution>();

        for (Long execId : execIds) {
            jobExecutions.add( getJobOperator().getJobExecution( execId ) );
        }

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "getRunningExecutions(jobName=" + jobName + "): ")
                           .printJobExecutions(jobExecutions)
                           .endResponse();
    }

    /**
     * @response list of stepexecution records
     */
    protected void getStepExecutions(HttpServletRequest request, HttpServletResponse response) throws IOException {

        long execId = getLongParm(request, "executionId");

        List<StepExecution> stepExecutions = getJobOperator().getStepExecutions(execId);

        getResponseWriter().setHttpServletResponse( response )
                           .beginResponse(HttpServletResponse.SC_OK)
                           .println( "getStepExecutions(executionId=" + execId + "): ")
                           .printStepExecutions(stepExecutions);
    }

}


/**
 * Helper class.
 */
class StringUtils {

    /**
     * @return true if the string is null or "" or only whitespace
     */
    public static boolean isEmpty(String str) {
        return (str == null) || str.trim().length() == 0;
    }

    /**
     * @return "\"" + s + "\"";
     */
    public static String enquote(String s) {
        return "\"" + s + "\"";
    }

    /**
     * @return All strings in the collection joined with the given delim.
     */
    public static String join(Collection<String> strs, String delim) {
        StringBuilder sb = new StringBuilder();
        String d = "";
        for (String str : ( (strs != null) ? strs : new ArrayList<String>() ) ) {
            sb.append(d).append(str);
            d = delim;
        }
        return sb.toString();
    }
}


/**
 * Batch-related utilities.
 */
class BatchUtils {

    public static boolean isRunning(BatchStatus batchStatus) {
        switch (batchStatus) {
            case STARTED:
            case STARTING:
                return true;
            //added a default case to remove the warning about all
            //statuses not being represented
		    default:
		    	break;
        }
        return false;
    }

}


/**
 * Text response writer.
 */
class TextWriter {

    private HttpServletResponse httpServletResponse;

    public TextWriter setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
        return this;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public TextWriter beginResponse(int httpStatusCode) throws IOException {
        getHttpServletResponse().setStatus(httpStatusCode);
        getHttpServletResponse().setContentType("text/plain");
        return this;
    }

    public TextWriter endResponse() throws IOException {
        return this;
    }

    public TextWriter print(String s) throws IOException {
        getHttpServletResponse().getWriter().print(s);
        return this;
    }

    public TextWriter println(String line) throws IOException {
        return print(line).printNewLine();
    }

    public TextWriter printNewLine() throws IOException {
        getHttpServletResponse().getWriter().println("");
        return this;
    }

    public TextWriter printJobInstance(JobInstance jobInstance) throws IOException {
        return println( "JobInstance: " + jobInstanceToString(jobInstance) );
    }

    public TextWriter printJobInstances(Collection<JobInstance> jobInstances) throws IOException {
        for (JobInstance jobInstance : jobInstances) {
            printJobInstance( jobInstance );
        }
        return this;
    }

    public TextWriter printJobExecution(JobExecution jobExecution) throws IOException {
        return println( "JobExecution: " + jobExecutionToString(jobExecution) );
    }

    public TextWriter printJobExecutions(Collection<JobExecution> jobExecutions) throws IOException {
        for (JobExecution jobExecution : jobExecutions) {
            printJobExecution( jobExecution );
        }
        return this;
    }

    public TextWriter printProps(Properties props) throws IOException {
        if (props == null) {
            return this;
        }

        for ( Enumeration<?> propNames = props.propertyNames(); propNames.hasMoreElements(); ) {
            String propName = (String) propNames.nextElement();
            println(propName + "=" + props.getProperty(propName));
        }

        return this;
    }

    public TextWriter printJobName(String jobName) throws IOException {
        return println("JobName: " + jobName);
    }

    public TextWriter printJobNames(Set<String> jobNames) throws IOException  {
        for ( String jobName : jobNames ) {
            printJobName(jobName);
        }
        return this;
    }

    /**
     * @return a stringified version of the jobInstance record.
     */
    public String jobInstanceToString(JobInstance jobInstance) {
        return "instanceId=" + jobInstance.getInstanceId() 
                + ", jobName=" + jobInstance.getJobName();
    }

    /**
     * @return a stringified version of the jobExecution record.
     */
    public String jobExecutionToString(JobExecution jobExecution) {
        return "executionId=" + jobExecution.getExecutionId() 
                + ", jobName=" + jobExecution.getJobName()
                + ", batchStatus=" + jobExecution.getBatchStatus()
                + ", createTime=" + jobExecution.getCreateTime()
                + ", startTime=" + jobExecution.getStartTime()
                + ", endTime=" + jobExecution.getEndTime()
                + ", lastUpdatedTime=" + jobExecution.getLastUpdatedTime()
                + ", jobParameters=" + jobExecution.getJobParameters();
    }

    public TextWriter printStepExecution(StepExecution stepExecution) throws IOException {
        return println( "StepExecution: " + stepExecutionToString(stepExecution) );
    }

    public TextWriter printStepExecutions(Collection<StepExecution> stepExecutions) throws IOException {
        for (StepExecution stepExecution : stepExecutions) {
            printStepExecution( stepExecution );
        }
        return this;
    }

    /**
     * @return a stringified version of the stepExecution record
     */
    public String stepExecutionToString(StepExecution stepExecution) {
        return "stepExecutionID="+stepExecution.getStepExecutionId()
    			+ ", stepName="+stepExecution.getStepName()
    			+ ", exitStatus="+ stepExecution.getExitStatus()
    			+ ", batchStatus="+stepExecution.getBatchStatus()
    			+ ", startTime="+stepExecution.getStartTime()
    			+ ", endTime=" +stepExecution.getEndTime()
                + ", metrics=" + metricsToString(stepExecution.getMetrics());
    }

    /**
     * @return a stringified version of the StepExecution metrics
     */
    public String metricsToString(Metric[] metrics) {
        List<String> metricStrings = new ArrayList<String>();
        for (Metric metric: metrics) {
            metricStrings.add( metric.getType() + "=" + metric.getValue() );
        }
        return "{" + StringUtils.join(metricStrings, ", ") + "}";
    }
}

/**
 * HTML response writer.
 */
class HtmlWriter extends TextWriter {

    public TextWriter beginResponse(int httpStatusCode) throws IOException {
        getHttpServletResponse().setStatus(httpStatusCode);
        getHttpServletResponse().setContentType("text/html");

        return print("<html>").print("<body>");
    }

    public TextWriter endResponse() throws IOException {
        return print("</body>").print("</html>");
    }

    public TextWriter printNewLine() throws IOException {
        return print("<br>");
    }

    /**
     * Print jobName and link for getJobInstances(jobName)
     */
    public TextWriter printJobName(String jobName) throws IOException  {
        print("JobName: " + jobName);
        print(", <a href=" + StringUtils.enquote("joboperator?action=getJobInstances&jobName=" + jobName + "&start=0&count=100") + ">"
                + "getJobInstances(jobName=" + jobName + ", start=0, count=100)"
                + "</a>");   		
        print(", <a href=" + StringUtils.enquote("joboperator?action=getJobInstanceCount&jobName=" + jobName ) + ">"
                + "getJobInstanceCount(jobName=" + jobName + ")"
                + "</a>");   		
        print(", <a href=" + StringUtils.enquote("joboperator?action=getRunningExecutions&jobName=" + jobName ) + ">"
                + "getRunningExecutions(jobName=" + jobName + ")"
                + "</a>");   		
        return println("");
    }

    /**
     * Print JobInstance and links for getJobExecutions(instanceId)
     */
    public TextWriter printJobInstance(JobInstance jobInstance) throws IOException {
        print( "JobInstance: " + jobInstanceToString(jobInstance) );
        print(", <a href=" + StringUtils.enquote("joboperator?action=getJobExecutions&instanceId=" + jobInstance.getInstanceId() ) + ">"
                      + "getJobExecutions(instanceId=" + jobInstance.getInstanceId() + ")"
                      + "</a>");   		
        return println("");
    }

    /**
     * Print JobExecution and associated links 
     */
    public TextWriter printJobExecution(JobExecution jobExecution) throws IOException {
        print( "JobExecution: " + jobExecutionToString(jobExecution) );
        print(", <a href=" + StringUtils.enquote("joboperator?action=getJobInstance&executionId=" + jobExecution.getExecutionId() ) + ">"
                      + "getJobInstance(executionId=" + jobExecution.getExecutionId() + ")"
                      + "</a>");   		
        print(", <a href=" + StringUtils.enquote("joboperator?action=getParameters&executionId=" + jobExecution.getExecutionId() ) + ">"
                      + "getParameters(executionId=" + jobExecution.getExecutionId() + ")"
                      + "</a>");   		
        print(", <a href=" + StringUtils.enquote("joboperator?action=getStepExecutions&executionId=" + jobExecution.getExecutionId() ) + ">"
                      + "getStepExecutions(executionId=" + jobExecution.getExecutionId() + ")"
                      + "</a>");   		

        if (BatchUtils.isRunning( jobExecution.getBatchStatus() ) ) {
            print(", <a href=" + StringUtils.enquote("joboperator?action=stop&executionId=" + jobExecution.getExecutionId() ) + ">"
                          + "stop(executionId=" + jobExecution.getExecutionId() + ")"
                          + "</a>");   		
        }

        return println("");
    }

}
