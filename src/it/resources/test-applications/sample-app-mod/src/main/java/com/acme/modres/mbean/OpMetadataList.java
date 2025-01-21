package com.acme.modres.mbean;

import java.util.ArrayList;
import java.util.List;

public class OpMetadataList {
	
	public OpMetadataList() {
	}

	private List<OpMetadata> opMetadatList = new ArrayList<>();
	
	public void add(OpMetadata opMetadata) {
		opMetadatList.add(opMetadata);
	}

	public List<OpMetadata> getOpMetadatList() {
		return opMetadatList;
	}

	public void setOpMetadatList(List<OpMetadata> opMetadatList) {
		this.opMetadatList = opMetadatList;
	}

}
