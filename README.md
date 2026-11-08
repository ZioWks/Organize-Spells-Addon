<div align="center">

# ✨ OrganiseSpells

### Organisez les sorts MagicSpells liés à vos objets, directement en jeu.

Un menu coffre intuitif pour réordonner les sorts d'un objet tenu en main, avec sauvegarde de l'ordre et configuration personnalisable.

<br>

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.7-45A049?style=for-the-badge&logo=minecraft&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MagicSpells](https://img.shields.io/badge/MagicSpells-4.0--Beta--17-6E56CF?style=for-the-badge)
![Auteur](https://img.shields.io/badge/Auteur-Karmaaa-0A66C2?style=for-the-badge)

</div>

---

## 📖 Sommaire

- [Présentation](#-présentation)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Commandes](#-commandes)
- [Fonctionnement du menu](#-fonctionnement-du-menu)
- [Permissions](#-permissions)
- [Configuration](#️-configuration)
- [Compilation](#-compilation)

## 🌟 Présentation

**OrganiseSpells** est un plugin Minecraft qui permet aux joueurs de gérer l'ordre des sorts MagicSpells associés à l'objet qu'ils tiennent dans leur main principale.

Avec `/org`, un coffre affiche les sorts liés à cet objet. Cliquez sur deux sorts pour échanger leurs places. Les changements sont sauvegardés et restaurés à la reconnexion, y compris après un redémarrage du serveur.

## 📋 Prérequis

- Un serveur Minecraft **1.21.7** compatible avec MagicSpells.
- **Java 21**.
- **MagicSpells 4.0-Beta-17**, installé comme plugin serveur.
- La permission d'utiliser la commande concernée.

> MagicSpells est une dépendance obligatoire : le serveur doit démarrer avec MagicSpells installé.

## 📦 Installation

1. Téléchargez `organisespells-1.0.jar` depuis les releases du dépôt ou compilez le projet.
2. Placez le fichier dans le dossier `plugins/` du serveur.
3. Vérifiez que MagicSpells 4.0-Beta-17 est également présent dans `plugins/`.
4. Démarrez ou redémarrez le serveur.
5. Personnalisez `plugins/OrganiseSpells/config.yml` puis exécutez `/org reload` pour appliquer vos modifications.

Au premier démarrage, le plugin crée automatiquement son fichier `config.yml`.

## 🎮 Commandes

| Commande | Description |
|---|---|
| `/org` | Ouvre le menu des sorts liés à l'objet tenu en main principale. |
| `/org reload` | Recharge la configuration du plugin |

Tenez l'objet concerné avant d'utiliser `/org`. Si aucun sort n'y est lié, le plugin vous le signalera.

## 🧙 Fonctionnement du menu

- Le coffre affiche les sorts associés à l'objet tenu.
- Le nombre de lignes s'adapte au nombre de sorts, jusqu'à la taille maximale d'un coffre.
- Avec plus de 54 sorts, le menu utilise des pages.
- Cliquez sur un premier sort, puis sur un second pour échanger leurs positions.
- Les changements sont sauvegardés et restaurés lors des connexions suivantes.

## 🔐 Permissions

| Permission | Description | Par défaut |
|---|---|---|
| `organisespells.command.org` | Ouvrir le menu `/org`. |
| `organisespells.command.reload` | Utiliser `/org reload`. |

## ⚙️ Configuration

Le fichier `plugins/OrganiseSpells/config.yml` permet de personnaliser les messages, le titre du coffre et les objets affichés dans le menu.

### Couleurs et variables

- Les codes couleur Minecraft classiques s'écrivent avec `&`, par exemple `&a` ou `&c`.
- Le nom d'un sort accepte `{spell}` et `{position}`.
- Le lore d'un sort accepte `{spell}`, `{position}` et `{status}`.
- Le bouton de page accepte `{page}` et `{pages}`.
- Les messages de sélection et d'échange acceptent respectivement `{spell}`, puis `{first}` et `{second}`.

### Options des objets

Les matériaux sont des noms de matériaux Bukkit, par exemple `ENCHANTED_BOOK`, `ARROW` ou `BOOK`.

| Clé | Utilisation |
|---|---|
| `gui.items.spell.material` | Matériau utilisé pour les sorts. |
| `gui.items.spell.name` | Nom affiché pour chaque sort. |
| `gui.items.spell.selected-status` | Texte de statut du sort sélectionné. |
| `gui.items.spell.unselected-status` | Texte de statut des autres sorts. |
| `gui.items.spell.lore` | Lore des objets représentant les sorts. |
| `gui.items.previous.material` | Matériau du bouton de page précédente. |
| `gui.items.previous-page.name` / `lore` | Nom et lore du bouton précédent. |
| `gui.items.next.material` | Matériau du bouton de page suivante. |
| `gui.items.next-page.name` / `lore` | Nom et lore du bouton suivant. |
| `gui.items.page.material` | Matériau de l'indicateur de page. |
| `gui.items.page.name` / `lore` | Nom et lore de l'indicateur de page. |

Les messages se modifient dans la section `messages`, et le titre du coffre dans `gui.title`. Après modification, utilisez `/org reload`.

## 🛠️ Compilation

Le projet utilise Maven. Depuis sa racine :

```bash
mvn clean package
```

Le plugin compilé est généré ici :

```text
target/organisespells-1.0.jar
```

---
