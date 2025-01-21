package monitor;

import irwwbase.IRWWBase;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

//decoders = {monitor.VerificationMessageTextDecoder.class}
@ServerEndpoint(value = "/monitor",
encoders = {monitor.InitMessageTextEncoder.class} )
public class SimpleMonitorEndpoint extends IRWWBase {

	private static final long serialVersionUID = 2490815645611722435L;

	private Session currentSession;
	
	private static List<Double> warehouseSales = Collections.synchronizedList(new ArrayList<Double>());
	private static List<Double> neworders = Collections.synchronizedList(new ArrayList<Double>());
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<Session> verificationPeers = Collections.synchronizedSet(new HashSet<Session>());
	
	private static Thread generatePaymentTransactionsThread;
	final static boolean USING_THREAD = false;
	
	private static boolean verify = false;
	private static AtomicInteger messageCount = new AtomicInteger(0);
	private static ConcurrentHashMap<Integer, VerificationMessage> mapping;
	private static Object messageLock = new Object();
	
	static {
		for (int i = 0; i < 25; i++) {
			warehouseSales.add(Double.valueOf(0));
			neworders.add(Double.valueOf(0));
		}
		
		if (USING_THREAD) {
			generatePaymentTransactionsThread=new Thread(){
				public void run() {
					try{
						Random random = new Random();
						while(true) {
							int warehouseId = random.nextInt(25)+1;
							double warehouseSale = random.nextDouble()*2000;
							sendPaymentTransaction(warehouseId, warehouseSale);
							
							try {
								int timeToSleep = random.nextInt(1000)+10;
								sleep(timeToSleep);
							} catch (InterruptedException e) {
								// do nothing
							}
						}
					} catch (Throwable t) {
						t.printStackTrace();
						// continue the loop
					}
				};
			};
			generatePaymentTransactionsThread.start();
		}
	}
	
	@OnOpen
	public void open(Session session) {
		
		debugOut("< MonitorEndpoint @OnOpen open() >");
		
		sendInitMessage(session);
		peers.add(session);
		currentSession = session;
	}
	
