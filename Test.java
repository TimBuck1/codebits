WITH MODIFIED_alpha AS (
    -- Identify new or modified alphas
    SELECT tr.id AS alphaid
    FROM alpha tr
    LEFT JOIN charge ch ON tr.id = ch.alphaid
    WHERE ch.alphaid IS NULL OR tr.uuid <> ch.uuid
),
COMBINED_DATA AS (
    -- Fetch alpha-related records from beta, gamma, and delta
    SELECT mt.alphaid, 'alpha' AS source, tr.*
    FROM MODIFIED_alpha mt
    JOIN alpha tr ON mt.alphaid = tr.id
    
    UNION ALL
    
    SELECT mt.alphaid, 'beta' AS source, beta.*
    FROM MODIFIED_alpha mt
    JOIN option beta ON mt.alphaid = beta.alpha
    
    UNION ALL
    
    SELECT mt.alphaid, 'gamma' AS source, gamma.*
    FROM MODIFIED_alpha mt
    JOIN future gamma ON mt.alphaid = gamma.alpha
    
    UNION ALL
    
    SELECT mt.alphaid, 'delta' AS source, ofs.*
    FROM MODIFIED_alpha mt
    JOIN delta ofs ON mt.alphaid = ofs.alpha
)

SELECT * FROM COMBINED_DATA;
