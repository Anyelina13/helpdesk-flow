package com.utn.helpdesk;

import com.utn.helpdesk.model.Incidencia;
import com.utn.helpdesk.model.Impacto;
import com.utn.helpdesk.model.Urgencia;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IncidenciaTest {

    @Test
    void tituloNoPuedeEstarVacio() {
        assertThrows(IllegalArgumentException.class, () ->
            new Incidencia("", "Descripcion valida con mas de 10 caracteres", null, null, null)
        );
    }

    @Test
    void descripcionDebeTenerAlMenosDiezCaracteres() {
        assertThrows(IllegalArgumentException.class, () ->
            new Incidencia("Titulo valido", "corta", null, null, null)
        );
    }

    @Test
    void impactoYUrgenciaQuedanCorrectamenteAsignados() {
        Incidencia i = new Incidencia("Titulo valido", "Descripcion valida larga", null, Impacto.ALTO, Urgencia.ALTA);
        assertEquals(Impacto.ALTO, i.getImpacto());
        assertEquals(Urgencia.ALTA, i.getUrgencia());
    }
}
