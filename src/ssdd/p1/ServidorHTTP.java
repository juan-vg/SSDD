/*
 * AUTORES: Juan Vela Garcia / Marta Frias Zapater
 * NIA: 643821 / 535621
 * FICHERO: ServidorHTTP.java
 * TIEMPO: 40 horas
 * DESCRIPCION: Servidor web con dos implementaciones: una usando hilos y otra
 *      utilizando la clase Selector.
 */
package ssdd.p1;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Servidor HTTP sencillo con dos implementaciones: una utilizando un solo hilo
 * y la clase Selector y otra con un hilo independiente para cada peticion
 * @author Juan Vela, Marta Frias
 *
 */
public class ServidorHTTP {

    /** Puerto asociado al servidor */
    static int SERVER_PORT;
    
    /**
     * Version del servidor HTTP utilizando hilos
     */
    private static void serverThread() {
        
        ServerSocket svrSock = null;
        Socket clntSock = null;
        
        try {
            svrSock = new ServerSocket(SERVER_PORT);
        }
        catch (IOException e) {
            System.err.println("ERROR: Fallo asociando puerto: "
                    + SERVER_PORT);
            System.exit(1);
        }
        
        try {
            clntSock = svrSock.accept();
            
            while (clntSock != null) {
                ServidorThreadHTTP hijo = new ServidorThreadHTTP(
                        clntSock);
                Thread thread = new Thread(hijo);
                thread.run();
                
                clntSock = svrSock.accept();
            }
        }
        catch (IOException e) {
            System.err.println("ERROR: Fallo aceptando nueva conexion.");
            System.exit(2);
        }
        
        try {
            svrSock.close();
        }
        catch (IOException e) {
            System.err.println("ERROR: Fallo cerrando conexion.");
            System.exit(3);
        }
    }
    
    /**
     * Metodo auxiliar de serverSelector que acepta peticiones HTTP
     * 
     * @param svrSockCh
     *            Canal asociado al servidor
     * @param selector
     *            Objeto Selector asociado a la peticion
     */
    private static void svrAccept(ServerSocketChannel svrSockCh,
            Selector selector) {
        
        SocketChannel cSockCh;
        try {
            cSockCh = svrSockCh.accept();
            
            cSockCh.configureBlocking(false);
            HTTPParser parser = new HTTPParser();
            
            SelectionKey selKey = cSockCh.register(selector, 
                    SelectionKey.OP_READ);
            Utils utils = new Utils();
            utils.setParser(parser);
            selKey.attach(utils);
        }
        catch (IOException e) {
            System.err.println("ERROR: Fallo aceptando nueva conexion " 
                    + e.getMessage());
        }
    }
    
