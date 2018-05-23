/*
 * see license.txt
 */
package lue.db.vfs.mem;

import java.nio.ByteBuffer;

import lue.db.vfs.File;
import lue.db.vfs.Path;

/**
 * @author Tony
 *
 */
public class MemFile implements File {

    private Path path;

    private ByteBuffer buffer;
    
    /**
     * 
     */
    public MemFile(Path path) {
        this.path = path;
        this.buffer = ByteBuffer.allocate(1024);
    }

    @Override
    public Path path() {
        return this.path;
    }

    @Override
    public long length() {
        return this.buffer.capacity();
    }

    @Override
    public void length(long newLength) {
        if(newLength < this.buffer.capacity()) {
            this.buffer.limit((int)newLength);
        }
        else {
            ByteBuffer newBuffer = ByteBuffer.allocate( (int)newLength);
            int pos = this.buffer.position();
            
            this.buffer.position(0);
            newBuffer.put(this.buffer);
            newBuffer.position(pos);
            
            this.buffer = newBuffer;
        }
    }

    @Override
    public void seek(long position) {
        this.buffer.position((int)position);
    }

    @Override
    public long position() {
        return this.buffer.position();
    }

    @Override
    public byte readByte() {
        return this.buffer.get();
    }

    @Override
    public short readShort() {
        return this.buffer.getShort();
    }

    @Override
    public int readInt() {
        return this.buffer.getInt();
    }

    @Override
    public long readLong() {
        return this.buffer.getLong();
    }

    @Override
    public float readFloat() {
        return this.buffer.getFloat();
    }

    @Override
    public double readDouble() {
        return this.buffer.getDouble();
    }

    @Override
    public void writeByte(byte value) {
        this.buffer.put(value);
    }

    
    @Override
    public void writeShort(short value) {
        this.buffer.putShort(value);
    }


    @Override
    public void writeInt(int value) {
        this.buffer.putInt(value);
    }

    @Override
    public void writeLong(long value) {
        this.buffer.putLong(value);
    }

    
    @Override
    public void writeFloat(float value) {
        this.buffer.putFloat(value);
    }

    
    @Override
    public void writeDouble(double value) {
        this.buffer.putDouble(value);
    }

    @Override
    public int readBytes(byte[] buffer, int offset, int length) {
        this.buffer.get(buffer, offset, length);
        return length;
    }

    @Override
    public void writeBytes(byte[] buffer, int offset, int length) {
        this.buffer.put(buffer, offset, length);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
        this.buffer.clear();
    }

}
