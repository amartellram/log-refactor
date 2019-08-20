# Refactor Log

## Consideraciones
Se consideró lo siguiente:
Dividir el método de log y la lógica en general para no centralizar la complejidad.
Eliminar variables que no se usan.
Manejar constantes.
Agregar la sentencias try-with-resources  en el acceso a base de datos para que se cierren las conexiones al momento de terminar la operación o en caso de error.
Utilizar prepared statement.
