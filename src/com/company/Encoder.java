package com.company;
import java.util.BitSet;
public class Encoder {

    public static String gammaCode(int num) {
        String gammaCode = "";
        String binaryRepresentation = Integer.toBinaryString(num);
        String offset = binaryRepresentation.substring(1);
        String unaryValue = getUnaryCode(offset.length());
        gammaCode = unaryValue.concat("0").concat(offset);
        return gammaCode;
    }

    public static String deltaCode(int num) {
        String deltaCode = "";
        String binaryRepresentation = Integer.toBinaryString(num);
        String gammaCode = gammaCode(binaryRepresentation.length());
        String offset = binaryRepresentation.substring(1);
        deltaCode = gammaCode.concat(offset);
        return deltaCode;
    }

    private static String getUnaryCode(int length) {
        String unaryValue = "";
        for(int i = 0; i < length; i++) {
            unaryValue = unaryValue.concat("1");
        }
        return unaryValue;
    }

    public static byte[] convertToByteArray(String code) {
        BitSet bitSet = new BitSet(code.length());
        for(int i = 0; i < code.length(); i ++){
            Boolean value = code.charAt(i) == '1' ? true : false;
            bitSet.set(i, value);
        }
        return bitSet.toByteArray();
    }
}
