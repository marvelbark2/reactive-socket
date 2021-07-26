package ws.prospeak.project.socket.reactivex.server.observer;

import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Flow;

public class Test {
    final static Logger logger = LoggerFactory.getLogger(Test.class);
    public static void main(String[] args) {
       Server server = new Server(61022);
       server.subscribe(Test::log);
       server.start();
    }

    static void log(Socket socket) {
        try {
          PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
          writer.println("Hello there");
          InputStream reader = socket.getInputStream();
          var t = Observable.defer(() -> {
              byte[] transBuffer = new byte[1028];
              Runnable read = () -> {
                  try {
                      var d = reader.read(transBuffer);
                  } catch (IOException ioException) {
                      logger.error(ioException.getMessage(), ioException);
                  }
              };
              read.run();
              Byte[] buffer = new Byte[1028];
              int i=0;
              for (byte buf: transBuffer) {
                  buffer[i++] = buf;
              }
              return Observable.fromArray(buffer);
          }).repeat().takeWhile(x -> x > 0).subscribe(x -> {
              logger.info("Byte: {}", x);
              writer.println("Byte is " + x);
          });
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
