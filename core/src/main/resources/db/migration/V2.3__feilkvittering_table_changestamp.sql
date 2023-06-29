ALTER TABLE FEILKVITTERING ADD (
	opprettet_dato       TIMESTAMP NOT NULL ,
	opprettet_av         VARCHAR2(20) NOT NULL ,
	endret_dato          TIMESTAMP NULL ,
	endret_av            VARCHAR2(20) NULL
);