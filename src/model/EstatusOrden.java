package model;

public enum EstatusOrden {
    EN_ESPERA,
    EN_PROCESO,
    FINALIZADO;

    public static EstatusOrden fromString(String dbValue) {
        if (dbValue == null) return null;
        switch (dbValue.toUpperCase()) {
            case "EN_ESPERA":
                return EN_ESPERA;
            case "EN_PROCESO":
                return EN_PROCESO;
            case "FINALIZADO":
                return FINALIZADO;
            default:
                return null;
        }
    }

    public String toDbValue() {
        // que se guarde exactamente igual que en la BD
        return this.name();
    }
}
