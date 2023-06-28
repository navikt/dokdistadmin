INSERT INTO K_FAGOMRADE (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
SELECT 'FRI',
       'Midlertidig kompensasjonsordning for selvstendig næringsdrivende og frilansere',
       timestamp '2020-04-30 12:00:00',
       'Joakim Bjørnstad',
       NULL,
       NULL
FROM DUAL
WHERE NOT EXISTS(SELECT 1 FROM K_FAGOMRADE WHERE k_fagomrade = 'FRI');
