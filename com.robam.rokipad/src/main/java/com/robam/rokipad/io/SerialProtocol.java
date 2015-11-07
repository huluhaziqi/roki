package com.robam.rokipad.io;

import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.legent.LogTags;
import com.legent.io.msgs.IMsg;
import com.legent.plat.io.device.AbsPlatProtocol;
import com.legent.plat.io.device.msg.Msg;
import com.legent.plat.io.device.msg.MsgUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class SerialProtocol extends AbsPlatProtocol {

    final static String TAG = LogTags.TAG_IO;

    /**
     * 发送指令结构 41+N: 同步头2B | Head Option 1B |包长度 变长1~2B | 源设备编码17B |目标设备编码17B |
     * CMD ID 1B | Payload N字节 | CRC 2B
     * <p/>
     * 接收指令结构 : 同步头2B | Head Option 1B |包长度 变长1~2B | 源设备编码17B | CMD ID 1B |
     * Payload N字节 | CRC 2B
     */

    final byte[] HEAD = new byte[]{(byte) 0xFE, (byte) 0x5C};
    final int HEAD_SIZE = HEAD.length;
    final int OPTION_SIZE = 1;
    final int CRC_SIZE = 2;
    final int LEN_SIZE = 1;
    final int CMD_CODE_SIZE = 1;

    byte[] buffer;
    int bufCount;
    int minFrameLength;

    public SerialProtocol() {
        buffer = new byte[BufferSize];

        // 接收指令的最小长度，只有源设备编码, min =25
        minFrameLength = HEAD_SIZE + OPTION_SIZE + LEN_SIZE + GUID_SIZE
                + CMD_CODE_SIZE + CRC_SIZE;
    }

    @Override
    public byte[] encode(IMsg reqMsg) throws Exception {
        Preconditions.checkNotNull(reqMsg);
        Preconditions.checkState(reqMsg instanceof Msg);

        Msg msg = (Msg) reqMsg;
        ByteBuffer buf = ByteBuffer.allocate(BufferSize).order(BYTE_ORDER);

        String srcGuid = msg.getSource().getGuid();
        String dstGuid = msg.getTarget().getGuid();

        Preconditions.checkNotNull(srcGuid);
        Preconditions.checkNotNull(dstGuid);

        Preconditions.checkState(srcGuid.length() == GUID_SIZE,
                "srcGuid length error");
        Preconditions.checkState(dstGuid.length() == GUID_SIZE,
                "dstGuid length error");

        // srcGuid
        buf.put(srcGuid.getBytes());
        // dstGuid
        buf.put(dstGuid.getBytes());
        // cmdCode
        buf.put(MsgUtils.toByte(reqMsg.getID()));

        // data params
        onEncodeMsg(buf, msg);

        // CRC
        byte[] data = new byte[buf.position()];
        System.arraycopy(buf.array(), 0, data, 0, data.length);
        short crc = MsgUtils.calcCrc2(data);

        // 完整一帧
        int frmLen = HEAD_SIZE + OPTION_SIZE + LEN_SIZE + GUID_SIZE * 2
                + CMD_CODE_SIZE + data.length + CRC_SIZE;
        buf = ByteBuffer.allocate(frmLen).order(BYTE_ORDER);
        buf.put(HEAD);
        buf.put(HeadOption.getByte());
        buf.put(MsgUtils.toByte(data.length + CRC_SIZE));
        buf.put(data);
        buf.putShort(crc);

        //
        data = new byte[buf.position()];
        System.arraycopy(buf.array(), 0, data, 0, data.length);
        buf.clear();
        buf = null;

        return data;
    }

    @Override
    public List<IMsg> decode(byte[] data, Object... params) throws Exception {
        List<IMsg> msgs = Lists.newArrayList();
        int dataLength = data.length;

        // 如果缓冲区满，重置游标
        if (bufCount + dataLength > buffer.length) {
            bufCount = 0;
        }

        System.arraycopy(data, 0, buffer, bufCount, dataLength);
        bufCount += dataLength;

        // 如果字节数小于最小帧长度，等待下一包数据
        if (bufCount < minFrameLength) {
            return msgs;
        }

        ByteBuffer bufAll = ByteBuffer.wrap(buffer, 0, bufCount).order(
                BYTE_ORDER);
        ByteBuffer msgData;
        short rawCrc1, rawCrc2, crc;
        byte[] tmp;
        int cursor = 0;
        boolean isMatchHead, isEnoughLength;

        // 从缓冲区的首字节开始比较，查找帧头
        while (cursor < bufCount) {
            bufAll.position(cursor);

            // 如果剩余字节数小于最小帧长度，等待下一包数据
            if (bufCount - cursor < minFrameLength) {
                break;
            }

            // 当前游标所在字节与下一字节是否匹配帧头
            isMatchHead = (bufAll.get(cursor) == HEAD[0] && bufAll
                    .get(cursor + 1) == HEAD[1]);

            if (!isMatchHead) {
                // 不是帧头，等待下一包数据
                cursor++;
            } else {
                // 找到帧头
                int offset = cursor;

                // 跳过帧头
                offset += HEAD_SIZE;

                // 跳过HeadOption
                offset += OPTION_SIZE;

                // 取包长
                short packLength = (short) (MsgUtils.getShort(bufAll
                        .get(offset)) - CRC_SIZE);
                // 跳过包长长度
                offset += LEN_SIZE;

                // 完整帧长
                int frameLength = HEAD_SIZE + OPTION_SIZE + LEN_SIZE + CRC_SIZE
                        + packLength;

                // 如果cursor后续数据长度>=帧长, cursor 指向帧头
                isEnoughLength = bufCount - cursor >= frameLength;

                if (!isEnoughLength) {
                    // 不满足一帧
                    break;
                } else {
                    // 满足一帧
                    // 取数据区数据
                    tmp = new byte[packLength];
                    System.arraycopy(bufAll.array(), offset, tmp, 0, packLength);
                    msgData = ByteBuffer.wrap(tmp).order(BYTE_ORDER);
                    offset += packLength;

                    // 取crc;
                    rawCrc1 = bufAll.order(ByteOrder.BIG_ENDIAN).getShort(
                            offset);
                    rawCrc2 = bufAll.order(ByteOrder.LITTLE_ENDIAN).getShort(
                            offset);
                    offset += CRC_SIZE;

                    // 计算crc
                    crc = MsgUtils.calcCrc(msgData.array());
                    if (rawCrc1 == crc || rawCrc2 == crc) {
                        IMsg msg = onFindFrame(msgData);
                        if (msg != null) {
                            msgs.add(msg);
                        }
                    } else {
                        Log.e(TAG, "CRC 错误");
                    }

                    // 移到此帧的帧尾
                    cursor = offset;
                }
            }

        }

        int oddCount = bufCount - cursor;
        if (oddCount < 0) {
            Log.e(TAG, "serialport decode error");
            Log.e(TAG, String.format("bufCount:%s cursor:%s", bufCount, cursor));
        }

        bufCount = oddCount;
        System.arraycopy(buffer, cursor, buffer, 0, bufCount);

        return msgs;
    }

    private IMsg onFindFrame(ByteBuffer packData) {

        try {
            IMsg msg = byte2Msg(packData);
            return msg;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private IMsg byte2Msg(ByteBuffer buf)throws Exception {
        byte[] data = buf.array();
        int offset = 0;

        String srcGuid = MsgUtils.getString(data, offset, GUID_SIZE);
        offset += GUID_SIZE;

        short msgKey = MsgUtils.getShort(data[offset]);
        offset += CMD_CODE_SIZE;

        Msg msg = Msg.newIncomingMsg(msgKey, srcGuid);

        // paser payload
        byte[] payload = new byte[data.length - offset];
        System.arraycopy(data, offset, payload, 0, payload.length);
        onDecodeMsg(msg, payload);

        return msg;
    }

    static class HeadOption {

        static int HEAD_OPTION_ENCRYT = 1 << 0;
        static int HEAD_OPTION_CRC = 1 << 1;
        static int HEAD_OPTION_BROADCAST_DATALINK = 1 << 2;

        static public byte getByte() {
            int res = HEAD_OPTION_CRC;
            return MsgUtils.toByte(res);
        }

    }
}
