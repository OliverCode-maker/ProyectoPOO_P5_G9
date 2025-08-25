package Modelo;

/**
 * Excepción verificada que indica que faltan campos obligatorios
 * o que la validación de un formulario no se cumple.
 *
 * <p>Se usa en pantallas de publicación o filtros que requieren datos.</p>
 */
public class DatosIncompletosException extends Exception {

    /**
     * Crea una excepción por datos incompletos con un mensaje específico.
     * @param message detalle del campo o regla faltante
     */
    public DatosIncompletosException(String message) {
        super(message);
    }
}
