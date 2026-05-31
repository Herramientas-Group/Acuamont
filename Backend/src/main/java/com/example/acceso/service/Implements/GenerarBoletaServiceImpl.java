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

    @Transactional
    public String enviarBoletaPorCorreo(Long ventaId) throws Exception {

        Venta venta = ventaService.obtenerVenta(ventaId);
        if (venta.getCliente() == null || venta.getCliente().getCorreo() == null || venta.getCliente().getCorreo().isEmpty()) {
            throw new RuntimeException("El cliente no tiene un correo electrónico.");
        }

        String correoCliente = venta.getCliente().getCorreo();
        String correlativoFormateado = String.format("%09d", venta.getCorrelativo());
        String numeroBoleta = venta.getSerieComprobante().getSerie() + "-" + correlativoFormateado;

        Context context = new Context();
        context.setVariable("venta", venta);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setFrom("johnchm007@gmail.com", "Acuamont S.A.C.");
        helper.setTo(correoCliente);
        helper.setSubject("Comprobante de Venta Acuamont: " + numeroBoleta);

        String htmlBody = templateEngine.process("boleta/plantilla_Comprobante", context);

        helper.setText(htmlBody, true);

        mailSender.send(message);

        return correoCliente;
    }

}
