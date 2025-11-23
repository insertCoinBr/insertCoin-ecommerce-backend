package org.insertcoin.productservice.dtos;

public class ProductKeyImportResponseDTO {
    private int totalImported;
    private int totalInvalid;
    private int totalDuplicates;

    public ProductKeyImportResponseDTO(int totalImported, int totalInvalid, int totalDuplicates) {
        this.totalImported = totalImported;
        this.totalInvalid = totalInvalid;
        this.totalDuplicates = totalDuplicates;
    }

    public int getTotalImported() {
        return totalImported;
    }

    public void setTotalImported(int totalImported) {
        this.totalImported = totalImported;
    }

    public int getTotalInvalid() {
        return totalInvalid;
    }

    public void setTotalInvalid(int totalInvalid) {
        this.totalInvalid = totalInvalid;
    }

    public int getTotalDuplicates() {
        return totalDuplicates;
    }

    public void setTotalDuplicates(int totalDuplicates) {
        this.totalDuplicates = totalDuplicates;
    }
}
