package com.example.acceso.service.Implements;

import com.example.acceso.service.Interfaces.ServicioAutenticacionDosPasos;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import dev.samstevens.totp.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServicioAutenticacionDosPasosImpl implements ServicioAutenticacionDosPasos {

    public String generarNuevoSecreto() {
        SecretGenerator secretGenerator = new DefaultSecretGenerator(64);
        return secretGenerator.generate();
    }

    public String generarUriDatosQr(String secreto, String email, String issuer) {
        QrData data = new QrData.Builder()
                .label(email)
                .secret(secreto)
                .issuer(issuer)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData;
        try {
            imageData = generator.generate(data);
        } catch (QrGenerationException e) {
            throw new RuntimeException("Error al generar el código QR", e);
        }

        return Utils.getDataUriForImage(imageData, generator.getImageMimeType());
    }

    public boolean esCodigoValido(String secreto, String codigo) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        return verifier.isValidCode(secreto, codigo);
    }
}
