.sorted(Map.Entry.comparingByValue(
    Comparator.comparing(entry -> 
        entry.getValue().stream()
            .filter(Objects::nonNull) // Filter out null values
            .min(Comparator.comparing(YourObject::getUpperBand))
            .map(YourObject::getUpperBand)
            .orElse(null),
        Comparator.nullsLast(Comparator.naturalOrder())
    )
))
