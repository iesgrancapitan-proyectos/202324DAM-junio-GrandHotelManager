package es.iesgrancapitan.proyectohotelfx;

import es.iesgrancapitan.proyectohotelfx.controllers.LoginController;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Clase ReportGenerator

 */
public class ReportGenerator {

    /**
     * Método para generar un informe de factura en formato PDF
     * y subirlo al servidor
     * @param idReserva
     */
    public void generateReport(int idReserva) {
        try {
            // Crear un cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Crear una solicitud HTTP a la API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + LoginController.IP + "/hotel/info-reserva.php"))
                    .POST(HttpRequest.BodyPublishers.ofString("token_auth=0de6e41e85570bfcf0afc59179b6f480&idReserva=" + idReserva))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            // Enviar la solicitud y obtener la respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Convertir la respuesta en un InputStream
            InputStream is = new ByteArrayInputStream(response.body().getBytes(StandardCharsets.UTF_8));

            // Crear un JRDataSource a partir de la respuesta JSON
            JRDataSource dataSource = new JsonDataSource(is);

            // Compilar el archivo .jrxml a .jasper
            InputStream reportStream = getClass().getResourceAsStream("/jasper/Factura2GrandHotel.jrxml");
            JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            // Llenar el informe con los datos
            JasperPrint print = JasperFillManager.fillReport(jasperReport, null, dataSource);

            // Exportar el informe a un archivo PDF
            String outputFileName = String.format("factura%d.pdf",idReserva);
            JasperExportManager.exportReportToPdfFile(print, outputFileName);
            System.out.println("Report generated: " + outputFileName);

            // Crear un cliente HTTP
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // Crear una solicitud HTTP a la API
            HttpPost uploadFile = new HttpPost("http://" + LoginController.IP + "/hotel/subir-factura.php");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("token_auth", "0de6e41e85570bfcf0afc59179b6f480", ContentType.TEXT_PLAIN);
            builder.addTextBody("idReserva", String.valueOf(idReserva), ContentType.TEXT_PLAIN);

            // Adjuntar el archivo PDF a la solicitud POST
            File f = new File(outputFileName);
            builder.addBinaryBody(
                    "pdf",
                    new FileInputStream(f),
                    ContentType.APPLICATION_OCTET_STREAM,
                    f.getName()
            );

            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);

            CloseableHttpResponse uploadResponse = httpClient.execute(uploadFile);
            HttpEntity responseEntity = uploadResponse.getEntity();
            System.out.println("Upload response: " + EntityUtils.toString(responseEntity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}