update k_fagomrade
set k_fagomrade = 'FIP',
    endret_dato = current_timestamp,
    endret_av   = 'MMA-6815'
where k_fagomrade = 'FIB';