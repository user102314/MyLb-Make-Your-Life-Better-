package MyLb.BackEnd.Model.Estnum;

public enum ValidationStatus {
    PENDING("En attente"),
    VALIDATED("Validé"),
    REJECTED("Rejeté"),
    IN_REVIEW("En révision");

    private final String displayName;

    ValidationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Méthode utilitaire pour convertir depuis un string
    public static ValidationStatus fromString(String status) {
        if (status == null) {
            return PENDING;
        }

        switch (status.toUpperCase()) {
            case "PENDING":
            case "EN_ATTENTE":
                return PENDING;
            case "VALIDATED":
            case "VALIDE":
                return VALIDATED;
            case "REJECTED":
            case "REJETE":
                return REJECTED;
            case "IN_REVIEW":
            case "EN_REVISION":
                return IN_REVIEW;
            default:
                return PENDING;
        }
    }
}