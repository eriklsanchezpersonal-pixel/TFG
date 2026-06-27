# NutriPet - Gestor Nutricional para Mascotas

Aplicación Android desarrollada en Java utilizando **Room** como sistema de persistencia de datos local, diseñada para gestionar la salud nutricional y citas médicas de mascotas.

## Requisitos previos

Para poder compilar y ejecutar este proyecto, necesitas:

1.  **Android Studio**: (Versión Jellyfish o superior recomendada).
2.  **JDK 17 o superior**.
3.  **SDK de Android**: Nivel de API 26 (Android 8.0) como mínimo.
4.  Un dispositivo físico o emulador con **Android 8.0+**.

## 🛠 Instalación

Sigue estos pasos para tener el proyecto funcionando en tu entorno local:

1.  **Clonar el repositorio**:
    ```bash
    git clone https://github.com/eriklsanchezpersonal-pixel/TFG.git
    ```

2.  **Abrir en Android Studio**:
    * Abre Android Studio.
    * Selecciona *File > Open* y elige la carpeta donde clonaste el proyecto.

3.  **Sincronizar Gradle**:
    * Espera a que Android Studio descargue las dependencias automáticamente. Si no sucede, ve a *File > Sync Project with Gradle Files*.

4.  **Configuración de la Base de Datos**:
    * El proyecto utiliza Room. Al iniciar la aplicación por primera vez, se creará automáticamente el archivo de base de datos local `nutripet.db`. Asegúrate de tener habilitados los permisos de almacenamiento si tu configuración de emulador lo requiere.

5.  **Ejecución**:
    * Selecciona un emulador o conecta tu dispositivo físico vía USB.
    * Presiona el botón **Run** (ícono de "Play" verde) en la barra de herramientas superior.

## 📱 Funcionalidades principales

* **Registro y Login**: Gestión de usuarios basada en persistencia local.
* **Gestión de Mascotas**: Alta, edición y seguimiento de datos biométricos.
* **Recomendador Nutricional**: Filtro inteligente de recetas basado en los ingredientes seleccionados y las patologías de la mascota.
* **Control de Citas**: Calendario integrado para el seguimiento médico.

## ⚙️ Tecnologías utilizadas

* **Lenguaje**: Java
* **Persistencia**: Room Database (ORM)
* **Arquitectura**: Activity-based navigation
* **UI**: Material Design components
