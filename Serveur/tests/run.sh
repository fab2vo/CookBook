#!/bin/bash
# Runs the cbV3 authorization scenarios against the fake database of harness.php
# (no MySQL needed: harness.php replaces the mysqli functions by an in-memory fake).
# Usage (Git Bash):  PHP=/path/to/php.exe bash Serveur/tests/run.sh
# PHP must NOT load the mysqli extension (a plain PHP zip without php.ini is fine).
# Scenarios tagged [cbV2] run on the old copy, if present, to show the former behaviour.
T="$(cd "$(dirname "$0")" && pwd)"
PHP="${PHP:-php}"
V3="$T/../cbV3"
V2="$T/../cbV2 2026 10 18"
mkdir -p "$T/private"
cat > "$T/private/cbconfig.php" <<'EOF'
<?php
return array('db_host' => 'x', 'db_user' => 'x', 'db_password' => 'x', 'db_name' => 'x', 'db_port' => null);
EOF
export CB_CONFIG="$(cygpath -w "$T/private/cbconfig.php")"

A1=11111111-1111-1111-1111-111111111111; A2=22222222-2222-2222-2222-222222222222
B1=33333333-3333-3333-3333-333333333333; C1=44444444-4444-4444-4444-444444444444
R1=aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa; R2=bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb
R3=cccccccc-cccc-cccc-cccc-cccccccccccc
RECIPE='"title":"Tarte","source":"","sourceurl":"","nbpers":"4","season":"ALLYEAR","difficulty":"EASY","type":"MAIN","date":"2026-10-24 10:00:00"'

pass=0; fail=0
# check <label> <dir> <endpoint> <post json> <expected regex> [session json]
check() {
    local out
    out=$("$PHP" -d display_errors=stderr "$T/harness.php" "$(cygpath -w "$2")" "$3" "$4" ${6:+"$6"} 2>&1 | tr -d '\r' | tr '\n' ' ')
    if [[ "$out" =~ $5 ]]; then pass=$((pass+1)); r="OK  "; else fail=$((fail+1)); r="KO  "; fi
    printf '%s %-62s -> %s\n' "$r" "$1" "${out:0:110}"
}

echo "== checkpwd (via checkrequests.php)"
check "own password"                                   "$V3" checkrequests.php "{\"iduser\":\"$A1\",\"pwd\":\"pwdA\"}" '^ ?#WRITES'
check "password of another family (old hole)"          "$V3" checkrequests.php "{\"iduser\":\"$A1\",\"pwd\":\"pwdB\"}" 'FAIL: Wrong pwd'
check "closed account with 'compteferme'"              "$V3" checkrequests.php "{\"iduser\":\"$C1\",\"pwd\":\"compteferme\"}" 'FAIL: Wrong pwd'
check "[cbV2] password of another family"              "$V2" checkrequests.php "{\"iduser\":\"$A1\",\"pwd\":\"pwdB\"}" '^ ?#WRITES'

echo "== updateorcreaterecipe.php"
check "update own recipe"                              "$V3" updateorcreaterecipe.php "{\"idrecipe\":\"$R1\",\"iduser\":\"$A1\",\"pwd\":\"pwdA\",$RECIPE}" '^1 #WRITES=UPDATE recipedb'
check "update recipe received from other family"       "$V3" updateorcreaterecipe.php "{\"idrecipe\":\"$R1\",\"iduser\":\"$A1\",\"pwd\":\"pwdB\",$RECIPE}" '^1 #WRITES=UPDATE recipedb'
check "update recipe NOT in cookbook"                  "$V3" updateorcreaterecipe.php "{\"idrecipe\":\"$R2\",\"iduser\":\"$B1\",\"pwd\":\"pwdA\",$RECIPE}" 'FAIL: Wrong pwd.*WRITES= *$'
check "update with iduser != owner (owner change)"     "$V3" updateorcreaterecipe.php "{\"idrecipe\":\"$R1\",\"iduser\":\"$B1\",\"pwd\":\"pwdB\",$RECIPE}" 'FAIL: User is not recipe owner'
check "create recipe"                                  "$V3" updateorcreaterecipe.php "{\"idrecipe\":\"$R3\",\"iduser\":\"$A1\",\"pwd\":\"pwdA\",$RECIPE}" '^1 #WRITES=INSERT INTO recipedb.*INSERT INTO cookbooks'
check "create recipe for someone else"                 "$V3" updateorcreaterecipe.php "{\"idrecipe\":\"$R3\",\"iduser\":\"$A1\",\"pwd\":\"pwdB\",$RECIPE}" 'FAIL: Wrong pwd'
check "[cbV2] update recipe NOT in cookbook"           "$V2" updateorcreaterecipe.php "{\"idrecipe\":\"$R2\",\"iduser\":\"$B1\",\"pwd\":\"pwdA\",$RECIPE}" '^1 #WRITES=UPDATE'

