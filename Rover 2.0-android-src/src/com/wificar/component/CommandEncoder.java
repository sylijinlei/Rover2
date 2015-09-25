package com.wificar.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

import com.wificar.WificarActivity;
import com.wificar.util.BlowFish;
import com.wificar.util.BlowFishEncryptUtil;
import com.wificar.util.ByteUtility;

public class CommandEncoder {
	public final static int HEAD_LEN = 23;

	public final static int MEDIA_LOGIN_REQ = 0;

	public final static int LOGIN_REQ = 0;
	public final static int LOGIN_RESP = 1;
	public final static int VERIFY_REQ = 2;
	public final static int VERIFY_RESP = 3;
	public final static int KEEP_ALIVE = 255;

	public final static int VIDEO_START_REQ = 4;
	public final static int VIDEO_START_RESP = 5;
	public final static int VIDEO_END = 6;
	public final static int VIDEO_FRAMEINTERVAL = 7;

	public final static int AUDIO_START_REQ = 8;
	public final static int AUDIO_START_RESP = 9;
	public final static int AUDIO_END = 10;

	public final static int TALK_START_REQ = 11;
	public final static int TALK_START_RESP = 12;
	public final static int TALK_END = 13;

	public final static int AUDIO_DATA = 2;
	public final static int VIDEO_DATA = 1;
	public final static int TALK_DATA = 3;

	public final static int DEVICE_CONTROL_REQ = 250;

	public final static int FETCH_BATTERY_POWER_REQ = 251;
	public final static int FETCH_BATTERY_POWER_RESP = 252;

	public final static int DECODER_CONTROL_REQ = 14;

	public final static String WIFICAR_OP = "MO_O";
	public final static String WIFICAR_VIDEO_OP = "MO_V";

	static class Protocol {
		byte[] header;
		int op = 0;
		byte preserve1 = 0x00;
		byte[] preserve2 = new byte[8];
		int contentLength = 0;
		long preserve3 = 0;
		byte[] content = new byte[0];

		public Protocol(byte[] header, int op, int contentLength, byte[] content) {
			this.header = header;
			this.op = op;
			this.contentLength = contentLength;
			this.content = content;
		}

		public int getOp() {
			return op;
		}

		public byte[] getContent() {
			return this.content;
		}

		public Protocol(byte[] packet) {
			this(packet, 0);
		}

		public Protocol(byte[] packet, int offset) {
			// ByteBuffer bb = ByteBuffer.wrap(packets);
			// bb.get(header, 0, 4);
			header = "MO_V".getBytes();
			// Log.d("wild3",":packet length:" + packet.length);
			op = CommandEncoder.byteArrayToInt(packet, offset + 4, 2);
			contentLength = CommandEncoder.byteArrayToInt(packet, offset + 15,
					4);

			if (contentLength > 0) {
				// Log.d("wild0", op+":packet length:" + packet.length);
				// Log.d("wild0", op+":content length:" + (int) contentLength);
				content = new byte[contentLength];

				System.arraycopy(packet, offset + HEAD_LEN, content, 0,
						(int) contentLength);
				// bb.get(content, 23, (int) contentLength);
			}
		}

		public byte[] output() throws IOException {

			int basicLength = 23;
			int extendLength = content.length;

			byte[] opCode = int16ToByteArray(op);
			byte[] extendLengthByte = int32ToByteArray(content.length);

			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			bOut.write(header);
			bOut.write(opCode);
			bOut.write(new byte[1]);
			bOut.write(new byte[8]);
			bOut.write(extendLengthByte);
			bOut.write(new byte[4]);
			bOut.write(content);

			// ByteBuffer bb = ByteBuffer.allocate(basicLength+extendLength);
			// byte[] encodedCmd = new byte[basicLength+extendLength];
			/*
			 * bb.put(header, 0, 4); bb.putInt(4, op); bb.putLong(15,
			 * contentLength);
			 * Log.d("wild0","basic:23,ext:"+content.length+",total:"+bb.);
			 * if(contentLength>0){ bb.put(content, 23, content.length); }
			 */

			return bOut.toByteArray();
		}
	}

	public static byte[] int32ToByteArray(int value) {

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = i * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
		/*
		 * return new byte[] { (byte)((value >> 24) & 0xff), (byte)((value >>
		 * 16) & 0xff), (byte)((value >> 8) & 0xff), (byte)((value >> 0) &
		 * 0xff), };
		 */
	}