    /**
     * Metodo auxiliar de serverSelector que escribe en un fichero
     * 
     * @param contP1
     *            contenido del primer parametro recibido en el post
     * @param contP2
     *            contenido del segundo parametro recibido en el post
     */
    private static void escribeFichero(String contP1, String contP2){
        File path = new File("");
        File fichero = new File(path
                .getAbsolutePath()
                + "/" + contP1);
        PrintWriter escritor;
        try {
            escritor = new PrintWriter(
                    fichero);
       
        // evita errores con el printf
        String tmp = contP2.replace("%","%%");
        escritor.printf(tmp);
        escritor.flush();
        escritor.close();
        }
        catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        } 
        
    }
    
    /**
     * Metodo auxiliar del metodo serverSelector que lee y procesa
     * peticiones HTTP cuando el canal esta preparado para leer.
     * 
     * @param key
     *            Objeto SelectionKey asociado a la conexion
     * @param selector
     *            Objeto Selector asociado a la peticion
     */
    private static void svrRead(SelectionKey key, Selector selector) {
        
        try { 
            SocketChannel cSockCh = (SocketChannel) key.channel();
            
            if (cSockCh != null) {
                
                Utils utils = (Utils) key.attachment();
                HTTPParser parser = utils.getParser();
                ByteBuffer buf;
                if (utils.getBuffer() != null) {
                    buf = utils.getBuffer();
                } else {
                    buf = ByteBuffer.allocate(1024);
                    utils.setBuffer(buf);
                }
                buf.clear();
                cSockCh.read(buf);
                buf.flip();
                parser.parseRequest(buf);
                
                if (parser.failed()) {
                    
                    // BAD REQUEST (400)
                    utils.setString(Utils.respuesta(400, null));
                    
                    SelectionKey selKey = cSockCh.register(selector,
                            SelectionKey.OP_WRITE);
                    selKey.attach(utils);
                } else if (parser.isComplete()) {
                    
                    // REQUEST COMPLETA
                    if (parser.getMethod().equals("GET")) {
                        
                        // METODO GET
                        File path = new File("");
                        File fichero = new File(path
                                .getAbsolutePath()
                                + parser.getPath());
                        
                        if (!fichero.exists()) {
                            
                            // NOT FOUND (404)
                            utils.setString(Utils
                                    .respuesta(404, null));
                        } else {
                            Matcher matcher = Utils.pathPattern
                                    .matcher(parser.getPath());
                            
                            if (matcher.matches()) {
                                
                                // OK (200)
                                Scanner target = new Scanner(fichero);
                                utils.setFile(target);
                                utils.setString(Utils.respuesta(200,
                                        null, fichero.length()));
                            } else {
                                
                                // FORBIDDEN (403)
                                utils.setString(Utils.respuesta(403,
                                        null));
                            }
                        }
                    } else if (parser.getMethod().equals("POST")) {
                        
                        // METODO POST
                        ByteBuffer bodyBuf = parser.getBody();
                        String body = "";
                        
                        if (bodyBuf.hasArray()) {
                            body = new String(bodyBuf.array());
                        }
                        
                        // separar los parametros antes de decodificar
                        // por si se incluye
                        // el caracter '&' en el contenido de alguno
                        String[] params = body.split("&");
                        
                        if (params.length == 2) {
                            
                            params[0] = URLDecoder.decode(params[0],
                                    "UTF-8");
                            params[1] = URLDecoder.decode(params[1],
                                    "UTF-8");
                            String nomP1 = params[0].substring(0,
                                    params[0].indexOf("="));
                            String contP1 = params[0]
                                    .substring(params[0].indexOf("=") + 1);
                            String nomP2 = params[1].substring(0,
                                    params[1].indexOf("="));
                            String contP2 = params[1]
                                    .substring(params[1].indexOf("=") + 1);
                            
                            if (nomP1.compareTo("fname") == 0
                                    && nomP2.compareTo("content") == 0) {
                                Matcher matcher = Utils.pathPattern
                                        .matcher(contP1);
                                if (matcher.matches()) {
                                    escribeFichero(contP1,contP2);
                                    
                                    utils.setString(Utils.respuesta(200, 
                                            Utils.cuerpoExito(contP1, contP2)));
                                } else {
                                    // FORBIDDEN (403)
                                    utils.setString(Utils.respuesta(403, null));
                                }
                            } else {
                                // BAD REQUEST (400)
                                utils.setString(Utils.respuesta(400,null));
                            }
                        } else {
                            // BAD REQUEST (400)
                            utils.setString(Utils.respuesta(400, null));
                        }
                    } else {
                        // NOT IMPLEMENTED (501)
                        utils.setString(Utils.respuesta(501, null));
                    }
                    
                    SelectionKey selKey = cSockCh.register(selector,
                            SelectionKey.OP_WRITE);
                    selKey.attach(utils);
                }
            }
        }
        catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Metodo auxiliar del metodo serverSelector que escribe
     * peticiones HTTP cuando el canal esta preparado para escribir.
     * 
     * @param key
     *            Objeto SelectionKey asociado a la conexion
     */
    private static void svrWrite(SelectionKey key){

        SocketChannel cSockCh= (SocketChannel)key.channel();
        
        if(cSockCh != null){
            Utils utils = (Utils)key.attachment();
            
            if(utils.hayBody()){
                ByteBuffer buf = utils.getBuffer();
                buf.clear();
                try {
                    buf.put(utils.getBody(buf.capacity()/2).getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    System.err.println("ERROR: Fallo al escribir en el buffer "
                            + e.getMessage());
                }
                buf.flip();
                try {
                    cSockCh.write(buf);
                } catch (IOException e) {
                    System.err.println("ERROR: " + e.getMessage());
                }
            } else{
                key.cancel();
                try {
                    cSockCh.close();
                } catch (IOException e) {
                    System.err.println("ERROR: Fallo cerrando conexion. " 
                            + e.getMessage());
                }
            }                               
        }
    }
    
    /**
     * Version del servidor HTTP utilizando la clase Selector
     */
    private static void serverSelector() {
        
        try {
            ServerSocketChannel svrSockCh = ServerSocketChannel.open();
            svrSockCh.configureBlocking(false);
            svrSockCh.socket().bind(new InetSocketAddress(SERVER_PORT));
            
            Selector selector = Selector.open();
            svrSockCh.register(selector, SelectionKey.OP_ACCEPT);
            
            while (true) {
                int numCh = selector.select();
                
                if (numCh > 0) {
                    
                    // Hay canales disponibles
                    Set<SelectionKey> selKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIt = selKeys.iterator();
                    
                    while (keyIt.hasNext()) {
                        SelectionKey key = keyIt.next();
                        
                        if (key.isAcceptable()) {
                            
                            // ServerSocketChannel acepta conexion
                            svrAccept(svrSockCh, selector);
                        } else if (key.isReadable()) {
                            
                            // canal preparado para leer
                            svrRead(key, selector);
                        } else if (key.isWritable()) {
                            
                            // canal preparado para escribir
                            svrWrite(key);
                        }
                        keyIt.remove();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
    
	public static void main(String[] args) {
		//TODO lanzador con main
		//TODO thread que escuche teclado, para cerrar servidor
        
        if (args.length == 2) {
            try {
                SERVER_PORT = Integer.parseInt(args[1]);
                
                if (args[0].equals("-t")) {
                    serverThread();
                } else if (args[0].equals("-s")) {
                    serverSelector();
                } else {
                    System.err.println("ERROR: Opcion no valida.");
                }
            } catch (NumberFormatException e) {
                System.err.println("ERROR: El puerto no es valido.");
            }
        } else {
            System.err.println("ERROR: Numero de parametros incorrecto.");
        }
        
        System.out.println("ADIOS");
    }

}
