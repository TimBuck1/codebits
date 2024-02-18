Map<Long, List<YourObject>> groupedAndSortedMap = yourObjects.stream()
        .collect(Collectors.groupingBy(
                obj -> obj.getParent() != null ? obj.getParent().getId() : obj.getId(),
                Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparing(obj -> obj.getUpperBand(), Comparator.nullsLast(Comparator.naturalOrder()))),
                        optional -> optional.map(Collections::singletonList).orElse(Collections.emptyList())
                )
        ))
        .entrySet().stream()
        .sorted(Map.Entry.comparingByValue(
                Comparator.comparing(entry -> entry.getValue().get(0).getUpperBand(), Comparator.nullsLast(Comparator.naturalOrder()))
        ))
        .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
        ));
