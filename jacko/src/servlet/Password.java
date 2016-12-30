package servlet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Password {

    public static String getHash(String pass,String mid) throws NoSuchAlgorithmException{
        String h = "";
        String m = hash(mid+"jacko");
        for(int i = 0;i<1000;i++){
            h=hash(h+pass+m);
        }
        return h;
    }

    private static String hash(String s) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-224");
        md.update(s.getBytes());
        byte[] digest = md.digest();
        StringBuffer buf = new StringBuffer();
        for(byte b:digest){
            buf.append(String.format("%02x", b));
        }
        return buf.toString();
    }
}
