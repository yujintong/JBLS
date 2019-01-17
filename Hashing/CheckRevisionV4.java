package Hashing;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import util.*;

/** 
 * This is the new CheckRevisionV4 introduced in 2019 for Diablo 2.
 * It comes in CheckRevision.mpq
 */
public class CheckRevisionV4 extends CheckRevisionV1
{
    /** Stores some past results */
	private static int Version[][] = new int[3][0x0C];
    private static Hashtable<String, CheckrevisionResults> crCache = new Hashtable<String, CheckrevisionResults>();
    private static int crCacheHits = 0;
    private static int crCacheMisses = 0;

    public static void clearCache(){
    	Version = new int[3][0x0C];
    	crCacheHits = 0;
    	crCacheMisses = 0;
    	crCache = new Hashtable<String, CheckrevisionResults>();
    	System.gc();
    }

    public static void main(String[] args) 
    {
    	CheckrevisionResults result = null;
    	String[] inputs = {"0RFf+AAA", "3ou3jQAA", "ZYDAHQAA", "Ry4VIgAA"};
    	for (int i = 0; i < inputs.length; i++) {
    		try {
        		System.out.println("in: " + inputs[i]);
    			result = checkRevision(inputs[i], Constants.PRODUCT_DIABLO2, Constants.PLATFORM_INTEL, "CheckRevision.mpq");
    			System.out.println("out: " + Integer.toHexString(result.getChecksum()) + " " + new String(result.getInfo().getBufferChar()));
    		} catch (IOException e) {
    			System.out.println("Test failed (IO error)");
    		}
    		System.out.println("");
    	}
    }
    
    public static CheckrevisionResults checkRevision(String value, int prod, byte plat, String mpq) throws FileNotFoundException, IOException
    {
        if(prod > 0x0B || plat > 3 || prod < 0 || plat < 0) return null;
        
        CheckrevisionResults cacheHit = (CheckrevisionResults)crCache.get(value + mpq + prod + plat);
        if(cacheHit != null){
            Out.println("CREV", "CheckRevision cache hit: " + crCacheHits + " hits, " + crCacheMisses + " misses.");
            crCacheHits++;
            return cacheHit;
        }
        crCacheMisses++;

        String version = null;
        if (prod == Constants.PRODUCT_DIABLO2 || prod == Constants.PRODUCT_LORDOFDESTRUCTION)
        	version = "1.14.3.71";
        else {
            String[] files = getFiles(prod, plat);
            if (Version[plat][prod] == 0) Version[plat][prod] = getVersion(files, prod);
            version = getVersionString(Version[plat][prod]);
        }
        
        BigInteger vint = new BigInteger(InetAddress.getByName(version).getAddress());
        Version[plat][prod] = vint.intValue();
        
        Buffer buff = new Buffer(new Buffer(Base64.getDecoder().decode(value)).removeBytes(4));
        buff.addString(":" + version + ":");
        buff.addByte((byte)1);
        
        byte[] hash;
		try {
			hash = MessageDigest.getInstance("SHA-1").digest(buff.getBuffer());
		} catch (NoSuchAlgorithmException e) {
			Out.error("CREV", "Hashing failed - SHA1 not supported");
			return null;
		}
        String b64 = Base64.getEncoder().encodeToString(hash);
        
        byte[] checkB = b64.substring(0, 4).getBytes();
        
        // Flip the byte order
        for (int i = 0, j = checkB.length - 1; i < j; i++, j--) {
        	byte temp = checkB[i];
        	checkB[i] = checkB[j];
        	checkB[j] = temp;
        }
        int checksum = new BigInteger(checkB).intValue();
        if (checksum == 0 || Version[plat][prod] == 0) return null;
        
        Buffer info = new Buffer(b64.substring(4).getBytes());
        info.addByte((byte)0);	// Null terminator

        CheckrevisionResults result = new CheckrevisionResults(0, checksum, info);
        crCache.put(value + mpq + prod + plat, result);
        return result;
    }
    
    public static String getVersionString(int version)
    {
    	try {
    		byte[] b = BigInteger.valueOf(version).toByteArray();
			InetAddress val = InetAddress.getByAddress(b);
			return val.getHostAddress();
		} catch (UnknownHostException e) {
			Out.error("CREV", "Unable to parse version string");
			return null;
		}
    }
}