package util;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptGeneratorTest {
    public static void main(String[] args) {
        String hashAdmin    = BCrypt.hashpw("admin123", BCrypt.gensalt(10));
        String hashTecnico  = BCrypt.hashpw("tec123", BCrypt.gensalt(10));
        String hashConsulta = BCrypt.hashpw("view123", BCrypt.gensalt(10));

        System.out.println("admin / admin123 => " + hashAdmin);
        System.out.println("tecnico1 / tec123 => " + hashTecnico);
        System.out.println("consulta1 / view123 => " + hashConsulta);
    }
}
