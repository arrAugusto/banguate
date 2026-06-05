package com.mycompany.banguate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;

public class tipoCambio {

    private static final String URL_BANGUAT = "https://www.banguat.gob.gt/variables/ws/TipoCambio.asmx";

    private final HttpClient httpClient;

    public tipoCambio() {
        this.httpClient = HttpClient.newHttpClient();
    }

    // ── Consulta el tipo de cambio del día ────────────────────
    public String getTipoCambioDia() throws Exception {

        String soapBody
                = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
                + "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soap:Body>"
                + "<TipoCambioDia xmlns=\"http://www.banguat.gob.gt/variables/ws/\" />"
                + "</soap:Body>"
                + "</soap:Envelope>";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BANGUAT))
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", "\"http://www.banguat.gob.gt/variables/ws/TipoCambioDia\"")
                .POST(HttpRequest.BodyPublishers.ofString(soapBody))
                .build();

        HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 200) {
            return parsearRespuesta(response.body());
        } else {
            throw new RuntimeException("Error Banguat. Status: " + response.statusCode());
        }
    }

    // ── Parsea el XML de respuesta ─────────────────────────────
    private String parsearRespuesta(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        // Tipo de cambio dólar (referencia)
        NodeList fechaNode = doc.getElementsByTagName("fecha");
        NodeList referenciaNode = doc.getElementsByTagName("referencia");

        String fecha = fechaNode.item(0) != null ? fechaNode.item(0).getTextContent() : "N/A";
        String referencia = referenciaNode.item(0) != null ? referenciaNode.item(0).getTextContent() : "N/A";

        // Compra y venta de otras monedas
        NodeList ventaNode = doc.getElementsByTagName("venta");
        NodeList compraNode = doc.getElementsByTagName("compra");

        String venta = ventaNode.item(0) != null ? ventaNode.item(0).getTextContent() : "N/A";
        String compra = compraNode.item(0) != null ? compraNode.item(0).getTextContent() : "N/A";

        return String.format(
                "{\"fecha\":\"%s\",\"referencia\":\"%s\",\"venta\":\"%s\",\"compra\":\"%s\"}",
                fecha, referencia, venta, compra
        );
    }

    // ── Retorna solo el valor de referencia como double ────────
    public double getReferencia() throws Exception {
        String json = getTipoCambioDia();
        // Extrae el valor de "referencia" del JSON
        String ref = json.replaceAll(".*\"referencia\":\"([^\"]+)\".*", "$1");
        return Double.parseDouble(ref);
    }
}