echo "== uploadphotointorecipe.php"
check "photo of recipe received from other family"     "$V3" uploadphotointorecipe.php "{\"idrecipe\":\"$R1\",\"image\":\"abc\",\"date\":\"2026-10-24 10:00:00\",\"pwd\":\"pwdB\"}" '^1 #WRITES=UPDATE recipedb'
check "photo of recipe NOT in cookbook"                "$V3" uploadphotointorecipe.php "{\"idrecipe\":\"$R2\",\"image\":\"abc\",\"date\":\"2026-10-24 10:00:00\",\"pwd\":\"pwdA\"}" 'FAIL: Wrong pwd'

echo "== acceptorrefuserequest.php (request 1 on R2, owner B1)"
check "deny by non-owner"                              "$V3" acceptorrefuserequest.php "{\"iduser\":\"$A1\",\"pwd\":\"pwdA\",\"pknum\":\"1\",\"status\":\"DENIED\",\"message\":\"m\"}" 'FAIL: User is not recipe owner'
check "[cbV2] deny by non-owner"                       "$V2" acceptorrefuserequest.php "{\"iduser\":\"$A1\",\"pwd\":\"pwdA\",\"pknum\":\"1\",\"status\":\"DENIED\",\"message\":\"m\"}" '^1 #WRITES=UPDATE requestdb'
check "deny by owner"                                  "$V3" acceptorrefuserequest.php "{\"iduser\":\"$B1\",\"pwd\":\"pwdB\",\"pknum\":\"1\",\"status\":\"DENIED\",\"message\":\"m\"}" '^1 #WRITES=UPDATE requestdb'
check "accept by owner"                                "$V3" acceptorrefuserequest.php "{\"iduser\":\"$B1\",\"pwd\":\"pwdB\",\"pknum\":\"1\",\"status\":\"ACCEPTED\",\"message\":\"m\"}" '^1 #WRITES=INSERT INTO cookbooks.*UPDATE requestdb'

echo "== sendmail.php"
check "share a recipe of my cookbook"                  "$V3" sendmail.php "{\"idrecipe\":\"$R2\",\"idfrom\":\"$B1\",\"pwd\":\"pwdB\",\"family\":\"FamA\",\"membre\":\"Alice\",\"message\":\"m\"}" '^1 #WRITES=INSERT INTO cookbooks'
check "share a recipe NOT in my cookbook"              "$V3" sendmail.php "{\"idrecipe\":\"$R2\",\"idfrom\":\"$A1\",\"pwd\":\"pwdA\",\"family\":\"FamA\",\"membre\":\"Arthur\",\"message\":\"m\"}" 'FAIL: Recipe not in sender cookbook'

