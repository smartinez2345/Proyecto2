Proyecto #2 — Dulces & Dados
Board Game Café — Sistema de Gestión con Torneos
Curso: ISIS-1226 Diseño y Programación Orientada a Objetos
Universidad de los Andes
Integrantes:

Santiago Martínez Chacón — 202511078
Daniel Niño — 202325515
Santiago Bobadilla — 20232470


Descripción
Sistema de gestión para el Board Game Café "Dulces & Dados". El Proyecto #2 extiende el sistema del Proyecto #1 con un módulo completo de torneos de juegos de mesa (amistosos y competitivos), interfaces de consola independientes para cada tipo de usuario y pruebas automatizadas con JUnit 4.

Estructura del Proyecto
Proyecto2/
├── src/
│   ├── logica/
│   │   ├── GestorInventario.java
│   │   ├── GestorPrestamos.java
│   │   ├── GestorTorneos.java        ← NUEVO
│   │   ├── GestorTurnos.java
│   │   ├── GestorUsuarios.java
│   │   └── GestorVentas.java
│   ├── modelo/
│   │   ├── Administrador.java
│   │   ├── Bebida.java
│   │   ├── Cafe.java                 ← MODIFICADO
│   │   ├── Cliente.java              ← MODIFICADO
│   │   ├── Cocinero.java
│   │   ├── DetalleVenta.java
│   │   ├── Empleado.java
│   │   ├── InscripcionTorneo.java    ← NUEVO
│   │   ├── InventarioCafeteria.java
│   │   ├── InventarioJuegos.java
│   │   ├── Juego.java
│   │   ├── Mesa.java
│   │   ├── Mesero.java
│   │   ├── Pasteleria.java
│   │   ├── Prestamo.java
│   │   ├── ProductoCafeteria.java
│   │   ├── SolicitudCambioTurno.java
│   │   ├── Torneo.java               ← NUEVO
│   │   ├── TorneoAmistoso.java       ← NUEVO
│   │   ├── TorneoCompetitivo.java    ← NUEVO
│   │   ├── Usuario.java
│   │   └── Venta.java
│   ├── persistencia/
│   │   ├── ExportadorTexto.java
│   │   └── Persistencia.java
│   ├── presentacion/
│   │   ├── GenerarDatosIniciales.java
│   │   ├── MainAdministrador.java    ← NUEVO
│   │   ├── MainCliente.java          ← NUEVO
│   │   ├── MainEmpleado.java         ← NUEVO
│   │   └── (otras pruebas del P1)
│   └── test/
│       ├── GestorTorneosTest.java    ← NUEVO
│       └── TorneoIntegracionTest.java ← NUEVO
└── data/
    └── cafe_inicial.dat

Requisitos

Java 8 o superior
Eclipse IDE
JUnit 4 (agregado al Build Path del proyecto)


Instrucciones de Ejecución
Paso 1 — Importar el proyecto en Eclipse

Abrir Eclipse
File → Import → General → Existing Projects into Workspace
Seleccionar la carpeta raíz del proyecto
Click Finish

Paso 2 — Verificar JUnit 4

Clic derecho en el proyecto → Build Path → Add Libraries
Seleccionar JUnit → JUnit 4
Click Finish

Paso 3 — Generar datos iniciales (solo la primera vez)
Si no existe el archivo data/cafe_inicial.dat:

Abrir presentacion/GenerarDatosIniciales.java
Clic derecho → Run As → Java Application
Esto crea el archivo data/cafe_inicial.dat con datos de prueba

Paso 4 — Ejecutar las interfaces de consola
Portal del Administrador:

Abrir presentacion/MainAdministrador.java
Clic derecho → Run As → Java Application
Login: admin | Password: admin123

Portal del Empleado:

Abrir presentacion/MainEmpleado.java
Clic derecho → Run As → Java Application
Usar credenciales de un empleado registrado por el administrador

Portal del Cliente:

Abrir presentacion/MainCliente.java
Clic derecho → Run As → Java Application
Opción 1: Iniciar sesión | Opción 2: Registrarse

Paso 5 — Ejecutar las pruebas JUnit

Clic derecho sobre el paquete test
Run As → JUnit Test
Deben ejecutarse 37 pruebas en total (34 unitarias + 3 integracion)


Persistencia
Los datos se guardan y cargan automaticamente desde data/cafe_inicial.dat usando serializacion Java. No se requiere intervencion del usuario para cargar o guardar  ocurre al iniciar y al seleccionar Salir en cualquier menu.

Credenciales por Defecto
UsuarioLoginPasswordAdministradoradminadmin123Cliente (ejemplo)cliente1passEmpleado (ejemplo)mesero1pass

Los empleados son registrados por el administrador desde el Portal del Administrador.
Los clientes pueden registrarse por su propia cuenta desde el Portal del Cliente.


Nuevas Funcionalidades — Proyecto #2

Torneos Amistosos: sin costo, premio = bono de descuento acumulable
Torneos Competitivos: tarifa de entrada, premio en metálico según inscripciones de clientes
Cupos para fanáticos: 20% de los cupos reservados (redondeado arriba) para clientes con el juego en favoritos
Inscripción de empleados: gratis, solo si no tienen turno el día del torneo
Bonos acumulables: los clientes pueden acumular bonos de distintos torneos amistosos ganados