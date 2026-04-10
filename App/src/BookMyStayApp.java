import java.util.*;

/**
 * ============================================================
 * MAIN CLASS - BookMyStayApp
 * ============================================================
 *
 * Use Case 10: Booking Cancellation & Inventory Rollback
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();

        BookingService bookingService = new BookingService(history);

        // Create and confirm bookings
        Reservation r1 = new Reservation("Abhi", "Single");
        Reservation r2 = new Reservation("Subha", "Double");

        bookingService.confirmBooking(r1, inventory);
        bookingService.confirmBooking(r2, inventory);

        // Cancellation service
        CancellationService cancelService = new CancellationService(history, inventory);

        System.out.println("\n--- Cancelling Booking ---\n");

        // Cancel one booking
        cancelService.cancelBooking(r1.getRoomId());

        // Try cancelling again (invalid case)
        cancelService.cancelBooking(r1.getRoomId());
    }
}

/**
 * ============================================================
 * CLASS - Reservation
 * ============================================================
 */
class Reservation {

    private String guestName;
    private String roomType;
    private String roomId;
    private boolean cancelled = false;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomId() { return roomId; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}

/**
 * ============================================================
 * CLASS - RoomInventory
 * ============================================================
 */
class RoomInventory {

    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single", 1);
        availability.put("Double", 1);
    }

    public int getAvailable(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        availability.put(type, availability.get(type) - 1);
    }

    public void increment(String type) {
        availability.put(type, availability.get(type) + 1);
    }
}

/**
 * ============================================================
 * CLASS - BookingHistory
 * ============================================================
 */
class BookingHistory {

    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public Reservation findByRoomId(String roomId) {
        for (Reservation r : history) {
            if (r.getRoomId().equals(roomId)) {
                return r;
            }
        }
        return null;
    }
}

/**
 * ============================================================
 * CLASS - BookingService
 * ============================================================
 */
class BookingService {

    private Set<String> allocatedIds = new HashSet<>();
    private BookingHistory history;

    public BookingService(BookingHistory history) {
        this.history = history;
    }

    public void confirmBooking(Reservation r, RoomInventory inventory) {

        if (inventory.getAvailable(r.getRoomType()) <= 0) {
            System.out.println("Booking failed for " + r.getGuestName());
            return;
        }

        String roomId = generateRoomId(r.getRoomType());

        while (allocatedIds.contains(roomId)) {
            roomId = generateRoomId(r.getRoomType());
        }

        allocatedIds.add(roomId);
        inventory.decrement(r.getRoomType());

        r.setRoomId(roomId);
        history.add(r);

        System.out.println("CONFIRMED: " + r.getGuestName()
                + " | " + r.getRoomType()
                + " | Room ID: " + roomId);
    }

    private String generateRoomId(String type) {
        return type.substring(0, 1) + (int)(Math.random() * 1000);
    }
}

/**
 * ============================================================
 * CLASS - CancellationService
 * ============================================================
 */
class CancellationService {

    private BookingHistory history;
    private RoomInventory inventory;

    // Stack for rollback tracking (LIFO)
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(BookingHistory history, RoomInventory inventory) {
        this.history = history;
        this.inventory = inventory;
    }

    public void cancelBooking(String roomId) {

        Reservation r = history.findByRoomId(roomId);

        // Validation
        if (r == null) {
            System.out.println("ERROR: Reservation not found for Room ID " + roomId);
            return;
        }

        if (r.isCancelled()) {
            System.out.println("ERROR: Booking already cancelled for Room ID " + roomId);
            return;
        }

        // Step 1: Push to rollback stack
        rollbackStack.push(roomId);

        // Step 2: Restore inventory
        inventory.increment(r.getRoomType());

        // Step 3: Mark as cancelled
        r.setCancelled(true);

        System.out.println("CANCELLED: " + r.getGuestName()
                + " | Room ID: " + roomId);

        // Show rollback stack
        System.out.println("Rollback Stack: " + rollbackStack);
    }
}