echo "== legacy endpoints"
check "uploadphotointorecipefromdisk disabled"         "$V3" uploadphotointorecipefromdisk.php "{\"idrecipe\":\"$R1\",\"file\":\"x.jpg\"}" 'FAIL: Endpoint disabled'
check "moverecipetocb disabled"                        "$V3" moverecipetocb.php "{\"iduser\":\"$A1\",\"idrecipe\":\"$R2\",\"pwd\":\"pwdA\"}" 'FAIL: Endpoint disabled'
check "sendrecipetosb without sender"                  "$V3" sendrecipetosb.php "{\"iduser\":\"$A1\",\"idrecipe\":\"$R2\"}" '^ ?#WRITES= *$'
check "[cbV2] sendrecipetosb without sender"           "$V2" sendrecipetosb.php "{\"iduser\":\"$A1\",\"idrecipe\":\"$R2\"}" '^1 #WRITES=INSERT'
check "sendrecipetosb recipe NOT in sender cookbook"   "$V3" sendrecipetosb.php "{\"iduser\":\"$A2\",\"idrecipe\":\"$R2\",\"idfrom\":\"$A1\",\"pwd\":\"pwdA\"}" 'FAIL: Recipe not in sender cookbook'
check "sendrecipetosb valid"                           "$V3" sendrecipetosb.php "{\"iduser\":\"$A1\",\"idrecipe\":\"$R2\",\"idfrom\":\"$B1\",\"pwd\":\"pwdB\"}" '^1 #WRITES=INSERT INTO cookbooks'
check "updatestatusrequest by non-owner"               "$V3" updatestatusrequest.php "{\"pknum\":\"1\",\"status\":\"DENIED\",\"iduser\":\"$A1\",\"pwd\":\"pwdA\"}" 'FAIL: User is not recipe owner'
check "updatestatusrequest without auth"               "$V3" updatestatusrequest.php "{\"pknum\":\"1\",\"status\":\"DENIED\"}" '^ ?#WRITES= *$'
check "updatestatusrequest by owner"                   "$V3" updatestatusrequest.php "{\"pknum\":\"1\",\"status\":\"DENIED\",\"iduser\":\"$B1\",\"pwd\":\"pwdB\"}" '^1 #WRITES=UPDATE requestdb'

echo "== addnotewithdate.php (duplicates)"
check "new note"                                       "$V3" addnotewithdate.php "{\"idrecipe\":\"$R1\",\"idfrom\":\"$A1\",\"pwd\":\"pwdA\",\"note\":\"04\",\"date\":\"2026-10-24 10:00:00\"}" '^1 #WRITES=INSERT INTO notesdb'
check "same note sent again: OK, not inserted"         "$V3" addnotewithdate.php "{\"idrecipe\":\"$R1\",\"idfrom\":\"$A1\",\"pwd\":\"pwdA\",\"note\":\"04\",\"date\":\"2026-10-01 12:00:00\"}" '^1 #WRITES= *$'
check "[cbV2] same note sent again: inserted"          "$V2" addnotewithdate.php "{\"idrecipe\":\"$R1\",\"idfrom\":\"$A1\",\"pwd\":\"pwdA\",\"note\":\"04\",\"date\":\"2026-10-01 12:00:00\"}" '^1 #WRITES=INSERT INTO notesdb'

echo "== closed account ('compteferme')"
check "join family with password 'compteferme'"        "$V3" createnewmember.php "{\"family\":\"FamA\",\"name\":\"Zoe\",\"pwd\":\"compteferme\",\"iduser\":\"$R3\"}" 'FAIL: Wrong family password'
check "join family with family password"               "$V3" createnewmember.php "{\"family\":\"FamA\",\"name\":\"Zoe\",\"pwd\":\"pwdA\",\"iduser\":\"$R3\"}" '^1 #WRITES=INSERT INTO userdb'

