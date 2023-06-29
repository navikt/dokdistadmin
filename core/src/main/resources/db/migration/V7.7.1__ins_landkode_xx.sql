begin
  insert into k_landkode_post_dest (k_landkode, post_dest, opprettet_dato, opprettet_av)
  values ('XX','VERDEN', sysdate, 'Ketill Fenne');
exception
  when dup_val_on_index then null;
end;