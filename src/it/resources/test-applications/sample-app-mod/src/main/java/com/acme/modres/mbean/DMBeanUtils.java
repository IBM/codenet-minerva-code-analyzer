package com.acme.modres.mbean;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanOperationInfo;

public final class DMBeanUtils {
	private static final Logger logger = Logger.getLogger(DMBeanUtils.class.getName());
	
	public static MBeanOperationInfo[] getOps(OpMetadataList opList) {
		MBeanOperationInfo[] ops = null;
		if (opList == null || opList.getOpMetadatList() == null) {
			logger.log(Level.WARNING, "No operation is configured");
			return ops;
		}
		
		int numOps = opList.getOpMetadatList().size(); 
		if (numOps > 0) {
			ops = new MBeanOperationInfo[numOps];
			int i = 0;
			for (OpMetadata opMetadata : opList.getOpMetadatList()) {
				String name = opMetadata.getName();
				String desc = opMetadata.getDescription();
				String type = opMetadata.getType();
				int impact = opMetadata.getImpact();

				MBeanOperationInfo opInfo = new MBeanOperationInfo(name, desc, /* signature */ null, type, impact, /* descriptor */ null);
				ops[i++] = opInfo;
			}
		}
		
		return ops;
	}
}
