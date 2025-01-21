package batch.artifacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.partition.PartitionMapper;
import jakarta.batch.api.partition.PartitionPlan;
import jakarta.batch.api.partition.PartitionPlanImpl;
import jakarta.batch.runtime.context.StepContext;
import jakarta.inject.Inject;

public class DistrictTablePartitionMapper implements PartitionMapper {

	@Inject
	private StepContext stepCtx;

	/**
	 * The number of warehouses to create
	 */
	@Inject
	@BatchProperty(name = "numberWarehouses")
	private String numWarehousesStr;
	
    /**
     * Default constructor. 
     */
    public DistrictTablePartitionMapper() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see PartitionMapper#mapPartitions()
     */
    public PartitionPlan mapPartitions() {
debugOut("District table mapper, step name is: " + stepCtx.getStepName());
		
		PartitionPlanImpl plan = new PartitionPlanImpl();
		List<Properties> partitionProperties = new ArrayList<Properties>(1);

		int numWarehouses = Integer.parseInt(numWarehousesStr);

		// If number of warehouses = 1, then partition by the 10 districts.   Otherwise,
		// if we have more than 1 warehouse, partition by warehouse instead of district.

		if (numWarehouses == 1) {
			plan.setPartitions(10);
			System.out.println("DistrictTablePartitionMapper is making 10 district partitions for 1 warehouse");
			for (int district=1; district<=10; district++){
				Properties p = new Properties();
				p.setProperty("numberWarehouses", numWarehousesStr);
				p.setProperty("currentWarehouse", String.valueOf(1));
				p.setProperty("currentDistrict", String.valueOf(district));
				partitionProperties.add(p);		
			}  	   
		} else {
			plan.setPartitions(numWarehouses);
			System.out.println("DistrictTablePartitionMapper is making " + numWarehouses + " partitions, one per warehouse");
			for (int warehouse=1; warehouse<=numWarehouses; warehouse++) {
				Properties p = new Properties();
				p.setProperty("numberWarehouses", numWarehousesStr);
				p.setProperty("currentWarehouse", String.valueOf(warehouse));
				p.setProperty("currentDistrict", String.valueOf(1));
				partitionProperties.add(p);		
			}	
		}

		plan.setPartitionProperties(partitionProperties.toArray(new Properties[0]));
		return plan;
	}


	private void debugOut(String message) {
		if (System.getProperty("DEBUG_OUT") != null)
			if (System.getProperty("DEBUG_OUT").equals("ON"))
				System.out.println(message);
	}
}
