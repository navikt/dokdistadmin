INSERT INTO K_FAGOMRADE (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'EYB',
       'Barnepensjon',
       current_timestamp,
       'MMA-6055',
       NULL,
       NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_FAGOMRADE WHERE k_fagomrade = 'EYB');
INSERT INTO K_FAGOMRADE (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'EYO',
       'Omstillingsstønad',
       current_timestamp,
       'MMA-6055',
       NULL,
       NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_FAGOMRADE WHERE k_fagomrade = 'EYO');
