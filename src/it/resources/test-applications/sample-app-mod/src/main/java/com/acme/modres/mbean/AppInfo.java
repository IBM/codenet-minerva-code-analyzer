package com.acme.modres.mbean;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

public class AppInfo implements DynamicMBean {
	
	private MBeanInfo dMBeanInfo = null;
	
	public AppInfo() {
		buildDMBeanInfo();
	}
	
	private void buildDMBeanInfo() {
		String className = getClass().getName();
		String desc = "Configurable App Info";
		MBeanAttributeInfo[] attrs = null;
		MBeanConstructorInfo[] cons = null;
		MBeanNotificationInfo[] notifications = null;
		
		OpMetadataList opMetadataList = IOUtils.getOpListFromConfig();
		MBeanOperationInfo[] ops = DMBeanUtils.getOps(opMetadataList);
		
		dMBeanInfo = new MBeanInfo(className, desc, attrs, cons, ops, notifications);
	}
	
	
	@Override
	public MBeanInfo getMBeanInfo() {
		return dMBeanInfo;
	}
	
	@Override
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		if ("increaseMaxLimit".equals(actionName)) {
			increaseLimit();
			return "Max limit increased";
		} else if ("resetMaxLimit".equals(actionName)) {
			resetLimit();
			return "Max limit reset";
		} else {
            throw new MBeanException(new UnsupportedOperationException(
                    getClass().getSimpleName() + " does not support operation " + actionName));
        }
	}
	
	private void increaseLimit() {
		System.out.println("Limit increased");
	}
	
	private void resetLimit() {
		System.out.println("Limit reset");
	}
	

	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException, ReflectionException {
		return null;
	}

	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
		
	}

	@Override
	public AttributeList getAttributes(String[] attributes) {
		return null;
	}

	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		return null;
	}
}
