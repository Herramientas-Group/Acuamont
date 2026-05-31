package com.example.acceso.service.Implements;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String subirImagen(MultipartFile file, String carpetaNombre) {
        try {
            Map params = ObjectUtils.asMap(
                    "folder", carpetaNombre,
                    "resource_type", "image");

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Error al subir imagen a Cloudinary: " + e.getMessage());
        }
    }

    public Map eliminarImagen(String id, Map options) {
        try {
            return cloudinary.uploader().destroy(id, options);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar imagen de Cloudinary");
        }
    }

    public List<String> listarImagenesDeCarpeta(String nombreCarpeta) {
        try {
            ApiResponse response = cloudinary.api().resources(ObjectUtils.asMap(
                    "type", "upload",
                    "prefix", nombreCarpeta + "/",
                    "max_results", 10
            ));

            List<Map> resources = (List<Map>) response.get("resources");

            return resources.stream()
                    .map(res -> res.get("secure_url").toString())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error listing images from Cloudinary folder: {}", nombreCarpeta, e);
            return new ArrayList<>();
        }
    }

    public String subirImagenConNombreFijo(MultipartFile file, String nombreFijo) {
        try {
            Map params = ObjectUtils.asMap(
                    "public_id", nombreFijo,
                    "folder", "iconos",
                    "overwrite", true,
                    "resource_type", "image"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Error al subir logo: " + e.getMessage());
        }
    }

    public String obtenerUrlImagen(String publicId) {
        try {
            return cloudinary.url().secure(true).generate("iconos/" + publicId);
        } catch (Exception e) {
            return null;
        }
    }

}
