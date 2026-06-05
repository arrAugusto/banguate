package com.mycompany.banguate;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class Banguate {

    private static final Logger LOGGER = Logger.getLogger(Banguate.class.getName());

    public static void main(String[] args) throws InterruptedException {
        try {
            TipoCambio tc = new TipoCambio();
            String resultado = tc.getTipoCambioDia();
            LOGGER.info(resultado);
            double referencia = tc.getReferencia();
            LOGGER.info("Q" + referencia);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error de red al consultar Banguat", ex);
        } catch (ParserConfigurationException | SAXException ex) {
            LOGGER.log(Level.SEVERE, "Error al parsear respuesta XML", ex);
        }
    }
}