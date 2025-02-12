alter table dokument_info
    rename column forsendelse_metadata to forsendelse_metadata_old_version_to_be_deleted;

alter table dokument_info
    set unused (forsendelse_metadata_old_version_to_be_deleted);

 alter table dokument_info
    add forsendelse_metadata clob;