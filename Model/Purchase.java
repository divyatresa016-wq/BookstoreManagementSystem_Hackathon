
package Model;

public class Purchase {

    private int purchaseId;
    private int customerId;
    private String purchaseDate;
    private double totalAmount;

    public Purchase(int purchaseId, int customerId,
                    String purchaseDate, double totalAmount) {
        this.purchaseId = purchaseId;
        this.customerId = customerId;
        this.purchaseDate = purchaseDate;
        this.totalAmount = totalAmount;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}

