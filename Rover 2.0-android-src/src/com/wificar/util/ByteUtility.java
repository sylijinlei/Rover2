package com.wificar.util;



public class ByteUtility {
	static char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	public static String convertByteArrayToString(byte[] bytes) {
		String temp = "";
		for (int i = 0; i < bytes.length; i++) {
			temp = temp + ":" + Byte.toString(bytes[i]);
		}
		return temp;
	}

	public static String bytesToHex(byte[] b) {

		return bytesToHex(b, 0, b.length);
	}

	public static String bytesToHex(byte[] b, int off, int len) {
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < len; j++)
			buf.append(byteToHex(b[off + j]));
		return buf.toString();
	}

	public static String byteToHex(byte b) {
		char[] a = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
		return new String(a);
	}

	public static String byteArrayToHex(byte[] inByteArray, int iOffset,
			int iLen) {
		int iResult = 0;
		// ByteBuffer bb = ByteBuffer.wrap(inByteArray);
		byte[] ch = new byte[iLen];

		for (int x = 0; x < iLen; x++) {
			// ch[x] = bb.getChar(x+iOffset);
			ch[x] = inByteArray[x + iOffset];
		}

		return ByteUtility.bytesToHex(ch);
	}
	public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }
	public static short[] bytesToShorts(byte[] b) {
        short[] s = new short[b.length/2];
        for(int i=0;i<b.length/2;i++){
        	//Byte B = new Byte(b[i]);
        	short s0 = (short) (b[2*i] & 0xff);// 最低位
        	short s1 = (short) (b[2*i+1] & 0xff);
        	s1 <<= 8;
        	s[i] = (short) (s0 | s1);
        	//s[i] = (short) (s0);
        	//s[i] = B.shortValue();
        }
        return s;
    }
	public static int byteArrayToInt(byte[] inByteArray, int iOffset, int iLen) {
		int iResult = 0;

		for (int x = 0; x < iLen; x++) {
			if ((x == 0) && (inByteArray[iOffset + (iLen - 1) - x] < 0))
				iResult = iResult
						| (0xffffffff & inByteArray[iOffset + (iLen - 1) - x]);
			else
				iResult = iResult
						| (0x000000ff & inByteArray[iOffset + (iLen - 1) - x]);
			if (x < (iLen - 1))
				iResult = iResult << 8;
		}

		return iResult;
	}

	public static int byteArrayToInt(byte[] b, int offset) throws Exception {
		return (b[0 + offset] << 24) + ((b[1 + offset] & 0xFF) << 16)
				+ ((b[2 + offset] & 0xFF) << 8) + (b[3 + offset] & 0xFF);
	}

	public static long byteArrayToLong(byte[] inByteArray, int iOffset, int iLen) {
		long iResult = 0;

		for (int x = 0; x < iLen; x++) {
			if ((x == 0) && (inByteArray[iOffset + (iLen - 1) - x] < 0))
				iResult = iResult
						| (0xffffffff & inByteArray[iOffset + (iLen - 1) - x]);
			else
				iResult = iResult
						| (0x000000ff & inByteArray[iOffset + (iLen - 1) - x]);
			if (x < (iLen - 1))
				iResult = iResult << 8;
		}

		return iResult;
	}
}
