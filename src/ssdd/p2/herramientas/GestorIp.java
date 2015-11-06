/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: GestorIp.java
 * TIEMPO: 30 minutos
 * DESCRIPCION: Obtiene una direccion Ip local que sea accesible desde el exterior
 */

package ssdd.p2.herramientas;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gestor que obtiene una direccion Ip local que sea accesible desde el exterior
 * 
 * @author Juan Vela y Marta Frias
 *
 */
public class GestorIp {

    /** Direccion ip de interfaz virtual. Se usa para descartarla */
    private final String IP_VIRTUAL = "192.168.56.1";

    /** Direccion ip obtenida despues de filtrar las candidatas */
    private String ipObtenida = null;

    /**
     * Crea un gestor de direcciones Ip
     */
    public GestorIp() {
        obtenerIpLocal();
    }

    /**
     * Devuelve la direccion IP obtenida tras filtrar las candidatas
     * 
     * @return Cadena que contiene la direccion IP seleccionada
     */
    public String getIpObtenida() {
        return ipObtenida;
    }

    /**
     * Analiza las distintas interfaces de red y sus direcciones IP hasta hallar
     * una que sea accesible remotamente
     */
    private void obtenerIpLocal() {

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface
                    .getNetworkInterfaces();

            while (ipObtenida == null && interfaces.hasMoreElements()) {

                NetworkInterface interfaz = interfaces.nextElement();
                Enumeration<InetAddress> direcciones = interfaz
                        .getInetAddresses();

                Pattern patronIPv4 = Pattern
                        .compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+");

                Pattern patronIPvalida = Pattern
                        .compile("((?!127.)(?!169.)(?!0.))[0-9.]+");

                while (ipObtenida == null && direcciones.hasMoreElements()) {

                    InetAddress direccion = direcciones.nextElement();
                    Matcher ipv4 = patronIPv4
                            .matcher(direccion.getHostAddress());
                    Matcher ipvalida = patronIPvalida
                            .matcher(direccion.getHostAddress());

                    if (ipv4.matches() && ipvalida.matches()
                            && !direccion.getHostAddress().equals(IP_VIRTUAL)) {

                        ipObtenida = direccion.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            System.err.println("ERROR: no se ha podido obtener la IP local.");
        }
    }
}
