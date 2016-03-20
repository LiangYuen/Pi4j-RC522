package com.liangyuen.util;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.Spi;
/**
 * Created by Liang on 2016/3/17,originated from  Python RC522
 */
public class RaspRC522
{
    private int NRSTPD = 22;        //RST Pin number,default 22
    private int Speed=500000;
    private int SPI_Channel=0;

    private final int MAX_LEN = 16; //扇区字节数

    //RC522命令字
    public static final byte PCD_IDLE       = 0x00;
    public static final byte PCD_AUTHENT    = 0x0E;
    public static final byte PCD_RECEIVE    = 0x08;
    public static final byte PCD_TRANSMIT   = 0x04;
    public static final byte PCD_TRANSCEIVE = 0x0C;
    public static final byte PCD_RESETPHASE = 0x0F;
    public static final byte PCD_CALCCRC    = 0x03;

    //PICC命令字
    public  static final byte PICC_REQIDL    = 0x26;
    public  static final byte PICC_REQALL    = 0x52;
    public  static final byte PICC_ANTICOLL  = (byte)0x93;
    public  static final byte PICC_SElECTTAG = (byte)0x93;
    public  static final byte PICC_AUTHENT1A = 0x60;
    public  static final byte PICC_AUTHENT1B = 0x61;
    public  static final byte PICC_READ      = 0x30;
    public  static final byte PICC_WRITE     = (byte)0xA0;
    public  static final byte PICC_DECREMENT = (byte)0xC0;
    public  static final byte PICC_INCREMENT = (byte)0xC1;
    public  static final byte PICC_RESTORE   = (byte)0xC2;
    public  static final byte PICC_TRANSFER  = (byte)0xB0;
    public  static final byte PICC_HALT      = 0x50;

    //返回状态
    public static final int MI_OK       = 0;
    public static final int MI_NOTAGERR = 1;
    public static final int MI_ERR      = 2;

    //RC522寄存器地址
    public static final byte Reserved00     = 0x00;
    public static final byte CommandReg     = 0x01;
    public static final byte CommIEnReg     = 0x02;
    public static final byte DivlEnReg      = 0x03;
    public static final byte CommIrqReg     = 0x04;
    public static final byte DivIrqReg      = 0x05;
    public static final byte ErrorReg       = 0x06;
    public static final byte Status1Reg     = 0x07;
    public static final byte Status2Reg     = 0x08;
    public static final byte FIFODataReg    = 0x09;
    public static final byte FIFOLevelReg   = 0x0A;
    public static final byte WaterLevelReg  = 0x0B;
    public static final byte ControlReg     = 0x0C;
    public static final byte BitFramingReg  = 0x0D;
    public static final byte CollReg        = 0x0E;
    public static final byte Reserved01     = 0x0F;

    public static final byte Reserved10     = 0x10;
    public static final byte ModeReg        = 0x11;
    public static final byte TxModeReg      = 0x12;
    public static final byte RxModeReg      = 0x13;
    public static final byte TxControlReg   = 0x14;
    public static final byte TxAutoReg      = 0x15;
    public static final byte TxSelReg       = 0x16;
    public static final byte RxSelReg       = 0x17;
    public static final byte RxThresholdReg = 0x18;
    public static final byte DemodReg       = 0x19;
    public static final byte Reserved11     = 0x1A;
    public static final byte Reserved12     = 0x1B;
    public static final byte MifareReg      = 0x1C;
    public static final byte Reserved13     = 0x1D;
    public static final byte Reserved14     = 0x1E;
    public static final byte SerialSpeedReg = 0x1F;

    public static final byte Reserved20        = 0x20;
    public static final byte CRCResultRegM     = 0x21;
    public static final byte CRCResultRegL     = 0x22;
    public static final byte Reserved21        = 0x23;
    public static final byte ModWidthReg       = 0x24;
    public static final byte Reserved22        = 0x25;
    public static final byte RFCfgReg          = 0x26;
    public static final byte GsNReg            = 0x27;
    public static final byte CWGsPReg          = 0x28;
    public static final byte ModGsPReg         = 0x29;
    public static final byte TModeReg          = 0x2A;
    public static final byte TPrescalerReg     = 0x2B;
    public static final byte TReloadRegH       = 0x2C;
    public static final byte TReloadRegL       = 0x2D;
    public static final byte TCounterValueRegH = 0x2E;
    public static final byte TCounterValueRegL = 0x2F;

