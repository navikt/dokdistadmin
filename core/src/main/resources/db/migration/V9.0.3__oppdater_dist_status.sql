INSERT INTO K_DIST_STATUS (k_dist_status,dekode,opprettet_dato,opprettet_av,endret_dato,endret_av)
 SELECT
  'EKSPEDERT',
  'Ekspedert',
  timestamp '2019-04-09 10:00:00',
  'Erik Bråten',
  NULL,
  NULL
 FROM dual
  WHERE NOT EXISTS (SELECT 1
                   FROM K_DIST_STATUS
                   WHERE k_dist_status = 'EKSPEDERT');