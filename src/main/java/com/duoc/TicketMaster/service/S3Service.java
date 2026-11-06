package com.duoc.TicketMaster.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Servicio para sincronizar la subida, descarga, actualización y eliminación
 * de los archivos PDF de los tickets en el bucket de AWS S3.
 */
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    /**
     * Sube el PDF del ticket a S3 bajo la estructura: /eventoId/usuarioX/ticketId.pdf
     */
    public void subirTicketS3(String eventoId, String usuarioEmail, Long ticketId, byte[] pdfBytes) {
        String s3Key = "evento" + eventoId + "/usuario" + usuarioEmail + "/ticket" + ticketId + ".pdf";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(pdfBytes.length);
        metadata.setContentType("application/pdf");

        InputStream inputStream = new ByteArrayInputStream(pdfBytes);
        amazonS3.putObject(bucketName, s3Key, inputStream, metadata);
    }

    /**
     * Descarga el flujo de datos del ticket desde S3.
     */
    public InputStream descargarTicketS3(String eventoId, String usuarioEmail, Long ticketId) {
        String s3Key = "evento" + eventoId + "/usuario" + usuarioEmail + "/ticket" + ticketId + ".pdf";
        S3Object s3Object = amazonS3.getObject(bucketName, s3Key);
        return s3Object.getObjectContent();
    }

    /**
     * Elimina el ticket específico de S3.
     */
    public void borrarTicketS3(String eventoId, String usuarioEmail, Long ticketId) {
        String s3Key = "evento" + eventoId + "/usuario" + usuarioEmail + "/ticket" + ticketId + ".pdf";
        amazonS3.deleteObject(bucketName, s3Key);
    }
}