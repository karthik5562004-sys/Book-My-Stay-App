public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();

        Room singleRoom = new Room(1, 250, 1500.0);
        Room doubleRoom = new Room(2, 400, 2500.0);
        Room suiteRoom = new Room(3, 750, 5000.0);

        RoomSearchService searchService = new RoomSearchService();

        searchService.searchAvailableRooms(
                inventory,
                singleRoom,
                doubleRoom,
                suiteRoom
        );
    }
}

class RoomSearchService {

    public void searchAvailableRooms(
            RoomInventory inventory,
            Room singleRoom,
            Room doubleRoom,
            Room suiteRoom) {

        java.util.Map<String, Integer> availability = inventory.getRoomAvailability();

        System.out.println("Room Search\n");

        if (availability.getOrDefault("Single", 0) > 0) {
            printRoomDetails("Single Room", singleRoom, availability.get("Single"));
        }

        if (availability.getOrDefault("Double", 0) > 0) {
            printRoomDetails("Double Room", doubleRoom, availability.get("Double"));
        }

        if (availability.getOrDefault("Suite", 0) > 0) {
            printRoomDetails("Suite Room", suiteRoom, availability.get("Suite"));
        }
    }

    private void printRoomDetails(String title, Room room, int availableCount) {
        System.out.println(title + ":");
        System.out.println("Beds: " + room.getBeds());
        System.out.println("Size: " + room.getSize() + " sqft");
        System.out.println("Price per night: " + room.getPrice());
        System.out.println("Available: " + availableCount);
        System.out.println();
    }
}

class RoomInventory {

    private java.util.Map<String, Integer> availability = new java.util.HashMap<>();

    public RoomInventory() {
        availability.put("Single", 5);
        availability.put("Double", 3);
        availability.put("Suite", 2);
    }

    public java.util.Map<String, Integer> getRoomAvailability() {
        return availability;
    }
}

class Room {

    private int beds;
    private int size;
    private double price;

    public Room(int beds, int size, double price) {
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public int getBeds() {
        return beds;
    }

    public int getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }
}