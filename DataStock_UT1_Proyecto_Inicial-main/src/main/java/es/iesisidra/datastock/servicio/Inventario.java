package es.iesisidra.datastock.servicio;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import es.iesisidra.datastock.modelo.Producto;

/**
 * Estado común del inventario durante la ejecución de UT1.
 */
public class Inventario {

    private final List<Producto> productos = new ArrayList<>();

    public List<Producto> getProductos() {
        return productos;
    }

    // Clase auxiliar interna para Gson (correctamente aislada)
    private static class ContenedorJson {

        private List<Producto> productos;

        public List<Producto> getProductos() {
            return productos;
        }
    }

    /**
     * Importa productos desde un fichero JSON cumpliendo las reglas de la UT1.
     */
    public void importarDesdeJson(String rutaFichero) {
        Path ruta = Paths.get(rutaFichero);

        if (!Files.exists(ruta) || !Files.isRegularFile(ruta)) {
            System.out.println("Error: El fichero no existe o no es un fichero válido.");
            return;
        }

        Gson gson = new Gson();
        ContenedorJson contenedor;

        try (BufferedReader reader = Files.newBufferedReader(ruta)) {
            contenedor = gson.fromJson(reader, ContenedorJson.class);
            if (contenedor == null || contenedor.getProductos() == null) {
                System.out.println(
                        "Error: El fichero JSON no tiene la estructura correcta (falta la clave 'productos').");
                return;
            }
        } catch (JsonSyntaxException e) {
            System.out.println("Error: El fichero JSON está mal formado y no se puede interpretar[cite: 16].");
            return;
        } catch (IOException e) {
            System.out.println("Error de E/S al leer el fichero: " + e.getMessage());
            return;
        }

        int encontrados = contenedor.getProductos().size();
        int importados = 0;
        int rechazados = 0;
        StringBuilder incidencias = new StringBuilder();

        for (Producto p : contenedor.getProductos()) {
            String codigo = p.getCodigo() != null ? p.getCodigo().trim() : "";

            boolean esValido = true;
            String motivo = "";

            if (codigo.isEmpty() || codigo.length() > 12) {
                esValido = false;
                motivo = "el código es obligatorio y máximo de 12 caracteres";
            } else if (p.getNombre() == null || p.getNombre().trim().length() < 3 || p.getNombre().length() > 100) {
                esValido = false;
                motivo = "el nombre debe tener entre 3 y 100 caracteres";
            } else if (p.getPrecio() == null || p.getPrecio().signum() <= 0) {
                esValido = false;
                motivo = "el precio debe ser mayor que 0";
            } else if (p.getStock() < 0) {
                esValido = false;
                motivo = "el stock no puede ser negativo";
            } else {

                boolean duplicado = false;

                for (Producto existente : productos) {
                    if (existente.getCodigo() != null
                            && existente.getCodigo().trim()
                                    .equalsIgnoreCase(codigo)) {
                        duplicado = true;
                    }
                }

            }

            if (esValido) {
                p.setCodigo(codigo);
                productos.add(p);
                importados++;
            } else {
                rechazados++;
                incidencias.append(" - Producto ").append(codigo.isEmpty() ? "desconocido" : codigo).append(": ")
                        .append(motivo).append("\n");
            }
        }

        System.out.println("\n===== IMPORTAR JSON =====");
        System.out.println("Productos encontrados: " + encontrados);
        System.out.println("Importados: " + importados + " | Rechazados: " + rechazados);
        if (rechazados > 0) {
            System.out.println("INCIDENCIAS:\n" + incidencias.toString());
        }
        System.out.println("Importación finalizada.");
    }

    /*
     * Método importarDesdeXml que:
     * - Compruebe que el fichero existe
     * - Lo lea mediante DOM
     * - Obtener los productos del documento
     * - Validar cada producto
     * - Añadir los válidos a produtos
     * - Mostrar el informe de importación
     */

    /**
     * Importa productos desde un fichero XML utilizando DOM.
     */
    public void importarDesdeXml(String rutaFichero) {
        File fichero = new File(rutaFichero);

        if (!fichero.exists() || !fichero.isFile()) {
            System.out.println("Error: El fichero no existe o no es válido.");
            return;
        }

        Document documento;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Protección frente a entidades externas
            factory.setFeature(
                    XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(
                    "http://apache.org/xml/features/disallow-doctype-decl",
                    true);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            documento = builder.parse(fichero);
            documento.getDocumentElement().normalize();

        } catch (Exception e) {
            System.out.println("Error al leer el XML: " + e.getMessage());
            return;
        }

        NodeList listaProductos = documento.getDocumentElement()
                .getElementsByTagName("producto");

        int encontrados = listaProductos.getLength();
        int importados = 0;
        int rechazados = 0;

        StringBuilder incidencias = new StringBuilder();

        for (int i = 0; i < encontrados; i++) {
            Element elemento = (Element) listaProductos.item(i);

            String codigo = "";
            try {
                codigo = elemento.getElementsByTagName("codigo")
                        .item(0).getTextContent().trim();

                String nombre = elemento.getElementsByTagName("nombre")
                        .item(0).getTextContent().trim();

                BigDecimal precio = new BigDecimal(
                        elemento.getElementsByTagName("precio")
                                .item(0).getTextContent().trim());

                int stock = Integer.parseInt(
                        elemento.getElementsByTagName("stock")
                                .item(0).getTextContent().trim());

                int categoriaId = Integer.parseInt(
                        elemento.getElementsByTagName("categoriaId")
                                .item(0).getTextContent().trim());

                int proveedorId = Integer.parseInt(
                        elemento.getElementsByTagName("proveedorId")
                                .item(0).getTextContent().trim());

                Producto p = new Producto(
                        codigo, nombre, precio, stock,
                        categoriaId, proveedorId);

                // Validación de los datos
                String motivo = "";

                if (codigo.isEmpty() || codigo.length() > 12) {
                    motivo = "el código es obligatorio y máximo de 12 caracteres";
                } else if (nombre.length() < 3 || nombre.length() > 100) {
                    motivo = "el nombre debe tener entre 3 y 100 caracteres";
                } else if (precio.signum() <= 0) {
                    motivo = "el precio debe ser mayor que 0";
                } else if (stock < 0) {
                    motivo = "el stock no puede ser negativo";
                } else {

                    boolean duplicado = false;

                    for (Producto existente : productos) {
                        if (existente.getCodigo() != null
                                && existente.getCodigo().trim()
                                        .equalsIgnoreCase(codigo)) {
                            duplicado = true;
                        }
                    }

                    if (duplicado) {
                        motivo = "código duplicado";
                    }
                }

                if (motivo.isEmpty()) {
                    productos.add(p);
                    importados++;
                } else {
                    rechazados++;
                    incidencias.append(" - Producto ")
                            .append(codigo.isEmpty() ? "desconocido" : codigo)
                            .append(": ").append(motivo).append("\n");
                }

            } catch (Exception e) {
                rechazados++;
                incidencias.append(" - Producto ")
                        .append(codigo.isEmpty() ? "desconocido" : codigo)
                        .append(": datos incompletos o formato numérico incorrecto\n");
            }
        }

        System.out.println("\n===== IMPORTAR XML =====");
        System.out.println("Productos encontrados: " + encontrados);
        System.out.println("Importados: " + importados
                + " | Rechazados: " + rechazados);

        if (rechazados > 0) {
            System.out.println("INCIDENCIAS:\n" + incidencias);
        }

        System.out.println("Importación finalizada.");
    }

}