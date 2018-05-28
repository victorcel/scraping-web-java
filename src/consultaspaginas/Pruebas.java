package consultaspaginas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class Pruebas {

    static String copia(String equipo) {
        try {
            String url = "smb://" + equipo + "/c$/Consultas Gobierno/Consultas Gobierno.jar";
            //final String sourcePath = "C:/Users/vbarrera.CORP/Documents/NetBeansProjects/ConsultasPaginas/src/consultaspaginas/imagenes/lupa.gif";
            final String sourcePath = "./store/Consultas Gobierno.jar";
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "administrator", "SIimage@1");
            SmbFile dir = new SmbFile(url, auth);
            if (dir.exists()) {
                SmbFileOutputStream smbFileOutputStream = new SmbFileOutputStream(dir);
                FileInputStream fileInputStream = new FileInputStream(sourcePath);
                final byte[] buf = new byte[16 * 1024 * 1024];
                int len;
                while ((len = fileInputStream.read(buf)) > 0) {
                    System.out.println("<");
                    smbFileOutputStream.write(buf, 0, len);
                    System.out.println(">");
                }
                System.out.println("Fin");
                fileInputStream.close();
                smbFileOutputStream.close();
//                dir.delete();
                //                System.err.println("yes");
            } else {
                return "No se encuentra archivo en el PC";
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(Pruebas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SmbException ex) {
//            System.out.print("SmbException: ");
//            System.err.print("" + ex.getMessage());
            return ex.getMessage();
        } catch (IOException ex) {
            return ex.getMessage();
        } catch (Exception ex) {
            System.out.println("" + ex.getMessage());
        }
        return "Fin";
    }

    public static void main(String args[]) {
        String host[] = {"nc1079151", "NC1079371", "NC1081601", "NC1080811", "NC1079001", "NC107935-11"};

        for (String equipo : host) {
            System.out.println("" + equipo);
            System.out.println(copia(equipo));
        }
    }
}
