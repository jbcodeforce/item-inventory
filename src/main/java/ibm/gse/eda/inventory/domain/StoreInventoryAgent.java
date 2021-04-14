package ibm.gse.eda.inventory.domain;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import ibm.gse.eda.inventory.infrastructure.InventoryAggregate;
import ibm.gse.eda.inventory.infrastructure.ItemStream;

@ApplicationScoped
public class StoreInventoryAgent {
    @Inject
    public ItemStream itemStream;

    @Inject
    public InventoryAggregate inventoryAggregate;
    
    public Topology processItemStream(){
        KStream<String,Item> items = itemStream.getItemStreams();     
        // process items and aggregate at the store level 
        KTable<String,Inventory> inventory = items
            // use store name as key
            .groupByKey(ItemStream.buildGroupDefinition())
            .aggregate(
                () ->  new Inventory(), // initializer
                (k , newItem, existingInventory) 
                    -> existingInventory.updateStockQuantity(k,newItem), 
                    InventoryAggregate.materializeAsInventoryStore());       
        inventoryAggregate.produceInventoryStockStream(inventory);
        return itemStream.run();
    }
}
