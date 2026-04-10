import java.util.*;

/**
 * ============================================================
 * MAIN CLASS - BookMyStayApp
 * ============================================================
 *
 * Use Case 7: Add-On Service Selection
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        // Step 1: Assume reservation already created (from Use Case 6)
        String reservationId = "Single-1";

        // Step 2: Create Add-On Services
        AddOnService breakfast = new AddOnService("Breakfast", 500.0);
        AddOnService wifi = new AddOnService("WiFi", 300.0);
        AddOnService pickup = new AddOnService("Airport Pickup", 700.0);

        // Step 3: Add services to list
        List<AddOnService> selectedServices = new ArrayList<>();
        selectedServices.add(breakfast);
        selectedServices.add(wifi);
        selectedServices.add(pickup);

        // Step 4: Add-On Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Step 5: Attach services to reservation
        manager.addServices(reservationId, selectedServices);

        // Step 6: Calculate and display cost
        double totalCost = manager.calculateTotalCost(reservationId);

        System.out.println("Add-On Service Selection");
        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Total Add-On Cost: " + totalCost);
    }
}

/**
 * ============================================================
 * CLASS - AddOnService
 * ============================================================
 *
 * Represents optional services.
 */
class AddOnService {

    private String serviceName;
    private double price;

    public AddOnService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }
}

/**
 * ============================================================
 * CLASS - AddOnServiceManager
 * ============================================================
 *
 * Maps reservation ID to list of services.
 */
class AddOnServiceManager {

    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Attach services to reservation
    public void addServices(String reservationId, List<AddOnService> services) {
        serviceMap.put(reservationId, services);
    }

    // Calculate total cost of services
    public double calculateTotalCost(String reservationId) {

        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null) return 0.0;

        double total = 0.0;

        for (AddOnService s : services) {
            total += s.getPrice();
        }

        return total;
    }
}