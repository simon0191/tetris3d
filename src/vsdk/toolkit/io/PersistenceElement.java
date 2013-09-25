//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - December 8 2006 - Oscar Chavarro: Original base version               =
//===========================================================================

package vsdk.toolkit.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import vsdk.toolkit.common.VSDK;

/**
DEFINITION: A `PersistenceElement` in VitralSDK is a software element with
algorithms and data structures (i.e. a class) with the specific functionality
of providing persistence operations for a data Entity.

The PersistenceElement abstract class provides an interface for *Persistence
style classes. This serves three purposes:
  - To help in design level organization of persistence classes (this eases the
    study of the class hierarchy)
  - To provide a place to locate possible future operations, common to
    all persistence classes and persistence private utility/supporting
    classes.  In particular, this class contains basic low level
    persistence operations for converting bit streams from and to basic
    numeric data types. Note that this code is NOT portable, as it needs
    explicit programmer configuration for little-endian or big-endian
    hardware platform.
  - To provide means of accessing some operating system's native library
    files and other basic file system management.
*/

public abstract class PersistenceElement {

    private static final boolean bigEndianArchitecture = false;

    private static byte[] byteBufferLone = new byte[1];
    private static byte[] bytesForInt = new byte[2];
    private static byte[] bytesForLong = new byte[4];
    private static byte[] bytesForFloat = new byte[4];

    public static boolean
    checkDirectory(String dirName)
    {
        File dirFd = new File(dirName);

        if ( dirFd.exists() && (!dirFd.isDirectory() ) ) {
            System.err.println("Directory " + dirName + " can not be created, because a file with that name already exists (not overwriten).");
            return false;
        }

        if ( !dirFd.exists() && !dirFd.mkdir() ) {
            System.err.println("Directory " + dirName + " can not be created, check permisions and available free disk space.");
            return false;
        }

        return true;
    }

    /**
    Given a filename, this method extract its extension and return it.
    @todo: This method will fail when directory path or filename contains
    more than one dot.  Needs to be fixed.
    */
    protected static String extractExtensionFromFile(File fd)
    {
        String filename = fd.getName();
        StringTokenizer st = new StringTokenizer(filename, ".");
        int numTokens = st.countTokens();
        for( int i = 0; i < numTokens - 1; i++ ) {
            st.nextToken();
        }
        String ext = st.nextToken();
        return ext;
    }

    public static int
    readByteInt(InputStream is) throws Exception
    {
        int a;

        is.read(byteBufferLone, 0, 1);
        a = (int)byteBufferLone[0];

        return a;
    }

    public static int
    readByteUnsignedInt(InputStream is) throws Exception
    {
        int a;

        is.read(byteBufferLone, 0, 1);
        a = VSDK.signedByte2unsignedInteger(byteBufferLone[0]);

        return a;
    }

    /**
    Given a previously initialized array of bytes, this method fills it
    with information readed from the given input stream.  If it is not
    enough information to read, this method generates an Exception.
    */
    public static void
    readBytes(InputStream is, byte[] bytesBuffer) throws Exception
    {
        int offset = 0;
        int numRead = 0;
        int length = bytesBuffer.length;
        do {
            numRead = is.read(bytesBuffer, 
                              offset, (length-offset));
            offset += numRead;
        } while( offset < length && numRead >= 0 ); 
    }

    /**
    Given a previously initialized array of bytes, this method writes it
    with information readed from the given output stream.  If it is not
    enough information to read, this method generates an Exception.
    */
    public static void
    writeBytes(OutputStream os, byte[] bytesBuffer) throws Exception
    {
        os.write(bytesBuffer, 0, bytesBuffer.length);
    }

    private static void int2byteArrayDirect(byte[] arr, int start, int num)
    {
        int i = 0;
        int len = 2;
        byte[] tmp = new byte[len];

        // Convert number to array
        for ( i = 0; i < len; i++ ) {
            tmp[i] = (byte)((num & (0xFF << 8*i)) >> 8*i);
        }

        // Export subarray to end array
        int cnt;
        for ( i = start, cnt = 0; i < (start + len); i++, cnt++ ) {
            arr[i] = tmp[cnt];
        }
    }

