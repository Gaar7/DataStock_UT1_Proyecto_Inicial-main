package es.iesisidra.datastock.ui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import es.iesisidra.datastock.modelo.Producto;
import es.iesisidra.datastock.servicio.Inventario;
import es.iesisidra.datastock.util.ColoresConsola;
import es.iesisidra.datastock.util.LectorConsola;

/**
 * Presentación de menús y navegación de la primera fase de DataStock.
 * <p>
 * Los métodos marcados TODO son puntos de partida, NO soluciones de los
 * requisitos. El alumnado deberá delegar el acceso a ficheros, la validación
 * y el procesamiento en métodos o clases independientes.
 * </p>
 */
public class MenuPrincipal {
    private final Inventario inventario;

    /** @param inventario colección común de productos de esta ejecución. */
    public MenuPrincipal(Inventario inventario) {
        this.inventario = inventario;
    }

    /** Ejecuta el menú principal hasta que el usuario selecciona salir. */
    public void iniciar() {
        int opcion;
        do {
            mostrarCabecera();
            System.out.println("1. Explorar ficheros y directorios");
            System.out.println("2. Importar productos desde JSON");
            System.out.println("3. Importar productos desde XML");
            System.out.println("4. Consultar productos");
            System.out.println("5. Exportar productos");
            System.out.println("6. Guardar y recuperar inventario");
            System.out.println("0. Salir");
            opcion = LectorConsola.leerOpcion("Selecciona una opción: ", 0, 6);
            switch (opcion) {
                case 1:
                    System.out.println("Introduce la ruta a explorar: ");
                    String ruta = LectorConsola.leerTexto("Introduce la ruta");
                    ExploradorFicheros.explorarRuta(ruta);
                    LectorConsola.pausar();
                    break;
                case 2:
                    System.out.println("Introduce la ruta del JSON: ");
                    String rutaJson = LectorConsola.leerTexto("Introduce la ruta");
                    inventario.importarDesdeJson(rutaJson);
                    LectorConsola.pausar();
                    break;
                case 3:
                    System.out.println("Introduce la ruta del XML: ");
                    String rutaXml = LectorConsola.leerTexto("Introduce la ruta: ");
                    inventario.importarDesdeXml(rutaXml);
                    LectorConsola.pausar();
                    break;
                case 4:
                    menuConsultas();
                case 5:
                    menuExportaciones();
                case 6:
                    menuSerializacion();
                case 0:
                    System.out.println(ColoresConsola.pintar("Hasta pronto.", ColoresConsola.VERDE));
                default:
                    throw new IllegalStateException("Opción inesperada");
            }
        } while (opcion != 0);
    }

    private void mostrarCabecera() {
        System.out.println(
                ColoresConsola.pintar("\n================================================", ColoresConsola.CIAN));
        System.out.println(ColoresConsola.pintar("         DATASTOCK - GESTIÓN DE INVENTARIO", ColoresConsola.NEGRITA));
        System.out.println(ColoresConsola.pintar("                 FASE 1 - UT1", ColoresConsola.CIAN));
        System.out.println(
                ColoresConsola.pintar("================================================", ColoresConsola.CIAN));
        System.out.println(ColoresConsola.pintar("Productos en memoria: " + inventario.getProductos().size(),
                ColoresConsola.GRIS));
    }

    private void mostrarProducto(es.iesisidra.datastock.modelo.Producto p) {
        System.out.println("--------------------------------");
        System.out.println("Código: " + p.getCodigo());
        System.out.println("Nombre: " + p.getNombre());
        System.out.println("Precio: " + p.getPrecio() + " €");
        System.out.println("Stock: " + p.getStock());
        System.out.println("Categoría: " + p.getCategoriaId());
        System.out.println("Proveedor: " + p.getProveedorId());
    }

    private void menuConsultas() {
        int opcion;

        do {
            System.out.println("\n---------- CONSULTA DE PRODUCTOS ----------");
            System.out.println("1. Listar todos los productos");
            System.out.println("2. Buscar producto por código");
            System.out.println("3. Buscar productos por nombre");
            System.out.println("0. Volver al menú principal");

            opcion = LectorConsola.leerOpcion(
                    "Selecciona una opción: ", 0, 3);

            switch (opcion) {
                case 1 -> {
                    if (inventario.getProductos().isEmpty()) {
                        System.out.println("No hay productos en el inventario.");
                    } else {
                        System.out.println("\n===== LISTADO DE PRODUCTOS =====");

                        for (Producto p : inventario.obtenerProductosOrdenados()) {
                            mostrarProducto(p);
                        }
                    }
                }

                case 2 -> {
                    String codigo = LectorConsola.leerTexto(
                            "Introduce el código del producto: ");

                    Producto p = inventario.buscarPorCodigo(codigo);

                    if (p != null) {
                        System.out.println("\n===== PRODUCTO ENCONTRADO =====");
                        mostrarProducto(p);
                    } else {
                        System.out.println("No existe ningún producto con ese código.");
                    }
                }

                case 3 -> {
                    String nombre = LectorConsola.leerTexto(
                            "Introduce el texto a buscar en el nombre: ");

                    if (nombre.trim().isEmpty()) {
                        System.out.println("Debes introducir un texto para buscar.");
                    } else {
                        var encontrados = inventario.buscarPorNombre(nombre);

                        if (encontrados.isEmpty()) {
                            System.out.println("No se encontraron productos.");
                        } else {
                            System.out.println("\n===== PRODUCTOS ENCONTRADOS =====");

                            for (Producto p : encontrados) {
                                mostrarProducto(p);
                            }

                            System.out.println("Total encontrados: "
                                    + encontrados.size());
                        }
                    }
                }

                case 0 -> {
                    System.out.println("Volviendo al menú principal...");
                }
            }

        } while (opcion != 0);
    }

