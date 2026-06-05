package com.mycompany.banguate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TipoCambio {

    private static final Logger LOGGER = Logger.getLogger(TipoCambio.class.getName());
    private static final String URL_BANGUAT = "https://www.banguat.gob.gt/variables/ws/TipoCambio.asmx";

    private final HttpClient httpClient;

    public TipoCambio() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String getTipoCambioDia() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        String soapBody =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<soap:Envelope " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
              "<soap:Body>" +
                "<TipoCambioDia xmlns=\"http://www.banguat.gob.gt/variables/ws/\" />" +
              "</soap:Body>" +
            "</soap:Envelope>";

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
            throw new IOException("Error Banguat. Status: " + response.statusCode());
        }
    }

    private String parsearRespuesta(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        NodeList fechaNode      = doc.getElementsByTagName("fecha");
        NodeList referenciaNode = doc.getElementsByTagName("referencia");
        NodeList ventaNode      = doc.getElementsByTagName("venta");
        NodeList compraNode     = doc.getElementsByTagName("compra");

        String fecha      = fechaNode.item(0)      != null ? fechaNode.item(0).getTextContent()      : "N/A";
        String referencia = referenciaNode.item(0) != null ? referenciaNode.item(0).getTextContent() : "N/A";
        String venta      = ventaNode.item(0)      != null ? ventaNode.item(0).getTextContent()      : "N/A";
        String compra     = compraNode.item(0)     != null ? compraNode.item(0).getTextContent()     : "N/A";

        return String.format(
            "{\"fecha\":\"%s\",\"referencia\":\"%s\",\"venta\":\"%s\",\"compra\":\"%s\"}",
            fecha, referencia, venta, compra
        );
    }

    public double getReferencia() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        String json = getTipoCambioDia();
        String ref = json.replaceAll(".*\"referencia\":\"([^\"]+)\".*", "$1");
        return Double.parseDouble(ref);
    }
}