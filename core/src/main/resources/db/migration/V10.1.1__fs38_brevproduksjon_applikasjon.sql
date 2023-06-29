INSERT INTO k_brev_prod_app (k_brev_prod_app, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'FS38', 'Melosys', TIMESTAMP '2019-10-30 17:00:00', 'Joakim Bjørnstad', NULL, NULL
FROM dual
WHERE NOT EXISTS(SELECT 1 FROM k_brev_prod_app WHERE k_brev_prod_app = 'FS38');