echo "== web portal (session)"
check "web: edit recipe of my cookbook"                "$V3" ixupdateorcreaterecipe.php "{\"idrecipe\":\"$R2\",\"iduser\":\"$B1\",\"pknum\":\"2\",$RECIPE}" '#WRITES=UPDATE recipedb' "{\"auth\":\"yes\",\"id_user\":\"$B1\"}"
check "web: edit recipe NOT in my cookbook"            "$V3" ixupdateorcreaterecipe.php "{\"idrecipe\":\"$R2\",\"iduser\":\"$B1\",\"pknum\":\"2\",$RECIPE}" '^ ?#WRITES= *$' "{\"auth\":\"yes\",\"id_user\":\"$A2\"}"
check "web: create recipe for myself"                  "$V3" ixupdateorcreaterecipe.php "{\"idrecipe\":\"$R3\",\"iduser\":\"$A2\",\"pknum\":\"0\",$RECIPE}" '#WRITES=INSERT INTO recipedb' "{\"auth\":\"yes\",\"id_user\":\"$A2\"}"
check "[cbV2] web: edit recipe (bind_param bug)"       "$V2" ixupdateorcreaterecipe.php "{\"idrecipe\":\"$R2\",\"iduser\":\"$B1\",\"pknum\":\"2\",$RECIPE}" 'ArgumentCountError' "{\"auth\":\"yes\",\"id_user\":\"$B1\"}"

