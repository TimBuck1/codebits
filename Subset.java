SELECT
    t1.ID,
    t1.FEEID,
    CASE WHEN t1.ID = t1.AMOUNTID THEN t1.UPSCALE ELSE NULL END AS "GROSS SCALE",
    t1.AMOUNTID,
    t1.RATE,
    t1.ALGO
FROM
    your_table t1
JOIN
    your_table t2 ON t1.FEEID = t2.FEEID
WHERE
    t1.FEEID = 'your_specific_feeid';
