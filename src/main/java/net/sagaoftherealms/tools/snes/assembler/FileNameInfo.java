package net.sagaoftherealms.tools.snes.assembler;

public class FileNameInfo {
    public int id;
    public String fileName;
    public FileNameInfo next;

    public FileNameInfo() {

    }

    public void setNext(FileNameInfo next) {
        this.next = next;
    }

    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public FileNameInfo getNext() {
        return next;
    }
}
