package com.duoc.TicketMaster.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            // Asegurar que la carpeta en EFS exista
            File dir = new File(EFS_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Instanciar el writer una sola vez en el stream en memoria (evita corrupción de PDF)
            PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();

            // Estilos y contenido profesional del Ticket (Cumple con los requerimientos del profesor)
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font bodyFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);

            document.add(new Paragraph("TICKET OFICIAL - TICKETMASTER", titleFont));
            document.add(new Paragraph("--------------------------------------------------", bodyFont));
            document.add(new Paragraph("Ticket ID: #" + ticketId, boldFont));
            document.add(new Paragraph("Evento: " + eventoNombre, bodyFont));
            document.add(new Paragraph("Artista / Equipo: " + artista, bodyFont));
            document.add(new Paragraph("Ubicación: " + ubicacion, bodyFont));
            document.add(new Paragraph("Precio: $" + precio + " CLP", bodyFont));
            document.add(new Paragraph("Comprador: " + usuarioEmail, bodyFont));
            document.add(new Paragraph("--------------------------------------------------", bodyFont));
            document.add(new Paragraph("¡Conserve este ticket para el ingreso al recinto!", bodyFont));

            document.close();

            // Obtener los bytes limpios del PDF
            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            // Guardar físicamente el archivo en la ruta de EFS
            String fileName = "ticket_" + ticketId + ".pdf";
            Path filePath = Paths.get(EFS_PATH, fileName);
            Files.write(filePath, pdfBytes);

            return pdfBytes;

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo PDF del ticket en EFS: " + e.getMessage(), e);
        }
    }
}

//Prueba de Git push