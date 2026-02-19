package no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal;

import jakarta.jms.Queue;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.exception.functional.UgyldigInputException;
import no.nav.dokdistadmin.exception.technical.DistribusjonTechnicalException;
import no.nav.meldinger.virksomhet.dokdistfordeling.qdist008.out.DistribuerTilKanal;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Slf4j
@Service
public class DistribusjonService {

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(DistribuerTilKanal.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("Feil ved instansiering av JAXBContext", e);
        }
    }

    private final JmsTemplate jmsTemplate;
    private final Queue printQueue;
    private final Queue dittnavQueue;
    private final Queue sdpQueue;
    private final Queue dpvtQueue;

    public DistribusjonService(JmsTemplate jmsTemplate, Queue printQueue, Queue dittnavQueue, Queue sdpQueue, Queue dpvtQueue) {
        this.jmsTemplate = jmsTemplate;
        this.printQueue = printQueue;
        this.dittnavQueue = dittnavQueue;
        this.sdpQueue = sdpQueue;
        this.dpvtQueue = dpvtQueue;
    }

    public void distribuerTilKanal(Long forsendelseId, DistribusjonKanalCode kanal) {
        Queue queue = bestemQueue(kanal);

        try {
            DistribuerTilKanal melding = new DistribuerTilKanal();
            melding.setForsendelseId(forsendelseId.toString());

            String xmlMelding = toXml(melding);
            jmsTemplate.convertAndSend(queue, xmlMelding);
        } catch (JAXBException e) {
            log.error("Feil ved marshalling av melding for forsendelseId={}", forsendelseId, e);
            throw new DistribusjonTechnicalException("Feil ved marshalling av melding", e);
        } catch (JmsException e) {
            log.error("Feil ved sending av melding til kø={} for forsendelseId={}", kanal, forsendelseId, e);
            throw new DistribusjonTechnicalException("Feil ved sending av melding til kø", e);
        }
    }

    private String toXml(DistribuerTilKanal melding) throws JAXBException {
        Marshaller marshaller = JAXB_CONTEXT.createMarshaller();

        StringWriter writer = new StringWriter();
        marshaller.marshal(melding, writer);
        return writer.toString();
    }

    private Queue bestemQueue(DistribusjonKanalCode kanal) {
        return switch (kanal) {
            case PRINT -> printQueue;
            case DITTNAV -> dittnavQueue;
            case SDP -> sdpQueue;
            case DPVT -> dpvtQueue;
            default ->
                throw new UgyldigInputException("Kan ikke bestemme kø for kanal '%s'".formatted(kanal));
        };
    }

}

