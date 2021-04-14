
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
@Path("/inventory")
public class InventoryResource {
    
    @GET
    @Path("/store/{storeID}")
    @Produces(MediaType.APPLICATION_JSON)
    public  Uni<JsonObject> getStock(@PathParam("storeID") String storeID) {
            JsonObject stock = new JsonObject("{\"name\": \"hello you\", \"id\": \"" + storeID + "\"}");
            return Uni.createFrom().item( stock);
    }
}
