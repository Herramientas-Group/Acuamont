package com.example.acceso.service.Implements;

import com.example.acceso.model.Venta;
import com.example.acceso.service.Interfaces.GenerarBoletaService;
import com.example.acceso.service.Interfaces.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerarBoletaServiceImpl implements GenerarBoletaService {

    private final TemplateEngine templateEngine;
    private final VentaService ventaService;
    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.fromName}")
    private String mailFromName;

    @Transactional(readOnly = true)
    public byte[] generarBoletaPdf(Long ventaId) throws Exception {

        Venta venta = ventaService.obtenerVenta(ventaId);

        Context context = new Context();
        context.setVariable("venta", venta);

        String html = templateEngine.process("Boleta/plantilla_Comprobante", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();

        String baseUrl = "File:/";
        renderer.setDocumentFromString(html, baseUrl);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }

    @Transactional(readOnly = true)
    public String enviarBoletaPorCorreo(Long ventaId) throws Exception {

        Venta venta = ventaService.obtenerVenta(ventaId);
        if (venta.getCliente() == null || venta.getCliente().getCorreo() == null || venta.getCliente().getCorreo().isEmpty()) {
            throw new RuntimeException("El cliente no tiene un correo electrónico.");
        }

        String correoCliente = venta.getCliente().getCorreo();
        String correlativoFormateado = String.format("%09d", venta.getCorrelativo());
        String numeroBoleta = venta.getSerieComprobante().getSerie() + "-" + correlativoFormateado;

        byte[] pdfBytes = generarBoletaPdf(ventaId);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(mailFrom, mailFromName);
        helper.setTo(correoCliente);
        helper.setSubject("Comprobante de Venta Acuamont: " + numeroBoleta);
        helper.setText("Adjuntamos su comprobante de venta en PDF.", false);
        helper.addAttachment("boleta_" + numeroBoleta + ".pdf", new ByteArrayResource(pdfBytes));

        mailSender.send(message);

        return correoCliente;
    }

}
