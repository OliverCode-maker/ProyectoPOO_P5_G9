package Modelo;

/**
 * Representa a un usuario del sistema con credenciales simples.
 * <p>Se utiliza para autenticación básica desde un archivo plano.</p>
 */
public class Usuario {

    /** Identificador interno del usuario. */
    private int idUser;

    /** Nombre de usuario (login). */
    private String username;

    /** Contraseña en texto (para demo; en producción debe ser un hash). */
    private String password;

    /**
     * Crea un usuario.
     * @param idUser   identificador interno
     * @param username nombre de usuario
     * @param password contraseña en texto (solo para práctica)
     */
    public Usuario(int idUser, String username, String password) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
    }

    /** @return identificador del usuario */
    public int getIdUser() { return idUser; }

    /** @return nombre de usuario */
    public String getUsername() { return username; }

    /** @return contraseña en texto (evitar en producción) */
    public String getPassword() { return password; }

    /**
     * Establece un nuevo nombre de usuario.
     * @param username nombre a asignar
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Establece una nueva contraseña (en texto).
     * @param password contraseña a asignar
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Representación textual del usuario.
     * <p><b>Nota:</b> actualmente expone la contraseña para depuración.
     * En producción, evita imprimirla o enmascárala.</p>
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "idUser=" + idUser +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
