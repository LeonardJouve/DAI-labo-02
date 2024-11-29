# Pass-Secure

<p align="center">
<img align="center" src="https://github.com/LeonardJouve/DAI-labo01/blob/Add-logo-readme/logo-pass-secure.png" alt="drawing" style="width:200px;" class="center"/>
</p>

**Pass-Secure** est un gestionnaire de mots de passe TCP offrant des fonctionnalités de gestion de mots de passe dans un environnement client-serveur. Il permet d'interagir avec un serveur via des commandes pour enregistrer, récupérer, stocker ou supprimer des mots de passe.

## Fonctionnalités

- **Enregistrement et Connexion des Utilisateurs** : Les utilisateurs peuvent créer un compte protégé par un mot de passe maître.
- **Stockage Sécurisé** : Les mots de passe sont chiffrés et sauvegardés dans un coffre protégé.
- **Récupération de Mots de Passe** : Permet aux utilisateurs d'accéder à leurs mots de passe stockés en utilisant le mot de passe maître.
- **Suppression de Mots de Passe** : Les entrées inutiles peuvent être supprimées du coffre.
- **Déconnexion et Fermeture** : Permet de mettre fin à la session de manière sécurisée.

## Installation

1. Clonez le dépôt :
   ```bash
   git clone https://github.com/your-repo/pass-secure.git
   cd pass-secure
   ```

2. Compilez le projet :
   ```bash
   ./mvnw spotless:apply spotless:check dependency:go-offline clean compile package
   ```

3. Lancez le serveur :
   ```bash
   java -cp target/pass-secure.jar ch.heigvd.dai.server.Server
   ```

4. Lancez le client :
   ```bash
   java -cp target/pass-secure.jar ch.heigvd.dai.client.Client
   ```

## Usage

### Commandes disponibles

L'application fonctionne avec un protocole TCP personnalisé. Voici les commandes supportées côté client :

| **Commande**     | **Description**                                                             |
|-------------------|-----------------------------------------------------------------------------|
| `--register`      | Enregistrer un nouvel utilisateur (requiert `--username` et `--password`). |
| `--login`         | Connecter un utilisateur existant (requiert `--username` et `--password`). |
| `--add`           | Ajouter un mot de passe au coffre (requiert `--name` et `--password`).     |
| `--get`           | Récupérer un mot de passe du coffre (requiert `--name`).                  |
| `--remove`        | Supprimer un mot de passe du coffre (requiert `--name`).                  |
| `--disconnect`    | Déconnecter l'utilisateur du serveur.                                      |
| `--ping`          | Vérifier la connectivité avec le serveur.                                  |
| `--quit`          | Fermer la connexion (client uniquement).                                   |

### Exemple de session client-serveur

#### Étapes :
1. Enregistrer un utilisateur
2. Connecter l'utilisateur
3. Ajouter un mot de passe
4. Récupérer un mot de passe
5. Déconnecter l'utilisateur

#### Exemple :
**Client** → `--register --username alice --password 1234`  
**Serveur** → `OK`

**Client** → `--add --name github --password securePass123 --encryptionPassword 1234`  
**Serveur** → `OK`

**Client** → `--get --name github`  
**Serveur** → `OK`  
**Serveur** → `rFMQGZ5LWQUCpCmNjmgrHYNPZrGktjm5dxZbmNg2hfs`

**Client** → `--get --name github --decryptionPassword 1234`  
**Serveur** → `OK`  
**Serveur** → `securePass123`

**Client** → `--disconnect`  
**Serveur** → `OK`

### Exemple de génération de mot de passe :
**Client** → `--generate --name twitter --length 12 --special true --store true`  
**Serveur** → `OK`  
**Serveur** → `RandomPassword123!`

## Remarques importantes

- **Sécurité** : Les mots de passe sont chiffrés localement avec un mot de passe maître. Veillez à ce que ce dernier soit robuste.
- **Configuration** : Le serveur utilise par défaut le port `6433`. Assurez-vous qu'il est ouvert sur votre machine.
- **Dossier par défaut** : Les coffres-forts sont sauvegardés dans le répertoire spécifié ou, par défaut, dans le répertoire courant.

## Contributions

Pour signaler un bug ou proposer des améliorations, veuillez soumettre une issue sur le dépôt GitHub.
