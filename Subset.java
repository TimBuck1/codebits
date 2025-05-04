
package com.analyser.application.practise.scenario;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TradetaxEnricher {
    public static void main(String[] args) {
        List<Trade> trades = List.of(
                new Trade(1, 1L, "Exchange1"),
                new Trade(2, 2L, "Exchange2"),
                new Trade(3, 3L, "Exchange3"),
                new Trade(4, 4L, "Exchange4"),
                new Trade(5, 5L, "Exchange5"),
                new Trade(6, 6L, "Exchange6"),
                new Trade(7, 7L, "Exchange7"),
                new Trade(8, 8L, "Exchange8"),
                new Trade(9, 9L, "Exchange9"),
                new Trade(10, 10L, "Exchange10")
        );

        List<tax> taxs = List.of(
                new tax(1, 1L, "group1", BigDecimal.valueOf(100), Action.INSERT),
                new tax(1, 2L, "group1", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(1, 3L, "group1", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(1, 4L, "group1", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(1, 5L, "group1", BigDecimal.valueOf(300), Action.DELETE),

                new tax(2, 2L, "group2", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(3, 3L, "group3", BigDecimal.valueOf(300), Action.DELETE),
                new tax(4, 1L, "group4", BigDecimal.valueOf(400), Action.INSERT),
                new tax(5, 5L, "group5", BigDecimal.valueOf(500), Action.UPDATE),

                new tax(6, 1L, "group1", BigDecimal.valueOf(100), Action.INSERT),
                new tax(6, 2L, "group2", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(6, 3L, "group2", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(6, 4L, "group3", BigDecimal.valueOf(300), Action.DELETE),

                new tax(7, 3L, "group2", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(7, 3L, "group3", BigDecimal.valueOf(300), Action.UPDATE),
                new tax(7, 3L, "group2", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(7, 3L, "group3", BigDecimal.valueOf(300), Action.UPDATE)
        );


/*// Group taxs by trade ID and filter trades with more than one tax
        Map<Integer, List<tax>> taxsGroupedByTradeId = taxs.stream()
                .collect(Collectors.groupingBy(tax::tradeId, LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        // Print grouped taxs for verification
        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);
            taxList.forEach(System.out::println);
        });

        System.out.println("-----------------------------------");*/

        Map<Integer, List<tax>> taxsGroupedByTradeId = taxs.stream()
                .collect(Collectors.groupingBy(tax::tradeId, LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparing(tax::group)) // Sort by group
                                .collect(Collectors.toList()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Print grouped taxs for verification
        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);
            taxList.forEach(System.out::println);
        });

        System.out.println("-----------------------------------");



        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);

            tax lastProcessed = null;

            for (tax current : taxList) {
                if (current.Action() == Action.INSERT) {
                    // Persist to DB and send container CREDIT event
                    System.out.println("Persisting to DB: " + current);
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessed = current;
                } else if (current.Action() == Action.UPDATE) {
                    if (lastProcessed != null) {
                        // Send container DEBIT for the previous amount
                        System.out.println("container DEBIT: " + lastProcessed.amount());
                    }
                    // Send container CREDIT for the new amount
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessed = current;
                } else if (current.Action() == Action.DELETE) {
                    // Send container DEBIT for all taxs fetched from DB
                    System.out.println("Deleting from DB: " + current);
                    System.out.println("container DEBIT: " + lastProcessed.amount());
                }
            }
        });

        System.out.println("------------------comparing taxs-----------------");

        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);

            tax lastProcessed = null;

            for (tax current : taxList) {
                if (current.Action() == Action.INSERT) {
                    // Persist to DB and send container CREDIT event
                    System.out.println("Persisting to DB: " + current);
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessed = current;
                } else if (current.Action() == Action.UPDATE) {
                    if (lastProcessed != null && lastProcessed.group().equals(current.group())) {
                        // Send container DEBIT for the previous amount if categories match
                        System.out.println("container DEBIT: " + lastProcessed.amount());
                    }
                    // Send container CREDIT for the new amount
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessed = current;
                } else if (current.Action() == Action.DELETE) {
                    if (lastProcessed != null && lastProcessed.group().equals(current.group())) {
                        // Send container DEBIT for all taxs fetched from DB if categories match
                        System.out.println("Deleting from DB: " + current);
                        System.out.println("container DEBIT: " + lastProcessed.amount());
                    }
                }
            }
        });

        System.out.println("------------------comparing taxs-----unordered------------");


        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);

            Map<String, tax> lastProcessedBygroup = new LinkedHashMap<>();

            for (tax current : taxList) {
                if (current.Action() == Action.INSERT) {
                    // Persist to DB and send container CREDIT event
                    System.out.println("Persisting to DB: " + current);
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessedBygroup.put(current.group(), current);
                } else if (current.Action() == Action.UPDATE) {
                    tax lastProcessed = lastProcessedBygroup.get(current.group());
                    if (lastProcessed != null) {
                        // Send container DEBIT for the previous amount if categories match
                        System.out.println("container DEBIT: " + lastProcessed.amount());
                    }
                    // Send container CREDIT for the new amount
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessedBygroup.put(current.group(), current);
                } else if (current.Action() == Action.DELETE) {
                    tax lastProcessed = lastProcessedBygroup.get(current.group());
                    if (lastProcessed != null) {
                        // Send container DEBIT for all taxs fetched from DB if categories match
                        System.out.println("Deleting from DB: " + current);
                        System.out.println("container DEBIT: " + lastProcessed.amount());
                    }
                }
            }
        });



        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);

            Map<String, tax> lastProcessedBygroup = new LinkedHashMap<>();

            for (tax current : taxList) {
                processtax(current, lastProcessedBygroup);
            }
        });


        Map<Integer, List<tax>> taxsGroupedByTradeIdDelete = taxs.stream()
                .collect(Collectors.groupingBy(tax::tradeId, LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparing(tax::group)) // Sort by group
                                .collect(Collectors.toList()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));


        taxsGroupedByTradeIdDelete.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);

            Map<String, tax> lastProcessedBygroup = new LinkedHashMap<>();

            for (tax current : taxList) {
                processtax(current, lastProcessedBygroup);
            }
        });


    }


    private static void handleUpdate(tax current, Map<String, tax> lastProcessedBygroup) {
        tax lastProcessed = lastProcessedBygroup.get(current.group());
        if (lastProcessed == null) {
            // Persist to DB if this is the first UPDATE for the group
            System.out.println("Persisting to DB: " + current);
        } else {
            // Send container DEBIT for the previous amount if categories match
            System.out.println("container DEBIT: " + lastProcessed.amount());
        }
        // Send container CREDIT for the new amount
        System.out.println("container CREDIT: " + current.amount());
        lastProcessedBygroup.put(current.group(), current);
    }



    private static void processtax(tax current, Map<String, tax> lastProcessedBygroup) {
        if (current.Action() == Action.INSERT) {
            handleInsert(current, lastProcessedBygroup);
        } else if (current.Action() == Action.UPDATE) {
            handleUpdate(current, lastProcessedBygroup);
        } else if (current.Action() == Action.DELETE) {
            handleDelete(current, lastProcessedBygroup);
        }
    }

    private static void handleInsert(tax current, Map<String, tax> lastProcessedBygroup) {
        System.out.println("Persisting to DB: " + current);
        System.out.println("container CREDIT: " + current.amount());
        lastProcessedBygroup.put(current.group(), current);
    }

