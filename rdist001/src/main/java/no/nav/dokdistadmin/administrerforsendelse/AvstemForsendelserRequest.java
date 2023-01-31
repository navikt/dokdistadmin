package no.nav.dokdistadmin.administrerforsendelse;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvstemForsendelserRequest {

    @NotEmpty
    private String avstemtReferanse;

    @NotNull
    private List<Forsendelse> forsendelser;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Forsendelse {
        private String forsendelseId;
    }


}
