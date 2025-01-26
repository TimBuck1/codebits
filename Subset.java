To efficiently handle and enrich the data for 100 trades, FPRs, OPRs, and OffsetPairs, a carefully designed SQL strategy is required to minimize joins and optimize enrichment.

Proposed Solution
Batch Query Optimization: Use a single SQL query or minimal batch queries to fetch all required data in one shot. Group data based on the type (Trade, FPR, OPR, OffsetPair) and enrich them appropriately.

Designing the SQL Query:

Use CASE to differentiate between Trade, FPR, OPR, and OffsetPair.
Use UNION ALL to fetch data for each type in a single query.
Fetch the enrichment data via JOINs or WITH (Common Table Expressions) and minimize redundant lookups.
SQL Query Example
Below is an example SQL structure for enrichment:

sql
Copy
Edit
WITH EnrichedTrades AS (
    SELECT
        t.trade_id,
        t.trade_type,
        e.field1 AS enrichment_field1,
        e.field2 AS enrichment_field2
    FROM
        Trades t
    LEFT JOIN EnrichmentTable e ON t.trade_id = e.trade_id
),
EnrichedFPRs AS (
    SELECT
        f.fpr_id,
        t.trade_id,
        f.other_field,
        e.field1 AS enrichment_field1,
        e.field2 AS enrichment_field2
    FROM
        FPRs f
    LEFT JOIN Trades t ON f.trade_id = t.trade_id
    LEFT JOIN EnrichmentTable e ON t.trade_id = e.trade_id
),
EnrichedOPRs AS (
    SELECT
        o.opr_id,
        t.trade_id,
        o.other_field,
        e.field1 AS enrichment_field1,
        e.field2 AS enrichment_field2
    FROM
        OPRs o
    LEFT JOIN Trades t ON o.trade_id = t.trade_id
    LEFT JOIN EnrichmentTable e ON t.trade_id = e.trade_id
),
EnrichedOffsetPairs AS (
    SELECT
        o.offset_id,
        t1.trade_id AS buy_trade_id,
        t2.trade_id AS sell_trade_id,
        e1.field1 AS buy_enrichment,
        e2.field1 AS sell_enrichment
    FROM
        OffsetPairs o
    LEFT JOIN Trades t1 ON o.buy_trade_id = t1.trade_id
    LEFT JOIN Trades t2 ON o.sell_trade_id = t2.trade_id
    LEFT JOIN EnrichmentTable e1 ON t1.trade_id = e1.trade_id
    LEFT JOIN EnrichmentTable e2 ON t2.trade_id = e2.trade_id
)
SELECT * FROM EnrichedTrades
UNION ALL
SELECT * FROM EnrichedFPRs
UNION ALL
SELECT * FROM EnrichedOPRs
UNION ALL
SELECT * FROM EnrichedOffsetPairs;
Steps to Execute
Identify Common Enrichment Fields: Define all fields required for enrichment (e.g., fields from EnrichmentTable).

Use Batch IDs for Querying: Fetch all 100 trade IDs or FPR/OPR/OffsetPair IDs at once and use them in the query:

sql
Copy
Edit
WHERE t.trade_id IN (:batch_ids)
Split Processing by Type: Separate logic for enriching Trade, FPR, OPR, and OffsetPair. Use a shared method to execute the above query and map results to corresponding objects.

Optimized Database Indexing: Ensure indexes on:

trade_id in all relevant tables.
Join keys such as buy_trade_id, sell_trade_id.
Parallel Processing in Code: Once the data is retrieved, process the enrichment for Trade, FPR, OPR, and OffsetPair objects in parallel using multithreading.

Code Implementation (Java + Spring Boot Example)
java
Copy
Edit
@Autowired
private JdbcTemplate jdbcTemplate;

public List<EnrichedObject> enrichTrades(List<String> tradeIds) {
    String sql = "WITH EnrichedTrades AS ( ... ) SELECT * FROM ...";
    
    Map<String, Object> params = Map.of("tradeIds", tradeIds);
    
    return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
        String type = rs.getString("type");
        if ("Trade".equals(type)) {
            return mapToTrade(rs);
        } else if ("FPR".equals(type)) {
            return mapToFPR(rs);
        } else if ("OPR".equals(type)) {
            return mapToOPR(rs);
        } else if ("OffsetPair".equals(type)) {
            return mapToOffsetPair(rs);
        }
        return null;
    });
}
Benefits of This Approach
Single Query Execution: Minimized database round trips.

