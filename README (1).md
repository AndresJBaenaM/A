# 🎵 EIA Player

Reproductor musical de escritorio desarrollado en **Java (Swing)** para el curso de **Lenguajes y Compiladores** — Universidad EIA.

El proyecto demuestra la implementación práctica de tres estructuras de datos distintas (lista circular doble, cola simple y árbol binario de búsqueda), cada una asociada a un modo de reproducción diferente.

---

## 📋 Requisitos previos

Antes de ejecutar el proyecto necesitas tener instalado:

- **JDK 17 o superior** (el proyecto usa *records*, introducidos en Java 16).
- No se requieren librerías externas: todo el proyecto usa únicamente `javax.swing` y `java.awt`, incluidos en el JDK.

Para verificar que tienes Java instalado, abre una terminal y ejecuta:

```bash
java -version
javac -version
```

Si no tienes Java instalado, descárgalo desde [Adoptium (Eclipse Temurin)](https://adoptium.net/) o [Oracle JDK](https://www.oracle.com/java/technologies/downloads/).

---

## 📁 Estructura del proyecto

```
ReproductorEIA/
├── Cancion.java                  # Modelo de datos de una canción
├── ModoReproduccion.java         # Interfaz común a los 3 modos
├── NodoCancion.java              # Nodo de la lista circular doble
├── NodoSimple.java               # Nodo de la cola simple
├── NodoArbol.java                # Nodo del árbol binario de búsqueda
├── ListaCircularDoble.java       # Modo 1: Reproducción aleatoria
├── ColaSimple.java                # Modo 2: Orden de llegada (FIFO)
├── ArbolBinarioBusqueda.java     # Modo 3: Orden alfabético (recorrido inorden)
├── ControladorReproductor.java   # Lógica de negocio (separada de la GUI)
├── Tema.java                     # Paleta de colores (tema claro/oscuro)
├── BotonPill.java                # Botón redondeado personalizado
├── BotonCircular.java            # Botón circular (controles de reproducción)
├── PortadaCancion.java           # Panel que dibuja la portada del álbum
└── ReproductorGUI.java           # Interfaz gráfica principal (clase con el main)
```

---

## ▶️ Cómo ejecutar el programa

### Opción 1: Desde la terminal (sin IDE)

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/tu-repositorio.git
   cd tu-repositorio
   ```

2. Compila todos los archivos `.java`:
   ```bash
   javac *.java
   ```

3. Ejecuta el programa:
   ```bash
   java ReproductorGUI
   ```

   Si compilaste dentro de una carpeta específica (por ejemplo `ReproductorEIA/`), asegúrate de ejecutar ambos comandos desde esa misma carpeta.

### Opción 2: Desde un IDE (IntelliJ IDEA, Eclipse, VS Code, NetBeans)

1. Abre el IDE y selecciona **"Abrir proyecto"** o **"Importar proyecto"**.
2. Apunta a la carpeta que contiene los archivos `.java`.
3. Asegúrate de que el **SDK del proyecto** esté configurado en Java 17 o superior.
4. Busca la clase `ReproductorGUI.java` y ejecuta su método `main` (botón ▶️ o clic derecho → *Run*).

---

## 🎧 Modos de reproducción

| Modo | Estructura de datos | Comportamiento |
|---|---|---|
| **Aleatorio** | Lista Circular Doble | Navegación infinita en ambas direcciones (Siguiente/Anterior). Al llegar al final, regresa automáticamente al inicio. |
| **Orden de llegada** | Cola Simple (FIFO) | Solo se puede avanzar. Cada canción reproducida desaparece de la cola. |
| **Alfabético** | Árbol Binario de Búsqueda | Recorrido inorden por nombre de canción, con Avanzar/Retroceder. |

Puedes cambiar de modo en cualquier momento desde el selector en la parte superior de la ventana.

---

## ✨ Funcionalidades

- Agregar, editar, eliminar y buscar canciones.
- Calificación de canciones (0-100).
- Portada del álbum (imagen elegida por el usuario o generada automáticamente si no se asigna ninguna).
- Barra de progreso simulada con tiempo transcurrido y duración total.
- **Tema claro/oscuro** intercambiable desde la esquina superior derecha.
- **Atajos de teclado**:
  - `Espacio` → Reproducir / Pausar
  - `→` → Siguiente canción
  - `←` → Canción anterior (deshabilitado en el modo Cola)

---

## 🎓 Contexto académico

Este proyecto fue desarrollado como entrega del **Trabajo 1** del curso de Lenguajes y Compiladores de la Universidad EIA. El objetivo principal no es construir un reproductor comercial, sino demostrar la correcta implementación de estructuras de datos (listas ligadas, colas y árboles) vistas durante el curso.

## 👥 Autores

- Juan Esteban 
- Andrés Julián Baena Martínez
