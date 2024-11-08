
# Pass-Secure

<p align="center">
<img align="center" src="https://github.com/LeonardJouve/DAI-labo01/blob/Add-logo-readme/logo-pass-secure.png" alt="drawing" style="width:200px;" class="center"/>
</p>

**Pass-Secure** is a non-secure password manager designed to provide basic functionality for managing passwords. It can encrypt passwords stored in a vault using another password ;)

## Features

- **Password Generation**: Generate strong, random passwords of configurable length.
- **Password Storage**: Store passwords securely in a vault that is protected by a master password.
- **Password Retrieval**: Retrieve passwords from the vault using the correct decryption password.
- **Vault Encryption**: Store passwords encrypted with a master password to secure your credentials.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/pass-secure.git
   cd pass-secure
   ```

2. Compile the project:
   ```bash
   ./mvnw spotless:apply spotless:check dependency:go-offline clean compile package 
   ```

3. Run the application:
   ```bash
   java -jar pass-secure-0.1.jar
   ```

## Usage

### Options

You can use the following options to interact with Pass-Secure:

```bash
  -p, --path=<path>       Path of the vault used to store and retrieve passwords. Defaults to current directory.
  -h, --help              Show help message.
  -V, --version           Print version.
```

### Generate a Password

Password generation can be achieved through the `generate` subcommand.

```bash
generate [-ahsV] [-l=<length>] [-p=<password>] <name>
Generate a new password:
  <name>                    Name of the password entry.
  -a, --add                 Store generated password in the vault (true or false).
  -l, --length=<length>     Specify the length of the password to be generated (default is 16).
  -p, --password=<password> Password used to encrypt the vault.
  -s, --special             Include special characters in the generated password (true or false).
  -o, --overwrite           Overwrite an already existing vault entry if it exists.
```

**Example**:
```bash
java -jar pass-secure-0.1.jar generate -l 12 -s -a -p "vaultPassword123" "myEmail"
```
This command generates a 12-character password with special characters and stores it in the vault under the name `myEmail`. The vault is encrypted using the password `vaultPassword123`.

### Store a Password

Password storage can be achieved through the `add` subcommand.

```bash
add [-hV] [-p=<password>] <name> <addPassword>
Add a new password to the vault:
  <name>                    Name of the password entry.
  <addPassword>             The new password to be added.
  -p, --password=<password> Password used to encrypt the vault.
  -o, --overwrite           Overwrite an existing vault entry if it already exists.
```

**Example**:
```bash
java --jar pass-secure-0.1.jar add -p "vaultPassword123" "myWebsite" "customPassword456"
```
This command adds the custom password `customPassword456` under the name `myWebsite` in the vault, which is encrypted using the password `vaultPassword123`.

### Retrieve a Password

Password retrieval can be achieved through the `get` subcommand.

```bash
get [-hV] [-p=<password>] <name>
Retrieve a password for a specific name:
  <name>                    Name of the password entry.
  -p, --password=<password> Password used to decrypt the vault.
```

**Example**:
```bash
java -jar pass-secure-0.1.jar get -p "vaultPassword123" "myEmail"
```
This command retrieves the password for `myEmail` from the vault. The vault is decrypted using the password `vaultPassword123`.

### Additional Examples of Usage

#### 1. Generate and Store a Password
```bash
java -jar pass-secure-0.1.jar generate -l 20 -s -a -p "strongVaultPass" "myTwitter"
```
This command generates a 20-character password with special characters and automatically stores it in the vault under the name `myTwitter`. The vault is encrypted using the password `strongVaultPass`.

#### 2. Add a Custom Password
```bash
java -jar pass-secure-0.1.jar add -p "vaultPass123" "myLinkedIn" "LinkedInPassword321"
```
This command adds the custom password `LinkedInPassword321` under the name `myLinkedIn` in the vault.

#### 3. Retrieve a Password
```bash
java -jar pass-secure-0.1.jar get -p "vaultPass123" "myTwitter"
```
This command retrieves the password stored under `myTwitter` from the vault.

## Important Notes

- **Encryption**: Passwords stored in the vault are encrypted using a master password. Ensure that this master password is strong and secure.
- **Security Warning**: As the tool is not designed for high-security purposes, avoid using it for storing highly sensitive information.
- **Vault Location**: By default, the vault is stored in the current working directory. You can specify a different location using the `--path` option.