Scalable for Large Batches: Efficient for 100+ trades or objects with proper indexing.

Flexible for Additional Enrichment: Easily extendable by adding more joins or enrichment fields.

Optimized for Mixed Types: Handles all types (Trade, FPR, OPR, OffsetPair) seamlessly.

Let me know if you need further clarification or help implementing!






You said:
how can we use unionall  if the field counts differ based on type
ChatGPT said:
ChatGPT
If the field counts or types differ for each entity (e.g., Trade, FPR, OPR, OffsetPair), you can use UNION ALL by ensuring all queries in the union return the same number of columns. You can achieve this by:

Adding placeholder values (NULL or default values) for fields not applicable to certain types.
Including a discriminator column (e.g., type) to identify the type of the record in the result set.
Here’s how you can handle it:

Modified SQL Query
sql
Copy
Edit
WITH EnrichedTrades AS (
    SELECT
        t.trade_id AS id,
        'Trade' AS type,
        t.trade_type AS trade_type,
        NULL AS fpr_field,
        NULL AS opr_field,
        NULL AS offset_field,
        e.field1 AS enrichment_field1,
        e.field2 AS enrichment_field2
    FROM
        Trades t
    LEFT JOIN EnrichmentTable e ON t.trade_id = e.trade_id
),
EnrichedFPRs AS (
    SELECT
        f.fpr_id AS id,
        'FPR' AS type,
        NULL AS trade_type,
        f.other_field AS fpr_field,
        NULL AS opr_field,
        NULL AS offset_field,
        e.field1 AS enrichment_field1,
        e.field2 AS enrichment_field2
    FROM
        FPRs f
    LEFT JOIN Trades t ON f.trade_id = t.trade_id
    LEFT JOIN EnrichmentTable e ON t.trade_id = e.trade_id
),
EnrichedOPRs AS (
    SELECT
        o.opr_id AS id,
        'OPR' AS type,
        NULL AS trade_type,
        NULL AS fpr_field,
        o.other_field AS opr_field,
        NULL AS offset_field,
        e.field1 AS enrichment_field1,
        e.field2 AS enrichment_field2
    FROM
        OPRs o
    LEFT JOIN Trades t ON o.trade_id = t.trade_id
    LEFT JOIN EnrichmentTable e ON t.trade_id = e.trade_id
),
EnrichedOffsetPairs AS (
    SELECT
        o.offset_id AS id,
        'OffsetPair' AS type,
        NULL AS trade_type,
        NULL AS fpr_field,
        NULL AS opr_field,
        CONCAT(t1.trade_id, '-', t2.trade_id) AS offset_field,
        e1.field1 AS enrichment_field1,
        e2.field2 AS enrichment_field2
    FROM
        OffsetPairs o
    LEFT JOIN Trades t1 ON o.buy_trade_id = t1.trade_id
    LEFT JOIN Trades t2 ON o.sell_trade_id = t2.trade_id
    LEFT JOIN EnrichmentTable e1 ON t1.trade_id = e1.trade_id
    LEFT JOIN EnrichmentTable e2 ON t2.trade_id = e2.trade_id
)
SELECT * FROM EnrichedTrades
UNION ALL
SELECT * FROM EnrichedFPRs
UNION ALL
SELECT * FROM EnrichedOPRs
UNION ALL
SELECT * FROM EnrichedOffsetPairs;
Key Points
Matching Column Count and Data Types:

For each type (Trade, FPR, OPR, OffsetPair), ensure that the query returns the same number of columns.
Use NULL or default values (e.g., 'N/A') for fields not relevant to a particular type.
Discriminator Column (type):

Add a column like type to indicate the type of the record (Trade, FPR, OPR, or OffsetPair).
This allows you to distinguish and process the records appropriately after fetching them.
Custom Column Mappings for Enrichment:

Use JOINs to add enrichment fields to each entity.
Ensure consistent column names for enrichment fields, even if their sources differ.
Java Implementation Example
The returned result set will have consistent fields regardless of the type. You can then map these results based on the type column:

