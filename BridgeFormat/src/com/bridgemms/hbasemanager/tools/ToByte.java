package com.bridgemms.hbasemanager.tools;

public class ToByte {
	

    /**
     * doubleת��byte
     * @param x ��תdouble
     * @return byte����
     */
    public static byte[] doubleToByte(double x) {
        byte[] b = new byte[8];
        long l = Double.doubleToLongBits(x);
        for (int i = 0; i < 8; i++) {
            b[i] = new Long(l).byteValue();
            l = l >> 8;
        }
        return b;
    }

    /**
     * ͨ��byte����ȡ��double
     * @param ��תbyte����
     * @return ת�����double��ֵ
     */
    public static double byteToDouble(byte[] b) {
        long l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);
        l &= 0xffffffffl;
        l |= ((long) b[4] << 32);
        l &= 0xffffffffffl;
        l |= ((long) b[5] << 40);
        l &= 0xffffffffffffl;
        l |= ((long) b[6] << 48);
        l &= 0xffffffffffffffl;
        l |= ((long) b[7] << 56);
        return Double.longBitsToDouble(l);
    }
    
    public static void main(String[] args) {
		double value = Math.random()*100;
		System.out.println(value+"    "+doubleToByte(value).toString()+"    "+byteToDouble(doubleToByte(value)));
	}
}
