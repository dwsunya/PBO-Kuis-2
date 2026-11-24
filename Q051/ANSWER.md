# Q051 — Your answer

## (d) HashMap order vs Python dict order

TODO(Q051): explain why printing `map.keySet()` from a `HashMap` may come out in a
different order on another machine or Java version, and what the Python `dict`
guarantees instead (and whether that guarantee is alphabetical).

```HashMap does not guarantee any specific iteration order because its elements are stored based on hash values. Therefore, the order returned by map.keySet() may vary between different machines or Java versions. In Python, a dict guarantees insertion order, meaning keys are iterated in the order they were first inserted.```