java
Copy
Edit
public List<Object> enrichData(List<String> ids) {
    String sql = "WITH EnrichedTrades AS ( ... ) SELECT * FROM ...";

    return jdbcTemplate.query(sql, (rs, rowNum) -> {
        String type = rs.getString("type");
        if ("Trade".equals(type)) {
            return mapToTrade(rs);
        } else if ("FPR".equals(type)) {
            return mapToFPR(rs);
        } else if ("OPR".equals(type)) {
            return mapToOPR(rs);
        } else if ("OffsetPair".equals(type)) {
            return mapToOffsetPair(rs);
        }
        return null;
    });
}

private Trade mapToTrade(ResultSet rs) throws SQLException {
    return new Trade(
        rs.getString("id"),
        rs.getString("trade_type"),
        rs.getString("enrichment_field1"),
        rs.getString("enrichment_field2")
    );
}

private FPR mapToFPR(ResultSet rs) throws SQLException {
    return new FPR(
        rs.getString("id"),
        rs.getString("fpr_field"),
        rs.getString("enrichment_field1"),
        rs.getString("enrichment_field2")
    );
}

// Similar mapping for OPR and OffsetPair
Advantages of This Approach
Unified Query for Multiple Types:
Single execution for all types using UNION ALL.
Flexibility:
Easily extendable for new types or enrichment fields.
Efficient Mapping:
Map results to appropriate POJOs in the application layer using the type column.
This approach ensures consistency in SQL output and simplifies post-processing in the application. Let me know if further clarification or optimizations are needed!






You said:
but how will we get data off all the trades/fpr/opr/offsets in single shot
ChatGPT said:
ChatGPT
To fetch data for all trades, FPRs, OPRs, and offsets in a single shot, we can optimize the SQL query and design to minimize database trips and ensure efficient processing. Here's how:

Steps to Fetch All Data in a Single Query
1. Use a Common Data Structure
Ensure a uniform structure for all entities (Trade, FPR, OPR, and OffsetPair) by combining them using UNION ALL. You can include a discriminator column (e.g., type) to identify the type of each record.

2. Pre-Enrichment
For each type (Trade, FPR, OPR, OffsetPair), enrich the data by joining required tables within a WITH clause or subqueries. This avoids multiple trips to the database.

3. Query Example
Here’s an optimized query:

sql
Copy
Edit
WITH EnrichedTrades AS (
    SELECT
        t.trade_id AS id,
        'Trade' AS type,
        t.trade_type AS trade_type,
        NULL AS fpr_field,
        NULL AS opr_field,
        NULL AS offset_field,
        e.enrichment_field1 AS enrichment_field1,
        e.enrichment_field2 AS enrichment_field2
    FROM
        Trades t
    LEFT JOIN EnrichmentTable e ON t.trade_id = e.trade_id
),
EnrichedFPRs AS (
    SELECT
        f.fpr_id AS id,
        'FPR' AS type,
        NULL AS trade_type,
        f.other_field AS fpr_field,
        NULL AS opr_field,
        NULL AS offset_field,
        e.enrichment_field1 AS enrichment_field1,
        e.enrichment_field2 AS enrichment_field2
    FROM
        FPRs f
    LEFT JOIN EnrichmentTable e ON f.fpr_id = e.trade_id
),
EnrichedOPRs AS (
    SELECT
        o.opr_id AS id,
        'OPR' AS type,
        NULL AS trade_type,
        NULL AS fpr_field,
        o.other_field AS opr_field,
        NULL AS offset_field,
        e.enrichment_field1 AS enrichment_field1,
        e.enrichment_field2 AS enrichment_field2
    FROM
        OPRs o
    LEFT JOIN EnrichmentTable e ON o.opr_id = e.trade_id
),
EnrichedOffsetPairs AS (
    SELECT
        o.offset_id AS id,
        'OffsetPair' AS type,
        NULL AS trade_type,
        NULL AS fpr_field,
        NULL AS opr_field,
        CONCAT(t1.trade_id, '-', t2.trade_id) AS offset_field,
        e1.enrichment_field1 AS enrichment_field1,
        e2.enrichment_field2 AS enrichment_field2
    FROM
        OffsetPairs o
    LEFT JOIN Trades t1 ON o.buy_trade_id = t1.trade_id
    LEFT JOIN Trades t2 ON o.sell_trade_id = t2.trade_id
    LEFT JOIN EnrichmentTable e1 ON t1.trade_id = e1.trade_id
    LEFT JOIN EnrichmentTable e2 ON t2.trade_id = e2.trade_id
)
SELECT * FROM EnrichedTrades
UNION ALL
SELECT * FROM EnrichedFPRs
UNION ALL
SELECT * FROM EnrichedOPRs
UNION ALL
SELECT * FROM EnrichedOffsetPairs;
How This Works
Pre-Enrich Data: Each type (Trade, FPR, OPR, OffsetPair) has its own enrichment logic in the WITH clauses. This ensures all necessary data is fetched in a single query.

