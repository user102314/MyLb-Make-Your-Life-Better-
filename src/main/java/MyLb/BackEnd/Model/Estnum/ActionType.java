
package MyLb.BackEnd.Model.Estnum;

public enum ActionType {
    SECURITY_ALERT, // Ex: Tentative de connexion échouée
    PASSWORD_CHANGE, // Ex: Le mot de passe a été modifié
    LOGIN_SUCCESS,   // Ex: Connexion réussie
    PROFILE_UPDATE,  // Ex: Nom ou email modifié
}