/*    private static void handleUpdate(tax current, Map<String, tax> lastProcessedBygroup) {
        tax lastProcessed = lastProcessedBygroup.get(current.group());
        if (lastProcessed != null) {
            System.out.println("container DEBIT: " + lastProcessed.amount());
        }
        System.out.println("container CREDIT: " + current.amount());
        lastProcessedBygroup.put(current.group(), current);
    }*/

/*
    private static void handleDelete(tax current, Map<String, tax> lastProcessedBygroup) {
        tax lastProcessed = lastProcessedBygroup.get(current.group());
        if (lastProcessed != null) {
            System.out.println("Deleting from DB: " + current);
            System.out.println("container DEBIT: " + lastProcessed.amount());
        }
    }
*/

    private static void handleDelete(tax current, Map<String, tax> lastProcessedBygroup) {
        tax lastProcessed = lastProcessedBygroup.get(current.group());
        if (lastProcessed != null) {
            // Send container CREDIT for the last processed amount
            System.out.println("container CREDIT: " + lastProcessed.amount());
            // Remove the last processed tax from the group
            lastProcessedBygroup.remove(current.group());
        }
        System.out.println("Deleting from DB: " + current);
    }


    record Trade(int tradeId, long uuid, String exchangeId) {
    }

    record tax(int tradeId, long uuid, String group, BigDecimal amount, Enum Action) {
    }


}
8888888888888888888



