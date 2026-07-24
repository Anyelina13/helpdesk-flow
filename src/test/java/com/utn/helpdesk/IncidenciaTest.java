package com.utn.helpdesk;

import com.utn.helpdesk.model.Incidencia;
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
}
