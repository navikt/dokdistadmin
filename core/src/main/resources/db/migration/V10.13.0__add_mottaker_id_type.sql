INSERT INTO K_MOTTAKER_ID_TYPE (k_mottaker_id_type,dekode,opprettet_dato,opprettet_av,endret_dato,endret_av)
SELECT
    'SAMHANDLER_UKJENT',
    'Ukjent mottakertype',
    timestamp '2022-06-27 13:00:00',
    'Håkon Snøtun',
    NULL,
    NULL
FROM dual
WHERE NOT EXISTS (SELECT 1
                  FROM K_MOTTAKER_ID_TYPE
                  WHERE k_mottaker_id_type = 'SAMHANDLER_UKJENT');