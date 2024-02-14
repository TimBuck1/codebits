 TreeSet<CustomObject> customObjectTreeSet = new TreeSet<>(
                Comparator.nullsFirst(customComparator)
                        .thenComparing((o1, o2) -> Objects.equals(o1.getAmount(), o2.getAmount())
                                && Objects.equals(o1.getRate(), o2.getRate()) ? 0 : 1)
        );