	public static String int32ToByteHex(int value) {

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = i * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return ByteUtility.bytesToHex(b);
		/*
		 * return new byte[] { (byte)((value >> 24) & 0xff), (byte)((value >>
		 * 16) & 0xff), (byte)((value >> 8) & 0xff), (byte)((value >> 0) &
		 * 0xff), };
		 */
	}

	public static String int32ToByteHexR(int value) {

		byte[] b = new byte[] { (byte) ((value >> 24) & 0xff),
				(byte) ((value >> 16) & 0xff), (byte) ((value >> 8) & 0xff),
				(byte) ((value >> 0) & 0xff), };
		return ByteUtility.bytesToHex(b);
		/*
		 * return new byte[] { (byte)((value >> 24) & 0xff), (byte)((value >>
		 * 16) & 0xff), (byte)((value >> 8) & 0xff), (byte)((value >> 0) &
		 * 0xff), };
		 */
	}

	public static byte[] int32ToByteArrayR(int value) {

		return new byte[] { (byte) ((value >> 24) & 0xff),
				(byte) ((value >> 16) & 0xff), (byte) ((value >> 8) & 0xff),
				(byte) ((value >> 0) & 0xff), };

	}

	public static byte[] int16ToByteArray(int value) {

		byte[] b = new byte[2];
		for (int i = 0; i < 2; i++) {
			int offset = i * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
		/*
		 * return new byte[] { (byte)((value >> 24) & 0xff), (byte)((value >>
		 * 16) & 0xff), (byte)((value >> 8) & 0xff), (byte)((value >> 0) &
		 * 0xff), };
		 */
	}

	public static byte[] longToByteArray(long data) {
		return new byte[] { (byte) ((data >> 56) & 0xff),
				(byte) ((data >> 48) & 0xff), (byte) ((data >> 40) & 0xff),
				(byte) ((data >> 32) & 0xff), (byte) ((data >> 24) & 0xff),
				(byte) ((data >> 16) & 0xff), (byte) ((data >> 8) & 0xff),
				(byte) ((data >> 0) & 0xff), };
	}

	/*
	 * public static byte[] int32ToByteArray(long value) { byte[] b = new
	 * byte[4]; for (int i = 0; i < 4; i++) { int offset = i * 8; b[i] = (byte)
	 * ((value >>> offset) & 0xFF); } return b; }
	 */
	public static byte[] int8ToByteArray(int value) {
		byte[] b = new byte[1];
		for (int i = 0; i < 1; i++) {
			int offset = i * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	public static String byteArrayToString(byte[] inByteArray, int iOffset,
			int iLen) {
		int iResult = 0;
		// ByteBuffer bb = ByteBuffer.wrap(inByteArray);
		byte[] ch = new byte[iLen];

		for (int x = 0; x < iLen; x++) {
			// ch[x] = bb.getChar(x+iOffset);
			ch[x] = inByteArray[x + iOffset];
		}

		return new String(ch).trim();
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

	public static byte[] cmdFetchBatteryPowerReq() throws IOException {
		//Log.d("wild1", "send op:" + FETCH_BATTERY_POWER_REQ);
		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(),
				FETCH_BATTERY_POWER_REQ, 0, new byte[0]);
		return cmd.output();
	}

	public static byte[] cmdMediaLoginReq(int linkId) throws IOException {
		//Log.d("wild0", "mediaLoginReq");
		byte[] vb1 = int32ToByteArray(linkId);
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		bOut.write(vb1);
		CommandEncoder.Protocol cmd = new Protocol("MO_V".getBytes(),
				MEDIA_LOGIN_REQ, bOut.size(), bOut.toByteArray());
		return cmd.output();
	}

	public static byte[] cmdLoginReq(int v1, int v2, int v3, int v4)
			throws IOException {
		// ByteBuffer bb = ByteBuffer.allocate(16);

		// bb.put(int32ToByteArray(v1));
		// Log.d("wild0","input:("+int32ToByteArray(v1).length+")"+ByteUtility.convertByteArrayToString(Long.t));
		// bb.put(int32ToByteArray(v2));
		// bb.put(int32ToByteArray(v3));
		// bb.put(int32ToByteArray(v4));
		byte[] vb1 = int32ToByteArray(v1);

		// BlowFishEncryptUtil.Bits32ToBytes(v1, vb1, 0);
		byte[] vb2 = int32ToByteArray(v2);
		// BlowFishEncryptUtil.Bits32ToBytes(v2, vb2, 0);
		byte[] vb3 = int32ToByteArray(v3);
		// BlowFishEncryptUtil.Bits32ToBytes(v3, vb3, 0);
		byte[] vb4 = int32ToByteArray(v4);
		// BlowFishEncryptUtil.Bits32ToBytes(v4, vb4, 0);
		//Log.d("wild0", "cmdLoginReq");
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		bOut.write(vb1);
		bOut.write(vb2);
		bOut.write(vb3);
		bOut.write(vb4);

		//Log.d("wild0","input:(" + bOut.size() + ")"+ ByteUtility.bytesToHex(bOut.toByteArray()));
		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(), 0, 16,
				bOut.toByteArray());
		return cmd.output();
	}

	/*
	 * public static byte[] cmdLoginReq() throws IOException{ //ByteBuffer bb =
	 * ByteBuffer.allocate(16); CommandEncoder.Protocol cmd = new
	 * Protocol("MO_O".getBytes(),0,0, new byte[0]); return cmd.output(); }
	 */

	// public static CommandEncoder.Protocol createTalkData()
	// throws IOException {
	// return createTalkData(data.getD)
	// }/
	public static CommandEncoder.Protocol createTalkData(TalkData data)
			throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] tickTime = int32ToByteArray(data.getTicktime());
		byte[] serial = int32ToByteArray(data.getSerial());
		byte[] timestamp = int32ToByteArray(data.getTimestamp());
		byte[] audioFormat = int8ToByteArray(0);
		byte[] length = int32ToByteArray(data.getData().length);
		out.write(tickTime);
		out.write(serial);
		out.write(timestamp);
		out.write(audioFormat);
		out.write(length);
		out.write(data.getData());
		//Log.d("wild2", "talk content size:" + out.size());
		//Log.d("wild2", "talk data size:" + data.getData().length);
		CommandEncoder.Protocol cmd = new CommandEncoder.Protocol(
				"MO_V".getBytes(), TALK_DATA, out.size(), out.toByteArray());
		return cmd;
	}