    public static final byte Reserved30      = 0x30;
    public static final byte TestSel1Reg     = 0x31;
    public static final byte TestSel2Reg     = 0x32;
    public static final byte TestPinEnReg    = 0x33;
    public static final byte TestPinValueReg = 0x34;
    public static final byte TestBusReg      = 0x35;
    public static final byte AutoTestReg     = 0x36;
    public static final byte VersionReg      = 0x37;
    public static final byte AnalogTestReg   = 0x38;
    public static final byte TestDAC1Reg     = 0x39;
    public static final byte TestDAC2Reg     = 0x3A;
    public static final byte TestADCReg      = 0x3B;
    public static final byte Reserved31      = 0x3C;
    public static final byte Reserved32      = 0x3D;
    public static final byte Reserved33      = 0x3E;
    public static final byte Reserved34      = 0x3F;


    public RaspRC522(int Speed,int PinReset)
    {
        this.NRSTPD=PinReset;
        if(Speed < 500000 || Speed > 32000000)
        {
            System.out.println("Speed out of range");
            return;
        }
        else this.Speed=Speed;
        RC522_Init();
    }

    public RaspRC522()
    {
        this.Speed=500000;
        RC522_Init();
    }

    public void RC522_Init()
    {
        Gpio.wiringPiSetup();           //Enable wiringPi pin schema
        int fd=Spi.wiringPiSPISetup(SPI_Channel,Speed);
        if (fd <= -1)
        {
            System.out.println(" --> Failed to set up  SPI communication");
            //Stop code when error happened
            return;
        }
        else
        {
            //System.out.println(" --> Successfully loaded SPI communication");
        }

        Gpio.pinMode(NRSTPD, Gpio.OUTPUT);
        Gpio.digitalWrite(NRSTPD, Gpio.HIGH);
        Reset();
        Write_RC522(TModeReg, (byte)0x8D);
        Write_RC522(TPrescalerReg, (byte)0x3E);
        Write_RC522(TReloadRegL, (byte) 30);
        Write_RC522(TReloadRegH, (byte) 0);
        Write_RC522(TxAutoReg, (byte) 0x40);
        Write_RC522(ModeReg, (byte) 0x3D);
        AntennaOn();
    }

    private void Reset()
    {
        Write_RC522(CommandReg, PCD_RESETPHASE);
    }

    private void Write_RC522(byte address,byte value)
    {
        byte data[]=new byte[2];
        data[0]=(byte) ((address << 1) & 0x7E);
        data[1]=value;
        int result=Spi.wiringPiSPIDataRW(SPI_Channel, data);
        if(result == -1)
            System.out.println("Device write  error,address="+address+",value="+value);
    }

    private byte Read_RC522(byte address)
    {
        byte data[]=new byte[2];
        data[0]=(byte) (((address << 1) & 0x7E) | 0x80);
        data[1]=0;
        int result=Spi.wiringPiSPIDataRW(SPI_Channel, data);
        if(result == -1)
            System.out.println("Device read  error,address="+address);
        return data[1];
    }

    private void SetBitMask(byte address,byte mask)
    {
        byte value=Read_RC522(address);
        Write_RC522(address,(byte)(value|mask));
    }

    private void ClearBitMask(byte address,byte mask)
    {
        byte value=Read_RC522(address);
        Write_RC522(address,(byte)( value&(~mask)));
    }

    private void AntennaOn()
    {
        byte value=Read_RC522(TxControlReg);
     //   if((value & 0x03) != 0x03)
        SetBitMask(TxControlReg,(byte) 0x03);
    }

    private void AntennaOff()
    {
        ClearBitMask(TxControlReg,(byte) 0x03);
    }

    //back_data-最长不超过Length=16;
    //back_data-返回数据
    //back_bits-返回比特数
    //backLen-返回字节数
    private int Write_Card(byte command,byte []data,int dataLen,byte[]back_data,int[]back_bits,int[]backLen)
    {
        int status=MI_ERR;
        byte irq=0,irq_wait=0,lastBits=0;
        int n=0,i=0;

        backLen[0]=0;
        if(command == PCD_AUTHENT)
        {
            irq = 0x12;
            irq_wait=0x10;
        }
        else if(command == PCD_TRANSCEIVE)
        {
            irq = 0x77;
            irq_wait=0x30;
        }

        Write_RC522(CommIEnReg, (byte)(irq|0x80));
        ClearBitMask(CommIrqReg, (byte)0x80);
        SetBitMask(FIFOLevelReg, (byte)0x80);

        Write_RC522(CommandReg,PCD_IDLE);

        for(i=0;i<dataLen;i++)
            Write_RC522(FIFODataReg, data[i]);

        Write_RC522(CommandReg, command);
        if(command == PCD_TRANSCEIVE)
            SetBitMask(BitFramingReg, (byte)0x80);

        i=2000;
        while (true)
        {
            n = Read_RC522(CommIrqReg);
            i--;
            if ((i == 0) || (n & 0x01) > 0 || (n & irq_wait) > 0)
            {
                //System.out.println("Write_Card i="+i+",n="+n);
                break;
            }
        }
        ClearBitMask(BitFramingReg, (byte)0x80);

        if(i != 0)
        {
            if ((Read_RC522(ErrorReg) & 0x1B)==0x00)
            {
                status = MI_OK;
                if ((n & irq & 0x01) > 0)
                    status = MI_NOTAGERR;
                if (command == PCD_TRANSCEIVE)
                {
                    n = Read_RC522(FIFOLevelReg);
                    lastBits = (byte) (Read_RC522(ControlReg) & 0x07);
                    if (lastBits != 0)
                        back_bits[0] = (n - 1) * 8 + lastBits;
                    else
                        back_bits[0] = n * 8;

                    if (n == 0) n = 1;
                    if (n > this.MAX_LEN) n = this.MAX_LEN;
                    backLen[0] = n;
                    for (i = 0; i < n; i++)
                        back_data[i] = Read_RC522(FIFODataReg);
                }
            }
            else
                status = MI_ERR;
        }
        return  status;
    }

