import java.util.*;

/**
 * ============================================================
 * MAIN CLASS - BookMyStayApp
 * ============================================================
 *
 * Use Case 6: Reservation Confirmation & Room Allocation
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        // Inventory setup
        RoomInventory inventory = new RoomInventory();

        // Booking queue (from Use Case 5)
        BookingRequestQueue requestQueue = new BookingRequestQueue();

        requestQueue.addRequest(new Reservation("Abhi", "Single"));
        requestQueue.addRequest(new Reservation("Subha", "Double"));
        requestQueue.addRequest(new Reservation("Vanmathi", "Suite"));

        // Booking service (allocation logic)
        BookingService bookingService = new BookingService();

        // Process and allocate rooms
        bookingService.processBookings(requestQueue, inventory);
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
}

/**
 * ============================================================
 * CLASS - BookingRequestQueue
 * ============================================================
 */
class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
    }

    public Reservation getNextRequest() {
        return queue.poll(); // FIFO
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

    public int getAvailableRooms(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void decrementRoom(String type) {
        availability.put(type, availability.get(type) - 1);
    }
}

/**
 * ============================================================
 * CLASS - BookingService
 * ============================================================
 */
class BookingService {

    // Track allocated room IDs (prevents duplicates)
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Map room type -> assigned room IDs
    private Map<String, Set<String>> roomAllocations = new HashMap<>();

    public void processBookings(BookingRequestQueue queue, RoomInventory inventory) {

        System.out.println("Booking Confirmation & Allocation\n");

        while (!queue.isEmpty()) {

            Reservation request = queue.getNextRequest();

            String type = request.getRoomType();

            // Check availability
            if (inventory.getAvailableRooms(type) > 0) {

                // Generate unique room ID
                String roomId = generateRoomId(type);

                // Ensure uniqueness
                while (allocatedRoomIds.contains(roomId)) {
                    roomId = generateRoomId(type);
                }

                // Store ID
                allocatedRoomIds.add(roomId);

                // Map allocation
                roomAllocations
                        .computeIfAbsent(type, k -> new HashSet<>())
                        .add(roomId);

                // Update inventory immediately
                inventory.decrementRoom(type);

                // Confirm booking
                System.out.println(
                        "Booking CONFIRMED for Guest: " + request.getGuestName()
                                + ", Room Type: " + type
                                + ", Room ID: " + roomId
                );

            } else {
                System.out.println(
                        "Booking FAILED for Guest: " + request.getGuestName()
                                + " (No " + type + " rooms available)"
                );
            }
        }
    }

    // Room ID generator
    private String generateRoomId(String type) {
        return type.substring(0, 1).toUpperCase() + (int)(Math.random() * 1000);
    }
}