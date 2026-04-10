import java.util.*;

/**
 * ============================================================
 * MAIN CLASS - BookMyStayApp
 * ============================================================
 *
 * Use Case 8: Booking History & Reporting
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        // Booking queue (Use Case 5)
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Abhi", "Single"));
        queue.addRequest(new Reservation("Subha", "Double"));
        queue.addRequest(new Reservation("Vanmathi", "Suite"));

        // Inventory (Use Case 6)
        RoomInventory inventory = new RoomInventory();

        // Booking history
        BookingHistory history = new BookingHistory();

        // Booking service
        BookingService bookingService = new BookingService(history);

        // Process bookings
        bookingService.processBookings(queue, inventory);

        // Reporting
        BookingReportService reportService = new BookingReportService();

        System.out.println("\n--- Booking History Report ---\n");
        reportService.printAllBookings(history);

        System.out.println("\n--- Summary Report ---\n");
        reportService.printSummary(history);
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
    private String roomId; // assigned after confirmation

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}

/**
 * ============================================================
 * CLASS - BookingRequestQueue
 * ============================================================
 */
class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNext() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

/**
 * ============================================================
 * CLASS - RoomInventory
 * ============================================================
 */
class RoomInventory {

    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single", 2);
        availability.put("Double", 1);
        availability.put("Suite", 1);
    }

    public int getAvailable(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        availability.put(type, availability.get(type) - 1);
    }
}

/**
 * ============================================================
 * CLASS - BookingHistory
 * ============================================================
 *
 * Stores confirmed bookings in order
 */
class BookingHistory {

    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
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

    public void processBookings(BookingRequestQueue queue, RoomInventory inventory) {

        while (!queue.isEmpty()) {

            Reservation r = queue.getNext();
            String type = r.getRoomType();

            if (inventory.getAvailable(type) > 0) {

                String roomId = generateRoomId(type);

                while (allocatedIds.contains(roomId)) {
                    roomId = generateRoomId(type);
                }

                allocatedIds.add(roomId);
                inventory.decrement(type);

                r.setRoomId(roomId);

                // Add to history
                history.add(r);

                System.out.println("CONFIRMED: " + r.getGuestName()
                        + " | " + type
                        + " | Room ID: " + roomId);

            } else {
                System.out.println("FAILED: " + r.getGuestName()
                        + " | No " + type + " rooms");
            }
        }
    }

    private String generateRoomId(String type) {
        return type.substring(0, 1) + (int)(Math.random() * 1000);
    }
}

/**
 * ============================================================
 * CLASS - BookingReportService
 * ============================================================
 */
class BookingReportService {

    // Print all bookings
    public void printAllBookings(BookingHistory history) {

        for (Reservation r : history.getAll()) {
            System.out.println("Guest: " + r.getGuestName()
                    + ", Room Type: " + r.getRoomType()
                    + ", Room ID: " + r.getRoomId());
        }
    }

    // Summary report
    public void printSummary(BookingHistory history) {

        Map<String, Integer> countMap = new HashMap<>();

        for (Reservation r : history.getAll()) {
            String type = r.getRoomType();
            countMap.put(type, countMap.getOrDefault(type, 0) + 1);
        }

        for (String type : countMap.keySet()) {
            System.out.println(type + " Rooms Booked: " + countMap.get(type));
        }
    }
}