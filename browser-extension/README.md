# DopamiNah Browser Extension

Extension de navegador para controlar el tiempo en sitios web usando las metas de DopamiNah.

## Instalacion (desarrollo)

1. Abre Chrome y ve a `chrome://extensions/`
2. Activa "Modo desarrollador" (esquina superior derecha)
3. Haz clic en "Cargar extension descomprimida"
4. Selecciona la carpeta `browser-extension/`

## Uso

### Crear metas
1. Haz clic en el icono de DopamiNah en la barra de extensiones
2. Clic en "+ Nueva"
3. Ingresa el dominio (ej. `youtube.com`) y selecciona un tiempo limite
4. Guarda la meta

### Bloqueo automatico
- Cuando visitas un sitio con meta activa, la extension cuenta el tiempo
- Al alcanzar el limite, la extension redirige a una pagina de bloqueo
- Puedes desbloquear temporalmente desde la pagina de bloqueo

### Sincronizacion con DopamiNah web
1. Abre la app web de DopamiNah
2. En el popup de la extension, haz clic en "Sincronizar con DopamiNah web"
3. Las metas se sincronizan en ambos sentidos

## Archivos

- `manifest.json` - Configuracion de la extension (Manifest V3)
- `background.js` - Service worker: rastreo de tiempo y bloqueo via DNR
- `content.js` - Script de contenido para sincronizar con la web app
- `popup/` - Interfaz del popup
- `blocked/` - Pagina mostrada cuando se bloquea un sitio
- `icons/` - Iconos de la extension

## Permisos

- `storage` - Guardar metas y tiempo acumulado
- `alarms` - Temporizador periodico para acumular tiempo
- `webNavigation` - Detectar navegacion a nuevos sitios
- `declarativeNetRequest` - Bloquear/redirigir sitios cuando se excede el limite
- `idle` - Detectar cuando el usuario esta inactivo
- `tabs` - Comunicacion con la app web
- `<all_urls>` - Aplicar bloqueo a cualquier sitio
