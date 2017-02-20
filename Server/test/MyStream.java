import java.io.InputStream;


final class MyStream extends InputStream {  // local mock stream
    private int counter = 0;
    MyStream() {
        super();
    }
    @Override public int available() 
    { 
        return 0; 
    }
    @Override public int read() {
        System.out.println("Read called");
        counter += 1;
        return counter == 1 ? '\n' : -1;
    }
    @Override public int read(byte[] b) {
        System.out.println("Read (bytes) called");
        counter += 1;
        
        int retval = 0;
        if (b.length == 2) {
            b[0] = 1; // WS:Binary
            b[1] = 40; // length including mask
            retval = 2;
        } else {
            int index = 0;
            while (index < 4) {
                b[index++]  = 0; // All zero mask is easiest
            }
            String JSNmsg = "{\"type\":\"HELLO\",\"node\":23, \"session\":1}";
            for (byte n: JSNmsg.getBytes()) {
                b[index++] = n;
            }
            retval = 40;
        }
        return retval;
    }
    @Override public void close() {
        
    }
}