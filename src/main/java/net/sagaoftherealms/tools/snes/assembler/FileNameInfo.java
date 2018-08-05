package net.sagaoftherealms.tools.snes.assembler;

public class FileNameInfo {
    private final int id;
    private final String fileName;
    private FileNameInfo next;

    public FileNameInfo(int id, String fileName) {
        this.id = id;
        this.fileName = fileName;
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
