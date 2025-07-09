insert into k_fagomrade (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
select 'AKT',
       'Aktivitetsplan med dialoger',
       current_timestamp,
       'MMA-8208',
       null,
       null
from dual
where not exists(select 1 from k_fagomrade where k_fagomrade = 'AKT');
insert into k_fagomrade (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
select 'UNG',
       'Ungdomsprogramytelsen',
       current_timestamp,
       'MMA-8208',
       null,
       null
from dual
where not exists(select 1 from k_fagomrade where k_fagomrade = 'UNG');
insert into k_fagomrade (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
select 'PAI',
       'Innsyn',
       current_timestamp,
       'MMA-8208',
       null,
       null
from dual
where not exists(select 1 from k_fagomrade where k_fagomrade = 'PAI');
insert into k_fagomrade (k_fagomrade, dekode, opprettet_dato, opprettet_av, endret_dato, endret_av)
select 'POI',
       'Innsyn etter personopplysningsloven',
       current_timestamp,
       'MMA-8208',
       null,
       null
from dual
where not exists(select 1 from k_fagomrade where k_fagomrade = 'POI');
