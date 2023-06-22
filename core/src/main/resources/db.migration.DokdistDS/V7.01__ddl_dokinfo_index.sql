declare
    already_exists exception;
    pragma exception_init( already_exists, -955 );
begin
    execute immediate 'CREATE INDEX XIN1DOKUMENT_INFO ON DOKUMENT_INFO (K_ARKIV_SYSTEM ASC, K_DOKUMENT_STATUS ASC, AVSTEMT_ARKIV_DATO ASC, DOKUMENT_INFO_ID ASC, 1 ASC)';
exception
    when already_exists then
    null;
end;
/