package com.analyser.application.practise.scenario;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TradetaxEnricherCopy {
    public static void main(String[] args) {
        List<Trade> trades = List.of(
                new Trade(1, 1L, "Exchange1"),
                new Trade(2, 2L, "Exchange2"),
                new Trade(3, 3L, "Exchange3"),
                new Trade(4, 4L, "Exchange4"),
                new Trade(5, 5L, "Exchange5"),
                new Trade(6, 6L, "Exchange6"),
                new Trade(7, 7L, "Exchange7"),
                new Trade(8, 8L, "Exchange8"),
                new Trade(9, 9L, "Exchange9"),
                new Trade(10, 10L, "Exchange10")
        );

        List<tax> taxs = List.of(
                new tax(1, 1L, "group1", BigDecimal.valueOf(100), Action.INSERT),
                new tax(1, 2L, "group1", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(1, 3L, "group1", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(1, 4L, "group1", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(1, 5L, "group1", BigDecimal.valueOf(300), Action.DELETE),

                new tax(2, 2L, "group2", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(3, 3L, "group3", BigDecimal.valueOf(300), Action.DELETE),
                new tax(4, 1L, "group4", BigDecimal.valueOf(400), Action.INSERT),
                new tax(5, 5L, "group5", BigDecimal.valueOf(500), Action.UPDATE),

                new tax(6, 1L, "group1", BigDecimal.valueOf(100), Action.INSERT),
                new tax(6, 2L, "group2", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(6, 3L, "group2", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(6, 4L, "group3", BigDecimal.valueOf(300), Action.DELETE),

                new tax(7, 3L, "group2", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(7, 3L, "group3", BigDecimal.valueOf(300), Action.UPDATE),
                new tax(7, 3L, "group2", BigDecimal.valueOf(200), Action.UPDATE),
                new tax(7, 3L, "group3", BigDecimal.valueOf(300), Action.UPDATE)
        );


/*// Group taxs by trade ID and filter trades with more than one tax
        Map<Integer, List<tax>> taxsGroupedByTradeId = taxs.stream()
                .collect(Collectors.groupingBy(tax::tradeId, LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        // Print grouped taxs for verification
        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);
            taxList.forEach(System.out::println);
        });

        System.out.println("-----------------------------------");*/

        Map<Integer, List<tax>> taxsGroupedByTradeId = taxs.stream()
                .collect(Collectors.groupingBy(tax::tradeId, LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparing(tax::group)) // Sort by group
                                .collect(Collectors.toList()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Print grouped taxs for verification
        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);
            taxList.forEach(System.out::println);
        });

        System.out.println("-----------------------------------");



        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);

            tax lastProcessed = null;

            for (tax current : taxList) {
                if (current.Action() == Action.INSERT) {
                    // Persist to DB and send container CREDIT event
                    System.out.println("Persisting to DB: " + current);
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessed = current;
                } else if (current.Action() == Action.UPDATE) {
                    if (lastProcessed != null) {
                        // Send container DEBIT for the previous amount
                        System.out.println("container DEBIT: " + lastProcessed.amount());
                    }
                    // Send container CREDIT for the new amount
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessed = current;
                } else if (current.Action() == Action.DELETE) {
                    // Send container DEBIT for all taxs fetched from DB
                    System.out.println("Deleting from DB: " + current);
                    System.out.println("container DEBIT: " + lastProcessed.amount());
                }
            }
        });

        System.out.println("------------------comparing taxs-----------------");

        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);

            tax lastProcessed = null;

            for (tax current : taxList) {
                if (current.Action() == Action.INSERT) {
                    // Persist to DB and send container CREDIT event
                    System.out.println("Persisting to DB: " + current);
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessed = current;
                } else if (current.Action() == Action.UPDATE) {
                    if (lastProcessed != null && lastProcessed.group().equals(current.group())) {
                        // Send container DEBIT for the previous amount if categories match
                        System.out.println("container DEBIT: " + lastProcessed.amount());
                    }
                    // Send container CREDIT for the new amount
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessed = current;
                } else if (current.Action() == Action.DELETE) {
                    if (lastProcessed != null && lastProcessed.group().equals(current.group())) {
                        // Send container DEBIT for all taxs fetched from DB if categories match
                        System.out.println("Deleting from DB: " + current);
                        System.out.println("container DEBIT: " + lastProcessed.amount());
                    }
                }
            }
        });

        System.out.println("------------------comparing taxs-----unordered------------");


        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);

            Map<String, tax> lastProcessedBygroup = new LinkedHashMap<>();

            for (tax current : taxList) {
                if (current.Action() == Action.INSERT) {
                    // Persist to DB and send container CREDIT event
                    System.out.println("Persisting to DB: " + current);
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessedBygroup.put(current.group(), current);
                } else if (current.Action() == Action.UPDATE) {
                    tax lastProcessed = lastProcessedBygroup.get(current.group());
                    if (lastProcessed != null) {
                        // Send container DEBIT for the previous amount if categories match
                        System.out.println("container DEBIT: " + lastProcessed.amount());
                    }
                    // Send container CREDIT for the new amount
                    System.out.println("container CREDIT: " + current.amount());
                    lastProcessedBygroup.put(current.group(), current);
                } else if (current.Action() == Action.DELETE) {
                    tax lastProcessed = lastProcessedBygroup.get(current.group());
                    if (lastProcessed != null) {
                        // Send container DEBIT for all taxs fetched from DB if categories match
                        System.out.println("Deleting from DB: " + current);
                        System.out.println("container DEBIT: " + lastProcessed.amount());
                    }
                }
            }
        });



        taxsGroupedByTradeId.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);

            Map<String, tax> lastProcessedBygroup = new LinkedHashMap<>();

            for (tax current : taxList) {
                processtax(current, lastProcessedBygroup);
            }
        });


        Map<Integer, List<tax>> taxsGroupedByTradeIdDelete = taxs.stream()
                .collect(Collectors.groupingBy(tax::tradeId, LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparing(tax::group)) // Sort by group
                                .collect(Collectors.toList()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));


        taxsGroupedByTradeIdDelete.forEach((tradeId, taxList) -> {
            System.out.println("Trade ID: " + tradeId);

            Map<String, tax> lastProcessedBygroup = new LinkedHashMap<>();

            for (tax current : taxList) {
                processtax(current, lastProcessedBygroup);
            }
        });


    }

    private static void processtax(tax current, Map<String, tax> lastProcessedBygroup) {
        if (current.Action() == Action.INSERT) {
            handleInsert(current, lastProcessedBygroup);
        } else if (current.Action() == Action.UPDATE) {
            handleUpdate(current, lastProcessedBygroup);
        } else if (current.Action() == Action.DELETE) {
            handleDelete(current, lastProcessedBygroup);
        }
    }

    private static void handleInsert(tax current, Map<String, tax> lastProcessedBygroup) {
        System.out.println("Persisting to DB: " + current);
        System.out.println("container CREDIT: " + current.amount());
        lastProcessedBygroup.put(current.group(), current);
    }

    private static void handleUpdate(tax current, Map<String, tax> lastProcessedBygroup) {
        tax lastProcessed = lastProcessedBygroup.get(current.group());
        if (lastProcessed != null) {
            System.out.println("container DEBIT: " + lastProcessed.amount());
        }
        System.out.println("container CREDIT: " + current.amount());
        lastProcessedBygroup.put(current.group(), current);
    }

/*
    private static void handleDelete(tax current, Map<String, tax> lastProcessedBygroup) {
        tax lastProcessed = lastProcessedBygroup.get(current.group());
        if (lastProcessed != null) {
            System.out.println("Deleting from DB: " + current);
            System.out.println("container DEBIT: " + lastProcessed.amount());
        }
    }
*/

    private static void handleDelete(tax current, Map<String, tax> lastProcessedBygroup) {
        tax lastProcessed = lastProcessedBygroup.get(current.group());
        if (lastProcessed != null) {
            // Send container CREDIT for the last processed amount
            System.out.println("container CREDIT: " + lastProcessed.amount());
            // Remove the last processed tax from the group
            lastProcessedBygroup.remove(current.group());
        }
        System.out.println("Deleting from DB: " + current);
    }


    record Trade(int tradeId, long uuid, String exchangeId) {
    }

    record tax(int tradeId, long uuid, String group, BigDecimal amount, Enum Action) {
    }


}



