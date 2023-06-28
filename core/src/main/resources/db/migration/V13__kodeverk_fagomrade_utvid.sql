INSERT INTO K_FAGOMRADE (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'KTA',
       'Kontroll anmeldelse',
       current_timestamp,
       'MMA-6718',
       NULL,
       NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_FAGOMRADE WHERE k_fagomrade = 'KTA');
INSERT INTO K_FAGOMRADE (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'FIB',
       'Fiskerpensjon',
       current_timestamp,
       'MMA-6718',
       NULL,
       NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_FAGOMRADE WHERE k_fagomrade = 'FIB');
INSERT INTO K_FAGOMRADE (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'ARS',
       'Arbeidsrådgivning skjermet',
       current_timestamp,
       'MMA-6718',
       NULL,
       NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_FAGOMRADE WHERE k_fagomrade = 'ARS');
INSERT INTO K_FAGOMRADE (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'ARP',
       'Arbeidsrådgivning psykologtester',
       current_timestamp,
       'MMA-6718',
       NULL,
       NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_FAGOMRADE WHERE k_fagomrade = 'ARP');
INSERT INTO K_FAGOMRADE (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'KLL',
       'Klage lønnsgaranti',
       current_timestamp,
       'MMA-6718',
       NULL,
       NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_FAGOMRADE WHERE k_fagomrade = 'KLL');