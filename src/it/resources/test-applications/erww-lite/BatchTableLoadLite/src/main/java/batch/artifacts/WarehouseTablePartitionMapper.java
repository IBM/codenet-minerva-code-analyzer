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

public class WarehouseTablePartitionMapper implements PartitionMapper {
	
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
    public WarehouseTablePartitionMapper() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see PartitionMapper#mapPartitions()
     */
    public PartitionPlan mapPartitions() {
    	debugOut("Warehouse partition mapper step name is: " + stepCtx.getStepName());
		
    	PartitionPlanImpl plan = new PartitionPlanImpl();
    	List<Properties> partitionProperties = new ArrayList<Properties>(1);

    	int numWarehouses = Integer.parseInt(numWarehousesStr);

    	plan.setPartitions(numWarehouses);
    	System.out.println("WarehouseParitionMapper is making 1 partition per warehouse");
    	for (int warehouse=1; warehouse<=numWarehouses; warehouse++){
    		Properties p = new Properties();
    		p.setProperty("numberWarehouses", numWarehousesStr);
    		p.setProperty("currentWarehouse", String.valueOf(warehouse));
    		partitionProperties.add(p);		
    		System.out.println("Warehouse Partition: " + warehouse);
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
