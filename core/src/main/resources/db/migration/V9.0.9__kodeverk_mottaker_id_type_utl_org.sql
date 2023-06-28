INSERT INTO K_MOTTAKER_ID_TYPE (k_mottaker_id_type,dekode,opprettet_dato,opprettet_av,endret_dato,endret_av)
 SELECT
  'SAMHANDLER_UTL_ORG',
  'Organisasjon utland',
  timestamp '2019-05-23 13:00:00',
  'Olav Thorsen',
  NULL,
  NULL
 FROM dual
  WHERE NOT EXISTS (SELECT 1
                   FROM K_MOTTAKER_ID_TYPE
                   WHERE k_mottaker_id_type = 'SAMHANDLER_UTL_ORG');