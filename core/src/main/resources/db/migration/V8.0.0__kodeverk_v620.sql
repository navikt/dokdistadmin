insert into K_DOKUMENT_STATUS (k_dokument_status, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
  select
    'RETURPOSTBEHANDLET',
    'Returpostbehandlet',
    timestamp '2018-04-06 10:00:00',
    'Maria Sølvberg',
    NULL,
    NULL
  from dual
  where not exists(select 1
                   from K_DOKUMENT_STATUS
                   where k_dokument_status = 'RETURPOSTBEHANDLET');