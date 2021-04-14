package ibm.gse.eda.inventory.api;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ibm.gse.eda.inventory.domain.Inventory;
import ibm.gse.eda.inventory.domain.Item;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Path("/inventory")
public class InventoryResource {
    
    @GET
    @Path("/store/{storeID}")
    @Produces(MediaType.APPLICATION_JSON)
    public  Uni<Inventory> getStock(@PathParam("storeID") String storeID) {
        Inventory stock = new Inventory();
        stock.storeName = storeID;
        Item newItem = new Item();
        newItem.quantity = 10;
        newItem.sku="item-01";
        newItem.type = Item.RESTOCK;
        stock.updateStockQuantity(storeID, newItem);
        return Uni.createFrom().item( stock);
    }
}
