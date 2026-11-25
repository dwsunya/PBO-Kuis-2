# Q054 - Your answer

## (a) The eight lines

1. `totalKg(shirts)`: compiles. A `Shirt` is a `Garment`, so we can read its weight.
2. `totalKg(anything)`: does not compile. An `Object` is not always a `Garment`.
3. `addSpareTowels(basket, 2)`: compiles. A list of garments can hold towels.
4. `addSpareTowels(bathTowels, 1)`: does not compile. A regular towel is not always a bath towel.
5. `transfer(bathTowels, towels)`: compiles. Bath towels can go into a towel list, with `T` as `Towel`.
6. `transfer(towels, shirts)`: does not compile. Towels cannot go into a shirt list.
7. `List<Garment> g = shirts;`: does not compile. `List` is invariant. Otherwise, we could add a towel through `g` to a shirt list.
8. `view.add(new Shirt("x"))`: does not compile. We do not know the exact element type of `view`; here it points to a towel list.

## (c) The rule

PECS means Producer Extends, Consumer Super. Use `extends` to read items and `super` to add items.

In `transfer`, we read from `src`, so it uses `? extends T`. We add to `dst`, so it uses `? super T`. We can still call `src.clear()` because `extends` does not make a collection immutable.

## (d) Python

`Sequence` and `Iterable` are covariant, so they can provide items of a subtype. That is why `total_kg(shirts)`, `transfer(bath_towels, towels)`, and `transfer(shirts, basket)` work.

Mypy rejects these calls or assignments:

- `total_kg(anything)` when `anything` is `List[object]`: not every object is a garment.
- `add_spare_towels(basket, 2)`: `MutableSequence` is invariant, so `List[Garment]` does not match `MutableSequence[Towel]`.
- `add_spare_towels(bath_towels, 1)`: the list should only hold bath towels.
- `transfer(towels, shirts)`: towels do not belong in a shirt list.
- Assigning `List[Shirt]` to `List[Garment]`: `List` is invariant too.

To get the behavior of `? super Towel`, we use the contravariant `Sink` protocol. It only asks for an `append` method that accepts a towel. So `Sink[Towel]` accepts towel, garment, and object lists, but not bath-towel-only lists.

The driver hides the type error in `transfer([Towel("rogue")], only_shirts)` with `# type: ignore[misc]`. Python still runs it because type hints are not enforced at runtime. Also, `transfer` does not clear the source in Python because `Iterable` has no guaranteed `clear` method; the driver clears it separately.
