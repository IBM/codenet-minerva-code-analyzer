Change history for ERWW_Lite application JEE ear file
------------------------------------------------------

Date       Version   Fix(es)                                                                     Developer
--------   -------   ------------------------------------------------------------------------    ------------
08/08/13    1.10     106845: Consistency Check fails on CC 9 for JPA but Okay for JDBC           Frank Niu
08/19/13    1.11     Redo fix 1.10 (the fix did not get included correctly before)               Frank Niu
08/26/13    1.12     109143: JMS Price Change transaction is not setting correlation ID          Mike Shupert
                              for error responses from MDB
10/04/13    1.13     112223: Fixed two error handling bugs in JMSDeliveryMDB, Improved           Mike Shupert
                             LogOff Transaction, added TinyDB2.jar for DB/2 error
                             classes, updated DELogin.jsp to not join session, fixed
                             WebCustomerSalesQuoteLite problem with security RunAsRole,
                             and fixed output of WebSessionClientLite1 & 2 to use displayOut.
10/09/13    1.14     113739: Fixed problem in WebLogOffLite that was breaking JIBE (caused       Mike Shupert       
                             by 112223 change above).  Also set session sharing to true
                             now in ibm-application-ext.xml file since Liberty finally
                             supports this.
10/15/13    1.15     114013: Fixed problem in PriceChangeSessionEJB where PRICECHANGER role      Mike Shupert
                             was not declared.  Also, fixed problem in WebStatefulEJBLite
                             where printStackTrace was included in expected error path.                            
10/21/13    1.16     109432: Updated consistency check 2 to display a warning if no new orders   Ethan Fritz
                             are found during the check. This is caused by imbalanced workload
                             transactions. Included a .classpath fix to remove old DB2 jar.
12/18/13    1.17     119526: Added EJB 3.2 Non-persistent timers for Concord.                    Mike Shupert 
                     120822: Also, did some eclipse warning message cleanup work.                Mike Shupert             
01/08/14    1.18      91169: Adds CDI style injection (eg @Inject) into a variety of projects    Ethan Fritz
02/05/14    1.19     104825: Fixed calculation of max OrderID in OS transactions and corrected   Ethan Fritz
                             opening and closing of PreparedStatements and ResultSets.
03/19/14    1.20     115154: ERWW-Lite source code for MLT_DeveloperExperience_CDI               Mary Ellen Kerr 
                             (Specification Services): WebNewOrderLite_JSF_CDI.   
                             WebPaymentLite: shortened length of customer data.                            
05/05/14    1.21      99636: Update JMS transactions for common configuration across platforms   Mike Shupert 
07/04/14    2Q15.22  103251: Added JMS 2.0 Simplified API, NewOrderJMSLite transaction           Mike Shupert 
                       none: Split ear projects into unique 855x, 4Q14, and 2Q15 versions        Mike Shupert
10/23/14             150195: Short-term Solution: Provide jvm property WEBSOCKETS = ON to        Mary Ellen Kerr 
                             enable WebNewOrderLite and WebPaymentLite to send a JMS message 
                             used by WebSockets 
11/07/14    4Q14.25          Added 2nd path (Business Logic in Listeners) to                     Mary Ellen Kerr 
                             WebCatalogUpdateLite
11/11/14    4Q14.26  153134: Added 3nd path (Business Logic: None) to WebCatalogUpdateLite       Mary Ellen Kerr
11/12/14    4Q14.27  153007: Fixed Websockets monitor problem with JMS send                      Mike Shupert
11/16/14    4Q14.28  153615: Added WebImageDeleteLite       
11/15/14    4Q14.29  153592: Thread safe data structures used in Websockets, improved failure    Ethan Fritz
                             message and removed dojo for client performance boost.
02/10/15    1Q15.30  162611: Added WebMonitorTransactionsLite_WebSocket1.1 resolved the 150195   Ethan Fritz
                             issue and removed the WEBSOCKET jvm.options flag.
02/10/15    1Q15.31  162689: Added the WebOrdersQueryLite_JDBC_EL and                            Mary Ellen Kerr
                             WebOrderArchiveLite_JDBC_EL to test JDBC 4.1, Java 8 and EL 3.0
02/16/15    2Q15.01  160603: Updated WebCentralLite GUI to group transaction types by color.     Mike Shupert
                             Added JMS 2.0 simplified API transaction WebNewOrderJMSLite. 
04/27/15    2Q15.02  171538: Added EJB 3.2 Asynchronous method processing in the                 Mike Shupert 
                             WebDeliveryLite_AsyncSyncEJB web application.
                             Updated WebPriceQuoteLite to include a qualifier injecting a
                             POJO into a Tag Handler class.
                             Turn off session context sharing.
04/30/15    2Q15.03  172542: Added JPA-2.1 Entity Graphs in WebDeliveryLite_EntityGraphs         Greg Ecock
                             Made JPA 2.1 related changes in the JPA projects.
05/08/15    2Q15.04  172483: Added CDI 1.2, JTA 1.2, BeanValidation 1​.1 (Servlet & EJB Paths)    Mary Ellen Kerr
                             in WebCustomerCareLite_CDI_BeanVal_JTA.
05/15/15    2Q15.05  174153: Added a new consistency check 13 (JDBC and JPA versions)            Mary Ellen Kerr                                 
06/02/15    2Q15.06  174360: Added Bean Validation 1.1 (Servlet & EJB Paths)                     Mary Ellen Kerr
06/25/15    2Q15.07  173311: Added EJB 3.2 persistent timers and cancel timers                   Mike Shupert & Greg Ecock
08/07/15    3Q15.01  179597: Added WebRTC 1.0 VideoChat, AudioOnly, Chat/Messaging               Mary Ellen Kerr
10/21/15    3Q15.02  188339: Enhanced WebRTC: More exception handling; Presence with refresh     Mary Ellen Kerr
                             button/presenceNode approach (interim approach); Prompt user for
                             MQTT host/port                             
02/09/16    3Q15.03  188339: WebRTC Bluemix: ERWW_LiteDataLoader & WebRTC changes                Mary Ellen Kerr 
                             (added Presence: event driven)                
02/09/16    1Q16.01  201124: REST API Discovery: WebRTC & CustomerCredit: swagger.json           Mary Ellen Kerr
02/25/16    1Q16.02  201124: REST API Discovery: WebRTC & CustomerCredit: swagger.yaml;          Mary Ellen Kerr
                             WebCatalogUpdateLite & WebImageDeleteLite WriteListener fix
07/26/16	3Q16.01	 220587: Added WebOrderArchiveLite_SQLJ										 Andy Guibert                             
08/01/16    2Q16.01  221430: Servlet 3.1 UpgradeRequest: WebProductCatalogLite                   Mary Ellen Kerr
08/03/16    2Q16.01  221752: Backport ERWW-Lite changes needed for tWAS v9 to Libery version     Mary Ellen Kerr
08/06/16	3Q16.01  222182: Add 3Q16 Enterprise Application project & SQLJ Button               Greg Ecock                                                             