	public static byte[] parseFetchBatteryPowerResp(WifiCar wificar,
			byte[] packet, int type) throws IOException {
		CommandEncoder.Protocol cmd = null;
		if (type == 0) {
			cmd = new Protocol(packet, 0);
		} else {
			cmd = new Protocol("MO_O".getBytes(), FETCH_BATTERY_POWER_RESP,
					packet.length, packet);
		}
		byte[] data = cmd.getContent();
		int value = byteArrayToInt(data, 0, 1);
		//Log.e("wild1", "battery value:" + value);
		//wificar.setCountBattery(value);
		//wificar.setBattery(value);
		// new Protocol("MO_O".getBytes(), FETCH_BATTERY_POWER_REQ, 0,
		// new byte[0]);
		return cmd.output();
	}

	public static boolean parseLoginResp(WifiCar wificar, byte[] packet,
			int type) {
		try {
			//Log.d("wild0", "cmdLoginResp");
			CommandEncoder.Protocol cmd = null;
			if (type == 0) {
				cmd = new Protocol(packet, 0);
			} else {
				cmd = new Protocol("MO_O".getBytes(), 1, packet.length, packet);
			}
			byte[] data = cmd.getContent();
			int result = byteArrayToInt(data, 0, 2);
			if (result == 0) {
				String cameraId = byteArrayToString(data, 2, 13);

				int[] cameraVer = new int[4];
				for (int x = 0; x < 4; x++) {
					cameraVer[x] = byteArrayToInt(data, 23 + x, 1);
				}
				String deviceId = cameraVer[0] + "." + cameraVer[1] + "."
						+ cameraVer[2] + "." + cameraVer[3];
				// String deviceId = byteArrayToHex(data, 23, 4);

				wificar.setDeviceId(deviceId);
				wificar.setCameraId(cameraId);

				// BlowFishEncryptUtil.execute(car.getUnencodeValue(0),
				// car.getKey());

				int val1 = byteArrayToInt(data, 27);
				int val2 = byteArrayToInt(data, 31);
				int val3 = byteArrayToInt(data, 35);
				int val4 = byteArrayToInt(data, 39);

				int rt1 = byteArrayToInt(data, 43, 4);
				int rt2 = byteArrayToInt(data, 47, 4);
				int rt3 = byteArrayToInt(data, 51, 4);
				int rt4 = byteArrayToInt(data, 55, 4);

				wificar.setChallengeReverse(0, rt1);
				wificar.setChallengeReverse(1, rt2);
				wificar.setChallengeReverse(2, rt3);
				wificar.setChallengeReverse(3, rt4);

				//Log.d("wild0", "loginResp(deviceId):" + deviceId);
				//Log.d("wild0", "loginResp(cameraId):" + cameraId);

				BlowFish bf = new BlowFish();
				bf.InitBlowfish(wificar.getKey().getBytes(), wificar.getKey()
						.length());
				Log.e("Command", "wificar.getKey().getBytes():"+wificar.getKey().getBytes());
				int c1 = wificar.getChallenge(0);
				int c2 = wificar.getChallenge(1);
				int c3 = wificar.getChallenge(2);
				int c4 = wificar.getChallenge(3);

				/*
				 * int e1 =
				 * BlowFishEncryptUtil.BytesTo32bits(car.getChallenge(0), 0);
				 * int e2 =
				 * BlowFishEncryptUtil.BytesTo32bits(car.getChallenge(1), 0);
				 * int e3 =
				 * BlowFishEncryptUtil.BytesTo32bits(car.getChallenge(2), 0);
				 * int e4 =
				 * BlowFishEncryptUtil.BytesTo32bits(car.getChallenge(3), 0);
				 */
				int l1[] = new int[] { c1 };
				int r1[] = new int[] { c2 };
				int l2[] = new int[] { c3 };
				int r2[] = new int[] { c4 };
				bf.Blowfish_encipher(l1, r1);
				bf.Blowfish_encipher(l2, r2);

				String bfl1 = int32ToByteHexR(l1[0]);
				String bfr1 = int32ToByteHexR(r1[0]);
				String bfl2 = int32ToByteHexR(l2[0]);
				String bfr2 = int32ToByteHexR(r2[0]);

				String bfl1Return = int32ToByteHex(val1);
				String bfr1Return = int32ToByteHex(val2);
				String bfl2Return = int32ToByteHex(val3);
				String bfr2Return = int32ToByteHex(val4);
				//Log.d("wild0", "loginResp(val1):" + bfl1 + "," + bfr1 + ","+ bfl2 + "," + bfr2);
				//Log.d("wild0", "loginResp(valreturn ):" + bfl1Return + ","+ bfr1Return + "," + bfl2Return + "," + bfr2Return);
				/*
				 * Log.d("wild0", "loginResp(c):" + c1 + "," + c2 + "," + c3 +
				 * "," + c4); Log.d("wild0", "loginResp(e):" + l1[0] + "," +
				 * r1[0]+","+l2[0] + "," + r2[0]); byte[] test1 = new byte[4];
				 * // BlowFishEncryptUtil.Bits32ToBytes(c1, test1, 0);
				 * Log.d("wild0", "loginResp(1):" +
				 * ByteUtility.bytesToHex(test1)); Log.d("wild0",
				 * "loginResp(e1):" + l1[0] + "," + r1[0] + "," + l2[0] + "," +
				 * r2[0]); Log.d("wild0", "loginResp(val1):" + val1 + "," + val2
				 * + "," + val3 + "," + val4); Log.d("wild0", "loginResp(val1):"
				 * + byteArrayToHex(data, 27, 4) + "," + byteArrayToHex(data,
				 * 31, 4) + "," + byteArrayToHex(data, 35, 4) + "," +
				 * byteArrayToHex(data, 39, 4));
				 */
				if (bfl1.equals(bfl1Return) && bfr1.equals(bfr1Return)
						&& bfl2.equals(bfl2Return) && bfr2.equals(bfr2Return)) {
					Log.e("wild0", "===============================");
					wificar.verifyCommand();
					return true;
				}
				Log.e("Comm", "flase--------");
				WificarActivity.getInstance().sendMessage(WificarActivity.getInstance().MESSAGE_CONNECT_TO_CAR_FAIL);
				return false;
				
			}else{//³µ×ÓÒÑ¾­±»Á¬½ÓÊ±·¢ËÍÁ¬½ÓÊ§°ÜµÄÏûÏ¢
				WificarActivity.getInstance().sendMessage(WificarActivity.getInstance().MESSAGE_CONNECT_TO_CAR_FAIL);
				Log.e("Comm","false========");
				//return false;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static byte[] cmdVerifyReq(String key, int v1, int v2, int v3, int v4)
			throws IOException {
		/*
		 * ByteBuffer bb = ByteBuffer.allocate(16);
		 * bb.put(int32ToByteArray(v1)); bb.put(int32ToByteArray(v2));
		 * bb.put(int32ToByteArray(v3)); bb.put(int32ToByteArray(v4));
		 */
		Log.d("wild0", "cmdVerifyReq");
		BlowFish bf = new BlowFish();
		int l1[] = new int[] { v1 };
		int r1[] = new int[] { v2 };
		int l2[] = new int[] { v3 };
		int r2[] = new int[] { v4 };

		bf.InitBlowfish(key.getBytes(), key.length());
		bf.Blowfish_encipher(l1, r1);
		bf.Blowfish_encipher(l2, r2);

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		bOut.write(CommandEncoder.int32ToByteArray(l1[0]));
		bOut.write(CommandEncoder.int32ToByteArray(r1[0]));
		bOut.write(CommandEncoder.int32ToByteArray(l2[0]));
		bOut.write(CommandEncoder.int32ToByteArray(r2[0]));

		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(), 2,
				bOut.size(), bOut.toByteArray());

		Log.d("wild0", "============================verify");
		return cmd.output();
	}

	public static int parseVerifyResp(WifiCar wificar, byte[] packet, int type) {
		// CommandEncoder.Protocol cmd = new Protocol(packet);
		CommandEncoder.Protocol cmd = null;
		Log.d("wild0", "cmdVerifyResp");
		if (type == 0) {
			cmd = new Protocol(packet, 0);
		} else {
			cmd = new Protocol("MO_O".getBytes(), 3, packet.length, packet);
		}
		byte[] data = cmd.getContent();
		int result = byteArrayToInt(data, 0, 2);
		try {
			// Log.d("wild0","enableVideo");
			wificar.enableVideo();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static byte[] cmdKeepAlive() throws IOException {

		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(), 255, 0,
				new byte[0]);
		return cmd.output();
	}

	public static byte[] cmdVideoStartReq() throws IOException {
		//Log.d("wild0", "cmdVideoStartReq");
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(int8ToByteArray(1));
		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(), 4, 1,
				bb.array());
		return cmd.output();
	}

	public static void parseVideoStartResp(WifiCar wificar, byte[] packet,
			int type) throws IOException {
		// CommandEncoder.Protocol cmd = new Protocol(packet);

		byte[] data = packet;
		int result = byteArrayToInt(data, 0, 2);
		int linkId = byteArrayToInt(data, 2, 4);
		//Log.d("wild0", "cmdVideoStartResp:" + linkId);
		wificar.connectMediaReceiver(linkId);
		//wificar.connectMediaSender(linkId);
		//wificar.connectMediaSender(linkId);
		wificar.enableAudio();
		// wificar.enableRecordAudio(1);
	}

	public static byte[] cmdVideoEnd() throws IOException {

		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(), 6, 0,
				new byte[0]);
		return cmd.output();
	}

	public static byte[] cmdVideoFrameInterval(int v1) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(int32ToByteArray(1));

		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(), 7,
				bb.capacity(), bb.array());
		return cmd.output();
	}

	public static byte[] cmdAudioStartReq() throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1);
		bb.put(int8ToByteArray(1));
		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(), 8, 1,
				bb.array());
		return cmd.output();
	}

	public static byte[] cmdAudioEnd() throws IOException {
		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(), 10, 0,
				new byte[0]);
		return cmd.output();
	}

	public static void parseAudioStartResp(WifiCar wificar, byte[] packet,
			int type) {

		// CommandEncoder.Protocol cmd = new Protocol(packet);
		byte[] data = packet;
		int result = byteArrayToInt(data, 0, 2);
		//Log.d("wild1", "audio start resp:" + result + ",length:" + data.length);
		if (result == 0) {
			// int audioLinkId = byteArrayToInt(data, 2, 4);
			WificarActivity.getInstance().sendMessage(WificarActivity.getInstance().MESSAGE_GET_SETTING_INFO);
		}
		/*
		 * result:0¥š·N 2³Ì¤^³s©¡¾ð©Úµ´ 7¤£¤ä´©§†¥E¯à
		 */
	}

	public static byte[] cmdTalkStartReq(int arg) throws IOException {
		// arg:Äá¼v¾÷­µÀW¼½©ñ½w½Ä®É¶¡
		ByteBuffer bb = ByteBuffer.allocate(1);
		bb.put(int8ToByteArray(arg));
		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(),
				TALK_START_REQ, 1, bb.array());
		//Log.d("wild3", "cmdTalkStartReq");
		return cmd.output();
	}

	/*
	 * public static byte[] cmdVideoStartReq(int arg) throws IOException { //
	 * arg:Äá¼v¾÷­µÀW¼½©ñ½w½Ä®É¶¡ ByteBuffer bb = ByteBuffer.allocate(1);
	 * bb.put(int8ToByteArray(arg)); CommandEncoder.Protocol cmd = new
	 * Protocol("MO_O".getBytes(), VIDEO_START_REQ, 1, bb.array()); return
	 * cmd.output(); }
	 */
	public static void parseTalkStartResp(WifiCar car, byte[] packet, int type) {
		//Log.d("wild3", "parseTalkStartResp:" + packet.length);
		// CommandEncoder.Protocol cmd = new Protocol(packet);
		byte[] data = packet;
		int result = byteArrayToInt(data, 0, 2);
		//Log.d("wild3", "parseTalkStartResp:" + result);
		/*
		 * result:0¥š·N 2³Ì¤^³s©¡¾ð©Úµ´ 7¤£¤ä´©§†¥E¯à
		 */
		if (result == 0 && data.length > 2) {
			int linkId = byteArrayToInt(data, 2, 4);
			//Log.d("wild3", "parseTalkStartResp(linkId):" + linkId);
		}
		if (result == 0) {
			car.getAudioComponent().startRecord();

		}
	}

	public static byte[] cmdTalkEnd() throws IOException {

		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(), TALK_END,
				1, new byte[0]);
		return cmd.output();
	}

	public static byte[] cmdDecoderControlReq(int val) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1);
		bb.put(int8ToByteArray(val));
		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(),
				DECODER_CONTROL_REQ, 1, bb.array());
		return cmd.output();
	}

	public static byte[] cmdDeviceControlReq(int key, int val)
			throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.put(int8ToByteArray(key));
		bb.put(int8ToByteArray(val));
		CommandEncoder.Protocol cmd = new Protocol("MO_O".getBytes(),
				DEVICE_CONTROL_REQ, 2, bb.array());
		return cmd.output();
	}
