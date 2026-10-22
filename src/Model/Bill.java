
package Model;

public class Bill {

    private int itemId;
    private int purchaseId;
    private int bookId;
    private int quantity;
    private double unitPrice;

    public Bill(int itemId, int purchaseId, int bookId,
                        int quantity, double unitPrice) {
        this.itemId = itemId;
        this.purchaseId = purchaseId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getItemId() {
        return itemId;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}

