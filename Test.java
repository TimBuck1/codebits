WHERE NOT EXISTS (
        SELECT 1
        FROM entity e
        WHERE e.bid = opr.trade
          AND e.type = 'O'
          AND e.action <> 'delete'
    )