echo "== agenda (E1,E2: FamA dinner 2026-11-20 full; E3: A1 perso with R2; E4: FamB)"
E1=e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1; E2=e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2
E3=e3e3e3e3-e3e3-e3e3-e3e3-e3e3e3e3e3e3; E4=e4e4e4e4-e4e4-e4e4-e4e4-e4e4e4e4e4e4
EN=f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0
AA="\"iduser\":\"$A2\",\"pwd\":\"pwdA\""; A1P="\"iduser\":\"$A1\",\"pwd\":\"pwdA\""; BB="\"iduser\":\"$B1\",\"pwd\":\"pwdB\""
check "get family agenda (member of FamA)"             "$V3" getagenda.php "{$AA,\"scope\":\"FAMILY\",\"date_from\":\"2026-11-16\",\"date_to\":\"2026-11-22\"}" "^\[.*$E1.*$E2.*\] #WRITES= *$"
check "get family agenda: other family sees only its"  "$V3" getagenda.php "{$BB,\"scope\":\"FAMILY\",\"date_from\":\"2026-11-16\",\"date_to\":\"2026-11-22\"}" "^\[\{\"id_entry\":\"$E4\"[^]]*\] #WRITES"
check "get perso agenda of A1"                         "$V3" getagenda.php "{$A1P,\"scope\":\"PERSO\",\"date_from\":\"2026-11-16\",\"date_to\":\"2026-11-22\"}" "^\[\{\"id_entry\":\"$E3\"[^]]*\] #WRITES"
check "get perso agenda of A2 (A1's not visible)"      "$V3" getagenda.php "{$AA,\"scope\":\"PERSO\",\"date_from\":\"2026-11-16\",\"date_to\":\"2026-11-22\"}" '^\[\] #WRITES= *$'
check "get agenda with wrong password"                 "$V3" getagenda.php "{\"iduser\":\"$A2\",\"pwd\":\"pwdB\",\"scope\":\"FAMILY\",\"date_from\":\"2026-11-16\",\"date_to\":\"2026-11-22\"}" 'FAIL: Wrong pwd'
check "get agenda over more than 92 days"              "$V3" getagenda.php "{$AA,\"scope\":\"FAMILY\",\"date_from\":\"2026-01-01\",\"date_to\":\"2026-12-31\"}" 'FAIL: Error in date range'
check "get agenda with reversed dates"                 "$V3" getagenda.php "{$AA,\"scope\":\"FAMILY\",\"date_from\":\"2026-11-22\",\"date_to\":\"2026-11-16\"}" 'FAIL: Error in date range'
check "add free text"                                  "$V3" addagenda.php "{$AA,\"identry\":\"$EN\",\"scope\":\"FAMILY\",\"date\":\"2026-11-20\",\"meal\":\"LUNCH\",\"idrecipe\":\"\",\"label\":\"Boeuf bourguignon\"}" '^1 #WRITES=INSERT INTO agendadb *$'
check "add recipe of another family, not shared"       "$V3" addagenda.php "{$AA,\"identry\":\"$EN\",\"scope\":\"FAMILY\",\"date\":\"2026-11-20\",\"meal\":\"LUNCH\",\"idrecipe\":\"$R2\",\"label\":\"\"}" 'FAIL: Recipe not accessible.*WRITES= *$'
check "then read it through the agenda (hole closed)"  "$V3" getagendarecipe.php "{$AA,\"idrecipe\":\"$R2\"}" 'FAIL: Recipe not in user agenda'
check "add recipe of my cookbook"                      "$V3" addagenda.php "{$AA,\"identry\":\"$EN\",\"scope\":\"FAMILY\",\"date\":\"2026-11-20\",\"meal\":\"LUNCH\",\"idrecipe\":\"$R1\",\"label\":\"\"}" '^1 #WRITES=INSERT INTO agendadb *$'
check "add recipe not in cookbook but in my agenda"    "$V3" addagenda.php "{$A1P,\"identry\":\"$EN\",\"scope\":\"FAMILY\",\"date\":\"2026-11-20\",\"meal\":\"LUNCH\",\"idrecipe\":\"$R2\",\"label\":\"\"}" '^1 #WRITES=INSERT INTO agendadb *$'
check "update: put a not shared recipe in an entry"    "$V3" updateagenda.php "{$AA,\"identry\":\"$E2\",\"date\":\"2026-11-20\",\"meal\":\"DINNER\",\"idrecipe\":\"$R2\",\"label\":\"\"}" 'FAIL: Recipe not accessible.*WRITES= *$'
check "add to a full meal (2 entries)"                 "$V3" addagenda.php "{$AA,\"identry\":\"$EN\",\"scope\":\"FAMILY\",\"date\":\"2026-11-20\",\"meal\":\"DINNER\",\"idrecipe\":\"$R1\",\"label\":\"\"}" 'FAIL: Slot full.*WRITES= *$'
check "same meal in perso agenda is free"              "$V3" addagenda.php "{$A1P,\"identry\":\"$EN\",\"scope\":\"PERSO\",\"date\":\"2026-11-20\",\"meal\":\"DINNER\",\"idrecipe\":\"$R1\",\"label\":\"\"}" '^1 #WRITES=INSERT INTO agendadb *$'
check "same entry sent again: OK, not inserted"        "$V3" addagenda.php "{$AA,\"identry\":\"$E1\",\"scope\":\"FAMILY\",\"date\":\"2026-11-20\",\"meal\":\"DINNER\",\"idrecipe\":\"$R1\",\"label\":\"\"}" '^1 #WRITES= *$'
check "add unknown recipe"                             "$V3" addagenda.php "{$AA,\"identry\":\"$EN\",\"scope\":\"FAMILY\",\"date\":\"2026-11-20\",\"meal\":\"LUNCH\",\"idrecipe\":\"$R3\",\"label\":\"\"}" 'FAIL: Unknown recipe.*WRITES= *$'
check "add label with HTML"                            "$V3" addagenda.php "{$AA,\"identry\":\"$EN\",\"scope\":\"FAMILY\",\"date\":\"2026-11-20\",\"meal\":\"LUNCH\",\"idrecipe\":\"\",\"label\":\"<script>\"}" 'FAIL: Error in label'
check "add empty label without recipe"                 "$V3" addagenda.php "{$AA,\"identry\":\"$EN\",\"scope\":\"FAMILY\",\"date\":\"2026-11-20\",\"meal\":\"LUNCH\",\"idrecipe\":\"\",\"label\":\"  \"}" 'FAIL: Error in label'
check "add on 30 February"                             "$V3" addagenda.php "{$AA,\"identry\":\"$EN\",\"scope\":\"FAMILY\",\"date\":\"2026-02-30\",\"meal\":\"LUNCH\",\"idrecipe\":\"\",\"label\":\"x\"}" 'FAIL: Error in date'
check "add with bad meal"                              "$V3" addagenda.php "{$AA,\"identry\":\"$EN\",\"scope\":\"FAMILY\",\"date\":\"2026-11-20\",\"meal\":\"BREAKFAST\",\"idrecipe\":\"\",\"label\":\"x\"}" 'FAIL: Error in meal'
check "update family entry by another member"          "$V3" updateagenda.php "{$AA,\"identry\":\"$E1\",\"date\":\"2026-11-20\",\"meal\":\"DINNER\",\"idrecipe\":\"\",\"label\":\"Quiche\"}" '^1 #WRITES=UPDATE agendadb SET *$'
check "update: move E5 into the full dinner"           "$V3" updateagenda.php "{$AA,\"identry\":\"e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5\",\"date\":\"2026-11-20\",\"meal\":\"DINNER\",\"idrecipe\":\"\",\"label\":\"x\"}" 'FAIL: Slot full.*WRITES= *$'
check "update: E2 stays in its full dinner"            "$V3" updateagenda.php "{$AA,\"identry\":\"$E2\",\"date\":\"2026-11-20\",\"meal\":\"DINNER\",\"idrecipe\":\"\",\"label\":\"Salade verte\"}" '^1 #WRITES=UPDATE agendadb SET *$'
check "update: move E3 to a free meal"                 "$V3" updateagenda.php "{$A1P,\"identry\":\"$E3\",\"date\":\"2026-11-22\",\"meal\":\"DINNER\",\"idrecipe\":\"$R2\",\"label\":\"\"}" '^1 #WRITES=UPDATE agendadb SET *$'
check "update family entry from other family"          "$V3" updateagenda.php "{$BB,\"identry\":\"$E1\",\"date\":\"2026-11-20\",\"meal\":\"DINNER\",\"idrecipe\":\"\",\"label\":\"x\"}" 'FAIL: Entry not in user family.*WRITES= *$'
check "update perso entry of someone else"             "$V3" updateagenda.php "{$AA,\"identry\":\"$E3\",\"date\":\"2026-11-21\",\"meal\":\"LUNCH\",\"idrecipe\":\"\",\"label\":\"x\"}" 'FAIL: Entry not owned by user.*WRITES= *$'
check "delete family entry by another member"          "$V3" deleteagenda.php "{$A1P,\"identry\":\"$E2\"}" '^1 #WRITES=DELETE FROM agendadb *$'
check "delete entry of another family"                 "$V3" deleteagenda.php "{$A1P,\"identry\":\"$E4\"}" 'FAIL: Entry not in user family.*WRITES= *$'
check "delete perso entry of someone else"             "$V3" deleteagenda.php "{$AA,\"identry\":\"$E3\"}" 'FAIL: Entry not owned by user.*WRITES= *$'
check "delete own perso entry"                         "$V3" deleteagenda.php "{$A1P,\"identry\":\"$E3\"}" '^1 #WRITES=DELETE FROM agendadb *$'
check "delete unknown entry"                           "$V3" deleteagenda.php "{$A1P,\"identry\":\"$EN\"}" 'FAIL: Unknown entry'
check "delete with wrong password"                     "$V3" deleteagenda.php "{\"iduser\":\"$A1\",\"pwd\":\"pwdB\",\"identry\":\"$E2\"}" 'FAIL: Wrong pwd'
check "agenda recipe planned in my perso agenda"       "$V3" getagendarecipe.php "{$A1P,\"idrecipe\":\"$R2\"}" "^\[\{\"id_recipe\":\"$R2\""
check "agenda recipe in someone else's perso agenda"   "$V3" getagendarecipe.php "{$AA,\"idrecipe\":\"$R2\"}" 'FAIL: Recipe not in user agenda'
check "agenda recipe planned in my family agenda"      "$V3" getagendarecipe.php "{$AA,\"idrecipe\":\"$R1\"}" "^\[\{\"id_recipe\":\"$R1\""
check "agenda recipe planned in another family"        "$V3" getagendarecipe.php "{$BB,\"idrecipe\":\"$R1\"}" 'FAIL: Recipe not in user agenda'
check "[ref] getrecipefromCB refuses it"               "$V3" getrecipefromCBwithphoto.php "{\"iduser\":\"$A1\",\"pwd\":\"pwdA\",\"idrecipe\":\"$R2\"}" 'FAIL: Recipe not in user cookbook'

echo
echo "Passed: $pass  Failed: $fail"
