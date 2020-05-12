package Instruments;

import java.io.Serializable;

public enum ManagerResponseCodes implements Serializable {
    OK,
    SQL_ERROR,
    UNKNOWN_ERROR,
    NO_CHANGES;
}