    public int Request(byte req_mode,int []back_bits) //参数为1字节数组
    {
        int status;
        byte tagType[]=new byte[1];
        byte data_back[]=new byte[16];
        int backLen[]=new int[1];

        Write_RC522(BitFramingReg, (byte)0x07);

        tagType[0]=req_mode;
        back_bits[0]=0;
        status=Write_Card(PCD_TRANSCEIVE,tagType,1,data_back,back_bits,backLen);
        if (status != MI_OK || back_bits[0] != 0x10)
        {
            //System.out.println("status="+status+",back_bits[0]="+back_bits[0]);
            status = MI_ERR;
        }

        return status;
    }

    //Anti-collision detection.
    //Returns tuple of (error state, tag ID).
    //back_data-5字节 4字节tagid+1字节校验
    public int AntiColl(byte[]back_data)
    {
        int status;
        byte []serial_number = new byte[2];   //2字节命令
        int serial_number_check = 0;
        int backLen[]=new int[1];
        int back_bits[]=new int[1];
        int i;

        Write_RC522(BitFramingReg, (byte)0x00);
        serial_number[0]=PICC_ANTICOLL;
        serial_number[1]=0x20;
        status=Write_Card(PCD_TRANSCEIVE,serial_number,2,back_data,back_bits,backLen);
        if(status == MI_OK)
        {
            if(backLen[0] == 5)
            {
                for(i=0;i<4;i++)
                    serial_number_check ^=back_data[i];
                if(serial_number_check != back_data[4])
                {
                    status = MI_ERR;
                    System.out.println("check error");
                }
            }
            else
            {
                status = MI_OK;
                System.out.println("backLen[0]="+backLen[0]);
            }
        }
        return status;
    }

    //CRC值放在data[]最后两字节
    private void Calculate_CRC(byte []data)
    {
        int i,n;
        ClearBitMask(DivIrqReg, (byte)0x04);
        SetBitMask(FIFOLevelReg, (byte)0x80);

        for(i=0;i<data.length-2;i++)
            Write_RC522(FIFODataReg, data[i]);
        Write_RC522(CommandReg, PCD_CALCCRC);
        i = 255;
        while(true)
        {
            n = Read_RC522(DivIrqReg);
            i--;
            if ((i == 0) || ((n & 0x04)>0))
                break;
        }
        data[data.length-2]=Read_RC522(CRCResultRegL);
        data[data.length-1]=Read_RC522(CRCResultRegM);
    }

    //uid-5字节数组,存放序列号
    //返值是大小
    public int Select_Tag(byte []uid)
    {
        int status;
        byte data[]=new byte[9];
        byte back_data[]=new byte[this.MAX_LEN];
        int back_bits[]=new int[1];
        int backLen[]=new int[1];
        int i,j;

        data[0]=PICC_SElECTTAG;
        data[1]=0x70;
        for(i=0,j=2;i<5;i++,j++)
            data[j]=uid[i];
        Calculate_CRC(data);

        status=Write_Card(PCD_TRANSCEIVE, data,9,back_data,back_bits,backLen);
        if (status == MI_OK && back_bits[0] == 0x18) return back_data[0];
        else return 0;
    }
    //Authenticates to use specified block address. Tag must be selected using select_tag(uid) before auth.
    //auth_mode-RFID.auth_a or RFID.auth_b
    //block_address- used to authenticate
    //key-list or tuple(数组) with six bytes key
    //uid-list or tuple with four bytes tag ID
    public int Auth_Card(byte auth_mode,byte block_address,byte []key,byte []uid)
    {
        int status;
        byte data[]=new byte[12];
        byte back_data[]=new byte[this.MAX_LEN];
        int back_bits[]=new int[1];
        int backLen[]=new int[1];
        int i,j;

        data[0]=auth_mode;
        data[1]=block_address;
        for(i=0,j=2;i<6;i++,j++)
            data[j]=key[i];
        for(i=0,j=8;i<4;i++,j++)
            data[j]=uid[i];

        status=Write_Card(PCD_AUTHENT, data,12,back_data,back_bits,backLen);
        if((Read_RC522(Status2Reg) & 0x08)== 0) status=MI_ERR;
        return status;
    }