Combine All Data with UNION ALL: Use UNION ALL to combine all types into a single result set.

Add a Discriminator Column: The type column helps distinguish between Trade, FPR, OPR, and OffsetPair records.

Application-Level Handling
Once the query returns the result set, handle it in your application logic by mapping the records to respective data models based on the type column.

Example in Java (Using Spring and JDBC Template):
java
Copy
Edit
public List<Object> fetchAllData() {
    String sql = "WITH EnrichedTrades AS ( ... ) SELECT * FROM ...";

    return jdbcTemplate.query(sql, (rs, rowNum) -> {
        String type = rs.getString("type");
        switch (type) {
            case "Trade":
                return mapToTrade(rs);
            case "FPR":
                return mapToFPR(rs);
            case "OPR":
                return mapToOPR(rs);
            case "OffsetPair":
                return mapToOffsetPair(rs);
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    });
}

private Trade mapToTrade(ResultSet rs) throws SQLException {
    return new Trade(
        rs.getString("id"),
        rs.getString("trade_type"),
        rs.getString("enrichment_field1"),
        rs.getString("enrichment_field2")
    );
}

private FPR mapToFPR(ResultSet rs) throws SQLException {
    return new FPR(
        rs.getString("id"),
        rs.getString("fpr_field"),
        rs.getString("enrichment_field1"),
        rs.getString("enrichment_field2")
    );
}

private OPR mapToOPR(ResultSet rs) throws SQLException {
    return new OPR(
        rs.getString("id"),
        rs.getString("opr_field"),
        rs.getString("enrichment_field1"),
        rs.getString("enrichment_field2")
    );
}

private OffsetPair mapToOffsetPair(ResultSet rs) throws SQLException {
    return new OffsetPair(
        rs.getString("id"),
        rs.getString("offset_field"),
        rs.getString("enrichment_field1"),
        rs.getString("enrichment_field2")
    );
}
Optimizations
Batch Queries: Use an IN clause with a batch of 100 trade_ids to fetch only relevant data.

sql
Copy
Edit
WHERE trade_id IN (?, ?, ?, ..., ?)
Indexes: Ensure all join and filter columns (e.g., trade_id, fpr_id, opr_id, offset_id) are indexed for faster lookups.

Database Connection Pooling: Use a connection pool (e.g., HikariCP) to manage database connections efficiently, especially for large queries.

Parallel Enrichment: If enrichment involves significant processing, consider splitting the result set by type and processing each type in parallel.

Why This Works
Single Query: Avoids multiple database calls by fetching all data in a single shot.
Efficient Mapping: Reduces application-level complexity by structuring the result set uniformly.
Scalability: Handles batch sizes effectively by leveraging SQL optimizations (e.g., WITH clauses, UNION ALL).
Let me know if you'd like further assistance!






You said:
but even opr/fpr/offsets will have their own ids
ChatGPT said:
ChatGPT
You're absolutely correct that FPRs, OPRs, and OffsetPairs will have their own IDs distinct from the trades they reference. We can design the query to include their unique IDs along with the respective enrichment and relationships while maintaining a uniform result structure. Below is a refined version of the SQL query that handles the unique IDs for each type:

Refined SQL Query
sql
Copy
Edit
WITH EnrichedTrades AS (
    SELECT
        t.trade_id AS entity_id,  -- Unique ID for Trade
        'Trade' AS type,          -- Discriminator column
        t.trade_type AS trade_type,
        NULL AS fpr_id,
        NULL AS opr_id,
        NULL AS offset_id,
        e.enrichment_field1 AS enrichment_field1,
        e.enrichment_field2 AS enrichment_field2
    FROM
        Trades t
    LEFT JOIN EnrichmentTable e ON t.trade_id = e.trade_id
),
EnrichedFPRs AS (
    SELECT
        f.fpr_id AS entity_id,    -- Unique ID for FPR
        'FPR' AS type,            -- Discriminator column
        NULL AS trade_type,
        f.fpr_id AS fpr_id,
        NULL AS opr_id,
        NULL AS offset_id,
        e.enrichment_field1 AS enrichment_field1,
        e.enrichment_field2 AS enrichment_field2
    FROM
        FPRs f
    LEFT JOIN EnrichmentTable e ON f.fpr_id = e.trade_id
),
EnrichedOPRs AS (
    SELECT
        o.opr_id AS entity_id,    -- Unique ID for OPR
        'OPR' AS type,            -- Discriminator column
        NULL AS trade_type,
        NULL AS fpr_id,
        o.opr_id AS opr_id,
        NULL AS offset_id,
        e.enrichment_field1 AS enrichment_field1,
        e.enrichment_field2 AS enrichment_field2
    FROM
        OPRs o
    LEFT JOIN EnrichmentTable e ON o.opr_id = e.trade_id
),
EnrichedOffsetPairs AS (
    SELECT
        o.offset_id AS entity_id, -- Unique ID for OffsetPair
        'OffsetPair' AS type,     -- Discriminator column
        NULL AS trade_type,
        NULL AS fpr_id,
        NULL AS opr_id,
        o.offset_id AS offset_id,
        e1.enrichment_field1 AS enrichment_field1,
        e2.enrichment_field2 AS enrichment_field2
    FROM
        OffsetPairs o
    LEFT JOIN Trades t1 ON o.buy_trade_id = t1.trade_id
    LEFT JOIN Trades t2 ON o.sell_trade_id = t2.trade_id
    LEFT JOIN EnrichmentTable e1 ON t1.trade_id = e1.trade_id
    LEFT JOIN EnrichmentTable e2 ON t2.trade_id = e2.trade_id
)
SELECT * FROM EnrichedTrades
UNION ALL
SELECT * FROM EnrichedFPRs
UNION ALL
SELECT * FROM EnrichedOPRs
UNION ALL
SELECT * FROM EnrichedOffsetPairs;
Explanation
Unique ID for Each Entity Type:

entity_id is the primary identifier for each row in the result set, corresponding to trade_id, fpr_id, opr_id, or offset_id, depending on the type.
Each type has its unique identifier included in the result.
Discriminator Column (type):

The type column differentiates rows in the result set as Trade, FPR, OPR, or OffsetPair.
Nulls for Non-Relevant Columns:

Fields that are not applicable for a given type (e.g., trade_type for FPRs) are set to NULL to maintain consistency across rows.
Enrichment for OffsetPairs:

For OffsetPairs, enrichment happens for the buy_trade_id and sell_trade_id by joining the relevant Trades and EnrichmentTable records.
Single Result Set:

The UNION ALL combines all enriched data into a single result set.
Sample Output
entity_id	type	trade_type	fpr_id	opr_id	offset_id	enrichment_field1	enrichment_field2
101	Trade	FX	NULL	NULL	NULL	value1	value2
202	FPR	NULL	202	NULL	NULL	value3	value4
303	OPR	NULL	NULL	303	NULL	value5	value6
404	OffsetPair	NULL	NULL	NULL	404	value7	value8
How to Process in Code
In your application (e.g., Java with Spring), you can map this unified result set into distinct objects using the type column:

java
Copy
Edit
public List<Object> fetchAllData() {
    String sql = "WITH EnrichedTrades AS (...) SELECT * FROM ...";

    return jdbcTemplate.query(sql, (rs, rowNum) -> {
        String type = rs.getString("type");
        switch (type) {
            case "Trade":
                return mapToTrade(rs);
            case "FPR":
                return mapToFPR(rs);
            case "OPR":
                return mapToOPR(rs);
            case "OffsetPair":
                return mapToOffsetPair(rs);
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    });
}
Optimizations
Batching with Filters:

If the IDs (e.g., trade_ids) are passed as input, filter them using:
sql
Copy
Edit
WHERE trade_id IN (?, ?, ?, ..., ?)
Indexes:

Ensure indexes on all ID fields (trade_id, fpr_id, opr_id, offset_id) and join columns for faster lookups.
Pagination:

Use LIMIT and OFFSET to paginate large result sets if the number of rows is huge.
Parallel Processing:

If enriching each type involves heavy computation, split the result set by type and process each subset in parallel.
Let me know if you'd like a deeper dive into implementation!






You said:
can you give the entire code, consider that even the trades/fpr/opr/offset needs to be enriched along with the trades they have
ChatGPT said:
ChatGPT
Trade Enrichment Sql
The above SQL script creates a unified enriched dataset combining data from trades, FPRs, OPRs, and OffsetPairs while also enriching them. Let me know if you'd like additional details or modifications!






You said:
i mean entire code with java
ChatGPT said:
ChatGPT
Trade Enrichment Sql
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
import java.sql.*;
import java.util.*;

public class TradeEnrichmentService {

    private static final String DB_URL = "jdbc:your_database_url";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    public static void main(String[] args) {
        List<Map<String, Object>> enrichedData = fetchAndEnrichData();
        enrichedData.forEach(System.out::println);
    }

    public static List<Map<String, Object>> fetchAndEnrichData() {
        String query = """
            SELECT 'TRADE' AS type, t.trade_id, t.field1, t.field2, e.enriched_field1, e.enriched_field2
            FROM trades t
            LEFT JOIN enrichment_table e ON t.trade_id = e.trade_id
            UNION ALL
            SELECT 'FPR' AS type, f.fpr_id, f.field1, f.field2, e.enriched_field1, e.enriched_field2
            FROM fprs f
            LEFT JOIN enrichment_table e ON f.fpr_id = e.fpr_id
            UNION ALL
            SELECT 'OPR' AS type, o.opr_id, o.field1, o.field2, e.enriched_field1, e.enriched_field2
            FROM oprs o
            LEFT JOIN enrichment_table e ON o.opr_id = e.opr_id
            UNION ALL
            SELECT 'OFFSET' AS type, off.offset_id, off.field1, off.field2, e.enriched_field1, e.enriched_field2
            FROM offsets off
            LEFT JOIN enrichment_table e ON off.offset_id = e.offset_id
            """;

        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                results.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}

Edited
I’ve added a Java program that fetches and enriches data from multiple sources (trades, FPR, OPR, offsets) in one go. The SQL query combines these datasets using UNION ALL, and the Java code handles data retrieval and enrichment into a unified structure. Let me know if you need further refinement or additional features!






You said:
where will the trade/fpr/opr/offset ids go?
ChatGPT said:
ChatGPT
In the current code, each dataset (Trade, FPR, OPR, and Offset) already includes its respective IDs (trade_id, fpr_id, opr_id, offset_id) as part of the UNION ALL query.

Key Points:
Identification Field:

Each record's type is identified with a type column (TRADE, FPR, OPR, OFFSET).
The relevant ID field (trade_id, fpr_id, opr_id, or offset_id) is retrieved based on the type.
Storage in Result:

The fetchAndEnrichData method collects these IDs in a Map alongside other fields.
When the result set is processed in Java, the column names (trade_id, fpr_id, etc.) will still reflect the original data source.
Example for Results:
A sample record retrieved might look like:

json
Copy
Edit
{
    "type": "TRADE",
    "trade_id": 12345,
    "field1": "value1",
    "field2": "value2",
    "enriched_field1": "enrichedValue1",
    "enriched_field2": "enrichedValue2"
}
For FPR:

json
Copy
Edit
{
    "type": "FPR",
    "fpr_id": 67890,
    "field1": "value3",
    "field2": "value4",
    "enriched_field1": "enrichedValue3",
    "enriched_field2": "enrichedValue4"
}
Further Refinement:
If you need to dynamically map IDs based on the type:

Add logic in the Java code to determine the specific ID field (trade_id, fpr_id, etc.) based on the type column.
Example:
java
Copy
Edit
String idField = switch (row.get("type").toString()) {
    case "TRADE" -> "trade_id";
    case "FPR" -> "fpr_id";
    case "OPR" -> "opr_id";
    case "OFFSET" -> "offset_id";
    default -> "unknown_id";
};
Object id = row.get(idField);
