package ut;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ibm.gse.eda.inventory.domain.Inventory;
import ibm.gse.eda.inventory.domain.Item;
import ibm.gse.eda.inventory.domain.StoreInventoryAgent;
import ibm.gse.eda.inventory.infrastructure.InventoryAggregate;
import ibm.gse.eda.inventory.infrastructure.ItemStream;
import io.quarkus.kafka.client.serialization.JsonbSerde;

public class TestInventoryTopology {
    private static TopologyTestDriver testDriver;
    private TestInputTopic<String, Item> inputTopic;
    private TestOutputTopic<String, Inventory> inventoryOutputTopic;
    private Serde<String> stringSerde = new Serdes.StringSerde();
    private JsonbSerde<Item> itemSerde = new JsonbSerde<>(Item.class);
    private JsonbSerde<Inventory> inventorySerde = new JsonbSerde<>(Inventory.class);

    private StoreInventoryAgent agent = new StoreInventoryAgent();
    
    public  Properties getStreamsConfig() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stock-aggregator");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummmy:1234");
        return props;
    }
    
    @BeforeEach
    public void setup() {
        
        agent.itemStream = new ItemStream();
        agent.itemStream.itemSoldInputStreamName="itemSold";
        agent.inventoryAggregate = new InventoryAggregate();
        agent.inventoryAggregate.inventoryStockOutputStreamName = "inventory";
        Topology topology = agent.processItemStream();
        testDriver = new TopologyTestDriver(topology, getStreamsConfig());
        inputTopic = testDriver.createInputTopic(agent.itemStream.itemSoldInputStreamName, 
                                stringSerde.serializer(),
                                itemSerde.serializer());
        inventoryOutputTopic = testDriver.createOutputTopic(agent.inventoryAggregate.inventoryStockOutputStreamName, 
                                stringSerde.deserializer(), 
                                inventorySerde.deserializer());
    }

    @AfterEach
    public void tearDown() {
        try {
            testDriver.close();
        } catch (final Exception e) {
             System.out.println("Ignoring exception, test failing due this exception:" + e.getLocalizedMessage());
        } 
    }
    
    @Test
    public void shouldGetInventoryUpdatedQuantity(){
        //given an item is sold in a store
        Item item = new Item("Store-1","Item-1",Item.RESTOCK,5,33.2);
        inputTopic.pipeInput(item.storeName, item);        
        item = new Item("Store-1","Item-1",Item.SALE,2,33.2);
        inputTopic.pipeInput(item.storeName, item);

        Assertions.assertFalse(inventoryOutputTopic.isEmpty()); 
        Assertions.assertEquals(5, inventoryOutputTopic.readKeyValue().value.stock.get("Item-1"));
        Assertions.assertEquals(3, inventoryOutputTopic.readKeyValue().value.stock.get("Item-1"));
    }
}
