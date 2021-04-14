package ibm.gse.eda.inventory.infrastructure;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import ibm.gse.eda.inventory.domain.Inventory;
import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class InventoryAggregate {

    @Inject
    @ConfigProperty(name = "inventory.topic")
    public String inventoryStockOutputStreamName;

    public static String INVENTORY_STORE_NAME = "StoreInventoryStock";

    private static JsonbSerde<Inventory> inventorySerde = new JsonbSerde<>(Inventory.class);
    
    /**
     * Create a key value store named INVENTORY_STORE_NAME to persist store inventory
     * @return
     */
    public static Materialized<String, Inventory, KeyValueStore<Bytes, byte[]>> materializeAsInventoryStore() {
        return Materialized.<String, Inventory, KeyValueStore<Bytes, byte[]>>as(INVENTORY_STORE_NAME)
                .withKeySerde(Serdes.String()).withValueSerde(inventorySerde);
    }

    public void produceInventoryStockStream(KTable<String, Inventory> inventory) {
        KStream<String, Inventory> inventories = inventory.toStream();
        inventories.print(Printed.toSysOut());

        inventories.to(inventoryStockOutputStreamName, Produced.with(Serdes.String(), inventorySerde));
    }
}
