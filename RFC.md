# RFC : Gestionnaire de mots de passe TCP

## 1. Overview

Le gestionnaire de mots de passe Pass-secure permet à un client de gérer ses mots de passe en interagissant avec un serveur via un protocole personnalisé. Les fonctionnalités incluent l'enregistrement, l'authentification, l'ajout, la récupération, la suppression et la déconnexion. Toutes les communications entre le client et le serveur utilisent des commandes structurées dans un environnement sécurisé.

### Objectifs :
- Permettre la gestion distribuée de mots de passe.
- Assurer la sécurité des données échangées.
- Supporter des interactions simples et fiables via TCP.

## 2. Transport Protocol

### Protocoles utilisés :
- **Transport** : TCP.
- **Encodage** : UTF-8 pour toutes les données échangées.
- **Port par défaut** : 6433.

### Flux de connexion :
1. Le serveur écoute sur un port spécifié.
2. Le client établit une connexion TCP au serveur.
3. Le client envoie des commandes structurées au serveur.
4. Le serveur traite les commandes et répond par des messages de confirmation ou d'erreur.

## 3. Messages

### Format général des messages :
Les messages sont échangés sous la forme de lignes de texte terminées par un saut de ligne (`\n`).

- **Commandes du client** :
  ```
  <COMMAND_TYPE> [ARGS]
  ```

- **Réponses du serveur** :
  ```
  OK | NOK [ARGS]
  ```

### Types de commandes supportées :
| Commande        | Arguments requis        | Description                                             |
|-----------------|-------------------------|---------------------------------------------------------|
| `REGISTER`      | `--username`, `--password` | Enregistrer un nouvel utilisateur.                     |
| `LOGIN`         | `--username`, `--password` | Connecter un utilisateur existant.                     |
| `ADD`           | `--name`, `--password`     | Ajouter un mot de passe au coffre.                     |
| `GET`           | `--name`                 | Récupérer un mot de passe à partir du coffre.          |
| `REMOVE`        | `--name`                 | Supprimer un mot de passe du coffre.                   |
| `DISCONNECT`    | Aucun                  | Déconnecter l'utilisateur.                             |
| `PING`          | Aucun                  | Vérifier la connectivité avec le serveur.              |
| `QUIT`          | Aucun                  | Fermer la connexion (côté client uniquement).          |

### Réponses du serveur :
| Réponse         | Arguments optionnels    | Description                                             |
|-----------------|-------------------------|---------------------------------------------------------|
| `OK`           | Aucun                   | Commande exécutée avec succès.                         |
| `NOK`          | `message`               | Une erreur s'est produite. Le champ `message` contient des détails. |

### Messages d'erreur potentiels :
| Type d'erreur           | Description                                   |
|-------------------------|-----------------------------------------------|
| `invalid_credentials`   | Les informations d'authentification sont invalides. |
| `user_already_exists`   | L'utilisateur existe déjà.                   |
| `user_already_connected`| L'utilisateur est déjà connecté.             |
| `entry_not_found`       | L'entrée demandée est introuvable.           |
| `entry_already_exists`  | L'entrée existe déjà.                        |
| `unauthorized`          | Accès non autorisé.                          |
| `server_error`          | Une erreur interne s'est produite côté serveur. |


### Format général des messages :
Les messages sont échangés sous la forme de lignes de texte terminées par un saut de ligne (`\n`).

- **Commandes du client** :
  ```
  <COMMAND_TYPE> [ARGS]
  ```

- **Réponses du serveur** :
  ```
  OK | NOK [type]
  ```

## 4. Examples

### Exemple de session client-serveur :
#### Étapes :
1. Enregistrement d'un utilisateur.
2. Connexion de l'utilisateur.
3. Ajout d'un mot de passe.
4. Récupération d'un mot de passe.
5. Déconnexion.

#### Interactions :
**Client** → `REGISTER --username alice --password 1234`  
**Serveur** → `OK`  

**Client** → `LOGIN --username alice --password 1234`  
**Serveur** → `NOK user_already_connected`  

**Client** → `ADD --name github --password securePass123`  
**Serveur** → `OK`  

**Client** → `GET --name github`  
**Serveur** → `OK`  
**Serveur** → `rFMQGZ5LWQUCpCmNjmgrHYNPZrGktjm5dxZbmNg2hfs=`  

**Client** → `DISCONNECT`  
**Serveur** → `OK`  

### Exemple d'erreur :
**Client** → `GET --name twitter`  
**Serveur** → `NOK entry_not_found`  

## Annexes
Le code utilise les classes suivantes pour gérer l'état et les interactions :
- **`State`** : Gestion des utilisateurs et des coffres-forts (fichiers système).
- **`Repl`** : Traitement des commandes client.
- **`Server`** : Gestion des connexions TCP côté serveur.
- **`Client`** : Interaction utilisateur via un terminal.

Pour tout complément ou modification, veuillez ouvrir une issue sur le dépôt associé.
