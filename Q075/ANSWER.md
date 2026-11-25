# Q075 - Your answer

## (a) The trace

Each row uses the format `id customer service status time`.

0. Attach, sorted by ready time:
   ```text
   A2 Budi WDI READY 11:00
   A4 Dewi WF READY 12:00
   A5 Rahmat WDI WASHING 13:00
   A1 Sari WF WASHING 14:00
   A6 Citra WF DRYING 15:00
   A3 Ayu IR RECEIVED 16:00
   ```
1. Filter `ra`, keeping Rahmat and Citra:
   ```text
   A5 Rahmat WDI WASHING 13:00
   A6 Citra WF DRYING 15:00
   ```
2. Sort by customer, Citra first:
   ```text
   A6 Citra WF DRYING 15:00
   A5 Rahmat WDI WASHING 13:00
   ```
3. Add A7 Budi: **no update**, because Budi does not contain `ra`.
4. Advance A5, Rahmat becomes DRYING:
   ```text
   A6 Citra WF DRYING 15:00
   A5 Rahmat WDI DRYING 13:00
   ```
5. Filter `RA`: **no update**, because `RA` and `ra` match the same names.
6. Only DRYING: **no update**, because both rows are already DRYING.
7. Advance A6, Citra becomes READY and drops out:
   ```text
   A5 Rahmat WDI DRYING 13:00
   ```
8. Clear the name filter: **no update**, because only A5 is DRYING, even without the name filter.
9. Only READY, still sorted by customer:
   ```text
   A2 Budi WDI READY 11:00
   A6 Citra WF READY 15:00
   A4 Dewi WF READY 12:00
   ```

Total: 6 updates for 10 events, including attach.

## (c) Why compare

We compare the rows so the view only updates when something on screen changes. For example, adding Budi does not change the results of the `ra` filter, so there is no need to redraw the list.

The view-model is easy to test without a GUI. We can use a callback to record the rows, then check them and the update count after each event.

## (d) Chapter 9 ideas

- `List<Order>` holds orders and `List<String>` holds display rows. Generics keep the types correct.
- `Comparator<Order>` sorts by time or name, then by ID when there is a tie.
- Streams filter, sort, and turn orders into text. `List.equals()` checks the row contents and their order.
- `List.copyOf()` gives callers an unmodifiable list, but the order objects are still shared.
- `List<Runnable>` holds listeners, and `Consumer<List<String>>` sends the rows to the view.
