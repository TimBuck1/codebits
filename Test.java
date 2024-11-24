SELECT t.*
FROM Trade t
LEFT JOIN (
  SELECT c.chargetradeid
  FROM Charge c
  WHERE c.status <> 'DELETED' -- Exclude charges with only 'DELETED' status
) active_charges
  ON t.id = active_charges.chargetradeid
WHERE 
  -- Case 1: The trade's versionid doesn't match with the charge's versionid
  EXISTS (
    SELECT 1
    FROM Charge c
    WHERE c.chargetradeid = t.id
      AND c.versionid <> t.versionid
      AND c.status <> 'DELETED'
  )

  -- Case 2: The trade is new and has no corresponding non-deleted charge
  OR active_charges.chargetradeid IS NULL;
