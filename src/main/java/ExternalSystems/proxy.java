package ExternalSystems;

public class proxy implements AccountingSystem, TaxSystem {

    @Override
    public boolean addPayment(String teamName, String date, double amount) {
        if (amount>0) {
            return true;
        }
        return false;
    }

    @Override
    public double getTaxRate(double revenueAmount) {
        return 0.17;
    }
}
