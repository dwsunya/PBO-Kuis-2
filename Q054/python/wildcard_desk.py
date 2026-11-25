# wildcard_desk.py - Q054: variance in Python type hints, checked by mypy.
from typing import Iterable, List, MutableSequence, Protocol, Sequence, TypeVar


class Garment:
    def __init__(self, name: str, grams: int) -> None:
        self.name = name
        self.grams = grams

    def __repr__(self) -> str:
        return self.name


class Shirt(Garment):
    def __init__(self, name: str) -> None:
        super().__init__(name, 250)


class Towel(Garment):
    def __init__(self, name: str, grams: int = 600) -> None:
        super().__init__(name, grams)


class BathTowel(Towel):
    def __init__(self, name: str) -> None:
        super().__init__(name, 900)


T = TypeVar("T")
T_contra = TypeVar("T_contra", contravariant=True)  # A consumer of Garment can also consume Towel.


class Sink(Protocol[T_contra]):
    """Anything with append(x): the Python spelling of "? super T"."""

    def append(self, item: T_contra, /) -> None: ...


def total_kg(items: Sequence[Garment]) -> float:
    # Sequence also accepts shirts and towels because they are garments
    return sum(g.grams for g in items) / 1000


def add_spare_towels(dst: MutableSequence[Towel], n: int) -> None:
    # invariant, so a garment list does not match this type hint
    for i in range(1, n + 1):
        dst.append(Towel(f"spare-{i}"))


def add_spare_towels_anywhere(dst: Sink[Towel], n: int) -> None:
    # only needs append, so a garment list works too
    for i in range(1, n + 1):
        dst.append(Towel(f"spare-{i}"))


def transfer(src: Iterable[T], dst: List[T]) -> int:
    # read from src, then add each item to dst
    moved = 0 # count the added items
    for item in src:
        dst.append(item) # add item to the destination
        moved += 1 # increase the count
    return moved  # Clearing is left to the caller because Iterable has no clear method.


def main() -> None:
    basket: List[Garment] = []
    shirts: List[Shirt] = [Shirt("batik"), Shirt("polo")]
    towels: List[Towel] = [Towel("hand")]
    bath_towels: List[BathTowel] = [BathTowel("bath-1"), BathTowel("bath-2")]

    print("(1) total_kg(shirts)          =", total_kg(shirts))
    add_spare_towels_anywhere(basket, 2)       # accepted by mypy, like List<? super Towel>
    print("(3) basket after spares       =", basket)
    moved = transfer(bath_towels, towels)      # T = Towel; Iterable is covariant
    bath_towels.clear()
    print(f"(5) transfer(bath -> towels)  = {moved} moved, towels = {towels}, bath_towels = {bath_towels}")
    moved = transfer(shirts, basket)
    print(f"    transfer(shirts -> basket) = {moved} moved, basket = {basket}")
    # Python still runs this, so a towel gets into the shirt list
    only_shirts: List[Shirt] = [Shirt("kemeja")]
    transfer([Towel("rogue")], only_shirts)    # type: ignore[misc]
    print("    run time let a Towel into List[Shirt]:", only_shirts,
          [type(g).__name__ for g in only_shirts])


if __name__ == "__main__":
    main()
