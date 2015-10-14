package ssdd.p1.herramientas;

import java.nio.ByteBuffer;

public interface HTTPParser<T> {
    
    public void parseRequest(T entrada);

    public boolean isComplete();

    public boolean failed();

    public String getMethod();

    public String getPath();

    public ByteBuffer getBody();
}
