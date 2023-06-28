INSERT INTO K_DIST_KANAL (k_dist_kanal, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'TRYGDERETTEN', 'Trygderetten', timestamp '2019-06-03 16:00:00', 'E.Braten', NULL, NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_DIST_KANAL WHERE k_dist_kanal = 'TRYGDERETTEN');
