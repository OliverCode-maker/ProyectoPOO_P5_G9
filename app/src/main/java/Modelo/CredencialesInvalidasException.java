package Modelo;

/**
 * Excepción verificada lanzada cuando las credenciales de acceso
 * (usuario/contraseña) son inválidas durante el proceso de autenticación.
 *
 * <p>Permite mostrar un mensaje claro al usuario en el flujo de login.</p>
 */
public class CredencialesInvalidasException extends Exception {

    /**
     * Crea una excepción de credenciales inválidas con un mensaje específico.
     * @param message detalle a mostrar
     */
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
