INSERT INTO K_FAGOMRADE (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'RVE', 'Rettferdsvederlag', timestamp '2018-08-30 12:30:00', 'Joakim Bjørnstad', NULL, NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_FAGOMRADE WHERE k_fagomrade = 'RVE');

INSERT INTO K_FAGOMRADE (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'RPO', 'Retting av personopplysninger', timestamp '2018-08-30 12:30:00', 'Joakim Bjørnstad', NULL, NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_FAGOMRADE WHERE k_fagomrade = 'RPO');

INSERT INTO K_FAGOMRADE (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'FAR', 'Farskap', timestamp '2018-08-30 12:30:00', 'Joakim Bjørnstad', NULL, NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_FAGOMRADE WHERE k_fagomrade = 'FAR');
