.sorted(Map.Entry.comparingByValue(
    Comparator.comparing(entry -> {
        List<YourObject> objects = entry.getValue();
        return objects.stream()
                .filter(Objects::nonNull)
                .min(Comparator.comparing(YourObject::getUpperBand, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(YourObject::getUpperBand)
                .orElse(null);
    }, Comparator.nullsLast(Comparator.naturalOrder()))
))
