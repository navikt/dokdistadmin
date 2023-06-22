-- Dokumentdistribusjon_Kodeverk-106
delete from K_BREV_PROD_APP where k_brev_prod_app = 'UR_LT';
delete from K_BREV_PROD_APP where k_brev_prod_app = 'UR_VLONN';
insert into K_KANAL_BEHANDLING (k_kanal_behandling,dekode,opprettet_dato,opprettet_av,endret_dato,endret_av) VALUES ('P_PK_E_S_UP','P_PK_E_S_UP',timestamp '2014-02-13 10:00:00','J. Bjørnstad',NULL,NULL);
