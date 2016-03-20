package com.liangyuen.idea.raspberryRFID;

/**
 * Created by Liang on 2016/3/13.
 */


import com.liangyuen.util.RaspRC522;
import com.liangyuen.util.Convert;


public class WiringPiSPIExample
{
    public static void main(String args[]) throws InterruptedException
    {
        RaspRC522 rc522=new RaspRC522();
        int back_len[]=new int[1];
        byte tagid[]=new byte[5];
        int i,status;
        String strUID;
        byte sector=15,block=3;

        //寻卡，获得序列号
        if(rc522.Request(RaspRC522.PICC_REQIDL, back_len) == rc522.MI_OK)
            System.out.println("Detecte card:"+back_len[0]);
        //防冲撞
        if(rc522.AntiColl(tagid) != RaspRC522.MI_OK)
        {
            System.out.println("anticoll error");
            return;
        }

        //显示序列号
        strUID= Convert.bytesToHex(tagid);
        //System.out.println(strUID);
        //System.out.println("Card Read UID:" + tagid[0] + "," + tagid[1] + "," + tagid[2] + "," + tagid[3]);
        System.out.println("Card Read UID:" + strUID.substring(0,2) + "," +
                strUID.substring(2,4) + "," +
                strUID.substring(4,6) + "," +
                strUID.substring(6,8));

        //默认密钥
        byte[] defaultkey=new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
        //Select the scanned tag，选中指定序列号的卡
        int size=rc522.Select_Tag(tagid);
        System.out.println("Size="+size);

        //Authenticate,A密钥验证卡,

        status = rc522.Auth_Card(RaspRC522.PICC_AUTHENT1A, sector,block, defaultkey, tagid);
        if(status != RaspRC522.MI_OK)
        {
            System.out.println("Authenticate error");
            return;
        }

        //卡各扇区初始控制字FF078069，15扇区改为08778F69,A密钥改为330123
        byte data[]=new byte[16];
        byte controlbytes[]=new byte[]{(byte)0x08,(byte)0x77,(byte)0x8f,(byte) 0x69};
        System.arraycopy(controlbytes,0,data,6,4);
        byte[] keyA=new byte[]{(byte)0x03,(byte)0x03,(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03};
        byte[] keyB=new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
        System.arraycopy(keyA,0,data,0,6);
        System.arraycopy(keyB,0,data,10,6);
        status=rc522.Write(sector,block,data);
        if( status== RaspRC522.MI_OK)
            System.out.println("Write data finished");
        else
        {
            System.out.println("Write data error,status="+status);
            return;
        }


    }

}