    //
    public int Auth_Card(byte auth_mode,byte sector,byte block,byte []key,byte []uid)
    {
        return Auth_Card(auth_mode,Sector2BlockAddress(sector,block),key,uid);
    }

    //Ends operations with Crypto1 usage.
    public void Stop_Crypto()
    {
        ClearBitMask(Status2Reg, (byte) 0x08);
    }

    //Reads data from block. You should be authenticated before calling read.
    //Returns tuple of (result state, read data).
    //block_address
    //back_data-data to be read,16 bytes
    public int Read(byte block_address,byte[]back_data)
    {
        int status;
        byte data[]=new byte[4];
        int back_bits[]=new int[1];
        int backLen[]=new int[1];
        int i,j;

        data[0]=PICC_READ;
        data[1]=block_address;
        Calculate_CRC(data);
        status=Write_Card(PCD_TRANSCEIVE, data,data.length,back_data,back_bits,backLen);
        if(backLen[0] == 16) status=MI_OK;
        return status;
    }

    //
    public int Read(byte sector,byte block,byte[]back_data)
    {
        return Read(Sector2BlockAddress(sector,block),back_data);
    }

    //Writes data to block. You should be authenticated before calling write.
    //Returns error state.
    //data-16 bytes
    public int Write(byte block_address,byte[]data)
    {
        int status;
        byte buff[]=new byte[4];
        byte buff_write[]=new byte[data.length+2];
        byte back_data[]=new byte[this.MAX_LEN];
        int back_bits[]=new int[1];
        int backLen[]=new int[1];
        int i;

        buff[0]=PICC_WRITE;
        buff[1]=block_address;
        Calculate_CRC(buff);
        status=Write_Card(PCD_TRANSCEIVE, buff,buff.length,back_data,back_bits,backLen);
        //System.out.println("write_card  status="+status);
        //System.out.println("back_bits[0]="+back_bits[0]+",(back_data[0] & 0x0F)="+(back_data[0] & 0x0F));
        if(status != MI_OK || back_bits[0] !=4 || (back_data[0] & 0x0F) != 0x0A) status=MI_ERR;
        if(status == MI_OK)
        {
            for (i=0;i<data.length;i++)
                buff_write[i]=data[i];
            Calculate_CRC(buff_write);
            status=Write_Card(PCD_TRANSCEIVE, buff_write,buff_write.length,back_data,back_bits,backLen);
            //System.out.println("write_card data status="+status);
            //System.out.println("back_bits[0]="+back_bits[0]+",(back_data[0] & 0x0F)="+(back_data[0] & 0x0F));
            if(status != MI_OK ||back_bits[0] !=4 || (back_data[0] & 0x0F) != 0x0A) status=MI_ERR;
        }
        return  status;
    }

    //
    public int Write(byte sector,byte block,byte[]data)
    {
        return Write(Sector2BlockAddress(sector,block),data);
    }

    //导出1K字节,64个扇区
    public byte[] DumpClassic1K(byte []key, byte[]uid)
    {
        int i,status;
        byte []data=new byte[1024];
        byte []buff=new byte[16];

        for(i=0;i<64;i++)
        {
            status=Auth_Card(PICC_AUTHENT1A, (byte) i, key, uid);
            if(status == MI_OK)
            {
                status=Read((byte) i,buff);
                if(status == MI_OK)
                    System.arraycopy(buff,0,data,i*64,16);
            }
        }
        return data;
    }

    //Convert sector  to blockaddress
    //sector-0~15
    //block-0~3
    //return blockaddress
    private byte Sector2BlockAddress(byte sector,byte block)
    {
        if(sector <0 || sector >15 || block <0 || block >3) return (byte)(-1);
        return (byte)(sector*4+block);
    }

    //uid-5 bytes
    public int Select_MirareOne(byte[]uid)
    {
        int back_bits[]=new int[1];
        byte tagid[]=new byte[5];
        int status;

        status=Request(RaspRC522.PICC_REQIDL, back_bits);
        if(status != MI_OK) return status;
        status=AntiColl(tagid);
        if(status != MI_OK) return status;
        Select_Tag(tagid);
        System.arraycopy(tagid,0,uid,0,5);

        return status;
    }

}
