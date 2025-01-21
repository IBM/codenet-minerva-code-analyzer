package pricequote.lite;

import irwwbase.ExtendedRandom;
import irwwbase.IRWWBase;
import irwwbase.MaxValues;
import irwwbase.POJOQualifier;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
//Commenting out unused import
//import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import jakarta.inject.Inject;


//Comment out unused import
//import pricequote.lite.PriceQuoteSingleItemInInfo;

import java.io.IOException;
import java.util.Random;

/**
 * @author mkerr
 *
 */
public class GenerateAutoGenInputTag extends SimpleTagSupport {
	
	private byte itemListLength = 15; 
	private String className = "GenerateAutoGenInputTag";
	@Inject @POJOQualifier IRWWBase irwwBase; 
  
	public GenerateAutoGenInputTag() {
		super();
	}
	
    public void doTag() throws JspException, IOException {
    	
        HttpServletRequest request;
        HttpSession session = null;
        //Commenting out unused variable
		//JspContext ctx = getJspContext();
		PageContext pageContext = null;
	
		try {
			if (getJspContext() instanceof PageContext){
				pageContext = (PageContext) getJspContext();
			}		
			request = (HttpServletRequest)pageContext.getRequest();		
			session = request.getSession();
		} catch (Throwable t) {
			System.out.println("Error: request.getSession(true) failed");
			t.printStackTrace();
			throw new JspException("<<< Error: In " + className + ": Error getting request or session: exception: "+t);
		}
    	   	
		    	
		byte database = 1;
		database = irwwBase.dbSize();
		PriceQuoteSingleItemInInfo[] arraySingleInputItems = new PriceQuoteSingleItemInInfo[itemListLength];
		arraySingleInputItems = this.generatePriceQuoteSingleItemInInfo(database);
		//Commenting out unused variable
		//ExtendedRandom rand = new ExtendedRandom();
    	
		try {			
			session.setAttribute("arraySingleInputItems", arraySingleInputItems);
			
			session.setAttribute("itemId1", arraySingleInputItems[0].getInItemId());
			session.setAttribute("itemId2", arraySingleInputItems[1].getInItemId());
			session.setAttribute("itemId3", arraySingleInputItems[2].getInItemId());
			session.setAttribute("itemId4", arraySingleInputItems[3].getInItemId());
			session.setAttribute("itemId5", arraySingleInputItems[4].getInItemId());
			session.setAttribute("itemId6", arraySingleInputItems[5].getInItemId());
			session.setAttribute("itemId7", arraySingleInputItems[6].getInItemId());
			session.setAttribute("itemId8", arraySingleInputItems[7].getInItemId());
			session.setAttribute("itemId9", arraySingleInputItems[8].getInItemId());
			session.setAttribute("itemId10", arraySingleInputItems[9].getInItemId());
			session.setAttribute("itemId11", arraySingleInputItems[10].getInItemId());
			session.setAttribute("itemId12", arraySingleInputItems[11].getInItemId());
			session.setAttribute("itemId13", arraySingleInputItems[12].getInItemId());
			session.setAttribute("itemId14", arraySingleInputItems[13].getInItemId());
			session.setAttribute("itemId15", arraySingleInputItems[14].getInItemId());
			
			session.setAttribute("quantity1", arraySingleInputItems[0].getInItemQuantity());
			session.setAttribute("quantity2", arraySingleInputItems[1].getInItemQuantity());
			session.setAttribute("quantity3", arraySingleInputItems[2].getInItemQuantity());
			session.setAttribute("quantity4", arraySingleInputItems[3].getInItemQuantity());
			session.setAttribute("quantity5", arraySingleInputItems[4].getInItemQuantity());
			session.setAttribute("quantity6", arraySingleInputItems[5].getInItemQuantity());
			session.setAttribute("quantity7", arraySingleInputItems[6].getInItemQuantity());
			session.setAttribute("quantity8", arraySingleInputItems[7].getInItemQuantity());;
			session.setAttribute("quantity9", arraySingleInputItems[8].getInItemQuantity());
			session.setAttribute("quantity10", arraySingleInputItems[9].getInItemQuantity());
			session.setAttribute("quantity11", arraySingleInputItems[10].getInItemQuantity());
			session.setAttribute("quantity12", arraySingleInputItems[11].getInItemQuantity());
			session.setAttribute("quantity13", arraySingleInputItems[12].getInItemQuantity());
			session.setAttribute("quantity14", arraySingleInputItems[13].getInItemQuantity());
			session.setAttribute("quantity15", arraySingleInputItems[14].getInItemQuantity());

		} catch (Exception e) {
			System.out.println("<<< "	+ className	+ " request.setAttribute - Exception: " + e);
			e.printStackTrace();
			throw new JspException("<<< Error: In " + className + ": Error setting attributes in the session: exception: "+e);
		}  		

    }
	
    private PriceQuoteSingleItemInInfo[] generatePriceQuoteSingleItemInInfo(byte db) {
		irwwBase.debugOut(">>>Entering generatePriceQuoteSingleItemInInfo");
		ExtendedRandom rand = new ExtendedRandom();
		Random generator = new Random();

		PriceQuoteSingleItemInInfo[] arraySingleInputItems = new PriceQuoteSingleItemInInfo[itemListLength];
		for (int i = 0; i < itemListLength; i++) {
			arraySingleInputItems[i] = new PriceQuoteSingleItemInInfo();
			arraySingleInputItems[i].setInItemId(rand.nextInt(1, MaxValues.itemId(db)));
			irwwBase.debugOut(">>>Item Id = " + arraySingleInputItems[i].getInItemId());
		}
		boolean disjunct;
		do {
			disjunct = true;
			for (int i = 0; i < itemListLength - 1; i++) {
				for (int j = i + 1; j < itemListLength; j++) {
					if (arraySingleInputItems[i].getInItemId() == arraySingleInputItems[j].getInItemId()) {
						disjunct = false;
					}
				}
				if (!disjunct) {
					arraySingleInputItems[i].setInItemId(rand.nextInt(1, MaxValues.itemId(db)));
					break;
				}
			}
		} while (!disjunct);

		for (int i = 0; i < itemListLength; i++) {
			arraySingleInputItems[i].setInItemQuantity(rand.nextInt(1, MaxValues.itemQuantity(db)));
		}
		
		for (int i = 0; i < itemListLength; i++) {
			arraySingleInputItems[i].setSleep(generator.nextInt(2000));
		}
		
		return arraySingleInputItems;
	}

}
