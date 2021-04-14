package ibm.gse.eda.inventory.infrastructure;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import ibm.gse.eda.inventory.domain.Item;
import io.quarkus.kafka.client.serialization.JsonbSerde;

@ApplicationScoped
public class ItemStream {
    @Inject
    @ConfigProperty(name="items.topic")
    public String itemSoldInputStreamName;
    
    private static JsonbSerde<Item> itemSerde = new JsonbSerde<>(Item.class);
    public StreamsBuilder builder;
      
    public ItemStream(){
        builder = new StreamsBuilder();
    }

    public KStream<String,Item> getItemStreams(){
        return builder.stream(itemSoldInputStreamName, 
                        Consumed.with(Serdes.String(), itemSerde));
    }

    public static Grouped<String, Item> buildGroupDefinition() {
		return Grouped.with(Serdes.String(),itemSerde);
    }

    public Topology run() {
		return builder.build();
    }
}
