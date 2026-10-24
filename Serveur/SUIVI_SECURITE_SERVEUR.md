# Suivi — sécurisation du serveur CookBook (cbV3)

Dernière mise à jour : 24/10/2026

## Organisation
- `Serveur/cbV3/` : code serveur actuel, déployé dans `public_html/cb/` (Hostinger).
- `Serveur/cb_private/cbconfig.example.php` : modèle de configuration. Le vrai `cbconfig.php`
  (identifiants MySQL) est sur le serveur dans
  `/home/u376513940/domains/cookbookfamily.cloud/cb_private/`, hors de `public_html`,
  jamais dans Git. Le même dossier contient `debug.log` et `php_errors.log`.
- `Serveur/cbV2 2026 10 18/` : ancienne version, conservée pour comparaison
  (son `idconnect.inc` contient l'ancien mot de passe : ne pas le commiter).
- `Serveur/tests/` : banc de test sans MySQL (`PHP=/chemin/php.exe bash Serveur/tests/run.sh`).
  Il faut un PHP sans l'extension mysqli (zip officiel de windows.php.net, sans php.ini).

Contrainte : chaque modification serveur doit rester **transparente pour l'app actuelle**
(l'app teste seulement si la réponse vaut `"1"` ou est du JSON ; elle n'affiche pas les messages du serveur).

## Fait et déployé
1. **Fuites** : les messages `exitfail` ne recopient plus les valeurs reçues ; débogage désactivé ;
   `display_errors=0` ; `test_cb.php`, `diagconfig.php` et les formulaires `*GhW78k.html` supprimés
   (sauf `superindexGhW78k.html`).
2. **Secrets** : identifiants MySQL déplacés dans `cb_private/cbconfig.php` ; `.htaccess` renforcé
   (HTTPS, blocage .inc/.sql/.json/.zip/.log…, `crashlogs/` privé, en-têtes de sécurité).
3. **checkpwd()** : suppression de « n'importe quel mot de passe valide » ; exception limitée aux
   utilisateurs ayant la recette dans leur carnet, pour `updateorcreaterecipe` et `uploadphotointorecipe`
   (variable `$pwd_recipe_scope`) ; pas de changement de propriétaire ; les comptes fermés
   (`compteferme`) ne sont jamais acceptés (`pwdmatches()`).
4. **Autorisations** : `acceptorrefuserequest` (le refus aussi est réservé au propriétaire), `sendmail`
   (la recette doit être dans le carnet de l'expéditeur), portail web `ix*` (la recette doit être dans
   le carnet du membre connecté) ; `uploadphotointorecipefromdisk` et `moverecipetocb` désactivés ;
   `sendrecipetosb` et `updatestatusrequest` sécurisés. Ces 4 fichiers tracent leurs appels dans le journal.
5. Bug corrigé : `ixupdateorcreaterecipe.php` (bind_param faux, le portail web ne pouvait pas enregistrer).

## À déployer
- `addnotewithdate.php` : refuse de réinsérer une note identique (même utilisateur, recette et date)
  et répond "1".
- `ikonexct007.inc` : journal d'erreurs PHP dans `cb_private/php_errors.log`.
- Nettoyer les doublons existants de `notesdb` (requête SQL plus bas), après une sauvegarde de la table.

## En cours d'investigation
- **Doublons dans notesdb** (vus dans clearnet.php, section E.1). L'app (`AsyncCallClass.syncNotesRecipe`)
  renvoie toutes les notes de l'utilisateur qu'elle ne retrouve pas dans `getnotesofrecipe.php` ;
  `addnotewithdate.php` n'avait aucun contrôle de doublon. Cause exacte à confirmer : relever dans
  E.1 les pknum, dates et utilisateurs des doublons (sont-ils récents ? un seul utilisateur ?).

## Étapes suivantes (transparentes pour l'app)
- Point 5 : administration (`superindexGhW78k.php`, `clearnet.php`) derrière htpasswd ou une liste d'IP ;
  retirer l'accès admin par session `auth=yes` dans `clearnet.php:15`.
- Point 6 : limites anti-abus (`crashedreport.php` taille et nombre de fichiers ; limitation par IP de
  `getfamilycomposition` et `createnew*` ; validation de l'image dans `uploadphotointorecipe`).
- Point 7 : compression gzip ; recompression des photos (environ 1280 px, qualité 80).
- Point 8 : `UNIQUE(id_user, id_recipe)` sur cookbooks, `UNIQUE(id_user, id_recipe, date_note)` sur
  notesdb (après nettoyage), transactions, `utf8mb4`.
- Après quelques semaines sans trace « disabled/legacy endpoint called » : supprimer ces 4 fichiers.

## Nécessite une mise à jour de l'app
- `getfamilycomposition.php` renvoie encore les mots de passe (l'app compare côté téléphone,
  `SplashActivity.java:561/601`) → endpoint `login.php` côté serveur, puis mise à jour forcée de l'app.
- Hachage des mots de passe (`password_hash`), jeton de session au lieu du mot de passe stocké en clair.
- Bug de déconnexion (`SessionInfo.clearStoredUser`), `usesCleartextTraffic`, `minSdk 34`, dépendances.

## Hors serveur
- Changer le mot de passe MySQL si ce n'est pas fait (l'ancien est dans l'historique Git, commit `db099df`).
- `Serveur/ExportSQL/*.sql` (données personnelles) est suivi par Git : à retirer, voire purger l'historique.

## Nettoyage des doublons de notes (phpMyAdmin, après export de la table notesdb)
```sql
-- 1. Compter les lignes qui seraient supprimées
SELECT COUNT(*) FROM notesdb n1
JOIN notesdb n2 ON n1.id_recipe = n2.id_recipe AND n1.id_user = n2.id_user
               AND n1.date_note = n2.date_note AND n1.note = n2.note AND n1.pknum > n2.pknum;
-- 2. Supprimer les doublons en gardant la plus ancienne ligne (plus petit pknum)
DELETE n1 FROM notesdb n1
JOIN notesdb n2 ON n1.id_recipe = n2.id_recipe AND n1.id_user = n2.id_user
               AND n1.date_note = n2.date_note AND n1.note = n2.note AND n1.pknum > n2.pknum;
```
