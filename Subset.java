Map<IdUpperBandKey, List<YourObject>> groupedAndSortedMap = yourObjects.stream()
        .collect(Collectors.groupingBy(
                obj -> new IdUpperBandKey(obj.getId(), obj.getUpperBand())
        ))
        .entrySet().stream()
        .sorted(Map.Entry.comparingByValue(
                Comparator.comparing(objList -> objList.get(0).getUpperBand(), Comparator.nullsLast(Comparator.naturalOrder()))
        ))
        .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
        ));




import java.math.BigDecimal;
import java.util.Objects;

class IdUpperBandKey {
    private final Long id;
    private final BigDecimal upperBand;

    public IdUpperBandKey(Long id, BigDecimal upperBand) {
        this.id = id;
        this.upperBand = upperBand;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getUpperBand() {
        return upperBand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdUpperBandKey that = (IdUpperBandKey) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(upperBand, that.upperBand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upperBand);
    }
}
