import java.io.*;
import java.util.*;

/**
 * ============================================================
 * MAIN CLASS - BookMyStayApp
 * ============================================================
 *
 * Use Case 12: Data Persistence & System Recovery
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        PersistenceService persistence = new PersistenceService();

        // Try to load previous state
        SystemState state = persistence.loadState();

        if (state == null) {
            System.out.println("No previous data found. Starting fresh...\n");

            state = new SystemState();
            state.inventory = new RoomInventory();
            state.history = new BookingHistory();
        } else {
            System.out.println("System state restored successfully!\n");
        }

        // Simulate new booking
        BookingService bookingService = new BookingService(state.history);

        Reservation r1 = new Reservation("Abhi", "Single");
        bookingService.confirmBooking(r1, state.inventory);

        // Save state before exit
        persistence.saveState(state);

        System.out.println("\nSystem state saved. Restart to test recovery.");
    }
}

/**
 * ============================================================
 * CLASS - SystemState (Serializable)
 * ============================================================
 */
class SystemState implements Serializable {
    RoomInventory inventory;
    BookingHistory history;
}

/**
 * ============================================================
 * CLASS - Reservation
 * ============================================================
 */
class Reservation implements Serializable {

    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getRoomId() { return roomId; }
}

/**
 * ============================================================
 * CLASS - RoomInventory
 * ============================================================
 */
class RoomInventory implements Serializable {

    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single", 2);
        availability.put("Double", 2);
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
 */
class BookingHistory implements Serializable {

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
 * CLASS - PersistenceService
 * ============================================================
 */
class PersistenceService {

    private static final String FILE_NAME = "system_state.ser";

    // Save system state
    public void saveState(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);
            System.out.println("State saved to file.");

        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    // Load system state
    public SystemState loadState() {

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            return (SystemState) ois.readObject();

        } catch (FileNotFoundException e) {
            return null; // First run case

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading state. Starting fresh.");
            return null;
        }
    }
}