    private void menuExportaciones() {
        int opcion;

        do {
            System.out.println("\n---------- EXPORTAR PRODUCTOS ----------");
            System.out.println("1. Exportar productos a JSON");
            System.out.println("2. Exportar productos a XML");
            System.out.println("0. Volver al menú principal");

            opcion = LectorConsola.leerOpcion(
                    "Selecciona una opción: ", 0, 2);

            switch (opcion) {
                case 1 -> gestionarExportacion(true);
                case 2 -> gestionarExportacion(false);
                case 0 -> System.out.println("Volviendo al menú principal...");
            }

            if (opcion != 0) {
                LectorConsola.pausar();
            }

        } while (opcion != 0);
    }

    /**
     * Solicita la ruta y gestiona la exportación elegida.
     * 
     * @param json true para JSON y false para XML.
     */
    private void gestionarExportacion(boolean json) {
        if (inventario.getProductos().isEmpty()) {
            System.out.println("No hay productos para exportar.");
        } else {
            String formato = json ? "JSON" : "XML";

            String rutaTexto = LectorConsola.leerTexto(
                    "Introduce la ruta de destino del " + formato + ": ");

            try {
                Path ruta = Paths.get(rutaTexto);

                boolean sobrescribir = false;

                if (Files.exists(ruta)) {
                    sobrescribir = LectorConsola.confirmar(
                            "El fichero ya existe. ¿Deseas sobrescribirlo?");
                }

                if (!Files.exists(ruta) || sobrescribir) {
                    if (json) {
                        inventario.exportarAJson(rutaTexto, sobrescribir);
                    } else {
                        inventario.exportarAXml(rutaTexto, sobrescribir);
                    }
                } else {
                    System.out.println("Exportación cancelada.");
                }

            } catch (Exception e) {
                System.out.println("Error en la ruta de destino: "
                        + e.getMessage());
            }
        }
    }

    private void gestionarGuardado() {
        String ruta = LectorConsola.leerTexto(
                "Introduce la ruta del fichero .dat: ");

        try {
            Path destino = Paths.get(ruta);

            boolean sobrescribir = false;

            if (Files.exists(destino)) {
                sobrescribir = LectorConsola.confirmar(
                        "El fichero ya existe. ¿Deseas sobrescribirlo?");
            }

            if (!Files.exists(destino) || sobrescribir) {
                inventario.guardarInventario(ruta);
            } else {
                System.out.println("Guardado cancelado.");
            }

        } catch (Exception e) {
            System.out.println("Error en la ruta: " + e.getMessage());
        }
    }

    private void gestionarRecuperacion() {
        if (inventario.getProductos().isEmpty()) {
            recuperarDesdeFichero();
        } else {
            boolean confirmar = LectorConsola.confirmar(
                    "Se sustituirá el inventario actual. ¿Continuar?");

            if (confirmar) {
                recuperarDesdeFichero();
            } else {
                System.out.println("Recuperación cancelada.");
            }
        }
    }

    private void recuperarDesdeFichero() {
        String ruta = LectorConsola.leerTexto(
                "Introduce la ruta del fichero .dat: ");

        try {
            inventario.recuperarInventario(ruta);
        } catch (Exception e) {
            System.out.println("Error en la ruta: " + e.getMessage());
        }
    }

    private void menuSerializacion() {
        int opcion;

        do {
            System.out.println("----------------------------------------------");
            System.out.println("GUARDAR Y RECUPERAR INVENTARIO");
            System.out.println("----------------------------------------------");
            System.out.println("1. Guardar inventario en fichero .dat");
            System.out.println("2. Recuperar inventario desde fichero .dat");
            System.out.println("0. Volver al menú principal");
            System.out.println("----------------------------------------------");

            opcion = LectorConsola.leerOpcion(
                    "Selecciona una opción: ", 0, 2);

            switch (opcion) {
                case 1 -> gestionarGuardado();
                case 2 -> gestionarRecuperacion();
                case 0 -> System.out.println(
                        "Volviendo al menú principal...");
            }

            if (opcion != 0) {
                LectorConsola.pausar();
            }

        } while (opcion != 0);
    }

    /**
     * Sustituir las llamadas por servicios propios a medida que se implementen los
     * RF.
     */
    private void pendiente(String requisito) {
        System.out.println(ColoresConsola.pintar("[PENDIENTE] " + requisito, ColoresConsola.AMARILLO));
        System.out.println("Esta funcionalidad debe desarrollarla el alumnado.");
        LectorConsola.pausar();
    }
}
