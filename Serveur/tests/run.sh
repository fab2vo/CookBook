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

echo
echo "Passed: $pass  Failed: $fail"
