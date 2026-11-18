# Spécification — Agenda des repas (Famille / Perso)

Version 1.0 — 18/11/2026 — statut : validée, prête à implémenter

## 1. Objectif

Planifier le déjeuner et le dîner jour par jour, soit avec une recette du cookbook,
soit avec un texte libre (« bœuf bourguignon »). Deux agendas :

- **Famille** : partagé par tous les membres de la famille (`userdb.family`), tous peuvent ajouter, modifier, effacer.
- **Perso** : visible et modifiable uniquement par son auteur.

## 2. Règles fonctionnelles

| Règle | Décision |
|---|---|
| Entrées par repas | **2 maximum** par (jour, repas, agenda). Chacune est soit une recette du cookbook, soit un texte libre (ex. entrée + plat, plat + dessert). |
| Droits agenda Famille | Tout membre de la famille peut ajouter, modifier, effacer n'importe quelle entrée. |
| Droits agenda Perso | Seul l'auteur voit, modifie, efface. |
| Modification | Autorisée (date, repas, recette/texte). |
| Recette absente du cookbook du lecteur | Elle reste **consultable** (téléchargée depuis le serveur, affichage en lecture seule). |
| Défilement | Sans limite, vers le passé comme vers le futur. |
| Réseau | **Modifications en ligne uniquement ; consultation possible hors connexion** (décision du 18/11/2026). La dernière version reçue du serveur est copiée sur le téléphone (`AgendaCache` : un fichier JSON par utilisateur et par agenda, dans le dossier privé de l'appli). À l'ouverture, la copie s'affiche aussitôt puis est remplacée par la réponse du serveur. Sans serveur : la copie reste affichée, le sous-titre indique « Hors connexion — lecture seule », un message donne la date de la copie, et ＋ / Modifier / Effacer sont bloqués (ouvrir une recette du cookbook reste possible). Si MySQL refuse la connexion, l'appli refait l'appel une fois après 1,5 s. |
| Concurrence | Pas de verrou ; la limite de 2 entrées est vérifiée par le serveur au moment de l'ajout. Le premier arrivé gagne. |
| Historique | Conservé indéfiniment, pas de purge. |

## 3. Base de données (serveur)

```sql
CREATE TABLE `agendadb` (
  `pknum`       INT(11) NOT NULL AUTO_INCREMENT,
  `id_entry`    VARCHAR(45) NOT NULL,             -- UUID généré par l'appli
  `family`      VARCHAR(255) NOT NULL,            -- recopié de userdb.family par le serveur
  `id_user`     VARCHAR(45) NOT NULL,             -- auteur (créateur) de l'entrée
  `scope`       ENUM('FAMILY','PERSO') NOT NULL,
  `date_meal`   DATE NOT NULL,
  `meal`        ENUM('LUNCH','DINNER') NOT NULL,
  `id_recipe`   VARCHAR(45) DEFAULT NULL,         -- NULL = texte libre
  `label`       VARCHAR(255) NOT NULL,            -- texte libre OU copie du titre de la recette
  `date_update` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pknum`),
  UNIQUE KEY `uk_entry` (`id_entry`),
  KEY `ix_read` (`family`, `scope`, `date_meal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

Notes :
- `label` est toujours rempli : si la recette est supprimée plus tard, l'entrée reste lisible
  (elle s'affiche alors comme un texte libre).
- `family` n'est **jamais** fourni par l'appli : le serveur le lit dans `userdb` pour l'utilisateur authentifié.
- Si un utilisateur change de famille, ses entrées Famille restent attachées à l'ancienne famille (comportement accepté).

## 4. API PHP (Serveur/cbV3)

Toutes suivent le modèle cbV3 : `ikonexct007.inc`, paramètres POST, validation par regex,
`itchekpwd.inc` (`iduser` + `pwd`), requêtes préparées, `exitfail` / `exitsuccess`.
Les fonctions et motifs communs (`$pat_day`, `$pat_scope`, `$pat_meal`, `$pat_label`, contrôle
des droits, contrôle des 2 entrées par repas) sont dans `agenda.inc`. Le script de création de la table
est dans `Serveur/sql/agendadb.sql`. Les scénarios de test sont dans `Serveur/tests/run.sh` (section agenda).

Réponses : `"1"` pour une écriture réussie, du JSON pour une lecture, sinon `"FAIL: <motif>"`.
Le motif `FAIL: Slot full` est le seul que l'appli doit reconnaître (message « Ce repas contient déjà deux plats »).

| Fichier | Paramètres (en plus de `iduser`, `pwd`) | Contrôles serveur | Réponse |
|---|---|---|---|
| `getagenda.php` | `scope`, `date_from`, `date_to` | écart ≤ 92 jours ; FAMILY → `family` = celle de l'utilisateur ; PERSO → `id_user = idfrom` | JSON : liste des entrées + `author_name` (jointure `userdb`) |
| `addagenda.php` | `identry`, `scope`, `date`, `meal`, `idrecipe` (vide ou UUID), `label` | si `identry` existe déjà → OK sans rien faire (renvoi idempotent) ; refus si le créneau contient déjà 2 entrées (`exitfail("Slot full")`) ; si `idrecipe` fourni, la recette doit exister dans `recipedb` | OK |
| `updateagenda.php` | `identry`, `date`, `meal`, `idrecipe`, `label` | l'entrée doit appartenir à la famille de l'utilisateur (FAMILY) ou à l'utilisateur (PERSO) ; si date/repas changent → contrôle des 2 entrées sur le créneau cible | OK |
| `deleteagenda.php` | `identry` | même contrôle d'appartenance que `updateagenda` | OK |
| `getagendarecipe.php` | `idrecipe` | la recette doit être planifiée dans l'agenda Famille de l'utilisateur ou dans son agenda Perso | même JSON que `getrecipefromCBwithphoto.php` |

Pour une recette, le serveur recopie toujours le titre de `recipedb` dans `label` (le `label` envoyé est ignoré).
Une recette ne peut être planifiée (ajout ou modification) que si elle est dans le cookbook de l'utilisateur
ou déjà planifiée dans un agenda qu'il voit ; sinon `FAIL: Recipe not accessible`.

Le `scope` d'une entrée ne change pas après sa création ; pour la passer de Perso à Famille, on la recrée.

**Sécurité** : le contrôle d'appartenance sur update/delete est indispensable ; sans lui, un `id_entry`
connu permettrait d'effacer l'agenda d'une autre famille. `getrecipefromCBwithphoto.php` refuse une
recette absente du cookbook de l'utilisateur (`itchekuser.inc`) : les recettes planifiées sont donc lues
par `getagendarecipe.php`, qui n'ouvre l'accès qu'aux recettes présentes dans un agenda visible par l'utilisateur.

## 5. Interface (appli Android)

### 5.1 Menu principal
`fragment_recipe_list.xml` : nouvel item **« Agenda »** (`list_agenda`, `orderInCategory="6"`)
entre « Communauté » (5) et « Feedback sur l'appli » (7). Il ouvre `AgendaActivity`.

### 5.2 Écran Agenda (`AgendaActivity` / `AgendaFragment`)
- Barre d'onglets en haut : **Famille | Perso** (TabLayout). Famille sélectionné par défaut.
- `RecyclerView` vertical, **une ligne par jour** :

```
┌──────────────────────────────────────────┐
│  Ma   18 nov.                 (aujourd'hui, surligné)
│     Déj.   Bœuf bourguignon       ⟶  texte libre
│            Tarte tatin  📖        ⟶  recette du cookbook
│     Dîner  ＋
├──────────────────────────────────────────┤
│  Me   19 nov.
│     Déj.   ＋
│     Dîner  Soupe de potiron 📖   ＋
└──────────────────────────────────────────┘
```

- Jour en gros (L, Ma, Me, J, V, S, D), date en petit (« 18 nov. », avec l'année si ce n'est pas l'année en cours).
- À l'ouverture : **aujourd'hui en haut de l'écran**. On remonte dans le passé en faisant défiler vers le haut, on descend vers le futur.
- Chargement par fenêtres de 4 semaines (`getagenda.php`) ; une nouvelle fenêtre se charge quand on approche d'un bord de la liste. Défilement infini dans les deux sens.
- Le signe « ＋ » apparaît tant que le repas a moins de 2 entrées.
- Onglet Famille : nom de l'auteur en petit sous l'entrée (facultatif, à valider visuellement).

### 5.3 Gestes sur une entrée

| Geste | Entrée « recette » 📖 | Entrée « texte libre » | « ＋ » |
|---|---|---|---|
| Appui court | **Ouvre la recette** | Ouvre la fenêtre de modification | Ouvre la fenêtre d'ajout |
| Appui long | Menu : **Modifier / Effacer** | Menu : Modifier / Effacer | — |

Effacer demande une confirmation (« Effacer *Tarte tatin* du dîner du 18/11 ? »).

### 5.4 Fenêtre Ajout / Modification (dialogue)
- Rappel en titre : « Dîner — mardi 18 nov. — Famille ».
- Choix : ◉ **Recette du cookbook** (saisie semi-automatique : toute recette dont le titre **contient** le texte
  tapé, sans tenir compte des accents ni des majuscules ; « tatin » propose « Pomme tatin »)
  ◯ **Texte libre** (champ texte, 255 caractères maximum).
- En modification seulement : la date et le repas peuvent être changés (sélecteur de date + Déj./Dîner).
- Boutons : Annuler / OK, plus **Effacer** en modification (même confirmation que l'appui long). Si le serveur répond « Slot full » → message « Ce repas contient déjà deux plats ».

### 5.5 « Planifier » depuis la liste des recettes (écran principal)
- `RecipeListFragment` : sur la **photo** de chaque recette,
  - **appui court** → visualiser la recette (comportement actuel, inchangé) ;
  - **appui long** → dialogue « Planifier » (`setOnLongClickListener` qui renvoie `true`, donc l'appui court n'est pas déclenché en plus).
  Vibration légère au déclenchement, comme le font les appuis longs standard d'Android.
- Pour que la fonction se découvre facilement : un message d'aide (« Astuce : appui long sur une photo pour planifier un repas »)
  s'affiche les 3 premières fois que l'on ouvre l'agenda (compteur dans les préférences).
- Le même dialogue est aussi proposé dans le menu à trois points de l'écran recette
  (`fragment_recipe_display.xml`, item `recipe_menu_plan`, `showAsAction="never"`), notamment pour les recettes ouvertes depuis l'agenda.
- Dialogue : date (par défaut aujourd'hui), Déjeuner / Dîner (par défaut : Déjeuner avant 14 h, Dîner ensuite), Famille / Perso (par défaut Famille).
- Message de confirmation : « Tarte tatin ajoutée au dîner du 18/11 (Famille) ».

### 5.6 Ouvrir une recette absente du cookbook local
`RecipeDisplayFragment` ne lit aujourd'hui que la base SQLite locale (`CookBook.getRecipe`), et
`onPause` réécrit la recette dans cette base. Il faut donc :
- si `CookBook.get().getRecipe(id)` vaut `null` → télécharger la recette via `getagendarecipe.php` ;
- l'afficher en **lecture seule** (mode « invité ») : pas d'enregistrement local dans `onPause`, menu réduit
  (Planifier, Partager), pas de Modifier ni d'Effacer ;
- si la recette n'existe plus sur le serveur → message « Recette supprimée », l'entrée reste affichée avec son `label`.

## 6. Textes à ajouter (`strings.xml`)

`P1MU_agenda` « Agenda », `PAG_tab_family` « Famille », `PAG_tab_perso` « Perso »,
`PAG_lunch` « Déj. », `PAG_dinner` « Dîner », `PAG_add`, `PAG_edit`, `PAG_delete`, `PAG_confirm_delete`,
`PAG_slot_full` « Ce repas contient déjà deux plats », `PAG_offline` « Agenda indisponible hors connexion »,
`PAG_from_cookbook` « Recette du cookbook », `PAG_free_text` « Texte libre », `P2MU_plan` « Planifier »,
`PAG_planned` « %1$s ajouté(e) au %2$s du %3$s (%4$s) ».
Les abréviations des jours (L, Ma, Me, J, V, S, D) dans un `string-array`.

## 7. Découpage de l'implémentation

1. **Serveur** — *fait le 18/11/2026, non déployé* : `agendadb.sql`, `agenda.inc` et 5 fichiers PHP ;
   38 scénarios ajoutés au banc de test (droits, repas plein, renvoi en double, validation) ; les 71 scénarios passent.
   *Déployé le 18/11/2026* (table créée, 6 fichiers copiés dans `/cb/`).
2. **Modèle Android** — *fait le 18/11/2026* : `AgendaEntry` (modèle, lecture du JSON) et `AgendaApi`
   (appels réseau sur un fil d'exécution dédié, réponses sur le fil principal ; résultats OK / OFFLINE / SLOT_FULL / ERROR).
   `getRecipe` lit la recette avec `parseObjectRecipe(..., full=false)` puis fixe le propriétaire,
   car une recette hors cookbook n'a ni statut ni expéditeur.
3. **Écran Agenda** — *fait et testé sur téléphone le 18/11/2026* : `AgendaActivity`, `AgendaFragment`
   (onglets faits de deux TextView plutôt que TabLayout, qui exige un thème MaterialComponents),
   défilement infini par blocs de 28 jours, relecture à chaque retour sur l'écran, bandeau hors connexion.
4. **Dialogue Ajout/Modification** — *codé avec l'étape 3* : `AgendaEditDialog` ; une recette doit être
   choisie dans la liste proposée (titres du cookbook local) ; `<` et `>` sont retirés du texte libre.
   En attendant l'étape 6, toucher une recette absente du cookbook affiche un message.
5. **Planifier** — *codé le 18/11/2026, à tester* : `AgendaPlanDialog` (date, Déjeuner/Dîner, Famille/Perso),
   ouvert par appui long sur la photo dans la liste principale et par le menu ⋮ de l'écran recette ;
   astuce affichée aux 3 premières ouvertures de l'agenda.
6. **Affichage invité** — *fait et testé le 18/11/2026* : `RecipeDisplayActivity.newGuestIntent` et mode
   invité de `RecipeDisplayFragment` (recette tenue en mémoire, jamais écrite dans la base locale ;
   notes et commentaires masqués ; menu réduit à Planifier et Partager ; sous-titre « Hors de votre cookbook —
   lecture seule » ; photo téléchargée supprimée en quittant l'écran). Hors connexion, seules les recettes
   du cookbook s'ouvrent.
7. **Suivi serveur** — *fait le 18/11/2026* : `Serveur/SUIVI_SECURITE_SERVEUR.md` à jour ; relecture des droits
   qui a révélé et corrigé une faille (planification d'une recette jamais partagée, voir le suivi).

   **Ajout — consultation hors connexion** : *codé le 18/11/2026, à tester* (`AgendaCache`, voir la règle « Réseau »).

## 8. Hors périmètre (évolutions possibles)

Liste de courses de la semaine, suggestion de recette pour un repas vide, notifications,
modifications hors connexion (file d'attente et gestion des conflits).
