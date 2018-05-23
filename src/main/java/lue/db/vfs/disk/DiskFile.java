/*
 * see license.txt
 */
package lue.db.vfs.disk;

import java.io.IOException;
import java.io.RandomAccessFile;

import lue.db.vfs.File;
import lue.db.vfs.Path;

/**
 * Reads/Writes to Disk
 * 
 * @author Tony
 *
 */
public class DiskFile implements File {

    private RandomAccessFile raf;
    private Path path;
    
    /**
     * @param path
     * @param raf
     */
    public DiskFile(Path path, RandomAccessFile raf) {
        this.path = path;
        this.raf = raf;        
    }

    @Override
    public Path path() {
        return this.path;
    }
    
    @Override
    public long length() {    
        try {
            return this.raf.length();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void length(long newLength) {    
        try {
            this.raf.setLength(newLength);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void seek(long position) {
        try {
            this.raf.seek(position);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public long position() {    
        try {
            return this.raf.getFilePointer();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public byte readByte() {
        try {
            return this.raf.readByte();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public short readShort() {
        try {
            return this.raf.readShort();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public int readInt() {
        try {
            return this.raf.readInt();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public long readLong() {
        try {
            return this.raf.readLong();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public float readFloat() {
        try {
            return this.raf.readFloat();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public double readDouble() {
        try {
            return this.raf.readDouble();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int readBytes(byte[] buffer, int offset, int length) {
        try {
            return this.raf.read(buffer, offset, length);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }        
    }

    
    @Override
    public void writeByte(byte value) {
        try {
            this.raf.writeByte(value);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void writeShort(short value) {
        try {
            this.raf.writeShort(value);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void writeInt(int value) {
        try {
            this.raf.writeInt(value);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void writeLong(long value) {
        try {
            this.raf.writeLong(value);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void writeFloat(float value) {
        try {
            this.raf.writeFloat(value);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void writeDouble(double value) {
        try {
            this.raf.writeDouble(value);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void writeBytes(byte[] buffer, int offset, int length) {
        try {
            this.raf.write(buffer, offset, length);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flush() {
        try {
            this.raf.getFD().sync();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
        
    }

    @Override
    public void close() {
        try {
            this.raf.close();
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

}