	@OnClose
	public void close(Session session, CloseReason reason) {
		
		debugOut("< MonitorEndpoint @OnClose close() >");
		debugOut("<< CloseReason message: " + reason.getReasonPhrase());
		
		try {
			peers.remove(session);
            if (session != null) {
                session.close();
            }
            currentSession = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	@OnError
	public void error(Session session, Throwable t) {
		
		debugOut("< MonitorEndpoint @OnError error() >");
		
		try {
			peers.remove(session);
            if (session != null) {
                session.close();
            }
            currentSession = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		// Print information about the error.
		t.printStackTrace();
	}
	
	@OnMessage
	public void onMessage(Session session, String msg) {
		
		debugOut("< MonitorEndpoint @OnMessage onMessage() >");
		
		//String msg = new String(byteMsg);
		
		JSONObject json = new JSONObject();
		try {
			json = JSONObject.parse(msg);
		} catch (IOException e) {
			//e.printStackTrace();
			return;
		}
		
		debugOut("<< onMessage() : type = " + json.get("type") + " >>");
		
		/* No longer used, but this was used to test websockets before JMS messaging was integrated.
		 * if ("neworder".equals(json.get("type"))) {
		 *	sendNewOrderTransaction((int)(long)json.get("outWarehouseId"), (double)(long)json.get("outTotal"));
		}*/
		if ("verify_begin".equals(json.get("type"))) {
			this.runVerification(session);
			this.requestClientVerificationData(session);
		} else if ("verify_data".equals(json.get("type"))) {
			debugOut("<< onMessage() : message_count = " + String.valueOf(json.get("message_count")) + " >>");
			debugOut("<< onMessage() : warehouse_sales = " + String.valueOf(json.get("warehouse_sales")) + " >>");
			debugOut("<< onMessage() : neworders = " + String.valueOf(json.get("neworders")) + " >>");
			
			int clientCount = (int) (long) json.get("message_count");
			List<Double> ws = new ArrayList<Double>();
			List<Double> no = new ArrayList<Double>(); 
			
			JSONArray jsonArray1 = (JSONArray)json.get("warehouse_sales"); 
			if (jsonArray1 != null) { 
			   int len = jsonArray1.size();
			   for (int i=0;i<len;i++){ 
			    ws.add(Double.valueOf(jsonArray1.get(i).toString()));
			   } 
			}
			
			JSONArray jsonArray2 = (JSONArray)json.get("neworders"); 
			if (jsonArray2 != null) { 
			   int len = jsonArray2.size();
			   for (int i=0;i<len;i++){ 
			    no.add(Double.valueOf(jsonArray2.get(i).toString()));
			   }
			}
			
			VerificationMessage verify_msg = new VerificationMessage(ws,no,clientCount);
			this.sendVerificationResult(session, verify_msg);
		}
	}
	
	private void runVerification(Session clientSession) {
		
		debugOut("< MonitorEndpoint runVerification() >");
		
		// Client asked us to begin verification
		if (verificationPeers.isEmpty() == true || mapping == null) {
			//No verifications are being done yet so we begin keeping track of message count and data
			verify = true;
			mapping = new ConcurrentHashMap<Integer, VerificationMessage>();
		}
		verificationPeers.add(clientSession);
		
		int newCount;
		ArrayList<Double> newWS = null;
		ArrayList<Double> newNO = null;
		synchronized (messageLock) {
			newCount = messageCount.get();
			newWS = new ArrayList<Double>(warehouseSales);
			newNO = new ArrayList<Double>(neworders);
		}
		if ((verify == true) && (newWS != null) && (newNO != null)) {
			VerificationMessage newVerifyPoint = new VerificationMessage(newWS, newNO, newCount);
			mapping.put(Integer.valueOf(messageCount.get()), newVerifyPoint);
		}
	}
	
	private void requestClientVerificationData(Session clientSession) {
		
		debugOut("< MonitorEndpoint requestClientVerificationData() >");
		JSONObject json = new JSONObject();
		String msg = null;
		json.put("type", "verify_request");
		
		try {
			msg = json.serialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.sendInstanceTextUpdate(clientSession, msg);
	}
	
	private void sendVerificationResult(Session clientSession, VerificationMessage clientVerification) {
		
		debugOut("< MonitorEndpoint sendVerificationResult() >");
		try {
			VerificationMessage serverVerification = mapping.get(Integer.valueOf(clientVerification.getClientCount()));
			if ((serverVerification != null) && (clientVerification != null)) {
				
				if (clientVerification.compareTo(serverVerification) == true) {
					//Send a message to client that verification succeeded
					String resultMessage = "Successful! Message Count: " + String.valueOf(clientVerification.getClientCount());
					String resultJsonString = this.makeJsonStringVerification("verify_result", resultMessage);
					this.sendInstanceTextUpdate(clientSession, resultJsonString);
				} else {
					//Send a message to client that verification failed
					String resultMessage = "Failed! Server and client data do not match:\n "
							+ "Message Count: " + String.valueOf(clientVerification.getClientCount())
							+ "\nServer Data\n"
							+ "Warehouse Sales: " + String.valueOf(serverVerification.getWarehouseSales())
							+ "New Orders: " + String.valueOf(serverVerification.getNewOrders())
							+ "\nClient Data\n"
							+ "Warehouse Sales: " + String.valueOf(clientVerification.getWarehouseSales())
							+ "New Orders: " + String.valueOf(clientVerification.getNewOrders());
					String resultJsonString = this.makeJsonStringVerification("verify_result", resultMessage);
					this.sendInstanceTextUpdate(clientSession, resultJsonString);
				}
			} else {
				
				//verification failed due to invalid client or server VerificationMessage
				String clientAndMessageCountInfo = "";
				String serverAndMessageCountInfo = "";
				if (clientVerification == null) {
					clientAndMessageCountInfo = "The client verification object is null. No message count is given.";
				} else {
					try {
						clientAndMessageCountInfo = "The client attempted to compare using message count = " + clientVerification.getClientCount() + ".";
					} catch (Exception e) {
						clientAndMessageCountInfo = "The client verification object could not collect message count because of an exception " + e.getMessage() + ".";
					}
				}
				
				if (serverVerification == null) {
					serverAndMessageCountInfo = "The server verification object is null. No message count is given.";
				} else {
					try {
						serverAndMessageCountInfo = "The server attempted to compare using message count = " + serverVerification.getClientCount() + ".";
					} catch (Exception e) {
						serverAndMessageCountInfo = "The server verification object could not collect message count because of an exception " + e.getMessage() + ".";
					}
				}
				List<Integer> mappingKeys = new ArrayList<Integer>(Collections.list(mapping.keys()));
				String resultMessage = new String("Failed! "
						 + clientAndMessageCountInfo + " "
						 + serverAndMessageCountInfo + " "
						 + "The server mapping contains message count keys:");
				for (Integer i : mappingKeys) {
					resultMessage += " " + i.toString();
				}
				String resultJsonString = this.makeJsonStringVerification("verify_result", resultMessage);
				this.sendInstanceTextUpdate(clientSession, resultJsonString);
			}
			
		} catch (Exception ex) {
			
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			if (ex!=null) ex.printStackTrace(printWriter);
			String stack = result.toString();
			
			ex.printStackTrace();
			
			//Send a message to client that verification failed
			String resultMessage = "Failed!\n" + stack;
			String resultJsonString = this.makeJsonStringVerification("verify_result", resultMessage);
			this.sendInstanceTextUpdate(clientSession, resultJsonString);
		} finally {
			
			//Verification on the server has stopped
			synchronized (verificationPeers) {
				verificationPeers.remove(clientSession);
				
				if (verificationPeers.isEmpty() == true) {
					//Since verification messages are sent to a specific session, this server will only end verification if this is the last session running a verification
					verify = false;
					mapping = null;
				}
			}
		}
		
		return;
	}
	
	private String makeJsonStringVerification(String type, String result) {
		
		JSONObject json = new JSONObject();
		String resultMsg = null;
		
		json.put("type", type);
		json.put("result", result);
		//TODO: Include a reason why the verification failed
		
		try {
			resultMsg = json.serialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return resultMsg;
	}

	public void sendInitMessage(Session session) {
		
		debugOut("< MonitorEndpoint sendInitMessage() >");
		
		try {
			InitMessage init_msg;
			synchronized (messageLock) {
				init_msg = new InitMessage(SimpleMonitorEndpoint.warehouseSales, SimpleMonitorEndpoint.neworders, SimpleMonitorEndpoint.messageCount.get());
			}
			session.getBasicRemote().sendObject(init_msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendInstanceTextUpdate(Session session, String msg) {
		
		if (session.isOpen()) {
			try {
				session.getBasicRemote().sendText(msg);
			} catch (IOException e) {
				System.err.println("Error in sending message to " + session);
				e.printStackTrace();
			}
		}
	}
	
	public static void sendStaticTextUpdate(String msg) {

		Session attemptedSession = null;
		
		synchronized (peers) {
			for (Session s : peers) {
				attemptedSession = s;
				if (s.isOpen()) {
					try {
						s.getBasicRemote().sendText(msg);
					} catch (IOException e) {
						System.err.println("Error in sending message to " + attemptedSession);
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void sendPaymentTransaction(int warehouseId, double sale) {
		
		try {
			int newCount;
			ArrayList<Double> newWS = null;
			ArrayList<Double> newNO = null;
			synchronized (messageLock) {
				warehouseSales.set(warehouseId-1, warehouseSales.get(warehouseId-1) + sale);
				newCount = messageCount.incrementAndGet();
				if (verify == true) {
					newWS = new ArrayList<Double>(warehouseSales);
					newNO = new ArrayList<Double>(neworders);
				}
			}
			if ((verify == true) && (newWS != null) && (newNO != null)) {
				VerificationMessage newVerifyPoint = new VerificationMessage(newWS, newNO, newCount);
				mapping.put(Integer.valueOf(messageCount.get()), newVerifyPoint);
			}
			String msg = makeJsonStringFromPaymentTransactionData(warehouseId, Double.valueOf(sale));
			sendTransactionUpdate(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendNewOrderTransaction(int warehouseId, double price) {
		
		try{
			int newCount;
			ArrayList<Double> newWS = null;
			ArrayList<Double> newNO = null;
			synchronized (messageLock) {
				neworders.set(warehouseId-1, neworders.get(warehouseId-1) + price);
				newCount = messageCount.incrementAndGet();
				if (verify == true) {
					newWS = new ArrayList<Double>(warehouseSales);
					newNO = new ArrayList<Double>(neworders);
				}
			}
			if ((verify == true) && (newWS != null) && (newNO != null)) {
				VerificationMessage newVerifyPoint = new VerificationMessage(newWS, newNO, newCount);
				mapping.put(Integer.valueOf(messageCount.get()), newVerifyPoint);
			}
			String msg = makeJsonStringFromNewOrderTransactionData(warehouseId, Double.valueOf(price));
			sendTransactionUpdate(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendTransactionUpdate(String msg) {
		
		Session attemptedSession = null;
		synchronized (peers) {
			for (Session s : peers) {
				attemptedSession = s;
				if (s.isOpen()) {
					try {
						s.getBasicRemote().sendText(msg);
					} catch (IOException e) {
						System.err.println("Error in sending message to " + attemptedSession);
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static String makeJsonStringFromPaymentTransactionData(int id, Double sale) {
		
		JSONObject json = new JSONObject();
		String msg = null;
		
		json.put("type", "payment");
		json.put("warehouse_id", id);
		json.put("warehouse_sale", sale);
		
		try {
			msg = json.serialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return msg;
	}
	
	public static String makeJsonStringFromNewOrderTransactionData(int id, Double price) {
		
		JSONObject json = new JSONObject();
		String msg = null;
		
		json.put("type", "neworder");
		json.put("outWarehouseId", id);
		json.put("outTotal", price);
		
		try {
			msg = json.serialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return msg;
	}
}
