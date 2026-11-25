// OrderListViewModel.java - Q075: a filtered, sorted list between an order book and its view.
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class OrderListViewModel {

    enum Status { RECEIVED, WASHING, DRYING, READY, PICKED_UP }

    static final class Order {
        final String id;
        final String customer;
        final String service;
        final int readyHour;
        Status status;

        Order(String id, String customer, String service, Status status, int readyHour) {
            this.id = id;
            this.customer = customer;
            this.service = service;
            this.status = status;
            this.readyHour = readyHour;
        }
    }

    // stores orders and tells listeners when something changes
    static final class OrderBook {
        private final List<Order> orders = new ArrayList<>();
        private final List<Runnable> listeners = new ArrayList<>();

        void addListener(Runnable r) {
            listeners.add(r);
        }

        void add(Order o) {
            orders.add(o);
            listeners.forEach(Runnable::run);
        }

        void advance(String id) {
            for (Order o : orders) {
                if (o.id.equals(id)) {
                    o.status = Status.values()[o.status.ordinal() + 1];
                }
            }
            listeners.forEach(Runnable::run);
        }

        List<Order> orders() {
            return List.copyOf(orders);
        }
    }

    // filter and sort the rows here
    static final Comparator<Order> BY_READY =
            Comparator.comparingInt((Order o) -> o.readyHour)
                    .thenComparing(o -> o.id); // same time, sort by id
    static final Comparator<Order> BY_CUSTOMER =
            Comparator.comparing((Order o) -> o.customer, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(o -> o.id);

    private final OrderBook book;
    private final Consumer<List<String>> view;
    private String filter = "";
    private Status onlyStatus = null;               // null: every status
    private Comparator<Order> sort = BY_READY;
    private List<String> shown = null; // null so the first result gets sent too
    int updates = 0;

    OrderListViewModel(OrderBook book, Consumer<List<String>> view) {
        this.book = book;
        this.view = view;
        book.addListener(this::recompute); // run recompute when the book changes
        recompute(); // show the first result
    }

    void setFilter(String text) {
        filter = text.strip().toLowerCase(Locale.ROOT); // remove spaces at the ends and use lowercase
        recompute();
    }

    void setOnlyStatus(Status s) {
        onlyStatus = s;
        recompute(); // update rows with the new status filter
    }

    void setSortByCustomer(boolean byCustomer) {
        sort = byCustomer ? BY_CUSTOMER : BY_READY;
        recompute(); // update the sorting
    }

    private void recompute() {
        List<String> rows = book.orders().stream()
                .filter(o -> o.customer.toLowerCase(Locale.ROOT).contains(filter)) // check the name
                .filter(o -> onlyStatus == null || o.status == onlyStatus) // null means all statuses
                .sorted(sort)
                // put status in the text so status changes are noticed too
                .map(o -> o.id + " " + o.customer + " " + o.service + " "
                        + o.status + " " + o.readyHour + ":00")
                .toList();

        if (!rows.equals(shown)) { // only update if the rows are different
            shown = rows;
            updates++; // count this update
            view.accept(rows); // send the new rows to the view
        }
    }

    public static void main(String[] args) {
        OrderBook book = new OrderBook();
        book.add(new Order("A1", "Sari", "WF", Status.WASHING, 14));
        book.add(new Order("A2", "Budi", "WDI", Status.READY, 11));
        book.add(new Order("A3", "Ayu", "IR", Status.RECEIVED, 16));
        book.add(new Order("A4", "Dewi", "WF", Status.READY, 12));
        book.add(new Order("A5", "Rahmat", "WDI", Status.WASHING, 13));
        book.add(new Order("A6", "Citra", "WF", Status.DRYING, 15));

        String[] step = {"0. attach"};
        List<String> log = new ArrayList<>();
        OrderListViewModel vm = new OrderListViewModel(book, rows -> log.add(step[0] + " -> " + rows));
        Object[][] script = {
            {"1. filter 'ra'", (Runnable) () -> vm.setFilter("ra")},
            {"2. sort by customer", (Runnable) () -> vm.setSortByCustomer(true)},
            {"3. add A7 Budi", (Runnable) () -> book.add(new Order("A7", "Budi", "IR", Status.RECEIVED, 17))},
            {"4. advance A5", (Runnable) () -> book.advance("A5")},
            {"5. filter 'RA'", (Runnable) () -> vm.setFilter("RA")},
            {"6. only DRYING", (Runnable) () -> vm.setOnlyStatus(Status.DRYING)},
            {"7. advance A6", (Runnable) () -> book.advance("A6")},
            {"8. filter ''", (Runnable) () -> vm.setFilter("")},
            {"9. only READY", (Runnable) () -> vm.setOnlyStatus(Status.READY)}};
        for (Object[] s : script) {
            step[0] = (String) s[0];
            int before = vm.updates;
            ((Runnable) s[1]).run();
            if (vm.updates == before) {
                log.add(step[0] + " -> (no update)");
            }
        }
        log.forEach(System.out::println);
        System.out.println("view updates: " + vm.updates + " for 10 events");
    }
}
