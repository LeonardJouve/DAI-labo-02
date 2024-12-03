/**
 * This class provides a method to display usage instructions and a description of available
 * commands for the password manager application.
 */
package ch.heigvd.dai.client.commands;

public class Help {

  /**
   * Displays a help message that includes:
   *
   * <ul>
   *   <li>The general usage syntax for commands.
   *   <li>A detailed description of each supported command, including required and optional
   *       arguments.
   * </ul>
   *
   * This message is printed directly to the console.
   */
  public static void help() {
    System.out.println(
        """
                    Usage : <COMMAND> --<argument> <value>

                    +---------------+---------------------------------------------------------------------------------------------------------------------------------------------------------+
                    | Commande      | Description                                                                                                                                             |
                    +---------------+---------------------------------------------------------------------------------------------------------------------------------------------------------+
                    | REGISTER      | Enregistrer un nouvel utilisateur (requis --username et --password).                                                                                    |
                    | LOGIN         | Connecter un utilisateur existant (requis --username et --password).                                                                                    |
                    | ADD           | Ajouter un mot de passe au coffre (requis --name et --password) (optionnel --encryptionPassword).                                                       |
                    | GET           | Récupérer un mot de passe du coffre (requis --name) (optionnel --decryptionPassword).                                                                   |
                    | REMOVE        | Supprimer un mot de passe du coffre (requis --name).                                                                                                    |
                    | DISCONNECT    | Déconnecter l'utilisateur du serveur.                                                                                                                   |
                    | PING          | Vérifier la connectivité avec le serveur.                                                                                                               |
                    | QUIT          | Fermer la connexion.                                                                                                                                    |
                    | GENERATE      | Créer un mot de passe sécurisé (requis --length et --name si --store est spécifié) (optionnel --special, --store, --overwrite si --store est spécifié). |
                    | HELP          | Affiche ce message d'aide.                                                                                                                              |
                    +---------------+---------------------------------------------------------------------------------------------------------------------------------------------------------+
                    """);
  }
}