    private static void int2byteArrayInvert(byte[] arr, int start, int num)
    {
        int i = 0;
        int len = 2;
        byte[] tmp = new byte[len];

        // Convert number to array
        for ( i = 0; i < len; i++ ) {
            tmp[len-i-1] = (byte)((num & (0xFF << 8*i)) >> 8*i);
        }

        // Export subarray to end array
        int cnt;
        for ( i = start, cnt = 0; i < (start + len); i++, cnt++ ) {
            arr[i] = tmp[cnt];
        }
    }

    private static int byteArray2intDirect(byte[] arr, int start) {
        int low = arr[start] & 0xff;
        int high = arr[start+1] & 0xff;
        return ( high << 8 | low );
    }

    private static int byteArray2intInvert(byte[] arr, int start) {
        int low = arr[start] & 0xff;
        int high = arr[start+1] & 0xff;
        return ( low << 8 | high );
    }

    private static long byteArray2longDirect(byte[] arr, int start) {
        int i = 0;
        int len = 4;
        int cnt = 0;
        byte[] tmp = new byte[len];
        for ( i = start; i < (start + len); i++ ) {
            tmp[cnt] = arr[i];
            cnt++;
        }
        long accum = 0;
        i = 0;
        for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
            accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
            i++;
        }
        return accum;
    }

    private static void long2byteArrayDirect(
        byte[] arr, int start, long num) {
        int i = 0;
        int len = 4;
        byte[] tmp = new byte[len];

        // Convert number to array
        for ( i = 0; i < len; i++ ) {
            tmp[i] = (byte)((num & (0xFF << 8*i)) >> 8*i);
        }

        // Export subarray to end array
        int cnt;
        for ( i = start, cnt = 0; i < (start + len); i++, cnt++ ) {
            arr[i] = tmp[cnt];
        }
    }

    private static void long2byteArrayInvert(
        byte[] arr, int start, long num) {
        int i = 0;
        int len = 4;
        byte[] tmp = new byte[len];

        // Convert number to array
        for ( i = 0; i < len; i++ ) {
            tmp[len-i-1] = (byte)((num & (0xFF << 8*i)) >> 8*i);
        }

        // Export subarray to end array
        int cnt;
        for ( i = start, cnt = 0; i < (start + len); i++, cnt++ ) {
            arr[i] = tmp[cnt];
        }
    }

    private static long byteArray2longInvert(byte[] arr, int start) {
        int i = 0;
        int len = 4;
        int cnt = 3;
        byte[] tmp = new byte[len];
        for ( i = start; i < (start + len); i++ ) {
            tmp[cnt] = arr[i];
            cnt--;
        }
        long accum = 0;
        i = 0;
        for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
            accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
            i++;
        }
        return accum;
    }

    private static float byteArray2floatDirect(byte[] arr, int start) {
        int i = 0;
        int len = 4;
        int cnt;
        byte[] tmp = new byte[len];

        for ( i = start, cnt = 0; i < (start + len); i++, cnt++ ) {
            tmp[cnt] = arr[i];
        }
        int accum = 0;
        i = 0;
        for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
            accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
            i++;
        }
        return Float.intBitsToFloat(accum);
    }

    private static float byteArray2floatInvert(byte[] arr, int start) {
        int i = 0;
        int len = 4;
        int cnt = 3;
        byte[] tmp = new byte[len];
        for ( i = start; i < (start + len); i++ ) {
            tmp[cnt] = arr[i];
            cnt--;
        }
        int accum = 0;
        i = 0;
        for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
            accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
            i++;
        }
        return Float.intBitsToFloat(accum);
    }

    /**
    This method is responsible of taking into account the endianess of the 
    original data
    */
    public static int byteArray2intBE(byte[] arr, int start) {
        if ( bigEndianArchitecture ) {
            return byteArray2intDirect(arr, start);
        }
        return byteArray2intInvert(arr, start);
    }

    public static void int2byteArrayBE(byte[] arr, int start, int num) {
        if ( bigEndianArchitecture ) {
            int2byteArrayDirect(arr, start, num);
        }
        int2byteArrayInvert(arr, start, num);
    }

    public static void int2byteArrayLE(byte[] arr, int start, int num) {
        if ( bigEndianArchitecture ) {
            int2byteArrayInvert(arr, start, num);
        }
        int2byteArrayDirect(arr, start, num);
    }

    /**
    This method is responsible of taking into account the endianess of the 
    original data
    */
    public static int byteArray2intLE(byte[] arr, int start) {
        if ( bigEndianArchitecture ) {
            return byteArray2intInvert(arr, start);
        }
        return byteArray2intDirect(arr, start);
    }


    /**
    This method is responsible of taking into account the endianess of the 
    original data
    */
    public static long byteArray2longBE(byte[] arr, int start) {
        if ( bigEndianArchitecture ) {
            return byteArray2longDirect(arr, start);
        }
        return byteArray2longInvert(arr, start);
    }

    /**
    This method is responsible of taking into account the endianess of the 
    original data
    */
    public static long byteArray2longLE(byte[] arr, int start) {
        if ( bigEndianArchitecture ) {
            return byteArray2longInvert(arr, start);
        }
        return byteArray2longDirect(arr, start);
    }

    /**
    This method is responsible of taking into account the endianess of the 
    original data
    */
    public static float byteArray2floatBE(byte[] arr, int start) {
        if ( bigEndianArchitecture ) {
            return byteArray2longDirect(arr, start);
        }
        return byteArray2longInvert(arr, start);
    }

    public static void float2byteArrayBE(byte[] arr, int start, float num) {
        long a = Float.floatToIntBits(num);
        if ( bigEndianArchitecture ) {
            long2byteArrayDirect(arr, start, a);
        }
        long2byteArrayInvert(arr, start, a);
    }

    public static void float2byteArrayLE(byte[] arr, int start, float num) {
        long a = Float.floatToIntBits(num);
        if ( bigEndianArchitecture ) {
            long2byteArrayInvert(arr, start, a);
        }
        long2byteArrayDirect(arr, start, a);
    }

    /**
    This method is responsible of taking into account the endianess of the 
    original data
    */
    public static float byteArray2floatLE(byte[] arr, int start) {
        if ( bigEndianArchitecture ) {
            return byteArray2floatInvert(arr, start);
        }
        return byteArray2floatDirect(arr, start);
    }

    public static int readIntLE(InputStream is) throws Exception
    {
        readBytes(is, bytesForInt);
        return byteArray2intLE(bytesForInt, 0);
    }

    public static int readIntBE(InputStream is) throws Exception
    {
        readBytes(is, bytesForInt);
        return byteArray2intBE(bytesForInt, 0);
    }

    public static void writeIntBE(OutputStream os, int num) throws Exception
    {
        int2byteArrayBE(bytesForInt, 0, num);
        writeBytes(os, bytesForInt);
    }

    public static void writeIntLE(OutputStream os, int num) throws Exception
    {
        int2byteArrayLE(bytesForInt, 0, num);
        writeBytes(os, bytesForInt);
    }

    public static long readLongLE(InputStream is) throws Exception
    {
        readBytes(is, bytesForLong);
        return byteArray2longLE(bytesForLong, 0);
    }

    public static long readLongBE(InputStream is) throws Exception
    {
        readBytes(is, bytesForLong);
        return byteArray2longBE(bytesForLong, 0);
    }
    public static float readFloatLE(InputStream is) throws Exception
    {
        readBytes(is, bytesForFloat);
        return byteArray2floatLE(bytesForFloat, 0);
    }

    public static float readFloatBE(InputStream is) throws Exception
    {
        readBytes(is, bytesForFloat);
        long i = byteArray2longBE(bytesForFloat, 0);
        int j = (int)i;
        return Float.intBitsToFloat(j);
    }

    public static void writeFloatBE(OutputStream os, float num)
        throws Exception
    {
        float2byteArrayBE(bytesForFloat, 0, num);
        writeBytes(os, bytesForFloat);
    }

    public static void writeFloatLE(OutputStream os, float num)
        throws Exception
    {
        float2byteArrayLE(bytesForFloat, 0, num);
        writeBytes(os, bytesForFloat);
    }

    public static void writeLongBE(OutputStream os, long num)
        throws Exception
    {
        if ( bigEndianArchitecture ) {
            long2byteArrayDirect(bytesForLong, 0, num);
        }
        long2byteArrayInvert(bytesForLong, 0, num);
        writeBytes(os, bytesForLong);
    }

    public static void writeLongLE(OutputStream os, long num)
        throws Exception
    {
        if ( bigEndianArchitecture ) {
            long2byteArrayInvert(bytesForLong, 0, num);
        }
        long2byteArrayDirect(bytesForLong, 0, num);
        writeBytes(os, bytesForLong);
    }

    public static String readAsciiFixedSizeString(InputStream is, int size) throws Exception
    {
        byte characters[] = new byte[size];
        char letter;
        String msg = "";
        int i;

        readBytes(is, characters);
        for ( i = 0; i < size && characters[0] != 0x00; i++ ) {
            letter = (char)characters[i];
            if ( letter != 0x00 ) {
                msg = msg + letter;
            }
        }

        return msg;
    }

    public static String readAsciiString(InputStream is) throws Exception
    {
        byte character[] = new byte[1];
        char letter;
        String msg = "";

        do {
            readBytes(is, character);
            letter = (char)character[0];
            if ( character[0] != 0x00 ) {
                msg = msg + letter;
            }
        } while ( character[0] != 0x00 );

        return msg;
    }

    public static String readUtf8String(InputStream is) throws Exception
    {
        byte character[] = new byte[1];
        char letter;
        String msg = "";
        byte a[] = new byte[2];

        do {
            readBytes(is, character);
            letter = (char)character[0];
            if ( character[0] != 0x00 && ((letter >> 7) == 0) ) {
                msg = msg + letter;
            }
            else if ( character[0] != 0x00 ) {
                a[0] = character[0];
                if ( is.available() >= 1 ) {
                    readBytes(is, character);
                    a[1] = character[0];
                    String cc;
                    cc = buildUtf8Char(a);
		    if ( cc != null ) {
                        msg += cc;
		    }
		    else {
			System.out.println("* UNHANDLED UTF! ********************************************************** ->" + msg);
		    }
                }
            }
        } while ( character[0] != 0x00 );

        return msg;
    }

    public static String buildUtf8Char(byte arr[])
    {
        String c = null;
        int a = VSDK.signedByte2unsignedInteger(arr[0]);
        int b = VSDK.signedByte2unsignedInteger(arr[1]);

        if ( ((a >> 5) == 0x06) &&
             ((b >> 6) == 0x02) ) {
            c = new String(arr);
        }
        else {
            System.out.println("VSDK: Unhandled UTF-8 binary encoding!");
            System.out.println("  - Byte 0: " + a + " / " + (a >> 5) );
            System.out.println("  - Byte 1: " + b + " / " + (b >> 6) );
            return null;
        }
        return c;
    }

    public static String readUtf8Line(InputStream is) throws Exception
    {
        byte character[] = new byte[1];
        char letter;
        StringBuffer msg = new StringBuffer("");
        byte a[] = new byte[2];

        do {
            if ( is.available() < 1 ) return "";
            readBytes(is, character);
            letter = (char)character[0];
            if ( character[0] != '\n' && character[0] != '\r' &&
                 ((letter >> 7) == 0) ) {
                msg.append(letter);
            }
            else if ( character[0] != '\n' && character[0] != '\r' ) {
                a[0] = character[0];
                if ( is.available() >= 1 ) {
                    readBytes(is, character);
                    a[1] = character[0];
                    msg.append(buildUtf8Char(a));
                }
            }
        } while ( character[0] != '\n' );

        return msg.toString();
    }

    public static String readAsciiLine(InputStream is) throws Exception
    {
        byte character[] = new byte[1];
        char letter;
        StringBuffer msg = new StringBuffer("");

        do {
            if ( is.available() < 1 ) return "";
            readBytes(is, character);
            letter = (char)character[0];
            if ( character[0] != '\n' && character[0] != '\r' ) {
                msg.append(letter);
            }
        } while ( character[0] != '\n' );

        return msg.toString();
    }

    private static boolean isInSet(byte key, byte set[])
    {
        int i;

        for ( i = 0; i < set.length; i++ ) {
            if ( key == set[i] ) {
                return true;
            }
        }
        return false;
    }

    public static String readAsciiToken(InputStream is, byte separators[]) throws Exception
    {
        byte character[] = new byte[1];
        char letter;
        String msg = "";
        int i;

        do {
            readBytes(is, character);
            if ( !isInSet(character[0], separators) ) {
                msg = msg + ((char)character[0]);
            }
        } while ( !isInSet(character[0], separators) );

        return msg;
    }

    public static void
    writeAsciiString(OutputStream writer, String cad)
        throws Exception
    {
        byte arr[];
        arr = cad.getBytes();
        writer.write(arr, 0, arr.length);

        byte end[] = new byte[1];
        end[0] = '\0';
        writer.write(end, 0, end.length);
    }

    public static void
    writeUtf8String(OutputStream writer, String cad) throws Exception
    {
        byte arr[];
        arr = cad.getBytes();
        writer.write(arr, 0, arr.length);

        byte end[] = new byte[1];
        end[0] = '\0';
        writer.write(end, 0, end.length);
    }

    public static void
    writeAsciiLine(OutputStream writer, String cad)
        throws Exception
    {
        byte arr[];
        arr = cad.getBytes();
        writer.write(arr, 0, arr.length);

        byte end[] = new byte[1];
        end[0] = '\n';
        writer.write(end, 0, end.length);
    }

    public static void
    writeUtf8Line(OutputStream writer, String cad)
        throws Exception
    {
        byte arr[];
        arr = cad.getBytes();
        writer.write(arr, 0, arr.length);

        byte end[] = new byte[1];
        end[0] = '\n';
        writer.write(end, 0, end.length);
    }

    /**
    Given the name of a native library, this method tries to determine
    wheter it is available or not.  Takes into account the cross-platform
    differences, and it is supposed to check if a System.loadLibrary
    call for givel library will succeed or not.

    Use this method to anticipate any problem before it fails, so a better
    user feedback instruction can be given instead of waiting for an exception
    to be thrown.  Some libraries, as JOGL fails to return to the application
    the exception of a failed System.loadLibrary, so this method is useful
    in bettering the user feedback for this kind of circumstance.
    */
    public static boolean verifyLibrary(String libname) {
        String nativeLibname = System.mapLibraryName(libname);
        String paths = System.getProperty("java.library.path");
        String os = System.getProperty("os.name").toLowerCase();

        if ( os.startsWith("linux") || os.startsWith("solaris") ||
             os.startsWith("unix") ) {
            paths = paths.concat(":/lib");
            paths = paths.concat(":/usr/lib");
            paths = paths.concat(":/usr/local/lib");
            paths = paths.concat(":/usr/X11R6/lib");
            paths = paths.concat(":/usr/X11R6/lib64");
            paths = paths.concat(":/usr/openwin/lib");
            paths = paths.concat(":/usr/dt/lib");
            paths = paths.concat(":/lib64");
            paths = paths.concat(":/usr/lib64");
            paths = paths.concat(":/usr/local/lib64");
            paths = paths.concat(":" + System.getenv("LD_LIBRARY_PATH"));
        }

        String separator = File.pathSeparator;                
        StringTokenizer st = new StringTokenizer(paths, separator);
        String token;
        String concat = File.separator;
        while ( st.hasMoreTokens() ) {
            token = st.nextToken();
            File directory = new File(token);
            if ( !directory.isDirectory()  ) {
                continue;
            }
            File file = new File(token + concat + nativeLibname);
            if ( file.exists() ) {
                return true;
            }
                        
        }
        return false;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
