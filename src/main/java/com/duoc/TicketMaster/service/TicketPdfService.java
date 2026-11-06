package com.duoc.TicketMaster.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Servicio para generar el archivo PDF real del ticket y gestionarlo
 * en el almacenamiento temporal EFS de la aplicación.
 */
@Service
public class TicketPdfService {

    // Ruta base donde se montará el EFS en el contenedor Docker (/app/efs)
    private final String EFS_PATH = "/app/efs/tickets";

    /**
     * Genera un archivo PDF físico con los detalles del ticket y lo guarda en EFS.
     */
    public byte[] generarPdfTicket(String eventoNombre, String artista, String ubicacion, Double precio, String usuarioEmail, Long ticketId) {
        Document document = new Document();
        ByteArrayOutputStreamEFS byteArrayOutputStream = new ByteArrayOutputStreamEFS();

        try {
            // Asegurar que la carpeta en EFS exista
            File dir = new File(EFS_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = "ticket_" + ticketId + ".pdf";
            String filePath = EFS_PATH + "/" + fileName;

            // Crear el PDF tanto localmente en EFS como en memoria
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();

            // Estilos y contenido profesional del Ticket
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font bodyFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

            document.add(new Paragraph("TICKET OFICIAL - TICKETMASTER", titleFont));
            document.add(new Paragraph("--------------------------------------------------", bodyFont));
            document.add(new Paragraph("Ticket ID: #" + ticketId, bodyFont));
            document.add(new Paragraph("Evento: " + eventoNombre, bodyFont));
            document.add(new Paragraph("Artista / Equipo: " + artista, bodyFont));
            document.add(new Paragraph("Ubicación: " + ubicacion, bodyFont));
            document.add(new Paragraph("Precio: $" + precio, bodyFont));
            document.add(new Paragraph("Comprador: " + usuarioEmail, bodyFont));
            document.add(new Paragraph("--------------------------------------------------", bodyFont));
            document.add(new Paragraph("¡Conserve este ticket para el ingreso al recinto!", bodyFont));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo PDF del ticket en EFS: " + e.getMessage(), e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Clase auxiliar interna para capturar el PDF en bytes y enviarlo a S3.
     */
    private static class ByteArrayOutputStreamEFS extends java.io.ByteArrayOutputStream {}
}