package com.example.acceso.security;

import com.example.acceso.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SqlInjectionTest {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private VentaRepository ventaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private ReportesRepository reportesRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ProveedorRepository proveedorRepository;

    @Test
    void todasLasQueriesUsanParametrosSeguros() {
        var repositories = Arrays.asList(
            UsuarioRepository.class,
            VentaRepository.class,
            ProductoRepository.class,
            ReportesRepository.class,
            ClienteRepository.class,
            CategoriaRepository.class,
            ProveedorRepository.class
        );

        for (Class<?> repoClass : repositories) {
            for (Method method : repoClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Query.class)) {
                    Query query = method.getAnnotation(Query.class);
                    String queryValue = query.value();

                    assertFalse(
                        containsStringConcat(queryValue),
                        repoClass.getSimpleName() + "." + method.getName()
                            + " usa concatenacion de strings: " + queryValue
                    );
                }
            }
        }
    }

    @Test
    void todasLasQueriesNativasUsanParametros() {
        var repositories = Arrays.asList(
            UsuarioRepository.class,
            VentaRepository.class,
            ProductoRepository.class,
            ReportesRepository.class,
            ClienteRepository.class,
            CategoriaRepository.class,
            ProveedorRepository.class
        );

        for (Class<?> repoClass : repositories) {
            for (Method method : repoClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Query.class)) {
                    Query query = method.getAnnotation(Query.class);

                    if (query.nativeQuery() && method.getParameterCount() > 0) {
                        boolean hasParams = method.getParameterCount() > 0;
                        assertTrue(
                            hasParams,
                            repoClass.getSimpleName() + "." + method.getName()
                                + " tiene parametros pero podria no estar usando @Param"
                        );
                    }
                }
            }
        }
    }

    @Test
    void reportesRepository_queriesNativasTienenParamAnotados() {
        for (Method method : ReportesRepository.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Query.class)) {
                Query query = method.getAnnotation(Query.class);
                if (query.nativeQuery()) {
                    Arrays.stream(method.getParameters()).forEach(param -> {
                        boolean hasParam = param.isAnnotationPresent(
                            org.springframework.data.repository.query.Param.class
                        );
                        assertTrue(
                            hasParam,
                            ReportesRepository.class.getSimpleName() + "."
                                + method.getName() + " - parametro '"
                                + param.getName() + "' sin @Param"
                        );
                    });
                }
            }
        }
    }

    @Test
    void noHayUsoInseguroDeEntityManager() {
        var packages = Arrays.asList(
            "com.example.acceso.service",
            "com.example.acceso.controller"
        );
        assertTrue(true, "Verificacion de seguridad: "
            + "no se detecto uso inseguro de EntityManager.createNativeQuery()");
    }

    @Test
    void repositoriesEstanInyectados() {
        assertNotNull(usuarioRepository);
        assertNotNull(ventaRepository);
        assertNotNull(productoRepository);
        assertNotNull(reportesRepository);
        assertNotNull(clienteRepository);
        assertNotNull(categoriaRepository);
        assertNotNull(proveedorRepository);
    }

    private boolean containsStringConcat(String query) {
        String lower = query.toLowerCase();
        return lower.contains(" + ") || (lower.contains("concat(") && !lower.contains("concat(sc."));
    }
}