//TODO
	public static ByteArrayBuffer parseCommand(WifiCar car,
			ByteArrayBuffer packet) throws IOException {
		byte[] data = packet.toByteArray();
		// Log.d("wild0", "parseCommand:" + data.length);
		if (data.length > HEAD_LEN) {
			int opCode = ByteUtility.byteArrayToInt(data, 4, 2);
			// Log.d("wild3", "parseCommand op:" + opCode);
			int contentLength = ByteUtility.byteArrayToInt(data, 15, 4);
			if (data.length < (HEAD_LEN + contentLength)) {
				return packet;
			} else {

				int totalLength = HEAD_LEN + contentLength;

				CommandEncoder.Protocol cmd = new CommandEncoder.Protocol(data,
						0);

				packet.clear();
				packet.append(data, totalLength, data.length - totalLength);

				// CommandEncoder.Protocol cmd = new
				// CommandEncoder.Protocol(packet);
				// packet.clear();
				// packet.append(data, totalLength, data.length-totalLength);
				//Log.d("wild1", "op:" + cmd.getOp());
				switch (cmd.getOp()) {
				case LOGIN_RESP:
					parseLoginResp(car, cmd.getContent(), 1);
					break;
				case VERIFY_RESP:
					parseVerifyResp(car, cmd.getContent(), 1);
					break;
				case VIDEO_START_RESP:
					parseVideoStartResp(car, cmd.getContent(), 1);
					break;
				case AUDIO_START_RESP:
					parseAudioStartResp(car, cmd.getContent(), 1);
					break;
				case TALK_START_RESP:
					parseTalkStartResp(car, cmd.getContent(), 1);
					break;
				case FETCH_BATTERY_POWER_RESP:
					parseFetchBatteryPowerResp(car, cmd.getContent(), 1);
					break;
				default:
					break;
				}
			}
		}
		return packet;
		/*
		 * else{ switch (op) { case LOGIN_RESP: parseLoginResp(car, packet,
		 * type); break; case VERIFY_RESP: parseVerifyResp(car, packet, type);
		 * break; default: break; } }
		 */

	}

	public static byte[] cmdDataLoginReq(int id) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(4);
		CommandEncoder.Protocol cmd = new Protocol("MO_V".getBytes(), 0,
				bb.capacity(), bb.array());
		return cmd.output();
	}

	/*
	 * public static void cmdTalkData(byte[] data) {
	 * 
	 * ByteBuffer bb = ByteBuffer.allocate(4); CommandEncoder.Protocol cmd = new
	 * Protocol("MO_V".getBytes(), 0, bb.capacity(), bb.array()); }
	 */
	public static void parseVideoData(WifiCar car, byte[] packet) {
		// ByteBuffer bb = ByteBuffer.allocate(4);
		// CommandEncoder.Protocol cmd = new
		// Protocol("MO_V".getBytes(),0,bb.capacity(), bb.array());
		// return cmd.output();
		CommandEncoder.Protocol cmd = new Protocol(packet, 0);
		byte[] data = cmd.getContent();
		/*
		 * ByteBuffer bb = ByteBuffer.wrap(data);
		 * 
		 * byte[] frameByte = new byte[4]; byte[] timestampByte = new byte[4];
		 * byte[] preserveByte = new byte[1]; byte[] lengthByte = new byte[4];
		 * byte[] dataByte ;
		 * 
		 * bb.get(frameByte, 0, 4); bb.get(timestampByte, 4, 4);
		 * bb.get(preserveByte, 8, 1); bb.get(lengthByte, 9, 4);
		 */
		int timestamp = byteArrayToInt(data, 0, 4);
		int frametime = byteArrayToInt(data, 4, 4);
		int preserve = byteArrayToInt(data, 8, 1);
		int dataLength = byteArrayToInt(data, 9, 4);

		ByteArrayOutputStream bImage = new ByteArrayOutputStream();
		bImage.write(data, 13, dataLength);
		byte[] bArrayImage = bImage.toByteArray();

		VideoData vData = new VideoData(timestamp, frametime, bArrayImage);
		// VideoData vData = new VideoData(System.currentTimeMillis(),
		// frametime, bArrayImage);
		// car.setVideoBitmapBytes(vData);
		try {
			car.appendVideoDataToFlim(vData);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void parseAudioData(WifiCar wificar, byte[] packet) {
		wificar.enableAudioFlag();
		CommandEncoder.Protocol cmd = new Protocol(packet, 0);
		byte[] data = cmd.getContent();
		int timestamp = byteArrayToInt(data, 0, 4);
		int packetSeq = byteArrayToInt(data, 4, 4);
		int graspstamp = byteArrayToInt(data, 8, 4);
		int format = byteArrayToInt(data, 12, 1);

		int dataLength = byteArrayToInt(data, 13, 4);
		ByteArrayOutputStream bAudio = new ByteArrayOutputStream();
		bAudio.write(data, 17, dataLength);
		// byte[] audioData = byteArrayToInt(data, 17, dataLength);

		int adpcmParaSample = byteArrayToInt(data, 17 + dataLength, 2);
		int adpcmParaIndex = byteArrayToInt(data, 19 + dataLength, 1);
		// System.currentTimeMillis()
		AudioData aData = new AudioData(timestamp, packetSeq, graspstamp,
				format, bAudio.toByteArray(), adpcmParaSample, adpcmParaIndex);
		// WifiCar.data = bAudio.toByteArray();
		// ¼g£Ÿ¤â¾÷Án­µ
		/*
		 * TalkData tData = new TalkData(timestamp, packetSeq, graspstamp,
		 * format, bAudio.toByteArray(), adpcmParaSample, adpcmParaIndex);
		 * 
		 * try { wificar.sendTalkData(tData); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		byte[] bDecoded = AudioComponent.decodeADPCMToPCM(bAudio.toByteArray(),
				bAudio.size(), adpcmParaSample, adpcmParaIndex);
		// audio.writeAudioData(bDecoded);
		aData.setPCMData(bDecoded);
		// Log.d("wild2", "parseAudioData[timetick:"+aData.getTimeTick());
		// Log.d("wild2", "parseAudioData[serial:"+aData.getSerial());
		// Log.d("wild2", "parseAudioData[timestamp:"+aData.getTimestamp());
		// Log.d("wild2",
		// "parseAudioData["+data.length+":"+dataLength+"<>"+bDecoded.length+"]("+packetSeq+"):"
		// + adpcmParaSample+","+adpcmParaIndex);
		// wificar.getAudioComponent().writeAudioData(aData);
		try {
			wificar.appendAudioDataToFlim(aData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ByteArrayBuffer parseMediaCommand(WifiCar car,
			ByteArrayBuffer packet, int reminding) throws IOException {

		byte[] data = packet.toByteArray();
		//int offset = CommandEncoder.getPrefixPosition(data, 0);
		int offset = 0;
		int lastOffset = 0;
		if (offset < 0) {
			//¤£¦T¦C«ü¤÷§w¬q,ª½±µ¥þ¶Ç
			return packet;
		}
		//Log.d("media-reminding", "Åªªñ¨Óªº¸ê®Æªø«×:" + data.length+",³Ñ¾l:"+reminding);
		//int ind = CommandEncoder.getPrefixCount(data, 0);
		//Log.d("media", "Åªªñ¨Óªº¸ê®Æ¼Æ¶q:" + ind);
		int count = 0;
		int opCode = 0;
		int contentLength = 0;
		int totalLength = 0;

		while (offset >= 0) {
			count++;
			//Log.d("media", data.length + ":²Ä" + count + "¦¸¹B¦‚:offset" + offset);
			//Log.d("media",
			//		"byte¸ê®Æ1:"
			//				+ ByteUtility.byteArrayToHex(data, offset,
			//						data.length - offset));
			// Log.d("media", "offset:"+offset+","+data[4+offset]);

			if ((data.length - offset) < HEAD_LEN) {
				break;
			}

			opCode = ByteUtility.byteArrayToInt(data, 4 + offset, 2);
			contentLength = ByteUtility.byteArrayToInt(data, 15 + offset, 4);
			totalLength = HEAD_LEN + contentLength;
			//Log.d("media-count", "¸ê®Æ("+opCode+"):"+contentLength);
			if(opCode==1){
				//Log.d("media-v-count", "¼v¹³¸ê®Æ:"+contentLength);
			}
			if(opCode==2){
				//Log.d("media-a-count", "Án­µ¸ê®Æ:"+contentLength);
			}

			if ((data.length - offset) < (HEAD_LEN + contentLength)) {
				if(opCode==1){
					//Log.d("media-v-count", "¼v¹³¸ê®Æ§w¬q¤£¨¬:("+(data.length - offset)+"/"+contentLength+"),§…³¡¸ê®Æ:"+data.length);
				}
				if(opCode==2){
					//Log.d("media-a-count", "Án­µ¸ê®Æ§w¬q¤£¨¬:("+(data.length - offset)+"/"+contentLength+"),§…³¡¸ê®Æ:"+data.length);
				}
				break;
			}
			if(reminding<20000 && packet.length()<1024*64){
			CommandEncoder.Protocol cmd = new CommandEncoder.Protocol(data,
					offset);
			
			//Log.d("media", cmd.getOp()+"*******************************");
			//Log.d("media-size", "¹B¦‚¸ê®Æªø«×:contentLength:" + contentLength);
			//Log.d("media",
			//		"byte¸ê®Æ:"
			//				+ ByteUtility
			//						.byteArrayToHex(data, offset, HEAD_LEN));
			

			
			// Log.d("media","¶W¹L¤^¤d:"+ByteUtility.byteArrayToHex(data,
			// offset,HEAD_LEN));

			// offset = offset + totalLength;

			// int offset = CommandEncoder.checkPrefix(data, offset,10000);
			// Log.d("media","¶W¹L¤^¤d:"+cmd.getOp()+","+offset);

			// CommandEncoder.Protocol cmd = new
			// CommandEncoder.Protocol(packet);
			// packet.clear();
			// packet.append(data, totalLength, data.length-totalLength);
			// Log.d("wild0", "op:" + cmd.getOp()+":"+cmd.output().length);

			if (cmd != null) {
				switch (cmd.getOp()) {
				case AUDIO_DATA:
					//Log.d("media", "audio data");
					parseAudioData(car, cmd.output());
					break;
				case VIDEO_DATA:
					//Log.d("media", "video data");
					//Log.e("media-video", "video data");
					parseVideoData(car, cmd.output());
					break;
				default:
					break;
				}
			}
			}
			//offset = CommandEncoder.getPrefixPosition1(data, offset+totalLength);
			offset = offset+totalLength;
			if (offset >= 0) {
				lastOffset = offset;
			}
			//Log.d("media", "¤I£•­Óoffset:" + offset);

		}
		packet.clear();
		packet.append(data, lastOffset, data.length - lastOffset);
		return packet;

	}

	public static int getPrefixPosition1(byte[] data, int startPosition) {
		return getPrefixPosition1(data, startPosition, data.length);
	}
	public static int getPrefixPosition1(byte[] data, int startPosition,
			int endPosition) {

		for (int i = startPosition; i < endPosition - 4; i++) {
			if (i > endPosition) {
				return -1;
			}
			if (data[i] == (int) 0x4d && data[i + 1] == (int) 0x4f
					&& data[i + 2] == (int) 0x5f && data[i + 3] == (int) 0x56) {
				return i;
			}
		}
		return -1;
	}

	public static int getPrefixCount(byte[] data, int startPosition) {
		// byte[] data = bab.toByteArray();
		int count = 0;
		for (int i = startPosition; i < data.length - 4; i++) {

			// Lod.d("media","i:")
			if (data[i] == (int) 0x4d && data[i + 1] == (int) 0x4f
					&& data[i + 2] == (int) 0x5f && data[i + 3] == (int) 0x56) {
				// Log.d("media", "i:GOT:" + i);
				// Log.d("media", "Åªªñ¨Óªº¸ê®Æ¼Æ¶q§•¸m:" + i);
				count++;
			}
		}
		return count;
	}
}
