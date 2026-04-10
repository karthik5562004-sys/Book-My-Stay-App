import java.util.*;

/**
 * ============================================================
 * MAIN CLASS - BookMyStayApp
 * ============================================================
 *
 * Use Case 9: Error Handling & Validation
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        BookingRequestQueue queue = new BookingRequestQueue();

        // Valid + Invalid requests
        queue.addRequest(new Reservation("Abhi", "Single"));
        queue.addRequest(new Reservation("Subha", "Double"));
        queue.addRequest(new Reservation("InvalidUser", "Penthouse")); // invalid
        queue.addRequest(new Reservation("OverUser", "Suite"));
        queue.addRequest(new Reservation("AnotherUser", "Suite")); // may exceed availability

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();

        BookingService bookingService = new BookingService(history);

        bookingService.processBookings(queue, inventory);
    }
}

/**
 * ============================================================
 * CUSTOM EXCEPTIONS
 * ============================================================
 */
class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class NoAvailabilityException extends Exception {
    public NoAvailabilityException(String message) {
        super(message);
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
        availability.put("Single", 1);
        availability.put("Double", 1);
        availability.put("Suite", 1);
    }

    public boolean isValidRoomType(String type) {
        return availability.containsKey(type);
    }

    public int getAvailable(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void decrement(String type) throws NoAvailabilityException {
        int count = availability.get(type);

        if (count <= 0) {
            throw new NoAvailabilityException("No " + type + " rooms available.");
        }

        availability.put(type, count - 1);
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

        System.out.println("Booking Processing with Validation\n");

        while (!queue.isEmpty()) {

            Reservation r = queue.getNext();

            try {
                validate(r, inventory);

                String roomId = generateRoomId(r.getRoomType());

                while (allocatedIds.contains(roomId)) {
                    roomId = generateRoomId(r.getRoomType());
                }

                allocatedIds.add(roomId);

                // Safe inventory update
                inventory.decrement(r.getRoomType());

                r.setRoomId(roomId);
                history.add(r);

                System.out.println("CONFIRMED: " + r.getGuestName()
                        + " | " + r.getRoomType()
                        + " | Room ID: " + roomId);

            } catch (InvalidRoomTypeException | NoAvailabilityException e) {

                System.out.println("ERROR for " + r.getGuestName() + ": " + e.getMessage());
            }
        }
    }

    // Validation (Fail-Fast)
    private void validate(Reservation r, RoomInventory inventory)
            throws InvalidRoomTypeException, NoAvailabilityException {

        if (!inventory.isValidRoomType(r.getRoomType())) {
            throw new InvalidRoomTypeException(
                    "Invalid room type: " + r.getRoomType());
        }

        if (inventory.getAvailable(r.getRoomType()) <= 0) {
            throw new NoAvailabilityException(
                    "No rooms available for type: " + r.getRoomType());
        }
    }

    private String generateRoomId(String type) {
        return type.substring(0, 1) + (int)(Math.random() * 1000);
    }
}