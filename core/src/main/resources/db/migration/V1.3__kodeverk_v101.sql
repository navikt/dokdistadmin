-- Dokumentdistribusjon_Kodeverk-101
insert into K_KANAL_BEHANDLING (k_kanal_behandling,dekode,opprettet_dato,opprettet_av,endret_dato,endret_av) VALUES ('B_C5_X_S_UP','B_C5_X_S_UP',timestamp '2013-12-17 10:00:00','T. Fiksdal',NULL,NULL);
insert into K_KANAL_BEHANDLING (k_kanal_behandling,dekode,opprettet_dato,opprettet_av,endret_dato,endret_av) VALUES ('P_PK_E_D_UP','P_PK_E_D_UP',timestamp '2013-12-17 10:00:00','T. Fiksdal',NULL,NULL);
update K_BEST_FAGSYSTEM set k_best_fagsystem='IT', endret_dato=timestamp '2013-11-17 10:00:00', endret_av='T. Fiksdal' where k_best_fagsystem='INFOTRYGD';
update K_BREV_PROD_APP set k_brev_prod_app='IT_TRYGD', endret_dato=timestamp '2013-11-17 10:00:00', endret_av='T. Fiksdal' where k_brev_prod_app='INFOTRYGD';
update K_BREV_PROD_APP set k_brev_prod_app='ADHOC_ADHOC', endret_dato=timestamp '2013-11-17 10:00:00', endret_av='T. Fiksdal' where k_brev_prod_app='ADHOC';
insert into K_FIL_TYPE (k_fil_type,dekode,opprettet_dato,opprettet_av,endret_dato,endret_av) VALUES ('KVITTERING_PRINT','Kvittering fra print',timestamp '2013-12-17 10:00:00','T. Fiksdal',NULL,NULL);

