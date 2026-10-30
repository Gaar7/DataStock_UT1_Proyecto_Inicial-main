**DataStock: Sistema de Gestion de Inventario**

**Finalidad del Proyecto**

DataStock es una aplicacion desarrollada en lenguaje Java orientada a la gestion de inventarios y catalogos de productos. Su finalidad academica y practica, enmarcada en el modulo de Acceso a Datos, consiste en aplicar los principios de arquitectura modular, separacion de responsabilidades y patron de diseño por capas. El sistema permite manipular informacion estructurada mediante diferentes fuentes de persistencia y ficheros de intercambio (JSON y XML), garantizando la integridad de los datos y un desacoplamiento estricto entre la interfaz de usuario, la logica de negocio y los modelos del dominio.

**Estructura del Proyecto e Interconexion entre Clases**

El codigo fuente se organiza bajo un esquema estandar de Maven, estructurado en paquetes que definen claramente las responsabilidades de cada componente:

* es.iesisidra.datastock
* App.java: Clase principal que contiene el punto de entrada de la aplicacion y se encarga de instanciar y lanzar el menu principal.


* es.iesisidra.datastock.modelo
* Producto.java: Entidad central que define las caracteristicas principales de los articulos almacenados en el inventario.
* Categoria.java y Proveedor.java: Clases que representan el dominio relacional de clasificacion y suministro de los productos.
* MovimientoStock.java: Clase encargada de registrar las modificaciones y operaciones asociadas al stock.


* es.iesisidra.datastock.servicio
* Inventario.java: Componente central de la logica de negocio. Mantiene la coleccion de productos en memoria y centraliza los metodos de validacion, importacion, busqueda y exportacion, desacoplando estas operaciones de la interfaz de usuario.


* es.iesisidra.datastock.ui
* MenuPrincipal.java: Gestiona la interaccion textual con el usuario mediante consola, presentando los submenus y delegando las operaciones correspondientes en la clase Inventario.
* ExploradorFicheros.java: Proporciona las herramientas de navegacion y recorrido de directorios y ficheros del sistema de archivos.


* es.iesisidra.datastock.util
* LectorConsola.java y ColoresConsola.java: Clases de apoyo transversal que facilitan la lectura segura de datos introducidos por el usuario y el formato visual en la terminal.


* es.iesisidra.datastock.complementarias
* XPathActividad.java: Modulo ejecutable independiente disenado para realizar consultas sobre ficheros XML mediante expresiones XPath.
* AccesoAleatorioActividad.java: Modulo ejecutable independiente destinado al manejo de registros de longitud fija mediante ficheros de acceso aleatorio.



**Requisitos para su Ejecucion**

Para asegurar el correcto funcionamiento, compilacion y ejecucion del proyecto, es necesario disponer de los siguientes elementos:

* Java Development Kit (JDK) en version 17 o superior.
* Apache Maven para la gestion del ciclo de vida del proyecto y resolucion de dependencias externas especificadas en el fichero pom.xml.
* Un entorno de desarrollo integrado compatible, tal como Visual Studio Code con el paquete de extensiones de Java o IntelliJ IDEA.

**Instrucciones de Ejecucion**

* Clonar o descomprimir el codigo fuente del proyecto en el directorio de trabajo local.
* Abrir una terminal en la raiz del proyecto donde se encuentra el fichero pom.xml y ejecutar la compilacion mediante la orden mvn clean compile.
* Para ejecutar la aplicacion principal, seleccione y ejecute la clase es.iesisidra.datastock.App, la cual iniciara la consola interactiva del menu de gestion.
* Para ejecutar de forma independiente las actividades complementarias tecnicas, ejecute las clases es.iesisidra.datastock.complementarias.XPathActividad o es.iesisidra.datastock.complementarias.AccesoAleatorioActividad segun corresponda.

**Funcionalidades Implementadas**

* Exploracion de ficheros y directorios (RF-FIC-01): Funcionalidad integrada para la navegacion y verificacion de rutas y ficheros dentro del sistema de almacenamiento.
* Importacion de productos desde JSON (RF-FIC-02): Carga y deserializacion de catalogos mediante Gson, aplicando validaciones globales de estructura, control de integridad de campos y deteccion de codigos duplicados.
* Importacion de productos desde XML (RF-FIC-03): Procesamiento de ficheros XML basados en DOM parser, validando esquemas y filtrando registros de manera individualizada antes de su insercion en el inventario.
* Consultas mediante XPath (RF-FIC-07): Actividad complementaria independiente que permite realizar busquedas y extraccion de informacion estructurada sobre ficheros XML sin alterar el estado del inventario principal.
* Acceso aleatorio con ficheros binarios (RF-FIC-08): Actividad complementaria independiente orientada a la manipulacion de registros de longitud fija mediante ficheros de acceso aleatorio, permitiendo la lectura directa y la modificacion selectiva de atributos.
