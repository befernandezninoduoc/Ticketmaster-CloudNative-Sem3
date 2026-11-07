package com.duoc.TicketMaster.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    private final String bucketName = "ticketmaster-resumenes-benja";

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    // 1. Subir PDF del ticket (usado por CompraService)
    public void subirTicketS3(String eventoId, String usuarioEmail, Long ticketId, byte[] pdfBytes) {
        String key = eventoId + "/" + usuarioEmail + "/ticket_" + ticketId + ".pdf";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(pdfBytes.length);
        metadata.setContentType("application/pdf");

        amazonS3.putObject(new PutObjectRequest(bucketName, key, new ByteArrayInputStream(pdfBytes), metadata));
    }

    // 2. Descargar PDF del ticket (usado por TicketController)
    public InputStream descargarTicketS3(String eventoId, String usuarioEmail, Long ticketId) {
        String key = eventoId + "/" + usuarioEmail + "/ticket_" + ticketId + ".pdf";
        S3Object s3Object = amazonS3.getObject(bucketName, key);
        return s3Object.getObjectContent();
    }

    // 3. Borrar PDF del ticket (usado por TicketController)
    public void borrarTicketS3(String eventoId, String usuarioEmail, Long ticketId) {
        String key = eventoId + "/" + usuarioEmail + "/ticket_" + ticketId + ".pdf";
        amazonS3.deleteObject(bucketName, key);
    }

    // 4. Métodos para resúmenes (usados por ResumenController)
    public void subirResumen(Long compraId, String contenido) {
        String key = "resumenes/" + compraId + "/detalle_compra.txt";
        byte[] bytes = contenido.getBytes(StandardCharsets.UTF_8);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType("text/plain; charset=utf-8");

        amazonS3.putObject(new PutObjectRequest(bucketName, key, new ByteArrayInputStream(bytes), metadata));
    }

    public InputStream descargarResumen(Long compraId) {
        String key = "resumenes/" + compraId + "/detalle_compra.txt";
        S3Object s3Object = amazonS3.getObject(bucketName, key);
        return s3Object.getObjectContent();
    }

    public void modificarResumen(Long compraId, String contenido) {
        subirResumen(compraId, contenido);
    }

    public void borrarResumen(Long compraId) {
        String key = "resumenes/" + compraId + "/detalle_compra.txt";
        amazonS3.deleteObject(bucketName, key);
    }
}