package ibm.gse.eda.inventory.domain;

import java.time.LocalDateTime;

public class Item {
    public static String RESTOCK = "RESTOCK";
    public static String SALE = "SALE";
    public String storeName;
    public String sku;
    public int quantity;
    public String type;
    public Double price;
    public String timestamp;

    public Item(){}

    public Item(String store, String sku, String type, int quantity, double price) {
        this.storeName = store;
        this.sku = sku;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = LocalDateTime.now().toString();
}
}
