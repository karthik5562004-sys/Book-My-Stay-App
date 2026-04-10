import java.util.*;

/**
 * ============================================================
 * MAIN CLASS - BookMyStayApp
 * ============================================================
 *
 * Use Case 11: Concurrent Booking Simulation (Thread Safety)
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue queue = new BookingRequestQueue();

        // Simulate multiple guest requests (same time)
        queue.addRequest(new Reservation("Guest1", "Single"));
        queue.addRequest(new Reservation("Guest2", "Single"));
        queue.addRequest(new Reservation("Guest3", "Single"));

        // Create multiple threads (simulating concurrent users)
        Thread t1 = new Thread(new BookingProcessor(queue, inventory));
        Thread t2 = new Thread(new BookingProcessor(queue, inventory));
        Thread t3 = new Thread(new BookingProcessor(queue, inventory));

        // Start threads
        t1.start();
        t2.start();
        t3.start();
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

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

/**
 * ============================================================
 * CLASS - BookingRequestQueue (THREAD-SAFE)
 * ============================================================
 */
class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    // synchronized ensures only one thread accesses at a time
    public synchronized void addRequest(Reservation r) {
        queue.offer(r);
    }

    public synchronized Reservation getNextRequest() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}

/**
 * ============================================================
 * CLASS - RoomInventory (THREAD-SAFE)
 * ============================================================
 */
class RoomInventory {

    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single", 1); // only 1 room → race condition test
    }

    public synchronized boolean hasAvailability(String type) {
        return availability.getOrDefault(type, 0) > 0;
    }

    public synchronized void allocateRoom(String type) {
        int count = availability.get(type);
        availability.put(type, count - 1);
    }
}

/**
 * ============================================================
 * CLASS - BookingProcessor (THREAD)
 * ============================================================
 */
class BookingProcessor implements Runnable {

    private BookingRequestQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingRequestQueue queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {

            Reservation r;

            // Critical section: queue access
            synchronized (queue) {
                if (queue.isEmpty()) break;
                r = queue.getNextRequest();
            }

            if (r == null) continue;

            // Critical section: inventory access
            synchronized (inventory) {
                if (inventory.hasAvailability(r.getRoomType())) {

                    inventory.allocateRoom(r.getRoomType());

                    System.out.println(Thread.currentThread().getName()
                            + " CONFIRMED: " + r.getGuestName()
                            + " (" + r.getRoomType() + ")");

                } else {
                    System.out.println(Thread.currentThread().getName()
                            + " FAILED: " + r.getGuestName()
                            + " (No rooms available)");
                }
            }
        }
    }
}